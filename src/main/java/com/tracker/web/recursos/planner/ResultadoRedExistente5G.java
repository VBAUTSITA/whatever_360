package com.tracker.web.recursos.planner;

import java.util.List;

import com.tracker.web.recursos.planner.dto.CeldaExistenteDTO5G;

/**
 * Resultado de cargar la red 5G existente, con conteos para que el revisor
 * humano pueda detectar un join roto (0 celdas porque la clave de join esta
 * mal) en vez de confundirlo con "no hay celdas cerca" (tambien 0, pero real).
 *
 * MULTI-TECNOLOGIA: clase separada de ResultadoRedExistente (4G) porque la
 * lista interna es de CeldaExistenteDTO5G (bins TA 5G) — los dos tipos de
 * celda existente NO son intercambiables entre motores.
 */
public class ResultadoRedExistente5G {

	private final List<CeldaExistenteDTO5G> celdas;
	private final int totalExistentes;
	private final int conDatosTrafico;

	public ResultadoRedExistente5G(List<CeldaExistenteDTO5G> celdas, int totalExistentes, int conDatosTrafico) {
		this.celdas = celdas;
		this.totalExistentes = totalExistentes;
		this.conDatosTrafico = conDatosTrafico;
	}

	public List<CeldaExistenteDTO5G> getCeldas() {
		return celdas;
	}

	public int getTotalExistentes() {
		return totalExistentes;
	}

	public int getConDatosTrafico() {
		return conDatosTrafico;
	}
}
