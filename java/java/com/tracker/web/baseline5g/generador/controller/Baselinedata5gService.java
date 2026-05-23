package com.tracker.web.baseline5g.generador.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class Baselinedata5gService {

	@Autowired
	private Baselinedata5gRepository baselinedata5gRepository;
	
	// Procesa el archivo CSV y obtiene datos de la base de datos
	public List<Baselinedata5g> getBaselinedataFromCsv(MultipartFile file) throws IOException {
	List<Baselinedata5g> result = new ArrayList<>();
	try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
	String line;
	
	while ((line = br.readLine()) != null) {
	String mbts = line.trim(); // Asumiendo que el CSV solo tiene un campo por línea
	
	
	if (!mbts.isEmpty()) {
		
	//System.out.println("Consultando mbts: " + mbts);
	//
	List<Baselinedata5g> baselinedataList = baselinedata5gRepository.findByMbts(mbts);
	//System.out.println("Objeto line: " + baselinedataList.toString());
	
	if (baselinedataList != null && !baselinedataList.isEmpty()) {
	for (Baselinedata5g data : baselinedataList) {
	//System.out.println("Agregando a la lista: " + data);
	result.add(data);
	//System.out.println("Objeto devuelto: " + result.toString());
	}
	} else {
	//System.out.println("No se encontraron resultados para: " + mbts);
	}
	}
	}
	}
	return result;
	}
	
	
	
	public void generateCsv(List<Baselinedata5g> baselinedataList) throws IOException {
	    //System.out.println("Exportando " + baselinedataList.size() + " filas");

	    // Procese y exporte los datos
	    exportBaselinedataToCsv(baselinedataList, new FileWriter("baselinedata.csv"));
	}

	// Exporta los datos a un archivo CSV
	
	public void exportBaselinedataToCsv(List<Baselinedata5g> baselinedataList, Writer writer) throws IOException {
		  //System.out.println("Exportando " + baselinedataList.size() + " filas"); // Print list size
		  try (CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(/*"orden", */"levelbs", "mbts", "gnodebfunctionname", "nrducellid", "nrcellid", "clase", "parametro","subparametro","frequencyband" ,"condicional", "valor_baseline", "valor_actual", "script_bs", "estado"))) {
		    for (Baselinedata5g baselinedata : baselinedataList) {
		      csvPrinter.printRecord(		    	  
		    	  //baselinedata.getOrden(),
		    	  baselinedata.getLevelbs(),
		          baselinedata.getMbts(),
		          baselinedata.getGnodebfunctionname(),
		          baselinedata.getNrducellid(),
		          baselinedata.getNrcellid(),
		          baselinedata.getClase(),
		          baselinedata.getParametro(),
		          baselinedata.getSubparametro(),
		          baselinedata.getFrequencyband(),
		          baselinedata.getCondicional(),
		          baselinedata.getValor_baseline(),
		          baselinedata.getValor_actual(),
		          baselinedata.getScript_bs(),
		          baselinedata.getEstado()
		      );
		      //System.out.println("Escribiendo a CSV: " + baselinedata.toString()); // Print object details
		    }
		  }
		}	

}