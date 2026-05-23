package com.tracker.web.datafillbook;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.Types;
import java.util.Collections;
import java.util.Map;



@Controller
@RequestMapping("/datafill")
public class SyncController {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    /*
    @GetMapping("/{id}/sync/ping")
    public ResponseEntity<String> ping(@PathVariable Long id) {
        return ResponseEntity.ok("sync reachable for " + id);
    }
	*/

    @PostMapping("/sync-list")
    public ResponseEntity<Void> syncList() {
        try {
            SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
            call.setProcedureName("SYNC_LIST");
            call.declareParameters(
                new SqlOutParameter("P_RESULTADO", Types.VARCHAR)
            );

            call.execute();

            return ResponseEntity
                    .status(HttpStatus.SEE_OTHER)
                    .header("Location", "/datafill/list")
                    .build();

        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }
    
    
    
    @PostMapping("/{id}/sync")
    public ResponseEntity<Map<String, String>> sync(@PathVariable("id") Long idDataField) {

        try {
            SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("SYNC_DATAFILL_4G")
                    .declareParameters(
                            new SqlParameter("P_ID_DATA_FIELD", Types.NUMERIC),
                            new SqlOutParameter("P_RESULTADO", Types.VARCHAR)
                    );

            Map<String, Object> result = call.execute(
                    Collections.singletonMap("P_ID_DATA_FIELD", idDataField)
            );

            String resultado = (String) result.get("P_RESULTADO");
            return ResponseEntity.ok(Collections.singletonMap("resultado", resultado != null ? resultado : ""));

        } catch (Exception ex) {
            // Spring 4.x style: use ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("resultado", "ERROR interno: " + ex.getMessage()));
        }
    }
}
