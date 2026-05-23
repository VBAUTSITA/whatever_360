package com.tracker.web.baseline5g4g.generador.controller;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface Baselinedata5g4gRepository extends JpaRepository<Baselinedata5g4g, String> {
List<Baselinedata5g4g> findByMbts(String mbts);
}