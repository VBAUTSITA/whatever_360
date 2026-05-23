package com.tracker.web.dashboard.kpi.r01;

public class KpiResponseDTO {
    private String resultTime;
    private String region;
    private String subRegion;
    private String departamento;
    private String provincia;
    private String clusterName;
    private String distrito;
    private String codigoSitio;
    private String enodobName;
    private String sectorName;    
    private String cellname;
    private String carrier;
    private Double kpiValue;
    // Constructor, getters y setters

    public KpiResponseDTO(String resultTime, String region, String subRegion, String departamento, String provincia,
            String clusterName, String distrito, String codigoSitio, String enodobName,
            String sectorName, String cellname, String carrier, Double kpiValue) {
			this.resultTime = resultTime;
			this.region = region;
			this.subRegion = subRegion;
			this.departamento = departamento;
			this.provincia = provincia;
			this.clusterName = clusterName;
			this.distrito = distrito;
			this.codigoSitio = codigoSitio;
			this.enodobName = enodobName;
			this.sectorName = sectorName;
			this.cellname = cellname;
			this.carrier = carrier;
			this.kpiValue = kpiValue;
}



    
    // Getters y setters aquí...

	public String getResultTime() {
		return resultTime;
	}

	public void setResultTime(String resultTime) {
		this.resultTime = resultTime;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getSubRegion() {
		return subRegion;
	}

	public void setSubRegion(String subRegion) {
		this.subRegion = subRegion;
	}

	public String getCodigoSitio() {
		return codigoSitio;
	}

	public void setCodigoSitio(String codigoSitio) {
		this.codigoSitio = codigoSitio;
	}

	public String getCellname() {
		return cellname;
	}

	public void setCellname(String cellname) {
		this.cellname = cellname;
	}

	public String getCarrier() {
		return carrier;
	}

	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}

	public Double getKpiValue() {
		return kpiValue;
	}

	public void setKpiValue(Double kpiValue) {
		this.kpiValue = kpiValue;
	}

	public String getDepartamento() {
		return departamento;
	}

	public void setDepartamento(String departamento) {
		this.departamento = departamento;
	}

	public String getProvincia() {
		return provincia;
	}

	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}

	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	public String getDistrito() {
		return distrito;
	}

	public void setDistrito(String distrito) {
		this.distrito = distrito;
	}

	public String getEnodobName() {
		return enodobName;
	}

	public void setEnodobName(String enodobName) {
		this.enodobName = enodobName;
	}

	public String getSectorName() {
		return sectorName;
	}

	public void setSectorName(String sectorName) {
		this.sectorName = sectorName;
	}

    
    
}

