package com.tracker.web.auditoriared;

import java.io.IOException;
import java.util.*;
import javax.servlet.*;
import javax.sql.DataSource;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.*;
import com.tracker.web.auditoriared.dashboard.*;

/** TEST CLASSPATH ONLY. Real repository + Oracle, with a localhost-only test identity and real CSRF. */
@Configuration
@EnableAutoConfiguration(exclude=DataSourceAutoConfiguration.class)
@Import({DashboardAuditoriaController.class, ErroresDashboard.class, AuditoriaRedController.class,
        DashboardAuditoriaService.class, CatalogoFiltrosDashboard.class, FiltrosDashboardConfig.class,
        AuditoriaRedRepository.class, AuditoriaRedService.class, AdaptadorMotorAuditoria.class,
        LectorSitioPlanificado.class, AccesoJefaturaAuditoria.class, CatalogoChequeos.class,
        ChequeoColisionPci.class, ChequeoColisionRsi.class, ChequeoConfusionMod3.class})
public class OracleBrowserFixture {
    private static DataSource localSource;
    public static void start(DataSource source) {
        localSource = source;
        SpringApplication app = new SpringApplication(OracleBrowserFixture.class);
        Map<String,Object> p = new HashMap<>();
        p.put("server.address", "127.0.0.1"); p.put("server.port", "3299");
        p.put("server.servlet.context-path", "/tracker"); p.put("spring.thymeleaf.cache", "false");
        p.put("auditoriared.cache-minutos", "0"); p.put("app.jefaturas", "oracle-fixture:ZONA PRUEBA");
        String hostStatic = System.getProperty("auditoriared.host-static");
        if (hostStatic != null) p.put("spring.resources.static-locations", "classpath:/static/," + hostStatic);
        app.setDefaultProperties(p); app.run();
    }
    @Bean public JdbcTemplate jdbcTemplate() { return new JdbcTemplate(localSource); }
    @Bean public FilterRegistrationBean<Filter> localIdentity() {
        Filter f = new Filter() {
            @Override public void init(FilterConfig c) { }
            @Override public void destroy() { }
            @Override public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
                SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                        "oracle-fixture", "", AuthorityUtils.createAuthorityList("ROLE_USER")));
                try { chain.doFilter(request, response); } finally { SecurityContextHolder.clearContext(); }
            }
        };
        FilterRegistrationBean<Filter> bean = new FilterRegistrationBean<>(f); bean.setOrder(0); return bean;
    }
    @Bean public FilterRegistrationBean<CsrfFilter> localCsrf() {
        FilterRegistrationBean<CsrfFilter> bean = new FilterRegistrationBean<>(new CsrfFilter(new HttpSessionCsrfTokenRepository()));
        bean.setOrder(1); return bean;
    }
}
