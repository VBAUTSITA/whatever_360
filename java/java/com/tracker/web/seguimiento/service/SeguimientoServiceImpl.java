package com.tracker.web.seguimiento.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tracker.web.seguimiento.dao.SeguimientoDaoImpl;
import com.tracker.web.seguimiento.model.Seguimiento;

@Service
public class SeguimientoServiceImpl implements SeguimientoService {

	@Autowired
	private SeguimientoDaoImpl seguimientoDao;

	@Override
	public List<Seguimiento> getAllSeguimientos() {

		return seguimientoDao.getAllSeguimientos();
	}

	@Override
	public List<Seguimiento> getAllSeguimientosHist() {

		return seguimientoDao.getAllSeguimientosHist();
	}

	
	
	@Override
	public Seguimiento findSeguimientoById(int id) {

		return seguimientoDao.findSeguimientoById(id);
	}
	
	@Override
	public List<Seguimiento> getAllSeguimientosMasivo() {

		return seguimientoDao.getAllSeguimientosMasivo();
	}
	
	@Override
	public List<Seguimiento> findSeguimientoHistById(int id) {

		return seguimientoDao.findSeguimientoHistById(id);
	}
	
	
	
	public Seguimiento findSeguimientoHistByIdSolo(int id) {

		return seguimientoDao.findSeguimientoHistByIdSolo(id);
	}
	
	public Seguimiento kpiIndividualID2G(int id) {

		return seguimientoDao.kpiIndividualID2G(id);
	}	
	
	@Override
	public List<Seguimiento> kpiListID2G(int id) {

		return seguimientoDao.kpiListID2G(id);
	}

	public Seguimiento kpiListID2GMaxMin(int id) {

		return seguimientoDao.kpiListID2GMaxMin(id);
	}
	
	public Seguimiento kpiIndividualID3G(int id) {

		return seguimientoDao.kpiIndividualID3G(id);
	}	
	
	@Override
	public List<Seguimiento> kpiListID3G(int id) {

		return seguimientoDao.kpiListID3G(id);
	}

	public Seguimiento kpiListID3GMaxMin(int id) {

		return seguimientoDao.kpiListID3GMaxMin(id);
	}
	
	public Seguimiento kpiIndividualID4G(int id) {

		return seguimientoDao.kpiIndividualID4G(id);
	}
	
	@Override
	public List<Seguimiento> kpiListID4G(int id) {

		return seguimientoDao.kpiListID4G(id);
	}
	
	public Seguimiento kpiListID4GMaxMin(int id) {

		return seguimientoDao.kpiListID4GMaxMin(id);
	}
	
		
	@Override
	public void addSeguimiento(Seguimiento seguimiento) {
		seguimientoDao.addSeguimiento(seguimiento);
	}


	@Override
	public void updateSeguimiento(Seguimiento seguimiento) {
		seguimientoDao.updateSeguimiento(seguimiento);
	}


	@Override
	public void deleteSeguimiento(int id) {
		seguimientoDao.deleteSeguimiento(id);
	}

}
