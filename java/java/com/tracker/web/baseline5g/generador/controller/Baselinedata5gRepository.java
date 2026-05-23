package com.tracker.web.baseline5g.generador.controller;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface Baselinedata5gRepository extends JpaRepository<Baselinedata5g, String> {
List<Baselinedata5g> findByMbts(String mbts);
}