package com.tracker.web.mapa.controller;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.springframework.boot.jdbc.DataSourceBuilder;

//VW_PLAN_MAP_SITES_MAS_SECTORES

@Entity
//@Table(name = "vw_smart_opti_mapa_saved_test")
//@Table(name = "opti_mapa_saved_test")
@Table(name = "VW_PLAN_MAP_SITES_MAS_SECTORES")
public class MarkerFull {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
/*ID, NODO, LATITUD_SITIO, LONGITUD_SITIO, TECNOLOGIAS, SECTOR_NAME, SECTOR_ADDRESS, 
 * ENODOB_NAME, ENODOB_ADDRESS, LATITUD_SECTOR, LONGITUD_SECTOR, DESCRIPTION, ALTURA*/
	private Long id;
	private String nodo;
	private String site_name_address;
	private Double latitud_sitio;
	private Double longitud_sitio;
	private String tecnologias;
	private String sector_name;
	private String sector_address;
	private String enodob_name;
	private String enodob_address;
	private String description;
	private String altura;
	
	private Double latitud_sector;
	private Double longitud_sector;
	
	
	 // Getters y setters compactos
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNodo() { return nodo; }
    public void setNodo(String nodo) { this.nodo = nodo; }

    public String getSite_name() { return site_name_address; }
    public void setSite_name(String site_name_address) { this.site_name_address = site_name_address; }
    
    public String getTecnologias() { return tecnologias; }
    public void setTecnologias(String tecnologias) { this.tecnologias = tecnologias; }

    public Double getLatitud_sitio() { return latitud_sitio; }
    public void setLatitud_sitio(Double latitud_sitio) { this.latitud_sitio = latitud_sitio; }

    public Double getLongitud_sitio() { return longitud_sitio; }
    public void setLongitud_sitio(Double longitud_sitio) { this.longitud_sitio = longitud_sitio; }

    public String getSector_name() { return sector_name; }
    public void setSector_name(String sector_name) { this.sector_name = sector_name; }

    public String getSector_address() { return sector_address; }
    public void setSector_address(String sector_address) { this.sector_address = sector_address; }

    public String getEnodb_name() { return enodob_name; }
    public void setEnodb_name(String enodob_name) { this.enodob_name = enodob_name; }

    public String getEnodb_address() { return enodob_address; }
    public void setEnodb_address(String enodob_address) { this.enodob_address = enodob_address; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAltura() { return altura; }
    public void setAltura(String altura) { this.altura = altura; }

    public Double getLatitud_sector() { return latitud_sector; }
    public void setLatitud_sector(Double latitud_sector) { this.latitud_sector = latitud_sector; }

    public Double getLongitud_sector() { return longitud_sector; }
    public void setLongitud_sector(Double longitud_sector) { this.longitud_sector = longitud_sector; }
		
}













