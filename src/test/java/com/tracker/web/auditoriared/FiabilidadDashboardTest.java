package com.tracker.web.auditoriared;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.*;
import org.junit.*;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import com.tracker.web.auditoriared.dashboard.*;

public class FiabilidadDashboardTest {
    private AuditoriaRedRepository repo;
    private AuditoriaRedServiceTest.MotorFalso motor;
    private AuditoriaRedService auditoria;
    private DashboardAuditoriaService panel;
    static CatalogoFiltrosDashboard filtros() {
        FiltrosDashboardConfig c = new FiltrosDashboardConfig();
        return new CatalogoFiltrosDashboard(Arrays.asList(c.filtroTipoAuditoria(), c.filtroTecnologiaAuditoria(),
                c.filtroCarrierAuditoria(), c.filtroBandaAuditoria(), c.filtroCeldaAuditoria(),
                c.filtroDistanciaAuditoria(), c.filtroCalidadAuditoria()));
    }
    private SolicitudDashboard solicitud() {
        SolicitudDashboard s = new SolicitudDashboard(); s.setLatitud(-12.0); s.setLongitud(-77.0);
        s.setRadioKm(2.0); s.setTecnologias(Arrays.asList("4G")); return s;
    }
    @Before public void preparar() {
        repo = mock(AuditoriaRedRepository.class); motor = new AuditoriaRedServiceTest.MotorFalso();
        AuditoriaRedServiceTest.responder(repo, "4G", Arrays.asList(
            AuditoriaRedServiceTest.celda("A", "4G", -12, -77, 100),
            AuditoriaRedServiceTest.celda("B", "4G", -12.005, -77, 100),
            AuditoriaRedServiceTest.celda("EXTERNA", "4G", -12.06, -77, 200)));
        motor.conflictos.add(new ConflictoMotor("PCI_COLLISION", "A", "B", 556, "100", null));
        motor.conflictos.add(new ConflictoMotor("PCI_MOD3", "A", "B", 556, "1", null));
        auditoria = AuditoriaRedServiceTest.servicio(repo, motor, 8000);
        panel = new DashboardAuditoriaService(auditoria, filtros());
        LectorSitioPlanificadoTest.usuario("ana", "ROLE_USER");
    }
    @After public void limpiar() { SecurityContextHolder.clearContext(); }

