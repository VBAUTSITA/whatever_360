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
}
