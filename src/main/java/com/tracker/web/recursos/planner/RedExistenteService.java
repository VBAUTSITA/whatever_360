package com.tracker.web.recursos.planner;

import java.util.List;

import com.tracker.web.recursos.planner.dto.CeldaPlanDTO;

/** Fuente de la red 4G ya desplegada (tm_conf_actual_celda_4g + PLAN_MAP_SECT_CRIT_4GWEEK). */
public interface RedExistenteService {

	/**
	 * Trae la red existente acotada a una caja geografica generosa alrededor de
	 * {@code aPlanificar} (no la red nacional completa) - ver RADIO_SEGURIDAD_KM
	 * en la implementacion para por que esto no cambia el resultado.
	 */
	ResultadoRedExistente obtenerCeldasExistentes(List<CeldaPlanDTO> aPlanificar);
}
