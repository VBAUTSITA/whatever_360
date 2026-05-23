package com.tracker.web.baseline.generador.controller;

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
public class BaselinedataController {

	@Autowired
	private BaselinedataService baselinedataService;	
		
	@GetMapping("/baselinedata")
	public String index() {
	return "baselinedata/baselinedata";
	}
	
	@PostMapping("/upload")
	public void uploadCsv(@RequestParam("file") MultipartFile file, HttpServletResponse response) {
	try {
	List<Baselinedata> baselinedataList = baselinedataService.getBaselinedataFromCsv(file);
	response.setContentType("text/csv");
	response.setHeader("Content-Disposition", "attachment; filename=\"baselinedata.csv\"");
	baselinedataService.exportBaselinedataToCsv(baselinedataList, new OutputStreamWriter(response.getOutputStream()));
	} catch (IOException e) {
	e.printStackTrace();
	}
	}
	
	@Autowired
	private BaselinesitesRepository baselinesitesRepository;	
	
	@GetMapping("/baselinesites/sites")
	@ResponseBody
	public List<Baselinesites> getBaselinesites() {
		return baselinesitesRepository.findAll();
	}
	
}