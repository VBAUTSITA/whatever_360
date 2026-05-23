package com.tracker.web.datafillbook;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
interface DF4GDatoGeneralRepository extends JpaRepository<DF4GDatoGeneral, Long> {

    List<DF4GDatoGeneral> findBySiteNameContainingIgnoreCaseAndEliminado(
            String siteName, Integer eliminado);

    List<DF4GDatoGeneral> findBySiteCodeContainingIgnoreCaseAndEliminado(
            String siteCode, Integer eliminado);

    List<DF4GDatoGeneral> findByEliminado(Integer eliminado);
}

@Repository
interface DF4GBookGeneralRepository extends JpaRepository<DF4GBookGeneral, Long> {

    Optional<DF4GBookGeneral> findByIdDataField(Long idDataField);

    boolean existsByIdDataField(Long idDataField);
}

@Repository
interface DF4GBookTarjetaRepository extends JpaRepository<DF4GBookTarjeta, Long> {

    List<DF4GBookTarjeta> findByIdBookGeneralOrderBySlotAsc(Long idBookGeneral);

    boolean existsByIdBookGeneralAndSlot(Long idBookGeneral, Integer slot);
}

@Repository
interface DF4GBookRruRepository extends JpaRepository<DF4GBookRru, Long> {

    List<DF4GBookRru> findByIdBookGeneralOrderByIdBookRruAsc(Long idBookGeneral);

    boolean existsByIdBookGeneralAndSrn(Long idBookGeneral, Integer srn);
}

@Repository
interface DF4GBookVinculoRepository extends JpaRepository<DF4GBookVinculo, Long> {

    List<DF4GBookVinculo> findByIdBookGeneralOrderByLocalcellidAsc(Long idBookGeneral);

    boolean existsByIdBookGeneralAndLocalcellid(Long idBookGeneral, Integer localcellid);
}

@Repository
interface DF4GDatoDetalleRepository extends JpaRepository<DF4GDatoDetalle, Long> {

    @Query(value =
        "SELECT DISTINCT s.localcellid, c.carrier, s.sector " +
        "FROM df4g_dato_detalle d " +
        "INNER JOIN df_sector  s ON d.id_sector  = s.id " +
        "INNER JOIN df_carrier c ON d.id_carrier = c.id " +
        "WHERE d.id_data_field = :idDataField " +
        "  AND d.eliminado = 0 " +
        "  AND s.tecnologia = '4G' " +
        "  AND s.localcellid IS NOT NULL " +
        "ORDER BY s.localcellid",
        nativeQuery = true)
    List<Object[]> findLocalcellidsForDataField(@Param("idDataField") Long idDataField);

    @Query(value =
        "SELECT d.cellname, " +
        "       d.lte_band, " +
        "       c.dlearfcn, " +
        "       bw.abreviatura_config, " +
        "       s.cell_id, " +
        "       s.localcellid, " +
        "       d.pci, " +
        "       d.prach, " +
        "       tl.descripcion       AS tipo_lte_desc, " +
        "       cp.descripcion       AS cpri_desc, " +
        "       tx.cod_txrxmode, " +
        "       d.rs_power, " +
        "       d.id_carrier " +
        "FROM df4g_dato_detalle d " +
        "LEFT JOIN df_sector           s  ON d.id_carrier = s.id_carrier AND d.id_sector = s.id " +
        "LEFT JOIN df_dlearfcn_freqband c  ON d.id_earfcn = c.id AND c.tecnologia = '4G' " +
        "LEFT JOIN df_bw               bw ON d.id_bw = bw.id " +
        "LEFT JOIN df4g_dato_general   g  ON d.id_data_field = g.id_data_field " +
        "LEFT JOIN df_tipo_lte         tl ON g.id_tipo_lte = tl.id " +
        "LEFT JOIN df_cpricompresion   cp ON d.id_cpricompresion = cp.id " +
        "LEFT JOIN df_txrx_mode        tx ON d.id_txrx_mode = tx.id " +
        "WHERE d.id_data_field = :idDataField " +
        "  AND d.eliminado = 0 " +
        // ── REMOVED: AND d.id_carrier IN (3, 4) ──────────────────────────────
        "ORDER BY s.localcellid",
        nativeQuery = true)
    List<Object[]> findCellDataForIntegracionScript(@Param("idDataField") Long idDataField);

