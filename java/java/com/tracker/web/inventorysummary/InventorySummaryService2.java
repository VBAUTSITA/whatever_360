package com.tracker.web.inventorysummary;

import com.tracker.web.inventorysummary.CategorySummaryDTO2;
import com.tracker.web.inventorysummary.InventarioInmovil2;
import com.tracker.web.inventorysummary.InventarioInmovilRepository2;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class InventorySummaryService2 {

    private final InventarioInmovilRepository2 repo;

    public InventorySummaryService2(InventarioInmovilRepository2 repo) {
        this.repo = repo;
    }

    public List<CategorySummaryDTO2> buildSummary(
            Integer currentWeek,
            Integer alarmWeek
    ) {

        List<InventarioInmovil2> rows = repo.findBySemana(currentWeek);

        Map<String, List<InventarioInmovil2>> byCategory =
                rows.stream().collect(Collectors.groupingBy(InventarioInmovil2::getCategoria));

        List<CategorySummaryDTO2> result = new ArrayList<>();

        for (Map.Entry<String, List<InventarioInmovil2>> entry : byCategory.entrySet()) {

            String category = entry.getKey();
            List<InventarioInmovil2> items = entry.getValue();

            long totalStock =
                    items.stream()
                            .mapToLong(i -> i.getStock() != null ? i.getStock() : 0)
                            .sum();

            // Use DB-calculated WOW (average per category)
            double wow =
                    items.stream()
                            .map(InventarioInmovil2::getWowPorcentaje)
                            .filter(Objects::nonNull)
                            .mapToDouble(Double::doubleValue)
                            .average()
                            .orElse(0.0);

            result.add(new CategorySummaryDTO2(
                    category,
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
