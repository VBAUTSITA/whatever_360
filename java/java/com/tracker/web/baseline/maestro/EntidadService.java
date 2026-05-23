package com.tracker.web.baseline.maestro;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class EntidadService {

@Autowired
private EntidadRepository repository;

public List<Entidad> listAll() {
return repository.findAll();
}

public void save(Entidad entidad) {
repository.save(entidad);
}

public Entidad get(Long id) {
return repository.findById(id).orElse(null);
}

public void delete(Long id) {
repository.deleteById(id);
}

public boolean existsByOrden(String orden) {
return repository.existsByOrden(orden);
}

public boolean existsByOrdenAndIdNot(String orden, Long id) {
return repository.existsByOrdenAndIdNot(orden, id);
}

@Transactional
public void deleteAll() {
repository.deleteAllInBatch();
}

@Transactional
public void saveAll(List<Entidad> entidades) {
repository.saveAll(entidades);
}

/*
public void loadFromCsv(MultipartFile file) {
try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
String line;
List<Entidad> entidades = new ArrayList<>();
while ((line = reader.readLine()) != null) {
String[] fields = line.split(",");
if (fields.length == 15) {
Entidad entidad = new Entidad();
entidad.setOrden(fields[0]);
entidad.setLevelBs(fields[1]);
entidad.setClaseMo(fields[2]);
entidad.setParametroId(fields[3]);
entidad.setParameterName(fields[4]);
entidad.setSubParametro(fields[5]);
entidad.setCondicional(fields[6]);
entidad.setBandAll(fields[7]);
entidad.setBand210MHz(fields[8]);
entidad.setBand215MHz(fields[9]);
entidad.setBand2815MHz(fields[10]);
entidad.setBand710MHz(fields[11]);
entidad.setBand715MHz(fields[12]);
entidad.setBand720MHz(fields[13]);
entidad.setScriptBs(fields[14]);
entidad.setFechaActualizada(new Date());
entidades.add(entidad);
}
}
deleteAll();
saveAll(entidades);
} catch (Exception e) {
e.printStackTrace();
}
}
*/

	public void loadFromCsv(MultipartFile file) {
	try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
	String[] fields;
	List<Entidad> entidades = new ArrayList<>();
	boolean firstLine = true;
	while ((fields = reader.readNext()) != null) {
	if (firstLine) {
	firstLine = false;
	if (fields[0].equalsIgnoreCase("orden")) {
	// Saltar encabezado
	continue;
	}
	}
	if (fields.length == 15) {
	Entidad entidad = new Entidad();
	entidad.setOrden(fields[0]);
	entidad.setLevelBs(fields[1]);
	entidad.setClaseMo(fields[2]);
	entidad.setParametroId(fields[3]);
	entidad.setParameterName(fields[4]);
	entidad.setSubParametro(fields[5]);
	entidad.setCondicional(fields[6]);
	entidad.setBandAll(fields[7]);
	entidad.setBand210MHz(fields[8]);
	entidad.setBand215MHz(fields[9]);
	entidad.setBand2815MHz(fields[10]);
	entidad.setBand710MHz(fields[11]);
	entidad.setBand715MHz(fields[12]);
	entidad.setBand720MHz(fields[13]);
	entidad.setScriptBs(fields[14]);
	entidad.setFechaActualizada(new Date());
	entidades.add(entidad);
	} else {
	System.err.println("Línea inválida en CSV: " + String.join(",", fields));
	}
	}
	deleteAll();
	saveAll(entidades);
	} catch (IOException | CsvValidationException e) {
	e.printStackTrace();
	}
	}

}