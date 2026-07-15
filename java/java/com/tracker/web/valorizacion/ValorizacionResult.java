package com.tracker.web.valorizacion;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Everything one generated valorization contains, ready for the Excel
 * builder (and already persisted as a snapshot in its FORM_ table).
 * Plain DTO: no Spring, no JPA — the builder stays unit-testable.
 */
public class ValorizacionResult {

    /** One row of the EQUIPOS or SERVICIOS table. */
    public static class Linea {
        private final String descripcion;
        private final int unidades;
        private final BigDecimal costoUnitarioUsd;

        public Linea(String descripcion, int unidades, BigDecimal costoUnitarioUsd) {
            this.descripcion = descripcion;
            this.unidades = unidades;
            this.costoUnitarioUsd = costoUnitarioUsd;
        }

        public String getDescripcion() { return descripcion; }
        public int getUnidades() { return unidades; }
        public BigDecimal getCostoUnitarioUsd() { return costoUnitarioUsd; }

        /** unidades x costo, 2 decimals HALF_UP — the sheet's SUBTOTAL. */
        public BigDecimal getSubtotalUsd() {
            return costoUnitarioUsd.multiply(BigDecimal.valueOf(unidades))
                    .setScale(2, RoundingMode.HALF_UP);
        }
    }

    // ---------- header (left block of the sheet) ----------
    private TipoSolucion tipoSolucion;
    private Long idValorizacion;
    private String nombreEmpresa;
    private String direccion;
    private Integer cantidadPisos;
    private String pisosDetalle;
    private Double areaTotalM2;
    private Integer cantidadSectores;
    private Integer cantidadAntenas;
    private String antenasDescripcion;   // e.g. "2 BOOKRRU" / "1 ANTENA EASY MACRO"
    private String farend;               // e.g. "LI0029_LOS_FLAMENCOS"
    private String departamento;
    private Double bbuLatitud;           // FAREND site coordinates (BBU side)
    private Double bbuLongitud;
    private Double rruLatitud;           // PAP coordinates (client side)
    private Double rruLongitud;
    private String proyeccion;           // "Se proyecta implementar: ..."

    // ---------- tables (right block of the sheet) ----------
    private final List<Linea> equipos = new ArrayList<Linea>();
    private final List<Linea> servicios = new ArrayList<Linea>();

    public BigDecimal getTotalEquiposUsd() { return suma(equipos); }
    public BigDecimal getTotalServiciosUsd() { return suma(servicios); }
    public BigDecimal getTotalUsd() { return getTotalEquiposUsd().add(getTotalServiciosUsd()); }

    private static BigDecimal suma(List<Linea> lineas) {
        BigDecimal total = BigDecimal.ZERO;
        for (Linea l : lineas) {
            total = total.add(l.getSubtotalUsd());
        }
        return total;
    }

    // ===== getters / setters =====
    public TipoSolucion getTipoSolucion() { return tipoSolucion; }
    public void setTipoSolucion(TipoSolucion tipoSolucion) { this.tipoSolucion = tipoSolucion; }

    public Long getIdValorizacion() { return idValorizacion; }
    public void setIdValorizacion(Long idValorizacion) { this.idValorizacion = idValorizacion; }

    public String getNombreEmpresa() { return nombreEmpresa; }
    public void setNombreEmpresa(String nombreEmpresa) { this.nombreEmpresa = nombreEmpresa; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public Integer getCantidadPisos() { return cantidadPisos; }
    public void setCantidadPisos(Integer cantidadPisos) { this.cantidadPisos = cantidadPisos; }

    public String getPisosDetalle() { return pisosDetalle; }
    public void setPisosDetalle(String pisosDetalle) { this.pisosDetalle = pisosDetalle; }

    public Double getAreaTotalM2() { return areaTotalM2; }
    public void setAreaTotalM2(Double areaTotalM2) { this.areaTotalM2 = areaTotalM2; }

    public Integer getCantidadSectores() { return cantidadSectores; }
    public void setCantidadSectores(Integer cantidadSectores) { this.cantidadSectores = cantidadSectores; }

    public Integer getCantidadAntenas() { return cantidadAntenas; }
    public void setCantidadAntenas(Integer cantidadAntenas) { this.cantidadAntenas = cantidadAntenas; }

    public String getAntenasDescripcion() { return antenasDescripcion; }
    public void setAntenasDescripcion(String antenasDescripcion) { this.antenasDescripcion = antenasDescripcion; }

    public String getFarend() { return farend; }
    public void setFarend(String farend) { this.farend = farend; }

    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }

    public Double getBbuLatitud() { return bbuLatitud; }
    public void setBbuLatitud(Double bbuLatitud) { this.bbuLatitud = bbuLatitud; }

    public Double getBbuLongitud() { return bbuLongitud; }
    public void setBbuLongitud(Double bbuLongitud) { this.bbuLongitud = bbuLongitud; }

    public Double getRruLatitud() { return rruLatitud; }
    public void setRruLatitud(Double rruLatitud) { this.rruLatitud = rruLatitud; }

    public Double getRruLongitud() { return rruLongitud; }
    public void setRruLongitud(Double rruLongitud) { this.rruLongitud = rruLongitud; }

    public String getProyeccion() { return proyeccion; }
    public void setProyeccion(String proyeccion) { this.proyeccion = proyeccion; }

    public List<Linea> getEquipos() { return equipos; }
    public List<Linea> getServicios() { return servicios; }
}
