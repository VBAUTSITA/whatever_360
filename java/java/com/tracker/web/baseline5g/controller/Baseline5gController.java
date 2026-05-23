package com.tracker.web.baseline5g.controller;

import org.apache.tomcat.util.http.fileupload.IOUtils;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.tracker.web.seguimiento.model.Seguimiento;

//import org.apache.commons.io.IOUtils;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;


/*
@Controller
@RequestMapping("/baseline")
public class BaselineController {

@Autowired
private BaselineRepositoryGraph01 baselineRepositoryGraph01;

@Autowired
private BaselineRepositoryGraph02 baselineRepositoryGraph02;

@GetMapping("/dash")
public String listarBaselines(Model model) {
model.addAttribute("baselinegraph01", baselineRepositoryGraph01.findAll());
model.addAttribute("baselinegraph02", baselineRepositoryGraph02.findAll());
return "baseline/dashboard";
}
}
*/


@Controller
@RequestMapping("/baseline-graph-5g")
public class Baseline5gController {

	@Autowired
	private Baseline5gRepositoryGraph01 baseline5gRepositoryGraph01;
	
	@Autowired
	private Baseline5gRepositoryGraph02 baseline5gRepositoryGraph02;
	
	@Autowired
	private Baseline5gRepositoryGraph03 baseline5gRepositoryGraph03;	
	
	@Autowired
	private Baseline5gRepositoryGraph04 baseline5gRepositoryGraph04;
	
	@Autowired
	private Baseline5gRepositoryGraph05 baseline5gRepositoryGraph05;	
	
	@Autowired
	private Baseline5gRepositoryGraph06 baseline5gRepositoryGraph06;	
	
	
	@GetMapping("/dash")
	public String listarBaselines(Model model) {		
		return "baseline5gdash/dashboard";	
	}
	
	@GetMapping("/data/graph01")
	@ResponseBody
	public List<Baseline5gGraph01> getBaselineGraph01() {
		return baseline5gRepositoryGraph01.findAll();
	}
	
	@GetMapping("/data/graph02")
	@ResponseBody
	public List<Baseline5gGraph02> getBaselineGraph02() {
		return baseline5gRepositoryGraph02.findAll();
	}
	
	@GetMapping("/data/graph03")
	@ResponseBody
	public List<Baseline5gGraph03> getBaselineGraph03() {
		return baseline5gRepositoryGraph03.findAll();
	}
	
	@GetMapping("/data/graph04")
	@ResponseBody
	public List<Baseline5gGraph04> getBaselineGraph04() {
		return baseline5gRepositoryGraph04.findAll();
	}
	
	@GetMapping("/data/graph05")
	@ResponseBody
	public List<Baseline5gGraph05> getBaselineGraph05() {
		return baseline5gRepositoryGraph05.findAll();
	}
	
	@GetMapping("/data/graph06")
	@ResponseBody
	public List<Baseline5gGraph06> getBaselineGraph06() {
		return baseline5gRepositoryGraph06.findAll();
	}


}