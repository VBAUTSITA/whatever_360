package com.tracker.web.recursos.planner;

/** Fallo del motor planner.js (eval, serializacion, o error reportado por el propio JS). */
public class PlannerEngineException extends Exception {

	private static final long serialVersionUID = 1L;

	public PlannerEngineException(String message) {
		super(message);
	}

	public PlannerEngineException(String message, Throwable cause) {
		super(message, cause);
	}
}
