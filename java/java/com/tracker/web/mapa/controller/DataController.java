package com.tracker.web.mapa.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DataController {

    @Autowired
    private MarkerRepository markerRepository;

    @Autowired
    private MarkerSitesRepository markerSitesRepository;
    
    @Autowired
    private MarkerFullRepository markerFullRepository;

    @GetMapping("/points")
    public List<Marker> getPoints() {
        return markerRepository.findAll();
    }

    @GetMapping("/pointsCapas")
    public List<MarkerSites> getPointsCapas() {
        return markerSitesRepository.findAll();
    }
    
    @GetMapping("/pointsFull")
    public List<MarkerFull> getPointsFull() {
        return markerFullRepository.findAll();
    }
    
}