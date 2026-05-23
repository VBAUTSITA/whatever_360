package com.tracker.web.baseline.controller;

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
@RequestMapping("/baseline")
public class BaselineController {

	@Autowired
	private BaselineRepositoryGraph01 baselineRepositoryGraph01;
	
	@Autowired
	private BaselineRepositoryGraph02 baselineRepositoryGraph02;
	
	@Autowired
	private BaselineRepositoryGraph03 baselineRepositoryGraph03;	
	
	@Autowired
	private BaselineRepositoryGraph04 baselineRepositoryGraph04;
	
	@Autowired
	private BaselineRepositoryGraph05 baselineRepositoryGraph05;	
	
	@GetMapping("/main")
    public String home(@RequestParam(name="name2", required=false) String name, Model model) {
        model.addAttribute("name2", name);
        return "baseline_main";
    }
	
	@GetMapping("/dash")
	public String listarBaselines(Model model) {		
		return "baseline/dashboard";	
	}
	
	@GetMapping("/data/graph01")
	@ResponseBody
	public List<Baselinegraph01> getBaselineGraph01() {
		return baselineRepositoryGraph01.findAll();
	}
	
	@GetMapping("/data/graph02")
	@ResponseBody
	public List<Baselinegraph02> getBaselineGraph02() {
		return baselineRepositoryGraph02.findAll();
	}
	
	@GetMapping("/data/graph03")
	@ResponseBody
	public List<Baselinegraph03> getBaselineGraph03() {
		return baselineRepositoryGraph03.findAll();
	}
	
	@GetMapping("/data/graph04")
	@ResponseBody
	public List<Baselinegraph04> getBaselineGraph04() {
		return baselineRepositoryGraph04.findAll();
	}
	
	@GetMapping("/data/graph05")
	@ResponseBody
	public List<Baselinegraph05> getBaselineGraph05() {
		return baselineRepositoryGraph05.findAll();
	}

}