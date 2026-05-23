package com.tracker.web.interferencia.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tracker.web.interferencia.dao.InterferenciaDaoImpl;
import com.tracker.web.interferencia.model.Interferencia;

@Service
public class InterferenciaServiceImpl implements InterferenciaService {

	@Autowired
	private InterferenciaDaoImpl interferenciaDao;

	@Override
	public List<Interferencia> getAllInterferencias() {

		return interferenciaDao.getAllInterferencias();
	}

	@Override
	public List<Interferencia> getAllInterferenciaHist() {

		return interferenciaDao.getAllInterferenciaHist();
	}

	
	
	@Override
	public Interferencia findInterferenciaById(int id) {

		return interferenciaDao.findInterferenciaById(id);
	}
	
	
	@Override
	public List<Interferencia> getAllLegalInterferencias() {

		return interferenciaDao.getAllInterferencias();
	}
	
	
	@Override
	public List<Interferencia> findInterferenciaHistById(int id) {

		return interferenciaDao.findInterferenciaHistById(id);
	}
	
	
	
	public Interferencia findInterferenciaHistByIdSolo(int id) {

		return interferenciaDao.findInterferenciaHistByIdSolo(id);
	}

		
	@Override
	public void addInterferencia(Interferencia interferencia) {
		interferenciaDao.addInterferencia(interferencia);
	}


	@Override
	public void updateInterferencia(Interferencia interferencia) {
		interferenciaDao.updateInterferencia(interferencia);
	}


	@Override
	public void deleteInterferencia(int id) {
		interferenciaDao.deleteInterferencia(id);
	}

}
