package com.tracker.web.recursos.planner.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Salida de Planificador.planificarLote: { resultados, params, duracion_s }. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResultadoLoteDTO {

	private List<ResultadoPlanificacionDTO> resultados;

	private String error;

	public List<ResultadoPlanificacionDTO> getResultados() {
		return resultados;
	}

	public void setResultados(List<ResultadoPlanificacionDTO> resultados) {
		this.resultados = resultados;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
}
