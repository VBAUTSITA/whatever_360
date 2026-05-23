package com.tracker.web.libreria.controller;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.FetchType;
import javax.persistence.CascadeType;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "ARCHIVO_COMENTARIO")
public class ArchivoComentario {
/*
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_COMENTARIO")
    private Long idComentario;
    */
    //NUEVO idComentario
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comentario_seq")
    @SequenceGenerator(
    name = "comentario_seq",
    sequenceName = "ARCHIVO_COMENTARIO_SEQ",
    allocationSize = 1
    )
    @Column(name = "ID_COMENTARIO")
    private Long idComentario;


    @ManyToOne
    @JoinColumn(name = "ID_ARCHIVO", nullable = false)
    private Archivado archivo;

    @ManyToOne
    @JoinColumn(name = "PARENT_ID")
    private ArchivoComentario parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ArchivoComentario> replies;

    @Column(name = "USUARIO", nullable = false)
    private String usuario;

    @Column(name = "NOMBRE_COMPLETO")
    private String nombreCompleto;

   
    @Column(name = "COMENTARIO_TEXTO", nullable = false)
    private String comentarioTexto;

    @Column(name = "FECHA_CREACION")
    private Date fechaCreacion = new Date();

    @Column(name = "activo")
    private Boolean activo = true;

    public Long getIdComentario() { return idComentario; }
    public void setIdComentario(Long idComentario) { this.idComentario = idComentario; }

    public Archivado getArchivo() { return archivo; }
    public void setArchivo(Archivado archivo) { this.archivo = archivo; }

    public ArchivoComentario getParent() { return parent; }
    public void setParent(ArchivoComentario parent) { this.parent = parent; }

    public List<ArchivoComentario> getReplies() { return replies; }
    public void setReplies(List<ArchivoComentario> replies) { this.replies = replies; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getComentarioTexto() { return comentarioTexto; }
    public void setComentarioTexto(String comentarioTexto) { this.comentarioTexto = comentarioTexto; }

    public Date getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(Date fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public boolean getActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}