package com.tracker.web.inventorysummaryVERSION2;

import com.tracker.web.inventorysummaryVERSION2.mainCategorySummaryDTO3;
import com.tracker.web.inventorysummaryVERSION2.InventarioInmovil3;
import com.tracker.web.inventorysummaryVERSION2.InventarioInmovilRepository3;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class InventorySummaryService3 {

    private final InventarioInmovilRepository3 repo;

    public InventorySummaryService3(InventarioInmovilRepository3 repo) {
        this.repo = repo;
    }
    
    // MAIN CATEGORY SUMMARY (middle panel)		
    public List<mainCategorySummaryDTO3> buildMainSummary(Integer currentWeek, List<Integer> rotaciones) {

        //List<InventarioInmovil3> rows = repo.findBySemanaAndRotacion(currentWeek, rotaciones);
        List<InventarioInmovil3> rows = repo.findBySemanaAndRotacionIn(currentWeek, rotaciones);
        
        Map<String, List<InventarioInmovil3>> byMainCategory =
                rows.stream().collect(Collectors.groupingBy(InventarioInmovil3::getMainCategoria));

        List<mainCategorySummaryDTO3> result = new ArrayList<>();

        for (Map.Entry<String, List<InventarioInmovil3>> entry : byMainCategory.entrySet()) {

            String mainCategory = entry.getKey();
            List<InventarioInmovil3> items = entry.getValue();

            long totalStock =
                    items.stream()
                            .mapToLong(i -> i.getStock() != null ? i.getStock() : 0)
                            .sum();

            double wow =
                    items.stream()
                            .map(InventarioInmovil3::getWowPorcentaje)
                            .filter(Objects::nonNull)
                            .mapToDouble(Double::doubleValue)
                            .average()
                            .orElse(0.0);
            
            
            long inventarioInmovil = 
            		items.stream()
            				.mapToLong(i -> i.getAlarmaInmovil() != null ? i.getAlarmaInmovil() : 0)
            				.sum();
            						

            result.add(new mainCategorySummaryDTO3(
                    mainCategory,
                    totalStock,
                    Math.round(wow * 100.0) / 100.0,
                    inventarioInmovil

            ));
        }

        return result;
    }

    // CATEGORY TREE (right panel)
    public Map<String, List<CategorySummaryDTO3>> buildCategoryTree(Integer currentWeek, List<Integer> rotaciones) {

        //List<InventarioInmovil3> rows = repo.findBySemanaAndRotacion(currentWeek, rotacion);
        List<InventarioInmovil3> rows = repo.findBySemanaAndRotacionIn(currentWeek, rotaciones);

        // 1) mainCategoria -> rows
        Map<String, List<InventarioInmovil3>> byMain =
                rows.stream().collect(Collectors.groupingBy(InventarioInmovil3::getMainCategoria));

        Map<String, List<CategorySummaryDTO3>> result = new LinkedHashMap<>();

        for (Map.Entry<String, List<InventarioInmovil3>> mainEntry : byMain.entrySet()) {
            String mainCategory = mainEntry.getKey();
            List<InventarioInmovil3> mainItems = mainEntry.getValue();

            // 2) categoria -> rows (inside each main)
            Map<String, List<InventarioInmovil3>> byCategory =
                    mainItems.stream().collect(Collectors.groupingBy(InventarioInmovil3::getCategoria));

            List<CategorySummaryDTO3> catSummaries = new ArrayList<>();

            for (Map.Entry<String, List<InventarioInmovil3>> catEntry : byCategory.entrySet()) {
                String category = catEntry.getKey();
                List<InventarioInmovil3> items = catEntry.getValue();

                long totalStock =
                        items.stream()
                                .mapToLong(i -> i.getStock() != null ? i.getStock() : 0)
                                .sum();

                double wow =
                        items.stream()
                                .map(InventarioInmovil3::getWowPorcentaje)
                                .filter(Objects::nonNull)
                                .mapToDouble(Double::doubleValue)
                                .average()
                                .orElse(0.0);
                long inventarioInmovil = 
                		items.stream()
                				.mapToLong(i -> i.getAlarmaInmovil() != null ? i.getAlarmaInmovil() : 0)
                				.sum();

                catSummaries.add(new CategorySummaryDTO3(
                        category,
                        totalStock,
                        Math.round(wow * 100.0) / 100.0,
                        inventarioInmovil
                ));	
            }

            // Optional: sort categories by total desc (nice UX). Remove if you want raw order.
            catSummaries.sort(Comparator.comparing(CategorySummaryDTO3::getCurrentTotal).reversed());

            result.put(mainCategory, catSummaries);
        }

        return result;
    }
    
    
    public int obtenerInventarioEstancadoCount(Integer semana) {
        List<InventarioInmovil3> lista =
                repo.findInventarioEstancado(semana);
        return lista.size();
    }

    public int obtenerSinStockProlongadoCount(Integer semana) {
        return repo
                .findSinStockProlongado(semana)
                .size();
    }
    
    public int obtenerTendenciaNegativaCount(Integer semana) {
        return repo
                .findTendenciaNegativa(semana)
                .size();
    }
    
    public List<Object[]> obtenerTop5MayorConsumo(Integer semana) {
        return repo
                .findTop5MayorConsumo(semana);
    }
    



        public List<ItemDetalleDTO3> obtenerDetalleMainCategoria(
                Integer semana, List<Integer> rotaciones, String mainCategoria) {

            List<InventarioInmovil3> rows =
                    repo.findDetalleBySemanaRotacionesAndMainCategoriaInmovil(semana, rotaciones, mainCategoria);

            return rows.stream()
                    .map(i -> new ItemDetalleDTO3(
                            i.getMainCategoria(),
                            i.getCategoria(),
                            i.getSapCode(),
                            i.getStock() != null ? i.getStock() : 0L,
                            i.getAlarmaInmovil() != null ? i.getAlarmaInmovil() : 0L,
                            i.getWowPorcentaje() != null ? Math.round(i.getWowPorcentaje() * 100.0) / 100.0 : 0.0,
                            i.getDescripcionMaterial() 		
                            		
                    ))
                    .collect(Collectors.toList());
                    //.toList();
        }

        // Helpers para evitar NPE o si tus campos no existen:
        private String safeString(String s) { return s == null ? "" : s; }
        private String getSku(InventarioInmovil3 i) { 
            // retorna i.getSku() si existe, si no, "".
            return "";
        }
        private String getDescripcion(InventarioInmovil3 i) { 
            // retorna i.getDescripcion() si existe, si no, "".
            return "";
        }
        
        
        public List<InventarioInmovil3> getItemsByMainCategory(
                Integer semana, List<Integer> rotaciones, String mainCategory) {

            return repo.findBySemanaAndRotacionInAndMainCategoria(
                    semana, rotaciones, mainCategory
            );
        }

    
    //MUESTRAN LA LISTA O RESULTADO DE LOS INDOCADORES/ALARMAS:
        public List<InventarioInmovil3> obtenerInventarioEstancado(Integer semana) {
            return repo.findInventarioEstancado(semana);
        }

        public List<InventarioInmovil3> obtenerSinStockProlongado(Integer semana) {
            return repo.findSinStockProlongado(semana);
        }

        public List<InventarioInmovil3> obtenerTendenciaNegativa(Integer semana) {
            return repo.findTendenciaNegativa(semana);
        }
       //listas mini
        
        

    
 // TOTAL SUMMARY
    
/*
    public List<mainCategorySummaryDTO3> buildTotalSummary(Integer currentWeek) {

        List<InventarioInmovil3> rows = repo.findBySemana(currentWeek);
        Map<String, List<InventarioInmovil3>> byMainCategory =
        rows.stream().collect(Collectors.groupingBy(InventarioInmovil3::getMainCategoria));
        List<mainCategorySummaryDTO3> result = new ArrayList<>();

        for (Map.Entry<String, List<InventarioInmovil3>> entry : byMainCategory.entrySet()) {

            String mainCategory = entry.getKey();
            List<InventarioInmovil3> items = entry.getValue();

            long totalStock =
                    items.stream()
                            .mapToLong(i -> i.getStock() != null ? i.getStock() : 0)
                            .sum();

            double wow =
                    items.stream()
                            .map(InventarioInmovil3::getWowPorcentaje)
                            .filter(Objects::nonNull)
                            .mapToDouble(Double::doubleValue)
                            .average()
                            .orElse(0.0);
            
            
            long inventarioInmovil = 
            		items.stream()
            				.mapToLong(i -> i.getAlarmaInmovil() != null ? i.getAlarmaInmovil() : 0)
            				.sum();
            						

            result.add(new mainCategorySummaryDTO3(
                    mainCategory,
                    totalStock,
                    Math.round(wow * 100.0) / 100.0,
                    inventarioInmovil
            ));
        }

        return result;
    }

*/
    
    
}
/*
@Service
public class InventorySummaryService3 {

    private final InventarioInmovilRepository3 repo;

    public InventorySummaryService3(InventarioInmovilRepository3 repo) {
        this.repo = repo;
    }

    public List<mainCategorySummaryDTO3> buildSummary(
            Integer currentWeek,
            Integer alarmWeek
    ) {

        List<InventarioInmovil3> rows = repo.findBySemana(alarmWeek);

        Map<String, List<InventarioInmovil3>> bymainCategory =
                rows.stream().collect(Collectors.groupingBy(InventarioInmovil3::getMainCategoria));

        List<mainCategorySummaryDTO3> result = new ArrayList<>();

        for (Map.Entry<String, List<InventarioInmovil3>> entry : bymainCategory.entrySet()) {

            String mainCategory = entry.getKey();
            List<InventarioInmovil3> items = entry.getValue();

            long totalStock =
                    items.stream()
                            .mapToLong(i -> i.getStock() != null ? i.getStock() : 0)
                            .sum();

            // Use DB-calculated WOW (average per mainCategory)
            double wow =
                    items.stream()
                            .map(InventarioInmovil3::getWowPorcentaje)
                            .filter(Objects::nonNull)
                            .mapToDouble(Double::doubleValue)
                            .average()
                            .orElse(0.0);

            result.add(new mainCategorySummaryDTO3(
                    mainCategory,
                    totalStock,
                    Math.round(wow * 100.0) / 100.0
            ));
        }
        return result;
    }
    // ver como hacer un build count o build summary de alarma
    // 1 de 2 que indique cuantos items estan con alarma o que sume el stock ques
    //esta en alarma por categoria
    
    
}
*/