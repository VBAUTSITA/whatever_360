package com.tracker.web.seguimiento.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.tracker.web.seguimiento.model.Seguimiento;
import com.tracker.web.seguimiento.service.SeguimientoService;
import com.tracker.web.seguimiento.service.SeguimientoServiceImpl;

@Controller
@RequestMapping("/seguimiento")
public class SeguimientoController {
		

	
	@Autowired
	private SeguimientoServiceImpl seguimientoService;

	@RequestMapping(value = "/list" , method = RequestMethod.GET)
	public ModelAndView getAllSeguimiento() {

		ModelAndView model = new ModelAndView();

		List<Seguimiento> list = seguimientoService.getAllSeguimientos();

		model.addObject("seguimiento_list", list);
		model.setViewName("seguimiento_list");

		return model;
	}

	
	@RequestMapping(value = "/list/hist" , method = RequestMethod.GET)
	public ModelAndView getAllSeguimientoHist() {

		ModelAndView model = new ModelAndView();

		List<Seguimiento> list_hist = seguimientoService.getAllSeguimientosHist();

		model.addObject("seguimiento_list_hist", list_hist);
		model.setViewName("seguimiento_list_hist");

		return model;
	}
	
	
	/*historico*/
	@RequestMapping(value = "/hist/{id}", method = RequestMethod.GET)
	public ModelAndView listSeguimientoHistId(@PathVariable int id) {

		ModelAndView model = new ModelAndView();

		List<Seguimiento> list_hist_id = seguimientoService.findSeguimientoHistById(id);
		
		model.addObject("seguimiento_list_hist_id", list_hist_id);
		model.setViewName("seguimiento_list_hist_id");

		return model;

	}
	
	@RequestMapping(value = "/list/masivo" , method = RequestMethod.GET)
	public ModelAndView getAllSeguimientoMasivo() {

		ModelAndView model = new ModelAndView();

		List<Seguimiento> list = seguimientoService.getAllSeguimientosMasivo();

		model.addObject("seguimiento_list_masivo", list);
		model.setViewName("seguimiento_list_masivo");

		return model;
	}
	
	
	/**/
	
	/*id del historico individual*/
	/*@RequestMapping(value = "/hist/id/{id}", method = RequestMethod.GET)
	public ModelAndView SeguimientoHistIdSolo(@PathVariable int id) {

		ModelAndView model = new ModelAndView();

		Seguimiento seguimiento = seguimientoService.findSeguimientoHistByIdSolo(id);


		List<Seguimiento> list_kpi_id = seguimientoService.kpiListID(id);
		

		model.addObject("seguimiento_list_kpi_id", list_kpi_id);			

		
		model.addObject("seguimiento_hist_id", seguimiento);
		model.setViewName("seguimiento_hist_id");

		return model;

	}*/
	/**/
	
	/*id del historico individual 2G*/
	@RequestMapping(value = "/hist/id/2g/{id}", method = RequestMethod.GET)
	public ModelAndView SeguimientoHistIdSolo2G(@PathVariable int id) {

		ModelAndView model = new ModelAndView();

		
		Seguimiento seguimiento = seguimientoService.findSeguimientoHistByIdSolo(id);/*extrae info del id elemento*/


		List<Seguimiento> list_kpi_id_2g = seguimientoService.kpiListID2G(id);
		
		Seguimiento seguimiento_kpi_2g_maxmin = seguimientoService.kpiListID2GMaxMin(id);
		
		Seguimiento kpi_individual_id_2g = seguimientoService.kpiIndividualID2G(id);

		model.addObject("seguimiento_list_kpi_id_2g", list_kpi_id_2g);		
		
		model.addObject("seguimiento_kpi_id_2g_max_min", seguimiento_kpi_2g_maxmin);

		model.addObject("seguimiento_individual_kpi_id_2g", kpi_individual_id_2g);
		
		model.addObject("seguimiento_hist_id_2g", seguimiento);/*extrae info del id elemento*/
		model.setViewName("seguimiento_hist_id_2g");

		return model;

	}
	/**/
	
