package com.tracker.web.auditoriared.dashboard;

import java.util.List;
import java.util.Map;
import com.tracker.web.auditoriared.HallazgoAuditoriaDTO;

/** Add a Spring bean implementing this contract to add a filter without changing the controller/UI. */
public interface FiltroDashboard {
    String id();
    void validar(List<String> valores);
    boolean acepta(HallazgoAuditoriaDTO hallazgo, List<String> valores);
    Map<String, Object> describir(List<HallazgoAuditoriaDTO> universo);
}
