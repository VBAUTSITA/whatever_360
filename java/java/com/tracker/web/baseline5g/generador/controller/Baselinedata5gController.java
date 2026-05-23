package com.tracker.web.baseline5g.generador.controller;

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
public class Baselinedata5gController {

	@Autowired
	private Baselinedata5gService baselinedata5gService;	
		
	@GetMapping("/baseline-5g")
	public String index() {
	return "baseline5g/generador_bs_5g";
	}
	
	@PostMapping("/baseline-5g/upload")
	public void uploadCsv(@RequestParam("file") MultipartFile file, HttpServletResponse response) {
	try {
	List<Baselinedata5g> baselinedataList = baselinedata5gService.getBaselinedataFromCsv(file);
	response.setContentType("text/csv");
	response.setHeader("Content-Disposition", "attachment; filename=\"baselinedata.csv\"");
	baselinedata5gService.exportBaselinedataToCsv(baselinedataList, new OutputStreamWriter(response.getOutputStream()));
	} catch (IOException e) {
	e.printStackTrace();
	}
	}
	
	@Autowired
	private Baselinesites5gRepository baselinesites5gRepository;	
	
	@GetMapping("/baseline-5g/sites")
	@ResponseBody
	public List<Baselinesites5g> getBaselinesites() {
		return baselinesites5gRepository.findAll();
	}
	
}