	/*id del historico individual 3G*/
	@RequestMapping(value = "/hist/id/3g/{id}", method = RequestMethod.GET)
	public ModelAndView SeguimientoHistIdSolo3G(@PathVariable int id) {

		ModelAndView model = new ModelAndView();

		
		Seguimiento seguimiento = seguimientoService.findSeguimientoHistByIdSolo(id);/*extrae info del id elemento*/


		List<Seguimiento> list_kpi_id_3g = seguimientoService.kpiListID3G(id);
		
		Seguimiento seguimiento_kpi_3g_maxmin = seguimientoService.kpiListID3GMaxMin(id);
		
		Seguimiento kpi_individual_id_3g = seguimientoService.kpiIndividualID3G(id);

		model.addObject("seguimiento_list_kpi_id_3g", list_kpi_id_3g);		
		
		model.addObject("seguimiento_kpi_id_3g_max_min", seguimiento_kpi_3g_maxmin);

		model.addObject("seguimiento_individual_kpi_id_3g", kpi_individual_id_3g);
		
		model.addObject("seguimiento_hist_id_3g", seguimiento);/*extrae info del id elemento*/
		model.setViewName("seguimiento_hist_id_3g");

		return model;

	}
	/**/
	
	/*id del historico individual 4G*/
	@RequestMapping(value = "/hist/id/4g/{id}", method = RequestMethod.GET)
	public ModelAndView SeguimientoHistIdSolo4G(@PathVariable int id) {

		ModelAndView model = new ModelAndView();

		
		Seguimiento seguimiento = seguimientoService.findSeguimientoHistByIdSolo(id);/*extrae info del id elemento*/


		List<Seguimiento> list_kpi_id_4g = seguimientoService.kpiListID4G(id);
		
		Seguimiento seguimiento_kpi_4g_maxmin = seguimientoService.kpiListID4GMaxMin(id);
		
		Seguimiento kpi_individual_id_4g = seguimientoService.kpiIndividualID4G(id);

		model.addObject("seguimiento_list_kpi_id_4g", list_kpi_id_4g);		
		
		model.addObject("seguimiento_kpi_id_4g_max_min", seguimiento_kpi_4g_maxmin);

		model.addObject("seguimiento_individual_kpi_id_4g", kpi_individual_id_4g);
		
		model.addObject("seguimiento_hist_id_4g", seguimiento);/*extrae info del id elemento*/
		model.setViewName("seguimiento_hist_id_4g");

		return model;

	}
	/**/	

	
	@RequestMapping(value = "/update/{id}", method = RequestMethod.GET)
	public ModelAndView updateSeguimiento(@PathVariable int id) {

		ModelAndView model = new ModelAndView();

		Seguimiento seguimiento = seguimientoService.findSeguimientoById(id);

		model.addObject("seguimientoFormUpdate", seguimiento);
		model.setViewName("seguimiento_form_update");

		return model;

	}
	

	
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public ModelAndView addSeguimiento() {

		ModelAndView model = new ModelAndView();

		Seguimiento seguimiento = new Seguimiento();

		model.addObject("seguimientoFormSave", seguimiento);
		model.setViewName("seguimiento_form_save");

		return model;

	}
	

	/*
	 * fue dividido en guarda y actualiza
	 * 
	 * 
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public ModelAndView saveOrUpdate(@ModelAttribute("seguimientoForm") Seguimiento seguimiento) {

		if (seguimiento.getSite_id() != null) {
			seguimientoService.updateSeguimiento(seguimiento);			
		} else {
			seguimientoService.addSeguimiento(seguimiento);
		}
		return new ModelAndView("redirect:/seguimiento/list");

	}
	*/
	
	@RequestMapping(value = "/guarda", method = RequestMethod.POST)
	public ModelAndView guarda(@ModelAttribute("seguimientoFormSave") Seguimiento seguimiento) {

			seguimientoService.addSeguimiento(seguimiento);

		return new ModelAndView("redirect:/seguimiento/list");

	}
	
	
	@RequestMapping(value = "/actualiza", method = RequestMethod.POST)
	public ModelAndView actualiza(@ModelAttribute("seguimientoFormUpdate") Seguimiento seguimiento) {

		seguimientoService.updateSeguimiento(seguimiento);			

		return new ModelAndView("redirect:/seguimiento/redirigiendo");
		

	}


	@RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
	public ModelAndView deleteSeguimiento(@PathVariable("id") int id) {

		seguimientoService.deleteSeguimiento(id);

		return new ModelAndView("redirect:/seguimiento/list");
	}

}
