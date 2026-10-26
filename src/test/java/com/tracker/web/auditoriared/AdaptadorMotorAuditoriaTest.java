package com.tracker.web.auditoriared;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.script.ScriptEngineManager;

import org.junit.Test;

/**
 * Las pruebas con el motor real necesitan Nashorn: corren en Java 8 (Eclipse) o en un JDK nuevo
 * con nashorn-core en el classpath. Sin Nashorn se saltan; las de agrupacion corren siempre.
 */
public class AdaptadorMotorAuditoriaTest {

    private static final String RUTA_4G = "scripts/auditoriared/planner_4g_2_MIRROR_FIXED.js";
    private static final String RUTA_5G = "scripts/auditoriared/old_planner_5g.js";

    static CeldaAuditoriaDTO celda(String nombre, String tec, String banda, int pci, double lat, double az) {
        return new CeldaAuditoriaDTO(nombre, tec, "C1", banda, lat, -77.05, az, pci, "4T4R", new double[8]);
    }

    private static boolean hayNashorn() {
        return new ScriptEngineManager().getEngineByName("nashorn") != null;
    }

    @Test
    public void laClaveDeGrupoEsCarrierMasBanda_comoLosPoolsDelMotor() {
        CeldaAuditoriaDTO c = celda("X", "4G", " 7 ", 1, -12, 0);
        c.setCarrier(" c2 ");
        assertEquals("C2|7", AdaptadorMotorAuditoria.claveGrupo(c));
        c.setBanda(null);
        assertEquals("C2|", AdaptadorMotorAuditoria.claveGrupo(c));
    }

    @Test
    public void laVersionIdentificaArchivoYHuella() {
        AdaptadorMotorAuditoria adaptador = new AdaptadorMotorAuditoria(RUTA_4G, RUTA_5G);
        assertTrue(adaptador.version("4G").matches("planner_4g_2_MIRROR_FIXED\\.js \\([0-9A-F]{8}\\)"));
        assertTrue(adaptador.version("5G").startsWith("old_planner_5g.js ("));
    }

    @Test(expected = IllegalStateException.class)
    public void unMotorQueNoEstaEnElClasspathFallaAlArrancar() {
        new AdaptadorMotorAuditoria("scripts/auditoriared/no_existe.js", RUTA_5G);
    }

    @Test
    public void motorReal4g_mismoPciMismaBandaEsColision_yEnOtraBandaNo() {
        assumeTrue(hayNashorn());
        AdaptadorMotorAuditoria adaptador = new AdaptadorMotorAuditoria(RUTA_4G, RUTA_5G);
        List<CeldaAuditoriaDTO> celdas = Arrays.asList(
                celda("A_C1", "4G", "2", 100, -12.0900, 0),
                celda("B_C1", "4G", "2", 100, -12.0950, 180),
                celda("X_C1", "4G", "4", 100, -12.0905, 90));

        ResultadoMotor r = adaptador.auditar("4G", celdas, 10000, 8000);

        Set<String> tipos = new HashSet<>();
        for (ConflictoMotor c : r.getConflictos()) {
            tipos.add(c.getTipo());
            assertFalse("Otra banda no puede colisionar: " + c.getNombreA() + "/" + c.getNombreB(),
                    c.getNombreA().startsWith("X") || c.getNombreB().startsWith("X"));
            assertEquals(556, c.getDistanciaMetros(), 2);
        }
        assertEquals(new HashSet<>(Arrays.asList("PCI_COLLISION", "RSI_COLLISION")), tipos);
        assertFalse(r.isCapped());

        // Segunda llamada: el motor ya compilado se reutiliza y da lo mismo.
        assertEquals(r.getConflictos().size(), adaptador.auditar("4G", celdas, 10000, 8000).getConflictos().size());
    }

    @Test
    public void motorReal5g_mismoContrato() {
        assumeTrue(hayNashorn());
        AdaptadorMotorAuditoria adaptador = new AdaptadorMotorAuditoria(RUTA_4G, RUTA_5G);
        List<CeldaAuditoriaDTO> celdas = Arrays.asList(
                celda("A_N78", "5G", "N78", 100, -12.0900, 0),
                celda("B_N78", "5G", "N78", 100, -12.0950, 180));

        ResultadoMotor r = adaptador.auditar("5G", celdas, 10000, 8000);

        Set<String> tipos = new HashSet<>();
        for (ConflictoMotor c : r.getConflictos()) tipos.add(c.getTipo());
        assertTrue(tipos.contains("PCI_COLLISION"));
    }

