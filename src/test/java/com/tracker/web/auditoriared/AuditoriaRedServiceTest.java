package com.tracker.web.auditoriared;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.LongSupplier;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class AuditoriaRedServiceTest {

    static final double LAT = -12.0;
    static final double LON = -77.0;

    private AuditoriaRedRepository repositorio;
    private MotorFalso motor;
    private AuditoriaRedService service;
    private List<CeldaAuditoriaDTO> red4g;

    /** Grados de latitud equivalentes a km hacia el norte. */
    static double norte(double km) { return LAT + km / 111.32; }

    static CeldaAuditoriaDTO celda(String nombre, String tec, double lat, double lon, int pci) {
        return new CeldaAuditoriaDTO(nombre, tec, "C1", "7", lat, lon, 0, pci, "4T4R", new double[8]);
    }

    static CeldaAuditoriaDTO copia(CeldaAuditoriaDTO c) {
        return new CeldaAuditoriaDTO(c.getNombre(), c.getTecnologia(), c.getCarrier(), c.getBanda(),
                c.getLatitud(), c.getLongitud(), c.getAzimuth(), c.getPci(), c.getTxrxmode(), c.getTa());
    }

    static CatalogoChequeos catalogo() {
        return new CatalogoChequeos(Arrays.<ChequeoAuditoria>asList(
                new ChequeoColisionPci(), new ChequeoColisionRsi(), new ChequeoConfusionMod3()));
    }

    static SolicitudAuditoria solicitud(double radioKm, String... tecnologias) {
        SolicitudAuditoria s = new SolicitudAuditoria();
        s.setLatitud(LAT);
        s.setLongitud(LON);
        s.setRadioKm(radioKm);
        s.setTecnologias(Arrays.asList(tecnologias));
        return s;
    }

    /** El repositorio devuelve objetos nuevos en cada lectura, como la BD real. */
    static void responder(AuditoriaRedRepository repo, String tec, final List<CeldaAuditoriaDTO> red) {
        when(repo.leer(eq(tec), any(CajaGeografica.class))).thenAnswer(new Answer<AuditoriaRedRepository.Lectura>() {
            @Override
            public AuditoriaRedRepository.Lectura answer(InvocationOnMock inv) {
                List<CeldaAuditoriaDTO> nuevas = new ArrayList<>();
                for (CeldaAuditoriaDTO c : red) nuevas.add(copia(c));
                return new AuditoriaRedRepository.Lectura(nuevas, nuevas.size() + 1, 1);
            }
        });
    }

    static class MotorFalso implements MotorAuditoria {
        final List<ConflictoMotor> conflictos = new ArrayList<>();
        final List<List<CeldaAuditoriaDTO>> llamadas = new ArrayList<>();
        int ultimoRadioReuso;
        boolean capped;
        List<String> cappedTipos = Collections.emptyList();
        CountDownLatch dentro;
        CountDownLatch soltar;

        @Override
        public ResultadoMotor auditar(String tecnologia, List<CeldaAuditoriaDTO> celdas, int radio, int tope) {
            llamadas.add(new ArrayList<>(celdas));
            ultimoRadioReuso = radio;
            if (dentro != null) {
                dentro.countDown();
                try {
                    soltar.await(5, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            return new ResultadoMotor(conflictos, capped, cappedTipos);
        }

        final Map<String, GeometriaSector> geometrias = new HashMap<>();
        final List<Collection<String>> nombresPedidos = new ArrayList<>();
        RuntimeException fallaGeometria;

        @Override
        public Map<String, GeometriaSector> geometria(String tecnologia,
                List<CeldaAuditoriaDTO> celdas, Collection<String> nombres) {
            nombresPedidos.add(new ArrayList<>(nombres));
            if (fallaGeometria != null) throw fallaGeometria;
            return geometrias;
        }

        @Override public String version(String tecnologia) { return "motor-falso-" + tecnologia; }
    }

    static AuditoriaRedService servicio(AuditoriaRedRepository repo, MotorAuditoria motor, int maxCeldas) {
        return new AuditoriaRedService(repo, motor, catalogo(), 10000, 20, maxCeldas, 8000, 15);
    }

    @Before
    public void preparar() {
        repositorio = mock(AuditoriaRedRepository.class);
        motor = new MotorFalso();
        service = servicio(repositorio, motor, 40000);
        red4g = new ArrayList<>(Arrays.asList(
                celda("DENTRO_A", "4G", norte(0.5), LON, 100),
                celda("MARGEN_B", "4G", norte(8), LON, 100),     // 8 km: margen (radio 2 + 10.1)
                celda("MARGEN_C", "4G", norte(-9), LON, 200),
                celda("MARGEN_D", "4G", norte(-9.5), LON, 200),
                // Esquina de la caja SQL: dentro del rectangulo pero a ~15 km, fuera del circulo con margen.
                celda("ESQUINA_E", "4G", norte(11), LON + 11 / (111.32 * Math.cos(Math.toRadians(LAT))), 300)));
        responder(repositorio, "4G", red4g);
        responder(repositorio, "5G", Collections.<CeldaAuditoriaDTO>emptyList());
    }

    @Test
    public void reglaDeBorde_seMuestraElParDentroMargen_yNoElParSoloDelMargen() {
        motor.conflictos.add(new ConflictoMotor("PCI_COLLISION", "DENTRO_A", "MARGEN_B", 7500, "100", null));
        motor.conflictos.add(new ConflictoMotor("PCI_COLLISION", "MARGEN_C", "MARGEN_D", 560, "200", null));

        ResultadoAuditoria r = service.auditar(solicitud(2, "4G"));

        assertEquals(1, r.getHallazgos().size());
        HallazgoAuditoriaDTO h = r.getHallazgos().get(0);
        assertEquals("DENTRO_A", h.getCeldaA().getNombre());
        assertEquals("MARGEN_B", h.getCeldaB().getNombre());
        assertTrue(h.getCeldaA().isDentroArea());
        assertFalse(h.getCeldaB().isDentroArea());
        assertEquals("Colisión PCI potencial", h.getEtiqueta());
        ResumenTecnologia t = r.getResumen().get(0);
        assertEquals(2, t.getConflictosMotor());
        assertEquals(1, t.getConflictosSoloMargen());
        assertEquals(1, t.getHallazgos());
    }

    @Test
    public void elMapaRecibeLasCeldasDelArea_yLasDelMargenSoloSiTienenHallazgo() {
        motor.conflictos.add(new ConflictoMotor("PCI_COLLISION", "DENTRO_A", "MARGEN_B", 7500, "100", null));

        ResultadoAuditoria r = service.auditar(solicitud(2, "4G"));

        Set<String> nombres = new HashSet<>();
        for (CeldaAuditoriaDTO c : r.getCeldas()) nombres.add(c.getNombre());
        assertEquals(new HashSet<>(Arrays.asList("DENTRO_A", "MARGEN_B")), nombres);
        for (CeldaAuditoriaDTO c : r.getCeldas()) assertTrue(c.isConHallazgo());
    }

    @Test
    public void laGeometriaSePideSoloParaLasCeldasQueSeMuestran_yLlegaACadaCelda() {
        motor.conflictos.add(new ConflictoMotor("PCI_COLLISION", "DENTRO_A", "MARGEN_B", 7500, "100", null));
        motor.geometrias.put("DENTRO_A", new GeometriaSector(90, 30, 546, false));
        motor.geometrias.put("MARGEN_B", new GeometriaSector(180, 100, 4755, true));

        ResultadoAuditoria r = service.auditar(solicitud(2, "4G"));

        assertEquals(new HashSet<>(Arrays.asList("DENTRO_A", "MARGEN_B")),
                new HashSet<>(motor.nombresPedidos.get(0)));
        CeldaAuditoriaDTO a = r.getHallazgos().get(0).getCeldaA();
        assertEquals(90.0, a.getAzimuthEfectivo(), 1e-9);
        assertEquals(30.0, a.getHbeam(), 1e-9);
        assertEquals(546.0, a.getRadioM(), 1e-9);
        assertFalse(a.getSinTa());
        assertTrue(r.getHallazgos().get(0).getCeldaB().getSinTa());
        assertTrue(r.getResumen().get(0).isGeometriaDisponible());
    }

    @Test
    public void siLaGeometriaFallaLaAuditoriaSeEntregaIgual() {
        motor.conflictos.add(new ConflictoMotor("PCI_COLLISION", "DENTRO_A", "MARGEN_B", 7500, "100", null));
        motor.fallaGeometria = new IllegalStateException("el motor ya no exporta PoolCarrier");

        ResultadoAuditoria r = service.auditar(solicitud(2, "4G"));

        assertEquals(1, r.getHallazgos().size());
        assertFalse(r.getResumen().get(0).isGeometriaDisponible());
        assertEquals(null, r.getHallazgos().get(0).getCeldaA().getRadioM());
    }

    @Test
    public void elModEsElPciModuloTres() {
        assertEquals(0, celda("X", "4G", LAT, LON, 0).getMod());
        assertEquals(1, celda("X", "4G", LAT, LON, 100).getMod());
        assertEquals(2, celda("X", "4G", LAT, LON, 503).getMod());
    }

    @Test
    public void laEsquinaDeLaCajaSqlNoLlegaAlMotor() {
        ResultadoAuditoria r = service.auditar(solicitud(2, "4G"));

        Set<String> enviadas = new HashSet<>();
        for (CeldaAuditoriaDTO c : motor.llamadas.get(0)) enviadas.add(c.getNombre());
        assertFalse(enviadas.contains("ESQUINA_E"));
        assertEquals(4, enviadas.size());
        assertEquals(1, r.getResumen().get(0).getFueraDelCirculo());
        assertEquals(1, r.getResumen().get(0).getCeldasEnArea());
        assertEquals(3, r.getResumen().get(0).getCeldasEnMargen());
    }

    @Test
    public void unaCeldaRepetidaSeDescartaParaQueElMotorNoLaCompareConsigoMisma() {
        red4g.add(celda("dentro_a", "4G", norte(0.5), LON, 100));
        responder(repositorio, "4G", red4g);

        ResultadoAuditoria r = service.auditar(solicitud(2, "4G"));

        assertEquals(4, motor.llamadas.get(0).size());
        assertEquals(1, r.getResumen().get(0).getDuplicadas());
    }

    @Test public void identidadOficialContradictoriaSeExcluyeIndependientementeDelOrden() {
        red4g.add(celda("dentro_a", "4G", norte(.5), LON, 101));
        responder(repositorio,"4G",red4g);
        ResultadoAuditoria a=service.auditar(solicitud(2,"4G"));
        assertTrue(a.getContexto().stream().noneMatch(c -> c.getNombre().equalsIgnoreCase("DENTRO_A")));
        assertEquals(1,a.getResumen().get(0).getDuplicadasConflictivas());
        Collections.reverse(red4g);responder(repositorio,"4G",red4g);
        SolicitudAuditoria s=solicitud(2,"4G");s.setRefrescar(true);
        ResultadoAuditoria b=service.auditar(s);
        assertEquals(a.getContexto().size(),b.getContexto().size());
        assertTrue(b.getContexto().stream().noneMatch(c -> c.getNombre().equalsIgnoreCase("DENTRO_A")));
    }

    @Test public void duplicadosEquivalentesEligenRepresentacionEstableYConsolidanUnidadesTa() {
        CeldaAuditoriaDTO primero=celda("dentro_a","4G",norte(.5),LON,100);
        primero.setCarrier("c1");primero.setBanda("7 ");primero.setTa(new double[]{80,20,0,0,0,0,0,0});
        CeldaAuditoriaDTO segundo=celda("DENTRO_A","4G",norte(.5),LON,100);
        segundo.setTa(new double[]{.8,.2,0,0,0,0,0,0});
        responder(repositorio,"4G",Arrays.asList(primero,segundo));
        ResultadoAuditoria a=service.auditar(solicitud(2,"4G"));
        responder(repositorio,"4G",Arrays.asList(segundo,primero));
        SolicitudAuditoria s=solicitud(2,"4G");s.setRefrescar(true);ResultadoAuditoria b=service.auditar(s);
        assertEquals(1,a.getContexto().size());assertEquals(0,a.getResumen().get(0).getDuplicadasConflictivas());
        assertEquals("DENTRO_A",a.getContexto().get(0).getNombre());
        assertEquals(a.getContexto().get(0).getNombre(),b.getContexto().get(0).getNombre());
        assertEquals(a.getContexto().get(0).getCarrier(),b.getContexto().get(0).getCarrier());
        assertEquals(a.getContexto().get(0).getBanda(),b.getContexto().get(0).getBanda());
        assertArrayEquals(a.getContexto().get(0).getTa(),b.getContexto().get(0).getTa(),0);
    }

    @Test public void histogramasTaContradictoriosTambienExcluyenIdentidad() {
        CeldaAuditoriaDTO a=red4g.get(0);a.setTa(new double[]{1,0,0,0,0,0,0,0});
        CeldaAuditoriaDTO distinta=copia(a);distinta.setTa(new double[]{0,1,0,0,0,0,0,0});red4g.add(distinta);
        responder(repositorio,"4G",red4g);
        ResultadoAuditoria r=service.auditar(solicitud(2,"4G"));
        assertEquals(1,r.getResumen().get(0).getDuplicadasConflictivas());
        assertTrue(r.getContexto().stream().noneMatch(c -> c.getNombre().equalsIgnoreCase("DENTRO_A")));
    }

    @Test public void edadCacheCuentaDesdeCalculoYNoPrometeFechaDeLaRed() {
        final long[] ahora={1_000_000};service.setReloj(()->ahora[0]);
        ResultadoAuditoria primera=service.auditar(solicitud(2,"4G"));assertEquals(1_000_000,primera.getGeneradoEnEpochMs());
        ahora[0]+=73_000;ResultadoAuditoria segunda=service.auditar(solicitud(2,"4G"));
        assertTrue(segunda.isDesdeCache());assertEquals(73,segunda.getEdadSegundos());assertEquals("America/Lima",segunda.getZonaHoraria());
    }

    @Test public void sitioQueCruzaAntimeridianoTieneCentroLocalYRadioPequeno() {
        responder(repositorio,"5G",Collections.emptyList());
        CeldaAuditoriaDTO a=celda("A","5G",0,179.999,100), b=celda("B","5G",0,-179.999,101);
        a.setPlanificada(true);b.setPlanificada(true);
        ResultadoAuditoria r=service.auditarSitio(Arrays.asList(a,b),Collections.emptyList());
        assertTrue(r.getAlcance().getDescripcion(),r.getAlcance().getDescripcion().contains("180.000000"));
        assertEquals(2,r.getResumen().get(0).getCeldasEnArea());
    }

    @Test
    public void soloCorreUnaAuditoriaALaVez() throws Exception {
        motor.dentro = new CountDownLatch(1);
        motor.soltar = new CountDownLatch(1);
        ExecutorService hilo = Executors.newSingleThreadExecutor();
        try {
            Future<ResultadoAuditoria> primera = hilo.submit(new java.util.concurrent.Callable<ResultadoAuditoria>() {
                @Override public ResultadoAuditoria call() { return service.auditar(solicitud(2, "4G")); }
            });
            assertTrue(motor.dentro.await(5, TimeUnit.SECONDS));
            try {
                service.auditar(solicitud(3, "4G"));
                fail("Una segunda auditoria simultanea debia rechazarse");
            } catch (AuditoriaOcupadaException esperado) {
                // la primera sigue en curso
            }
            motor.soltar.countDown();
            primera.get(5, TimeUnit.SECONDS);
            service.auditar(solicitud(3, "4G"));
        } finally {
            hilo.shutdownNow();
        }
    }

    @Test
    public void laMismaSolicitudSeSirveDeLaCache_yRefrescarRecalcula() {
        ResultadoAuditoria primera = service.auditar(solicitud(2, "4G"));
        ResultadoAuditoria segunda = service.auditar(solicitud(2, "4G"));

        assertFalse(primera.isDesdeCache());
        assertTrue(segunda.isDesdeCache());
        verify(repositorio, times(1)).leer(eq("4G"), any(CajaGeografica.class));

        SolicitudAuditoria refrescar = solicitud(2, "4G");
        refrescar.setRefrescar(true);
        assertFalse(service.auditar(refrescar).isDesdeCache());
        verify(repositorio, times(2)).leer(eq("4G"), any(CajaGeografica.class));
    }

    @Test
    public void laCacheVence() {
        final long[] ahora = {1_000_000L};
        service.setReloj(new LongSupplier() {
            @Override public long getAsLong() { return ahora[0]; }
        });
        service.auditar(solicitud(2, "4G"));
        ahora[0] += 16 * 60_000L;
        assertFalse(service.auditar(solicitud(2, "4G")).isDesdeCache());
    }

    @Test
    public void sinRadioDeReusoSeUsaElDeConfiguracion() {
        ResultadoAuditoria r = service.auditar(solicitud(2, "4G"));
        assertEquals(10000, motor.ultimoRadioReuso);
        assertEquals(10.1, r.getAlcance().getMargenKm(), 1e-9);
    }

    @Test
    public void unRadioDeReusoMenorAchicaElMargen_yLlegaAlMotor() {
        SolicitudAuditoria s = solicitud(2, "4G");
        s.setRadioReusoKm(3.0);

        ResultadoAuditoria r = service.auditar(s);

        assertEquals(3000, motor.ultimoRadioReuso);
        assertEquals(3.1, r.getAlcance().getMargenKm(), 1e-9);
        // Con 2 km de radio + 3.1 de margen, la celda a 8 km ya no se lee como parte del margen.
        Set<String> enviadas = new HashSet<>();
        for (CeldaAuditoriaDTO c : motor.llamadas.get(0)) enviadas.add(c.getNombre());
        assertEquals(new HashSet<>(Arrays.asList("DENTRO_A")), enviadas);
    }

    @Test
    public void elRadioDeReusoTieneLimites() {
        SolicitudAuditoria grande = solicitud(2, "4G");
        grande.setRadioReusoKm(15.0);
        esperarRechazo(grande, "radio de reuso");
        SolicitudAuditoria chico = solicitud(2, "4G");
        chico.setRadioReusoKm(0.1);
        esperarRechazo(chico, "radio de reuso");
    }

    @Test
    public void elOrdenDeLasTecnologiasNoCambiaLaClaveDeCache() {
        service.auditar(solicitud(2, "5G", "4G"));
        ResultadoAuditoria r = service.auditar(solicitud(2, "4G", "5G"));
        assertTrue(r.isDesdeCache());
        assertEquals("4G", r.getResumen().get(0).getTecnologia());
        assertEquals("5G", r.getResumen().get(1).getTecnologia());
    }

    @Test
    public void elTopeDelMotorLlegaAlResultado() {
        motor.capped = true;
        motor.cappedTipos = Arrays.asList("4G:RSI_COLLISION");

        ResultadoAuditoria r = service.auditar(solicitud(2, "4G"));

        assertTrue(r.isCapped());
        assertEquals(Arrays.asList("4G:RSI_COLLISION"), r.getCappedTipos());
    }

    @Test
    public void unTipoNuevoDelMotorSinClasePropiaNoSeDescarta() {
        motor.conflictos.add(new ConflictoMotor("PCI_MOD30", "DENTRO_A", "MARGEN_B", 7500, "10", "nuevo"));

        ResultadoAuditoria r = service.auditar(solicitud(2, "4G"));

        assertEquals(1, r.getHallazgos().size());
        assertEquals("PCI_MOD30", r.getHallazgos().get(0).getTipo());
    }

    @Test
    public void validaLaSolicitud() {
        esperarRechazo(solicitud(25, "4G"), "radio maximo");
        esperarRechazo(solicitud(0, "4G"), "mayor que cero");
        esperarRechazo(solicitud(2), "Seleccione");
        esperarRechazo(solicitud(2, "3G"), "no soportada");
        SolicitudAuditoria sinCentro = solicitud(2, "4G");
        sinCentro.setLatitud(null);
        esperarRechazo(sinCentro, "centro");
        verify(repositorio, never()).leer(anyString(), any(CajaGeografica.class));
    }

    @Test
    public void unAreaConDemasiadasCeldasSeRechazaAntesDeLlamarAlMotor() {
        AuditoriaRedService chico = servicio(repositorio, motor, 2);
        try {
            chico.auditar(solicitud(2, "4G"));
            fail("Debia rechazarse por exceso de celdas");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage(), e.getMessage().contains("Reduzca el radio"));
        }
        assertTrue(motor.llamadas.isEmpty());
    }

    @Test
    public void laBusquedaDeSitiosExigeDosCaracteres() {
        assertTrue(service.buscarSitios(" L ").isEmpty());
        verify(repositorio, never()).buscarSitios(anyString(), anyInt());
        service.buscarSitios("LI0137");
        verify(repositorio).buscarSitios("LI0137", 20);
    }

    private void esperarRechazo(SolicitudAuditoria s, String fragmento) {
        try {
            service.auditar(s);
            fail("Debia rechazarse: " + fragmento);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage(), e.getMessage().contains(fragmento));
        }
    }
}
