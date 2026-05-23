package com.tracker.web.inventarioV3.stock;

import javax.persistence.*;
import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

@Entity
//@Table(name = "PLANI_INVENTORY_STOCK", uniqueConstraints = {@UniqueConstraint(columnNames = "orden")})
@Table(name = "PLANI_INVENTORY_STOCK_TEST")
public class TestStock {

@Id
//@GeneratedValue(strategy = GenerationType.IDENTITY)
@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PLANI_INVENTORY_STOCK_TEST_ID_SEQ")
@SequenceGenerator(name = "PLANI_INVENTORY_STOCK_TEST_ID_SEQ", sequenceName = "PLANI_INVENTORY_STOCK_TEST_ID_SEQ", allocationSize = 1)
private Long id;
private String centro;
private String almacen;
private String nombre_centro;
private String denominacion_almcn;
private String material;
private String lote;
private String descrip_material;
private String categoria;
private int stock_real;
//QUE ES PREPERSIST
//@PrePersist
//@PreUpdate
//Getters and setters
public Long getId() {
	return id;
}

public void setId(Long id) {
	this.id = id;
}

public String getCentro() {
	return centro;
}

public void setCentro(String centro) {
	this.centro = centro;
}

public String getAlmacen() {
	return almacen;
}

public void setAlmacen(String almacen) {
	this.almacen = almacen;
}

public String getNombre_centro() {
	return nombre_centro;
}

public void setNombre_centro(String nombre_centro) {
	this.nombre_centro = nombre_centro;
}

public String getDenominacion_almcn() {
	return denominacion_almcn;
}

public void setDenominacion_almcn(String denominacion_almcn) {
	this.denominacion_almcn = denominacion_almcn;
}

public String getMaterial() {
	return material;
}

public void setMaterial(String material) {
	this.material = material;
}

public String getLote() {
	return lote;
}

public void setLote(String lote) {
	this.lote = lote;
}

public String getDescrip_material() {
	return descrip_material;
}

public void setDescrip_material(String descrip_material) {
	this.descrip_material = descrip_material;
}

//nueva columna

public String getCategoria() {
	return categoria;
}

public void setCategoria(String categoria) {
	this.categoria = categoria;
}

public int getStock_real() {
	return stock_real;
}

public void setStock_real(int stock_real) {
	this.stock_real = stock_real;
}

}