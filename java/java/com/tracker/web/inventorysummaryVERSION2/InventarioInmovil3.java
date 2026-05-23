package com.tracker.web.inventorysummaryVERSION2;

//import jakarta.persistence.*;
import javax.persistence.*;
import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@Table(name = "PLANI_INVENTARIO_TEST_INMOVIL_COPIA_V2")
public class InventarioInmovil3 {

    @Id
    @Column(name = "SAP_CODE")
    private String sapCode;

    @Column(name = "CATEGORIA")
    private String categoria;

    @Column(name = "STOCK")
    private Long stock;

    @Column(name = "SEMANA")
    private Integer semana;

    @Column(name = "ALARMA_INMOVIL")
    private Integer alarmaInmovil;

    @Column(name = "WOW_PORCENTAJE")
    private Double wowPorcentaje;
    
    @Column(name = "MAIN_CATEGORIA")
    private String mainCategoria;
    
    @Column(name = "ROTACION")
    private Integer rotacion;
    
    @Column(name = "DESCRIPCION_MATERIAL")
    private String descripcionMaterial;

    //descripcion_tecnica
  //descripcion_material
    // ===== getters =====
    public String getSapCode() { return sapCode; }
    public String getCategoria() { return categoria; }
    public String getMainCategoria() { return mainCategoria; }

    public String getDescripcionMaterial() {return descripcionMaterial;}
    
    public Long getStock() { return stock; }
    public Integer getSemana() { return semana; }
    public Integer getAlarmaInmovil() { return alarmaInmovil; }
    public Double getWowPorcentaje() { return wowPorcentaje; }
    
    public Integer getRotacion() {return rotacion;}
    
}




