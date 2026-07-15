package com.tracker.web.valorizacion;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Common columns of the three FORM_VALORIZACION_* tables.
 *
 * The agreement asks for ONE TABLE PER SOLUTION TYPE (the template has
 * versions), so instead of copy-pasting three entities we keep the shared
 * shape here as a {@link MappedSuperclass}: each concrete subclass only
 * declares its own @Table name plus its type-specific columns. JPA then
 * creates/maps three independent tables with these columns repeated.
 *
 * Every row is a full SNAPSHOT of a generated valorization: what the RF
 * user typed, what the PAP view answered at that moment, what was derived
 * (antennas, farend) and the resulting totals. Snapshots on purpose — if
 * the PAP data or the price list changes tomorrow, the numbers signed
 * today must stay reproducible.
 *
 * ID strategy: IDENTITY works on MySQL/Postgres/Oracle 12c+. If this app's
 * Oracle needs a sequence, switch to
 * @GeneratedValue(strategy = SEQUENCE, generator = "...") per subclass.
 */
@MappedSuperclass
public abstract class FormValorizacionBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    // ---------- typed by the RF user ----------
    @Column(name = "ID_VALORIZACION")
    private Long idValorizacion;

    @Column(name = "AREA_TOTAL_M2")
    private Double areaTotalM2;

    @Column(name = "CANTIDAD_PISOS")
    private Integer cantidadPisos;

    @Column(name = "PISOS_DETALLE")
    private String pisosDetalle;

    @Column(name = "DEPARTAMENTO")
    private String departamento;

    // ---------- snapshot of the PAP answer ----------
    @Column(name = "NOMBRE_EMPRESA")
    private String nombreEmpresa;

    @Column(name = "DIRECCION")
    private String direccion;

    @Column(name = "LATITUD")
    private Double latitud;

    @Column(name = "LONGITUD")
    private Double longitud;

    // ---------- derived by the platform ----------
    @Column(name = "FAREND")
    private String farend;

    @Column(name = "FAREND_LATITUD")
    private Double farendLatitud;

    @Column(name = "FAREND_LONGITUD")
    private Double farendLongitud;

    @Column(name = "CANTIDAD_ANTENAS")
    private Integer cantidadAntenas;

    @Column(name = "CANTIDAD_SECTORES")
    private Integer cantidadSectores;

    @Column(name = "TOTAL_EQUIPOS_USD", precision = 14, scale = 2)
    private BigDecimal totalEquiposUsd;

    @Column(name = "TOTAL_SERVICIOS_USD", precision = 14, scale = 2)
    private BigDecimal totalServiciosUsd;

    @Column(name = "TOTAL_USD", precision = 14, scale = 2)
    private BigDecimal totalUsd;

    @Column(name = "FECHA_CREACION")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion = new Date();

    // ===== getters / setters =====
    public Long getId() { return id; }

    public Long getIdValorizacion() { return idValorizacion; }
    public void setIdValorizacion(Long idValorizacion) { this.idValorizacion = idValorizacion; }

    public Double getAreaTotalM2() { return areaTotalM2; }
    public void setAreaTotalM2(Double areaTotalM2) { this.areaTotalM2 = areaTotalM2; }

    public Integer getCantidadPisos() { return cantidadPisos; }
    public void setCantidadPisos(Integer cantidadPisos) { this.cantidadPisos = cantidadPisos; }

    public String getPisosDetalle() { return pisosDetalle; }
    public void setPisosDetalle(String pisosDetalle) { this.pisosDetalle = pisosDetalle; }

    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }

    public String getNombreEmpresa() { return nombreEmpresa; }
    public void setNombreEmpresa(String nombreEmpresa) { this.nombreEmpresa = nombreEmpresa; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public Double getLatitud() { return latitud; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }

    public Double getLongitud() { return longitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }

    public String getFarend() { return farend; }
    public void setFarend(String farend) { this.farend = farend; }

    public Double getFarendLatitud() { return farendLatitud; }
    public void setFarendLatitud(Double farendLatitud) { this.farendLatitud = farendLatitud; }

    public Double getFarendLongitud() { return farendLongitud; }
    public void setFarendLongitud(Double farendLongitud) { this.farendLongitud = farendLongitud; }

    public Integer getCantidadAntenas() { return cantidadAntenas; }
    public void setCantidadAntenas(Integer cantidadAntenas) { this.cantidadAntenas = cantidadAntenas; }

    public Integer getCantidadSectores() { return cantidadSectores; }
    public void setCantidadSectores(Integer cantidadSectores) { this.cantidadSectores = cantidadSectores; }

    public BigDecimal getTotalEquiposUsd() { return totalEquiposUsd; }
    public void setTotalEquiposUsd(BigDecimal totalEquiposUsd) { this.totalEquiposUsd = totalEquiposUsd; }

    public BigDecimal getTotalServiciosUsd() { return totalServiciosUsd; }
    public void setTotalServiciosUsd(BigDecimal totalServiciosUsd) { this.totalServiciosUsd = totalServiciosUsd; }

    public BigDecimal getTotalUsd() { return totalUsd; }
    public void setTotalUsd(BigDecimal totalUsd) { this.totalUsd = totalUsd; }

    public Date getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(Date fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
