package com.tracker.web.valorizacion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tracker.web.valorizacion.ValorizacionResult.Linea;

/**
 * Business core of the valorization platform:
 *
 *   1. buscarPap(id)      — the "al ingresar manualmente el ID de
 *                           valorización, debería jalar el nombre de la
 *                           empresa y la dirección, así como las
 *                           coordenadas" step, plus the nearest FAREND.
 *   2. calcular(input)    — applies the agreed dimensioning + pricing
 *                           rules and returns the full ValorizacionResult.
 *   3. registrar(...)     — persists the snapshot in the FORM_ table that
 *                           corresponds to the solution type.
 *
 * The controller composes them: buscarPap → calcular → registrar → Excel.
 */
@Service
public class ValorizacionService {

    private final PapValorizacionRepository papRepo;
    private final FarendSiteRepository farendRepo;
    private final FormValorizacionIndoorRepository indoorRepo;
    private final FormValorizacionOutdoorRepository outdoorRepo;

    public ValorizacionService(PapValorizacionRepository papRepo,
                               FarendSiteRepository farendRepo,
                               FormValorizacionIndoorRepository indoorRepo,
                               FormValorizacionOutdoorRepository outdoorRepo) {
        this.papRepo = papRepo;
        this.farendRepo = farendRepo;
        this.indoorRepo = indoorRepo;
        this.outdoorRepo = outdoorRepo;
    }

    // =================================================================
    //  1. PAP lookup (black box) + nearest FAREND
    // =================================================================

    /**
     * Returns a small map (JSON-friendly for the form's auto-fill AJAX)
     * with the PAP fields and the derived FAREND. Key "encontrado" tells
     * the UI whether the id existed.
     */
    public Map<String, Object> buscarPap(Long idValorizacion) {
        Map<String, Object> out = new HashMap<String, Object>();

        PapValorizacion pap = (idValorizacion == null) ? null
                : papRepo.findById(idValorizacion).orElse(null);

        if (pap == null) {
            out.put("encontrado", false);
            return out;
        }

        out.put("encontrado", true);
        out.put("idValorizacion", pap.getIdValorizacion());
        out.put("nombreEmpresa", pap.getNombreEmpresa());
        out.put("direccion", pap.getDireccionValorizacion());
        out.put("latitud", pap.getLatitud());
        out.put("longitud", pap.getLongitud());

        FarendSite farend = farendMasCercano(pap.getLatitud(), pap.getLongitud());
        if (farend != null) {
            out.put("farend", farend.getCodigo());
            out.put("farendLatitud", farend.getLatitud());
            out.put("farendLongitud", farend.getLongitud());
            out.put("farendDistanciaKm",
                    Math.round(haversineKm(pap.getLatitud(), pap.getLongitud(),
                            farend.getLatitud(), farend.getLongitud()) * 100.0) / 100.0);
        }
        return out;
    }

    /**
     * Nearest FAREND by great-circle (air) distance. Linear scan over the
     * catalog — simple and correct; see FarendSiteRepository's note for the
     * scale-up path. Returns null when there is no catalog or no coords.
     */
    public FarendSite farendMasCercano(Double lat, Double lon) {
        if (lat == null || lon == null) {
            return null;
        }
        List<FarendSite> sitios = farendRepo.findAll();
        FarendSite mejor = null;
        double mejorDist = Double.MAX_VALUE;
        for (FarendSite s : sitios) {
            if (s.getLatitud() == null || s.getLongitud() == null) {
                continue;
            }
            double d = haversineKm(lat, lon, s.getLatitud(), s.getLongitud());
            if (d < mejorDist) {
                mejorDist = d;
                mejor = s;
            }
        }
        return mejor;
    }

    /** Standard Haversine great-circle distance in kilometers. */
    static double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0; // Earth radius in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return 2 * R * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    // =================================================================
    //  2. Dimensioning + pricing (agreement points 5, 6, 7)
    // =================================================================

