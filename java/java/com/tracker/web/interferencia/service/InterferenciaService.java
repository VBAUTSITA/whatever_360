package com.tracker.web.interferencia.service;

import java.util.List;

import com.tracker.web.interferencia.model.Interferencia;

public interface InterferenciaService {
	
	public List<Interferencia> getAllInterferencias();
	
	public List<Interferencia> getAllInterferenciaHist();
	
	public Interferencia findInterferenciaById(int id);
	
	public List<Interferencia> getAllLegalInterferencias();
	
	public List<Interferencia> findInterferenciaHistById(int id);
		
	public Interferencia findInterferenciaHistByIdSolo(int id);
	
	public void addInterferencia(Interferencia interferencia);

	public void updateInterferencia(Interferencia interferencia);

	public void deleteInterferencia(int id);
}
