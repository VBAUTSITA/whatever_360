package com.tracker.web.recursos.planner;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tracker.web.recursos.model.Recursos;
import com.tracker.web.recursos.planner.dto.CeldaPlanDTO;
import com.tracker.web.recursos.planner.dto.ResultadoLoteDTO;
import com.tracker.web.recursos.planner.dto.ResultadoPlanificacionDTO;
import com.tracker.web.recursos.repository.RecursosRepository;

/**
 * ============================ MULTI-TECNOLOGIA ============================
 * [CRITICO] Este servicio es el UNICO punto donde se decide la tecnologia.
 * El flujo por lote es:
 *
 *   1. Se separan los candidatos en DOS sub-lotes segun Recursos.tecnologia
 *      (normalizada: "4G"/"LTE" -> lote 4G; "5G"/"NR" -> lote 5G; vacia u
 *      otra cosa -> se omite con mensaje visible, NUNCA se planifica con un
 *      motor "por defecto" que podria ser el equivocado).
 *   2. Cada sub-lote consulta SU PROPIA red existente (RedExistenteService
 *      para 4G, RedExistenteService5G para 5G) — un lote 4G jamas toca
 *      tablas 5G ni viceversa, y si el lote de una tecnologia esta vacio esa
 *      consulta Oracle ni siquiera se ejecuta.
 *   3. Cada sub-lote se ejecuta con SU motor (PlannerEngine.planificarLote4g
 *      evalua planner_4g.js; planificarLote5g evalua planner_5g.js).
 *   4. Los resultados de ambos sub-lotes se vuelcan sobre la misma lista de
 *      candidatos y los contadores (total existentes / con trafico) se suman
 *      para la vista.
 *
 * Todo lo demas (no pisar PCI/RSI ya guardados, validacion de campos,
 * mapeo por cellname, auditoria) es identico para ambas tecnologias y por
 * eso vive en helpers compartidos.
 * ==========================================================================
 */
@Service
public class PciRsiAsignacionServiceImpl implements PciRsiAsignacionService {

	private static final Logger log = LoggerFactory.getLogger(PciRsiAsignacionServiceImpl.class);

	@Autowired
	private RecursosRepository recursosRepository;

	@Autowired
	private RedExistenteService redExistenteService;      // fuente 4G

	@Autowired
	private RedExistenteService5G redExistenteService5G;  // fuente 5G

	@Autowired
	private PlannerEngine plannerEngine;                  // ambos motores JS