    /**
     * Builds the complete valorization for the given input. Throws
     * IllegalArgumentException when the PAP id does not exist — the
     * controller surfaces that back to the form.
     */
    public ValorizacionResult calcular(ValorizacionFormInput input) {

        PapValorizacion pap = papRepo.findById(input.getIdValorizacion())
                .orElseThrow(() -> new IllegalArgumentException(
                        "El ID de valorización no existe en el PAP"));

        ValorizacionResult r = new ValorizacionResult();
        r.setTipoSolucion(input.getTipoSolucion());
        r.setIdValorizacion(pap.getIdValorizacion());
        r.setNombreEmpresa(pap.getNombreEmpresa());
        r.setDireccion(pap.getDireccionValorizacion());
        r.setCantidadPisos(input.getCantidadPisos());
        r.setPisosDetalle(input.getPisosDetalle());
        r.setAreaTotalM2(input.getAreaTotalM2());
        r.setDepartamento(input.getDepartamento());
        r.setRruLatitud(pap.getLatitud());
        r.setRruLongitud(pap.getLongitud());

        FarendSite farend = farendMasCercano(pap.getLatitud(), pap.getLongitud());
        if (farend != null) {
            r.setFarend(farend.getCodigo());
            r.setBbuLatitud(farend.getLatitud());
            r.setBbuLongitud(farend.getLongitud());
        }

        double area = input.getAreaTotalM2() != null ? input.getAreaTotalM2().doubleValue() : 0d;

        switch (input.getTipoSolucion()) {
            case INDOOR:
                calcularIndoor(r, area);
                break;
            case OUTDOOR:
                calcularOutdoor(r, input);
                break;
            default:
                // Combined template is phase 2 — table exists, builder later.
                throw new IllegalArgumentException(
                        "El template COMBINADO (INDOOR + OUTDOOR) aún no está habilitado");
        }
        return r;
    }

    /**
     * INDOOR (BOOK RRU):
     *  #antenas  = ceil(area / 250)             [agreement point 7]
     *  #sectores = #antenas                     ["es el mismo número"]
     *  Equipos   por BOOK RRU: 1 material de instalación y 2 celdas.
     *  Servicios: planos (precio por tramo de área), implementación
     *             (por BOOK RRU), transporte (item único), integración 0.
     */
    /* package-private for tests */ void calcularIndoor(ValorizacionResult r, double area) {
        int antenas = PreciarioValorizacion.antenasIndoorPorArea(area);
        r.setCantidadAntenas(antenas);
        r.setCantidadSectores(antenas);
        r.setAntenasDescripcion(antenas + " BOOKRRU");
        r.setProyeccion(antenas + " Book RRU en: "
                + (r.getPisosDetalle() != null && !r.getPisosDetalle().isEmpty()
                        ? r.getPisosDetalle() : "los pisos indicados")
                + ".");

        int celdas = antenas * PreciarioValorizacion.CELDAS_POR_BOOKRRU;

        r.getEquipos().add(new Linea(PreciarioValorizacion.DESC_BOOKRRU,
                antenas, PreciarioValorizacion.PRECIO_BOOKRRU));
        r.getEquipos().add(new Linea(PreciarioValorizacion.DESC_BOOKRRU_MATERIAL,
                antenas, PreciarioValorizacion.PRECIO_BOOKRRU_MATERIAL));
        r.getEquipos().add(new Linea(PreciarioValorizacion.DESC_UBBP,
                0, PreciarioValorizacion.PRECIO_UBBP));
        r.getEquipos().add(new Linea(PreciarioValorizacion.DESC_CELDA,
                celdas, PreciarioValorizacion.PRECIO_CELDA));

        r.getServicios().add(new Linea(PreciarioValorizacion.DESC_PLANOS,
                1, PreciarioValorizacion.precioPlanosPorArea(area)));
        r.getServicios().add(new Linea(PreciarioValorizacion.DESC_IMPLEMENTACION_BOOKRRU,
                antenas, PreciarioValorizacion.PRECIO_IMPLEMENTACION_BOOKRRU));
        r.getServicios().add(new Linea(PreciarioValorizacion.DESC_TRANSPORTE,
                1, PreciarioValorizacion.PRECIO_TRANSPORTE));
        r.getServicios().add(new Linea(PreciarioValorizacion.DESC_INTEGRACION_4G,
                0, PreciarioValorizacion.PRECIO_INTEGRACION_4G));
    }

