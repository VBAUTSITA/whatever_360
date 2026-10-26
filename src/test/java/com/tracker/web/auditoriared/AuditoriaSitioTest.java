package com.tracker.web.auditoriared;

import static com.tracker.web.auditoriared.AuditoriaRedServiceTest.LAT;
import static com.tracker.web.auditoriared.AuditoriaRedServiceTest.LON;
import static com.tracker.web.auditoriared.AuditoriaRedServiceTest.celda;
import static com.tracker.web.auditoriared.AuditoriaRedServiceTest.norte;
import static com.tracker.web.auditoriared.AuditoriaRedServiceTest.responder;
import static com.tracker.web.auditoriared.AuditoriaRedServiceTest.solicitud;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.script.ScriptEngineManager;

import org.junit.Before;
import org.junit.Test;

/** Modo sitio: las celdas de /recursos/asignar con su PCI propuesto contra la red existente. */
public class AuditoriaSitioTest {

    private AuditoriaRedRepository repositorio;
    private AuditoriaRedServiceTest.MotorFalso motor;
    private AuditoriaRedService service;

    static CeldaAuditoriaDTO planificada(String nombre, double lat, int pci) {
        CeldaAuditoriaDTO c = celda(nombre, "4G", lat, LON, pci);
        c.setPlanificada(true);
        return c;
    }

    static Set<String> nombres(List<CeldaAuditoriaDTO> celdas) {
        Set<String> salida = new HashSet<>();
        for (CeldaAuditoriaDTO c : celdas) salida.add(c.getNombre());
        return salida;
    }

    @Before
    public void preparar() {
        repositorio = mock(AuditoriaRedRepository.class);
        motor = new AuditoriaRedServiceTest.MotorFalso();
        service = AuditoriaRedServiceTest.servicio(repositorio, motor, 40000);
        responder(repositorio, "4G", Arrays.asList(
                celda("LIL01681C1", "4G", LAT, LON, 50),        // la misma celda ya existe: re-planificacion
                celda("VECINA_V", "4G", norte(3), LON, 100),
                celda("EXIST_X", "4G", norte(-4), LON, 200),
                celda("EXIST_Y", "4G", norte(-4.5), LON, 200)));
        responder(repositorio, "5G", Collections.<CeldaAuditoriaDTO>emptyList());
    }

    private ResultadoAuditoria auditarSitio(CeldaAuditoriaDTO... planificadas) {
        return service.auditarSitio(new ArrayList<>(Arrays.asList(planificadas)), Arrays.asList("aviso de prueba"));
    }

    @Test
    public void laPlanificadaReemplazaALaExistenteDelMismoNombre() {
        auditarSitio(planificada("lil01681c1", LAT, 100), planificada("LIL01682C1", LAT, 101));

        List<CeldaAuditoriaDTO> enviadas = motor.llamadas.get(0);
        assertEquals(5, enviadas.size());
        int conEseNombre = 0;
        for (CeldaAuditoriaDTO c : enviadas) {
            if (c.getNombre().equalsIgnoreCase("LIL01681C1")) {
                conEseNombre++;
                assertEquals("Viaja el PCI propuesto, no el existente", 100, c.getPci());
                assertTrue(c.isPlanificada());
            }
        }
        assertEquals(1, conEseNombre);
    }

    @Test
    public void soloQuedanLosParesEnQueParticipaUnaCeldaDelSitio() {
        motor.conflictos.add(new ConflictoMotor("PCI_COLLISION", "LIL01682C1", "VECINA_V", 3000, "100", null));
        motor.conflictos.add(new ConflictoMotor("PCI_COLLISION", "EXIST_X", "EXIST_Y", 556, "200", null));

        ResultadoAuditoria r = auditarSitio(planificada("LIL01682C1", LAT, 100));

        assertEquals(1, r.getHallazgos().size());
        assertEquals("VECINA_V", r.getHallazgos().get(0).getCeldaB().getNombre());
        // El mapa recibe el sitio, la vecina en conflicto y, como contexto, las demas vecinas: sin hallazgo.
        assertEquals(new HashSet<>(Arrays.asList("LIL01682C1", "VECINA_V", "LIL01681C1", "EXIST_X", "EXIST_Y")),
                nombres(r.getCeldas()));
        for (CeldaAuditoriaDTO c : r.getCeldas()) {
            assertEquals(c.getNombre(), c.getNombre().equals("LIL01682C1") || c.getNombre().equals("VECINA_V"),
                    c.isConHallazgo());
        }
        ResumenTecnologia t = r.getResumen().get(0);
        assertEquals(1, t.getCeldasEnArea());
        assertEquals(4, t.getCeldasEnMargen());
        assertEquals(1, t.getConflictosSoloMargen());
        assertEquals(Arrays.asList("aviso de prueba"), r.getAvisos());
    }

