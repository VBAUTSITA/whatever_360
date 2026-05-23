package com.tracker.web.libreria.controller;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tracker.web.libreria.controller.ArchivoComentario;
import java.util.List;

public interface ArchivoComentarioRepository 
        extends JpaRepository<ArchivoComentario, Long> {

	@Query("SELECT DISTINCT c FROM ArchivoComentario c " +
		       "LEFT JOIN FETCH c.replies " +
		       "WHERE c.archivo.id_libreria = :idArchivo " +
		       "AND c.parent IS NULL " +
		       "AND c.activo = true " +
		       "ORDER BY c.fechaCreacion DESC")
		List<ArchivoComentario> findComentariosPrincipales(@Param("idArchivo") Long idArchivo);
}