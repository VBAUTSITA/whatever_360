package com.tracker.web.datafillbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import javax.servlet.http.HttpServletResponse;

//import java.awt.PageAttributes.MediaType;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Controller
@RequestMapping("/datafill")
public class BookRruController {

    @Autowired private DF4GDatoGeneralRepository datoGeneralRepo;
    @Autowired private DF4GBookGeneralRepository  bookGeneralRepo;
    @Autowired private BookRruService             bookRruService;

    //@Autowired  private JdbcTemplate jdbcTemplate;

    // ======================================================f===================
    // LIST PAGE
    // GET /datafill/list
    // =========================================================================
    @GetMapping("/list")
    public String listDataFields(
            @RequestParam(required = false) String search,
            Model model) {

        List<DF4GDatoGeneral> fields;
        if (search != null && !search.trim().isEmpty()) {
            fields = datoGeneralRepo.findBySiteNameContainingIgnoreCaseAndEliminado(search, 0);
            if (fields.isEmpty())
                fields = datoGeneralRepo.findBySiteCodeContainingIgnoreCaseAndEliminado(search, 0);
        } else {
            fields = datoGeneralRepo.findByEliminado(0);
        }
        model.addAttribute("fields", fields);
        model.addAttribute("search", search);
        return "datafill/list";
    }
    
    

    // =========================================================================
    // BOOK RRU FORM
    // GET /datafill/{idDataField}/bookrru
    //
    // If bookGeneral == null → show creation form (creation mode)
    // If bookGeneral exists  → show full 3-part form (edit mode)
    // =========================================================================
    @GetMapping("/{idDataField}/bookrru")
    public String bookRruForm(
            @PathVariable Long idDataField,
            Model model) {

        Optional<DF4GDatoGeneral> optGeneral = datoGeneralRepo.findById(idDataField);
        if (!optGeneral.isPresent()) {
            model.addAttribute("error", "DataField " + idDataField + " no encontrado");
            return "datafill/error";
        }

        Optional<DF4GBookGeneral> optBook = bookGeneralRepo.findByIdDataField(idDataField);

        model.addAttribute("general",     optGeneral.get());
        model.addAttribute("bookGeneral", optBook.orElse(null));
        model.addAttribute("modoCreacion", !optBook.isPresent());

        if (optBook.isPresent()) {
            Long bgId = optBook.get().getIdBookGeneral();
            model.addAttribute("tarjetas",    bookRruService.listarTarjetas(bgId));
            model.addAttribute("rrus",         bookRruService.listarRrus(bgId));
            model.addAttribute("vinculos",     bookRruService.listarVinculos(bgId));
            model.addAttribute("localcellids", bookRruService.listarLocalcellidsDisponibles(idDataField));
        }

        return "datafill/bookrru";
    }

    // =========================================================================
    // BOOK GENERAL — Create (Step 2.5 initial creation from Spring portal)
    // POST /datafill/{idDataField}/bookrru/create
    // =========================================================================
    @PostMapping("/{idDataField}/bookrru/create")
    @ResponseBody
    public ResponseEntity<String> createBookGeneral(
            @PathVariable Long idDataField,
            @RequestParam String  siteAddressBook,
            @RequestParam Integer slot,
            @RequestParam String  bbws,
            @RequestParam Integer basebandeqmid,
            @RequestParam Integer rcn,
            @RequestParam(required = false) Integer hsn,
            @RequestParam(required = false) Integer hpn,
            @RequestParam(defaultValue = "0") Integer cascada,
            @RequestParam Integer srn,
            @RequestParam(required = false, defaultValue = "0") Integer ps,
            @RequestParam Integer sectorId,
            @RequestParam Integer sectoreqmid) {

        String result = bookRruService.crearBookGeneral(
                idDataField, siteAddressBook,
                slot, bbws, basebandeqmid,
                rcn, hsn, hpn, cascada, srn, ps,
                sectorId, sectoreqmid);

        return result.startsWith("ERROR")
                ? ResponseEntity.badRequest().body(result)
                : ResponseEntity.ok("OK");
    }

    // =========================================================================
    // PART 1 — TARJETA endpoints
    // =========================================================================