	@Override
	public ResultadoAutoAsignacion autoAsignar(List<Long> recursoIds) {
		// findAllById devuelve Iterable<Recursos>, no List: se copia explicito.
		List<Recursos> candidatos = new ArrayList<>();
		recursosRepository.findAllById(recursoIds).forEach(candidatos::add);
		List<String> omitidos = new ArrayList<>();

		// [MULTI-TECNOLOGIA] paso 1: separar el lote por tecnologia.
		List<Recursos> aPlanificar4g = new ArrayList<>();
		List<Recursos> aPlanificar5g = new ArrayList<>();

		for (Recursos r : candidatos) {
			// No sobreescribir un PCI/RSI que un humano ya guardo: auto-asignar
			// solo rellena lo que esta vacio.
			if (r.getPciPsc() != null && r.getRsi() != null) {
				omitidos.add(etiqueta(r) + ": ya tenia PCI/RSI, no se modifico");
				continue;
			}
			String faltante = campoFaltante(r);
			if (faltante != null) {
				omitidos.add(etiqueta(r) + ": falta " + faltante + ", no se puede auto-asignar");
				continue;
			}
			// [MULTI-TECNOLOGIA - CRITICO] el enrutamiento al motor correcto.
			// Sin tecnologia reconocible la celda se OMITE con mensaje, porque
			// planificar 5G con el motor 4G (o viceversa) produce un PCI de
			// rango equivocado sin ningun error visible.
			String tecno = normalizarTecnologia(r.getTecnologia());
			if ("4G".equals(tecno)) {
				aPlanificar4g.add(r);
			} else if ("5G".equals(tecno)) {
				aPlanificar5g.add(r);
			} else {
				omitidos.add(etiqueta(r) + ": tecnologia '" + String.valueOf(r.getTecnologia())
						+ "' no soportada por el auto-asignador (usar 4G/LTE o 5G/NR)");
			}
		}

		List<Long> autoComputadosIds = new ArrayList<>();
		int totalExistentes = 0;
		int conDatosTrafico = 0;

		// ============================ SUB-LOTE 4G ============================
		// [MULTI-TECNOLOGIA] red 4G + planner_4g.js. Solo si hay celdas 4G.
		if (!aPlanificar4g.isEmpty()) {
			Map<String, Recursos> porCellname = new LinkedHashMap<>();
			List<CeldaPlanDTO> plan = construirPlan(aPlanificar4g, porCellname, omitidos);

			// La red existente se acota a una caja alrededor de "plan", por eso
			// se construye plan primero y se le pasa a obtenerCeldasExistentes.
			ResultadoRedExistente red = redExistenteService.obtenerCeldasExistentes(plan);
			totalExistentes += red.getTotalExistentes();
			conDatosTrafico += red.getConDatosTrafico();

			try {
				ResultadoLoteDTO lote = plannerEngine.planificarLote4g(red.getCeldas(), plan);
				aplicarResultados(lote, porCellname, omitidos, autoComputadosIds);
			} catch (PlannerEngineException e) {
				log.error("Fallo el motor de auto-asignacion PCI/RSI 4G", e);
				marcarLoteFallido(porCellname, omitidos, e);
			}
		}

		// ============================ SUB-LOTE 5G ============================
		// [MULTI-TECNOLOGIA] red 5G + planner_5g.js. Independiente del 4G: si
		// el motor 4G fallo, el 5G igual se intenta (y viceversa) — cada
		// sub-lote reporta lo suyo en "omitidos".
		if (!aPlanificar5g.isEmpty()) {
			Map<String, Recursos> porCellname = new LinkedHashMap<>();
			List<CeldaPlanDTO> plan = construirPlan(aPlanificar5g, porCellname, omitidos);

			ResultadoRedExistente5G red = redExistenteService5G.obtenerCeldasExistentes(plan);
			totalExistentes += red.getTotalExistentes();
			conDatosTrafico += red.getConDatosTrafico();

			try {
				ResultadoLoteDTO lote = plannerEngine.planificarLote5g(red.getCeldas(), plan);
				aplicarResultados(lote, porCellname, omitidos, autoComputadosIds);
			} catch (PlannerEngineException e) {
				log.error("Fallo el motor de auto-asignacion PCI/RSI 5G", e);
				marcarLoteFallido(porCellname, omitidos, e);
			}
		}

		// [MULTI-TECNOLOGIA] los contadores que ve el usuario son la SUMA de
		// ambas redes consultadas (solo las tecnologias presentes en el lote).
		return new ResultadoAutoAsignacion(candidatos, autoComputadosIds, omitidos, totalExistentes, conDatosTrafico);
	}

	/**
	 * [MULTI-TECNOLOGIA] "4G"/"LTE" -> "4G"; "5G"/"NR" -> "5G"; cualquier otra
	 * cosa (incluido null/vacio/3G) -> null = no enrutable. Se usa contains()
	 * para tolerar valores tipeados a mano tipo "LTE 4G" o "5G NR SA".
	 */
	private static String normalizarTecnologia(String cruda) {
		if (cruda == null) {
			return null;
		}
		String t = cruda.trim().toUpperCase();
		if (t.isEmpty()) {
			return null;
		}
		boolean es5g = t.contains("5G") || t.contains("NR");
		boolean es4g = t.contains("4G") || t.contains("LTE");
		if (es5g && !es4g) {
			return "5G";
		}
		if (es4g && !es5g) {
			return "4G";
		}
		// Ambiguo ("4G/5G") o desconocido ("3G", "UMTS"): no se enruta.
		return null;
	}

	/**
	 * Construye el plan de UN sub-lote de tecnologia y llena el mapa
	 * cellname -> Recursos. Mapeo por cellname: planificarLote reordena por
	 * costo, asi que no se puede mapear el resultado de vuelta por indice del
	 * array de entrada. Compartido por 4G y 5G (la logica es identica; lo que
	 * cambia por tecnologia ya quedo decidido antes de llamar aqui).
	 */
	private static List<CeldaPlanDTO> construirPlan(List<Recursos> aPlanificar,
			Map<String, Recursos> porCellname, List<String> omitidos) {
		List<CeldaPlanDTO> plan = new ArrayList<>();
		for (Recursos r : aPlanificar) {
			if (porCellname.containsKey(r.getCellName())) {
				omitidos.add(etiqueta(r) + ": cellName duplicado en este lote, no se puede "
						+ "mapear el resultado de forma confiable, no se auto-asigno");
				continue;
			}
			porCellname.put(r.getCellName(), r);
			plan.add(aDto(r));
		}
		return plan;
	}

