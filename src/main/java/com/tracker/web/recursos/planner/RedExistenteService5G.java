package com.tracker.web.recursos.planner;

import java.util.List;

import com.tracker.web.recursos.planner.dto.CeldaPlanDTO;

/**
 * ============================ MULTI-TECNOLOGIA ============================
 * Fuente de la red 5G NR ya desplegada (tm_conf_actual_celda_5g +
 * vw_plani_recursos_5g_ta_diario). Es la contraparte 5G de
 * {@link RedExistenteService} (4G): cada tecnologia tiene su PROPIA interfaz
 * y su propia implementacion con sus propias tablas Oracle, y
 * PciRsiAsignacionServiceImpl decide cual invocar segun la tecnologia del
 * lote. Asi un lote 4G jamas consulta tablas 5G ni viceversa.
 * ==========================================================================
 */
public interface RedExistenteService5G {

	/**
	 * Trae la red 5G existente acotada a una caja geografica generosa alrededor
	 * de {@code aPlanificar} (no la red nacional completa) - ver
	 * RADIO_SEGURIDAD_KM en la implementacion para por que esto no cambia el
	 * resultado del motor.
	 */
	ResultadoRedExistente5G obtenerCeldasExistentes(List<CeldaPlanDTO> aPlanificar);
}