    @PostMapping("/{idDataField}/bookrru/tarjeta/add")
    @ResponseBody
    public ResponseEntity<String> addTarjeta(
            @PathVariable Long idDataField,
            @RequestParam Long    idBookGeneral,
            @RequestParam Integer slot,
            @RequestParam String  bbws) {

        String result = bookRruService.agregarTarjeta(idBookGeneral, idDataField, slot, bbws);
        return result.startsWith("ERROR")
                ? ResponseEntity.badRequest().body(result)
                : ResponseEntity.ok("OK");
    }

    @PostMapping("/bookrru/tarjeta/{idBookTarjeta}/delete")
    @ResponseBody
    public ResponseEntity<String> deleteTarjeta(@PathVariable Long idBookTarjeta) {
        String result = bookRruService.eliminarTarjeta(idBookTarjeta);
        return result.startsWith("ERROR")
                ? ResponseEntity.badRequest().body(result)
                : ResponseEntity.ok("OK");
    }

    // =========================================================================
    // PART 2 — RRU endpoints
    // =========================================================================

    @PostMapping("/{idDataField}/bookrru/rru/add")
    @ResponseBody
    public ResponseEntity<String> addRru(
            @PathVariable Long idDataField,
            @RequestParam Long    idBookGeneral,
            @RequestParam String  tipo,
            @RequestParam Integer srn,
            @RequestParam Integer rcn,
            @RequestParam(required = false) Integer ps,
            @RequestParam(required = false) String  rnCellname,
            @RequestParam(required = false) Integer rxnum,
            @RequestParam(required = false) Integer txnum,
            @RequestParam(defaultValue = "0") Integer cascada,
            @RequestParam(required = false) Integer hsn,
            @RequestParam(required = false) Integer hpn) {

        String result = bookRruService.agregarRru(
                idBookGeneral, idDataField, tipo,
                srn, rcn, ps, rnCellname, rxnum, txnum,
                cascada, hsn, hpn);
        return result.startsWith("ERROR")
                ? ResponseEntity.badRequest().body(result)
                : ResponseEntity.ok("OK");
    }

    @PostMapping("/bookrru/rru/{idBookRru}/delete")
    @ResponseBody
    public ResponseEntity<String> deleteRru(@PathVariable Long idBookRru) {
        String result = bookRruService.eliminarRru(idBookRru);
        return result.startsWith("ERROR")
                ? ResponseEntity.badRequest().body(result)
                : ResponseEntity.ok("OK");
    }

    // =========================================================================
    // PART 3 — VÍNCULO endpoints
    // =========================================================================

    @PostMapping("/{idDataField}/bookrru/vinculo/add")
    @ResponseBody
    public ResponseEntity<String> addVinculo(
            @PathVariable Long idDataField,
            @RequestParam Long    idBookGeneral,
            @RequestParam Integer localcellid,
            @RequestParam Integer basebandeqmid,
            @RequestParam Integer sectoreqmid) {

        String result = bookRruService.agregarVinculo(
                idBookGeneral, idDataField, localcellid, basebandeqmid, sectoreqmid);
        return result.startsWith("ERROR")
                ? ResponseEntity.badRequest().body(result)
                : ResponseEntity.ok("OK");
    }

    @PostMapping("/bookrru/vinculo/{idVinculo}/delete")
    @ResponseBody
    public ResponseEntity<String> deleteVinculo(@PathVariable Long idVinculo) {
        String result = bookRruService.eliminarVinculo(idVinculo);
        return result.startsWith("ERROR")
                ? ResponseEntity.badRequest().body(result)
                : ResponseEntity.ok("OK");
    }

    // =========================================================================
    // EXPORT — TXT Book RRU (INTEGRACION + BASELINE)
    // GET /datafill/{idDataField}/exportBookRRU
    // =========================================================================
    
