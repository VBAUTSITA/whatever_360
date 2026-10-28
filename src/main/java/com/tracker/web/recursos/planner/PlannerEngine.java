package com.tracker.web.recursos.planner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracker.web.recursos.planner.dto.CeldaExistenteDTO;
import com.tracker.web.recursos.planner.dto.CeldaExistenteDTO5G;
import com.tracker.web.recursos.planner.dto.CeldaPlanDTO;
import com.tracker.web.recursos.planner.dto.ResultadoLoteDTO;

/**
 * Puente hacia los motores planner_*.js via Nashorn embebido en Java 8
 * (javax.script). Un ScriptEngine NUEVO por llamada (no pooled/ThreadLocal):
 * los planner no se pueden ejecutar localmente para comprobar que no tengan
 * estado mutable a nivel de modulo, asi que se evita por diseno cualquier
 * posibilidad de que una corrida deje basura visible para la siguiente.
 * El costo de re-evaluar ~1500 lineas de JS por llamada es irrelevante frente
 * al resto del flujo (render de pagina + roundtrip a Oracle) ya que esto se
 * dispara por click humano en un batch, no por fila.
 *
 * ============================ MULTI-TECNOLOGIA ============================
 * [CRITICO] Este componente carga DOS motores en el arranque:
 *   - scripts/planner_4g.js  -> planificarLote4g(...)  (LTE:   PCI 0-503)
 *   - scripts/planner_5g.js  -> planificarLote5g(...)  (NR:    PCI 0-1007,
 *     tabla RSI propia embebida, bins TA con cortes distintos)
 * Ambos exponen la MISMA API JS (PCIPlanner.Planificador + planificarLote y
 * el mismo shape de resultado {resultados:[{cellname,pci,rsi,error,...}]}),
 * verificado en ambos fuentes. Por eso el metodo privado ejecutar() es unico
 * y lo que cambia por tecnologia es (a) QUE fuente JS se evalua y (b) el tipo
 * de DTO de celdas existentes que se serializa. La seleccion de tecnologia
 * NO ocurre aqui adentro: PciRsiAsignacionServiceImpl separa el lote por
 * tecnologia y llama al metodo publico que corresponde. Asi este componente
 * no necesita interpretar el valor de "tecnologia" y no hay ningun default
 * silencioso (llamar al motor equivocado es imposible por firma de tipos:
 * List&lt;CeldaExistenteDTO&gt; solo compila contra el 4G y
 * List&lt;CeldaExistenteDTO5G&gt; solo contra el 5G).
 * ==========================================================================
 */
@Component
public class PlannerEngine {

	private static final Logger log = LoggerFactory.getLogger(PlannerEngine.class);

	// [MULTI-TECNOLOGIA] un script por tecnologia. planner_4g.js es el motor
	// LTE (antes scripts/planner.js); planner_5g.js es el motor 5G NR.
	private static final String SCRIPT_PATH_4G = "scripts/planner_4g.js";
	private static final String SCRIPT_PATH_5G = "scripts/planner_5g.js";

	private final ObjectMapper objectMapper = new ObjectMapper();

	private String planner4gSource;
	private String planner5gSource;

	@PostConstruct
	public void init() throws IOException {
		// [MULTI-TECNOLOGIA] ambos fuentes se cargan y validan al ARRANCAR:
		// si falta cualquiera de los dos scripts la app no levanta (falla
		// ruidosa en el deploy, no un error criptico en el primer click).
		this.planner4gSource = cargarScript(SCRIPT_PATH_4G);
		this.planner5gSource = cargarScript(SCRIPT_PATH_5G);
		if (new ScriptEngineManager().getEngineByName("nashorn") == null) {
			throw new IllegalStateException(
					"El motor JS 'nashorn' no esta disponible en este JDK. "
					+ "El modulo de auto-asignacion PCI/RSI requiere Java 8 (Nashorn fue "
					+ "removido en JDK 15+). Verificar la version del JDK de despliegue.");
		}
	}

	private String cargarScript(String path) throws IOException {
		try (InputStream in = getClass().getClassLoader().getResourceAsStream(path)) {
			if (in == null) {
				throw new IllegalStateException("No se encontro " + path + " en el classpath");
			}
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			byte[] chunk = new byte[8192];
			int read;
			while ((read = in.read(chunk)) != -1) {
				buffer.write(chunk, 0, read);
			}
			return new String(buffer.toByteArray(), StandardCharsets.UTF_8);
		}
	}

