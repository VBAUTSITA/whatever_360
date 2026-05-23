package com.tracker.web.cuentas.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tracker.web.cuentas.dao.CuentasDaoImpl;
import com.tracker.web.cuentas.model.Cuentas;

@Service
public class CuentasServiceImpl implements CuentasService {

	@Autowired
	private CuentasDaoImpl cuentasDao;

	
	@Override
	public void addCuentas(Cuentas cuentas) {
		cuentasDao.addCuentas(cuentas);
	}


}

