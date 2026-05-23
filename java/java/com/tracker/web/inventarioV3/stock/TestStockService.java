package com.tracker.web.inventarioV3.stock;

import com.tracker.web.inventarioV3.stock.TestStock;
import com.tracker.web.inventarioV3.stock.TestStockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
public class TestStockService {
	private final TestStockRepository repository;
	
	
	public List<TestStock> listAll() {
		return repository.findAll();
		}
	

public Page<TestStock> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }


    public TestStockService(TestStockRepository repository) {
        this.repository = repository;
    }
/*
    public List<TestStock> findAll() {
        return repository.findAll();
    }*/		
    
    //miercoles 21 
    public TestStock findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));
    }
    // Verificar existencia (útil para el buscador)
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }

    
    @Transactional
    public void updateStock(Long id, int  newStock) {
        TestStock item = findById(id);
        //item.setStock(newStock);
        item.setStock_real(newStock);
        // no save() needed — JPA dirty checking
    }
    
    // Actualizar stock_real
    @Transactional
    public void updateStockReal(Long id, int newStock) {
        TestStock item = findById(id);
        item.setStock_real(newStock);
        // JPA dirty checking -> no se necesita save()
    }
    
    
    /*ACTUALIZAR LOTE
    @Transactional
    public void updateLote(Long id, String newLote) {
        TestStock item = findById(id);
        //item.setStock(newStock);
        item.setLote(newLote);
        // no save() needed — JPA dirty checking
    }*/
    
    //miercoles 21 anterior
    /*
    @Transactional
    public void updateStock(Long id, Integer newStock) {
        TestStock item = findById(id);
        //item.setStock(newStock);
        item.setStock_total(newStock);
        // no save() needed — JPA dirty checking
    }*/
}