    static CeldaAuditoriaDTO conTa(String nombre, String tec, String txrx, double az, double lat, double... ta) {
        double[] v = new double[8];
        System.arraycopy(ta, 0, v, 0, ta.length);
        return new CeldaAuditoriaDTO(nombre, tec, "C1", "7", lat, -77.05, az, 10, txrx, v);
    }

    /** Las reglas de la lamina del paso 6 salen del motor, no de una copia en Java o en el JS del mapa. */
    @Test
    public void motorReal4g_geometriaDelPaso6() {
        assumeTrue(hayNashorn());
        AdaptadorMotorAuditoria adaptador = new AdaptadorMotorAuditoria(RUTA_4G, RUTA_5G);
        List<CeldaAuditoriaDTO> celdas = new java.util.ArrayList<>(Arrays.asList(
                conTa("LIL0001C1_MM_2", "4G", "32T32R", 100, -12.090, 0.5, 0.4, 0.1),
                conTa("LIL0001C1", "4G", "4T4R", 100, -12.091, 0.9),
                conTa("LIL0002C1", "4G", "4T4R", 200, -12.095)));
        for (int i = 0; i < 40; i++) celdas.add(conTa("X" + i, "4G", "4T4R", 0, -12.10 - i * 0.003, 0, 0, 1));

        java.util.Map<String, GeometriaSector> g = adaptador.geometria("4G", celdas,
                Arrays.asList("LIL0001C1_MM_2", "LIL0001C1", "LIL0002C1"));

        GeometriaSector mm = g.get("LIL0001C1_MM_2");
        assertEquals(90, mm.getAzimuthEfectivo(), 1e-9);   // MM_2 = AZ - 10
        assertEquals(30, mm.getHbeam(), 1e-9);             // 32T32R
        assertEquals(546, mm.getRadioM(), 1e-9);           // el TA acumula mas del 80 % en el 2.o tramo
        assertEquals(100, g.get("LIL0001C1").getHbeam(), 1e-9);   // 4T4R
        assertEquals(234, g.get("LIL0001C1").getRadioM(), 1e-9);
        assertTrue(g.get("LIL0002C1").isSinTa());          // sin TA: promedio de sus vecinas
        assertEquals(3, g.size());
    }

    @Test
    public void motorReal5g_elHazDe32t32rLoDecideElMotor5g() {
        assumeTrue(hayNashorn());
        AdaptadorMotorAuditoria adaptador = new AdaptadorMotorAuditoria(RUTA_4G, RUTA_5G);
        java.util.Map<String, GeometriaSector> g = adaptador.geometria("5G",
                Arrays.asList(conTa("LIR0001C1_MM_2", "5G", "32T32R", 100, -12.09, 0.9)),
                Arrays.asList("LIR0001C1_MM_2"));
        assertEquals(90, g.get("LIR0001C1_MM_2").getAzimuthEfectivo(), 1e-9);
        assertEquals(100, g.get("LIR0001C1_MM_2").getHbeam(), 1e-9);   // en 5G el motor usa 100 tambien en MM
    }

    @Test
    public void motorReal_fueraDelRadioDeReusoNoEsColision() {
        assumeTrue(hayNashorn());
        AdaptadorMotorAuditoria adaptador = new AdaptadorMotorAuditoria(RUTA_4G, RUTA_5G);
        List<CeldaAuditoriaDTO> celdas = Arrays.asList(
                celda("A_C1", "4G", "2", 100, -12.0900, 0),
                celda("B_C1", "4G", "2", 100, -12.0950, 180));

        ResultadoMotor r = adaptador.auditar("4G", celdas, 300, 8000);

        for (ConflictoMotor c : r.getConflictos()) {
            assertFalse(c.getTipo().endsWith("COLLISION"));
        }
    }
}
