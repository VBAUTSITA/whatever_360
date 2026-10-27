package com.tracker.web.auditoriared.dashboard;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.tracker.web.auditoriared.SolicitudAuditoria;

public class SolicitudDashboard extends SolicitudAuditoria {
    private Map<String, List<String>> filtros = new LinkedHashMap<>();
    private int pagina = 1;
    private int tamanoPagina = 25;
    private String celdaObjetivo;
    private String tecnologiaObjetivo;
    private int limiteVecinas = 30;
    public Map<String, List<String>> getFiltros() { return filtros; }
    public void setFiltros(Map<String, List<String>> filtros) { this.filtros = filtros; }
    public int getPagina() { return pagina; }
    public void setPagina(int pagina) { this.pagina = pagina; }
    public int getTamanoPagina() { return tamanoPagina; }
    public void setTamanoPagina(int tamanoPagina) { this.tamanoPagina = tamanoPagina; }
    public String getCeldaObjetivo() { return celdaObjetivo; }
    public void setCeldaObjetivo(String celdaObjetivo) { this.celdaObjetivo = celdaObjetivo; }
    public String getTecnologiaObjetivo() { return tecnologiaObjetivo; }
    public void setTecnologiaObjetivo(String tecnologiaObjetivo) { this.tecnologiaObjetivo = tecnologiaObjetivo; }
    public int getLimiteVecinas() { return limiteVecinas; }
    public void setLimiteVecinas(int limiteVecinas) { this.limiteVecinas = limiteVecinas; }
}
