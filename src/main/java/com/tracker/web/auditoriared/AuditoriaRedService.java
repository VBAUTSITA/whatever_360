package com.tracker.web.auditoriared;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.LongSupplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AuditoriaRedService {

    private static final Logger log = LoggerFactory.getLogger(AuditoriaRedService.class);
    private static final int LIMITE_SITIOS = 20;
    /** Modo sitio: vecinas que se muestran por celda planificada, las mismas 30 que compara el motor. */
    static final int VECINAS_SITIO = 30;
    private static final int TAMANO_CACHE = 20;
    /** 3G queda fuera: no tenemos el planner_3g.js del servidor. */
    static final List<String> TECNOLOGIAS = Collections.unmodifiableList(Arrays.asList("4G", "5G"));

    private final AuditoriaRedRepository repositorio;
    private final MotorAuditoria motor;
    private final CatalogoChequeos catalogo;
    private final int radioReusoMetros;
    private final double radioMaxKm;
    private final int maxCeldas;
    private final int tope;
    private final long cacheMs;

    private final ReentrantLock enCurso = new ReentrantLock();
    private final Map<String, EntradaCache> cache = new LinkedHashMap<String, EntradaCache>(16, 0.75f, true) {
        private static final long serialVersionUID = 1L;
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, EntradaCache> eldest) {
            return size() > TAMANO_CACHE;
        }
    };
    private LongSupplier reloj = new LongSupplier() {
        @Override public long getAsLong() { return System.currentTimeMillis(); }
    };

    public AuditoriaRedService(AuditoriaRedRepository repositorio, MotorAuditoria motor,
            CatalogoChequeos catalogo,
            @Value("${auditoriared.radio-reuso-m:10000}") int radioReusoMetros,
            @Value("${auditoriared.radio-max-km:20}") double radioMaxKm,
            @Value("${auditoriared.max-celdas:8000}") int maxCeldas,
            @Value("${auditoriared.tope-hallazgos:8000}") int tope,
            @Value("${auditoriared.cache-minutos:15}") int cacheMinutos) {
        if (radioReusoMetros < 500 || !Double.isFinite(radioMaxKm) || radioMaxKm <= 0
                || maxCeldas < 1 || tope < 1 || cacheMinutos < 0) {
            throw new IllegalArgumentException("Configuracion de auditoria invalida: revise radios, limites y cache");
        }
        this.repositorio = repositorio;
        this.motor = motor;
        this.catalogo = catalogo;
        this.radioReusoMetros = radioReusoMetros;
        this.radioMaxKm = radioMaxKm;
        this.maxCeldas = maxCeldas;
        this.tope = tope;
        this.cacheMs = cacheMinutos * 60_000L;
    }

    void setReloj(LongSupplier reloj) { this.reloj = reloj; }

    public double getRadioMaxKm() { return radioMaxKm; }

    /** Valor por defecto y maximo del radio de reuso que puede pedir el usuario. */
    public double getRadioReusoKm() { return radioReusoMetros / 1000.0; }

    public List<InteraccionVecinaDTO> vecinos(ResultadoAuditoria resultado, String tecnologia,
            String objetivo, int limite) {
        if (!TECNOLOGIAS.contains(tecnologia) || objetivo == null || limite < 1 || limite > 200) {
            throw new IllegalArgumentException("Celda objetivo, tecnologia o limite de vecinas invalidos");
        }
        List<CeldaAuditoriaDTO> contexto = new ArrayList<>();
        boolean encontrada = false;
        for (CeldaAuditoriaDTO c : resultado.getContexto()) {
            if (!tecnologia.equals(c.getTecnologia())) continue;
            contexto.add(c);
            if (normalizar(c.getNombre()).equals(normalizar(objetivo)) && c.isDentroArea()) encontrada = true;
        }
        if (!encontrada) throw new IllegalArgumentException("Seleccione una celda del area auditada");
        if (!enCurso.tryLock()) throw new AuditoriaOcupadaException();
        try { return motor.vecinos(tecnologia, contexto, objetivo, limite); }
        finally { enCurso.unlock(); }
    }

    /**
     * Garantiza pares PCI/RSI dentro del radio. No certifica la cobertura del modelo PSS
     * ni del promedio de 30 vecinas: éstos se limitan explícitamente al contexto cargado.
     */
    static double margenPara(double radioReusoKm) { return radioReusoKm + 0.1; }

    public List<SitioDTO> buscarSitios(String texto) {
        String t = texto == null ? "" : texto.trim();
        if (t.length() < 2) return Collections.emptyList();
        if (t.length() > 60) throw new IllegalArgumentException("El texto de busqueda es demasiado largo");
        return repositorio.buscarSitios(t, LIMITE_SITIOS);
    }

    public ResultadoAuditoria auditar(SolicitudAuditoria solicitud) {
        double radioReusoKm = radioReuso(solicitud);
        Alcance alcance = construirAlcance(solicitud, margenPara(radioReusoKm));
        List<String> tecnologias = tecnologias(solicitud);
        int radioReusoM = (int) Math.round(radioReusoKm * 1000);
        String clave = alcance.clave() + "|" + String.join(",", tecnologias) + "|" + radioReusoM;

        if (!solicitud.isRefrescar()) {
            ResultadoAuditoria guardado = deCache(clave);
            if (guardado != null) return guardado.comoDesdeCache();
        }
        ResultadoAuditoria resultado = ejecutar(alcance, tecnologias, radioReusoM,
                Collections.<CeldaAuditoriaDTO>emptyList(), Collections.<String>emptyList());
        guardarEnCache(clave, resultado);
        return resultado;
    }

    /**
     * Celdas de un sitio de /recursos/asignar, con el PCI propuesto, contra la red existente. Sin
     * cache (el PCI cambia en cada llamada) pero con el mismo candado de una auditoria a la vez.
     */
    public ResultadoAuditoria auditarSitio(List<CeldaAuditoriaDTO> planificadas, List<String> avisos) {
        if (planificadas == null || planificadas.isEmpty()) throw new IllegalArgumentException("No hay celdas 4G/5G que auditar");
        double x = 0, y = 0, z = 0;
        for (CeldaAuditoriaDTO c : planificadas) {
            if (c == null || !TECNOLOGIAS.contains(c.getTecnologia()) || !Double.isFinite(c.getLatitud())
                    || !Double.isFinite(c.getLongitud()) || Math.abs(c.getLatitud()) > 90 || Math.abs(c.getLongitud()) > 180) {
                throw new IllegalArgumentException("Las celdas planificadas requieren tecnología 4G/5G y coordenadas válidas");
            }
            double p = Math.toRadians(c.getLatitud()), l = Math.toRadians(c.getLongitud());
            x += Math.cos(p) * Math.cos(l); y += Math.cos(p) * Math.sin(l); z += Math.sin(p);
        }
        if (Math.sqrt(x*x + y*y + z*z) < 1e-9) throw new IllegalArgumentException("Las coordenadas del sitio no definen un centro local");
        double lat = Math.toDegrees(Math.atan2(z, Math.sqrt(x*x+y*y)));
        double lon = Math.toDegrees(Math.atan2(y,x));
        double radioKm = 0.1;
        List<String> tecnologias = new ArrayList<>();
        for (CeldaAuditoriaDTO c : planificadas) {
            radioKm = Math.max(radioKm, ClusterAlcance.distanciaKm(lat, lon, c.getLatitud(), c.getLongitud()) + 0.01);
            if (!tecnologias.contains(c.getTecnologia())) tecnologias.add(c.getTecnologia());
        }
        if (radioKm > radioMaxKm) {
            throw new IllegalArgumentException("Las celdas del sitio estan a mas de " + (int) radioMaxKm
                    + " km entre si: revise sus coordenadas");
        }
        tecnologias.retainAll(TECNOLOGIAS);
        Collections.sort(tecnologias);
        return ejecutar(new ClusterAlcance(lat, lon, radioKm, margenPara(getRadioReusoKm())), tecnologias,
                radioReusoMetros, planificadas, avisos);
    }

    private ResultadoAuditoria ejecutar(Alcance alcance, List<String> tecnologias, int radioReusoM,
            List<CeldaAuditoriaDTO> planificadas, List<String> avisos) {
        if (!enCurso.tryLock()) throw new AuditoriaOcupadaException();
        try {
            long inicio = System.nanoTime();
            List<ResumenTecnologia> resumen = new ArrayList<>();
            List<CeldaAuditoriaDTO> celdas = new ArrayList<>();
            List<CeldaAuditoriaDTO> contexto = new ArrayList<>();
            List<HallazgoAuditoriaDTO> hallazgos = new ArrayList<>();
            for (String tecnologia : tecnologias) {
                resumen.add(auditarTecnologia(tecnologia, alcance, radioReusoM, planificadas, celdas, hallazgos, contexto));
            }
            long duracionMs = (System.nanoTime() - inicio) / 1_000_000L;
            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            formato.setTimeZone(TimeZone.getTimeZone("America/Lima"));
            long generadoMs = reloj.getAsLong();
            String generadoEn = formato.format(new Date(generadoMs));
            log.info("Auditoria de red {} {}{}: {} hallazgos en {} ms", alcance.getDescripcion(), tecnologias,
                    planificadas.isEmpty() ? "" : " con " + planificadas.size() + " celdas planificadas",
                    hallazgos.size(), duracionMs);
            ResultadoAuditoria resultado = new ResultadoAuditoria(alcance, resumen, celdas, hallazgos, duracionMs, generadoEn, false, avisos, contexto);
            resultado.marcarTiempo(generadoMs, 0);
            return resultado;
        } finally {
            enCurso.unlock();
        }
    }

    private ResumenTecnologia auditarTecnologia(String tecnologia, Alcance alcance, int radioReusoM,
            List<CeldaAuditoriaDTO> planificadas, List<CeldaAuditoriaDTO> celdasSalida,
            List<HallazgoAuditoriaDTO> hallazgos, List<CeldaAuditoriaDTO> contexto) {
        ResumenTecnologia resumen = new ResumenTecnologia(tecnologia);
        resumen.setMotor(motor.version(tecnologia));

        AuditoriaRedRepository.Lectura lectura = repositorio.leer(tecnologia, alcance.cajaConMargen());
        resumen.setFilasLeidas(lectura.getFilasLeidas());
        resumen.setDescartadasSinDatos(lectura.getDescartadasSinDatos());

        // La caja SQL es un rectangulo: aqui se recorta al area exacta y se quitan duplicados,
        // que de otro modo el motor reportaria como colision de la celda consigo misma.
        Map<String, CeldaAuditoriaDTO> porNombre = new LinkedHashMap<>();
        Set<String> conflictivas = new HashSet<>();
        int duplicadas = 0;
        int fueraDelCirculo = 0;
        int enArea = 0;
        for (CeldaAuditoriaDTO celda : lectura.getCeldas()) {
            if (!alcance.contieneConMargen(celda.getLatitud(), celda.getLongitud())) {
                fueraDelCirculo++;
                continue;
            }
            String nombre = normalizar(celda.getNombre());
            if (porNombre.containsKey(nombre)) {
                duplicadas++;
                if (!equivalentes(porNombre.get(nombre), celda)) conflictivas.add(nombre);
                else if (firmaRepresentacion(celda).compareTo(firmaRepresentacion(porNombre.get(nombre))) < 0) {
                    celda.setDentroArea(porNombre.get(nombre).isDentroArea()); celda.setConHallazgo(false);
                    porNombre.put(nombre, celda);
                }
                continue;
            }
            celda.setDentroArea(planificadas.isEmpty() && alcance.contiene(celda.getLatitud(), celda.getLongitud()));
            celda.setConHallazgo(false);
            if (celda.isDentroArea()) enArea++;
            porNombre.put(nombre, celda);
        }
        for (String nombre : conflictivas) porNombre.remove(nombre);
        enArea = 0;
        for (CeldaAuditoriaDTO c : porNombre.values()) if (c.isDentroArea()) enArea++;
        // Modo sitio: las planificadas son el "area", asi la regla de borde deja solo los pares en que
        // participa una de ellas. Reemplazan a la existente del mismo nombre (re-planificacion).
        for (CeldaAuditoriaDTO celda : planificadas) {
            if (!tecnologia.equals(celda.getTecnologia())) continue;
            if (porNombre.containsKey(normalizar(celda.getNombre()))
                    && porNombre.get(normalizar(celda.getNombre())).isDentroArea()) enArea--;
            celda.setDentroArea(true);
            celda.setConHallazgo(false);
            porNombre.put(normalizar(celda.getNombre()), celda);
            enArea++;
        }
        resumen.setDuplicadas(duplicadas);
        resumen.setDuplicadasConflictivas(conflictivas.size());
        resumen.setCeldasSinFrecuencia((int) porNombre.values().stream().filter(c -> c.getCanalRadio() == null).count());
        resumen.setTaAusente((int) porNombre.values().stream().filter(c -> "AUSENTE".equals(c.getEstadoTa())).count());
        resumen.setTaInvalido((int) porNombre.values().stream().filter(c -> "INVALIDO".equals(c.getEstadoTa())).count());
        resumen.setFueraDelCirculo(fueraDelCirculo);
        resumen.setCeldasEnArea(enArea);
        resumen.setCeldasEnMargen(porNombre.size() - enArea);

        if (porNombre.size() > maxCeldas) {
            throw new IllegalArgumentException(String.format(Locale.ROOT,
                    "El area %s con su margen de %.1f km tiene %d celdas %s (el limite es %d). "
                    + "Reduzca el radio o el radio de reuso.",
                    alcance.getDescripcion(), alcance.getMargenKm(), porNombre.size(), tecnologia, maxCeldas));
        }

        ResultadoMotor salida = motor.auditar(tecnologia, new ArrayList<>(porNombre.values()),
                radioReusoM, tope);
        resumen.setConflictosMotor(salida.getConflictos().size());
        resumen.setCapped(salida.isCapped(), salida.getCappedTipos());

        int soloMargen = 0;
        int sinCelda = 0;
        int encontrados = 0;
        for (ConflictoMotor conflicto : salida.getConflictos()) {
            CeldaAuditoriaDTO a = porNombre.get(normalizar(conflicto.getNombreA()));
            CeldaAuditoriaDTO b = porNombre.get(normalizar(conflicto.getNombreB()));
            if (a == null || b == null) {
                sinCelda++;
                continue;
            }
            // Regla de borde: se muestra el par solo si al menos una de las dos celdas esta en el area.
            if (!a.isDentroArea() && !b.isDentroArea()) {
                soloMargen++;
                continue;
            }
            a.setConHallazgo(true);
            b.setConHallazgo(true);
            hallazgos.add(catalogo.convertir(conflicto, a, b));
            encontrados++;
        }
        if (sinCelda > 0) {
            log.warn("auditarRed {} devolvio {} conflictos con celdas que no se le enviaron", tecnologia, sinCelda);
        }
        resumen.setConflictosSoloMargen(soloMargen);
        resumen.setHallazgos(encontrados);

        // En modo sitio tambien se muestran las vecinas de cada planificada, aunque no choquen con ella.
        Set<CeldaAuditoriaDTO> vecinas = vecinasDe(planificadas, tecnologia, porNombre.values());
        List<CeldaAuditoriaDTO> mostradas = new ArrayList<>();
        for (CeldaAuditoriaDTO celda : porNombre.values()) {
            if (celda.isDentroArea() || celda.isConHallazgo() || vecinas.contains(celda)) mostradas.add(celda);
        }
        resumen.setGeometriaDisponible(
                aplicarGeometria(tecnologia, new ArrayList<>(porNombre.values()), mostradas));
        resumen.setGeometriaSinRadio((int) mostradas.stream().filter(c -> c.getRadioM() == null).count());
        resumen.setTaOverflow((int) mostradas.stream().filter(c -> "TA_DESBORDAMIENTO_DESCONOCIDO".equals(c.getFuenteRadio())).count());
        celdasSalida.addAll(mostradas);
        contexto.addAll(porNombre.values());
        return resumen;
    }

    /**
     * Las VECINAS_SITIO existentes mas cercanas a cada planificada, de su mismo carrier y banda: el pool
     * del motor, y las 30 vecinas que usa para elegir el PSS. Solo las que se leyeron (radio de reuso).
     */
    static Set<CeldaAuditoriaDTO> vecinasDe(List<CeldaAuditoriaDTO> planificadas, String tecnologia,
            Collection<CeldaAuditoriaDTO> celdas) {
        Set<CeldaAuditoriaDTO> vecinas = new HashSet<>();
        for (final CeldaAuditoriaDTO p : planificadas) {
            if (!tecnologia.equals(p.getTecnologia())) continue;
            List<CeldaAuditoriaDTO> candidatas = new ArrayList<>();
            for (CeldaAuditoriaDTO c : celdas) {
                if (!c.isPlanificada() && AdaptadorMotorAuditoria.compatibles(p, c)) candidatas.add(c);
            }
            candidatas.sort(Comparator.comparingDouble((CeldaAuditoriaDTO c) ->
                    ClusterAlcance.distanciaKm(p.getLatitud(), p.getLongitud(), c.getLatitud(), c.getLongitud()))
                    .thenComparing(c -> normalizar(c.getNombre())));
            vecinas.addAll(candidatas.subList(0, Math.min(VECINAS_SITIO, candidatas.size())));
        }
        return vecinas;
    }

    /** El mapa es secundario: si el motor cambia y la geometria falla, la auditoria se entrega igual. */
    private boolean aplicarGeometria(String tecnologia, List<CeldaAuditoriaDTO> enviadas,
            List<CeldaAuditoriaDTO> mostradas) {
        if (mostradas.isEmpty()) return true;
        List<String> nombres = new ArrayList<>();
        for (CeldaAuditoriaDTO c : mostradas) nombres.add(c.getNombre());
        try {
            Map<String, GeometriaSector> geometria = motor.geometria(tecnologia, enviadas, nombres);
            for (CeldaAuditoriaDTO c : mostradas) {
                GeometriaSector g = geometria.get(normalizar(c.getNombre()));
                if (g != null) c.aplicarGeometria(g);
            }
            return true;
        } catch (RuntimeException e) {
            log.warn("No se pudo calcular la geometria de sectores " + tecnologia + "; el radio se entrega desconocido", e);
            return false;
        }
    }

    private double radioReuso(SolicitudAuditoria s) {
        if (s == null || s.getRadioReusoKm() == null) return getRadioReusoKm();
        double km = s.getRadioReusoKm();
        if (!Double.isFinite(km) || !(km >= 0.5) || km > getRadioReusoKm()) {
            throw new IllegalArgumentException(String.format(Locale.ROOT,
                    "El radio de reuso debe estar entre 0.5 y %.1f km", getRadioReusoKm()));
        }
        return km;
    }

    private Alcance construirAlcance(SolicitudAuditoria s, double margenKm) {
        if (s == null || s.getLatitud() == null || s.getLongitud() == null) {
            throw new IllegalArgumentException("Indique el centro del cluster (sitio o punto en el mapa)");
        }
        if (s.getRadioKm() == null || !(s.getRadioKm() > 0)) {
            throw new IllegalArgumentException("Indique un radio mayor que cero");
        }
        if (s.getRadioKm() > radioMaxKm) {
            throw new IllegalArgumentException(String.format(Locale.ROOT,
                    "El radio maximo por cluster es %.0f km", radioMaxKm));
        }
        return new ClusterAlcance(s.getLatitud(), s.getLongitud(), s.getRadioKm(), margenKm);
    }

    /** Siempre en el orden de TECNOLOGIAS, sin repetir, para que la clave de cache sea estable. */
    private static List<String> tecnologias(SolicitudAuditoria s) {
        List<String> pedidas = new ArrayList<>();
        if (s.getTecnologias() != null) {
            for (String pedida : s.getTecnologias()) {
                String p = pedida == null ? "" : pedida.trim().toUpperCase(Locale.ROOT);
                if (!TECNOLOGIAS.contains(p)) throw new IllegalArgumentException("Tecnologia no soportada: " + pedida);
                pedidas.add(p);
            }
        }
        List<String> salida = new ArrayList<>();
        for (String t : TECNOLOGIAS) {
            if (pedidas.contains(t)) salida.add(t);
        }
        if (salida.isEmpty()) throw new IllegalArgumentException("Seleccione 4G, 5G o ambas");
        return salida;
    }

    private static String normalizar(String nombre) {
        return nombre == null ? "" : nombre.trim().toUpperCase(Locale.ROOT);
    }

    /** Identidad = tecnología+nombre; sin timestamp autoritativo se excluyen configuraciones contradictorias. */
    private static boolean equivalentes(CeldaAuditoriaDTO a, CeldaAuditoriaDTO b) {
        return normalizar(a.getCarrier()).equals(normalizar(b.getCarrier()))
                && normalizar(a.getBanda()).equals(normalizar(b.getBanda()))
                && normalizar(a.getCanalRadio()).equals(normalizar(b.getCanalRadio()))
                && Double.compare(a.getLatitud(), b.getLatitud()) == 0
                && Double.compare(a.getLongitud(), b.getLongitud()) == 0
                && Double.compare(a.getAzimuth(), b.getAzimuth()) == 0
                && a.getPci() == b.getPci() && normalizar(a.getTxrxmode()).equals(normalizar(b.getTxrxmode()))
                && taEquivalente(a,b);
    }

    private static boolean taEquivalente(CeldaAuditoriaDTO a, CeldaAuditoriaDTO b) {
        double[] va=a.getTa(), vb=b.getTa();
        if (a.getEstadoTa().startsWith("VALIDO") && b.getEstadoTa().startsWith("VALIDO")) {
            double sa=Arrays.stream(va).sum(), sb=Arrays.stream(vb).sum();
            if (va.length != vb.length || sa <= 0 || sb <= 0) return false;
            for(int i=0;i<va.length;i++) if(Math.abs(va[i]/sa-vb[i]/sb)>1e-12) return false;
            return true;
        }
        return a.getEstadoTa().equals(b.getEstadoTa()) && Arrays.equals(va,vb);
    }

    /** Desempate de representación, nunca de configuración contradictoria ni de actualidad. */
    private static String firmaRepresentacion(CeldaAuditoriaDTO c) {
        return String.valueOf(c.getNombre())+'\u0000'+String.valueOf(c.getCarrier())+'\u0000'+String.valueOf(c.getBanda())
                +'\u0000'+String.valueOf(c.getTxrxmode())+'\u0000'+String.valueOf(c.getEstadoTa())
                +'\u0000'+String.valueOf(c.getFuenteTa())+'\u0000'+String.valueOf(c.getFuenteFrecuencia())+'\u0000'+Arrays.toString(c.getTa());
    }

    private ResultadoAuditoria deCache(String clave) {
        synchronized (cache) {
            EntradaCache entrada = cache.get(clave);
            if (entrada == null) return null;
            if (cacheMs == 0 || reloj.getAsLong() - entrada.guardadoMs >= cacheMs) {
                cache.remove(clave);
                return null;
            }
            ResultadoAuditoria r = entrada.resultado.copia(true);
            r.marcarTiempo(entrada.resultado.getGeneradoEnEpochMs(), Math.max(0, reloj.getAsLong() - entrada.resultado.getGeneradoEnEpochMs()));
            return r;
        }
    }

    private void guardarEnCache(String clave, ResultadoAuditoria resultado) {
        synchronized (cache) {
            EntradaCache anterior = cache.get(clave);
            if (cacheMs > 0 && (anterior == null || anterior.resultado.getGeneradoEnEpochMs() <= resultado.getGeneradoEnEpochMs())) {
                cache.put(clave, new EntradaCache(resultado.copia(false), reloj.getAsLong()));
            }
        }
    }

    private static final class EntradaCache {
        final ResultadoAuditoria resultado;
        final long guardadoMs;

        EntradaCache(ResultadoAuditoria resultado, long guardadoMs) {
            this.resultado = resultado;
            this.guardadoMs = guardadoMs;
        }
    }
}
