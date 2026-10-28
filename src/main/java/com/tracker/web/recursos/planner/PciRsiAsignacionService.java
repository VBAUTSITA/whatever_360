package com.tracker.web.recursos.planner;

import java.util.List;

/** Unico punto de entrada del modulo recursos hacia el motor PCI/RSI. */
public interface PciRsiAsignacionService {

	/**
	 * Calcula PCI/RSI para los Recursos indicados que aun no tengan valor.
	 * NO persiste nada: deja los valores calculados en los objetos Recursos
	 * en memoria para que el controlador los muestre en /recursos/asignar y
	 * el usuario los confirme (o corrija) antes de guardar.
	 */
	ResultadoAutoAsignacion autoAsignar(List<Long> recursoIds);
}
