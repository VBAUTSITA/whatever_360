package com.tracker.web.recursos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tracker.web.recursos.model.Recursos;

public interface RecursosRepository extends  JpaRepository <Recursos, Long >{

	List<Recursos> findByUsuarioIngreso(String name);
    // orden garantizado por ID descendente
    List<Recursos> findAllByOrderByIdDesc();
    List<Recursos> findByUsuarioIngresoOrderByIdDesc(String usuarioIngreso);

    // "solo los últimos" para list y asignar (cambia el 15 al número que quieras)
    List<Recursos> findTop15ByOrderByIdDesc();
}