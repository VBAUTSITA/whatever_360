package com.tracker.web.inventarioSimpleSuma;



import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

@Entity
@Immutable // important if it's a view or read-only table
@Table(name = "PLANI_INVENTARIO_TEST_INMOVIL")  // use your real name
public class InventarioInmovil {

    @Id
    @Column(name = "SAP_CODE")
    private String sapCode;

    @Column(name = "DESCRIPCION_TECNICA")
    private String descripcionTecnica;

    @Column(name = "DESCRIPCION_MATERIAL")
    private String descripcionMaterial;

    @Column(name = "CATEGORIA")
    private String categoria;

    @Column(name = "ROTACION")
    private Integer rotacion;

    @Column(name = "COMENTARIO")
    private String comentario;

    @Column(name = "TIPO")
    private String tipo;

    @Column(name = "STOCK")
    private Long stock;

    @Column(name = "DEVOLUCION")
    private Long devolucion;

    @Column(name = "SEMANA")
    private Integer semana;

    @Column(name = "ALARMA_INMOVIL")
    private Integer alarmaInmovil;

    @Column(name = "WOW_PORCENTAJE")
    private Double wowPorcentaje;

    // getters/setters...
}