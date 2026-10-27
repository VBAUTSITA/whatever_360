package com.tracker.web.auditoriared;

import java.sql.*;
import java.util.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.util.ReflectionTestUtils;
import com.tracker.web.auditoriared.dashboard.*;

/** Assertions are added here without substituting the JDBC repository or Nashorn engines. */
public final class OracleIntegrationVerification {
    private static int checks;
    static void verify(Connection c) throws Exception {
        checks = 0;
        OracleIntegrationHarness.validateSchema(c);
        for (String preserved : Arrays.asList("ORACLE_TEST_TABLE", "TM_CONF_CATUAL_3G_AUX")) {
            try { OracleIntegrationHarness.requireOwned(c,preserved); throw new AssertionError("Unmarked original accepted: "+preserved); }
            catch (IllegalStateException expected) { check(expected.getMessage().startsWith("COLISION:"),"ownership guard refuses original "+preserved); }
        }
        JdbcTemplate jdbc = new JdbcTemplate(OracleIntegrationHarness.dataSource(c));
        equal(17, jdbc.queryForObject("SELECT COUNT(*) FROM TM_CONF_ACTUAL_CELDA_4G", Integer.class), "4G seed count");
        equal(48, jdbc.queryForObject("SELECT COUNT(*) FROM TM_CONF_ACTUAL_CELDA_5G", Integer.class), "5G seed count");
        equal(12, jdbc.queryForObject("SELECT COUNT(*) FROM PLAN_MAP_SECT_CRIT_4GWEEK", Integer.class), "4G TA seed count");
        equal(44, jdbc.queryForObject("SELECT COUNT(*) FROM AUDRED_TA5G_AUX", Integer.class), "5G TA seed count");
        equal(2, jdbc.queryForObject("SELECT COUNT(*) FROM RECURSOS_FORMULARIO", Integer.class), "planned seed count");
        AuditoriaRedRepository repository = new AuditoriaRedRepository(jdbc);
        CajaGeografica box = new ClusterAlcance(-12.09,-77.05,1,10.1).cajaConMargen();
        AuditoriaRedRepository.Lectura l4 = repository.leer("4G", box), l5 = repository.leer("5G", box);
        equal(16, l4.getFilasLeidas(), "Oracle 4G LEFT JOIN and malformed coordinate guard");
        equal(48, l5.getFilasLeidas(), "Oracle case insensitive 5G LEFT JOIN retains duplicate TA rows");
        equal(1, l4.getDescartadasSinDatos(), "4G fractional PCI rejected");
        equal(1, l5.getDescartadasSinDatos(), "5G fractional PCI rejected");
        for (AuditoriaRedRepository.Lectura read : Arrays.asList(l4,l5)) {
            String tech = read == l4 ? "4G" : "5G";
            CeldaAuditoriaDTO a = cell(read.getCeldas(),"AUDRED_"+tech+"_A");
            equal(tech.equals("4G")?"VALIDO_PORCENTAJE":"VALIDO_FRACCION",a.getEstadoTa(),tech+" TA units");
            near(.6,a.getTa()[0],1e-12,tech+" TA normalization");
            equal("AUSENTE",cell(read.getCeldas(),"AUDRED_"+tech+"_MISSING").getEstadoTa(),tech+" missing TA");
            equal("INVALIDO",cell(read.getCeldas(),"AUDRED_"+tech+"_INVALID").getEstadoTa(),tech+" malformed/negative TA");
            near(-12.091,cell(read.getCeldas(),"AUDRED_"+tech+"_DECIMAL").getLatitud(),1e-12,tech+" Oracle comma conversion");
            check(a.getCanalRadio()==null,tech+" channel evidence remains unknown");
            check(read.getCeldas().stream().noneMatch(x->x.getNombre().endsWith("_DIRTY")),tech+" bad coordinates excluded");
        }
        equal(2L,l5.getCeldas().stream().filter(x->x.getNombre().equals("AUDRED_5G_TA_CONFLICT")).count(),"conflicting joined TA preserved for service exclusion");
        check(!repository.buscarSitios("AUDRED",20).isEmpty(),"Oracle literal site search");
        equal(0,repository.buscarSitios("%",20).size(),"percent is literal, not SQL wildcard");
        check(repository.leer("5G",new ClusterAlcance(0,0,1,1).cajaConMargen()).getCeldas().isEmpty(),"Oracle empty area");
        AuditoriaRedRepository limited=new AuditoriaRedRepository(jdbc);ReflectionTestUtils.setField(limited,"maxFilasLeidas",2);
        try { limited.leer("5G",box);throw new AssertionError("Oracle row cap not enforced"); }
        catch(IllegalArgumentException expected){checks++;System.out.println("PASS Oracle row cap fails explicitly");}

        AdaptadorMotorAuditoria motor = new AdaptadorMotorAuditoria("scripts/auditoriared/planner_4g_2_MIRROR_FIXED.js","scripts/auditoriared/old_planner_5g.js");
        CatalogoChequeos catalog = new CatalogoChequeos(Arrays.asList(new ChequeoColisionPci(),new ChequeoColisionRsi(),new ChequeoConfusionMod3()));
        AuditoriaRedService service = new AuditoriaRedService(repository,motor,catalog,10000,20,8000,8000,0);
        SolicitudAuditoria request = request();
        ResultadoAuditoria result = service.auditar(request);
        check(!result.getHallazgos().isEmpty(),"real Oracle repository/service/Nashorn findings");
        for(String tech:Arrays.asList("4G","5G")) {
            String prefix="AUDRED_"+tech+"_";
            check(pair(result,"PCI_COLLISION",prefix+"A",prefix+"B"),tech+" same PCI candidate inside reuse radius");
            check(pair(result,"PCI_COLLISION",prefix+"A",prefix+"BOUNDARY"),tech+" outside area neighbor retained by margin");
            check(result.getHallazgos().stream().noneMatch(h->h.getCeldaA().getNombre().equals(prefix+"OTHER_CARRIER")||h.getCeldaB().getNombre().equals(prefix+"OTHER_CARRIER")),tech+" incompatible carrier excluded");
            check(result.getHallazgos().stream().noneMatch(h->h.getCeldaA().getNombre().equals(prefix+"OTHER_BAND")||h.getCeldaB().getNombre().equals(prefix+"OTHER_BAND")),tech+" incompatible band excluded");
            check(result.getHallazgos().stream().noneMatch(h->h.getCeldaA().getNombre().equals(h.getCeldaB().getNombre())),tech+" no self collision from source duplicates");
            check(result.getContexto().stream().noneMatch(x->x.getNombre().equals(prefix+"CONFLICT")),tech+" conflicting official identity excluded");
            equal(1L,result.getContexto().stream().filter(x->x.getNombre().equals(prefix+"A")).count(),tech+" exact duplicates collapse");
            check(result.getResumen().stream().filter(x->x.getTecnologia().equals(tech)).allMatch(x->x.getMotor().contains("(")),tech+" engine fingerprint");
            CeldaAuditoriaDTO sparse=cell(result.getContexto(),prefix+"SPARSE"), overflow=cell(result.getContexto(),prefix+"OVERFLOW");
            check(sparse.getRadioM()==null,tech+" isolated missing TA has no invented radius");
            check(sparse.getFuenteRadio()!=null,tech+" isolated fallback limitation has provenance");
            check(overflow.getRadioM()==null,tech+" open terminal TA bin has no finite invented radius");
            check(overflow.getFuenteRadio()!=null,tech+" open terminal bin limitation has provenance");
        }
        check(result.getContexto().stream().noneMatch(x->x.getNombre().equals("AUDRED_5G_TA_CONFLICT")),"ambiguous joined TA identity excluded");
        check(!cell(result.getContexto(),"AUDRED_4G_BOUNDARY").isDentroArea(),"margin neighbor is explicitly outside area");
        check(!result.isDesdeCache()&&!service.auditar(request).isDesdeCache(),"disabled cache stays fresh against Oracle");
        List<InteraccionVecinaDTO> neighbors=service.vecinos(result,"5G","AUDRED_5G_A",200);
        check(neighbors.size()>30,"full loaded dense neighbor context");
        for(InteraccionVecinaDTO n:neighbors) if(n.getInterseccionNormalizadaM()!=null && n.getDistanciaMetros()>1) {
            near(n.getAreaInterseccionM2()/n.getDistanciaMetros(),n.getInterseccionNormalizadaM(),1e-6,"m2/m geometric units"); break;
        }
        for(InteraccionVecinaDTO n:neighbors) if(n.getDistanciaMetros()<1) check(n.getInterseccionNormalizadaM()==null,"cosite index unavailable");
        check(new AuditoriaRedService(repository,motor,catalog,10000,20,8000,1,0).auditar(request).isCapped(),"real engine cap marked incomplete");
        SolicitudAuditoria empty=request();empty.setLatitud(0d);empty.setLongitud(0d);
        check(service.auditar(empty).getHallazgos().isEmpty(),"no findings in empty area");
        verifyControllerAndPlanning(jdbc,service);
        OracleIntegrationHarness.reportQuery(c,"SAMPLE_4G_JOIN","SELECT r.CELLNAME,r.PHYCELLID,m.TA_0_234_MTS FROM TM_CONF_ACTUAL_CELDA_4G r LEFT JOIN PLAN_MAP_SECT_CRIT_4GWEEK m ON r.SECTOR_NAME=m.SECTOR_NAME WHERE r.CELLNAME IN ('AUDRED_4G_A','AUDRED_4G_MISSING','AUDRED_4G_INVALID') ORDER BY r.CELLNAME");
        OracleIntegrationHarness.reportQuery(c,"SAMPLE_5G_JOIN","SELECT r.CELLNAME,r.PHYSICALCELLID,m.TA_0_117_MTS_PORC FROM TM_CONF_ACTUAL_CELDA_5G r LEFT JOIN VW_PLANI_RECURSOS_5G_TA_DIARIO m ON UPPER(r.CELLNAME)=UPPER(m.CELLNAME) WHERE r.CELLNAME IN ('AUDRED_5G_A','AUDRED_5G_MISSING','AUDRED_5G_TA_CONFLICT') ORDER BY r.CELLNAME,m.TA_0_117_MTS_PORC");
        System.out.println("ORACLE_VERIFICATION_PASS checks="+checks+" findings="+result.getHallazgos().size()+" context="+result.getContexto().size());
    }

