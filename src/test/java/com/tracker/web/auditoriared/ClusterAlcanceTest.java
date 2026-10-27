package com.tracker.web.auditoriared;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ClusterAlcanceTest {

    private final ClusterAlcance alcance = new ClusterAlcance(-12.0, -77.0, 2, 10.1);

    @Test
    public void separaElAreaDelMargen() {
        double a1km = -12.0 + 1 / 111.32;
        double a8km = -12.0 + 8 / 111.32;
        double a13km = -12.0 + 13 / 111.32;
        assertTrue(alcance.contiene(a1km, -77.0));
        assertFalse(alcance.contiene(a8km, -77.0));
        assertTrue(alcance.contieneConMargen(a8km, -77.0));
        assertFalse(alcance.contieneConMargen(a13km, -77.0));
    }

    @Test
    public void laCajaSqlCubreTodoElCirculoConMargen() {
        CajaGeografica caja = alcance.cajaConMargen();
        double km = 12.1 - 0.01;
        double cosLat = Math.cos(Math.toRadians(12.0));
        assertTrue(caja.contiene(-12.0 + km / 111.32, -77.0));
        assertTrue(caja.contiene(-12.0 - km / 111.32, -77.0));
        assertTrue(caja.contiene(-12.0, -77.0 + km / (111.32 * cosLat)));
        assertTrue(caja.contiene(-12.0, -77.0 - km / (111.32 * cosLat)));
    }

    @Test public void noRecortaElBordeEsfericoNiElAntimeridianoNiElPolo() {
        for (ClusterAlcance c : new ClusterAlcance[]{alcance,new ClusterAlcance(89.99,179.99,2,10.1),new ClusterAlcance(0,179.99,2,10.1)}) {
            double lat=Math.toRadians(c.getLatitud()),lon=Math.toRadians(c.getLongitud());
            double d=(c.getRadioKm()+c.getMargenKm()-0.000001)/6371.0088;
            for(int az=0;az<360;az+=5) {
                double b=Math.toRadians(az),y=Math.asin(Math.sin(lat)*Math.cos(d)+Math.cos(lat)*Math.sin(d)*Math.cos(b));
                double x=lon+Math.atan2(Math.sin(b)*Math.sin(d)*Math.cos(lat),Math.cos(d)-Math.sin(lat)*Math.sin(y));
                double latP=Math.toDegrees(y),lonP=((Math.toDegrees(x)+540)%360)-180;
                assertTrue(c.contieneConMargen(latP,lonP)); assertTrue("Caja recorta rumbo "+az,c.cajaConMargen().contiene(latP,lonP));
            }
        }
    }

    @Test
    public void laClaveIdentificaElArea() {
        assertEquals(alcance.clave(), new ClusterAlcance(-12.0, -77.0, 2, 10.1).clave());
        assertFalse(alcance.clave().equals(new ClusterAlcance(-12.0, -77.0, 3, 10.1).clave()));
        assertEquals("CIRCULO", alcance.getTipo());
    }

    @Test(expected = IllegalArgumentException.class)
    public void rechazaUnRadioNoPositivo() {
        new ClusterAlcance(-12.0, -77.0, 0, 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rechazaCoordenadasImposibles() {
        new ClusterAlcance(-95.0, -77.0, 2, 10);
    }
}
