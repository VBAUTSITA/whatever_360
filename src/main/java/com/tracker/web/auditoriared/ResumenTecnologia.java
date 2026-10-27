package com.tracker.web.auditoriared;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Que se leyo, que se descarto y que devolvio el motor, para una tecnologia. */
public final class ResumenTecnologia {
    private final String tecnologia;
    private String motor;
    private int filasLeidas;
    private int descartadasSinDatos;
    private int duplicadas;
    private int duplicadasConflictivas;
    private int celdasSinFrecuencia;
    private int taAusente;
    private int taInvalido;
    private int taOverflow;
    private int geometriaSinRadio;
    private int fueraDelCirculo;
    private int celdasEnArea;
    private int celdasEnMargen;
    private int conflictosMotor;
    private int conflictosSoloMargen;
    private int hallazgos;
    private boolean capped;
    private final List<String> cappedTipos = new ArrayList<>();
    private boolean geometriaDisponible;

    ResumenTecnologia(String tecnologia) { this.tecnologia = tecnologia; }

    public String getTecnologia() { return tecnologia; }
    public String getMotor() { return motor; }
    public int getFilasLeidas() { return filasLeidas; }
    public int getDescartadasSinDatos() { return descartadasSinDatos; }
    public int getDuplicadas() { return duplicadas; }
    public int getDuplicadasConflictivas() { return duplicadasConflictivas; }
    public int getCeldasSinFrecuencia() { return celdasSinFrecuencia; }
    public int getTaAusente() { return taAusente; }
    public int getTaInvalido() { return taInvalido; }
    public int getTaOverflow() { return taOverflow; }
    public int getGeometriaSinRadio() { return geometriaSinRadio; }
    void setDuplicadasConflictivas(int n) { duplicadasConflictivas = n; }
    void setCeldasSinFrecuencia(int n) { celdasSinFrecuencia = n; }
    void setTaAusente(int n) { taAusente = n; }
    void setTaInvalido(int n) { taInvalido = n; }
    void setTaOverflow(int n) { taOverflow = n; }
    void setGeometriaSinRadio(int n) { geometriaSinRadio = n; }
    public int getFueraDelCirculo() { return fueraDelCirculo; }
    public int getCeldasEnArea() { return celdasEnArea; }
    public int getCeldasEnMargen() { return celdasEnMargen; }
    public int getConflictosMotor() { return conflictosMotor; }
    public int getConflictosSoloMargen() { return conflictosSoloMargen; }
    public int getHallazgos() { return hallazgos; }
    public boolean isCapped() { return capped; }
    public List<String> getCappedTipos() { return Collections.unmodifiableList(cappedTipos); }
    /** False si el motor no pudo dar la geometria de los sectores (el mapa usa sectores genericos). */
    public boolean isGeometriaDisponible() { return geometriaDisponible; }
    void setGeometriaDisponible(boolean disponible) { this.geometriaDisponible = disponible; }

    void setMotor(String motor) { this.motor = motor; }
    void setFilasLeidas(int n) { this.filasLeidas = n; }
    void setDescartadasSinDatos(int n) { this.descartadasSinDatos = n; }
    void setDuplicadas(int n) { this.duplicadas = n; }
    void setFueraDelCirculo(int n) { this.fueraDelCirculo = n; }
    void setCeldasEnArea(int n) { this.celdasEnArea = n; }
    void setCeldasEnMargen(int n) { this.celdasEnMargen = n; }
    void setConflictosMotor(int n) { this.conflictosMotor = n; }
    void setConflictosSoloMargen(int n) { this.conflictosSoloMargen = n; }
    void setHallazgos(int n) { this.hallazgos = n; }
    void setCapped(boolean capped, List<String> tipos) {
        this.capped = capped;
        this.cappedTipos.clear();
        this.cappedTipos.addAll(tipos);
    }

    ResumenTecnologia copia() {
        ResumenTecnologia r = new ResumenTecnologia(tecnologia);
        r.motor = motor; r.filasLeidas = filasLeidas; r.descartadasSinDatos = descartadasSinDatos;
        r.duplicadas = duplicadas; r.fueraDelCirculo = fueraDelCirculo; r.celdasEnArea = celdasEnArea;
        r.duplicadasConflictivas = duplicadasConflictivas; r.celdasSinFrecuencia = celdasSinFrecuencia;
        r.taAusente = taAusente; r.taInvalido = taInvalido; r.taOverflow = taOverflow; r.geometriaSinRadio = geometriaSinRadio;
        r.celdasEnMargen = celdasEnMargen; r.conflictosMotor = conflictosMotor;
        r.conflictosSoloMargen = conflictosSoloMargen; r.hallazgos = hallazgos;
        r.geometriaDisponible = geometriaDisponible; r.setCapped(capped, cappedTipos);
        return r;
    }
}
