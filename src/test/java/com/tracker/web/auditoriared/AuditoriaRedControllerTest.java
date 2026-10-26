package com.tracker.web.auditoriared;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;

/** Controlador y plantilla juntos: la pagina se renderiza de verdad, no solo se comprueba el nombre de la vista. */
public class AuditoriaRedControllerTest {

    private static final String JSON =
            "{\"latitud\":-12.0,\"longitud\":-77.0,\"radioKm\":2,\"tecnologias\":[\"4G\"]}";

    private AuditoriaRedServiceTest.MotorFalso motor;
    private MockMvc mvc;

    /** Lector de RECURSOS_FORMULARIO con dos celdas 4G de LI0168 de ZONA LIMA, y ana de ZONA LIMA logueada. */
    private static LectorSitioPlanificado lector() {
        JdbcTemplate jdbc = org.mockito.Mockito.mock(JdbcTemplate.class);
        java.util.List<java.util.Map<String, Object>> filas = Arrays.asList(
                LectorSitioPlanificadoTest.fila(12, "LI0168", "DENTRO_A", "4G", "ZONA LIMA"),
                LectorSitioPlanificadoTest.fila(13, "LI0168", "LIL01682C1", "4G", "ZONA NORTE"));
        for (java.util.Map<String, Object> f : filas) {
            f.put("LATITUD", AuditoriaRedServiceTest.LAT);
            f.put("LONGITUD", AuditoriaRedServiceTest.LON);
        }
        LectorSitioPlanificadoTest.responder(jdbc, filas);
        LectorSitioPlanificadoTest.usuario("ana", "ROLE_USER");
        return new LectorSitioPlanificado(jdbc, LectorSitioPlanificadoTest.jefaturas("ana:ZONA LIMA"));
    }

    private static MockMvc mvc(AuditoriaRedService service) {
        FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setPrefix("src/main/resources/templates/");
        resolver.setSuffix(".html");
        resolver.setCharacterEncoding("UTF-8");
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(resolver);
        ThymeleafViewResolver vistas = new ThymeleafViewResolver();
        vistas.setTemplateEngine(engine);
        vistas.setCharacterEncoding("UTF-8");
        return MockMvcBuilders.standaloneSetup(new AuditoriaRedController(service, lector()))
                .setViewResolvers(vistas).build();
    }

    @Before
    public void preparar() {
        AuditoriaRedRepository repo = mock(AuditoriaRedRepository.class);
        AuditoriaRedServiceTest.responder(repo, "4G", Arrays.asList(
                AuditoriaRedServiceTest.celda("DENTRO_A", "4G", AuditoriaRedServiceTest.norte(0.5), -77.0, 100),
                AuditoriaRedServiceTest.celda("MARGEN_B", "4G", AuditoriaRedServiceTest.norte(8), -77.0, 100)));
        motor = new AuditoriaRedServiceTest.MotorFalso();
        motor.conflictos.add(new ConflictoMotor("PCI_COLLISION", "DENTRO_A", "MARGEN_B", 7500, "100", null));
        mvc = mvc(AuditoriaRedServiceTest.servicio(repo, motor, 40000));
    }

