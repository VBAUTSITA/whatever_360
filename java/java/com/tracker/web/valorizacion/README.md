# valorizacion — plataforma de valorizaciones (PAP → Excel)

Almost-ready module implementing the agreed valorization flow end-to-end.
Table names are **generic placeholders** — the PAP view is a black box, so
point the mappings at the real objects when they're provisioned and the rest
of the module doesn't change.

```
RF user (form) ──► GET /valorizacion/nueva
   │  types ID VALORIZACIÓN ──► GET /valorizacion/api/pap/{id} (JSON)
   │        auto-fills: NOMBRE EMPRESA, DIRECCIÓN, LAT/LON, FAREND cercano
   │  picks TIPO DE SOLUCIÓN (dropdown), types ÁREA TOTAL (+pisos, etc.)
   ▼
POST /valorizacion/generar
   ├─ ValorizacionService.calcular()   dimensioning + preciario
   ├─ ValorizacionService.registrar()  snapshot → FORM_VALORIZACION_*
   └─ ValorizacionExcelBuilder         .xlsx replica of the current template
```

## Business rules implemented (from the agreement)

| Rule | Where |
|---|---|
| PAP fields consumed: IDVALORIZACION, NOMBRE_EMPRESA, DIRECCION, LAT/LON | `PapValorizacion` |
| Nearest FAREND by coordinates: native SQL on `tm_conf_actual_celda_4g` (bounding-box pre-filter + Haversine), same pattern as recursos' `RedExistenteServiceImpl`; lat/lon are VARCHAR2 → `TO_NUMBER` in SQL + defensive re-parse in Java | `FarendTmConfDao` |
| Solution types as dropdown, with tech/frequencies fixed per type (INDOOR 4G 2600; OUTDOOR y COMBINADO 4G 1900/2600) | `TipoSolucion` + form |
| INDOOR: #antenas = ceil(área/250), sectores = antenas; per BOOK RRU: 1 material + 2 celdas; planos por tramo de área; implementación por RRU; transporte item único; integración 0 | `PreciarioValorizacion`, `calcularIndoor` |
| OUTDOOR: EMs typed (default 1); per EM: 1 material + 3 celdas; TI/energía/integración por EM; SAQ por poste; planos 0 | `calcularOutdoor` |
| Output Excel "lo más similar al actual" with the fundamental items + preciario dividido en EQUIPOS/SERVICIOS/TOTAL USD | `ValorizacionExcelBuilder` |
| One snapshot table per template version | `FORM_VALORIZACION_INDOOR`, `_OUTDOOR`, `_OUTDOOR_PLUSINDOOR` |

⚠ Open point for the business: the agreement says **300 m²** per BOOK RRU in
point 5 but **área/250** in the output formula of point 7. The code follows
250 (`PreciarioValorizacion.M2_POR_BOOKRRU`) — flip that constant if 300 wins.

INDOOR+OUTDOOR (template combinado) is **phase 2**: its entity/table exists so
the schema is stable, but `calcular()` rejects it and the dropdown shows it
disabled. The reference workbooks live in `docs/valorizacion/`.

## Placeholder schema (adapt names, then update the @Table/@Column mappings)

```sql
-- Black box: already exists on the PAP side; we only SELECT these fields.
-- Expected contract of the view we are given:
--   PAP(IDVALORIZACION PK, NOMBRE_EMPRESA, DIRECCION_VALORIZACION,
--       LATITUD, LONGITUD)

-- FAREND source: tm_conf_actual_celda_4g (already exists — live network
-- inventory, also read by the recursos module). No new table needed.
-- FarendTmConfDao reads SECTOR_NAME, LATITUD, LONGITUD from it; if the
-- printed site code needs deriving from SECTOR_NAME, adjust
-- FarendTmConfDao.normalizarSite().

-- Shared id sequence (Oracle 11g has no identity columns):
CREATE SEQUENCE FORM_VALORIZACION_SEQ;

-- One per template version; identical core + type-specific extras.
CREATE TABLE FORM_VALORIZACION_INDOOR (
    ID                  BIGINT PRIMARY KEY,   -- fed by FORM_VALORIZACION_SEQ
    ID_VALORIZACION     BIGINT,
    AREA_TOTAL_M2       DOUBLE PRECISION,
    CANTIDAD_PISOS      INTEGER,
    PISOS_DETALLE       VARCHAR(255),
    DEPARTAMENTO        VARCHAR(64),
    NOMBRE_EMPRESA      VARCHAR(255),
    DIRECCION           VARCHAR(512),
    LATITUD             DOUBLE PRECISION,
    LONGITUD            DOUBLE PRECISION,
    FAREND              VARCHAR(64),
    FAREND_LATITUD      DOUBLE PRECISION,
    FAREND_LONGITUD     DOUBLE PRECISION,
    CANTIDAD_ANTENAS    INTEGER,
    CANTIDAD_SECTORES   INTEGER,
    TOTAL_EQUIPOS_USD   NUMERIC(14,2),
    TOTAL_SERVICIOS_USD NUMERIC(14,2),
    TOTAL_USD           NUMERIC(14,2),
    FECHA_CREACION      TIMESTAMP
);

CREATE TABLE FORM_VALORIZACION_OUTDOOR (
    -- same columns as _INDOOR, plus:
    CANTIDAD_POSTES     INTEGER
);

CREATE TABLE FORM_VALORIZACION_OUTDOOR_PLUSINDOOR (
    -- same columns as _INDOOR, plus:
    AREA_INDOOR_M2      DOUBLE PRECISION,
    ANTENAS_EM          INTEGER,
    CANTIDAD_POSTES     INTEGER
);
```

(Column types shown generically — on Oracle use `NUMBER`/`VARCHAR2`. The
entities already use `@GeneratedValue(SEQUENCE)` against
`FORM_VALORIZACION_SEQ`, matching the recursos module's convention.)

## Dependency

Same as the excelreport sample module: `org.apache.poi:poi-ooxml:4.1.2`
(Java 8 compatible; POI 5.x also runs on Java 8). Nothing else.

## Prices

Every USD figure lives ONLY in `PreciarioValorizacion` (values copied from
the MODELO workbook, tabs VALORIZACION BOOKRRU / EASY MACRO / PLANOS).
Verified against the MODELO's own examples: INDOOR 230 m² → 1 BOOKRRU →
equipos 2,320.37 + servicios 1,262.29 = **3,582.66 USD**; OUTDOOR 1 EM →
equipos 5,498.43 + servicios 9,373.46 = **14,871.89 USD** — both match the
source workbook cell for cell.
