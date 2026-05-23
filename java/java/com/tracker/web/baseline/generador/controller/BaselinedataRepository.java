package com.tracker.web.baseline.generador.controller;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BaselinedataRepository extends JpaRepository<Baselinedata, String> {
List<Baselinedata> findByMbts(String mbts);
}