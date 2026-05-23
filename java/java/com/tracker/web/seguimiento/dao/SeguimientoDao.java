package com.tracker.web.seguimiento.dao;

import java.util.List;

import com.tracker.web.seguimiento.model.Seguimiento;

public interface SeguimientoDao {
	
	public List<Seguimiento> getAllSeguimientos();
	
	public List<Seguimiento> getAllSeguimientosHist();
	
	public Seguimiento findSeguimientoById(int id);
	
	public List<Seguimiento> getAllSeguimientosMasivo();
	
	public List<Seguimiento> findSeguimientoHistById(int id);
		
	public Seguimiento findSeguimientoHistByIdSolo(int id);
		
	public Seguimiento kpiIndividualID2G(int id);
	
	public List<Seguimiento> kpiListID2G(int id);
	
	public Seguimiento kpiListID2GMaxMin(int id);
	
	public Seguimiento kpiIndividualID3G(int id);
	
	public List<Seguimiento> kpiListID3G(int id);
	
	public Seguimiento kpiListID3GMaxMin(int id);
	
	public Seguimiento kpiIndividualID4G(int id);
	
	public List<Seguimiento> kpiListID4G(int id);
	
	public Seguimiento kpiListID4GMaxMin(int id);
		
	public void addSeguimiento(Seguimiento seguimiento);

	public void updateSeguimiento(Seguimiento seguimiento);

	public void deleteSeguimiento(int id);
}