    static void verifyControllerAndPlanning(JdbcTemplate jdbc,AuditoriaRedService service) {
        FiltrosDashboardConfig f=new FiltrosDashboardConfig();
        DashboardAuditoriaController controller=new DashboardAuditoriaController(new DashboardAuditoriaService(service,new CatalogoFiltrosDashboard(Arrays.asList(f.filtroTipoAuditoria(),f.filtroTecnologiaAuditoria(),f.filtroCarrierAuditoria(),f.filtroBandaAuditoria(),f.filtroCeldaAuditoria(),f.filtroDistanciaAuditoria(),f.filtroCalidadAuditoria()))));
        SolicitudDashboard s=new SolicitudDashboard();s.setLatitud(-12.09);s.setLongitud(-77.05);s.setRadioKm(1d);s.setTecnologias(Arrays.asList("4G","5G"));s.setCeldaObjetivo("AUDRED_5G_A");s.setTecnologiaObjetivo("5G");
        try {
            SecurityContextHolder.clearContext();
            try {controller.consultar(s);throw new AssertionError("Anonymous controller accepted");}catch(AccessDeniedException expected){checks++;}
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("oracle-fixture","",AuthorityUtils.createAuthorityList("ROLE_USER")));
            Map<String,Object> full=controller.consultar(s);
            check(((Number)full.get("totalHallazgos")).intValue()>25,"real controller dense findings and pagination");
            equal(25,((List<?>)full.get("hallazgos")).size(),"controller page limit");
            s.setFiltros(Collections.singletonMap("celda",Collections.singletonList("NO_MATCH")));
            Map<String,Object> filtered=controller.consultar(s);
            equal(0,((Number)filtered.get("totalFiltrados")).intValue(),"presentation filter hides all findings");
            equal(((List<?>)full.get("vecinos")).size(),((List<?>)filtered.get("vecinos")).size(),"presentation filter preserves neighbor context");
            AccesoJefaturaAuditoria access=new AccesoJefaturaAuditoria();ReflectionTestUtils.setField(access,"jefaturasRaw","oracle-fixture:ZONA PRUEBA");access.init();
            LectorSitioPlanificado reader=new LectorSitioPlanificado(jdbc,access);
            List<CeldaAuditoriaDTO> planned=reader.leer(Collections.singletonList(910001L),Collections.singletonList(500),new ArrayList<>());
            equal("AUDRED_PLAN_5G",planned.get(0).getNombre(),"real planned Oracle row authorized");
            check(service.auditarSitio(planned,new ArrayList<>()).getHallazgos().stream().anyMatch(h->h.getCeldaA().isPlanificada()||h.getCeldaB().isPlanificada()),"planned PCI flows through real audit");
            try {reader.leer(Collections.singletonList(910002L),Collections.singletonList(500),new ArrayList<>());throw new AssertionError("Foreign jefatura accepted");}catch(AccessDeniedException expected){checks++;}
        } finally {SecurityContextHolder.clearContext();}
    }
    static SolicitudAuditoria request(){SolicitudAuditoria s=new SolicitudAuditoria();s.setLatitud(-12.09);s.setLongitud(-77.05);s.setRadioKm(1d);s.setTecnologias(Arrays.asList("4G","5G"));return s;}
    static CeldaAuditoriaDTO cell(List<CeldaAuditoriaDTO> cells,String name){return cells.stream().filter(x->x.getNombre().equals(name)).findFirst().orElseThrow(()->new AssertionError("Missing "+name));}
    static boolean pair(ResultadoAuditoria result,String type,String a,String b){return result.getHallazgos().stream().anyMatch(h->h.getTipo().equals(type)&&((h.getCeldaA().getNombre().equals(a)&&h.getCeldaB().getNombre().equals(b))||(h.getCeldaA().getNombre().equals(b)&&h.getCeldaB().getNombre().equals(a))));}
    static void check(boolean v,String label){if(!v)throw new AssertionError(label);checks++;System.out.println("PASS "+label);}
    static void equal(Object a,Object b,String label){check(Objects.equals(a,b),label+" expected="+a+" actual="+b);}
    static void near(double a,double b,double tolerance,String label){check(Math.abs(a-b)<=tolerance,label+" expected="+a+" actual="+b);}
}
