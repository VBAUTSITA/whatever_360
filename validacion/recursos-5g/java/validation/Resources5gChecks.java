package validation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracker.web.recursos.controlador.RecursosController;
import com.tracker.web.recursos.model.*;
import com.tracker.web.recursos.repository.RecursosRepository;
import com.tracker.web.recursos.planner.*;
import com.tracker.web.recursos.planner.dto.*;
import java.io.*;
import java.lang.reflect.*;
import java.nio.file.*;
import java.util.*;
import org.springframework.test.util.ReflectionTestUtils;
import static org.mockito.Mockito.*;
import org.mockito.ArgumentMatchers;

/** Characterization and independent business checks against UNMODIFIED active source.
 * Real Java8/Nashorn; substituted data/repository for isolated checks, explicitly not Oracle.
 * FAIL means the requested business property was not satisfied, never an accepted golden.
 */
public final class Resources5gChecks {
    static final ObjectMapper JSON = new ObjectMapper();
    static final List<Map<String,Object>> checks = new ArrayList<>();
    static PlannerEngine engine;
    static Path root;
    static String mode="baseline";
    static class Blocked extends RuntimeException {Blocked(String text){super(text);}}
    interface Case { void run() throws Exception; }
    static void check(String id, String expected, Case c) {
        Map<String,Object> row = new LinkedHashMap<>(); row.put("id",id);row.put("expected",expected);
        try { c.run(); row.put("status","PASS"); }
        catch (Blocked e) {row.put("status","BLOCKED");row.put("observed",e.toString());}
        catch (Throwable e) {row.put("status","FAIL");row.put("observed",e.toString());}
        checks.add(row); System.out.println(row.get("status")+" "+id+" "+row.getOrDefault("observed",""));
    }
    static void require(boolean ok, String msg) { if(!ok)throw new AssertionError(msg); }
    public static void main(String[] args) throws Exception {
        root=Paths.get(args[0]);if(args.length>1)mode=args[1];engine=new PlannerEngine();engine.init();
        List<CeldaExistenteDTO5G> network=ring();
        check("J01_5G_ROUTING_AND_OUTPUT", "5G uses only the NR source and returns valid outdoor PCI/RSI",()->{
            Fixture f=new Fixture(network);Recursos p=proposal(1,"LIL99991C3_TEST");f.rows.add(p);
            ResultadoAutoAsignacion r=f.run();require(r.getAutoComputadosIds().equals(Arrays.asList(1L)),"computed IDs="+r.getAutoComputadosIds()+" omitted="+r.getOmitidos());
            require(p.getPciPsc()!=null&&p.getPciPsc()>=0&&p.getPciPsc()<=985,"PCI="+p.getPciPsc());
            require(p.getRsi()==(2*p.getPciPsc())%840,"RSI table mismatch");
            verify(f.red4,never()).obtenerCeldasExistentes(ArgumentMatchers.anyList());
            verify(f.repo,never()).save(ArgumentMatchers.any(Recursos.class));
            verify(f.repo,never()).saveAll(ArgumentMatchers.anyIterable());
        });
        check("J02_COMPLETE_MANUAL_PRESERVED","Complete manual PCI and RSI remain unchanged",()->{
            Fixture f=new Fixture(network);Recursos p=proposal(2,"LIL99992C3_TEST");p.setPciPsc(777);p.setRsi(714);f.rows.add(p);f.run();
            require(p.getPciPsc()==777&&p.getRsi()==714,"manual values changed");
            verify(f.red5,never()).obtenerCeldasExistentes(ArgumentMatchers.anyList());
        });
        check("J03_PARTIAL_PCI_PRESERVED","A pre-existing PCI is not silently replaced when RSI is missing",()->{
            Fixture f=new Fixture(network);Recursos p=proposal(3,"LIL99993C3_TEST");p.setPciPsc(777);f.rows.add(p);ResultadoAutoAsignacion result=f.run();if(result.getAutoComputadosIds().isEmpty())throw new Blocked("no assignment; downstream preservation not exercised: "+result.getOmitidos());
            require(p.getPciPsc()==777,"stored PCI777 became "+p.getPciPsc()+"; RSI="+p.getRsi());
        });
        check("J04_PARTIAL_RSI_PRESERVED","A pre-existing RSI is not silently replaced when PCI is missing",()->{
            Fixture f=new Fixture(network);Recursos p=proposal(4,"LIL99994C3_TEST");p.setRsi(838);f.rows.add(p);ResultadoAutoAsignacion result=f.run();if(result.getAutoComputadosIds().isEmpty())throw new Blocked("no assignment; downstream preservation not exercised: "+result.getOmitidos());
            require(p.getRsi()==838,"stored RSI838 became "+p.getRsi()+"; PCI="+p.getPciPsc());
        });
        check("J05_MISSING_FIELD_VISIBLE","Missing carrier produces a visible omission and no fabricated result",()->{
            Fixture f=new Fixture(network);Recursos p=proposal(5,"LIL99995C3_TEST");p.setCarrier(null);f.rows.add(p);ResultadoAutoAsignacion r=f.run();
            require(p.getPciPsc()==null&&r.getOmitidos().toString().contains("carrier"),r.getOmitidos().toString());
        });
        check("J06_UNKNOWN_TECH_VISIBLE","Unknown technology produces a visible omission",()->{
            Fixture f=new Fixture(network);Recursos p=proposal(6,"LIL99996C3_TEST");p.setTecnologia("4G/5G");f.rows.add(p);ResultadoAutoAsignacion r=f.run();
            require(p.getPciPsc()==null&&r.getOmitidos().toString().contains("no soportada"),r.getOmitidos().toString());
        });
        check("J07_DUPLICATE_VISIBLE","Duplicate names must be reported instead of mapped silently by array index",()->{
            Fixture f=new Fixture(network);f.rows.add(proposal(7,"LIL99997C3_TEST"));f.rows.add(proposal(8,"LIL99997C3_TEST"));ResultadoAutoAsignacion r=f.run();
            require(r.getAutoComputadosIds().size()==1&&r.getOmitidos().toString().contains("duplicado"),r.getOmitidos().toString());
        });
        check("J08_EMPTY_NETWORK_VISIBLE","No usable network => visible error and no invented PCI",()->{
            Fixture f=new Fixture(Collections.emptyList());Recursos p=proposal(9,"LIL99999C3_TEST");f.rows.add(p);ResultadoAutoAsignacion r=f.run();
            require(p.getPciPsc()==null&&!r.getOmitidos().isEmpty(),"PCI="+p.getPciPsc());
        });
        check("J09_INDOOR_RANGE","Indoor proposal respects configured PCI986..1007",()->{
            Fixture f=new Fixture(network);Recursos p=proposal(10,"LIL99991C3_INDOOR");p.setIndoor("S");f.rows.add(p);f.run();
            require(p.getPciPsc()!=null&&p.getPciPsc()>=986&&p.getPciPsc()<=1007,"PCI="+p.getPciPsc());
        });
        check("J10_NUMERIC_RANGE_VALIDATION","Out-of-range/nonfinite coordinates and azimuth must be rejected before planning",()->{
            Fixture f=new Fixture(network);Recursos p=proposal(11,"LIL99991C3_BADCOORD");p.setLatitud(Double.NaN);p.setAzimuth(999.0);f.rows.add(p);ResultadoAutoAsignacion r=f.run();
            require(p.getPciPsc()==null&&r.getOmitidos().toString().toLowerCase().matches(".*(latitud|azimuth|coordenad|inv[aá]lid).*"),"omitted="+r.getOmitidos()+" PCI="+p.getPciPsc());
        });
        check("J11_RESERVATIONS_INCLUDED","Existing saved reservations must be consulted as planning context",()->{
            Fixture f=new Fixture(network);f.rows.add(proposal(12,"LIL99991C3_RESERVED"));f.run();
            boolean queried=mockingDetails(f.repo).getInvocations().stream().anyMatch(i-> !i.getMethod().getName().equals("findAllById"));
            require(queried,"repository consulted only selected IDs; network source SQL contains no reservations");
        });
        check("J12_TA_NEGATIVE_REJECTED","TA containing negatives must not be treated as a valid fraction",()->{
            double[] v=normalize(new double[]{-0.1,1.1,0,0,0,0,0,0});require(Arrays.stream(v).sum()==0,"accepted "+Arrays.toString(v));
        });
        check("J13_TA_BAD_SUM_REJECTED","TA sum60% must not be treated as a complete distribution",()->{
            double[] v=normalize(new double[]{30,30,0,0,0,0,0,0});require(Arrays.stream(v).sum()==0,"accepted "+Arrays.toString(v));
        });
        check("J14_TA_VALID_PERCENT","Complete40/60 percent TA becomes fractions .4/.6",()->{
            double[] v=normalize(new double[]{40,60,0,0,0,0,0,0});require(Math.abs(v[0]-.4)<1e-12&&Math.abs(v[1]-.6)<1e-12,Arrays.toString(v));
        });
        check("J15_SAVE_PRESERVES_IDENTITY","Saving proposed PCI/RSI must preserve server-side cell identity",()->{
            RecursosRepository repo=mock(RecursosRepository.class);Recursos db=proposal(51,"ORIGINAL");when(repo.findById(51L)).thenReturn(Optional.of(db));
            Recursos sent=proposal(51,"FORGED");sent.setPciPsc(700);sent.setRsi(560);RecursosForm form=new RecursosForm();form.setRecursos(Arrays.asList(sent));
            RecursosController controller=new RecursosController();ReflectionTestUtils.setField(controller,"recursosRepository",repo);controller.assignResources(form);
            require("ORIGINAL".equals(db.getCellName())&&db.getPciPsc()==700&&db.getRsi()==560,"wrong mapping");verify(repo).save(db);
        });
        check("J16_SAVE_REJECTS_INVALID_RESOURCE","5G PCI1008 and negative RSI must not be saved",()->{
            RecursosRepository repo=mock(RecursosRepository.class);Recursos db=proposal(52,"INVALID_SAVE");when(repo.findById(52L)).thenReturn(Optional.of(db));
            Recursos sent=proposal(52,"INVALID_SAVE");sent.setPciPsc(1008);sent.setRsi(-1);RecursosForm form=new RecursosForm();form.setRecursos(Arrays.asList(sent));
            RecursosController controller=new RecursosController();ReflectionTestUtils.setField(controller,"recursosRepository",repo);controller.assignResources(form);
            require(db.getPciPsc()==null&&db.getRsi()==null,"controller accepted PCI="+db.getPciPsc()+" RSI="+db.getRsi());
        });
        if(Files.exists(root.resolve("datos/red5g-host.json"))&&Files.exists(root.resolve("datos/plan5g-host.json"))) realBatch();
        Map<String,Object> result=new LinkedHashMap<>();result.put("mode",mode);result.put("source","Current Recursos source, compiled separately; actual Java8/Nashorn, substituted repository/network data; NOT Oracle");result.put("checks",checks);
        result.put("pass",checks.stream().filter(x->"PASS".equals(x.get("status"))).count());result.put("fail",checks.stream().filter(x->"FAIL".equals(x.get("status"))).count());
        result.put("blocked",checks.stream().filter(x->"BLOCKED".equals(x.get("status"))).count());
        JSON.writerWithDefaultPrettyPrinter().writeValue(root.resolve("resultados/java-service-checks-"+mode+".json").toFile(),result);
        System.out.println("RESULT pass="+result.get("pass")+" fail="+result.get("fail"));
        if(((Number)result.get("fail")).longValue()>0||((Number)result.get("blocked")).longValue()>0)System.exit(1);
    }
    static double[] normalize(double[] values)throws Exception{Method m=RedExistenteServiceImpl5G.class.getDeclaredMethod("normalizarTA",double[].class);m.setAccessible(true);return(double[])m.invoke(null,(Object)values);}
    public static Recursos proposal(long id,String name){Recursos p=new Recursos();p.setId(id);p.setCellName(name);p.setSiteName("VALIDACION_5G");p.setTecnologia("5G");p.setCarrier("C3");p.setFreqband("N78");p.setTxrxmode("32T32R");p.setLatitud(-12.05);p.setLongitud(-77.04);p.setAzimuth(40.0);p.setIndoor("N");p.setJefatura("ZONA_PRUEBA");p.setUsuarioIngreso("validation");return p;}
    static List<CeldaExistenteDTO5G> ring(){List<CeldaExistenteDTO5G> out=new ArrayList<>();for(int i=0;i<36;i++){CeldaExistenteDTO5G c=new CeldaExistenteDTO5G();c.setCellname("LIL"+(1000+i)+"1C3_RING");c.setCarrier("C3");c.setFreqband("N78");c.setPhycellid((i*37)%986);c.setTxrxmode("32T32R");double a=i*Math.PI/18;c.setLatitud(-12.05+.02*Math.cos(a));c.setLongitud(-77.04+.02*Math.sin(a));c.setAzimuth((i*30.0)%360);c.setTa0117(.4);c.setTa117507(.6);out.add(c);}return out;}
    public static class Fixture{
        public List<Recursos> rows=new ArrayList<>();public RecursosRepository repo=mock(RecursosRepository.class);public RedExistenteService red4=mock(RedExistenteService.class);public RedExistenteService5G red5=mock(RedExistenteService5G.class);public PciRsiAsignacionServiceImpl service=new PciRsiAsignacionServiceImpl();
        Fixture(List<CeldaExistenteDTO5G> red){when(repo.findAllById(ArgumentMatchers.anyIterable())).thenAnswer(x->rows);when(red5.obtenerCeldasExistentes(ArgumentMatchers.anyList())).thenReturn(new ResultadoRedExistente5G(red,red.size(),red.size()));ReflectionTestUtils.setField(service,"recursosRepository",repo);ReflectionTestUtils.setField(service,"redExistenteService",red4);ReflectionTestUtils.setField(service,"redExistenteService5G",red5);ReflectionTestUtils.setField(service,"plannerEngine",engine);}
        ResultadoAutoAsignacion run(){List<Long> ids=new ArrayList<>();for(Recursos p:rows)ids.add(p.getId());return service.autoAsignar(ids);}
    }
    static void realBatch()throws Exception{
        Path redPath=root.resolve("datos/red5g-cemento-host.json");if(!Files.exists(redPath))throw new IllegalStateException("Prepare150kmCementoContext first");
        List<CeldaExistenteDTO5G> red=JSON.readValue(redPath.toFile(),new TypeReference<List<CeldaExistenteDTO5G>>(){});
        List<CeldaPlanDTO> input=JSON.readValue(root.resolve("datos/plan5g-host.json").toFile(),new TypeReference<List<CeldaPlanDTO>>(){});
        Fixture f=new Fixture(red);int index=0;for(CeldaPlanDTO d:input){Recursos p=proposal(1000+index++,d.getCellname());p.setCarrier(d.getCarrier());p.setFreqband(d.getFreqband());p.setTxrxmode(d.getTxrxmode());p.setLatitud(d.getLatitud());p.setLongitud(d.getLongitud());p.setAzimuth(d.getAzimuth());p.setIndoor(Boolean.TRUE.equals(d.getIndoor())?"S":"N");f.rows.add(p);}
        long start=System.nanoTime();ResultadoAutoAsignacion result=f.run();Map<String,Object> output=new LinkedHashMap<>();output.put("inputNetworkRows",red.size());output.put("inputProposals",input.size());output.put("computed",result.getAutoComputadosIds().size());output.put("seconds",(System.nanoTime()-start)/1e9);output.put("omitted",result.getOmitidos());output.put("proposals",result.getRecursos());
        output.put("mode",mode);output.put("context",redPath.toString());
        JSON.writerWithDefaultPrettyPrinter().writeValue(root.resolve("resultados/real-data-batch-"+mode+".json").toFile(),output);
        check("J17_REAL_BATCH_ACCOUNTING","Each supplied proposal must be computed or explicitly omitted",()->require(result.getAutoComputadosIds().size()+result.getOmitidos().size()==input.size(),"counts do not reconcile"));
        check("J18_REAL_BATCH_RANGE","Every computed resource has valid5G PCI and table RSI",()->{if(result.getAutoComputadosIds().isEmpty())throw new Blocked("no computed outputs to validate");for(Recursos p:result.getRecursos())if(result.getAutoComputadosIds().contains(p.getId()))require(p.getPciPsc()!=null&&p.getPciPsc()>=0&&p.getPciPsc()<=1007&&p.getRsi()==(2*p.getPciPsc())%840,"bad output for "+p.getCellName());});
        check("J19_REAL_BATCH_ALL_ASSIGNED","The3completeCemento5Gproposals must all receive resources",()->require(result.getAutoComputadosIds().size()==input.size(),"computed="+result.getAutoComputadosIds().size()+"/"+input.size()+"; omitted="+result.getOmitidos()));
    }
}