    @GetMapping("/{idDataField}/exportBookRRU")
    public ResponseEntity<byte[]> exportScript(@PathVariable Long idDataField) {
        Optional<DF4GBookGeneral> bg = bookGeneralRepo.findByIdDataField(idDataField);
        if (!bg.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Long idBookGeneral = bg.get().getIdBookGeneral();
        Optional<DF4GDatoGeneral> g = datoGeneralRepo.findById(idDataField);
        String siteCode = g.map(DF4GDatoGeneral::getSiteCode).orElse("DF" + idDataField);

        Map<String, String> scripts = new LinkedHashMap<>();
        scripts.put("INTEGRACION", bookRruService.generarScriptIntegracion(idDataField, idBookGeneral));
        scripts.put("BASELINE", bookRruService.generarScriptBaseline(idDataField, idBookGeneral));
        scripts.put("ADYACENCIAS", bookRruService.generarAdyacenciasScript(idDataField));
        scripts.put("OTROS", bookRruService.generarOtrosScript(idDataField));
        scripts.put("PCRF", bookRruService.generarPcrfScript(idDataField));

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ZipOutputStream zip = new ZipOutputStream(baos);

            for (Map.Entry<String, String> entry : scripts.entrySet()) {
                String filename = "SCRIPT_" + entry.getKey() + "_" + siteCode + ".txt";
                byte[] content = entry.getValue().getBytes(StandardCharsets.UTF_8);

                zip.putNextEntry(new ZipEntry(filename));
                zip.write(content);
                zip.closeEntry();
            }

            zip.finish();
            zip.close();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"BookRRU_" + siteCode + ".zip\"")
                    .contentType(MediaType.parseMediaType("application/zip"))
                    .body(baos.toByteArray());

        } catch (IOException e) {
        	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    
    /*@GetMapping("/{idDataField}/exportBookRRU")
    public ResponseEntity<byte[]> exportScript(@PathVariable Long idDataField) {
     
        // resolve idBookGeneral — needed by integracion and baseline

        Optional<DF4GBookGeneral> bg = bookGeneralRepo.findByIdDataField(idDataField);

        if (!bg.isPresent()) {

            return ResponseEntity.notFound().build();

        }

        Long idBookGeneral = bg.get().getIdBookGeneral();
     
        // correct method names + correct parameter count

        String integracion  = bookRruService.generarScriptIntegracion(idDataField, idBookGeneral);

        String baseline     = bookRruService.generarScriptBaseline(idDataField, idBookGeneral);

        String adyacencias  = bookRruService.generarAdyacenciasScript(idDataField);
     
        String otros = bookRruService.generarOtrosScript(idDataField);
        
        String Pcrf = bookRruService.generarPcrfScript(idDataField);
        
        String full = integracion + "\n\n"

                    + baseline    + "\n\n"
                    
                    + otros + "\\n\\n"
                    
                    + Pcrf + "\\n\\n" 

                    + adyacencias;
     
        byte[] bytes = full.getBytes(StandardCharsets.UTF_8);
     
        Optional<DF4GDatoGeneral> g = datoGeneralRepo.findById(idDataField);

        String siteName = g.map(DF4GDatoGeneral::getSiteCode).orElse("DF" + idDataField);
     
        return ResponseEntity.ok()

            .header(HttpHeaders.CONTENT_DISPOSITION,

                    "attachment; filename=\"BookRRU_" + siteName + ".txt\"")

            .contentType(MediaType.parseMediaType("text/plain;charset=UTF-8"))

            .body(bytes);

    }*/
     
    /*@GetMapping("/{idDataField}/exportBookRRU")
    public void exportBookRRU(
            @PathVariable Long idDataField,
            @RequestParam(required = false) Long idBookGeneral,
            HttpServletResponse response) throws IOException {

        Long bookId = idBookGeneral;
        if (bookId == null) {
            Optional<DF4GBookGeneral> bg = bookGeneralRepo.findByIdDataField(idDataField);
            if (!bg.isPresent()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND,
                        "No existe Book RRU para DataField " + idDataField);
                return;
            }
            bookId = bg.get().getIdBookGeneral();
        }

        String script = bookRruService.generarTxtScript(idDataField, bookId);

        if (script.startsWith("ERROR")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, script);
            return;
        }

        Optional<DF4GDatoGeneral> g = datoGeneralRepo.findById(idDataField);
        String siteName = g.map(DF4GDatoGeneral::getSiteCode).orElse("DF" + idDataField);
        String filename = "BookRRU_" + siteName + ".txt";

        response.setContentType("text/plain; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        try (PrintWriter writer = response.getWriter()) {
            writer.write(script);
            writer.flush();
        }
    }
    
    */
}