    @After
    public void limpiar() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void laPaginaSeRenderizaConElLayoutYLasUrlsDeContexto() throws Exception {
        String html = mvc.perform(get("/tracker/auditoria-red").contextPath("/tracker"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertTrue(html.contains("data-ejecutar-url=\"/tracker/auditoria-red/api/ejecutar\""));
        assertTrue(html.contains("data-sitios-url=\"/tracker/auditoria-red/api/sitios\""));
        assertTrue(html.contains("data-radio-max=\"20.0\""));
        assertTrue(html.contains("data-reuso-max=\"10.0\""));
        assertTrue(html.contains("id=\"ar-reuso\""));
        assertTrue(html.contains("/tracker/js/leaflet/leaflet.js"));
        assertTrue(html.contains("/tracker/js/leaflet/leaflet.css"));
        assertTrue(html.contains("/tracker/js/auditoriared.js"));
        assertTrue("Debe usar el encabezado comun de la app", html.contains("Auditoría de Red"));
        Files.createDirectories(Paths.get("target/auditoriared-render"));
        Files.write(Paths.get("target/auditoriared-render/index.html"), html.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    public void cadaHallazgoLlegaConLasCoordenadasDeAmbasCeldas() throws Exception {
        mvc.perform(post("/auditoria-red/api/ejecutar").contentType(MediaType.APPLICATION_JSON).content(JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.alcance.tipo").value("CIRCULO"))
                .andExpect(jsonPath("$.alcance.radioKm").value(2.0))
                .andExpect(jsonPath("$.alcance.margenKm").value(10.1))
                .andExpect(jsonPath("$.hallazgos.length()").value(1))
                .andExpect(jsonPath("$.hallazgos[0].etiqueta").value("Colision PCI"))
                .andExpect(jsonPath("$.hallazgos[0].celdaA.latitud").isNumber())
                .andExpect(jsonPath("$.hallazgos[0].celdaB.longitud").value(-77.0))
                .andExpect(jsonPath("$.hallazgos[0].celdaB.dentroArea").value(false))
                .andExpect(jsonPath("$.celdas.length()").value(2))
                .andExpect(jsonPath("$.resumen[0].motor").value("motor-falso-4G"))
                .andExpect(jsonPath("$.hallazgos[0].celdaA.ta").doesNotExist())
                .andExpect(jsonPath("$.hallazgos[0].celdaA.mod").value(1))
                .andExpect(jsonPath("$.resumen[0].geometriaDisponible").value(true))
                .andExpect(jsonPath("$.capped").value(false));
    }

    @Test
    public void unaSolicitudInvalidaDevuelve400ConMensaje() throws Exception {
        mvc.perform(post("/auditoria-red/api/ejecutar").contentType(MediaType.APPLICATION_JSON)
                .content("{\"latitud\":-12.0,\"longitud\":-77.0,\"radioKm\":50,\"tecnologias\":[\"4G\"]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El radio maximo por cluster es 20 km"));
    }

    @Test
    public void unaAuditoriaEnCursoDevuelve409() throws Exception {
        AuditoriaRedService ocupado = mock(AuditoriaRedService.class);
        when(ocupado.auditar(any(SolicitudAuditoria.class))).thenThrow(new AuditoriaOcupadaException());
        mvc(ocupado).perform(post("/auditoria-red/api/ejecutar").contentType(MediaType.APPLICATION_JSON).content(JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void sinNashornElErrorEsLegibleYNoUn500() throws Exception {
        AuditoriaRedService sinMotor = mock(AuditoriaRedService.class);
        when(sinMotor.auditar(any(SolicitudAuditoria.class)))
                .thenThrow(new IllegalStateException("El motor JS 'nashorn' no esta disponible en este JDK."));
        mvc(sinMotor).perform(post("/auditoria-red/api/ejecutar").contentType(MediaType.APPLICATION_JSON).content(JSON))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error").value("El motor JS 'nashorn' no esta disponible en este JDK."));
    }

    @Test
    public void laPaginaDeSitioEsLaMismaConLaUrlDeLaApiDeSitio() throws Exception {
        String html = mvc.perform(get("/tracker/auditoria-red/sitio").contextPath("/tracker")
                .param("sitio", "LI0168").param("ids", "12").param("pci", "100"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertTrue(html.contains("data-sitio-url=\"/tracker/auditoria-red/api/sitio\""));
        assertTrue(html.contains("id=\"ar-sitio-panel\" hidden"));
        assertTrue(html.contains("id=\"ar-sitio-celdas\""));
        assertTrue(html.contains("id=\"ar-titulo\""));
        assertFalse("La pagina no debe incrustar los parametros: los lee el JS", html.contains("LI0168"));
        Files.createDirectories(Paths.get("target/mapasitio-render"));
        Files.write(Paths.get("target/mapasitio-render/sitio.html"), html.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    public void laApiDeSitioDevuelveLasPlanificadasYSusHallazgos() throws Exception {
        motor.conflictos.clear();
        motor.conflictos.add(new ConflictoMotor("PCI_COLLISION", "DENTRO_A", "MARGEN_B", 7500, "100", null));
        mvc.perform(get("/auditoria-red/api/sitio").param("ids", "12").param("pci", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hallazgos.length()").value(1))
                .andExpect(jsonPath("$.hallazgos[0].celdaA.planificada").value(true))
                .andExpect(jsonPath("$.hallazgos[0].celdaA.pci").value(100))
                .andExpect(jsonPath("$.hallazgos[0].celdaB.planificada").value(false))
                .andExpect(jsonPath("$.celdas.length()").value(2))
                .andExpect(jsonPath("$.avisos.length()").value(0))
                .andExpect(jsonPath("$.desdeCache").value(false));
    }

    @Test
    public void laApiDeSitioConIdsYPciDesparejosDevuelve400() throws Exception {
        mvc.perform(get("/auditoria-red/api/sitio").param("ids", "12,13").param("pci", "100"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Tiene que llegar un PCI por cada id"));
    }

    @Test
    public void laApiDeSitioConUnIdDeOtraJefaturaDevuelve403() throws Exception {
        mvc.perform(get("/auditoria-red/api/sitio").param("ids", "12,13").param("pci", "100,101"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("El registro 13 es de otra jefatura"));
    }

    @Test
    public void laBusquedaDeSitiosDevuelveJson() throws Exception {
        AuditoriaRedService conSitios = mock(AuditoriaRedService.class);
        when(conSitios.buscarSitios("LI0137")).thenReturn(Collections.singletonList(
                new SitioDTO("LIL01377C1_Nat", -12.092056, -77.07125, "4G")));
        mvc(conSitios).perform(get("/auditoria-red/api/sitios").param("q", "LI0137"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("LIL01377C1_Nat"))
                .andExpect(jsonPath("$[0].tecnologia").value("4G"));
    }
}
