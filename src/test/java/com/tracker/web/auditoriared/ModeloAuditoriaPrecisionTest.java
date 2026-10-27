package com.tracker.web.auditoriared;

import static org.junit.Assert.*;
import java.util.*;
import org.junit.Test;

/** Expectativas independientes: límites TA documentados, radio geodésico y evidencia faltante. */
public class ModeloAuditoriaPrecisionTest {
    private AdaptadorMotorAuditoria motor() { return new AdaptadorMotorAuditoria("scripts/auditoriared/planner_4g_2_MIRROR_FIXED.js","scripts/auditoriared/old_planner_5g.js"); }
    private CeldaAuditoriaDTO c(String n,String t,double lat,double lon,int pci,double az,double... ta) {
        double[] bins=new double[8]; System.arraycopy(ta,0,bins,0,ta.length);
        return new CeldaAuditoriaDTO(n,t,"C1","7",lat,lon,az,pci,"4T4R",bins);
    }
    private GeometriaSector g(AdaptadorMotorAuditoria m,String t,CeldaAuditoriaDTO... cells) {
        return m.geometria(t,Arrays.asList(cells),Collections.singletonList(cells[0].getNombre())).get(cells[0].getNombre().trim().toUpperCase(Locale.ROOT));
    }
    @Test public void taP80AlcanzaElLimiteYNoSaltaAlBinSiguiente() {
        AdaptadorMotorAuditoria m=motor();
        assertEquals(234,g(m,"4G",c("A","4G",-12,-77,1,0,.8,.2)).getRadioM(),1e-9);
        assertEquals(117,g(m,"5G",c("A","5G",-12,-77,1,0,80,20)).getRadioM(),1e-9);
    }
    @Test public void septimoBinTieneLimiteRealYOverflowEsDesconocido() {
        for(String t:Arrays.asList("4G","5G")) {
            AdaptadorMotorAuditoria m=motor();
            assertEquals(t.equals("4G")?14430:15015,g(m,t,c("A",t,-12,-77,1,0,0,0,0,0,0,0,1,0)).getRadioM(),1e-9);
            GeometriaSector overflow=g(m,t,c("B",t,-12,-77,1,0,0,0,0,0,0,0,0,1));
            assertNull(overflow.getRadioM()); assertFalse(overflow.isSinTa());
            assertEquals("TA_DESBORDAMIENTO_DESCONOCIDO",overflow.getFuenteRadio());
        }
    }
    @Test public void singletonSinTaNoInventaCatorceKilometros() {
        GeometriaSector g=g(motor(),"4G",c("A","4G",-12,-77,1,0));
        assertNull(g.getRadioM()); assertEquals(0,g.getVecinasRespaldo()); assertEquals("SIN_VECINAS",g.getFuenteRadio());
    }
    @Test public void nombrePrototypeNoOcultaGeometriaNiContaminaDiccionarios() {
        GeometriaSector g=g(motor(),"5G",c("__proto__","5G",-12,-77,1,0,1));
        assertNotNull(g);assertEquals(117,g.getRadioM(),1e-9);
    }
    @Test public void respaldoEscasoUsaDistanciaRealYNoCuentaCosite() {
        CeldaAuditoriaDTO a=c("A","5G",0,0,1,0),b=c("B","5G",.01,0,2,0),cosite=c("CO","5G",0,0,3,0);
        GeometriaSector g=g(motor(),"5G",a,b,cosite);
        assertEquals(1111.9493,g.getRadioM(),.01); assertEquals(1,g.getVecinasRespaldo());
        assertEquals("RESPALDO_VECINAS",g.getFuenteRadio());
    }
    @Test public void taInvalidoNoSeTrataComoCoberturaConocida() {
        CeldaAuditoriaDTO a=c("A","4G",0,0,1,0,.4,.4); assertEquals("INVALIDO",a.getEstadoTa());
        GeometriaSector g=g(motor(),"4G",a); assertNull(g.getRadioM()); assertTrue(g.isSinTa());
    }
    @Test public void canalConocidoDiferenteExcluyeHallazgosYVecinas() {
        for(String t:Arrays.asList("4G","5G")) {
            CeldaAuditoriaDTO a=c("A",t,-12,-77,100,0),b=c("B",t,-12.001,-77,100,180);
            a.setCanalRadio("100"); b.setCanalRadio("200");
            assertTrue(motor().auditar(t,Arrays.asList(a,b),10000,8000).getConflictos().isEmpty());
            assertTrue(motor().vecinos(t,Arrays.asList(a,b),"A",30).isEmpty());
            b.setCanalRadio("100"); assertTrue(pair(motor().auditar(t,Arrays.asList(a,b),10000,8000),"PCI_COLLISION","A","B"));
            b.setCarrier("C2"); assertTrue(pair(motor().auditar(t,Arrays.asList(a,b),10000,8000),"PCI_COLLISION","A","B"));
            b.setBanda("OTRA_ETIQUETA"); assertTrue(pair(motor().auditar(t,Arrays.asList(a,b),10000,8000),"PCI_COLLISION","A","B"));
            HallazgoAuditoriaDTO h=new HallazgoAuditoriaDTO("PCI_COLLISION","","",111,null,a,b);
            assertEquals("CANAL_COINCIDENTE",h.getEvidenciaFrecuencia());
            b.setCanalRadio(null); assertEquals("FRECUENCIA_NO_VERIFICADA",h.getEvidenciaFrecuencia());
        }
    }
    @Test public void cosite5gNoSeEximeSoloPorMismoAzimuthOSufijo() {
        CeldaAuditoriaDTO a=c("CAPA_A_C1","5G",-12,-77,100,90,1),b=c("CAPA_B_C1","5G",-12,-77,100,90,1);
        assertTrue(pair(motor().auditar("5G",Arrays.asList(a,b),10000,8000),"PCI_COLLISION",a.getNombre(),b.getNombre()));
        InteraccionVecinaDTO v=motor().vecinos("5G",Arrays.asList(a,b),a.getNombre(),30).get(0);
        assertNull(v.getAreaInterseccionM2()); assertNull(v.getInterseccionNormalizadaM());
    }
    @Test public void paresDentroDelRadioNoDesaparecenDetrasDeSeiscientasVecinas() {
        List<CeldaAuditoriaDTO> cells=new ArrayList<>();
        cells.add(c("A","5G",0,0,100,0,1)); cells.add(c("Z","5G",.08,0,100,180,1));
        // Ambas tienen >600 vecinas más cercanas en la mediatriz, pero A/Z están a 8.90 km.
        for(int i=0;i<605;i++) cells.add(c("M"+i,"5G",.04,(i-302)*.000001,200+(i%50),90,1));
        assertTrue(pair(motor().auditar("5G",cells,10000,2),"PCI_COLLISION","A","Z"));
    }
    @Test public void topeMarcaOmisionesRealesYSeAplicaPorTipoEnAmbasTecnologias() {
        for(String t:Arrays.asList("4G","5G")) {
            List<CeldaAuditoriaDTO> pair=Arrays.asList(c("A",t,0,0,100,0,1),c("B",t,.001,0,100,180,1));
            ResultadoMotor exact=motor().auditar(t,pair,10000,1); assertFalse(exact.isCapped()); assertEquals(2,exact.getConflictos().size());
            List<CeldaAuditoriaDTO> triple=new ArrayList<>(pair);triple.add(c("C",t,.002,0,100,180,1));
            ResultadoMotor cap=motor().auditar(t,triple,10000,1); assertTrue(cap.isCapped());
            assertEquals(new HashSet<>(Arrays.asList(t+":PCI_COLLISION",t+":RSI_COLLISION")),new HashSet<>(cap.getCappedTipos()));
        }
    }
    @Test public void nearestEsEsfericaCruzaAntimeridianoYDesempataPorNombre() {
        List<CeldaAuditoriaDTO> cells=Arrays.asList(c("A","5G",0,179.99,1,0,1),c("FAR","5G",0,179.8,2,0,1),c("Z","5G",0,-179.99,3,0,1),c("B","5G",0,-179.99,4,0,1));
        List<InteraccionVecinaDTO> n=motor().vecinos("5G",cells,"A",2);
        assertEquals("B",n.get(0).getCelda().getNombre());assertEquals("Z",n.get(1).getCelda().getNombre()); assertEquals(2223.9,n.get(0).getDistanciaMetros(),1);
        assertNull(n.get(0).getAreaInterseccionM2());
    }
    @Test public void pssEsModeloDireccionalYNoConfusionConMismoPci() {
        for(String t:Arrays.asList("4G","5G")) {
            CeldaAuditoriaDTO a=c("A",t,0,0,100,0,0,0,0,1),b=c("B",t,.005,0,103,180,0,0,0,1);
            assertTrue(pair(motor().auditar(t,Arrays.asList(a,b),10000,8000),"PCI_MOD3","A","B"));
            b.setAzimuth(0);assertFalse(pair(motor().auditar(t,Arrays.asList(a,b),10000,8000),"PCI_MOD3","A","B"));
            b.setPci(100);assertFalse(pair(motor().auditar(t,Arrays.asList(a,b),10000,8000),"PCI_MOD3","A","B"));
        }
    }
    @Test public void entradaPermutadaMantieneOrdenYContenidoDeterminista() {
        List<CeldaAuditoriaDTO> cells=new ArrayList<>(Arrays.asList(c("Z","5G",0,0,100,0,1),c("B","5G",.001,0,100,180,1),c("A","5G",.002,0,100,180,1)));
        AdaptadorMotorAuditoria m=motor(); ResultadoMotor primero=m.auditar("5G",cells,10000,2);
        Collections.reverse(cells);ResultadoMotor segundo=m.auditar("5G",cells,10000,2);
        assertEquals(primero.getConflictos().size(),segundo.getConflictos().size());
        for(int i=0;i<primero.getConflictos().size();i++) {
            ConflictoMotor a=primero.getConflictos().get(i),b=segundo.getConflictos().get(i);
            assertEquals(a.getTipo(),b.getTipo());assertEquals(a.getNombreA(),b.getNombreA());assertEquals(a.getNombreB(),b.getNombreB());assertEquals(a.getDistanciaMetros(),b.getDistanciaMetros(),0);
        }
    }
    private boolean pair(ResultadoMotor r,String type,String a,String b) { return r.getConflictos().stream().anyMatch(x -> x.getTipo().equals(type) && ((x.getNombreA().equals(a)&&x.getNombreB().equals(b))||(x.getNombreA().equals(b)&&x.getNombreB().equals(a)))); }
}