	/**
	 * [MULTI-TECNOLOGIA] Ejecuta el motor LTE (planner_4g.js) contra la red
	 * 4G existente dada. Solo acepta CeldaExistenteDTO (bins TA 4G).
	 */
	public ResultadoLoteDTO planificarLote4g(List<CeldaExistenteDTO> existentes, List<CeldaPlanDTO> plan)
			throws PlannerEngineException {
		return ejecutar("4G", planner4gSource, existentes, plan);
	}

	/**
	 * [MULTI-TECNOLOGIA] Ejecuta el motor 5G NR (planner_5g.js) contra la red
	 * 5G existente dada. Solo acepta CeldaExistenteDTO5G (bins TA 5G).
	 */
	public ResultadoLoteDTO planificarLote5g(List<CeldaExistenteDTO5G> existentes, List<CeldaPlanDTO> plan)
			throws PlannerEngineException {
		return ejecutar("5G", planner5gSource, existentes, plan);
	}

	/**
	 * Nucleo comun: identico para todas las tecnologias porque todos los planner
	 * exponen la misma API JS y el mismo shape de resultado. {@code existentes}
	 * es Object a proposito — el tipado fuerte ya ocurrio en los metodos
	 * publicos de arriba; aqui solo se serializa a JSON.
	 *
	 * [FIX] El JSON se pasa via Bindings en vez de interpolarlo como string
	 * literal en el codigo JS. Nashorn lee __existentesJson y __planJson como
	 * objetos Java String en RUNTIME a traves de JSON.parse(); el lexer/compiler
	 * de Nashorn nunca ve el payload de 14MB como codigo fuente, eliminando el
	 * "Missing close quote" por exceder el limite de CONSTANT_Utf8.
	 */
	private ResultadoLoteDTO ejecutar(String tecnologia, String plannerJsSource,
			Object existentes, List<CeldaPlanDTO> plan) throws PlannerEngineException {
		try {
			String existentesJson = objectMapper.writeValueAsString(existentes);
			String planJson = objectMapper.writeValueAsString(plan);

			log.info("PlannerEngine [{}]: {} celdas a planificar, red existente JSON de {} chars",
					tecnologia, plan == null ? 0 : plan.size(), existentesJson.length());

			ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
			if (engine == null) {
				throw new IllegalStateException("Motor 'nashorn' no disponible (ver PlannerEngine.init)");
			}

			// [FIX] Pasar el JSON como objetos Java nativos via Bindings.
			// JSON.parse() los lee en RUNTIME, asi el LEXER de Nashorn nunca
			// ve el blob de 14MB como codigo fuente JS.
			Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
			bindings.put("__existentesJson", existentesJson);
			bindings.put("__planJson", planJson);

			String wrapper = "(function() {"
					+ "  try {"
					+ "    var existentes = JSON.parse(__existentesJson);"
					+ "    var plan = JSON.parse(__planJson);"
					+ "    var planificador = new PCIPlanner.Planificador(existentes, {});"
					+ "    var resultado = planificador.planificarLote(plan);"
					+ "    return JSON.stringify(resultado);"
					+ "  } catch (e) {"
					+ "    return JSON.stringify({ error: String(e), stack: (e && e.stack) ? String(e.stack) : null });"
					+ "  }"
					+ "})();";

			// [MULTI-TECNOLOGIA] aqui se evalua SOLO el fuente de la tecnologia
			// pedida. Como cada llamada usa un ScriptEngine nuevo, el PCIPlanner
			// global del 4G y el del 5G jamas conviven en el mismo contexto y no
			// pueden pisarse entre si.
			Object resultRaw = engine.eval(plannerJsSource + "\n" + wrapper);
			if (resultRaw == null) {
				throw new PlannerEngineException("planner_" + tecnologia.toLowerCase() + ".js no devolvio resultado (eval devolvio null)");
			}
			ResultadoLoteDTO resultado = objectMapper.readValue(resultRaw.toString(), ResultadoLoteDTO.class);
			if (resultado.getError() != null) {
				throw new PlannerEngineException("planner_" + tecnologia.toLowerCase() + ".js lanzo un error: " + resultado.getError());
			}
			return resultado;
		} catch (ScriptException e) {
			throw new PlannerEngineException("Error evaluando planner_" + tecnologia.toLowerCase() + ".js: " + e.getMessage(), e);
		} catch (IOException e) {
			throw new PlannerEngineException("Error serializando/deserializando JSON para planner_" + tecnologia.toLowerCase() + ".js", e);
		}
	}
}
