package com.tracker.web.auditoriared;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.LinkedCaseInsensitiveMap;

import com.tracker.web.auditoriared.AccesoJefaturaAuditoria;

public class LectorSitioPlanificadoTest {

    private JdbcTemplate jdbc;
    private LectorSitioPlanificado lector;
    private final List<String> avisos = new ArrayList<>();

    /** Fila de RECURSOS_FORMULARIO como la devuelve Oracle: ID NUMBER, latitud a veces texto. */
    static Map<String, Object> fila(long id, String sitio, String celda, String tecnologia, String jefatura) {
        Map<String, Object> f = new LinkedCaseInsensitiveMap<>();
        f.put("ID", BigDecimal.valueOf(id));
        f.put("SITE_NAME", sitio);
        f.put("CELL_NAME", celda);
        f.put("TECNOLOGIA", tecnologia);
        f.put("CARRIER", "C1");
        f.put("FREQ_BAND_TEXT", "N78");
        f.put("TXRX_MODE", "32T32R");
        f.put("LATITUD", "-12.0500");
        f.put("LONGITUD", -77.04);
        f.put("AZIMUTH", 120.0);
        f.put("JEFATURA", jefatura);
        return f;
    }

    static AccesoJefaturaAuditoria jefaturas(String declaradas) {
        AccesoJefaturaAuditoria r = new AccesoJefaturaAuditoria();
        ReflectionTestUtils.setField(r, "jefaturasRaw", declaradas);
        r.init();
        return r;
    }

