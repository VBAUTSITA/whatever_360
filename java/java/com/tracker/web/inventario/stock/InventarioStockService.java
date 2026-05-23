package com.tracker.web.inventario.stock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.io.*;

@Service
public class InventarioStockService {

@Autowired
private InventarioStockRepository repository;

public List<InventarioStock> listAll() {
return repository.findAll();
}

public void save(InventarioStock inventarioStock) {
repository.save(inventarioStock);
}

public InventarioStock get(Long id) {
return repository.findById(id).orElse(null);
}

public void delete(Long id) {
repository.deleteById(id);
}


@Transactional
public void deleteAll() {
repository.deleteAllInBatch();
}

@Transactional
public void saveAll(List<InventarioStock> inventarioStockes) {
repository.saveAll(inventarioStockes);
}


/*
public void loadFromCsv(MultipartFile file) {
    try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
        String[] fields;
        List<InventarioStock> inventarioStockes = new ArrayList<>();
        boolean firstLine = true;

        while ((fields = reader.readNext()) != null) {
            if (firstLine) {
                firstLine = false;
                if (fields[0].equalsIgnoreCase("id")) {
                    // Saltar encabezado
                    continue;
                }
            }

            if (fields.length == 19) {
                InventarioStock inventarioStock = new InventarioStock();
                inventarioStock.setCentro(fields[0]);
                inventarioStock.setAlmacen(fields[1]);
                inventarioStock.setNombre_centro(fields[2]);
                inventarioStock.setDenominacion_almcn(fields[3]);
                inventarioStock.setMaterial(fields[4]);
                inventarioStock.setLote(fields[5]);
                inventarioStock.setDescrip_material(fields[6]);
                inventarioStock.setLibre_utilizacion(fields[7]);
                inventarioStock.setEntregas_liberadas(fields[8]);
                inventarioStock.setCant_pedido_translado(fields[9]);
                inventarioStock.setCant_reservada(fields[10]);
                inventarioStock.setStock_disponible(fields[11]);
                inventarioStock.setStock_real(fields[12]);
                inventarioStock.setStock_bloqueado(fields[13]);
                inventarioStock.setControl_calidad(fields[14]);
                inventarioStock.setStock_transito(fields[15]);
                inventarioStock.setStock_total(fields[16]);
                inventarioStock.setRegion(fields[17]);
                inventarioStock.setFecha_hora(new Date());
                //inventarioStock.setSemana(fields[18]);
                
                
                // Calcular la semana del año
                LocalDate currentDate = LocalDate.now();
                WeekFields weekFields = WeekFields.of(Locale.getDefault());
                int weekNumber = currentDate.get(weekFields.weekOfWeekBasedYear());
                inventarioStock.setSemana(String.valueOf(weekNumber));
                
                inventarioStockes.add(inventarioStock);
            } else {
                System.err.println("Línea inválida en CSV: " + String.join(",", fields));
            }
        }

        deleteAll();
        saveAll(inventarioStockes);
    } catch (IOException | CsvValidationException e) {
        e.printStackTrace();
    }
}
*/

/*
public void loadFromCsv(MultipartFile file) {
    try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
        String[] fields;
        List<InventarioStock> inventarioStockes = new ArrayList<>();
        boolean firstLine = true;

        while ((fields = reader.readNext()) != null) {
            if (firstLine) {
                firstLine = false;
                if (fields[0].equalsIgnoreCase("id")) {
                    // Saltar encabezado
                    continue;
                }
            }

            if (fields.length == 19) {
                InventarioStock inventarioStock = new InventarioStock();
                inventarioStock.setCentro(fields[0].isEmpty() ? null : fields[0]);
                inventarioStock.setAlmacen(fields[1].isEmpty() ? null : fields[1]);
                inventarioStock.setNombre_centro(fields[2].isEmpty() ? null : fields[2]);
                inventarioStock.setDenominacion_almcn(fields[3].isEmpty() ? null : fields[3]);
                inventarioStock.setMaterial(fields[4].isEmpty() ? null : fields[4]);
                inventarioStock.setLote(fields[5].isEmpty() ? null : fields[5]);
                inventarioStock.setDescrip_material(fields[6].isEmpty() ? null : fields[6]);
                inventarioStock.setLibre_utilizacion(fields[7].isEmpty() ? null : fields[7]);
                inventarioStock.setEntregas_liberadas(fields[8].isEmpty() ? null : fields[8]);
                inventarioStock.setCant_pedido_translado(fields[9].isEmpty() ? null : fields[9]);
                inventarioStock.setCant_reservada(fields[10].isEmpty() ? null : fields[10]);
                inventarioStock.setStock_disponible(fields[11].isEmpty() ? null : fields[11]);
                inventarioStock.setStock_real(fields[12].isEmpty() ? null : fields[12]);
                inventarioStock.setStock_bloqueado(fields[13].isEmpty() ? null : fields[13]);
                inventarioStock.setControl_calidad(fields[14].isEmpty() ? null : fields[14]);
                inventarioStock.setStock_transito(fields[15].isEmpty() ? null : fields[15]);
                inventarioStock.setStock_total(fields[16].isEmpty() ? null : fields[16]);
                inventarioStock.setRegion(fields[17].isEmpty() ? null : fields[17]);
                inventarioStock.setFecha_hora(new Date());
                
                // Calcular la semana del año
                LocalDate currentDate = LocalDate.now();
                WeekFields weekFields = WeekFields.of(Locale.getDefault());
                int weekNumber = currentDate.get(weekFields.weekOfWeekBasedYear());
                inventarioStock.setSemana(String.valueOf(weekNumber));
                
                inventarioStockes.add(inventarioStock);
            } else {
                System.err.println("Línea inválida en CSV: " + String.join(",", fields));
            }
        }

        deleteAll();
        saveAll(inventarioStockes);
    } catch (IOException | CsvValidationException e) {
        e.printStackTrace();
    }
}
*/
/*
public void loadFromCsv(MultipartFile file) {
    try {
        // Leer el archivo CSV original y modificarlo
        List<String[]> modifiedRows = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String[] row;
            while ((row = reader.readNext()) != null) {
                String[] modifiedRow = new String[row.length];
                for (int i = 0; i < row.length; i++) {
                    if (row[i].contains(",")) {
                        modifiedRow[i] = "\"" + row[i] + "\"";
                    } else {
                        modifiedRow[i] = row[i];
                    }
                }
                modifiedRows.add(modifiedRow);
            }
        }

        // Guardar el archivo CSV modificado en un archivo temporal
        File tempFile = File.createTempFile("modified", ".csv");
        try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(new FileOutputStream(tempFile), StandardCharsets.UTF_8))) {
            writer.writeAll(modifiedRows);
        }

        // Leer el archivo CSV modificado
        CSVParser parser = new CSVParserBuilder()
                .withSeparator(',')
                .withQuoteChar('"')
                .build();

        try (CSVReader reader = new CSVReaderBuilder(new InputStreamReader(new FileInputStream(tempFile), StandardCharsets.UTF_8))
                .withCSVParser(parser)
                .build()) {

            String[] fields;
            List<InventarioStock> inventarioStockes = new ArrayList<>();
            boolean firstLine = true;

            while ((fields = reader.readNext()) != null) {
                if (firstLine) {
                    firstLine = false;
                    if (fields[0].equalsIgnoreCase("id")) {
                        // Saltar encabezado
                        continue;
                    }
                }

                if (fields.length == 18) {
                    InventarioStock inventarioStock = new InventarioStock();
                    inventarioStock.setCentro(fields[0].isEmpty() ? null : fields[0]);
                    inventarioStock.setAlmacen(fields[1].isEmpty() ? null : fields[1]);
                    inventarioStock.setNombre_centro(fields[2].isEmpty() ? null : fields[2]);
                    inventarioStock.setDenominacion_almcn(fields[3].isEmpty() ? null : fields[3]);
                    inventarioStock.setMaterial(fields[4].isEmpty() ? null : fields[4]);
                    inventarioStock.setLote(fields[5].isEmpty() ? null : fields[5]);
                    inventarioStock.setDescrip_material(fields[6].isEmpty() ? null : fields[6]);
                    inventarioStock.setLibre_utilizacion(fields[7].isEmpty() ? null : fields[7]);
                    inventarioStock.setEntregas_liberadas(fields[8].isEmpty() ? null : fields[8]);
                    inventarioStock.setCant_pedido_translado(fields[9].isEmpty() ? null : fields[9]);
                    inventarioStock.setCant_reservada(fields[10].isEmpty() ? null : fields[10]);
                    inventarioStock.setStock_disponible(fields[11].isEmpty() ? null : fields[11]);
                    inventarioStock.setStock_real(fields[12].isEmpty() ? null : fields[12]);
                    inventarioStock.setStock_bloqueado(fields[13].isEmpty() ? null : fields[13]);
                    inventarioStock.setControl_calidad(fields[14].isEmpty() ? null : fields[14]);
                    inventarioStock.setStock_transito(fields[15].isEmpty() ? null : fields[15]);
                    inventarioStock.setStock_total(fields[16].isEmpty() ? null : fields[16]);
                    
                    inventarioStock.setFecha_hora(new Date());

                    // Calcular la semana del año
                    LocalDate currentDate = LocalDate.now();
                    WeekFields weekFields = WeekFields.of(Locale.getDefault());
                    int weekNumber = currentDate.get(weekFields.weekOfWeekBasedYear());
                    inventarioStock.setSemana(String.valueOf(weekNumber));

                    inventarioStockes.add(inventarioStock);
                } else {
                    System.err.println("Línea inválida en CSV: " + String.join(",", fields));
                }
            }

            System.out.println("Total de registros a guardar: " + inventarioStockes.size());
            deleteAll();
            saveAll(inventarioStockes);
            System.out.println("Datos guardados exitosamente.");
        }
    } catch (IOException | CsvValidationException e) {
        e.printStackTrace();
    }
}
*/

public void loadFromCsv(MultipartFile file) {
    try {
        // Leer el archivo CSV original y modificarlo
        List<String[]> modifiedRows = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String[] row;
            while ((row = reader.readNext()) != null) {
                modifiedRows.add(row);
            }
        }

        // Guardar el archivo CSV modificado en un archivo temporal
        File tempFile = File.createTempFile("modified", ".csv");
        try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(new FileOutputStream(tempFile), StandardCharsets.UTF_8))) {
            writer.writeAll(modifiedRows);
        }

        // Leer el archivo CSV modificado
        CSVParser parser = new CSVParserBuilder()
                .withSeparator(',')
                .withQuoteChar('"')
                .build();

        try (CSVReader reader = new CSVReaderBuilder(new InputStreamReader(new FileInputStream(tempFile), StandardCharsets.UTF_8))
                .withCSVParser(parser)
                .build()) {

            String[] fields;
            List<InventarioStock> inventarioStockes = new ArrayList<>();
            boolean firstLine = true;

            while ((fields = reader.readNext()) != null) {
                if (firstLine) {
                    firstLine = false;
                    // Ignorar la primera fila (encabezado)
                    continue;
                }

                InventarioStock inventarioStock = new InventarioStock();
                inventarioStock.setCentro(fields[0].isEmpty() ? null : fields[0]);
                inventarioStock.setAlmacen(fields[1].isEmpty() ? null : fields[1]);
                inventarioStock.setNombre_centro(fields[2].isEmpty() ? null : fields[2]);
                inventarioStock.setDenominacion_almcn(fields[3].isEmpty() ? null : fields[3]);
                inventarioStock.setMaterial(fields[4].isEmpty() ? null : fields[4]);
                inventarioStock.setLote(fields[5].isEmpty() ? null : fields[5]);
                inventarioStock.setDescrip_material(fields[6].isEmpty() ? null : fields[6]);
                inventarioStock.setLibre_utilizacion(parseNumber(fields[7]));
                inventarioStock.setEntregas_liberadas(parseNumber(fields[8]));
                inventarioStock.setCant_pedido_translado(parseNumber(fields[9]));
                inventarioStock.setCant_reservada(parseNumber(fields[10]));
                inventarioStock.setStock_disponible(parseNumber(fields[11]));
                inventarioStock.setStock_real(parseNumber(fields[12]));
                inventarioStock.setStock_bloqueado(parseNumber(fields[13]));
                inventarioStock.setControl_calidad(parseNumber(fields[14]));
                inventarioStock.setStock_transito(parseNumber(fields[15]));
                inventarioStock.setStock_total(parseNumber(fields[16]));
                
                inventarioStock.setFecha_hora(new Date());

                // Calcular la semana del año
                LocalDate currentDate = LocalDate.now();
                WeekFields weekFields = WeekFields.of(Locale.getDefault());
                int weekNumber = currentDate.get(weekFields.weekOfWeekBasedYear());
                inventarioStock.setSemana(String.valueOf(weekNumber));

                inventarioStockes.add(inventarioStock);
            }

            System.out.println("Total de registros a guardar: " + inventarioStockes.size());
            deleteAll();
            saveAll(inventarioStockes);
            System.out.println("Datos guardados exitosamente.");
        }
    } catch (IOException | CsvValidationException e) {
        e.printStackTrace();
    }
}

private String parseNumber(String numberStr) {
    if (numberStr == null || numberStr.isEmpty()) {
        return null;
    }
    // Reemplazar comas con vacío y puntos con comas
    return numberStr.replace(",", "").replace(".", ",");
}

}