    @Query(value =
        "SELECT s.localcellid, " +
        "       c.carrier, " +
        "       bl.base_col_1, " +
        "       bl.base_col_2, " +
        "       bl.orden " +
        "FROM df4g_dato_detalle   d " +
        "INNER JOIN df_sector         s  ON d.id_carrier = s.id_carrier AND d.id_sector = s.id " +
        "INNER JOIN df_carrier        c  ON d.id_carrier = c.id " +
        "INNER JOIN df4g_book_general bg ON d.id_data_field = bg.id_data_field " +
        "INNER JOIN df_book_baseline_4g bl ON c.carrier = bl.carrier " +
        "WHERE d.id_data_field = :idDataField " +
        "  AND d.eliminado = 0 " +
        // ── REMOVED: AND c.carrier IN ('C2', 'C3') ───────────────────────────
     // BEFORE "ORDER BY bl.carrier, bl.orden"  // AFTER "ORDER BY bl.orden, s.localcellid"
        "ORDER BY bl.orden, s.localcellid",
        
        
        nativeQuery = true)
    List<Object[]> findBaselineDataForScript(@Param("idDataField") Long idDataField);

    //MISSING SCRIPTS:
    
   /* @Query(value = """
    	    SELECT SCRIPT
    	    FROM   df4g_lte_anr_neigh_b_source1
    	    WHERE  id_data_field = :id
    	    ORDER BY localcellid, cellreselpriority DESC
    	    """, nativeQuery = true)
    	List<String> findSource1Scripts(@Param("id") Long idDataField);*/
    @Query(value =
    	    "SELECT SCRIPT " +
    	    "FROM df4g_lte_anr_neigh_b_source1 " +
    	    "WHERE id_data_field = :id " +
    	    "ORDER BY localcellid, cellreselpriority DESC",
    	    nativeQuery = true)
    	List<String> findSource1Scripts(@Param("id") Long idDataField);


    /*	@Query(value = """
    	    SELECT SCRIPT
    	    FROM   df4g_lte_anr_neigh_b_source2
    	    WHERE  id_data_field = :id
    	    ORDER BY localcellid, cellid
    	    """, nativeQuery = true)
    	List<String> findSource2Scripts(@Param("id") Long idDataField);*/
    @Query(value =
    	    "SELECT SCRIPT " +
    	    "FROM df4g_lte_anr_neigh_b_source2 " +
    	    "WHERE id_data_field = :id " +
    	    "ORDER BY localcellid, cellid",
    	    nativeQuery = true)
    	List<String> findSource2Scripts(@Param("id") Long idDataField);


    /*	@Query(value = """
    	    SELECT SCRIPT
    	    FROM   df4g_lte_anr_neigh_b_source3
    	    WHERE  id_data_field = :id
    	    ORDER BY local_cell_id, cell_id
    	    """, nativeQuery = true)
    	List<String> findSource3Scripts(@Param("id") Long idDataField);*/
    
    @Query(value =
    	    "SELECT SCRIPT " +
    	    "FROM df4g_lte_anr_neigh_b_source3 " +
    	    "WHERE id_data_field = :id " +
    	    "ORDER BY local_cell_id, cell_id",
    	    nativeQuery = true)
    	List<String> findSource3Scripts(@Param("id") Long idDataField);
    
    
    //-------------------------------------------
    //------- LO QUE FALTABA DE ADYACENCIAS-------
    
    @Query(value =
    	    "SELECT UTRANNFREQ " +
    	    "FROM df4g_lte_add_utranfreq " +
    	    "WHERE id_data_field = :id " +
    	    "ORDER BY utrandlarfcn, carrier, sector, orden",
    	    nativeQuery = true)
    	List<String> findUtranFreqScripts(@Param("id") Long idDataField);

    	@Query(value =
    	    "SELECT SCRIPT " +
    	    "FROM df4g_utran_external_cell " +
    	    "WHERE id_data_field = :id " +
    	    "ORDER BY carrier, sector_name",
    	    nativeQuery = true)
    	List<String> findUtranExternalCellScripts(@Param("id") Long idDataField);

    	@Query(value =
    	    "SELECT SCRIPT " +
    	    "FROM df4g_lte_utran_ncell " +
    	    "WHERE id_data_field = :id " +
    	    "ORDER BY cell_name_3g, carrier, sector",
    	    nativeQuery = true)
    	List<String> findUtranNcellScripts(@Param("id") Long idDataField);

    	@Query(value =
    	    "SELECT SCRIPT " +
    	    "FROM df4g_umts_ucellnfreqprioinfo " +
    	    "WHERE id_data_field = :id " +
    	    "ORDER BY orden, carrier, cellid",
    	    nativeQuery = true)
    	List<String> findUcellnfreqScripts(@Param("id") Long idDataField);

