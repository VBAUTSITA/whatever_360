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

@Controller
@RequestMapping("/mapa")
public class MapController {		
	
		@Autowired
		private MarkerRepository markerRepository;
		
		@Autowired
		private MarkerSitesRepository markerSitesRepository;
		
		@Autowired
		private Marker5GRepository marker5GRepository;
		
		//nueva tabla
		@Autowired
		private PlotConstruccionRepository plotConstruccionRepository;
		
		@Autowired
		private ObjectMapper objectMapper;

	    @GetMapping("/sectores")
	    public String showMap(Model model) throws JsonProcessingException {
	        List<Marker> points = markerRepository.findAll();
	        List<MarkerSites> pointsCapas = markerSitesRepository.findAll();
	        List<Marker5G> points5G = marker5GRepository.findAll();
	        
	        List<PlotConstruccion> plotConstruccion = plotConstruccionRepository.findAll();
	        
	        
	        String pointsJson = objectMapper.writeValueAsString(points);
	        String pointsCapasJson = objectMapper.writeValueAsString(pointsCapas);
	        String points5GJson = objectMapper.writeValueAsString(points5G);
	        
	        String plotConstruccionJson = objectMapper.writeValueAsString(plotConstruccion);
	        

	        model.addAttribute("pointsJson", pointsJson);
	        model.addAttribute("pointsCapasJson", pointsCapasJson);
	        model.addAttribute("points5GJson", points5GJson);
	        
	        model.addAttribute("plotConstruccionJson", plotConstruccionJson);

	        

	        return "coordenadas/mapa_coordenadas";
	    }
		
	
	/*
    @GetMapping("/sectores")
    public String showMap() {
        return "coordenadas/mapa_coordenadas";
    }*/

}