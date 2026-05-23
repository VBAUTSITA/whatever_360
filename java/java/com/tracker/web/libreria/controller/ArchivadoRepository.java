package com.tracker.web.libreria.controller;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface ArchivadoRepository extends CrudRepository<Archivado, Long> {

}