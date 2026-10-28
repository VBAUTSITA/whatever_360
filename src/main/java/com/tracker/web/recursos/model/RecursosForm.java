package com.tracker.web.recursos.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;


public class RecursosForm {
//por algun motivo estio private List<Recursos> recursos; estaba mal
    private List<Recursos> recursos = new ArrayList<>();

    public RecursosForm() {
        // constructor vacío requerido por Spring
    }

    public List<Recursos> getRecursos() {
        return recursos;
    }

    public void setRecursos(List<Recursos> recursos) {
        this.recursos = recursos;
    }
}
