package com.tracker.web.sectores.sitesnuevos.service;


import com.tracker.web.sectores.sitesnuevos.dao.SectoresSitesDao;
import com.tracker.web.sectores.sitesnuevos.dto.DetalleRow;
import com.tracker.web.sectores.sitesnuevos.dto.ResumenMatriz;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Map;


@Service
public class SectoresSitesService {
private final SectoresSitesDao dao;


public SectoresSitesService(SectoresSitesDao dao) {
this.dao = dao;
}


public List<ResumenMatriz> resumenMatriz() { return dao.resumenMatriz(); }
public Map<String, Integer> sectoresRealesPorPrioridad() { return dao.sectoresRealesPorPrioridad(); }
public List<String> tiposSitio() { return dao.tiposSitio(); }
public List<DetalleRow> detalle(String prioridad, String tipoSitio) { return dao.detalle(prioridad, tipoSitio); }
public List<String> detalleSectoresReales(String prioridad) { return dao.detalleSectoresReales(prioridad); }

public Map<String, Integer> sectoresAsignadosPorPrioridad() {
    return dao.sectoresAsignadosPorPrioridad();
}

public java.util.List<String> detalleSectoresNoAsignados(String prioridad) {
    return dao.detalleSectoresNoAsignados(prioridad);
}

public java.util.List<String> detalleSectoresAsignados(String prioridad) {
    return dao.detalleSectoresAsignados(prioridad);
}


}