    /**
     * OUTDOOR (POSTE/NAT — Easy Macro):
     *  #antenas  = EMs typed by the RF user (default 1)
     *  #sectores = #antenas
     *  Equipos   por EM: 1 material de instalación y 3 celdas.
     *  Servicios: TI, energía e integración 4G por EM; SAQ por poste;
     *             planos 0 (es diseño outdoor).
     */
    /* package-private for tests */ void calcularOutdoor(ValorizacionResult r, ValorizacionFormInput input) {
        int ems = input.getAntenasEm() != null && input.getAntenasEm().intValue() > 0
                ? input.getAntenasEm().intValue() : 1;
        int postes = input.getCantidadPostes() != null && input.getCantidadPostes().intValue() > 0
                ? input.getCantidadPostes().intValue() : 1;

        r.setCantidadAntenas(ems);
        r.setCantidadSectores(ems);
        r.setAntenasDescripcion(ems + " ANTENA" + (ems > 1 ? "S" : "") + " EASY MACRO");
        r.setProyeccion("Poste con " + ems + " antena" + (ems > 1 ? "s" : "")
                + " Easy Macro en: "
                + (r.getPisosDetalle() != null && !r.getPisosDetalle().isEmpty()
                        ? r.getPisosDetalle() : "la zona indicada")
                + ".");

        int celdas = ems * PreciarioValorizacion.CELDAS_POR_EASYMACRO;

        r.getEquipos().add(new Linea(PreciarioValorizacion.DESC_EASYMACRO,
                ems, PreciarioValorizacion.PRECIO_EASYMACRO));
        r.getEquipos().add(new Linea(PreciarioValorizacion.DESC_EASYMACRO_MATERIAL,
                ems, PreciarioValorizacion.PRECIO_EASYMACRO_MATERIAL));
        r.getEquipos().add(new Linea(PreciarioValorizacion.DESC_UBBP,
                0, PreciarioValorizacion.PRECIO_UBBP));
        r.getEquipos().add(new Linea(PreciarioValorizacion.DESC_CELDA,
                celdas, PreciarioValorizacion.PRECIO_CELDA));

        r.getServicios().add(new Linea(PreciarioValorizacion.DESC_PLANOS,
                0, PreciarioValorizacion.precioPlanosPorArea(
                        input.getAreaTotalM2() != null ? input.getAreaTotalM2().doubleValue() : 0d)));
        r.getServicios().add(new Linea(PreciarioValorizacion.DESC_IMPLEMENTACION_TI,
                ems, PreciarioValorizacion.PRECIO_IMPLEMENTACION_TI));
        r.getServicios().add(new Linea(PreciarioValorizacion.DESC_ENERGIA,
                ems, PreciarioValorizacion.PRECIO_ENERGIA));
        r.getServicios().add(new Linea(PreciarioValorizacion.DESC_INTEGRACION_4G,
                ems, PreciarioValorizacion.PRECIO_INTEGRACION_4G));
        r.getServicios().add(new Linea(PreciarioValorizacion.DESC_SAQ,
                postes, PreciarioValorizacion.PRECIO_SAQ));
    }

    // =================================================================
    //  3. Snapshot persistence — one table per solution type
    // =================================================================

    /** Saves the generated valorization into its FORM_ table. */
    @Transactional
    public void registrar(ValorizacionFormInput input, ValorizacionResult r) {
        switch (input.getTipoSolucion()) {
            case INDOOR: {
                FormValorizacionIndoor f = new FormValorizacionIndoor();
                copiarBase(f, input, r);
                indoorRepo.save(f);
                break;
            }
            case OUTDOOR: {
                FormValorizacionOutdoor f = new FormValorizacionOutdoor();
                copiarBase(f, input, r);
                f.setCantidadPostes(input.getCantidadPostes());
                outdoorRepo.save(f);
                break;
            }
            default:
                // unreachable today: calcular() rejects the combined type first
                throw new IllegalArgumentException("Tipo no soportado todavía");
        }
    }

    private void copiarBase(FormValorizacionBase f, ValorizacionFormInput input,
                            ValorizacionResult r) {
        f.setIdValorizacion(r.getIdValorizacion());
        f.setAreaTotalM2(input.getAreaTotalM2());
        f.setCantidadPisos(input.getCantidadPisos());
        f.setPisosDetalle(input.getPisosDetalle());
        f.setDepartamento(input.getDepartamento());
        f.setNombreEmpresa(r.getNombreEmpresa());
        f.setDireccion(r.getDireccion());
        f.setLatitud(r.getRruLatitud());
        f.setLongitud(r.getRruLongitud());
        f.setFarend(r.getFarend());
        f.setFarendLatitud(r.getBbuLatitud());
        f.setFarendLongitud(r.getBbuLongitud());
        f.setCantidadAntenas(r.getCantidadAntenas());
        f.setCantidadSectores(r.getCantidadSectores());
        f.setTotalEquiposUsd(r.getTotalEquiposUsd());
        f.setTotalServiciosUsd(r.getTotalServiciosUsd());
        f.setTotalUsd(r.getTotalUsd());
    }
}
