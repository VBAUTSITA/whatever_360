package com.tracker.web.auditoriared;

import java.io.IOException;
import java.util.*;
import javax.servlet.*;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import com.tracker.web.auditoriared.dashboard.*;

/** TEST CLASSPATH ONLY: reproducible HTTP/browser fixture, no production mode or data. */
@SpringBootConfiguration
@EnableAutoConfiguration(exclude=DataSourceAutoConfiguration.class)
@Import({DashboardAuditoriaController.class,ErroresDashboard.class,AuditoriaRedController.class,
         DashboardAuditoriaService.class,CatalogoFiltrosDashboard.class,FiltrosDashboardConfig.class})
public class DashboardBrowserFixture {
    public static void main(String[] args) {
        SpringApplication app=new SpringApplication(DashboardBrowserFixture.class);
        Map<String,Object> props=new HashMap<>(); props.put("server.address","127.0.0.1"); props.put("server.port",3298);
        props.put("server.servlet.context-path","/tracker"); props.put("spring.thymeleaf.cache",false);
        app.setDefaultProperties(props); app.run(args);
    }
    @Bean public FilterRegistrationBean<Filter> fixtureIdentity() {
        Filter f=new Filter() {
            @Override public void init(FilterConfig config) { }
            @Override public void destroy() { }
            @Override public void doFilter(ServletRequest req,ServletResponse res,FilterChain chain) throws IOException,ServletException {
                SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("browser-fixture","",AuthorityUtils.createAuthorityList("ROLE_USER")));
                try { chain.doFilter(req,res); } finally { SecurityContextHolder.clearContext(); }
            }
        };
        return new FilterRegistrationBean<>(f);
    }
    @Bean public CatalogoChequeos chequeos() { return AuditoriaRedServiceTest.catalogo(); }
    @Bean public AuditoriaRedRepository repositorio() {
        return new AuditoriaRedRepository(new JdbcTemplate()) {
            @Override public Lectura leer(String tec,CajaGeografica caja) {
                List<CeldaAuditoriaDTO> cells=new ArrayList<>();
                for(int i=0;i<32;i++) {
                    String name=i==0?"FIXTURE_A":(i==1?"<img src=x onerror=alert(1)>":"FIXTURE_"+i);
                    double lat=-12.09+(i/8)*0.003,lon=-77.05+(i%8)*0.003;
                    cells.add(new CeldaAuditoriaDTO(name,tec,"C1","5G".equals(tec)?"N78":"7",lat,lon,(i%2)*180,100,"4T4R",i%5==0?new double[8]:new double[]{0,0,0,1,0,0,0,0}));
                }
                cells.add(new CeldaAuditoriaDTO("FIXTURE_MARGIN",tec,"C1","5G".equals(tec)?"N78":"7",-12.15,-77.05,0,100,"4T4R",new double[8]));
                return new Lectura(cells,cells.size(),0);
            }
            @Override public List<SitioDTO> buscarSitios(String q,int limit) { return Arrays.asList(new SitioDTO("FIXTURE_LIMA",-12.09,-77.05,"5G")); }
        };
    }
    @Bean public MotorAuditoria motor() { return new AdaptadorMotorAuditoria("scripts/auditoriared/planner_4g_2_MIRROR_FIXED.js","scripts/auditoriared/old_planner_5g.js"); }
    @Bean public AuditoriaRedService auditoria(AuditoriaRedRepository r,MotorAuditoria m,CatalogoChequeos c) { return new AuditoriaRedService(r,m,c,10000,20,8000,8000,15); }
    @Bean public LectorSitioPlanificado lector() { return new LectorSitioPlanificado(new JdbcTemplate(),new AccesoJefaturaAuditoria()); }
}