    @Test
    public void seMuestranLas30VecinasMasCercanasDelMismoCarrierYBanda_aunqueNoChoquen() {
        List<CeldaAuditoriaDTO> red = new ArrayList<>();
        for (int i = 1; i <= 35; i++) red.add(celda("V" + i, "4G", norte(i * 0.1), LON, i));
        CeldaAuditoriaDTO minusculas = celda("MINUS", "4G", norte(0.01), LON, 50);
        minusculas.setCarrier(" c1 ");   // el motor la pone en el mismo pool: claves sin espacios ni mayusculas
        CeldaAuditoriaDTO otraBanda = celda("OTRA_BANDA", "4G", norte(0.02), LON, 51);
        otraBanda.setBanda("4");
        CeldaAuditoriaDTO otroCarrier = celda("OTRO_CARRIER", "4G", norte(0.02), LON, 52);
        otroCarrier.setCarrier("C2");
        red.addAll(Arrays.asList(minusculas, otraBanda, otroCarrier));
        responder(repositorio, "4G", red);

        ResultadoAuditoria r = auditarSitio(planificada("NUEVA", LAT, 400));

        Set<String> mostradas = nombres(r.getCeldas());
        assertEquals(1 + AuditoriaRedService.VECINAS_SITIO, mostradas.size());
        assertTrue(mostradas.contains("NUEVA"));
        assertTrue(mostradas.contains("MINUS"));
        for (int i = 1; i <= 29; i++) assertTrue("V" + i, mostradas.contains("V" + i));
        for (int i = 30; i <= 35; i++) assertFalse("V" + i, mostradas.contains("V" + i));
        assertFalse(mostradas.contains("OTRA_BANDA"));
        assertFalse(mostradas.contains("OTRO_CARRIER"));
        assertTrue(r.getHallazgos().isEmpty());
    }

    @Test
    public void laAuditoriaDeClusterNoMuestraVecinasSinHallazgo() {
        // El modo cluster sigue igual: del margen, solo las celdas con hallazgo.
        ResultadoAuditoria r = service.auditar(solicitud(0.5, "4G"));
        assertEquals(new HashSet<>(Arrays.asList("LIL01681C1")), nombres(r.getCeldas()));
    }

    @Test
    public void elAlcanceSeCentraEnElSitio_conElRadioDeReusoComoMargen() {
        ResultadoAuditoria r = auditarSitio(planificada("A", LAT, 1), planificada("B", norte(0.2), 2));

        ClusterAlcance a = (ClusterAlcance) r.getAlcance();
        assertEquals(norte(0.1), a.getLatitud(), 1e-9);
        assertEquals(0.11, a.getRadioKm(), 1e-3);
        assertEquals(10.1, a.getMargenKm(), 1e-9);
        assertEquals(10000, motor.ultimoRadioReuso);
        assertEquals(1, r.getResumen().size());
        assertEquals("4G", r.getResumen().get(0).getTecnologia());
    }