	/**
	 * Vuelca los resultados de UN motor sobre sus Recursos. Compartido por 4G
	 * y 5G porque ambos planner devuelven el mismo shape
	 * {cellname, pci, rsi, error, auditoria, advertencias}; en 5G "rsi" ya
	 * viene de la tabla RSI 5G embebida en planner_5g.js.
	 */
	private void aplicarResultados(ResultadoLoteDTO lote, Map<String, Recursos> porCellname,
			List<String> omitidos, List<Long> autoComputadosIds) {
		for (ResultadoPlanificacionDTO res : lote.getResultados()) {
			Recursos r = porCellname.remove(res.getCellname());
			if (r == null) {
				log.warn("El planner devolvio un resultado para '{}' que no estaba en el lote enviado", res.getCellname());
				continue;
			}
			if (res.getError() != null) {
				omitidos.add(etiqueta(r) + ": el motor reporto un error - " + res.getError());
				continue;
			}
			r.setPciPsc(res.getPci());
			r.setRsi(res.getRsi());
			r.setAuditoriaPci(formatearAuditoria(res));
			autoComputadosIds.add(r.getId());
		}
		// Lo que quedo en porCellname se envio al motor pero no volvio en los
		// resultados (no deberia pasar; se deja visible en vez de ignorarlo).
		for (Recursos r : porCellname.values()) {
			omitidos.add(etiqueta(r) + ": el motor no devolvio resultado para esta celda");
		}
		porCellname.clear();
	}

	/** Marca todas las celdas de un sub-lote cuyo motor exploto por completo. */
	private static void marcarLoteFallido(Map<String, Recursos> porCellname,
			List<String> omitidos, PlannerEngineException e) {
		for (Recursos r : porCellname.values()) {
			omitidos.add(etiqueta(r) + ": fallo el motor de auto-asignacion - " + e.getMessage());
		}
		porCellname.clear();
	}

	private static CeldaPlanDTO aDto(Recursos r) {
		CeldaPlanDTO dto = new CeldaPlanDTO();
		dto.setCellname(r.getCellName());
		dto.setCarrier(r.getCarrier());
		// [MULTI-TECNOLOGIA] viaja como traza; el enrutamiento ya ocurrio en
		// autoAsignar() sobre Recursos.tecnologia.
		dto.setTecnologia(r.getTecnologia());
		dto.setFreqband(r.getFreqband());
		dto.setTxrxmode(r.getTxrxmode());
		dto.setLatitud(r.getLatitud());
		dto.setLongitud(r.getLongitud());
		dto.setAzimuth(r.getAzimuth());
		dto.setIndoor("S".equalsIgnoreCase(r.getIndoor()));
		dto.setPhycellid(null); // celda nueva, todavia sin PCI
		return dto;
	}

	private static String campoFaltante(Recursos r) {
		// [MULTI-TECNOLOGIA] tecnologia es ahora un campo OBLIGATORIO para
		// auto-asignar: sin ella no se puede elegir motor ni red existente.
		if (isBlank(r.getTecnologia())) return "tecnologia";
		if (isBlank(r.getCellName())) return "cellName";
		if (isBlank(r.getCarrier())) return "carrier";
		if (r.getAzimuth() == null) return "azimuth";
		if (isBlank(r.getTxrxmode())) return "txrxmode";
		if (isBlank(r.getFreqband())) return "freqband";
		if (r.getLatitud() == null) return "latitud";
		if (r.getLongitud() == null) return "longitud";
		return null;
	}

	private static boolean isBlank(String s) {
		return s == null || s.trim().isEmpty();
	}

	private static String etiqueta(Recursos r) {
		String site = r.getSiteName() != null ? r.getSiteName() : "?";
		String cell = r.getCellName() != null ? r.getCellName() : ("id=" + r.getId());
		return site + "/" + cell;
	}

	private static String formatearAuditoria(ResultadoPlanificacionDTO res) {
		StringBuilder sb = new StringBuilder();
		if (res.getAuditoria() != null) {
			for (String linea : res.getAuditoria()) {
				sb.append(linea).append('\n');
			}
		}
		if (res.getAdvertencias() != null && !res.getAdvertencias().isEmpty()) {
			sb.append("ADVERTENCIAS:\n");
			for (String linea : res.getAdvertencias()) {
				sb.append("- ").append(linea).append('\n');
			}
		}
		String texto = sb.toString();
		// AUDITORIA_PCI es VARCHAR2(4000) (bytes) en Oracle; se trunca por seguridad.
		return texto.length() > 3900 ? texto.substring(0, 3900) : texto;
	}
}
