package com.tracker.web.datafillbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookRruService {

    @Autowired private DF4GDatoGeneralRepository   datoGeneralRepo;
    @Autowired private DF4GBookGeneralRepository    bookGeneralRepo;
    @Autowired private DF4GBookTarjetaRepository    tarjetaRepo;
    @Autowired private DF4GBookRruRepository        rruRepo;
    @Autowired private DF4GBookVinculoRepository    vinculoRepo;
    @Autowired private DF4GDatoDetalleRepository    detalleRepo;

    // =========================================================================
    // BOOK GENERAL — create (Step 2.5 creation from Spring portal)
    // =========================================================================

    @Transactional
    public String crearBookGeneral(Long idDataField,
                                    String siteAddressBook,
                                    Integer slot, String bbws,
                                    Integer basebandeqmid,
                                    Integer rcn, Integer hsn, Integer hpn,
                                    Integer cascada, Integer srn, Integer ps,
                                    Integer sectorId, Integer sectoreqmid) {

        if (bookGeneralRepo.existsByIdDataField(idDataField))
            return "ERROR: Ya existe un Book General para DataField " + idDataField;

        Optional<DF4GDatoGeneral> optGeneral = datoGeneralRepo.findById(idDataField);
        if (!optGeneral.isPresent())
            return "ERROR: DataField " + idDataField + " no encontrado";

        String codigo = optGeneral.get().getSiteCode();

        DF4GBookGeneral bg = new DF4GBookGeneral(
                idDataField, codigo, siteAddressBook,
                slot, bbws, basebandeqmid,
                rcn, hsn, hpn, cascada, srn, ps,
                sectorId, sectoreqmid);

        // Derive rn_cellname: first active cellname (truncated) + '_' + siteAddressBook
        List<Object[]> cells = detalleRepo.findCellDataForIntegracionScript(idDataField);
        if (!cells.isEmpty()) {
            String firstCellname = str(cells.get(0)[0]);
            String truncated     = firstCellname.length() > 10
                                    ? firstCellname.substring(0, 10)
                                    : firstCellname;
            bg.setRnCellname(truncated + "_" + siteAddressBook);
        }

        bookGeneralRepo.save(bg);
        return "OK";
    }

    // =========================================================================
    // PART 1 — TARJETA CRUD
    // =========================================================================

    public List<DF4GBookTarjeta> listarTarjetas(Long idBookGeneral) {
        return tarjetaRepo.findByIdBookGeneralOrderBySlotAsc(idBookGeneral);
    }

    @Transactional
    public String agregarTarjeta(Long idBookGeneral, Long idDataField,
                                  Integer slot, String bbws) {
        if (!bookGeneralRepo.existsById(idBookGeneral))
            return "ERROR: No existe Book General con ID " + idBookGeneral;
        if (tarjetaRepo.existsByIdBookGeneralAndSlot(idBookGeneral, slot))
            return "ERROR: El slot " + slot + " ya está en uso";
        tarjetaRepo.save(new DF4GBookTarjeta(idBookGeneral, idDataField, slot, bbws));
        return "OK";
    }

    @Transactional
    public String eliminarTarjeta(Long idBookTarjeta) {
        if (!tarjetaRepo.existsById(idBookTarjeta))
            return "ERROR: No existe tarjeta con ID " + idBookTarjeta;
        tarjetaRepo.deleteById(idBookTarjeta);
        return "OK";
    }

    // =========================================================================
    // PART 2 — RRU CRUD
    // =========================================================================

    public List<DF4GBookRru> listarRrus(Long idBookGeneral) {
        return rruRepo.findByIdBookGeneralOrderByIdBookRruAsc(idBookGeneral);
    }

    @Transactional
    public String agregarRru(Long idBookGeneral, Long idDataField,
                              String tipo, Integer srn, Integer rcn, Integer ps,
                              String rnCellname, Integer rxnum, Integer txnum,
                              Integer cascada, Integer hsn, Integer hpn) {
        if (!bookGeneralRepo.existsById(idBookGeneral))
            return "ERROR: No existe Book General con ID " + idBookGeneral;
        if (!"BOOK".equals(tipo) && !"EASY_MACRO".equals(tipo))
            return "ERROR: Tipo debe ser BOOK o EASY_MACRO";
        if (rruRepo.existsByIdBookGeneralAndSrn(idBookGeneral, srn))
            return "ERROR: SRN " + srn + " ya está en uso";

        rruRepo.save(new DF4GBookRru(
                idBookGeneral, idDataField, tipo,
                srn, rcn, nvlInt(ps, 0), rnCellname,
                nvlInt(rxnum, 4), nvlInt(txnum, 4),
                nvlInt(cascada, 0), hsn, hpn));
        return "OK";
    }

    @Transactional
    public String eliminarRru(Long idBookRru) {
        if (!rruRepo.existsById(idBookRru))
            return "ERROR: No existe RRU con ID " + idBookRru;
        rruRepo.deleteById(idBookRru);
        return "OK";
    }

    // =========================================================================
    // PART 3 — VÍNCULO CRUD
    // =========================================================================

    public List<DF4GBookVinculo> listarVinculos(Long idBookGeneral) {
        return vinculoRepo.findByIdBookGeneralOrderByLocalcellidAsc(idBookGeneral);
    }

    public List<LocalcellidDto> listarLocalcellidsDisponibles(Long idDataField) {
        List<Object[]> rows = detalleRepo.findLocalcellidsForDataField(idDataField);
        List<LocalcellidDto> result = new ArrayList<>();
        for (Object[] row : rows) {
            Integer lcid    = row[0] != null ? ((Number) row[0]).intValue() : null;
            String  carrier = str(row[1]);
            String  sector  = str(row[2]);
            if (lcid != null) result.add(new LocalcellidDto(lcid, carrier, sector));
        }
        return result;
    }

    @Transactional
    public String agregarVinculo(Long idBookGeneral, Long idDataField,
                                  Integer localcellid,
                                  Integer basebandeqmid,
                                  Integer sectoreqmid) {
        if (!bookGeneralRepo.existsById(idBookGeneral))
            return "ERROR: No existe Book General con ID " + idBookGeneral;
        if (vinculoRepo.existsByIdBookGeneralAndLocalcellid(idBookGeneral, localcellid))
            return "ERROR: LOCALCELLID " + localcellid + " ya está vinculado";
        vinculoRepo.save(new DF4GBookVinculo(
                idBookGeneral, idDataField, localcellid, basebandeqmid, sectoreqmid));
        return "OK";
    }

    @Transactional
    public String eliminarVinculo(Long idVinculo) {
        if (!vinculoRepo.existsById(idVinculo))
            return "ERROR: No existe vínculo con ID " + idVinculo;
        vinculoRepo.deleteById(idVinculo);
        return "OK";
    }

    // =========================================================================
    // SCRIPT: INTEGRACION (TXT Book RRU)
    // Equivalent to Listar_DF4G_Book_Tarj_Cfg
    // =========================================================================

    public String generarScriptIntegracion(Long idDataField, Long idBookGeneral) {

        Optional<DF4GBookGeneral> optBook = bookGeneralRepo.findById(idBookGeneral);
        if (!optBook.isPresent())
            return "ERROR: No existe Book General con ID " + idBookGeneral;

        Optional<DF4GDatoGeneral> optGeneral = datoGeneralRepo.findById(idDataField);
        if (!optGeneral.isPresent())
            return "ERROR: No existe DataField con ID " + idDataField;

        DF4GBookGeneral b    = optBook.get();
        DF4GDatoGeneral g    = optGeneral.get();
        String          mbts = g.getMbts();

        List<DF4GBookTarjeta> tarjetas = tarjetaRepo.findByIdBookGeneralOrderBySlotAsc(idBookGeneral);
        List<DF4GBookRru>     rrus     = rruRepo.findByIdBookGeneralOrderByIdBookRruAsc(idBookGeneral);
        List<DF4GBookVinculo> vinculos = vinculoRepo.findByIdBookGeneralOrderByLocalcellidAsc(idBookGeneral);

        // Cell data from native join query
        List<Object[]> cellRows = detalleRepo.findCellDataForIntegracionScript(idDataField);
        List<CellScriptRowDto> cells = new ArrayList<>();
        for (Object[] row : cellRows) cells.add(new CellScriptRowDto(row));

        StringBuilder sb = new StringBuilder();

        // ── BLOCK 1: TARJETAS ─────────────────────────────────────────────────
        sb.append("//AGREGAR TARJETA\n");
        if (tarjetas.isEmpty()) {
            // Legacy: single slot from BOOK_GENERAL (tarjeta = 1)
            if (b.getTarjeta() != null && b.getTarjeta() == 1) {
                sb.append(buildAddBrd(b.getSlot(), b.getBbws(), mbts));
                sb.append(buildAddBasebandeqm(b.getBasebandeqmid(), b.getSlot(), mbts));
            }
        } else {
            for (DF4GBookTarjeta t : tarjetas)
                sb.append(buildAddBrd(t.getSlot(), t.getBbws(), mbts));
            for (DF4GBookTarjeta t : tarjetas)
                sb.append(buildAddBasebandeqm(b.getBasebandeqmid(), t.getSlot(), mbts));
        }

        // ── BLOCK 2: RADIOS ───────────────────────────────────────────────────
        sb.append("\n//AGREGAR RRU\n");
        if (rrus.isEmpty()) {
            // Legacy: single RRU from BOOK_GENERAL
            if (nvlInt(b.getCascada(), 0) == 0)
                sb.append(buildAddRruChain(b.getRcn(), b.getHsn(), b.getHpn(), mbts));
            sb.append(buildAddRruLegacy(b, mbts));
        } else {
            for (DF4GBookRru rru : rrus) {
                if (nvlInt(rru.getCascada(), 0) == 0)
                    sb.append(buildAddRruChain(rru.getRcn(), rru.getHsn(), rru.getHpn(), mbts));
                sb.append(buildAddRru(rru, mbts));
            }
        }

        // ── BLOCK 3: SECTOR ───────────────────────────────────────────────────
        sb.append("\n//AGREGAR SECTOR\n");
        sb.append(buildAddSector(b, mbts));
        sb.append(buildAddSectoreqm(b, mbts));

        // ── BLOCK 4: CELDAS ───────────────────────────────────────────────────
        sb.append("\n//AGREGAR CELDA\n");
        for (CellScriptRowDto cell : cells) {
            // Cellname: first 10 chars + '_' + siteAddressBook
            String cellname = cell.getCellname();
            String truncName = cellname.length() > 10 ? cellname.substring(0, 10) : cellname;
            String fullCellname = truncName + "_" + nvl(b.getSiteAddressBook(), "");

            sb.append("ADD CELL:LOCALCELLID=").append(cell.getLocalcellid())
              .append(",CELLNAME=\"").append(fullCellname).append("\"")
              .append(",NBCELLFLAG=FALSE")
              .append(",FREQBAND=").append(cell.getLteBand())
              .append(",ULEARFCNCFGIND=NOT_CFG")
              .append(",DLEARFCN=").append(cell.getDlearfcn())
              .append(",ULBANDWIDTH=").append(cell.getAbreviaturaConfig())
              .append(",DLBANDWIDTH=").append(cell.getAbreviaturaConfig())
              .append(",CELLID=").append(cell.getCellId())
              .append(",PHYCELLID=").append(cell.getPci())
              .append(",FDDTDDIND=CELL_")
                .append(cell.getTipoLteDesc() != null
                        ? cell.getTipoLteDesc().substring(0, Math.min(3, cell.getTipoLteDesc().length()))
                        : "FDD")
              .append(",EUCELLSTANDBYMODE=ACTIVE")
              .append(",ROOTSEQUENCEIDX=").append(cell.getPrach())
              .append(",HIGHSPEEDFLAG=LOW_SPEED,CUSTOMIZEDBANDWIDTHCFGIND=NOT_CFG")
              .append(",UEPOWERMAXCFGIND=NOT_CFG,MULTIRRUCELLFLAG=BOOLEAN_FALSE")
              .append(",CPRICOMPRESSION=").append(nvl(cell.getCpriDesc(), "NORMAL_COMPRESSION"))
              .append(",CRSPORTNUM=CRS_PORT_").append(cell.getCrsPortNum())
              .append(",TXRXMODE=").append(nvl(cell.getCodTxrxmode(), "4T4R"))
              .append(",CRSPORTMAP=4T4P_0321;")
              .append("{").append(mbts).append("}\n");

            sb.append("ADD CELLOP:LOCALCELLID=").append(cell.getLocalcellid())
              .append(",TRACKINGAREAID=0,MMECFGNUM=CELL_MME_CFG_NUM_0")
              .append(",ULAMRCMODE=ULRATECTRL_OFF;")
              .append("{").append(mbts).append("}\n");

            if (cell.getRsPowerX10() != null) {
                sb.append("MOD PDSCHCFG:LOCALCELLID=").append(cell.getLocalcellid())
                  .append(",REFERENCESIGNALPWR=").append(cell.getRsPowerX10())
                  .append(",PB=1; ")
                  .append("{").append(mbts).append("}\n");
            }
        }

        // ── BLOCK 5: VINCULOS ─────────────────────────────────────────────────
        sb.append("\n//ASOCIAR CELDA CON RADIO Y TARJETA\n");
        if (vinculos.isEmpty()) {
            sb.append("// Sin vínculos configurados\n");
        } else {
            for (DF4GBookVinculo v : vinculos) {
                sb.append("ADD EUCELLSECTOREQM:")
                  .append("LOCALCELLID=").append(v.getLocalcellid())
                  .append(",BASEBANDEQMID=").append(v.getBasebandeqmid())
                  .append(",SECTOREQMID=").append(v.getSectoreqmid())
                  .append(";{").append(mbts).append("}\n");
            }
        }

        return sb.toString();
    }

    // =========================================================================
    // SCRIPT: BASELINE
    // Equivalent to Listar_DF4G_Book_Baseline
    // =========================================================================

    public String generarScriptBaseline(Long idDataField, Long idBookGeneral) {

        Optional<DF4GDatoGeneral> optGeneral = datoGeneralRepo.findById(idDataField);
        if (!optGeneral.isPresent())
            return "ERROR: No existe DataField con ID " + idDataField;

        String mbts = optGeneral.get().getMbts();

        List<Object[]> rows = detalleRepo.findBaselineDataForScript(idDataField);
        if (rows.isEmpty())
            return "// Sin datos de baseline para DataField " + idDataField + "\n";

        StringBuilder sb = new StringBuilder();
        sb.append("//AGREGAR BASELINE\n");

        for (Object[] row : rows) {
            Integer localcellid = row[0] != null ? ((Number) row[0]).intValue() : null;
            String  baseCol1    = str(row[2]);
            String  baseCol2    = str(row[3]);

            if (localcellid == null) continue;

            sb.append(baseCol1)
              .append(localcellid)
              .append(baseCol2)
              .append("{").append(mbts).append("}\n");
        }

        return sb.toString();
    }

    // =========================================================================
    // SCRIPT: Combined (all sections concatenated)
    // Used by the single-button TXT export
    // =========================================================================

    public String generarTxtScript(Long idDataField, Long idBookGeneral) {
        StringBuilder sb = new StringBuilder();
        sb.append(generarScriptIntegracion(idDataField, idBookGeneral));
        sb.append("\n");
        sb.append(generarScriptBaseline(idDataField, idBookGeneral));
        return sb.toString();
    }

    // =========================================================================
    // Private builders — INTEGRACION blocks
    // =========================================================================

    private String buildAddBrd(Integer slot, String bbws, String mbts) {
        return "ADD BRD:SN=" + slot +
               ",BT=UBBP,BBWS=" + bbws +
               ",BBRESALLOCMODE=MANUAL,CPRIITFTYPE=CPRI_SFP,CELLDEPLOY=PERFORMANCE;" +
               "{" + mbts + "}\n";
    }

    private String buildAddBasebandeqm(Integer basebandeqmid, Integer slot, String mbts) {
        return "ADD BASEBANDEQM:BASEBANDEQMID=" + basebandeqmid +
               ",BASEBANDEQMTYPE=ULDL,UMTSDEMMODE=NULL,SN1=" + slot + ";" +
               "{" + mbts + "}\n";
    }

    private String buildAddRruChain(Integer rcn, Integer hsn, Integer hpn, String mbts) {
        return "ADD RRUCHAIN:RCN=" + rcn +
               ",TT=CHAIN,BM=COLD,AT=LOCALPORT,HSRN=0" +
               ",HSN=" + hsn + ",HPN=" + hpn +
               ",PROTOCOL=DEFAULT,CR=AUTO,USERDEFRATENEGOSW=OFF;" +
               "{" + mbts + "}\n";
    }

    private String buildAddRru(DF4GBookRru rru, String mbts) {
        String rt = "EASY_MACRO".equals(rru.getTipo()) ? "AIRU" : "MRRU";
        return "ADD RRU:CN=0" +
               ",SRN=" + rru.getSrn() +
               ",SN=0,TP=TRUNK" +
               ",RCN=" + rru.getRcn() +
               ",PS=" + nvlInt(rru.getPs(), 0) +
               ",RT=" + rt +
               ",RS=LO" +
               ",RN=\"" + nvl(rru.getRnCellname(), "") + "\"" +
               ",RXNUM=" + nvlInt(rru.getRxnum(), 4) +
               ",TXNUM=" + nvlInt(rru.getTxnum(), 4) +
               ",ALMPROCTHRHLD=25,ALMTHRHLD=15,MNTMODE=NORMAL" +
               ",RFDCPWROFFALMDETECTSW=OFF,RFTXSIGNDETECTSW=OFF" +
               ",CUSTOMEDRFSPECSW=SPEC_LOOSE_PSD_LIMIT-1,DORMANCYSW=OFF" +
               ",CIRCUITBREAKERVALUE=default" +
               ",TEMPERATUREALMDETENHSW=OFF,DCUVADSW=OFF;" +
               "{" + mbts + "}\n";
    }

    private String buildAddRruLegacy(DF4GBookGeneral b, String mbts) {
        return "ADD RRU:CN=0" +
               ",SRN=" + b.getSrn() +
               ",SN=0,TP=TRUNK" +
               ",RCN=" + b.getRcn() +
               ",PS=" + nvlInt(b.getPs(), 0) +
               ",RT=MRRU" +
               ",RS=LO" +
               ",RN=\"" + nvl(b.getRnCellname(), "") + "\"" +
               ",RXNUM=" + nvlInt(b.getRxnum(), 4) +
               ",TXNUM=" + nvlInt(b.getTxnum(), 4) +
               ",ALMPROCTHRHLD=25,ALMTHRHLD=15,MNTMODE=NORMAL" +
               ",RFDCPWROFFALMDETECTSW=OFF,RFTXSIGNDETECTSW=OFF" +
               ",CUSTOMEDRFSPECSW=SPEC_LOOSE_PSD_LIMIT-1,DORMANCYSW=OFF" +
               ",CIRCUITBREAKERVALUE=default" +
               ",TEMPERATUREALMDETENHSW=OFF,DCUVADSW=OFF;" +
               "{" + mbts + "}\n";
    }

    private String buildAddSector(DF4GBookGeneral b, String mbts) {
        Integer srn = b.getSrn();
        return "ADD SECTOR:SECTORID=" + b.getSectorId() +
               ",LOCATIONNAME=\"" + nvl(b.getLocationname(), "") + "\"" +
               ",ANTNUM=" + nvlInt(b.getAntnum(), 4) +
               ",ANT1CN=0,ANT1SRN=" + srn + ",ANT1SN=0,ANT1N=R0A" +
               ",ANT2CN=0,ANT2SRN=" + srn + ",ANT2SN=0,ANT2N=R0B" +
               ",ANT3CN=0,ANT3SRN=" + srn + ",ANT3SN=0,ANT3N=R0C" +
               ",ANT4CN=0,ANT4SRN=" + srn + ",ANT4SN=0,ANT4N=R0D" +
               ",CREATESECTOREQM=FALSE;" +
               "{" + mbts + "}\n";
    }

    private String buildAddSectoreqm(DF4GBookGeneral b, String mbts) {
        Integer srn = b.getSrn();
        return "ADD SECTOREQM:SECTOREQMID=" + b.getSectoreqmid() +
               ",SECTORID=" + b.getSectorId() +
               ",ANTCFGMODE=ANTENNAPORT" +
               ",ANTNUM=" + nvlInt(b.getAntnum(), 4) +
               ",ANT1CN=0,ANT1SRN=" + srn + ",ANT1SN=0,ANT1N=R0A,ANTTYPE1=RXTX_MODE" +
               ",ANT2CN=0,ANT2SRN=" + srn + ",ANT2SN=0,ANT2N=R0B,ANTTYPE2=RXTX_MODE" +
               ",ANT3CN=0,ANT3SRN=" + srn + ",ANT3SN=0,ANT3N=R0C,ANTTYPE3=RXTX_MODE" +
               ",ANT4CN=0,ANT4SRN=" + srn + ",ANT4SN=0,ANT4N=R0D,ANTTYPE4=RXTX_MODE;" +
               "{" + mbts + "}\n";
    }
    
    
    
    
    /*
    public String generarAdyacenciasScript(Long idDataField) {
        DF4GDatoGeneral general = datoGeneralRepo.findById(idDataField)
            .orElseThrow(() -> new RuntimeException("DataField not found"));
        String mbts = general.getMbts();

        StringBuilder sb = new StringBuilder();

        // Section A — EUTRANINTERNFREQ (4G → 4G inter-frequency objects)
        List<String> source1 = detalleRepo.findSource1Scripts(idDataField);
        if (!source1.isEmpty()) {
            sb.append("//EUTRANINTERNFREQ\n");
            for (String script : source1) {
                // script already contains the full command, just append MBTS tag
                String line = script.endsWith(";")
                    ? script + "{" + mbts + "}"
                    : script;
                sb.append(line).append("\n");
            }
        }

        // Section B — EUTRANINTERFREQNCELL (4G → 4G inter-frequency neighbours)
        List<String> source2 = detalleRepo.findSource2Scripts(idDataField);
        if (!source2.isEmpty()) {
            sb.append("\n//EUTRANINTERFREQNCELL\n");
            for (String script : source2) {
                String line = script.endsWith(";")
                    ? script + "{" + mbts + "}"
                    : script;
                sb.append(line).append("\n");
            }
        }

        // Section C — EUTRANINTRAFREQNCELL (4G → 4G intra-frequency neighbours)
        List<String> source3 = detalleRepo.findSource3Scripts(idDataField);
        if (!source3.isEmpty()) {
            sb.append("\n//EUTRANINTRAFREQNCELL\n");
            for (String script : source3) {
                // SOURCE3 scripts already embed {MBTS} from the sync procedure
                sb.append(script).append("\n");
            }
        }

        return sb.toString();
    }*/
    
 public String   generarAdyacenciasScript(Long idDataField) {
        String mbts = datoGeneralRepo.findById(idDataField)
            .map(DF4GDatoGeneral::getMbts).orElse("");
        StringBuilder sb = new StringBuilder();

        // ── ANR switch — static, one per eNodeB ──────────────────────────────────
        sb.append("//4G --- > 4G //ANR algorithm switch\n");
        sb.append("MOD ENODEBALGOSWITCH:ANRSWITCH=IntraRatEventAnrSwitch-1")
          .append("&IntraRatFastAnrSwitch-0&IntraRatAnrAutoDelSwitch-1; {")
          .append(mbts).append("}\n");

        // ── SOURCE1 — EUTRANINTERNFREQ ────────────────────────────────────────────
        List<String> s1 = detalleRepo.findSource1Scripts(idDataField);
        if (!s1.isEmpty()) {
            sb.append("//EUTRANINTERNFREQ\n");
            for (String sc : s1)
                sb.append(sc.endsWith(";") ? sc + "{" + mbts + "}" : sc).append("\n");
        }

        // ── SOURCE2 — EUTRANINTERFREQNCELL ────────────────────────────────────────
        List<String> s2 = detalleRepo.findSource2Scripts(idDataField);
        if (!s2.isEmpty()) {
            sb.append("//EUTRANINTERNFREQNCELL\n");
            for (String sc : s2)
                sb.append(sc.endsWith(";") ? sc + "{" + mbts + "}" : sc).append("\n");
        }

        // ── SOURCE3 — EUTRANINTRAFREQNCELL ────────────────────────────────────────
        List<String> s3 = detalleRepo.findSource3Scripts(idDataField);
        if (!s3.isEmpty()) {
            sb.append("//EUTRANINTRAFREQNCELL\n");
            for (String sc : s3)
                sb.append(sc).append("\n"); // SOURCE3 already has {MBTS} embedded twice
        }

        // ── 4G → 3G: UTRANNFREQ ──────────────────────────────────────────────────
        List<String> utranFreq = detalleRepo.findUtranFreqScripts(idDataField);
        if (!utranFreq.isEmpty()) {
            sb.append("//4G ---> 3G: LTE->ADD UTRANFREQ \n");
            for (String sc : utranFreq)
                sb.append(sc.endsWith(";") ? sc + "{" + mbts + "}" : sc).append("\n");
        }

        // ── UTRAN EXTERNAL CELL ───────────────────────────────────────────────────
        List<String> utranExt = detalleRepo.findUtranExternalCellScripts(idDataField);
        if (!utranExt.isEmpty()) {
            sb.append("//UTRAN EXTERNAL CELL\n");
            for (String sc : utranExt) sb.append(sc).append("\n");
        }

        // ── LTE → UTRAN NCELL ────────────────────────────────────────────────────
        List<String> utranNcell = detalleRepo.findUtranNcellScripts(idDataField);
        if (!utranNcell.isEmpty()) {
            sb.append("//LTE->UTRAN NCELL\n");
            for (String sc : utranNcell) sb.append(sc).append("\n");
        }

        // ── UMTS → UCELLNFREQPRIOINFO ─────────────────────────────────────────────
        List<String> ucell = detalleRepo.findUcellnfreqScripts(idDataField);
        if (!ucell.isEmpty()) {
            sb.append("//UMTS->UCELLNFREQPRIOINF\n");
            for (String sc : ucell) sb.append(sc).append("\n");
        }

        // ── LTE CellIndex ─────────────────────────────────────────────────────────
        List<String> cellIndex = detalleRepo.findCellIndexScripts(idDataField);
        if (!cellIndex.isEmpty()) {
            sb.append("//LTE CellIndex\n");
            for (String sc : cellIndex) sb.append(sc).append("\n");
        }

        // ── 3G to 4G ─────────────────────────────────────────────────────────────
        List<String> g3to4g = detalleRepo.find3gTo4gScripts(idDataField);
        if (!g3to4g.isEmpty()) {
            sb.append("//3G to 4G\n");
            for (String sc : g3to4g) sb.append(sc).append("\n");
        }

        // ── 5G NSA: NRMFBIFREQ ───────────────────────────────────────────────────
        List<String> nrmfbi = detalleRepo.findNrmfbifreqScripts(idDataField);
        if (!nrmfbi.isEmpty()) {
            sb.append("//4GNRMFBIFREQ\n");
            for (String sc : nrmfbi) sb.append(sc).append("\n");
        }

        // ── 5G NSA: NRSCGFREQCONFIG ──────────────────────────────────────────────
        List<String> nrscg = detalleRepo.findNrscgFreqScripts(idDataField);
        if (!nrscg.isEmpty()) {
            sb.append("//4G - NRSCGFREQCONFIG\n");
            for (String sc : nrscg) sb.append(sc).append("\n");
        }

        // ── 5G NSA: NRNFREQ ──────────────────────────────────────────────────────
        List<String> nrnfreq = detalleRepo.findNrnfreqScripts(idDataField);
        if (!nrnfreq.isEmpty()) {
            sb.append("//4G - NRNFREQ\n");
            for (String sc : nrnfreq) sb.append(sc).append("\n");
        }

        // ── 5G NSA: NREXTERNALCELL ───────────────────────────────────────────────
        List<String> nrext = detalleRepo.findNrExternalCellScripts(idDataField);
        if (!nrext.isEmpty()) {
            sb.append("//4G NREXTERNALCELL\n");
            for (String sc : nrext) sb.append(sc).append("\n");
        }

        // ── 5G NSA: NRNRELATIONSHIP ──────────────────────────────────────────────
        List<String> nrnrel = detalleRepo.findNrnRelationshipScripts(idDataField);
        if (!nrnrel.isEmpty()) {
            sb.append("//4G - NRNRELATIONSHIP\n");
            for (String sc : nrnrel) sb.append(sc).append("\n");
        }

        // ── 5G NSA: NSADCMGMTCONFIG ──────────────────────────────────────────────
        List<String> nsadc = detalleRepo.findNsadcMgmtScripts(idDataField);
        if (!nsadc.isEmpty()) {
            sb.append("//4G - NSADCMGMTCONFIG\n");
            for (String sc : nsadc) sb.append(sc).append("\n");
        }

        return sb.toString();
    }
 
 public String generarOtrosScript(Long idDataField) {
	    StringBuilder sb = new StringBuilder();

	    sb.append("//TWAMP\n");
	    List<Object[]> twamp = detalleRepo.findTwampScripts(idDataField);
	    for (Object[] row : twamp) {
	        for (Object col : row) {
	            if (col != null) sb.append(col.toString()).append("\n");
	        }
	    }

	    sb.append("//CONFIGURACION IP NTP\n");
	    List<String> ntp = detalleRepo.findNtpScripts(idDataField);
	    for (String line : ntp) {
	        if (line != null) sb.append(line).append("\n");
	    }

	    return sb.toString();
	}

	public String generarPcrfScript(Long idDataField) {
	    StringBuilder sb = new StringBuilder();
	    sb.append("//4G - IFI RESTRICCION\n");

	    List<String> normal = detalleRepo.findPcrfNonBlacklist(idDataField);
	    for (String line : normal) {
	        if (line != null) sb.append(line).append("\n");
	    }

	    List<String> blacklist = detalleRepo.findPcrfBlacklist(idDataField);
	    for (String line : blacklist) {
	        if (line != null) sb.append(line).append("\n");
	    }

	    return sb.toString();
	}

 
 


    // ── Null-safe helpers ─────────────────────────────────────────────────────

    private String str(Object o)              { return o != null ? o.toString() : ""; }
    private String nvl(String v, String fb)   { return v != null ? v : fb; }
    private int    nvlInt(Integer v, int fb)  { return v != null ? v : fb; }
}
