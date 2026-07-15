package com.tracker.web.valorizacion;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Snapshot table for INDOOR (solución BOOKRRU) valorizations.
 * Antennas are derived from the area: ceil(area / m2-per-bookrru).
 * All columns come from {@link FormValorizacionBase}.
 */
@Entity
@Table(name = "FORM_VALORIZACION_INDOOR")
public class FormValorizacionIndoor extends FormValorizacionBase {
}
