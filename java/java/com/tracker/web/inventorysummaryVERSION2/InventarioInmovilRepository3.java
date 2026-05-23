package com.tracker.web.inventorysummaryVERSION2;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import com.tracker.web.inventorysummary.InventarioInmovil2;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface InventarioInmovilRepository3
        extends JpaRepository<InventarioInmovil3, String> {

    List<InventarioInmovil3> findBySemana(Integer semana);

	List<InventarioInmovil3> findBySemanaAndRotacionIn(Integer semana, List<Integer> rotaciones);
	
	/*@Query(value ="SELECT * FROM PLANI_INVENTARIO_TEST_INMOVIL_COPIA r WHERE " +
				"LOWER(r.CATEGORIA) LIKE LOWER('%' || :searchTerm || '%') OR " + 
				"LOWER(CAST(r.SAP_CODE AS VARCHAR2(30))) LIKE LOWER('%' || :searchTerm || '%') OR "+
				"LOWER(r.DESCRIPCION_MATERIAL) LIKE LOWER('%' || :searchTerm || '%')" +
				"order by SAP_CODE asc", nativeQuery = true)*/
	
    /*@Query(value = "SELECT t.* FROM PLANI_INVENTARIO_TEST_INMOVIL_COPIA_V2 t WHERE" +
    		" t.rotacion = 0 AND t.stock > 0 AND " + 
    		"t.semana = :semana" +
    		"AND t.semana = :semana AND " +
    		" NOT EXISTS " +
    		"( SELECT 1 FROM PLANI_INVENTARIO_TEST_INMOVIL_COPIA_V2 t2 WHERE t2.sap_code = t.sap_code AND "+
    		" t2.semana BETWEEN :semana - 3 AND :semana AND "+
    		" t2.stock <> t.stock )",nativeQuery = true)*/
    
	@Query(
		    value =
		        "SELECT t.*\n" +
		        "FROM PLANI_INVENTARIO_TEST_INMOVIL_COPIA_V2 t\n" +
		        "WHERE t.rotacion = 0\n" +
		        "  AND t.stock > 0\n" +
		        "  AND t.semana = :semana\n" +
		        "  AND NOT EXISTS (\n" +
		        "      SELECT 1\n" +
		        "      FROM PLANI_INVENTARIO_TEST_INMOVIL_COPIA_V2 t2\n" +
		        "      WHERE t2.sap_code = t.sap_code\n" +
		        "        AND t2.semana BETWEEN :semana - 3 AND :semana\n" +
		        "        AND t2.stock <> t.stock\n" +
		        "  )",
		    nativeQuery = true
		)
    List<InventarioInmovil3> findInventarioEstancado(@Param("semana") Integer semana);


	@Query(
		    value =
		        "SELECT t.*\n" +
		        "FROM PLANI_INVENTARIO_TEST_INMOVIL_COPIA_V2 t\n" +
		        "WHERE t.rotacion = 0\n" +
		        "  AND t.stock = 0\n" +
		        "  AND t.semana = :semana\n" +
		        "  AND NOT EXISTS (\n" +
		        "      SELECT 1\n" +
		        "      FROM PLANI_INVENTARIO_TEST_INMOVIL_COPIA_V2 t2\n" +
		        "      WHERE t2.sap_code = t.sap_code\n" +
		        "        AND t2.semana BETWEEN :semana - 3 AND :semana\n" +
		        "        AND t2.stock <> 0\n" +
		        "  )",
		    nativeQuery = true
		)
		List<InventarioInmovil3> findSinStockProlongado(@Param("semana") Integer semana);
              
	
	
	      @Query(
	    		    value =
	    		        "SELECT t.*\n" +
	    		        "FROM PLANI_INVENTARIO_TEST_INMOVIL_COPIA_V2 t\n" +
	    		        "WHERE t.semana = :semana\n" +
	    		        "  AND EXISTS (\n" +
	    		        "      SELECT 1\n" +
	    		        "      FROM PLANI_INVENTARIO_TEST_INMOVIL_COPIA_V2 t1\n" +
	    		        "      JOIN PLANI_INVENTARIO_TEST_INMOVIL_COPIA_V2 t2\n" +
	    		        "        ON t1.sap_code = t2.sap_code\n" +
	    		        "      WHERE t1.sap_code = t.sap_code\n" +
	    		        "        AND t1.semana = :semana - 1\n" +
	    		        "        AND t2.semana = :semana - 2\n" +
	    		        "        AND t2.stock > t1.stock\n" +
	    		        "        AND t1.stock > t.stock\n" +
	    		        "  )",
	    		    nativeQuery = true
	    		)
	    		List<InventarioInmovil3> findTendenciaNegativa(@Param("semana") Integer semana);	
		      
	      @Query(
	    		    value =
	    		        "SELECT t.sap_code,\n" +
	    		        "       (t.stock - t2.stock) AS delta\n" +
	    		        "FROM PLANI_INVENTARIO_TEST_INMOVIL_COPIA_V2 t\n" +
	    		        "JOIN PLANI_INVENTARIO_TEST_INMOVIL_COPIA_V2 t2\n" +
	    		        "  ON t.sap_code = t2.sap_code\n" +
	    		        "WHERE t.semana = :semana\n" +
	    		        "  AND t2.semana = :semana - 1\n" +
	    		        "ORDER BY delta ASC\n" +
	    		        "FETCH FIRST 5 ROWS ONLY",
	    		    nativeQuery = true
	    		)
	    		List<Object[]> findTop5MayorConsumo(@Param("semana") Integer semana);
		
	    		
	    		
	    		
	    		// En tu InventarioInmovilRepository3
	    		// Ajusta el nombre de la entidad/campos según tus reales:
	    		@Query("SELECT i FROM InventarioInmovil3 i " +
	    			       "WHERE i.semana = :semana " +
	    			       "AND i.rotacion IN (:rotaciones) " +
	    			       "AND i.mainCategoria = :mainCategoria " +
	    			       "AND i.alarmaInmovil IS NOT NULL AND i.alarmaInmovil > 0")
	    			List<InventarioInmovil3> findDetalleBySemanaRotacionesAndMainCategoriaInmovil(
	    			        @Param("semana") Integer semana,
	    			        @Param("rotaciones") List<Integer> rotaciones,
	    			        @Param("mainCategoria") String mainCategoria
	    			);
	    		
	    		
	    		List<InventarioInmovil3> findBySemanaAndRotacionInAndMainCategoria(
	    		        Integer semana,
	    		        List<Integer> rotacion,
	    		        String mainCategoria
	    		);
	    		
	
	}
