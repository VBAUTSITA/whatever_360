package com.tracker.web.baseline5g4g.generador.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.tracker.web.baseline.controller.BaselineRepositoryGraph05;
import com.tracker.web.baseline.controller.Baselinegraph05;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;


@Controller
public class Baselinedata5g4gController {

	@Autowired
	private Baselinedata5g4gService baselinedata5g4gService;	
		
	@GetMapping("/baseline-5g-nsa")
	public String index() {
	return "baseline5g4g/generador_bs_5g4g";
	}
	
	@PostMapping("/baseline-5g-nsa/upload")
	public void uploadCsv(@RequestParam("file") MultipartFile file, HttpServletResponse response) {
	try {
	List<Baselinedata5g4g> baselinedataList = baselinedata5g4gService.getBaselinedataFromCsv(file);
	response.setContentType("text/csv");
	response.setHeader("Content-Disposition", "attachment; filename=\"baselinedata.csv\"");
	baselinedata5g4gService.exportBaselinedataToCsv(baselinedataList, new OutputStreamWriter(response.getOutputStream()));
	} catch (IOException e) {
	e.printStackTrace();
	}
	}
	
	@Autowired
	private Baselinesites5g4gRepository baselinesites5g4gRepository;	
	
	@GetMapping("/baseline-5g-nsa/sites")
	@ResponseBody
	public List<Baselinesites5g4g> getBaselinesites() {
		return baselinesites5g4gRepository.findAll();
	}
	
}