    	@Query(value =
    	    "SELECT " +
    	    "'ADD ULTECELL:LOGICRNCID=' || b.rncid " +
    	    "|| ',LTECELLINDEX=' || a.lte_cell_index " +
    	    "|| ',LTECELLNAME=\"' || a.lte_cell_name || '\"' " +
    	    "|| ',EUTRANCELLID=' || a.lte_cell_identity " +
    	    "|| ',MCC=\"716\",MNC=\"10\"' " +
    	    "|| ',TAC=' || a.tac " +
    	    "|| ',CNOPGRPINDEX=' || a.operator_group_index " +
    	    "|| ',CELLPHYID=' || a.lte_pci " +
    	    "|| ',LTEBAND=' || a.lte_band " +
    	    "|| ',LTEARFCN=' || a.lte_dl_arfcn " +
    	    "|| '; {' || a.rnc_name || '}' AS script " +
    	    "FROM df4g_lte_cellindex a " +
    	    "LEFT JOIN tm_conf_actual_celda_3g b ON a.rnc_name = b.central " +
    	    "WHERE a.id_data_field = :id " +
    	    "AND a.configurado IS NULL " +
    	    "ORDER BY a.lte_cell_name",
    	    nativeQuery = true)
    	List<String> findCellIndexScripts(@Param("id") Long idDataField);

    	@Query(value =
    	    "SELECT " +
    	    "'ADD ULTENCELL: RNCID=' || rnc_id " +
    	    "|| ', CELLID=' || cell_id " +
    	    "|| ', LTECELLINDEX=' || lte_cell_index " +
    	    "|| '; {' || rnc_name || '}' AS script " +
    	    "FROM df4g_3g_to_4g " +
    	    "WHERE id_data_field = :id " +
    	    "ORDER BY orden, cell_name_3g, lte_cell_index",
    	    nativeQuery = true)
    	List<String> find3gTo4gScripts(@Param("id") Long idDataField);
    	
    	//-------------------------------------------
        //------- LO QUE FALTABA DE 5G NSA-------
    	
    	@Query(value =
    		    "SELECT 'ADD NRMFBIFREQ:DLARFCN=' || downlink_arfcn " +
    		    "|| ',FREQUENCYBAND=' || cod_freq_band " +
    		    "|| '; {' || mbts || '}' AS script " +
    		    "FROM df4g_4g_nrmfbifreq " +
    		    "WHERE id_data_field = :id",
    		    nativeQuery = true)
    		List<String> findNrmfbifreqScripts(@Param("id") Long idDataField);

    		@Query(value =
    		    "SELECT 'ADD NRSCGFREQCONFIG:PCCDLEARFCN=' || pcc_dl_earfcn " +
    		    "|| ',SCGDLARFCN=' || scg_dl_arfcn " +
    		    "|| ',SCGDLARFCNPRIORITY=' || scg_dl_arfcn_priority " +
    		    "|| ',NSADCB1THLDRSRP=-105; {' || mbts || '}' AS script " +
    		    "FROM df4g_4g_nrscgfreqconfig " +
    		    "WHERE id_data_field = :id",
    		    nativeQuery = true)
    		List<String> findNrscgFreqScripts(@Param("id") Long idDataField);

    		@Query(value =
    		    "SELECT 'ADD NRNFREQ:LOCALCELLID=' || local_cell_id " +
    		    "|| ',DLARFCN=' || downlink_arfcn " +
    		    "|| ',ULARFCNCONFIGIND=NOT_CFG; {' || mbts || '}' AS script " +
    		    "FROM df4g_4g_nrnfreq " +
    		    "WHERE id_data_field = :id",
    		    nativeQuery = true)
    		List<String> findNrnfreqScripts(@Param("id") Long idDataField);

    		@Query(value =
    		    "SELECT 'ADD NREXTERNALCELL:MCC=\"716\",MNC=\"10\"' " +
    		    "|| ',GNODEBID=' || gnodeb_id " +
    		    "|| ',CELLID=' || cell_id " +
    		    "|| ',DLARFCN=' || downlink_arfcn " +
    		    "|| ',ULARFCNCONFIGIND=NOT_CFG' " +
    		    "|| ',PHYCELLID=' || physical_cell_id " +
    		    "|| ',TAC=' || tac " +
    		    "|| ';{' || mbts || '}' AS script " +
    		    "FROM df4g_4g_nrexternalcell " +
    		    "WHERE id_data_field = :id " +
    		    "ORDER BY gnodeb_id, cell_id",
    		    nativeQuery = true)
    		List<String> findNrExternalCellScripts(@Param("id") Long idDataField);

