package com.tracker.web.auditoriared;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class ClusterRedTest {

    static final double LAT = -12.0, LON = -77.0;
    static double norte(double km) { return LAT + km / 111.32; }

    static CeldaAuditoriaDTO celda(String nombre, String cluster, double lat, int pci) {
        CeldaAuditoriaDTO c = new CeldaAuditoriaDTO(nombre, "5G", "C1", "N78", lat, LON, 0, pci, "32T32R", new double[8]);
        c.setCluster(cluster);
        return c;
    }

    static List<double[]> puntos(double... latitudes) {
        List<double[]> p = new ArrayList<>();
        for (double lat : latitudes) p.add(new double[] {lat, LON});
        return p;
    }

    @Test
    public void elAreaSonLasCeldasDelClusterYElMargenSeMideDesdeEllas() {
        ClusterRedAlcance a = new ClusterRedAlcance(" LI.Cluster036 ", puntos(LAT, norte(1)), 10.1);
        assertEquals("CLUSTER_RED", a.getTipo());
        assertEquals("LI.Cluster036", a.getCluster());
        assertTrue(a.incluye(celda("A", "li.cluster036", LAT, 1)));
        // Mismo lugar, otro cluster: es contexto, no area.
        assertFalse(a.incluye(celda("B", "LI.Cluster037", LAT, 1)));
        assertFalse(a.incluye(celda("C", null, LAT, 1)));
        // El margen se cuenta desde la celda mas cercana del cluster (la de 1 km al norte).
        assertTrue(a.contieneConMargen(norte(11.0), LON));
        assertFalse(a.contieneConMargen(norte(11.2), LON));
        CajaGeografica caja = a.cajaConMargen();
        assertTrue(caja.contiene(norte(11.0), LON) && caja.contiene(norte(-10.0), LON));
        assertTrue(a.getRadioKm() > 0.49 && a.getRadioKm() < 0.51);
    }

    @Test
    public void laClaveCambiaSiCambianLasCeldasDelCluster() {
        String clave = new ClusterRedAlcance("X", puntos(LAT), 10.1).clave();
        assertEquals(clave, new ClusterRedAlcance("x", puntos(LAT), 10.1).clave());
        assertFalse(clave.equals(new ClusterRedAlcance("X", puntos(LAT, norte(1)), 10.1).clave()));
        assertFalse(clave.equals(new ClusterRedAlcance("X", puntos(LAT), 5.1).clave()));
    }

    @Test
    public void rechazaClusterSinNombreOSinCeldas() {
        for (Runnable r : Arrays.<Runnable>asList(
                () -> new ClusterRedAlcance(" ", puntos(LAT), 10),
                () -> new ClusterRedAlcance("X", new ArrayList<double[]>(), 10),
                () -> new ClusterRedAlcance("X", puntos(LAT), -1),
                () -> new ClusterRedAlcance("X", Arrays.asList(new double[] {95, LON}), 10))) {
            try {
                r.run();
                fail("Debia rechazarse");
            } catch (IllegalArgumentException esperado) { /* ok */ }
        }
    }

    @Test
    public void auditaElClusterConContextoDeOtrosClustersYReglaDeBorde() {
        AuditoriaRedRepository repo = mock(AuditoriaRedRepository.class);
        when(repo.puntosCluster("5G", "LI.C1")).thenReturn(puntos(LAT, norte(0.5)));
        final List<CeldaAuditoriaDTO> red = Arrays.asList(
                celda("A", "LI.C1", LAT, 10), celda("B", "LI.C1", norte(0.5), 11),
                celda("C", "LI.C2", norte(5), 10), celda("E", "LI.C2", norte(6), 20),
                celda("D", "LI.C2", norte(50), 20));
        when(repo.leer(eq("5G"), any(CajaGeografica.class))).thenAnswer(inv -> {
            List<CeldaAuditoriaDTO> nuevas = new ArrayList<>();
            for (CeldaAuditoriaDTO c : red) nuevas.add(c.copia());
            return new AuditoriaRedRepository.Lectura(nuevas, nuevas.size(), 0);
        });
        AuditoriaRedServiceTest.MotorFalso motor = new AuditoriaRedServiceTest.MotorFalso();
        motor.conflictos.add(new ConflictoMotor("PCI_COLLISION", "A", "C", 5000, "10", null));
        motor.conflictos.add(new ConflictoMotor("PCI_COLLISION", "C", "E", 1000, "20", null));
        AuditoriaRedService service = AuditoriaRedServiceTest.servicio(repo, motor, 8000);
        SolicitudAuditoria s = new SolicitudAuditoria();
        s.setCluster("LI.C1");
        s.setTecnologias(Arrays.asList("5G"));

        ResultadoAuditoria r = service.auditar(s);

        assertTrue(r.getAlcance() instanceof ClusterRedAlcance);
        Set<String> enArea = new HashSet<>(), contexto = new HashSet<>();
        for (CeldaAuditoriaDTO c : r.getContexto()) { contexto.add(c.getNombre()); if (c.isDentroArea()) enArea.add(c.getNombre()); }
        assertEquals(new HashSet<>(Arrays.asList("A", "B")), enArea);
        assertEquals(new HashSet<>(Arrays.asList("A", "B", "C", "E")), contexto);
        ResumenTecnologia resumen = r.getResumen().get(0);
        assertEquals(2, resumen.getCeldasEnArea());
        assertEquals(2, resumen.getCeldasEnMargen());
        assertEquals(1, resumen.getFueraDelCirculo());
        assertEquals(1, resumen.getConflictosSoloMargen());
        assertEquals(1, r.getHallazgos().size());
        assertEquals("C", r.getHallazgos().get(0).getCeldaB().getNombre());
        assertEquals("LI.C2", r.getHallazgos().get(0).getCeldaB().getCluster());
        ArgumentCaptor<CajaGeografica> caja = ArgumentCaptor.forClass(CajaGeografica.class);
        verify(repo).leer(eq("5G"), caja.capture());
        assertTrue(caja.getValue().contiene(norte(10.5), LON));
    }

    @Test
    public void unClusterSinCeldasUbicablesSeExplicaSinLeerLaRed() {
        AuditoriaRedRepository repo = mock(AuditoriaRedRepository.class);
        when(repo.puntosCluster("5G", "NO.EXISTE")).thenReturn(new ArrayList<double[]>());
        AuditoriaRedService service = AuditoriaRedServiceTest.servicio(repo, new AuditoriaRedServiceTest.MotorFalso(), 8000);
        SolicitudAuditoria s = new SolicitudAuditoria();
        s.setCluster("NO.EXISTE");
        s.setTecnologias(Arrays.asList("5G"));
        try {
            service.auditar(s);
            fail("Un cluster vacio debia rechazarse");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage(), e.getMessage().contains("NO.EXISTE"));
        }
        verify(repo, never()).leer(eq("5G"), any(CajaGeografica.class));
    }

    @Test
    public void elListadoSumaCeldasPorTecnologiaYOrdenaPorNombre() {
        AuditoriaRedRepository repo = mock(AuditoriaRedRepository.class);
        Map<String, Integer> c4 = new LinkedHashMap<>(), c5 = new LinkedHashMap<>();
        c4.put("TJ.Cluster413", 40); c4.put("AR.Cluster937", 7);
        c5.put("TJ.Cluster413", 198); c5.put("LI.Cluster036", 12);
        when(repo.clusters("4G")).thenReturn(c4);
        when(repo.clusters("5G")).thenReturn(c5);
        AuditoriaRedService service = AuditoriaRedServiceTest.servicio(repo, new AuditoriaRedServiceTest.MotorFalso(), 8000);
        List<ClusterRedDTO> lista = service.clusters(Arrays.asList("5G", "4G"));
        assertEquals(3, lista.size());
        assertEquals("AR.Cluster937", lista.get(0).getNombre());
        assertEquals(7, lista.get(0).getCeldas4G());
        assertEquals(0, lista.get(0).getCeldas5G());
        assertEquals("TJ.Cluster413", lista.get(2).getNombre());
        assertEquals(40, lista.get(2).getCeldas4G());
        assertEquals(198, lista.get(2).getCeldas5G());
    }

    @Test
    public void elRepositorioLeeElClusterDeLaColumna17() throws Exception {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getString(1)).thenReturn("AUDRED_5G_CLUSTER");
        when(rs.getObject(2)).thenReturn("C1");
        when(rs.getObject(3)).thenReturn("500");
        when(rs.getObject(4)).thenReturn("N78");
        when(rs.getObject(5)).thenReturn("64T64R");
        when(rs.getObject(6)).thenReturn("-12.09");
        when(rs.getObject(7)).thenReturn("-77.05");
        when(rs.getObject(8)).thenReturn("0");
        when(rs.getObject(17)).thenReturn(" TEST.Cluster001 ");
        CeldaAuditoriaDTO c = AuditoriaRedRepository.aCelda("5G", rs);
        assertEquals("TEST.Cluster001", c.getCluster());
        assertEquals("TEST.Cluster001", c.copia().getCluster());
    }
}
