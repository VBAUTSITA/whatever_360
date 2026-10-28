package com.tracker.web.recursos.planner;

import java.util.List;

import com.tracker.web.recursos.model.Recursos;

/**
 * Resultado de autoAsignar. "recursos" es el SET COMPLETO que se pidio
 * (mismos ids que se mandaron), mutado en memoria donde fue posible calcular
 * PCI/RSI - no solo los que cambiaron - para que el controlador pueda
 * volcarlo directo al formulario de /asignar sin tener que recombinar listas.
 * "autoComputadosIds" marca cuales de esos sí se recalcularon recien, para
 * que la plantilla les aplique un estilo visual distinto de "valor manual ya
 * guardado" - la revision humana solo tiene sentido si se nota cual es cual.
 */
public class ResultadoAutoAsignacion {

	private final List<Recursos> recursos;
	private final List<Long> autoComputadosIds;
	private final List<String> omitidos;
	private final int totalExistentes;
	private final int conDatosTrafico;

	public ResultadoAutoAsignacion(List<Recursos> recursos, List<Long> autoComputadosIds,
			List<String> omitidos, int totalExistentes, int conDatosTrafico) {
		this.recursos = recursos;
		this.autoComputadosIds = autoComputadosIds;
		this.omitidos = omitidos;
		this.totalExistentes = totalExistentes;
		this.conDatosTrafico = conDatosTrafico;
	}

	public List<Recursos> getRecursos() {
		return recursos;
	}

	public List<Long> getAutoComputadosIds() {
		return autoComputadosIds;
	}

	public List<String> getOmitidos() {
		return omitidos;
	}

	public int getTotalExistentes() {
		return totalExistentes;
	}

	public int getConDatosTrafico() {
		return conDatosTrafico;
	}
}
