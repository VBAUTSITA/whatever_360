package com.tracker.web.recursos.planner;

import java.util.List;

import com.tracker.web.recursos.planner.dto.CeldaExistenteDTO;

/**
 * Resultado de cargar la red existente, con conteos para que el revisor
 * humano pueda detectar un join roto (0 celdas porque la clave de join esta
 * mal) en vez de confundirlo con "no hay celdas cerca" (tambien 0, pero real).
 */
public class ResultadoRedExistente {

	private final List<CeldaExistenteDTO> celdas;
	private final int totalExistentes;
	private final int conDatosTrafico;

	public ResultadoRedExistente(List<CeldaExistenteDTO> celdas, int totalExistentes, int conDatosTrafico) {
		this.celdas = celdas;
		this.totalExistentes = totalExistentes;
		this.conDatosTrafico = conDatosTrafico;
	}

	public List<CeldaExistenteDTO> getCeldas() {
		return celdas;
	}

	public int getTotalExistentes() {
		return totalExistentes;
	}

	public int getConDatosTrafico() {
		return conDatosTrafico;
	}
}
