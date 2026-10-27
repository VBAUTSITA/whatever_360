package com.tracker.web.auditoriared;
import static org.junit.Assert.*;
import java.util.*;
import org.junit.Test;

/** Actual Java 8/Nashorn contract, including sparse data and carrier/band isolation. */
public class VecindadMotorTest {
    private AdaptadorMotorAuditoria motor() { return new AdaptadorMotorAuditoria("scripts/auditoriared/planner_4g_2_MIRROR_FIXED.js","scripts/auditoriared/old_planner_5g.js"); }
    private CeldaAuditoriaDTO cell(String name,String tec,String band,double lat,int pci,boolean ta) {
        return new CeldaAuditoriaDTO(name,tec,"C1",band,lat,-77,lat < -12 ? 0 : 180,pci,"4T4R",ta?new double[]{0,0,0,1,0,0,0,0}:new double[8]);
    }
    @Test public void ambosMotoresDanDistanciaSolapamientoYNoMezclanBandas() {
        for(String tec:Arrays.asList("4G","5G")) {
            List<CeldaAuditoriaDTO> cells=Arrays.asList(cell("A",tec,"7",-12,100,true),cell("B",tec,"7",-12.005,100,true),cell("OTRA",tec,"28",-12.0001,100,true));
            InteraccionVecinaDTO n=motor().vecinos(tec,cells,"A",30).get(0);
            assertEquals("B",n.getCelda().getNombre()); assertEquals(556,n.getDistanciaMetros(),2);
            assertTrue(n.isMismoPci()); assertTrue(n.isMismoMod3()); assertTrue(n.isMismoRsi());
            assertNotNull(n.getAreaInterseccionM2()); assertTrue(n.getAreaInterseccionM2()>0);
            assertEquals(n.getAreaInterseccionM2()/n.getDistanciaMetros(),n.getInterseccionNormalizadaM(),1e-6);
            assertFalse(n.isSinTaVecina());
        }
    }
    @Test public void sinTaSeInformaRespaldoYVecinasOrdenadasSinIncluirse() {
        List<CeldaAuditoriaDTO> cells=Arrays.asList(cell("A","5G","7",-12,100,false),cell("FAR","5G","7",-12.02,103,false),cell("NEAR","5G","7",-12.001,101,false));
        List<InteraccionVecinaDTO> n=motor().vecinos("5G",cells,"A",1);
        assertEquals(1,n.size()); assertEquals("NEAR",n.get(0).getCelda().getNombre());
        assertTrue(n.get(0).isSinTaObjetivo()); assertTrue(n.get(0).isSinTaVecina());
    }
    @Test public void unaCeldaSolaNoInventaVecinasYNombreNoEjecutaCodigo() {
        String name="A'\";throw new Error('inyeccion');//\u2028";
        assertTrue(motor().vecinos("5G",Arrays.asList(cell(name,"5G","7",-12,100,true)),name,30).isEmpty());
    }
}