    @Test
    public void celdasDelSitioMuySeparadasSeRechazan() {
        try {
            auditarSitio(planificada("A", LAT, 1), planificada("B", norte(45), 2));
            fail("Coordenadas a 45 km dentro de un mismo sitio debian rechazarse");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage(), e.getMessage().contains("revise sus coordenadas"));
        }
        verify(repositorio, never()).leer(eq("4G"), any(CajaGeografica.class));
    }

    @Test
    public void laAuditoriaDeSitioNoUsaLaCache() {
        service.auditar(solicitud(2, "4G"));
        ResultadoAuditoria primera = auditarSitio(planificada("LIL01682C1", LAT, 100));
        ResultadoAuditoria segunda = auditarSitio(planificada("LIL01682C1", LAT, 101));

        assertFalse(primera.isDesdeCache());
        assertFalse(segunda.isDesdeCache());
        verify(repositorio, times(3)).leer(eq("4G"), any(CajaGeografica.class));
        // Y tampoco la ensucia: la auditoria de cluster sigue saliendo de su propia entrada.
        assertTrue(service.auditar(solicitud(2, "4G")).isDesdeCache());
        verify(repositorio, times(3)).leer(eq("4G"), any(CajaGeografica.class));
    }

    @Test
    public void respetaElCandadoDeUnaAuditoriaALaVez() throws Exception {
        motor.dentro = new CountDownLatch(1);
        motor.soltar = new CountDownLatch(1);
        ExecutorService hilo = Executors.newSingleThreadExecutor();
        try {
            Future<ResultadoAuditoria> cluster = hilo.submit(new Callable<ResultadoAuditoria>() {
                @Override public ResultadoAuditoria call() { return service.auditar(solicitud(2, "4G")); }
            });
            assertTrue(motor.dentro.await(5, TimeUnit.SECONDS));
            try {
                auditarSitio(planificada("LIL01682C1", LAT, 100));
                fail("Con otra auditoria en curso debia rechazarse");
            } catch (AuditoriaOcupadaException esperado) {
                // la de cluster sigue
            }
            motor.soltar.countDown();
            cluster.get(5, TimeUnit.SECONDS);
        } finally {
            hilo.shutdownNow();
        }
    }

    @Test
    public void motorReal_unPciPropuestoQueYaUsaUnaVecinaCercanaEsColision() {
        assumeTrue(new ScriptEngineManager().getEngineByName("nashorn") != null);
        AuditoriaRedService real = new AuditoriaRedService(repositorio,
                new AdaptadorMotorAuditoria("scripts/auditoriared/planner_4g_2_MIRROR_FIXED.js",
                        "scripts/auditoriared/old_planner_5g.js"),
                AuditoriaRedServiceTest.catalogo(), 10000, 20, 40000, 8000, 15);
        CeldaAuditoriaDTO otraBanda = celda("OTRA_BANDA", "4G", norte(0.4), LON, 100);
        otraBanda.setBanda("4");
        responder(repositorio, "4G", Arrays.asList(
                celda("VECINA_C1", "4G", norte(0.5), LON, 100),
                otraBanda,
                celda("EXIST_X", "4G", norte(-2), LON, 200),
                celda("EXIST_Y", "4G", norte(-2.5), LON, 200),
                celda("LEJANA", "4G", norte(1.5), LON, 7)));

        ResultadoAuditoria r = real.auditarSitio(
                new ArrayList<>(Arrays.asList(planificada("NUEVA_1", LAT, 100))), Collections.<String>emptyList());

        Set<String> tipos = new HashSet<>();
        for (HallazgoAuditoriaDTO h : r.getHallazgos()) {
            tipos.add(h.getTipo());
            Set<String> par = new HashSet<>(Arrays.asList(h.getCeldaA().getNombre(), h.getCeldaB().getNombre()));
            assertEquals(new HashSet<>(Arrays.asList("NUEVA_1", "VECINA_C1")), par);
            assertEquals(500, h.getDistanciaMetros(), 2); // 0.5 km al norte
        }
        assertEquals(new HashSet<>(Arrays.asList("PCI_COLLISION", "RSI_COLLISION")), tipos);
        // La planificada no tiene TA: el motor le da el radio promedio de sus vecinas, como en el paso 4.
        for (CeldaAuditoriaDTO c : r.getCeldas()) {
            if (c.getNombre().equals("NUEVA_1")) {
                assertTrue(c.getSinTa());
                assertTrue(c.getRadioM() > 0);
            }
        }
    }
}