    		@Query(value =
    		    "SELECT 'ADD NRNRELATIONSHIP:LOCALCELLID=' || localcellid " +
    		    "|| ',MCC=\"716\",MNC=\"10\"' " +
    		    "|| ',GNODEBID=' || ngnodebid " +
    		    "|| ',CELLID=' || ncellid " +
    		    "|| ',AGGREGATIONATTRIBUTE=CONTROL_MODE_FLAG-1&NO_REMOVE_FLAG-0&NO_HO_FLAG-0&CO_DEPLOYMENT_NSA_FLAG-0; {' || mbts || '}' AS script " +
    		    "FROM df4g_4g_nrnrelationship " +
    		    "WHERE id_data_field = :id",
    		    nativeQuery = true)
    		List<String> findNrnRelationshipScripts(@Param("id") Long idDataField);

    		@Query(value =
    		    "SELECT 'MOD NSADCMGMTCONFIG:LOCALCELLID=' || s.localcellid " +
    		    "|| ',NSADCALGOSWITCH=NSA_DC_CAPABILITY_SWITCH-1,UPPERLAYERINDICATIONSWITCH=ON; {' || g.mbts || '}' AS script " +
    		    "FROM df4g_dato_detalle d " +
    		    "JOIN df_sector s ON d.id_carrier = s.id_carrier AND d.id_sector = s.id " +
    		    "JOIN df4g_dato_general g ON d.id_data_field = g.id_data_field " +
    		    "WHERE d.id_data_field = :id AND d.eliminado = 0 " +
    		    "ORDER BY s.localcellid",
    		    nativeQuery = true)
    		List<String> findNsadcMgmtScripts(@Param("id") Long idDataField);

    		
    		// ── OTROS ────────────────────────────────────────────────────────────────────

    		@Query(value =
    		    "SELECT " +
    		    "    'ADD TWAMPCLIENT: CLIENTID=0, IPVERSION=\"IPv4\",LOCALIP=\"' || NVL(h.locipv4,'') || '\", PEERIP=\"' || t.ip_twamp1 || '\",TWAMPARCH=\"FULL\", PEERPORT=862 ;{' || g.mbts || '}' AS s1, " +
    		    "    'ADD TWAMPSENDER:CLIENTID=0,SENDERID=0,TWAMPARCH=FULL,DSCP=12,PKTSIZETYPE=FIXED,PKTSIZE=70,PKTINTTYPE=FIXED;{' || g.mbts || '}' AS s2, " +
    		    "    'ADD TWAMPCLIENT: CLIENTID=1, IPVERSION=\"IPv4\",LOCALIP=\"' || NVL(h.locipv4,'') || '\", PEERIP=\"' || t.ip_twamp2 || '\",TWAMPARCH=\"FULL\", PEERPORT=862 ;{' || g.mbts || '}' AS s3, " +
    		    "    'ADD TWAMPSENDER:CLIENTID=1,SENDERID=1,TWAMPARCH=FULL,DSCP=18,PKTSIZETYPE=FIXED,PKTSIZE=70,PKTINTTYPE=FIXED;{' || g.mbts || '}' AS s4 " +
    		    "FROM df4g_dato_general g " +
    		    "LEFT JOIN df_userplanehost h ON g.mbts = h.mbts_name " +
    		    "LEFT JOIN df_twamp t ON g.tac = t.tac " +
    		    "WHERE g.id_data_field = :id",
    		    nativeQuery = true)
    		List<Object[]> findTwampScripts(@Param("id") Long idDataField);

    		@Query(value =
    		    "SELECT script || ' {' || " +
    		    "(SELECT mbts FROM df4g_dato_general WHERE id_data_field = :id AND ROWNUM < 2) " +
    		    "|| '}' AS script " +
    		    "FROM df_configuracion_ip_ntp ",
    		    nativeQuery = true)
    		List<String> findNtpScripts(@Param("id") Long idDataField);
    		
    		// ----------------------------------------------------
    		// ── PCRF ───────────────────────────────────────────

    		@Query(value =
    		    "SELECT add_pcrf FROM df4g_ifi_restriccion " +
    		    "WHERE id_data_field = :id AND blacklist IS NULL " +
    		    "ORDER BY lte_cell_id",
    		    nativeQuery = true)
    		List<String> findPcrfNonBlacklist(@Param("id") Long idDataField);

    		@Query(value =
    		    "SELECT add_pcrf_blacklist FROM df4g_ifi_restriccion " +
    		    "WHERE id_data_field = :id AND blacklist IS NOT NULL " +
    		    "ORDER BY lte_cell_id",
    		    nativeQuery = true)
    		List<String> findPcrfBlacklist(@Param("id") Long idDataField);
    		

}