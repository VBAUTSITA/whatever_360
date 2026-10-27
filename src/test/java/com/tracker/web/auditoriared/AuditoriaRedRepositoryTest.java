package com.tracker.web.auditoriared;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Test;

public class AuditoriaRedRepositoryTest {

    @Test
    public void losNumerosVienenComoTextoYLasTaConElPorcentajePegado() {
        assertEquals(39.82, AuditoriaRedRepository.numero("39.82%"), 1e-9);
        assertEquals(-77.0428, AuditoriaRedRepository.numero(" -77.0428 "), 1e-9);
        assertEquals(120.0, AuditoriaRedRepository.numero(120), 1e-9);
        assertNull(AuditoriaRedRepository.numero(""));
        assertNull(AuditoriaRedRepository.numero("abc"));
        assertNull(AuditoriaRedRepository.numero(null));
    }

    @Test
    public void lasTaSeLlevanAFraccionesComoEnProduccion() {
        double[] porcentajes = {50, 30, 20, 0, 0, 0, 0, 0};
        assertArrayEquals(new double[] {0.5, 0.3, 0.2, 0, 0, 0, 0, 0},
                AuditoriaRedRepository.normalizarTA(porcentajes), 1e-9);
        double[] fracciones = {0.5, 0.5, 0, 0, 0, 0, 0, 0};
        assertArrayEquals(fracciones, AuditoriaRedRepository.normalizarTA(fracciones), 1e-9);
        // Si no suman ni ~1 ni ~100 se mandan en cero: el motor usa su respaldo.
        assertArrayEquals(new double[8], AuditoriaRedRepository.normalizarTA(new double[] {7, 0, 0, 0, 0, 0, 0, 0}), 1e-9);
    }

    private static ResultSet fila(Object pci, Object lat) throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getString(1)).thenReturn(" LIL01378C1_X ");
        when(rs.getObject(2)).thenReturn("C1");
        when(rs.getObject(3)).thenReturn(pci);
        when(rs.getObject(4)).thenReturn("2");
        when(rs.getObject(5)).thenReturn("4T4R");
        when(rs.getObject(6)).thenReturn(lat);
        when(rs.getObject(7)).thenReturn("-77.07125");
        when(rs.getObject(8)).thenReturn("180");
        when(rs.getObject(9)).thenReturn("60%");
        when(rs.getObject(10)).thenReturn("40%");
        for (int i = 11; i <= 16; i++) when(rs.getObject(i)).thenReturn("0%");
        return rs;
    }

    @Test
    public void unaFilaCompletaSeConvierteEnCelda() throws SQLException {
        CeldaAuditoriaDTO c = AuditoriaRedRepository.aCelda("4G", fila("123", "-12.092056"));
        assertEquals("LIL01378C1_X", c.getNombre());
        assertEquals("4G", c.getTecnologia());
        assertEquals(123, c.getPci());
        assertEquals(-12.092056, c.getLatitud(), 1e-9);
        assertEquals(180, c.getAzimuth(), 1e-9);
        assertEquals(0.6, c.getTa()[0], 1e-9);
        assertEquals("VALIDO_PORCENTAJE", c.getEstadoTa());
        assertEquals(0.6, c.paraMotor().get("ta_0_234_mts_porc"));
    }

    @Test
    public void sinPciOSinCoordenadasLaFilaSeDescartaEnVezDeInventarUnCero() throws SQLException {
        assertNull(AuditoriaRedRepository.aCelda("4G", fila(null, "-12.09")));
        assertNull(AuditoriaRedRepository.aCelda("4G", fila("123", "sin dato")));
    }

    @Test public void validaRangosYNumerosMalformados() throws SQLException {
        for(Object pci : new Object[]{-1,504,100.5,"NaN","Infinity"}) assertNull(AuditoriaRedRepository.aCelda("4G",fila(pci,"-12")));
        assertNull(AuditoriaRedRepository.aCelda("5G",fila(1008,"-12")));
        assertNull(AuditoriaRedRepository.aCelda("5G",fila(100,"-91")));
        ResultSet sinBanda=fila(100,"-12"); when(sinBanda.getObject(4)).thenReturn(null);
        assertNull(AuditoriaRedRepository.aCelda("5G",sinBanda));
        assertEquals(-12.09,AuditoriaRedRepository.numero("-12,09"),1e-9);
        assertArrayEquals(new double[8],AuditoriaRedRepository.normalizarTA(new double[]{-1,2,0,0,0,0,0,0}),1e-9);
        assertArrayEquals(new double[8],AuditoriaRedRepository.normalizarTA(new double[]{Double.NaN,0,0,0,0,0,0,0}),1e-9);
    }

    @Test public void taParcialOMalformadoNoSeConvierteSilenciosamenteEnCero() throws SQLException {
        ResultSet parcial = fila(100, "-12");
        when(parcial.getObject(11)).thenReturn(null);
        CeldaAuditoriaDTO c = AuditoriaRedRepository.aCelda("5G", parcial);
        assertEquals("INVALIDO", c.getEstadoTa());
        assertArrayEquals(new double[8], c.getTa(), 0);
        when(parcial.getObject(11)).thenReturn("dato incorrecto");
        assertEquals("INVALIDO", AuditoriaRedRepository.aCelda("5G", parcial).getEstadoTa());
    }

    @Test public void taRedondeadoSeNormalizaYLasUnidadesAmbiguasSeRechazan() {
        double[] redondeado = {33.3,33.3,33.3,0,0,0,0,0};
        assertEquals(1, java.util.Arrays.stream(AuditoriaRedRepository.normalizarTA(redondeado)).sum(), 1e-12);
        assertArrayEquals(new double[8], AuditoriaRedRepository.normalizarTA(new double[]{.7,0,0,0,0,0,0,0}),0);
        assertArrayEquals(new double[8], AuditoriaRedRepository.normalizarTA(new double[]{70,0,0,0,0,0,0,0}),0);
        assertEquals("INVALIDO", AuditoriaRedRepository.leerTa(new Object[]{"0.5%","0.5%",0,0,0,0,0,0}).estado);
        assertEquals("AUSENTE", AuditoriaRedRepository.leerTa(new Object[8]).estado);
        assertEquals("AUSENTE", AuditoriaRedRepository.leerTa(new Object[]{0,0,0,0,0,0,0,0}).estado);
    }
}