    static void usuario(String nombre, String... roles) {
        SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(nombre, "x", roles));
    }

    static void responder(JdbcTemplate jdbc, List<Map<String, Object>> filas) {
        when(jdbc.query(anyString(), any(Object[].class), ArgumentMatchers.<RowMapper<Map<String, Object>>>any()))
                .thenReturn(filas);
    }

    @Before
    public void preparar() {
        jdbc = mock(JdbcTemplate.class);
        lector = new LectorSitioPlanificado(jdbc, jefaturas("ana:ZONA LIMA,beto:ZONA NORTE"));
        usuario("ana", "ROLE_USER");
        responder(jdbc, Arrays.asList(
                fila(12, "LI0168", "LIR01681C1_BONNEMAISON", "5G", " zona lima "),
                fila(13, "LI0168", "LIR01682C1_BONNEMAISON", "5G NR", "ZONA LIMA")));
    }

    @After
    public void limpiar() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void elPciEsElDeLaPantalla_noElDeLaBd_yLaCeldaVaSinTa() {
        List<CeldaAuditoriaDTO> celdas = lector.leer(Arrays.asList(13L, 12L), Arrays.asList(884, 883), avisos);

        assertEquals(2, celdas.size());
        CeldaAuditoriaDTO c = celdas.get(0);
        assertEquals("LIR01682C1_BONNEMAISON", c.getNombre());
        assertEquals(884, c.getPci());
        assertEquals("5G", c.getTecnologia());
        assertEquals("C1", c.getCarrier());
        assertEquals("N78", c.getBanda());
        assertEquals("32T32R", c.getTxrxmode());
        assertEquals(-12.05, c.getLatitud(), 1e-9);
        assertEquals(120.0, c.getAzimuth(), 1e-9);
        assertArrayEquals(new double[8], c.getTa(), 0);
        assertTrue(c.isPlanificada());
        assertEquals(883, celdas.get(1).getPci());
        assertTrue(avisos.isEmpty());
        verify(jdbc).query(eq(LectorSitioPlanificado.SQL + "?,?)"), any(Object[].class),
                ArgumentMatchers.<RowMapper<Map<String, Object>>>any());
    }

    @Test
    public void idsYPciDesparejosSeRechazanSinLeerLaBd() {
        esperarRechazo(Arrays.asList(12L, 13L), Arrays.asList(883), "un PCI por cada id");
        esperarRechazo(Collections.<Long>emptyList(), Collections.<Integer>emptyList(), "un PCI por cada id");
        esperarRechazo(Arrays.asList(12L, 12L), Arrays.asList(883, 884), "repetidos");
        List<Long> muchos = new ArrayList<>();
        List<Integer> pcis = new ArrayList<>();
        for (long i = 0; i <= LectorSitioPlanificado.MAX_CELDAS; i++) { muchos.add(i); pcis.add(1); }
        esperarRechazo(muchos, pcis, "Maximo 30");
        verify(jdbc, never()).query(anyString(), any(Object[].class), ArgumentMatchers.<RowMapper<Map<String, Object>>>any());
    }

    @Test
    public void unIdDeOtraJefaturaSeRechaza() {
        responder(jdbc, Arrays.asList(
                fila(12, "LI0168", "LIR01681C1_BONNEMAISON", "5G", "ZONA LIMA"),
                fila(13, "LI0168", "LIR01682C1_BONNEMAISON", "5G", "ZONA NORTE")));
        try {
            lector.leer(Arrays.asList(12L, 13L), Arrays.asList(883, 884), avisos);
            fail("Un registro de otra jefatura debia rechazarse");
        } catch (AccessDeniedException e) {
            assertTrue(e.getMessage(), e.getMessage().contains("13"));
        }
    }

    @Test
    public void unaFilaSinJefaturaSoloLaVeElAdmin() {
        responder(jdbc, Arrays.asList(fila(12, "LI0168", "LIR01681C1_BONNEMAISON", "5G", null)));
        try {
            lector.leer(Arrays.asList(12L), Arrays.asList(883), avisos);
            fail("Una fila huerfana solo la ve ROLE_ADMIN");
        } catch (AccessDeniedException esperado) {
            // igual que AccesoJefaturaAuditoria.coincide en /asignar
        }
        usuario("root", "ROLE_ADMIN");
        assertEquals(1, lector.leer(Arrays.asList(12L), Arrays.asList(883), avisos).size());
    }

    @Test
    public void unUsuarioSinJefaturaDeclaradaNoLlegaALaBd() {
        usuario("carla", "ROLE_USER");
        try {
            lector.leer(Arrays.asList(12L), Arrays.asList(883), avisos);
            fail("Sin jefatura declarada debia rechazarse");
        } catch (AccessDeniedException esperado) {
            // falla cerrado, como AccesoJefaturaAuditoria
        }
        verify(jdbc, never()).query(anyString(), any(Object[].class), ArgumentMatchers.<RowMapper<Map<String, Object>>>any());
    }

    @Test
    public void unIdQueNoExisteSeRechaza() {
        esperarRechazo(Arrays.asList(12L, 99L), Arrays.asList(883, 884), "No existe el registro 99");
    }

    @Test
    public void unSitioPorVez() {
        responder(jdbc, Arrays.asList(
                fila(12, "LI0168", "LIR01681C1_BONNEMAISON", "5G", "ZONA LIMA"),
                fila(13, "LI0200", "LIR02001C1_OTRO", "5G", "ZONA LIMA")));
        esperarRechazo(Arrays.asList(12L, 13L), Arrays.asList(883, 884), "Un sitio por vez");
    }

    @Test
    public void elPciTieneElRangoDeSuTecnologia() {
        responder(jdbc, Arrays.asList(fila(12, "LI0168", "LIL01681C2", "LTE 4G", "ZONA LIMA")));
        esperarRechazo(Arrays.asList(12L), Arrays.asList(504), "fuera de 0-503 (4G)");
        esperarRechazo(Arrays.asList(12L), Arrays.asList(-1), "fuera de 0-503");
        assertEquals(503, lector.leer(Arrays.asList(12L), Arrays.asList(503), avisos).get(0).getPci());

        responder(jdbc, Arrays.asList(fila(12, "LI0168", "LIR01681C1", "5G", "ZONA LIMA")));
        assertEquals(1007, lector.leer(Arrays.asList(12L), Arrays.asList(1007), avisos).get(0).getPci());
        esperarRechazo(Arrays.asList(12L), Arrays.asList(1008), "fuera de 0-1007 (5G)");
    }

    @Test
    public void las3gSeOmitenConAviso_ySiSoloHay3gSeRechaza() {
        responder(jdbc, Arrays.asList(
                fila(12, "LI0168", "LIR01681C1_BONNEMAISON", "5G", "ZONA LIMA"),
                fila(14, "LI0168", "LIU01681", "3G", "ZONA LIMA")));
        List<CeldaAuditoriaDTO> celdas = lector.leer(Arrays.asList(12L, 14L), Arrays.asList(883, 7), avisos);
        assertEquals(1, celdas.size());
        assertEquals(1, avisos.size());
        assertTrue(avisos.get(0), avisos.get(0).contains("LIU01681") && avisos.get(0).contains("3G"));

        responder(jdbc, Arrays.asList(fila(14, "LI0168", "LIU01681", "3G", "ZONA LIMA")));
        esperarRechazo(Arrays.asList(14L), Arrays.asList(7), "3G todavia no existe");
    }

    @Test
    public void sinCoordenadasNiAzimuthNoSeInventaNada() {
        Map<String, Object> f = fila(12, "LI0168", "LIR01681C1_BONNEMAISON", "5G", "ZONA LIMA");
        f.put("AZIMUTH", null);
        responder(jdbc, Arrays.asList(f));
        esperarRechazo(Arrays.asList(12L), Arrays.asList(883), "faltan latitud, longitud o azimuth");
    }

    @Test
    public void laTecnologiaSeNormalizaComoEnLaAsignacion() {
        assertEquals("4G", LectorSitioPlanificado.tecnologia(" lte "));
        assertEquals("4G", LectorSitioPlanificado.tecnologia("4G"));
        assertEquals("5G", LectorSitioPlanificado.tecnologia("5G NR SA"));
        assertNull(LectorSitioPlanificado.tecnologia("4G/5G"));
        assertNull(LectorSitioPlanificado.tecnologia("3G"));
        assertNull(LectorSitioPlanificado.tecnologia(null));
    }

    private void esperarRechazo(List<Long> ids, List<Integer> pcis, String fragmento) {
        try {
            lector.leer(ids, pcis, avisos);
            fail("Debia rechazarse: " + fragmento);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage(), e.getMessage().contains(fragmento));
        }
    }
}