    @Test public void centrosCercanosNoCompartenCache() {
        auditoria.auditar(solicitud()); SolicitudDashboard b = solicitud(); b.setLatitud(-12.000001);
        assertFalse(auditoria.auditar(b).isDesdeCache()); assertEquals(2, motor.llamadas.size());
    }
    @Test public void cadaRespuestaEsIndependienteDeLaCache() {
        ResultadoAuditoria a = auditoria.auditar(solicitud()); a.getCeldas().get(0).setPci(999);
        a.getResumen().get(0).setCeldasEnArea(999);
        ResultadoAuditoria b = auditoria.auditar(solicitud());
        assertEquals(100, b.getCeldas().get(0).getPci()); assertEquals(2, b.getResumen().get(0).getCeldasEnArea());
        b.getHallazgos().get(0).getCeldaA().setPci(888);
        assertEquals(100, auditoria.auditar(solicitud()).getHallazgos().get(0).getCeldaA().getPci());
    }
    @Test public void cacheCeroSiempreRecalcula() {
        AuditoriaRedService sinCache = new AuditoriaRedService(repo,motor,AuditoriaRedServiceTest.catalogo(),10000,20,8000,8000,0);
        sinCache.auditar(solicitud()); assertFalse(sinCache.auditar(solicitud()).isDesdeCache());
        assertEquals(2,motor.llamadas.size());
    }
    @Test public void filtrarYPaginarNoCambiaElUniverso() {
        SolicitudDashboard s = solicitud(); s.setTamanoPagina(1); s.setPagina(2);
        Map<String,Object> r = panel.consultar(s); assertEquals(2,r.get("totalHallazgos")); assertEquals(2,r.get("totalPaginas"));
        s.setPagina(1); s.getFiltros().put("tipo", Arrays.asList("Colision PCI"));
        s.getFiltros().put("celda", Arrays.asList("b"));
        r = panel.consultar(s); assertEquals(1,r.get("totalFiltrados")); assertEquals(2,r.get("totalHallazgos"));
        assertEquals(3,motor.llamadas.get(0).size()); assertEquals(1,motor.llamadas.size());
    }
    @Test public void filtrosInvalidosSeRechazanAntesDeLeerLaRed() {
        for (String id : Arrays.asList("desconocido", "distanciaMaxM")) {
            SolicitudDashboard s = solicitud(); s.getFiltros().put(id, Arrays.asList("NaN"));
            try { panel.consultar(s); fail(); } catch (IllegalArgumentException esperado) { }
        }
        verify(repo,never()).leer(anyString(),any(CajaGeografica.class));
    }
    @Test public void agregarUnFiltroNoRequiereCambiarServicioNiControlador() {
        FiltroDashboard nuevo = new FiltroCampoDashboard("grupoNuevo", "Grupo nuevo", FiltroCampoDashboard.Tipo.SELECCION,
                h -> Arrays.asList(h.getCeldaA().getPci() >= 100 ? "Alto" : "Bajo"));
        DashboardAuditoriaService extendido = new DashboardAuditoriaService(auditoria,new CatalogoFiltrosDashboard(Arrays.asList(nuevo)));
        SolicitudDashboard s=solicitud(); s.getFiltros().put("grupoNuevo",Arrays.asList("Bajo"));
        assertEquals(0,extendido.consultar(s).get("totalFiltrados"));
        assertEquals("grupoNuevo",extendido.definiciones().get(0).get("id"));
        try { new CatalogoFiltrosDashboard(Arrays.asList(nuevo,nuevo)); fail(); } catch (IllegalStateException esperado) { }
    }
    @Test public void unResultadoConTopeConservaElAvisoAunqueElFiltroEsteVacio() {
        motor.capped=true; SolicitudDashboard s=solicitud(); s.getFiltros().put("celda",Arrays.asList("NoExiste"));
        Map<String,Object> r=panel.consultar(s); assertEquals(true,r.get("incompleto")); assertEquals(0,r.get("totalFiltrados"));
        assertTrue(r.get("avisos").toString().contains("incompleto"));
    }
    @Test public void vecinosUsaTambienContextoQueNoSeDibujaEnElArea() {
        MotorAuditoria espia=mock(MotorAuditoria.class);
        when(espia.auditar(anyString(),anyList(),anyInt(),anyInt())).thenReturn(new ResultadoMotor(Collections.emptyList(),false,Collections.emptyList()));
        when(espia.geometria(anyString(),anyList(),anyCollection())).thenReturn(Collections.emptyMap());
        when(espia.vecinos(anyString(),anyList(),anyString(),anyInt())).thenReturn(Collections.emptyList());
        AuditoriaRedService a=AuditoriaRedServiceTest.servicio(repo,espia,8000);
        SolicitudDashboard s=solicitud(); s.setCeldaObjetivo("A"); s.setTecnologiaObjetivo("4G");
        Map<String,Object> r=new DashboardAuditoriaService(a,filtros()).consultar(s);
        assertEquals(2,((List<?>)r.get("celdas")).size());
        org.mockito.ArgumentCaptor<List> cap=org.mockito.ArgumentCaptor.forClass(List.class);
        verify(espia).vecinos(eq("4G"),cap.capture(),eq("A"),eq(30)); assertEquals(3,cap.getValue().size());
    }
    private MockMvc mvc() {
        FileTemplateResolver t=new FileTemplateResolver(); t.setPrefix("src/main/resources/templates/"); t.setSuffix(".html"); t.setCharacterEncoding("UTF-8");
        SpringTemplateEngine e=new SpringTemplateEngine(); e.setTemplateResolver(t);
        ThymeleafViewResolver v=new ThymeleafViewResolver(); v.setTemplateEngine(e); v.setCharacterEncoding("UTF-8");
        return MockMvcBuilders.standaloneSetup(new DashboardAuditoriaController(panel)).setControllerAdvice(new ErroresDashboard()).setViewResolvers(v).build();
    }
    @Test public void renderizaElPanelConContextPathYMetadatos() throws Exception {
        String html=mvc().perform(get("/tracker/auditoria-red/dashboard").contextPath("/tracker")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertTrue(html.contains("data-query-url=\"/tracker/auditoria-red/dashboard/api/consultar\""));
        assertTrue(html.contains("/tracker/js/auditoriared-dashboard.js")); assertTrue(html.contains("id=\"ad-target\""));
        mvc().perform(get("/auditoria-red/dashboard/api/filtros")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(7));
    }
    @Test public void rechazaAnonimosYUsuariosSinRol() throws Exception {
        SecurityContextHolder.clearContext(); mvc().perform(get("/auditoria-red/dashboard/api/filtros")).andExpect(status().isForbidden());
        SecurityContextHolder.getContext().setAuthentication(new AnonymousAuthenticationToken("key","anonymous",AuthorityUtils.createAuthorityList("ROLE_USER")));
        mvc().perform(get("/auditoria-red/dashboard/api/filtros")).andExpect(status().isForbidden());
        LectorSitioPlanificadoTest.usuario("otro","ROLE_OTHER");
        mvc().perform(get("/auditoria-red/dashboard/api/filtros")).andExpect(status().isForbidden());
    }
    @Test public void apiRechazaFiltrosDesconocidosYPaginasInvalidas() throws Exception {
        String json="{\"latitud\":-12,\"longitud\":-77,\"radioKm\":2,\"tecnologias\":[\"4G\"],\"filtros\":{\"SQL\":[\"x\"]}}";
        mvc().perform(post("/auditoria-red/dashboard/api/consultar").contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isBadRequest());
        SolicitudDashboard s=solicitud(); s.setPagina(Integer.MAX_VALUE);
        try { panel.consultar(s); fail(); } catch (IllegalArgumentException esperado) { }
    }
}
