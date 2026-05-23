package com.tracker.web.inventario.stock;

import javax.persistence.*;
import java.util.Date;

@Entity
//@Table(name = "PLANI_INVENTORY_STOCK", uniqueConstraints = {@UniqueConstraint(columnNames = "orden")})
@Table(name = "PLANI_INVENTORY_STOCK")
public class InventarioStock {

@Id
//@GeneratedValue(strategy = GenerationType.IDENTITY)
@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PLANI_INVENT_STOCK_ID_SEQ")
@SequenceGenerator(name = "PLANI_INVENT_STOCK_ID_SEQ", sequenceName = "PLANI_INVENT_STOCK_ID_SEQ", allocationSize = 1)
private Long id;
private String centro;
private String almacen;
private String nombre_centro;
private String denominacion_almcn;
private String material;
private String lote;
private String descrip_material;
private String libre_utilizacion;  //number
private String entregas_liberadas; //number
private String cant_pedido_translado; //number
private String cant_reservada;       //number
private String stock_disponible;      //number
private String stock_real;            //number
private String stock_bloqueado;       //number
private String control_calidad;       //number
private String stock_transito;        //number
private String stock_total;           //number
private String region;
private Date fecha_hora;
private String semana;

@PrePersist
@PreUpdate
public void prePersist() {
this.fecha_hora = new Date();
}

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

public String getLibre_utilizacion() {
	return libre_utilizacion;
}

public void setLibre_utilizacion(String libre_utilizacion) {
	this.libre_utilizacion = libre_utilizacion;
}

public String getEntregas_liberadas() {
	return entregas_liberadas;
}

public void setEntregas_liberadas(String entregas_liberadas) {
	this.entregas_liberadas = entregas_liberadas;
}

public String getCant_pedido_translado() {
	return cant_pedido_translado;
}

public void setCant_pedido_translado(String cant_pedido_translado) {
	this.cant_pedido_translado = cant_pedido_translado;
}

public String getCant_reservada() {
	return cant_reservada;
}

public void setCant_reservada(String cant_reservada) {
	this.cant_reservada = cant_reservada;
}

public String getStock_disponible() {
	return stock_disponible;
}

public void setStock_disponible(String stock_disponible) {
	this.stock_disponible = stock_disponible;
}

public String getStock_real() {
	return stock_real;
}

public void setStock_real(String stock_real) {
	this.stock_real = stock_real;
}

public String getStock_bloqueado() {
	return stock_bloqueado;
}

public void setStock_bloqueado(String stock_bloqueado) {
	this.stock_bloqueado = stock_bloqueado;
}

public String getControl_calidad() {
	return control_calidad;
}

public void setControl_calidad(String control_calidad) {
	this.control_calidad = control_calidad;
}

public String getStock_transito() {
	return stock_transito;
}

public void setStock_transito(String stock_transito) {
	this.stock_transito = stock_transito;
}

public String getStock_total() {
	return stock_total;
}

public void setStock_total(String stock_total) {
	this.stock_total = stock_total;
}

public String getRegion() {
	return region;
}

public void setRegion(String region) {
	this.region = region;
}

public Date getFecha_hora() {
	return fecha_hora;
}

public void setFecha_hora(Date fecha_hora) {
	this.fecha_hora = fecha_hora;
}

public String getSemana() {
	return semana;
}

public void setSemana(String semana) {
	this.semana = semana;
}



}