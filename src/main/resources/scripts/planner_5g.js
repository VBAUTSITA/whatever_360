/* ============================================================================
 *  planner.js — Motor de planificación PCI/RSI (algoritmo de 12 pasos)
 * ----------------------------------------------------------------------------
 *  Port fiel en JavaScript del motor Python validado en producción
 *  (core/geometry.py + core/planner.py). Reproduce el flujo Excel de
 *  ingeniería RF de forma automatizada:
 *
 *    1/1.1  Ingreso y validación de datos (ver io en app.js).
 *    2      Filtro por tecnología/carrier (claves: Carrier + Freqband).
 *    3      30 celdas existentes más cercanas (haversine).
 *    4      Promedio de distancias a esas 30 celdas.
 *    5      Sector (wedge) de la celda a planificar (radio = promedio).
 *    6      Sectores de las 30 vecinas (radio = RADIO TA por cascada 80 %).
 *    7      Área de intersección en m² (UTM) e intersección normalizada.
 *    8      Suma de intersección normalizada por PSS (PCI mod 3), ordenada.
 *    9      PSS ganador = menor suma.
 *    10     Vecindad ampliada (unión de 500 más cercanas / < 70 km, tope 500).
 *    11     Elección de PCI (rango outdoor/indoor, PCI mod 3 == PSS, sin
 *           colisión en vecindad, valor cercano a la mediana de PCIs vecinos).
 *    12     RSI por lookup de tabla + validación de reuso en vecindad inmediata.
 *
 *  ADAPTACIÓN 5G NR: tabla RSI por (COBERTURA, PCI) del Excel "TABLA PCI RSI 5G"
 *  (PCI 0–1007; OUTDOOR 0–985 / INDOOR 986–1007), y registro de carriers NR
 *  (CARRIER_REGISTRY) con factores espectrales DL/UL tomados del archivo de
 *  referencia 5G_PCI_RSI_Planner_12. El método geométrico de 12 pasos, el
 *  coloreo conjunto de PSS, la auditoría y la replanificación son los del 4G.
 *
 *  Este módulo es agnóstico del DOM: corre en el navegador (global
 *  `window.PCIPlanner`) y en Node (`module.exports`) para los tests.
 * ==========================================================================*/
(function (root) {
  'use strict';

  // === Constantes del procedimiento =======================================
  var RADIO_TIERRA_M = 6371000.0; // radio terrestre medio (procedimiento)

  // Cascada TA del paso 6: [columna de porcentaje, límite superior del bin (m)].
  // Bins del reporte de Timing Advance 5G NR (Reporte 5G): cortes DISTINTOS a
  // los del 4G (117/507/975/1775/3315/7215/15015 vs 234/546/.../14430).
  var BINS_TA = [
    ['ta_0_117_mts_porc', 117.0],
    ['ta_117_507_mts_porc', 507.0],
    ['ta_507_975_mts_porc', 975.0],
    ['ta_975_1775_mts_porc', 1775.0],
    ['ta_1775_3315_mts_porc', 3315.0],
    ['ta_3315_7215_mts_porc', 7215.0]
  ];
  var RADIO_TA_DEFECTO = 15015.0; // si el acumulado nunca supera el umbral (bin 7215–15015)

  // Desplazamientos de azimuth por sub-beam Massive MIMO (fórmula del Excel).
  var OFFSETS_MM = { 1: -30.0, 2: -10.0, 3: 10.0, 4: 30.0 };
  var PATRON_MM = /_MM_([1-4])(?!\d)/i;

  // Parámetros por defecto (idénticos al procedimiento Python).
  var DEFAULTS = {
    k_cercanas: 30, // paso 3
    umbral_ta: 0.80, // paso 6
    hbeam_mm_deg: 100.0, // 5G NR: HBEAM por defecto 100° también en 32T32R (Massive MIMO)
    hbeam_convencional_deg: 100.0, // 4T4R / resto de modos
    n_arco: 64, // puntos del arco del wedge
    politica_cosito: 'dist_min_1m', // 'dist_min_1m' | 'excluir' (paso 7)
    dist_min_normalizacion_m: 1.0,
    max_vecindad: 400, // paso 10/11 (reuso PCI, mismo carrier)
    radio_vecindad_m: 10000.0, // paso 10/11 (10 km, mismo carrier)
    pci_outdoor: [0, 985], // paso 11 (5G NR: PCI 0–1007)
    pci_indoor: [986, 1007], // paso 11 (5G NR)
    n_mediana: 30, // N celdas para mediana de PCI (paso 11)
    delta_pci_vecino: 3, // "PCI vecino" para el desempate (paso 11)
    evitar_pss_repetido_en_sitio: true, // paso 11
    radio_cosito_m: 1.0, // umbral para considerar misma coordenada
    ajustar_azimuth_mm_plan: true, // aplicar offsets MM también a la celda plan
    claves_carrier: ['Carrier', 'Freqband'], // paso 2
    // Grupos de carriers que comparten plano de PSS/PCI (paso 11). En 5G NR el
    // PCI se planifica por carrier asignado (cada portadora su propio plano);
    // app.js puede pasar enlaces derivados del CARRIER_REGISTRY (co-canal).
    carrier_links: [],
    // Radio (m) para considerar dos celdas "del mismo sitio" en la regla de PSS
    // y la exclusión de PCI co-sitio. 0 = coordenada exacta. Útil cuando los
    // sectores de un mismo emplazamiento traen coordenadas ligeramente distintas.
    radio_sitio_m: 50
  };

  function asignaDefaults(p) {
    var out = {};
    for (var k in DEFAULTS) out[k] = DEFAULTS[k];
    if (p) for (var j in p) if (p[j] !== undefined && p[j] !== null) out[j] = p[j];
    return out;
  }

  // === Tabla de lookup (COBERTURA, PCI) → RSI — 5G NR ======================
  // Embebida (1008 filas) tal cual `data/tabla_pci_rsi_5g.csv`, derivada del
  // Excel "TABLA PCI RSI 5G". OUTDOOR (PCI 0–985) e INDOOR (986–1007) son rangos
  // DISJUNTOS, así que indexar por PCI equivale al lookup (COBERTURA, PCI). El
  // RSI cumple (2·PCI) mod 840, pero se usa como TABLA (fuente de verdad), nunca
  // se calcula con fórmula. TABLA_RSI[pci] = RSI del PCI dado.
  var TABLA_RSI = [
0,2,4,6,8,10,12,14,16,18,20,22,24,26,28,30,32,34,
36,38,40,42,44,46,48,50,52,54,56,58,60,62,64,66,68,70,
72,74,76,78,80,82,84,86,88,90,92,94,96,98,100,102,104,106,
108,110,112,114,116,118,120,122,124,126,128,130,132,134,136,138,140,142,
144,146,148,150,152,154,156,158,160,162,164,166,168,170,172,174,176,178,
180,182,184,186,188,190,192,194,196,198,200,202,204,206,208,210,212,214,
216,218,220,222,224,226,228,230,232,234,236,238,240,242,244,246,248,250,
252,254,256,258,260,262,264,266,268,270,272,274,276,278,280,282,284,286,
288,290,292,294,296,298,300,302,304,306,308,310,312,314,316,318,320,322,
324,326,328,330,332,334,336,338,340,342,344,346,348,350,352,354,356,358,
360,362,364,366,368,370,372,374,376,378,380,382,384,386,388,390,392,394,
396,398,400,402,404,406,408,410,412,414,416,418,420,422,424,426,428,430,
432,434,436,438,440,442,444,446,448,450,452,454,456,458,460,462,464,466,
468,470,472,474,476,478,480,482,484,486,488,490,492,494,496,498,500,502,
504,506,508,510,512,514,516,518,520,522,524,526,528,530,532,534,536,538,
540,542,544,546,548,550,552,554,556,558,560,562,564,566,568,570,572,574,
576,578,580,582,584,586,588,590,592,594,596,598,600,602,604,606,608,610,
612,614,616,618,620,622,624,626,628,630,632,634,636,638,640,642,644,646,
648,650,652,654,656,658,660,662,664,666,668,670,672,674,676,678,680,682,
684,686,688,690,692,694,696,698,700,702,704,706,708,710,712,714,716,718,
720,722,724,726,728,730,732,734,736,738,740,742,744,746,748,750,752,754,
756,758,760,762,764,766,768,770,772,774,776,778,780,782,784,786,788,790,
792,794,796,798,800,802,804,806,808,810,812,814,816,818,820,822,824,826,
828,830,832,834,836,838,0,2,4,6,8,10,12,14,16,18,20,22,
24,26,28,30,32,34,36,38,40,42,44,46,48,50,52,54,56,58,
60,62,64,66,68,70,72,74,76,78,80,82,84,86,88,90,92,94,
96,98,100,102,104,106,108,110,112,114,116,118,120,122,124,126,128,130,
132,134,136,138,140,142,144,146,148,150,152,154,156,158,160,162,164,166,
168,170,172,174,176,178,180,182,184,186,188,190,192,194,196,198,200,202,
204,206,208,210,212,214,216,218,220,222,224,226,228,230,232,234,236,238,
240,242,244,246,248,250,252,254,256,258,260,262,264,266,268,270,272,274,
276,278,280,282,284,286,288,290,292,294,296,298,300,302,304,306,308,310,
312,314,316,318,320,322,324,326,328,330,332,334,336,338,340,342,344,346,
348,350,352,354,356,358,360,362,364,366,368,370,372,374,376,378,380,382,
384,386,388,390,392,394,396,398,400,402,404,406,408,410,412,414,416,418,
420,422,424,426,428,430,432,434,436,438,440,442,444,446,448,450,452,454,
456,458,460,462,464,466,468,470,472,474,476,478,480,482,484,486,488,490,
492,494,496,498,500,502,504,506,508,510,512,514,516,518,520,522,524,526,
528,530,532,534,536,538,540,542,544,546,548,550,552,554,556,558,560,562,
564,566,568,570,572,574,576,578,580,582,584,586,588,590,592,594,596,598,
600,602,604,606,608,610,612,614,616,618,620,622,624,626,628,630,632,634,
636,638,640,642,644,646,648,650,652,654,656,658,660,662,664,666,668,670,
672,674,676,678,680,682,684,686,688,690,692,694,696,698,700,702,704,706,
708,710,712,714,716,718,720,722,724,726,728,730,732,734,736,738,740,742,
744,746,748,750,752,754,756,758,760,762,764,766,768,770,772,774,776,778,
780,782,784,786,788,790,792,794,796,798,800,802,804,806,808,810,812,814,
816,818,820,822,824,826,828,830,832,834,836,838,0,2,4,6,8,10,
12,14,16,18,20,22,24,26,28,30,32,34,36,38,40,42,44,46,
48,50,52,54,56,58,60,62,64,66,68,70,72,74,76,78,80,82,
84,86,88,90,92,94,96,98,100,102,104,106,108,110,112,114,116,118,
120,122,124,126,128,130,132,134,136,138,140,142,144,146,148,150,152,154,
156,158,160,162,164,166,168,170,172,174,176,178,180,182,184,186,188,190,
192,194,196,198,200,202,204,206,208,210,212,214,216,218,220,222,224,226,
228,230,232,234,236,238,240,242,244,246,248,250,252,254,256,258,260,262,
264,266,268,270,272,274,276,278,280,282,284,286,288,290,292,294,296,298,
300,302,304,306,308,310,312,314,316,318,320,322,324,326,328,330,332,334
  ];

  // === Registro de carriers NR (del 5G_PCI_RSI_Planner_12) =================
  // Fuente: pantallas de carrier OSS/NetAct. Define banda, dúplex (TDD/FDD),
  // ancho de banda y frecuencias centrales DL/UL (MHz). Se usa para color/etiquetas
  // y para los FACTORES ESPECTRALES de interferencia DL (PCI/PSS) y UL (RSI/PRACH).
  // dlCenterMHz null → centro desconocido → aislado espectralmente (factor 0).
  var CARRIER_REGISTRY = {
    'NR_3500MHz':    { band: 'n78', duplex: 'TDD', bwMHz: 20,  dlCenterMHz: 3590.010, ulCenterMHz: 3590.010, arfcn: 639334 },
    'NR_3500MHz_C2': { band: 'n78', duplex: 'TDD', bwMHz: 20,  dlCenterMHz: 3489.990, ulCenterMHz: 3489.990, arfcn: 632666 },
    'NR_2600MHz':    { band: 'n7',  duplex: 'FDD', bwMHz: 15,  dlCenterMHz: 2642.500, ulCenterMHz: 2522.500, arfcn: 528500 },
    'NR_n78_100MHz': { band: 'n78', duplex: 'TDD', bwMHz: 100, dlCenterMHz: 3549.990, ulCenterMHz: 3549.990, arfcn: 636666 }
  };
  var ADJ_GUARD_MHZ = 5; // separación borde-a-borde ≤ esto → canal adyacente
  function _carInfo(name) { return CARRIER_REGISTRY[name] || null; }
  function _specFactor(a, b, key) {
    if (a === b) return 1.0;
    var x = _carInfo(a), y = _carInfo(b);
    if (!x || !y || x[key] == null || y[key] == null) return 0;
    var aLo = x[key] - x.bwMHz / 2, aHi = x[key] + x.bwMHz / 2;
    var bLo = y[key] - y.bwMHz / 2, bHi = y[key] + y.bwMHz / 2;
    var gap = Math.max(aLo, bLo) - Math.min(aHi, bHi); // negativo → solapan
    if (gap < 0) return 0.8;
    if (gap <= ADJ_GUARD_MHZ) return 0.3;
    return 0;
  }
  /** Factor espectral DL (rige PCI/PSS): 1 mismo carrier · 0.8 solapan · 0.3 adyacente · 0 separados. */
  function carrierSpectralFactor(a, b) { return _specFactor(a, b, 'dlCenterMHz'); }
  /** Factor espectral UL (rige RSI/PRACH): para TDD UL=DL; para FDD el bloque UL va aparte. */
  function carrierSpectralFactorUL(a, b) { return _specFactor(a, b, 'ulCenterMHz'); }

  // === Geometría / geodesia ===============================================
  function toRad(d) { return d * Math.PI / 180.0; }
  function toDeg(r) { return r * 180.0 / Math.PI; }

  /** Distancia de círculo máximo (haversine) en metros entre dos lat/lon. */
  function haversine_m(lat1, lon1, lat2, lon2) {
    var p1 = toRad(lat1), p2 = toRad(lat2);
    var dphi = toRad(lat2 - lat1), dlam = toRad(lon2 - lon1);
    var a = Math.sin(dphi / 2) * Math.sin(dphi / 2) +
            Math.cos(p1) * Math.cos(p2) * Math.sin(dlam / 2) * Math.sin(dlam / 2);
    a = Math.max(0, Math.min(1, a));
    return 2.0 * RADIO_TIERRA_M * Math.asin(Math.sqrt(a));
  }

  /** Punto destino siguiendo un rumbo (azimuth desde el norte) y distancia. */
  function puntoDestino(lat, lon, rumboDeg, distM) {
    var theta = toRad(rumboDeg);
    var delta = distM / RADIO_TIERRA_M;
    var phi1 = toRad(lat), lam1 = toRad(lon);
    var sinPhi2 = Math.sin(phi1) * Math.cos(delta) +
                  Math.cos(phi1) * Math.sin(delta) * Math.cos(theta);
    sinPhi2 = Math.max(-1, Math.min(1, sinPhi2));
    var phi2 = Math.asin(sinPhi2);
    var lam2 = lam1 + Math.atan2(
      Math.sin(theta) * Math.sin(delta) * Math.cos(phi1),
      Math.cos(delta) - Math.sin(phi1) * sinPhi2
    );
    var lat2 = toDeg(phi2);
    var lon2 = ((toDeg(lam2) + 540.0) % 360.0) - 180.0;
    return [lat2, lon2];
  }

  /** Vértices [lat, lon] del abanico (centro + arco de n_arco puntos). */
  function wedgeLatLon(lat, lon, azDeg, hbeamDeg, radioM, nArco) {
    var pts = [[lat, lon]];
    var ini = azDeg - hbeamDeg / 2.0, fin = azDeg + hbeamDeg / 2.0;
    var n = nArco | 0;
    for (var i = 0; i < n; i++) {
      var rumbo = ini + (fin - ini) * (i / (n - 1));
      pts.push(puntoDestino(lat, lon, rumbo, radioM));
    }
    return pts;
  }

  // --- Proyección UTM (Transverse Mercator WGS84, EPSG 326xx/327xx) -------
  var WGS84_A = 6378137.0, WGS84_F = 1 / 298.257223563;
  function epsgUTM(lat, lon) {
    var zona = Math.floor((lon + 180.0) / 6.0) + 1;
    zona = Math.min(Math.max(zona, 1), 60);
    return (lat >= 0 ? 32600 : 32700) + zona;
  }
  /** Proyecta [lat, lon] (grados) a [x, y] en metros UTM para el EPSG dado. */
  function projUTM(lat, lon, epsg) {
    var zona = epsg % 100;
    var sur = epsg >= 32700;
    var a = WGS84_A, f = WGS84_F;
    var e2 = f * (2 - f);
    var ep2 = e2 / (1 - e2);
    var k0 = 0.9996;
    var lon0 = toRad((zona - 1) * 6 - 180 + 3);
    var phi = toRad(lat), lam = toRad(lon);
    var N = a / Math.sqrt(1 - e2 * Math.sin(phi) * Math.sin(phi));
    var T = Math.tan(phi) * Math.tan(phi);
    var C = ep2 * Math.cos(phi) * Math.cos(phi);
    var A = Math.cos(phi) * (lam - lon0);
    var M = a * (
      (1 - e2 / 4 - 3 * e2 * e2 / 64 - 5 * e2 * e2 * e2 / 256) * phi -
      (3 * e2 / 8 + 3 * e2 * e2 / 32 + 45 * e2 * e2 * e2 / 1024) * Math.sin(2 * phi) +
      (15 * e2 * e2 / 256 + 45 * e2 * e2 * e2 / 1024) * Math.sin(4 * phi) -
      (35 * e2 * e2 * e2 / 3072) * Math.sin(6 * phi)
    );
    var x = k0 * N * (A + (1 - T + C) * A * A * A / 6 +
            (5 - 18 * T + T * T + 72 * C - 58 * ep2) * Math.pow(A, 5) / 120) + 500000.0;
    var y = k0 * (M + N * Math.tan(phi) * (A * A / 2 +
            (5 - T + 9 * C + 4 * C * C) * Math.pow(A, 4) / 24 +
            (61 - 58 * T + T * T + 600 * C - 330 * ep2) * Math.pow(A, 6) / 720));
    if (sur) y += 10000000.0;
    return [x, y];
  }

  // --- Polígonos: clipping convexo (Sutherland-Hodgman) + área shoelace ---
  function areaShoelace(poly) {
    var s = 0, n = poly.length;
    for (var i = 0; i < n; i++) {
      var a = poly[i], b = poly[(i + 1) % n];
      s += a[0] * b[1] - b[0] * a[1];
    }
    return s / 2.0;
  }
  function orientarCCW(poly) {
    return areaShoelace(poly) < 0 ? poly.slice().reverse() : poly;
  }
  /** Recorta el polígono `subj` contra el polígono CONVEXO `clip` (CCW). */
  function clipConvex(subj, clip) {
    var output = subj;
    for (var i = 0; i < clip.length; i++) {
      var A = clip[i], B = clip[(i + 1) % clip.length];
      var input = output;
      output = [];
      if (input.length === 0) break;
      var edgeX = B[0] - A[0], edgeY = B[1] - A[1];
      var dentro = function (p) { return edgeX * (p[1] - A[1]) - edgeY * (p[0] - A[0]) >= 0; };
      var inter = function (p, q) {
        var x1 = p[0], y1 = p[1], x2 = q[0], y2 = q[1];
        var d1 = edgeX * (y1 - A[1]) - edgeY * (x1 - A[0]);
        var d2 = edgeX * (y2 - A[1]) - edgeY * (x2 - A[0]);
        var t = d1 / (d1 - d2);
        return [x1 + t * (x2 - x1), y1 + t * (y2 - y1)];
      };
      for (var j = 0; j < input.length; j++) {
        var cur = input[j], prev = input[(j + input.length - 1) % input.length];
        var curIn = dentro(cur), prevIn = dentro(prev);
        if (curIn) {
          if (!prevIn) output.push(inter(prev, cur));
          output.push(cur);
        } else if (prevIn) {
          output.push(inter(prev, cur));
        }
      }
    }
    return output;
  }
  /** Área de intersección (m²) entre dos wedges dados como vértices lat/lon. */
  function areaInterseccionM2(wedgeA, wedgeB, epsg) {
    var pa = orientarCCW(wedgeA.map(function (p) { return projUTM(p[0], p[1], epsg); }));
    var pb = orientarCCW(wedgeB.map(function (p) { return projUTM(p[0], p[1], epsg); }));
    var inter = clipConvex(pa, pb);
    if (inter.length < 3) return 0.0;
    return Math.abs(areaShoelace(inter));
  }

  // === Utilidades de celda ================================================
  function sufijoMM(cellname) {
    var m = PATRON_MM.exec(String(cellname));
    return m ? parseInt(m[1], 10) : null;
  }
  function es32t32r(txrxmode, cellname) {
    var modo = String(txrxmode || '').toUpperCase().replace(/\s/g, '');
    return modo === '32T32R' || sufijoMM(cellname || '') !== null;
  }
  function hbeamCelda(txrxmode, cellname, params) {
    return es32t32r(txrxmode, cellname) ? params.hbeam_mm_deg : params.hbeam_convencional_deg;
  }
  function azimuthEfectivo(cellname, azimuth) {
    var n = sufijoMM(cellname);
    var az = Number(azimuth) + (n ? OFFSETS_MM[n] : 0.0);
    return ((az % 360.0) + 360.0) % 360.0;
  }
  /** Número de sector = 8.º carácter del Cellname (1-indexado) leído como dígito.
   *  Ej.: "LIL07391C2_Estadio_Nacional" → carácter 8 = '1' → 1. Devuelve null si
   *  el nombre tiene < 8 caracteres o el 8.º no es un dígito. Se usa en la regla
   *  de PSS por proximidad de número de sector (paso 11). */
  function numeroSector(cellname) {
    var ch = String(cellname == null ? '' : cellname).charAt(7);
    return /[0-9]/.test(ch) ? parseInt(ch, 10) : null;
  }
  /** RADIO TA de una fila existente: cascada exacta del Excel (paso 6). */
  function radioTaFila(fila, umbral) {
    var acum = 0.0;
    for (var i = 0; i < BINS_TA.length; i++) {
      var v = fila[BINS_TA[i][0]];
      acum += (v === undefined || v === null || isNaN(v)) ? 0.0 : Number(v);
      if (acum > umbral) return BINS_TA[i][1];
    }
    return RADIO_TA_DEFECTO;
  }

  function mediana(arr) {
    if (!arr.length) return NaN;
    var s = arr.slice().sort(function (a, b) { return a - b; });
    var m = Math.floor(s.length / 2);
    return s.length % 2 ? s[m] : (s[m - 1] + s[m]) / 2.0;
  }
  /** Distancia angular mínima (0–180°) entre dos azimuths. */
  function angDist(a, b) {
    var d = Math.abs(a - b) % 360.0;
    return d > 180.0 ? 360.0 - d : d;
  }
  /** Rumbo inicial (azimuth desde el norte, 0–360°) de (lat1,lon1) → (lat2,lon2). */
  function rumbo(lat1, lon1, lat2, lon2) {
    var p1 = toRad(lat1), p2 = toRad(lat2), dl = toRad(lon2 - lon1);
    var y = Math.sin(dl) * Math.cos(p2);
    var x = Math.cos(p1) * Math.sin(p2) - Math.sin(p1) * Math.cos(p2) * Math.cos(dl);
    return ((toDeg(Math.atan2(y, x)) % 360.0) + 360.0) % 360.0;
  }
  /** Azimuth del CENTRO del sector de una celda (descuenta el offset del sub-beam MM). */
  function _centroAz(c) {
    return c.beam ? (((c.azEf - OFFSETS_MM[c.beam]) % 360.0) + 360.0) % 360.0 : c.azEf;
  }
  /** Distancia de número de sector CIRCULAR (módulo = máx−mín+1 del sitio). */
  function _circSec(a, b, mod) {
    var raw = Math.abs(a - b);
    return mod > 1 ? Math.min(raw, mod - raw) : raw;
  }
  /** ¿El sub-beam de borde `mm` apunta hacia el sector de `otro`? (el borde está
   *  angularmente más cerca del azimuth del vecino que el centro del sector MM). */
  function _bordeApunta(mm, otro) {
    var c = _centroAz(mm), dir = _centroAz(otro);
    return angDist(mm.azEf, dir) < angDist(c, dir) - 1e-9;
  }
  /** Visibilidad MUTUA direccional acotada por RADIO_TA (caso inter-sitio): cada
   *  celda debe "ver" a la otra dentro de su cono (azEf ± HBEAM/2) y de su radio. */
  function _seVenMutuo(a, b) {
    var d = haversine_m(a.lat, a.lon, b.lat, b.lon);
    if (d > a.radioTa || d > b.radioTa) return false;
    var ab = rumbo(a.lat, a.lon, b.lat, b.lon), ba = rumbo(b.lat, b.lon, a.lat, a.lon);
    return angDist(ab, a.azEf) <= a.hbeam / 2.0 && angDist(ba, b.azEf) <= b.hbeam / 2.0;
  }
  /** CRITERIO DE CONFUSIÓN (mod-3) geométrico. El llamador garantiza: mismo
   *  carrier, mismo PSS y PCI distinto. a,b = { lat, lon, azEf, hbeam, sector
   *  (nº|null), beam (1..4|null), radioTa }. `mismoSitio` y `modSitio` (módulo
   *  circular de sectores del sitio) describen la relación de sitio. Devuelve el
   *  motivo ('sitio-adyacente' | 'mm-consecutivo' | 'mm-borde-sector' |
   *  'inter-sitio-visible') o null si NO hay confusión. */
  function confusionMod3(a, b, mismoSitio, modSitio) {
    if (mismoSitio) {
      var sa = a.sector, sb = b.sector;
      if (sa != null && sb != null && sa === sb) {
        // Mismo sector: solo sub-beams MM consecutivos (|n−m| = 1).
        if (a.beam && b.beam && Math.abs(a.beam - b.beam) === 1) return 'mm-consecutivo';
        return null;
      }
      if (sa == null || sb == null) return null;
      if (_circSec(sa, sb, modSitio) !== 1) return null; // sectores deben ser consecutivos (circular)
      var aMM = !!a.beam, bMM = !!b.beam;
      if (!aMM && !bMM) return 'sitio-adyacente'; // caso 1
      // Caso 3: el MM debe ser sub-beam de BORDE (1 o 4) y apuntar al otro sector.
      if (aMM && (a.beam === 1 || a.beam === 4) && _bordeApunta(a, b)) return 'mm-borde-sector';
      if (bMM && (b.beam === 1 || b.beam === 4) && _bordeApunta(b, a)) return 'mm-borde-sector';
      return null;
    }
    return _seVenMutuo(a, b) ? 'inter-sitio-visible' : null;
  }

  // === Pool de celdas existentes por carrier (paso 2 + índices) ===========
  // Optimizado para grandes volúmenes (~90 k celdas): arreglos planos con
  // inserción incremental (sin reconstruir todo ni reaplicar regex en cada
  // celda al incorporar planificadas) y una única pasada de distancias por
  // celda a planificar que produce a la vez las 30 más cercanas, la vecindad
  // ampliada y los mínimos de reuso por PCI y por PCI mod-30.
  // Columnas TA (8) para detectar celdas sin datos de tráfico.
  var COLS_TA_ALL = BINS_TA.map(function (b) { return b[0]; })
    .concat(['ta_7215_15015_mts_porc', 'ta_mayor_a_15015_mts_porc']);
  function sumaTa(f) {
    var s = 0;
    for (var i = 0; i < COLS_TA_ALL.length; i++) {
      var v = f[COLS_TA_ALL[i]];
      if (v !== undefined && v !== null && !isNaN(v)) s += Number(v);
    }
    return s;
  }

  function PoolCarrier(filas, params) {
    this.params = params;
    this.lat = []; this.lon = []; this.pci = []; this.pss = []; this.pciMod30 = [];
    this.radioTa = []; this.azEf = []; this.hbeam = []; this.nombre = [];
    this.azimuth = []; this.txrxmode = []; this.carrier = []; this.sinTa = [];
    this._promCache = {}; // caché del promedio de 30 cercanas (para celdas sin TA)
    this.n = 0;
    for (var i = 0; i < filas.length; i++) this._push(filas[i]);
    this._buildGrid();
  }
  /** Índice espacial (rejilla ~2 km) para buscar vecinas cercanas en O(local),
   *  usado por promedioCercanas (celdas sin TA) sin recorrer todo el pool. */
  PoolCarrier.prototype._buildGrid = function () {
    this._cell = 0.02; // grados (~2.2 km)
    this._grid = {};
    for (var i = 0; i < this.n; i++) {
      var key = Math.floor(this.lat[i] / this._cell) + ',' + Math.floor(this.lon[i] / this._cell);
      (this._grid[key] || (this._grid[key] = [])).push(i);
    }
  };
  /** Calcula los escalares derivados de una celda y los añade (O(1)). */
  PoolCarrier.prototype._push = function (f) {
    var p = this.params;
    var nombre = String(f.Cellname), az = Number(f.Azimuth);
    var pci = Math.round(Number(f.Phycellid));
    this.lat.push(Number(f.Latitud));
    this.lon.push(Number(f.Longitud));
    this.pci.push(pci);
    this.pss.push(((pci % 3) + 3) % 3);
    this.pciMod30.push(((pci % 30) + 30) % 30);
    var forzado = (f._radio_ta_m !== undefined && f._radio_ta_m !== null && !isNaN(f._radio_ta_m));
    // "Sin TA": no trae datos de tráfico (las 8 columnas suman ≈0). En ese caso
    // se usará el promedio de las 30 más cercanas (radioEfectivo).
    var sinDatos = !forzado && sumaTa(f) <= 1e-9;
    this.sinTa.push(sinDatos);
    this.radioTa.push(forzado ? Number(f._radio_ta_m) : radioTaFila(f, p.umbral_ta));
    this.nombre.push(nombre);
    this.azimuth.push(az);
    this.txrxmode.push(f.Txrxmode);
    this.carrier.push(String(f.Carrier).trim().toUpperCase());
    this.azEf.push(azimuthEfectivo(nombre, az));
    // HBEAM: explícito de la antena (5G NR: 'Horizontal Beamwidth' → _hbeam_deg)
    // si viene; si no, por modo (32T32R/4T4R) como en 4G.
    var hbExp = (f._hbeam_deg !== undefined && f._hbeam_deg !== null && !isNaN(f._hbeam_deg) && Number(f._hbeam_deg) > 0) ? Number(f._hbeam_deg) : null;
    this.hbeam.push(hbExp !== null ? hbExp : hbeamCelda(f.Txrxmode, nombre, p));
    this.n++;
    // Si el grid ya existe (celda incorporada en la corrida), añadirla al bucket
    // para que las siguientes celdas la "vean" en la vecindad (no repetir PCI).
    if (this._grid) {
      var idx = this.n - 1;
      var gk = Math.floor(this.lat[idx] / this._cell) + ',' + Math.floor(this.lon[idx] / this._cell);
      (this._grid[gk] || (this._grid[gk] = [])).push(idx);
    }
  };
  /** Promedio de distancia a las k=30 celdas más cercanas a la celda i
   *  (excluyendo i misma), vía rejilla espacial (O(local)). Cacheado. */
  PoolCarrier.prototype.promedioCercanas = function (i) {
    if (this._promCache[i] !== undefined) return this._promCache[i];
    var la = this.lat, lo = this.lon, lat = la[i], lon = lo[i], cell = this._cell, grid = this._grid;
    var gx = Math.floor(lat / cell), gy = Math.floor(lon / cell);
    var k = Math.min(this.params.k_cercanas | 0, Math.max(1, this.n - 1));
    var cand = [], ring = 0, enough = false, extra = 0;
    while (ring <= 200) {
      for (var dx = -ring; dx <= ring; dx++) {
        for (var dy = -ring; dy <= ring; dy++) {
          if (Math.max(Math.abs(dx), Math.abs(dy)) !== ring) continue; // solo el borde del anillo
          var b = grid[(gx + dx) + ',' + (gy + dy)];
          if (b) for (var m = 0; m < b.length; m++) if (b[m] !== i) cand.push(b[m]);
        }
      }
      if (enough) { extra++; if (extra >= 1) break; } // un anillo extra de seguridad
      else if (cand.length >= k) enough = true;
      ring++;
    }
    var dists = cand.map(function (j) { return haversine_m(lat, lon, la[j], lo[j]); });
    dists.sort(function (a, b) { return a - b; });
    var kk = Math.min(k, dists.length), sum = 0;
    for (var z = 0; z < kk; z++) sum += dists[z];
    var avg = kk ? sum / kk : this.radioTa[i];
    this._promCache[i] = avg;
    return avg;
  };
  /** Índices de las k celdas MÁS CERCANAS a i (mismo pool = mismo carrier),
   *  SIN límite de distancia. Barrido lineal directo O(n) con ranking planar
   *  (Δlat² + (Δlon·cos lat)²): sin trigonometría y con el MISMO orden que la
   *  distancia real a estas escalas. Es más rápido que la rejilla cuando el tope
   *  de vecinas (k) es grande respecto al pool. Lo usa la auditoría de existentes. */
  PoolCarrier.prototype.nCercanasIdx = function (i, k) {
    var la = this.lat, lo = this.lon, lat = la[i], lon = lo[i], n = this.n;
    k = Math.min(k | 0, Math.max(0, n - 1));
    if (k <= 0) return [];
    var coslat = Math.cos(lat * 0.017453292519943295);
    var dd = [];
    for (var j = 0; j < n; j++) {
      if (j === i) continue;
      var dla = la[j] - lat, dlo = (lo[j] - lon) * coslat;
      dd.push([j, dla * dla + dlo * dlo]);
    }
    dd.sort(function (a, b) { return a[1] - b[1]; });
    if (dd.length > k) dd.length = k;
    var out = []; for (var z = 0; z < dd.length; z++) out.push(dd[z][0]);
    return out;
  };
  /** RADIO TA efectivo: el de la cascada; si la celda no trae datos de TA, el
   *  promedio de las 30 más cercanas. */
  PoolCarrier.prototype.radioEfectivo = function (i) {
    return this.sinTa[i] ? this.promedioCercanas(i) : this.radioTa[i];
  };
  /** Vía rejilla espacial (O(local)): 30 más cercanas (toda la banda, para PSS)
   *  y la vecindad de reuso de PCI del MISMO carrier (unión {≤radio} ∪ {max más
   *  cercanas}) con sus mínimos de reuso por PCI / PCI mod-30. Recolecta un
   *  superconjunto (anillos que cubren el radio y ≥ max del carrier, + 1 anillo
   *  de seguridad), así el resultado es idéntico al de recorrer todo el pool. */
  PoolCarrier.prototype.consulta = function (lat, lon, carrier, soloIdx30) {
    var p = this.params, cell = this._cell, grid = this._grid;
    var la = this.lat, lo = this.lon, pc = this.pci, pm = this.pciMod30, car = this.carrier;
    var gx = Math.floor(lat / cell), gy = Math.floor(lon / cell);
    var k = Math.min(p.k_cercanas | 0, this.n);
    var maxVec = p.max_vecindad, radioVec = p.radio_vecindad_m;
    var ringsRadio = Math.ceil(radioVec / (cell * 110000)) + 1; // anillos que cubren el radio
    // soloIdx30 (paso de ordenamiento): basta con las 30 más cercanas → se
    // expande hasta tenerlas y se omite el costoso barrido de 10 km/400.
    var cand = [], ring = 0, scc = 0, bandc = 0, done = false;
    while (ring <= 60) {
      for (var dx = -ring; dx <= ring; dx++) {
        for (var dy = -ring; dy <= ring; dy++) {
          if (Math.max(Math.abs(dx), Math.abs(dy)) !== ring) continue; // borde del anillo
          var b = grid[(gx + dx) + ',' + (gy + dy)]; if (!b) continue;
          for (var m = 0; m < b.length; m++) { var ii = b[m]; cand.push(ii); bandc++; if (carrier == null || car[ii] === carrier) scc++; }
        }
      }
      if (done) break; // ya se recolectó el anillo extra de seguridad
      if (bandc >= k && (soloIdx30 || (ring >= ringsRadio && (carrier == null || scc >= maxVec)))) done = true;
      ring++;
    }
    var cd = new Array(cand.length);
    for (var z = 0; z < cand.length; z++) cd[z] = haversine_m(lat, lon, la[cand[z]], lo[cand[z]]);
    var ord = cand.map(function (_, z) { return z; });
    ord.sort(function (a, b) { return cd[a] - cd[b] || cand[a] - cand[b]; });
    var kk = Math.min(k, cand.length), idx30 = new Array(kk), dist30 = new Array(kk);
    for (var j = 0; j < kk; j++) { idx30[j] = cand[ord[j]]; dist30[j] = cd[ord[j]]; }
    if (soloIdx30) return { idx30: idx30, dist30: dist30, idxVec: [], dentro: 0, minByPci: null, minByMod30: null };
    // Vecindad de reuso de PCI: mismo carrier; unión {≤ radio} ∪ {max más cercanas}.
    var minByPci = new Float64Array(1008); minByPci.fill(Infinity);
    var minByMod30 = new Float64Array(30); minByMod30.fill(Infinity);
    var idxVec = [], dentro = 0, cnt = 0;
    for (var o = 0; o < ord.length; o++) {
      var jj = cand[ord[o]], d = cd[ord[o]];
      if (carrier != null && car[jj] !== carrier) continue;
      if (cnt < maxVec || d <= radioVec) {
        idxVec.push(jj); if (d <= radioVec) dentro++;
        if (d < minByPci[pc[jj]]) minByPci[pc[jj]] = d;
        if (d < minByMod30[pm[jj]]) minByMod30[pm[jj]] = d;
        cnt++;
      } else break;
    }
    return { idx30: idx30, dist30: dist30, idxVec: idxVec, dentro: dentro, minByPci: minByPci, minByMod30: minByMod30 };
  };
  /** Mínimos de reuso GLOBALES (todo el pool, mismo carrier) por PCI, RSI y
   *  PCI mod 30 desde (lat, lon). Una pasada O(n). Sustenta el criterio del
   *  paso 11 de MAXIMIZAR la distancia de reuso: los candidatos están libres
   *  en la vecindad, así que su reuso real está más allá de ella. En NR el RSI
   *  se repite con OTRO PCI (RSI = (2·PCI) mod 840 → PCI y PCI±420 comparten
   *  RSI), por eso el reuso de RSI es un criterio con peso propio. */
  PoolCarrier.prototype.reusoGlobal = function (lat, lon, carrier) {
    var porPci = new Float64Array(1008); porPci.fill(Infinity);
    var porMod30 = new Float64Array(30); porMod30.fill(Infinity);
    var porRsi = {}; // RSI → distancia mínima
    for (var i = 0; i < this.n; i++) {
      if (carrier != null && this.carrier[i] !== carrier) continue;
      var d = haversine_m(lat, lon, this.lat[i], this.lon[i]);
      var pc = this.pci[i];
      if (d < porPci[pc]) porPci[pc] = d;
      if (d < porMod30[this.pciMod30[i]]) porMod30[this.pciMod30[i]] = d;
      var rs = TABLA_RSI[pc];
      if (rs !== undefined && (porRsi[rs] === undefined || d < porRsi[rs])) porRsi[rs] = d;
    }
    return { porPci: porPci, porMod30: porMod30, porRsi: porRsi };
  };
  PoolCarrier.prototype.agregar = function (fila) { this._push(fila); };

  // === Planificador (orquesta los 12 pasos) ===============================
  function Planificador(existentes, params) {
    this.params = asignaDefaults(params);
    this.existentes = existentes; // array de objetos celda existente
    this.claves = this.params.claves_carrier.slice();
    this.pools = {}; // clave → PoolCarrier
    this.asignadosSitio = {}; // clave de sitio → [{grupo, carrier, az, pss, pci, sector, beam}, ...]
    this._sitios = []; // centros de sitio para el agrupamiento por tolerancia
    this._sssSitio = {}; // "clave_sitio|grupo" → SSS (grupo de PCI) común del sitio
    this._sectoresPlanSG = {}; // "clave_sitio|grupo" → { sectorId: true } de las celdas a planificar (reserva de PSS por sector)
    this._planPorSG = {}; // "clave_sitio|grupo" → [filas a planificar del sitio] (coloreo conjunto)
    this._colorSG = {}; // "clave_sitio|grupo" → { cellname: pss } resultado del coloreo conjunto (cacheado)
  }
  /** Identificador de SECTOR FÍSICO dentro de un sitio: el número de sector (8.º
   *  carácter del nombre) si existe; si no, el azimuth efectivo redondeado. Sirve
   *  para reservar un PSS distinto por sector y confinar los repasos a los
   *  sub-beams MM (varias celdas del MISMO sector). */
  Planificador.prototype._sectorIdDe = function (sector, az) {
    return (sector !== null && sector !== undefined) ? ('s' + sector) : ('az' + Math.round(az));
  };
  /** Precalcula, por sitio+grupo, el conjunto de SECTORES FÍSICOS distintos entre
   *  las celdas a planificar. Permite, al elegir el PSS, reservar un PSS por cada
   *  sector pendiente y evitar que los sub-beams MM agoten los 3 PSS y "hambreen"
   *  a un sector que aún no se planifica. */
  Planificador.prototype._prepararSitios = function (plan) {
    this._sectoresPlanSG = {}; this._planPorSG = {}; this._colorSG = {};
    var p = this.params;
    for (var i = 0; i < plan.length; i++) {
      var fila = plan[i];
      var lat = Number(fila.Latitud), lon = Number(fila.Longitud);
      var nombre = String(fila.Cellname), az = Number(fila.Azimuth);
      var azEf = p.ajustar_azimuth_mm_plan ? azimuthEfectivo(nombre, az) : az;
      var SG = this._claveSitio(lat, lon) + '|' + this._grupoCarrier(fila.Carrier);
      var sid = this._sectorIdDe(numeroSector(nombre), azEf);
      (this._sectoresPlanSG[SG] || (this._sectoresPlanSG[SG] = {}))[sid] = true;
      (this._planPorSG[SG] || (this._planPorSG[SG] = [])).push(fila);
    }
  };

  /** COLOREO CONJUNTO POR SITIO. Asigna PSS a TODAS las celdas a planificar de un
   *  sitio+grupo a la vez (no celda por celda), minimizando las coincidencias de
   *  PSS PONDERADAS POR CERCANÍA ANGULAR: dos celdas con el mismo PSS penalizan
   *  más cuanto más cerca están en azimuth, y más aún si son de sectores
   *  distintos. Así los sectores macro quedan distintos, los sub-beams MM
   *  conservan PSS variados, y el repaso inevitable (LTE solo tiene 3 PSS) cae
   *  entre las celdas MÁS separadas. Las gemelas espejo C2/C3 (mismo sector y
   *  azimuth efectivo) son el mismo nodo → mismo PSS. Cacheado por sitio. */
  Planificador.prototype._colorearSitio = function (SG) {
    if (this._colorSG[SG]) return this._colorSG[SG];
    var filas = this._planPorSG[SG] || [];
    // Nodos = haz físico único (sector + azimuth efectivo redondeado). Las gemelas
    // C2/C3 del mismo sector/azimuth caen en el mismo nodo (comparten PSS).
    var byKey = {}, nodos = [], tolSitio = Math.max(this.params.radio_sitio_m || 0, 1), poolSG = null;
    for (var i = 0; i < filas.length; i++) {
      var ev = this._evaluar(filas[i], true); // soloSumas (interferencia por PSS)
      var r = ev.r; if (r.error) continue;
      var sid = this._sectorIdDe(numeroSector(r.cellname), r.azimuth_efectivo);
      var key = sid + '|' + Math.round(r.azimuth_efectivo);
      var nd = byKey[key];
      if (!nd) { nd = byKey[key] = { sector: sid, secNum: numeroSector(r.cellname), beam: sufijoMM(r.cellname), azEf: r.azimuth_efectivo, sumas: [0, 0, 0], cells: [], fijo: null,
        lat: r.lat, lon: r.lon, hbeam: (r.hbeam_plan != null ? r.hbeam_plan : hbeamCelda(r.txrxmode, r.cellname, this.params)), radioTa: r.radio_sector_m }; nodos.push(nd); }
      nd.sumas[0] += r.sumas_pss[0]; nd.sumas[1] += r.sumas_pss[1]; nd.sumas[2] += r.sumas_pss[2];
      nd.cells.push(r.cellname);
      // Celdas EXISTENTES co-sitio (mismo sitio+grupo): se siembran como nodos de
      // PSS FIJO para que el sector nuevo no repita el PSS de un sector ya
      // desplegado. Se buscan entre las 30 más cercanas con distancia ≤ tol_sitio.
      var pool = ev.pool, q = ev.q; if (pool) poolSG = pool;
      if (pool && q && q.idx30) {
        for (var t = 0; t < q.idx30.length; t++) {
          if (q.dist30[t] > tolSitio) continue;
          var pi = q.idx30[t];
          if (this._grupoCarrier(pool.carrier[pi]) !== r.grupo) continue;
          var fsid = this._sectorIdDe(numeroSector(pool.nombre[pi]), pool.azEf[pi]);
          var fkey = fsid + '|' + Math.round(pool.azEf[pi]);
          if (byKey[fkey]) continue; // ya es un nodo plan o ya sembrado
          nodos.push(byKey[fkey] = { sector: fsid, secNum: numeroSector(pool.nombre[pi]), beam: sufijoMM(pool.nombre[pi]), azEf: pool.azEf[pi], sumas: [0, 0, 0], cells: [], fijo: pool.pss[pi],
            lat: pool.lat[pi], lon: pool.lon[pi], hbeam: pool.hbeam[pi], radioTa: pool.radioEfectivo(pi) });
        }
      }
    }
    // Orden de PSS por interferencia (ascendente). Los nodos fijos (existentes)
    // no se optimizan: su orden es solo su PSS fijo.
    for (var n = 0; n < nodos.length; n++) {
      if (nodos[n].fijo != null) { nodos[n].orden = [nodos[n].fijo]; continue; }
      var s = nodos[n].sumas;
      nodos[n].orden = [0, 1, 2].sort(function (a, b) { return (s[a] - s[b]) || (a - b); });
    }
    // (D) Penalización de CONFUSIÓN INTER-SITIO: por nodo, evita el PSS de las
    // celdas de OTROS sitios (mismo grupo) que se ven mutuamente con él.
    var ext = this._penExternos(nodos, poolSG, tolSitio);
    if (ext) for (var ke = 0; ke < nodos.length; ke++) nodos[ke].extPen = ext[ke];
    var color = this._optimizarColoreo(nodos);
    var mapa = {};
    for (var z = 0; z < nodos.length; z++) {
      for (var c = 0; c < nodos[z].cells.length; c++) mapa[nodos[z].cells[c]] = color[z];
    }
    this._colorSG[SG] = mapa;
    return mapa;
  };

  /** Matriz de penalización pen[i][j] = costo si los nodos i,j comparten PSS.
   *  Cercanía angular = objetivo dominante, con penalización CUADRÁTICA: una
   *  coincidencia muy cercana (p. ej. sub-beams a 20°) pesa muchísimo más que
   *  varias lejanas, así el repaso inevitable se empuja a las celdas más
   *  separadas. Sesgo leve ×1.5 entre sectores distintos (desempate). */
  /** (D) Penalización de CONFUSIÓN INTER-SITIO por nodo. Para cada nodo plan
   *  busca, en el pool (mismo grupo, OTRO sitio), las celdas que se VEN
   *  mutuamente con él (cono ± HBEAM/2 y dentro del RADIO_TA de ambas) y suma un
   *  peso por cada una según su PSS. Devuelve [[p0,p1,p2], …] o null si no hay. */
  Planificador.prototype._penExternos = function (nodos, pool, tolSitio) {
    if (!pool) return null;
    var W = 100, cell = pool._cell, grid = pool._grid, any = false;
    var ext = nodos.map(function () { return [0, 0, 0]; });
    for (var ni = 0; ni < nodos.length; ni++) {
      var nd = nodos[ni]; if (nd.fijo != null || nd.lat == null) continue;
      var R = nd.radioTa || 0; if (R <= 0) continue;
      var a = { lat: nd.lat, lon: nd.lon, azEf: nd.azEf, hbeam: nd.hbeam, radioTa: R };
      var rings = Math.ceil(R / (cell * 110000)) + 1;
      var gx = Math.floor(nd.lat / cell), gy = Math.floor(nd.lon / cell);
      for (var dx = -rings; dx <= rings; dx++) for (var dy = -rings; dy <= rings; dy++) {
        var b = grid[(gx + dx) + ',' + (gy + dy)]; if (!b) continue;
        for (var m = 0; m < b.length; m++) {
          var j = b[m], d = haversine_m(nd.lat, nd.lon, pool.lat[j], pool.lon[j]);
          if (d <= tolSitio || d > R) continue; // co-sitio (ya tratado) o fuera de su radio
          var bc = { lat: pool.lat[j], lon: pool.lon[j], azEf: pool.azEf[j], hbeam: pool.hbeam[j], radioTa: pool.radioEfectivo(j) };
          if (!_seVenMutuo(a, bc)) continue;
          ext[ni][pool.pss[j]] += W; any = true;
        }
      }
      // Tope 600 por PSS: la confusión inter-sitio NUNCA debe superar el piso
      // mismo-sitio (1000) → jamás induce una confusión co-sitio para evitar una
      // inter-sitio (la mismo-sitio es peor y manda).
      for (var c3 = 0; c3 < 3; c3++) if (ext[ni][c3] > 600) ext[ni][c3] = 600;
    }
    return any ? ext : null;
  };
  Planificador.prototype._penNodos = function (nodos) {
    var n = nodos.length, pen = [], CONF_W = 1000;
    // Módulo circular de sectores del sitio (para la adyacencia por nº de sector).
    var mn = null, mx = null;
    for (var k = 0; k < n; k++) { var s = nodos[k].secNum; if (s != null) { if (mn == null || s < mn) mn = s; if (mx == null || s > mx) mx = s; } }
    var mod = (mn == null) ? 1 : (mx - mn + 1);
    // El costo de que dos nodos del MISMO sitio compartan PSS = el propio criterio
    // de CONFUSIÓN (confusionMod3): adyacencia por nº de sector entre sectores, por
    // nº de sub-beam dentro de un sector MM, y borde MM ↔ sector adyacente. Si el
    // par SERÍA confusión → peso alto (CONF_W, > tope inter-sitio 600); si no →
    // término leve por separación angular (desempate de dónde cae un repaso libre).
    for (var i = 0; i < n; i++) {
      pen[i] = [];
      for (var j = 0; j < n; j++) {
        if (i === j) { pen[i][j] = 0; continue; }
        var a = { sector: nodos[i].secNum, beam: nodos[i].beam, azEf: nodos[i].azEf };
        var b = { sector: nodos[j].secNum, beam: nodos[j].beam, azEf: nodos[j].azEf };
        var prox = (181 - angDist(nodos[i].azEf, nodos[j].azEf)) / 181;
        pen[i][j] = confusionMod3(a, b, true, mod) ? CONF_W : (prox * prox * 10);
      }
    }
    return pen;
  };
  /** Costo de un coloreo `col` (PSS por nodo): Σ pares con mismo PSS de pen[i][j]
   *  + Σ nodo NO fijo (rango de interferencia del PSS elegido). */
  Planificador.prototype._costoColoreo = function (nodos, col, pen) {
    var n = nodos.length, t = 0;
    for (var a = 0; a < n; a++) {
      if (nodos[a].fijo == null) {
        t += nodos[a].orden.indexOf(col[a]); // interferencia
        if (nodos[a].extPen) t += nodos[a].extPen[col[a]]; // confusión inter-sitio (D)
      }
      for (var b = a + 1; b < n; b++) if (col[a] === col[b]) t += pen[a][b];
    }
    return t;
  };
  /** Minimiza el costo del coloreo. Los nodos con `fijo` (existentes co-sitio)
   *  conservan su PSS; solo se colorean los nodos libres. Fuerza bruta si
   *  3^(nodos libres) es pequeño; si no, greedy. Determinista. */
  Planificador.prototype._optimizarColoreo = function (nodos) {
    var n = nodos.length;
    if (n === 0) return [];
    var pen = this._penNodos(nodos), self = this;
    function rango(i, c) { return nodos[i].orden.indexOf(c); }
    function costo(col) { return self._costoColoreo(nodos, col, pen); }
    // Nodos FIJOS conservan su PSS; solo se colorean los libres.
    var base = new Array(n), libres = [];
    for (var bi = 0; bi < n; bi++) { base[bi] = (nodos[bi].fijo == null) ? 0 : nodos[bi].fijo; if (nodos[bi].fijo == null) libres.push(bi); }
    var m = libres.length;
    if (m === 0) return base;
    if (Math.pow(3, m) <= 20000) { // fuerza bruta sobre los nodos plan (m ≤ 9)
      var best = null, bestC = Infinity, total = Math.pow(3, m);
      for (var code = 0; code < total; code++) {
        var col = base.slice(), x = code;
        for (var d = 0; d < m; d++) { col[libres[d]] = x % 3; x = Math.floor(x / 3); }
        var cc = costo(col);
        if (cc < bestC) { bestC = cc; best = col.slice(); }
      }
      return best;
    }
    // Greedy: nodos plan por azimuth; cada uno toma el PSS de menor costo
    // incremental contra los ya puestos (incluidos los fijos).
    var ordenN = libres.slice().sort(function (a, b) { return nodos[a].azEf - nodos[b].azEf; });
    var col2 = base.slice(), puestos = [];
    for (var pf = 0; pf < n; pf++) if (nodos[pf].fijo != null) puestos.push(pf);
    for (var oi = 0; oi < ordenN.length; oi++) {
      var idx = ordenN[oi], mejor = 0, mejorC = Infinity;
      for (var c2 = 0; c2 < 3; c2++) {
        var inc = rango(idx, c2) + (nodos[idx].extPen ? nodos[idx].extPen[c2] : 0);
        for (var p = 0; p < puestos.length; p++) { var q = puestos[p]; if (col2[q] === c2) inc += pen[idx][q]; }
        if (inc < mejorC) { mejorC = inc; mejor = c2; }
      }
      col2[idx] = mejor; puestos.push(idx);
    }
    return col2;
  };

  // === Auditoría de PSS de existentes con el MISMO algoritmo (coloreo) =====
  /** Agrupa las celdas de un pool por SITIO (clúster dentro de `tolM` metros)
   *  vía rejilla 3×3 (O(n)). Devuelve [{lat, lon, members:[idxPool...]}]. */
  Planificador.prototype._clusterSitios = function (pool, tolM) {
    var cellDeg = Math.max(tolM, 1) / 111320.0, grid = {}, centers = [];
    for (var i = 0; i < pool.n; i++) {
      var la = pool.lat[i], lo = pool.lon[i];
      var gx = Math.floor(la / cellDeg), gy = Math.floor(lo / cellDeg), best = -1;
      for (var dx = -1; dx <= 1 && best < 0; dx++) for (var dy = -1; dy <= 1 && best < 0; dy++) {
        var b = grid[(gx + dx) + ',' + (gy + dy)];
        if (b) for (var k = 0; k < b.length; k++) if (haversine_m(la, lo, centers[b[k]].lat, centers[b[k]].lon) <= tolM) { best = b[k]; break; }
      }
      if (best < 0) { best = centers.length; centers.push({ lat: la, lon: lo, members: [] }); (grid[gx + ',' + gy] = grid[gx + ',' + gy] || []).push(best); }
      centers[best].members.push(i);
    }
    return centers;
  };
  /** Sumas de interferencia por PSS de una celda EXISTENTE (pool[i]), excluyéndose
   *  a sí misma de sus 30 más cercanas (si no, se solaparía con su propio wedge).
   *  Misma geometría que el paso 6-8 del planificador. Devuelve { sumas, orden }. */
  Planificador.prototype._sumasExistente = function (pool, i) {
    var p = this.params, lat = pool.lat[i], lon = pool.lon[i];
    var q = pool.consulta(lat, lon, pool.carrier[i], true), idx = [], dist = [];
    for (var k = 0; k < q.idx30.length; k++) { if (q.idx30[k] === i) continue; idx.push(q.idx30[k]); dist.push(q.dist30[k]); }
    if (!idx.length) return { sumas: [0, 0, 0], orden: [0, 1, 2] };
    var suma = 0; for (var a = 0; a < dist.length; a++) suma += dist[a];
    var radio = suma / dist.length, epsg = epsgUTM(lat, lon);
    var wp = wedgeLatLon(lat, lon, pool.azEf[i], pool.hbeam[i], radio, p.n_arco), sumas = [0, 0, 0];
    for (var r = 0; r < idx.length; r++) {
      var j = idx[r], dd = Math.max(dist[r], p.dist_min_normalizacion_m);
      var wv = wedgeLatLon(pool.lat[j], pool.lon[j], pool.azEf[j], pool.hbeam[j], pool.radioEfectivo(j), p.n_arco);
      sumas[pool.pss[j]] += areaInterseccionM2(wp, wv, epsg) / dd;
    }
    var orden = [0, 1, 2].sort(function (x, y) { return (sumas[x] - sumas[y]) || (x - y); });
    return { sumas: sumas, orden: orden };
  };
  /** AUDITORÍA DE PSS DE LA RED EXISTENTE con el mismo algoritmo de coloreo
   *  (versión completa: incluye la interferencia geométrica). Por cada sitio+grupo
   *  re-colorea óptimamente y compara con el PSS real; marca los pares de mismo
   *  PSS que el algoritmo SEPARARÍA (coincidencias evitables), con el PSS
   *  recomendado. opts: { radio_sitio_m, cap, maxSitios, onProgress }. */
  Planificador.prototype.auditarPss = function (opts) {
    opts = opts || {};
    var tol = (opts.radio_sitio_m != null ? opts.radio_sitio_m : this.params.radio_sitio_m) || 0;
    var CAP = opts.cap || 8000, MAXS = opts.maxSitios || 50000, onProgress = opts.onProgress;
    var self = this, claves = {};
    for (var e = 0; e < this.existentes.length; e++) claves[this._claveDe(this.existentes[e])] = true;
    var clavesArr = Object.keys(claves), findings = [], sitiosEval = 0, sitiosSubopt = 0, sitiosVistos = 0;
    for (var ci = 0; ci < clavesArr.length && findings.length < CAP; ci++) {
      var pool = this._pool(clavesArr[ci]); if (!pool || pool.n < 2) continue;
      var sitios = this._clusterSitios(pool, tol);
      for (var s = 0; s < sitios.length && findings.length < CAP && sitiosVistos < MAXS; s++) {
        var members = sitios[s].members; if (members.length < 2) continue;
        sitiosVistos++;
        var porGrupo = {};
        for (var mi = 0; mi < members.length; mi++) { var g = self._grupoCarrier(pool.carrier[members[mi]]); (porGrupo[g] = porGrupo[g] || []).push(members[mi]); }
        for (var gk in porGrupo) {
          var mem = porGrupo[gk]; if (mem.length < 2) continue;
          var byKey = {}, nodos = [];
          for (var mj = 0; mj < mem.length; mj++) {
            var pidx = mem[mj], sid = self._sectorIdDe(numeroSector(pool.nombre[pidx]), pool.azEf[pidx]);
            var key = sid + '|' + Math.round(pool.azEf[pidx]), nd = byKey[key];
            if (!nd) { nd = byKey[key] = { sector: sid, secNum: numeroSector(pool.nombre[pidx]), beam: sufijoMM(pool.nombre[pidx]), azEf: pool.azEf[pidx], fijo: null, miembros: [], actual: pool.pss[pidx] }; nodos.push(nd); }
            nd.miembros.push(pidx);
          }
          if (nodos.length < 2) continue;
          sitiosEval++;
          for (var nn = 0; nn < nodos.length; nn++) { var sm = self._sumasExistente(pool, nodos[nn].miembros[0]); nodos[nn].orden = sm.orden; nodos[nn].sumas = sm.sumas; }
          var pen = self._penNodos(nodos);
          var actual = nodos.map(function (x) { return x.actual; });
          var optimo = self._optimizarColoreo(nodos);
          if (self._costoColoreo(nodos, actual, pen) <= self._costoColoreo(nodos, optimo, pen) + 1e-6) continue;
          sitiosSubopt++;
          for (var a2 = 0; a2 < nodos.length && findings.length < CAP; a2++) for (var b2 = a2 + 1; b2 < nodos.length; b2++) {
            if (actual[a2] === actual[b2] && optimo[a2] !== optimo[b2]) {
              var ra = nodos[a2].miembros[0], rb = nodos[b2].miembros[0];
              var dM = haversine_m(pool.lat[ra], pool.lon[ra], pool.lat[rb], pool.lon[rb]);
              findings.push({
                type: 'PSS_SUBOPTIMO', aType: 'exist', bType: 'exist',
                aName: pool.nombre[ra], bName: pool.nombre[rb], carrier: self._grupoCarrier(pool.carrier[ra]),
                dist: dM, w: pesoDist(dM), val: actual[a2], status: 'pending',
                aPci: pool.pci[ra], aPss: pool.pss[ra], aRsi: TABLA_RSI[pool.pci[ra]],
                bPci: pool.pci[rb], bPss: pool.pss[rb], bRsi: TABLA_RSI[pool.pci[rb]],
                sep_az: Math.round(angDist(nodos[a2].azEf, nodos[b2].azEf)), recA: optimo[a2], recB: optimo[b2]
              });
              if (findings.length >= CAP) break;
            }
          }
        }
      }
      if (onProgress) onProgress(ci + 1, clavesArr.length, findings.length);
    }
    return { findings: findings, sitiosEval: sitiosEval, sitiosSubopt: sitiosSubopt, capped: findings.length >= CAP, cap: CAP };
  };
  Planificador.prototype._claveDe = function (fila) {
    var parts = [];
    for (var i = 0; i < this.claves.length; i++) parts.push(this._norm(fila[this.claves[i]]));
    return parts.join('|');
  };
  Planificador.prototype._norm = function (v) {
    if (v === undefined || v === null) return '';
    if (typeof v === 'number') return v; // se compara numéricamente abajo
    var s = String(v).trim();
    var num = Number(s);
    return (s !== '' && !isNaN(num)) ? num : s;
  };
  Planificador.prototype._pool = function (clave, filaRef) {
    if (this.pools[clave] !== undefined) return this.pools[clave];
    var self = this, sub = [];
    for (var i = 0; i < this.existentes.length; i++) {
      if (self._claveDe(this.existentes[i]) === clave) sub.push(this.existentes[i]);
    }
    this.pools[clave] = sub.length ? new PoolCarrier(sub, this.params) : null;
    return this.pools[clave];
  };
  /** Clave de sitio. Con radio_sitio_m > 0, celdas dentro de ese radio del
   *  primer centro visto comparten clave (mismo emplazamiento físico aunque las
   *  coordenadas difieran unos metros). Con 0, usa la coordenada exacta. */
  Planificador.prototype._claveSitio = function (lat, lon) {
    var tol = this.params.radio_sitio_m || 0;
    if (tol <= 0) return lat.toFixed(6) + ',' + lon.toFixed(6);
    var best = null, bestD = Infinity;
    for (var i = 0; i < this._sitios.length; i++) {
      var s = this._sitios[i], d = haversine_m(lat, lon, s.lat, s.lon);
      if (d <= tol && d < bestD) { bestD = d; best = s; }
    }
    if (best) return best.key;
    var key = lat.toFixed(6) + ',' + lon.toFixed(6);
    this._sitios.push({ lat: lat, lon: lon, key: key });
    return key;
  };

  /** Planifica N celdas. El ORDEN es greedy: primero la celda con menor
   *  Suma_interseccion_normalizada de su PSS ganador (la de menor interferencia
   *  en su mejor PSS), y así sucesivamente. Las ya resueltas cuentan como
   *  existentes para las siguientes. */
  Planificador.prototype.planificarLote = function (plan, onProgress) {
    var inicio = (typeof performance !== 'undefined' ? performance.now() : Date.now());
    var orden = this.ordenarPorCosto(plan);
    var resultados = [];
    for (var i = 0; i < orden.length; i++) {
      var r = this.planificarCelda(orden[i]);
      resultados.push(r);
      if (!r.error) this._incorporarPlanificada(orden[i], r);
      if (onProgress) onProgress(i + 1, orden.length, r);
    }
    var fin = (typeof performance !== 'undefined' ? performance.now() : Date.now());
    return { resultados: resultados, params: this.params, duracion_s: (fin - inicio) / 1000.0 };
  };

  /** Ordena las celdas a planificar por menor Suma_interseccion_normalizada del
   *  PSS ganador (paso 9), ascendente, contra el pool original (sin incorporar).
   *  Determina el sitio/celda que se planifica primero (paso 1 del criterio). */
  Planificador.prototype.ordenarPorCosto = function (plan) {
    var self = this;
    this._prepararSitios(plan); // sectores físicos por sitio (reserva de PSS)
    var conCosto = plan.map(function (fila, idx) { return { fila: fila, idx: idx, costo: self._costoOrden(fila) }; });
    conCosto.sort(function (a, b) { return (a.costo - b.costo) || (a.idx - b.idx); });
    return conCosto.map(function (x) { return x.fila; });
  };

  /** Suma_interseccion_normalizada del PSS ganador de una celda (mínimo de las
   *  3 sumas), calculada sin alterar el estado. Inf si no hay pool. */
  Planificador.prototype._costoOrden = function (fila) {
    var ev = this._evaluar(fila, true); // soloSumas: omite la vecindad de 10 km/400
    if (ev.r.error) return Infinity;
    var s = ev.r.sumas_pss;
    return Math.min(s[0], s[1], s[2]);
  };

  /** Pasos 2 a 8: filtra el pool, calcula vecinas, wedges, áreas e
   *  intersección normalizada y las sumas por PSS. No muta el estado del sitio
   *  (no registra PSS/PCI). Devuelve {r, pool, q}. */
  Planificador.prototype._evaluar = function (fila, soloSumas) {
    var p = this.params;
    var lat = Number(fila.Latitud), lon = Number(fila.Longitud), az = Number(fila.Azimuth);
    var nombre = String(fila.Cellname);
    var azEf = p.ajustar_azimuth_mm_plan ? azimuthEfectivo(nombre, az) : az;
    var indoor = ['1', 'si', 'sí', 'true', 'indoor', 'x', 'yes'].indexOf(String(fila.Indoor || '').trim().toLowerCase()) >= 0;
    var pciIn = fila.Phycellid;
    var clave = this._claveDe(fila);

    var r = {
      cellname: nombre, carrier_clave: clave, carrier: String(fila.Carrier).trim().toUpperCase(),
      grupo: this._grupoCarrier(fila.Carrier), lat: lat, lon: lon,
      azimuth: az, azimuth_efectivo: azEf, txrxmode: String(fila.Txrxmode || ''),
      indoor: indoor, pci_entrada: (pciIn === undefined || pciIn === null || pciIn === '' || isNaN(Number(pciIn))) ? null : Math.round(Number(pciIn)),
      radio_sector_m: NaN, epsg: 0, vecinas: [], sumas_pss: { 0: 0, 1: 0, 2: 0 },
      orden_pss: [], pss_ganador: -1, pss_elegido: -1, mediana_pci: NaN,
      n_candidatos: 0, candidatos_top: [], descartados_ej: [], pci: -1, rsi: -1,
      dist_reuso_pci_m: Infinity, dist_reuso_mod30_m: Infinity, dist_reuso_rsi_m: Infinity,
      vecindad_n: 0, vecindad_dentro_70km: 0, advertencias: [], auditoria: [],
      wedge_plan: null, error: null
    };

    var pool = this._pool(clave, fila);
    if (!pool || pool.n === 0) {
      r.error = 'Sin celdas existentes para la clave ' + clave + ' (paso 2)';
      r.auditoria.push(r.error);
      return { r: r, pool: null, q: null };
    }
    r.auditoria.push('Paso 2: ' + pool.n + ' celdas existentes con clave {' +
      this.claves.join(', ') + '} = ' + clave);

    // Pasos 3 y 10 en una sola pasada O(n): 30 más cercanas, vecindad ampliada
    // y mínimos de reuso por PCI / PCI mod-30 (para el paso 11).
    var q = pool.consulta(lat, lon, String(fila.Carrier).trim().toUpperCase(), soloSumas);
    var idx30 = q.idx30, dist30 = q.dist30;
    if (idx30.length < p.k_cercanas) {
      r.advertencias.push('Solo ' + idx30.length + ' celdas existentes en el carrier (se pedían ' + p.k_cercanas + ')');
    }
    // Paso 4: promedio de distancias.
    var suma = 0; for (var a = 0; a < dist30.length; a++) suma += dist30[a];
    r.radio_sector_m = suma / dist30.length;
    r.auditoria.push('Paso 3/4: ' + idx30.length + ' vecinas; distancia promedio = ' + r.radio_sector_m.toFixed(1) + ' m');

    // Paso 5: wedge de la celda a planificar. HBEAM explícito (5G NR) si viene.
    r.epsg = epsgUTM(lat, lon);
    var hbExpPlan = (fila._hbeam_deg !== undefined && fila._hbeam_deg !== null && !isNaN(fila._hbeam_deg) && Number(fila._hbeam_deg) > 0) ? Number(fila._hbeam_deg) : null;
    var hbeamPlan = hbExpPlan !== null ? hbExpPlan : hbeamCelda(r.txrxmode, nombre, p);
    r.hbeam_plan = hbeamPlan;
    var wedgePlan = wedgeLatLon(lat, lon, azEf, hbeamPlan, r.radio_sector_m, p.n_arco);
    r.wedge_plan = wedgePlan;
    r.auditoria.push('Paso 5: sector plan AZ=' + azEf.toFixed(0) + '°, HBEAM=' +
      hbeamPlan.toFixed(0) + '°, radio=' + r.radio_sector_m.toFixed(1) + ' m, EPSG:' + r.epsg);

    // Pasos 6–7: wedges vecinos, áreas e intersección normalizada.
    var sumas = { 0: 0.0, 1: 0.0, 2: 0.0 };
    for (var rank = 0; rank < idx30.length; rank++) {
      var i = idx30[rank], dist = dist30[rank];
      var radioTa = pool.radioEfectivo(i); // sin TA → promedio de sus 30 más cercanas
      var wedgeVec = wedgeLatLon(pool.lat[i], pool.lon[i], pool.azEf[i], pool.hbeam[i], radioTa, p.n_arco);
      var area = areaInterseccionM2(wedgePlan, wedgeVec, r.epsg);
      var esCosito = dist <= p.radio_cosito_m;
      var distNorm, intersNorm;
      if (esCosito && p.politica_cosito === 'excluir') {
        distNorm = NaN; intersNorm = NaN;
      } else {
        distNorm = Math.max(dist, p.dist_min_normalizacion_m);
        intersNorm = area / distNorm;
      }
      if (!isNaN(intersNorm)) sumas[pool.pss[i]] += intersNorm;
      r.vecinas.push({
        rank: rank + 1, cellname: pool.nombre[i], lat: pool.lat[i], lon: pool.lon[i],
        distancia_m: dist, distancia_norm_m: distNorm, phycellid: pool.pci[i],
        carrier: pool.carrier[i], sin_ta: pool.sinTa[i],
        pss: pool.pss[i], radio_ta_m: radioTa, azimuth: pool.azimuth[i],
        azimuth_efectivo: pool.azEf[i], txrxmode: pool.txrxmode[i], hbeam_deg: pool.hbeam[i],
        area_interseccion_m2: area, interseccion_normalizada: intersNorm,
        co_sitio: esCosito, wedge: wedgeVec
      });
    }

    // Paso 8: sumas por PSS ordenadas de menor a mayor (desempate por PSS).
    r.sumas_pss = { 0: round3(sumas[0]), 1: round3(sumas[1]), 2: round3(sumas[2]) };
    r.orden_pss = [0, 1, 2].sort(function (s1, s2) { return (sumas[s1] - sumas[s2]) || (s1 - s2); });
    r.auditoria.push('Paso 8: suma de intersección normalizada por PSS → ' +
      r.orden_pss.map(function (s) { return 'PSS ' + s + ': ' + sumas[s].toFixed(1); }).join(', '));
    return { r: r, pool: pool, q: q };
  };

  /** Pasos 2 a 12 para una sola celda a planificar. */
  Planificador.prototype.planificarCelda = function (fila) {
    var ev = this._evaluar(fila);
    var r = ev.r;

    // ESPEJO CO-SECTOR (capas C1/C2… del MISMO carrier). Si en el sitio ya hay una
    // celda del mismo carrier, sector (nº) y azimuth efectivo, esta es su capa
    // espejo: copia su PCI/RSI (no se le asigna uno propio). Celdas del mismo sector
    // físico pero de carrier distinto NO se espejan (planos de PCI independientes).
    var twin = this._espejoSibling(r);
    if (twin) {
      r.error = null;
      r.pss_elegido = twin.pss; r.pss_ganador = (r.pss_ganador < 0 ? twin.pss : r.pss_ganador);
      r.pci = twin.pci; r.rsi = TABLA_RSI[twin.pci];
      r.dist_reuso_pci_m = 0; r.mirror_of = twin.cellname;
      r.auditoria.push('Paso 11/12: espejo co-sector de ' + twin.cellname +
        ' (mismo sitio/sector/azimuth) → PCI/RSI ' + twin.pci + '/' + r.rsi);
      this._registrarSitio(this._claveSitio(r.lat, r.lon), r);
      return r;
    }
    if (r.error) return r;
    var p = this.params, q = ev.q, pool = ev.pool;

    // Paso 9: PSS ganador (+ regla de adyacencia/co-sitio del paso 11 si aplica).
    r.pss_ganador = r.orden_pss[0];
    r.pss_elegido = this._elegirPssSitio(r);

    // Paso 10: vecindad ampliada (ya calculada en la pasada única).
    r.vecindad_n = q.idxVec.length;
    r.vecindad_dentro_70km = q.dentro;
    r.auditoria.push('Paso 10: vecindad de ' + r.vecindad_n + ' celdas (' + q.dentro +
      ' dentro de ' + (p.radio_vecindad_m / 1000).toFixed(0) + ' km)');

    // Paso 11: elección del PCI (PSS fijo + SSS de grupo del sitio).
    this._elegirPci(r, pool, q);

    // Paso 12: RSI por lookup + validación de reuso cercano.
    if (r.pci >= 0) {
      r.rsi = TABLA_RSI[r.pci];
      r.auditoria.push('Paso 12: RSI = tabla[' + r.pci + '] = ' + r.rsi);
      this._validarRsi(r, pool, q.idx30, q.dist30);
    }
    return r;
  };

  /** Grupo de PSS/PCI de un carrier: los carriers enlazados (C2+C3) comparten
   *  grupo; el resto es independiente. */
  Planificador.prototype._grupoCarrier = function (carrier) {
    var c = String(carrier).trim().toUpperCase();
    var grupos = this.params.carrier_links || [];
    for (var i = 0; i < grupos.length; i++) {
      var g = grupos[i].map(function (s) { return String(s).toUpperCase(); });
      if (g.indexOf(c) >= 0) return g.join('+');
    }
    return c;
  };

  /** PSS prohibidos por adyacencia en el sitio (mismo grupo de carrier): celdas
   *  co-sector (mismo azimuth, mismo carrier) y la(s) del azimuth más cercano.
   *  Detecta además la gemela espejo (mismo azimuth, otro carrier del grupo
   *  enlazado), cuyo PSS debe IGUALARSE en vez de evitarse. */
  Planificador.prototype._pssProhibidos = function (r) {
    var lista = (this.asignadosSitio[this._claveSitio(r.lat, r.lon)] || [])
      .filter(function (x) { return x.grupo === r.grupo; });
    var azRef = r.azimuth_efectivo; // azimuth efectivo (sub-beams MM con su offset)
    var secRef = numeroSector(r.cellname); // número de sector (8.º carácter del nombre)
    var twinPss = null;
    // Se agrupa por azimuth efectivo (un sector): las celdas del grupo a la
    // misma azimuth comparten PSS (espejo C2/C3), así un sector = una entrada.
    var porAz = {};
    // También por NÚMERO DE SECTOR distinto: distancia mínima |sec - secRef| y el
    // conjunto de PSS asignados a ese número de sector.
    var porSec = {};
    var prohibidos = {};
    var motivos = {}; // pss → 'azimuth' | 'sector' | 'mm14' (combinables con '+'; auditoría)
    function marcar(pss, motivo) {
      prohibidos[pss] = true;
      motivos[pss] = motivos[pss] && motivos[pss] !== motivo ? motivos[pss] + '+' + motivo : motivo;
    }
    // Distancia de NÚMERO DE SECTOR CIRCULAR (anillo): el sector más alto del
    // sitio es adyacente al más bajo. Módulo = (máx − mín + 1) sobre los números
    // de sector presentes en el sitio (incluida la celda a planificar). Así, con
    // sectores 1..4, dist(1,4) = dist(1,2) = 1. dist(a,b)=min(|a−b|, M−|a−b|).
    var minSec = secRef, maxSec = secRef;
    if (secRef !== null) {
      for (var s0 = 0; s0 < lista.length; s0++) {
        var sc = lista[s0].sector;
        if (sc === null || sc === undefined) continue;
        if (sc < minSec) minSec = sc;
        if (sc > maxSec) maxSec = sc;
      }
    }
    var modSec = (secRef === null) ? 0 : (maxSec - minSec + 1);
    function distSector(a, b) {
      var raw = Math.abs(a - b);
      return modSec > 1 ? Math.min(raw, modSec - raw) : raw;
    }
    for (var i = 0; i < lista.length; i++) {
      var x = lista[i], d = angDist(x.az, azRef);
      if (d < 1e-6 && x.carrier !== r.carrier) { twinPss = x.pss; continue; } // gemela espejo → igualar
      if (!(x.az in porAz) || d < porAz[x.az].dist) porAz[x.az] = { pss: x.pss, dist: d };
      if (secRef !== null && x.sector !== null && x.sector !== undefined) {
        var ds = distSector(x.sector, secRef);
        if (ds === 0) {
          // Mismo número de sector (distancia 0): EXCLUIDA del criterio general.
          // Excepción Massive MIMO: entre los sub-beams del mismo sector, los
          // sub-beams 1 y 4 SÍ prohíben su PSS; los sub-beams 2 y 3 (y las
          // celdas no-MM, beam === null) quedan excluidos.
          if (x.beam === 1 || x.beam === 4) marcar(x.pss, 'mm14');
        } else {
          if (!(x.sector in porSec)) porSec[x.sector] = { dist: ds, pss: {} };
          porSec[x.sector].pss[x.pss] = true;
        }
      }
    }
    // (a) PSS de los 2 sectores más cercanos por AZIMUTH efectivo (con empates
    // a la 2.ª menor distancia angular).
    var entries = [];
    for (var az in porAz) entries.push(porAz[az]);
    entries.sort(function (a, b) { return a.dist - b.dist; });
    if (entries.length) {
      var d2 = entries[Math.min(1, entries.length - 1)].dist;
      for (var j = 0; j < entries.length; j++) if (entries[j].dist <= d2 + 1e-9) marcar(entries[j].pss, 'azimuth');
    }
    // (b) PSS de los 2 sectores más cercanos por NÚMERO DE SECTOR (8.º carácter
    // del Cellname), con empates a la 2.ª menor distancia de número de sector.
    var secs = [];
    for (var sk in porSec) secs.push(porSec[sk]);
    secs.sort(function (a, b) { return a.dist - b.dist; });
    if (secs.length) {
      var s2 = secs[Math.min(1, secs.length - 1)].dist;
      for (var z = 0; z < secs.length; z++) {
        if (secs[z].dist <= s2 + 1e-9) for (var pk in secs[z].pss) marcar(parseInt(pk, 10), 'sector');
      }
    }
    return { prohibidos: prohibidos, twinPss: twinPss, motivos: motivos, secRef: secRef };
  };

  /** Contexto de SECTORES del sitio para la coordinación de PSS. Para cada sector
   *  físico ya con celdas asignadas se toma su PSS REPRESENTATIVO = el de su
   *  primera celda planificada. Permite que un sector nuevo elija un PSS distinto
   *  del de los demás sectores (orden-independiente), confinando los repasos a
   *  los sub-beams MM. Devuelve { esNuevoSector, otrosRep:{pss:true}, hayLibre }
   *  o null si no hay precálculo de sitios (llamada suelta a planificarCelda). */
  Planificador.prototype._sectorContexto = function (r) {
    var SG = this._claveSitio(r.lat, r.lon) + '|' + r.grupo;
    if (!this._sectoresPlanSG[SG]) return null;
    var lista = (this.asignadosSitio[this._claveSitio(r.lat, r.lon)] || [])
      .filter(function (x) { return x.grupo === r.grupo; });
    var K = this._sectorIdDe(numeroSector(r.cellname), r.azimuth_efectivo);
    var repPss = {}, tieneK = false; // sector → PSS de su 1.ª celda (representativo)
    for (var i = 0; i < lista.length; i++) {
      var x = lista[i], sid = this._sectorIdDe(x.sector, x.az);
      if (sid === K) tieneK = true;
      if (repPss[sid] === undefined) repPss[sid] = x.pss;
    }
    var otrosRep = {}, nOtros = 0;
    for (var sid in repPss) if (sid !== K) { if (!otrosRep[repPss[sid]]) nOtros++; otrosRep[repPss[sid]] = true; }
    return { esNuevoSector: !tieneK, otrosRep: otrosRep, hayLibre: nOtros < 3 };
  };

  Planificador.prototype._elegirPssSitio = function (r) {
    if (!this.params.evitar_pss_repetido_en_sitio) return r.pss_ganador;
    // COLOREO CONJUNTO POR SITIO: si la celda pertenece a un sitio precalculado
    // (corrida normal vía ordenarPorCosto), su PSS lo fija el coloreo del sitio.
    var SG = this._claveSitio(r.lat, r.lon) + '|' + r.grupo;
    if (this._planPorSG[SG]) {
      var mapa = this._colorearSitio(SG);
      if (mapa[r.cellname] !== undefined) {
        var pssC = mapa[r.cellname];
        r.auditoria.push('Paso 9/11: coloreo conjunto del sitio (' + this._planPorSG[SG].length +
          ' celda(s)) → PSS ' + pssC + (pssC !== r.pss_ganador ? ' (ganador por interferencia era ' + r.pss_ganador + '; se ajusta para separar mismos PSS de celdas cercanas del sitio)' : ''));
        return pssC;
      }
    }
    var pr = this._pssProhibidos(r);
    // Gemela espejo co-sector (C2↔C3): se iguala el PSS, no se evita.
    if (pr.twinPss !== null) {
      r.auditoria.push('Paso 9/11: gemela co-sector del grupo ' + r.grupo +
        ' ya asignada; se iguala el PSS ' + pr.twinPss + ' (PCI/RSI espejo)');
      return pr.twinPss;
    }
    var prohib = pr.prohibidos, orden = r.orden_pss;
    var porQue = function (pss) {
      var m = pr.motivos[pss]; if (!m) return 'por adyacencia';
      var map = { azimuth: 'por azimuth efectivo', sector: 'por número de sector', mm14: 'por sub-beam MM 1/4 del mismo sector' };
      return m.split('+').map(function (k) { return map[k] || k; }).join(' y ');
    };
    // COORDINACIÓN DE SITIO: un sector NUEVO debe tomar un PSS distinto del de los
    // demás sectores del sitio (con ≤ 3 sectores siempre es posible). Esto evita
    // que dos sectores distintos compartan PSS y prevalece sobre la adyacencia.
    var ctx = this._sectorContexto(r);
    if (ctx && ctx.esNuevoSector && ctx.hayLibre) {
      var k, pss;
      for (k = 0; k < orden.length; k++) { pss = orden[k]; // 1) distinto de otros sectores y sin prohibición
        if (!ctx.otrosRep[pss] && !prohib[pss]) {
          if (pss !== r.pss_ganador) r.auditoria.push('Paso 9/11: PSS ganador ' + r.pss_ganador + ' prohibido (' + porQue(r.pss_ganador) + '); sector nuevo toma PSS ' + pss + ' (distinto de los otros sectores del sitio)');
          return pss;
        }
      }
      for (k = 0; k < orden.length; k++) { pss = orden[k]; // 2) distinto de otros sectores aunque la adyacencia lo prohíba (la distinción manda)
        if (!ctx.otrosRep[pss]) {
          r.auditoria.push('Paso 9/11: para mantener el sector distinto de los demás del sitio se elige PSS ' + pss + ' (prevalece sobre la prohibición de adyacencia)');
          return pss;
        }
      }
    }
    // Sub-beam extra de un sector ya representado (o sitio con > 3 sectores):
    // comportamiento previo (menor suma sin prohibición; si todas, repite ganador).
    if (!prohib[r.pss_ganador]) return r.pss_ganador;
    for (var i = 0; i < orden.length; i++) {
      if (!prohib[orden[i]]) {
        r.auditoria.push('Paso 9/11: PSS ganador ' + r.pss_ganador +
          ' usado por uno de los 2 sectores más cercanos del grupo ' + r.grupo +
          ' (' + porQue(r.pss_ganador) + (pr.secRef !== null ? '; nº sector de la celda = ' + pr.secRef : '') +
          '); se toma el siguiente con menor suma: PSS ' + orden[i]);
        return orden[i];
      }
    }
    r.advertencias.push('Todos los PSS están usados por celdas adyacentes del grupo ' + r.grupo + ': se repite el PSS ganador');
    return r.pss_ganador;
  };

  Planificador.prototype._elegirPci = function (r, pool, q) {
    var p = this.params;
    var rango = r.indoor ? p.pci_indoor : p.pci_outdoor;
    var lo = rango[0], hi = rango[1];
    var claveSitio = this._claveSitio(r.lat, r.lon);
    // PCI co-sitio: solo del MISMO grupo de carrier (PCI independiente por
    // carrier, salvo C2+C3). Distintos sectores del grupo no repiten PCI.
    var listaSitio = (this.asignadosSitio[claveSitio] || []).filter(function (x) { return x.grupo === r.grupo; });
    var pciSitio = {}; listaSitio.forEach(function (x) { pciSitio[x.pci] = true; });
    var usadosVecindad = {};
    for (var v = 0; v < q.idxVec.length; v++) usadosVecindad[pool.pci[q.idxVec[v]]] = true;
    var usados = {};
    for (var k in usadosVecindad) usados[k] = true;
    for (var k2 in pciSitio) usados[k2] = true;

    var candidatos = [];
    for (var c = lo; c <= hi; c++) {
      if (c % 3 === r.pss_elegido && !usados[c]) candidatos.push(c);
    }
    r.n_candidatos = candidatos.length;

    var pcis30 = q.idx30.map(function (i) { return pool.pci[i]; });
    r.mediana_pci = pcis30.length ? mediana(pcis30) : NaN;

    if (candidatos.length === 0) { this._pciFallback(r, pool, q, lo, hi, usados); return; }

    // SSS (grupo de PCI = floor(PCI/3)): los sectores del mismo sitio+grupo
    // comparten un SSS común "kBase", elegido cercano a las celdas vecinas. Si
    // el PSS ya está usado en el sitio, el PCI 3·kBase+PSS estará ocupado y se
    // tomará el SSS libre más cercano (adyacente).
    var claveSG = claveSitio + '|' + r.grupo;
    var kBase = this._sssSitio[claveSG];
    if (kBase === undefined || isNaN(kBase)) kBase = Math.round((isNaN(r.mediana_pci) ? 0 : r.mediana_pci) / 3);
    // Criterio: si el sitio YA fijó su SSS, compartirlo manda (estructura del
    // sitio). Entre iguales, MAXIMIZAR la distancia de reuso: 1º la del mismo
    // PCI (global, todo el carrier), 2º la del mismo RSI (en NR el RSI se
    // repite con PCI±420), 3º kBase/mediana como desempates de estabilidad.
    var delta = p.delta_pci_vecino, minByPci = q.minByPci;
    var reuso = pool.reusoGlobal(r.lat, r.lon, r.carrier);
    function tieDe(c) {
      var best = Infinity, ini = Math.max(0, c - delta), fin = Math.min(1007, c + delta);
      for (var pp = ini; pp <= fin; pp++) if (pp !== c && minByPci[pp] < best) best = minByPci[pp];
      return best;
    }
    function reusoRsiDe(c) { var v = reuso.porRsi[TABLA_RSI[c]]; return v === undefined ? Infinity : v; }
    var sssFijo = this._sssSitio[claveSG] !== undefined;
    var scored = candidatos.map(function (c) {
      return { c: c, sss: Math.floor(c / 3), dSss: Math.abs(Math.floor(c / 3) - kBase),
               rPci: reuso.porPci[c], rRsi: reusoRsiDe(c),
               score: Math.abs(c - r.mediana_pci), tie: tieDe(c) };
    });
    scored.sort(function (a, b) {
      if (sssFijo && a.dSss !== b.dSss) return a.dSss - b.dSss;   // SSS compartido del sitio
      if (a.rPci !== b.rPci) return b.rPci - a.rPci;              // máx. reuso de PCI
      if (a.rRsi !== b.rRsi) return b.rRsi - a.rRsi;              // máx. reuso de RSI
      if (!sssFijo && a.dSss !== b.dSss) return a.dSss - b.dSss;  // estabilidad (SSS ~ kBase)
      return (a.score - b.score) || (a.tie === b.tie ? 0 : a.tie - b.tie) || (a.c - b.c);
    });
    var elegido = scored[0].c;
    if (this._sssSitio[claveSG] === undefined) this._sssSitio[claveSG] = Math.floor(elegido / 3); // 1er sector fija el SSS del sitio
    r.candidatos_top = scored.slice(0, 5).map(function (s) {
      return { pci: s.c, sss: s.sss, dif_vs_mediana: round1(s.score), dist_pci_vecino_m: isFinite(s.tie) ? round1(s.tie) : null,
               dist_reuso_pci_m: isFinite(s.rPci) ? round1(s.rPci) : null, dist_reuso_rsi_m: isFinite(s.rRsi) ? round1(s.rRsi) : null };
    });

    // Ejemplos de descartados por colisión (mismo PSS, en la vecindad).
    var ejemplos = [];
    var usadosOrden = Object.keys(usadosVecindad).map(Number).filter(function (x) { return x >= lo && x <= hi; }).sort(function (a, b) { return a - b; });
    for (var e = 0; e < usadosOrden.length && ejemplos.length < 5; e++) {
      var cc = usadosOrden[e];
      if (cc % 3 === r.pss_elegido) ejemplos.push('PCI ' + cc + ' usado a ' + fmtMiles(minByPci[cc]) + ' m');
    }
    r.descartados_ej = ejemplos;

    r.pci = elegido;
    // Distancias de reuso GLOBALES del elegido (∞ = ese PCI/RSI no se usa en el carrier).
    r.dist_reuso_pci_m = reuso.porPci[elegido];
    r.dist_reuso_rsi_m = reusoRsiDe(elegido);
    r.dist_reuso_mod30_m = reuso.porMod30[((elegido % 30) + 30) % 30];
    r.auditoria.push('Paso 11: rango ' + (r.indoor ? 'indoor' : 'outdoor') + ' ' + lo + '–' + hi + '; ' +
      candidatos.length + ' candidatos con PCI mod 3 == ' + r.pss_elegido + ' libres en la vecindad; ' +
      'criterio = máx. distancia de reuso de PCI y RSI' + (sssFijo ? ' (SSS ' + kBase + ' fijado por el sitio)' : '') +
      '; elegido PCI ' + elegido + ' = 3·' + Math.floor(elegido / 3) + '+' + r.pss_elegido +
      ' (reuso PCI ' + (isFinite(r.dist_reuso_pci_m) ? fmtMiles(r.dist_reuso_pci_m) + ' m' : '∞') +
      ' · reuso RSI ' + (isFinite(r.dist_reuso_rsi_m) ? fmtMiles(r.dist_reuso_rsi_m) + ' m' : '∞') + ')');
    if (ejemplos.length) r.auditoria.push('Paso 11: descartados por colisión → ' + ejemplos.join('; '));
    this._registrarSitio(claveSitio, r);
  };

  Planificador.prototype._pciFallback = function (r, pool, q, lo, hi, usados) {
    // Mínimos GLOBALES (todo el carrier): mismo criterio de máx. reuso que el
    // camino normal. Preferir un PCI NO usado en el sitio ni en la vecindad
    // (co-sitio nunca se repite); entre esos, el de mayor distancia de reuso.
    var reuso = pool.reusoGlobal(r.lat, r.lon, r.carrier), porPci = reuso.porPci;
    var mejor = -1, mejorD = -1, libre = -1, libreD = -1;
    for (var c = lo; c <= hi; c++) {
      if (c % 3 !== r.pss_elegido) continue;
      var d = porPci[c];
      if (d > mejorD) { mejorD = d; mejor = c; }
      if (!(usados && usados[c]) && d > libreD) { libreD = d; libre = c; }
    }
    if (libre >= 0) { mejor = libre; mejorD = libreD; }
    r.pci = mejor; r.dist_reuso_pci_m = mejorD;
    var rsF = reuso.porRsi[TABLA_RSI[mejor]];
    r.dist_reuso_rsi_m = rsF === undefined ? Infinity : rsF;
    r.dist_reuso_mod30_m = reuso.porMod30[((mejor % 30) + 30) % 30];
    var claveSG = this._claveSitio(r.lat, r.lon) + '|' + r.grupo;
    if (this._sssSitio[claveSG] === undefined && mejor >= 0) this._sssSitio[claveSG] = Math.floor(mejor / 3);
    r.advertencias.push('Sin PCI libre con PSS ' + r.pss_elegido + ' en la vecindad: se asigna PCI ' +
      mejor + ' maximizando la distancia de reuso (' + fmtMiles(mejorD) + ' m)');
    r.auditoria.push('Paso 11 (fallback): ' + r.advertencias[r.advertencias.length - 1]);
    this._registrarSitio(this._claveSitio(r.lat, r.lon), r);
  };

  Planificador.prototype._registrarSitio = function (clave, r) {
    if (!this.asignadosSitio[clave]) this.asignadosSitio[clave] = [];
    this.asignadosSitio[clave].push({ grupo: r.grupo, carrier: r.carrier, az: r.azimuth_efectivo, pss: r.pss_elegido, pci: r.pci, sector: numeroSector(r.cellname), beam: sufijoMM(r.cellname), cellname: r.cellname });
  };
  /** ESPEJO CO-SECTOR (capas C1/C2…) del MISMO carrier. Busca en el sitio una celda
   *  ya asignada del mismo carrier, número de sector y azimuth efectivo; si existe,
   *  esta celda es su espejo y copia su PCI/RSI. Celdas del mismo sector físico pero
   *  de carriers DISTINTOS NO se espejan (planos de PCI independientes por carrier). */
  Planificador.prototype._espejoSibling = function (r) {
    var lista = this.asignadosSitio[this._claveSitio(r.lat, r.lon)] || [];
    var sec = numeroSector(r.cellname);
    for (var i = 0; i < lista.length; i++) {
      var x = lista[i];
      if (x.carrier === r.carrier && x.pci >= 0 && x.sector === sec && angDist(x.az, r.azimuth_efectivo) < 1e-6) return x;
    }
    return null;
  };

  Planificador.prototype._validarRsi = function (r, pool, idx30, dist30) {
    for (var k = 0; k < idx30.length; k++) {
      var i = idx30[k];
      if (TABLA_RSI[pool.pci[i]] === r.rsi) {
        r.advertencias.push('RSI ' + r.rsi + ' reutilizado por ' + pool.nombre[i] +
          ' (PCI ' + pool.pci[i] + ') a ' + fmtMiles(dist30[k]) + ' m');
      }
    }
  };

  Planificador.prototype._incorporarPlanificada = function (fila, r) {
    var pool = this._pool(r.carrier_clave, fila);
    if (!pool) return;
    var nueva = {};
    // Copia los campos de la fila plan y fuerza los valores asignados.
    for (var key in fila) nueva[key] = fila[key];
    nueva.Cellname = r.cellname; nueva.Latitud = r.lat; nueva.Longitud = r.lon;
    nueva.Azimuth = r.azimuth; nueva.Phycellid = r.pci; nueva._radio_ta_m = r.radio_sector_m;
    pool.agregar(nueva);
  };

  // === Auditoría de la red existente (sin planificar) =====================
  // Escanea, por carrier y acotado por la rejilla espacial (nunca O(n²) sobre
  // toda la red), las colisiones de PCI, confusiones mod-3 (mismo PSS dentro de
  // `mod3` m) y colisiones de RSI entre celdas existentes del MISMO carrier
  // cercanas. Lógica pura (sin DOM): la usan el Web Worker y el fallback de
  // hilo principal de app.js, de modo que el algoritmo vive solo aquí.
  function pesoDist(d) { return d < 1 ? 99 : d < 500 ? 10 : d < 1000 ? 6 : d < 2000 ? 3 : 1; }
  function mkConfAudit(type, pool, i, j, d, val, carr, motivo) {
    return {
      type: type, aName: pool.nombre[i], aType: 'exist', bName: pool.nombre[j], bType: 'exist',
      dist: d, w: pesoDist(d), val: val, status: 'pending', carrier: carr, motivo: motivo || null,
      aPci: pool.pci[i], aPss: pool.pss[i], aRsi: TABLA_RSI[pool.pci[i]],
      bPci: pool.pci[j], bPss: pool.pss[j], bRsi: TABLA_RSI[pool.pci[j]]
    };
  }
  /** Audita la red existente. `byCarrier` = { CARRIER: [filas...] }. opts:
   *  { mod3 (m), scanR (m, radio de escaneo), cap (tope de conflictos),
   *    onProgress(ci, totalCarriers, nConflictos) }. Devuelve
   *  { conflicts, capped, cap }. */
  /** Módulo circular de sectores por sitio: clusteriza las celdas del pool por
   *  tolerancia (mismo emplazamiento) y devuelve, por índice, (máx−mín+1) de los
   *  números de sector del sitio (para la adyacencia circular de la confusión). */
  function _modulosSitio(pool, tol) {
    var n = pool.n, mod = new Float64Array(n), seen = new Uint8Array(n);
    var cell = pool._cell, grid = pool._grid, la = pool.lat, lo = pool.lon;
    var rings = Math.max(1, Math.ceil(tol / (cell * 110000)) + 1);
    for (var i = 0; i < n; i++) {
      if (seen[i]) continue;
      var gx = Math.floor(la[i] / cell), gy = Math.floor(lo[i] / cell);
      var cluster = [], mn = Infinity, mx = -Infinity;
      for (var dx = -rings; dx <= rings; dx++) for (var dy = -rings; dy <= rings; dy++) {
        var bk = grid[(gx + dx) + ',' + (gy + dy)]; if (!bk) continue;
        for (var m = 0; m < bk.length; m++) {
          var k = bk[m];
          if (haversine_m(la[i], lo[i], la[k], lo[k]) <= tol) {
            cluster.push(k);
            var sc = numeroSector(pool.nombre[k]);
            if (sc != null) { if (sc < mn) mn = sc; if (sc > mx) mx = sc; }
          }
        }
      }
      var mo = (mn === Infinity) ? 1 : (mx - mn + 1);
      for (var c = 0; c < cluster.length; c++) { mod[cluster[c]] = mo; seen[cluster[c]] = 1; }
    }
    return mod;
  }
  function _descrPool(pool, i) {
    return { lat: pool.lat[i], lon: pool.lon[i], azEf: pool.azEf[i], hbeam: pool.hbeam[i],
      sector: numeroSector(pool.nombre[i]), beam: sufijoMM(pool.nombre[i]), radioTa: pool.radioEfectivo(i) };
  }
  /** ¿Las celdas i, j del pool (mismo carrier) son ESPEJO co-sector? Mismo sitio
   *  (dist ≤ tol), mismo número de sector y mismo azimuth efectivo (capas C1/C2). */
  function _esEspejoCoSector(pool, i, j, tol) {
    if (haversine_m(pool.lat[i], pool.lon[i], pool.lat[j], pool.lon[j]) > tol) return false;
    if (numeroSector(pool.nombre[i]) !== numeroSector(pool.nombre[j])) return false;
    return angDist(pool.azEf[i], pool.azEf[j]) < 1e-6;
  }
  function auditarRed(byCarrier, opts) {
    opts = opts || {};
    var CAP = opts.cap || 8000, tol = DEFAULTS.radio_sitio_m;
    // Ventana de búsqueda = las `maxVec` vecinas más cercanas del mismo carrier
    // (sin límite de distancia). Un par se MARCA como colisión (PCI/RSI) solo si
    // está a ≤ `radioReuso` (el "Radio de reuso" del slider): más lejos es reuso
    // legítimo, no conflicto. La confusión mod-3 se evalúa SOLO entre las 30 más
    // cercanas (misma vecindad que la planificación) con su criterio geométrico.
    // El espejo co-sector C1/C2 queda exento. El coloreo de PSS va aparte.
    var maxVec = opts.maxVec || 600, radioReuso = opts.radioReuso || 10000;
    var K30 = 30, onProgress = opts.onProgress;
    var carriers = Object.keys(byCarrier), out = [];
    for (var ci = 0; ci < carriers.length && out.length < CAP; ci++) {
      var carr = carriers[ci];
      var pool = new PoolCarrier(byCarrier[carr], DEFAULTS);
      var n = pool.n, modSit = _modulosSitio(pool, tol), seen = {};
      for (var i = 0; i < n && out.length < CAP; i++) {
        var neigh = pool.nCercanasIdx(i, maxVec); // ordenadas por distancia
        for (var t = 0; t < neigh.length && out.length < CAP; t++) {
          var j = neigh[t], a = i < j ? i : j, b = i < j ? j : i; // par único (a<b)
          var d = haversine_m(pool.lat[a], pool.lon[a], pool.lat[b], pool.lon[b]);
          // Vecinas ordenadas: pasadas las 30 (mod-3) y el radio de reuso
          // (colisiones, margen 10 % por el ranking planar), no queda nada que marcar.
          if (t >= K30 && d > radioReuso * 1.1) break;
          // Espejo co-sector (capas C1/C2 del mismo sector físico): comparten
          // PCI/RSI por diseño → NO es colisión.
          var espejo = _esEspejoCoSector(pool, a, b, tol);
          if (d <= radioReuso && pool.pci[a] === pool.pci[b]) {
            if (!espejo) { var kC = 'C|' + a + '|' + b; if (!seen[kC]) { seen[kC] = 1; out.push(mkConfAudit('PCI_COLLISION', pool, a, b, d, pool.pci[a], carr)); } }
          } else if (t < K30 && pool.pss[a] === pool.pss[b] && pool.pci[a] !== pool.pci[b]) {
            var motA = confusionMod3(_descrPool(pool, a), _descrPool(pool, b), d <= tol, modSit[a]);
            if (motA) { var kM = 'M|' + a + '|' + b; if (!seen[kM]) { seen[kM] = 1; out.push(mkConfAudit('PCI_MOD3', pool, a, b, d, pool.pss[a], carr, motA)); } }
          }
          if (d <= radioReuso && !espejo && TABLA_RSI[pool.pci[a]] === TABLA_RSI[pool.pci[b]]) {
            var kR = 'R|' + a + '|' + b; if (!seen[kR]) { seen[kR] = 1; out.push(mkConfAudit('RSI_COLLISION', pool, a, b, d, TABLA_RSI[pool.pci[a]], carr)); }
          }
        }
      }
      if (onProgress) onProgress(ci + 1, carriers.length, out.length);
    }
    return { conflicts: out, capped: out.length >= CAP, cap: CAP };
  }

  // === Helpers numéricos ==================================================
  function round1(x) { return Math.round(x * 10) / 10; }
  function round3(x) { return Math.round(x * 1000) / 1000; }
  function fmtMiles(x) {
    if (!isFinite(x)) return '∞';
    return Math.round(x).toLocaleString('en-US');
  }

  // === API pública ========================================================
  var API = {
    RADIO_TIERRA_M: RADIO_TIERRA_M,
    BINS_TA: BINS_TA,
    OFFSETS_MM: OFFSETS_MM,
    DEFAULTS: DEFAULTS,
    TABLA_RSI: TABLA_RSI,
    CARRIER_REGISTRY: CARRIER_REGISTRY,
    carrierSpectralFactor: carrierSpectralFactor,
    carrierSpectralFactorUL: carrierSpectralFactorUL,
    haversine_m: haversine_m,
    puntoDestino: puntoDestino,
    wedgeLatLon: wedgeLatLon,
    epsgUTM: epsgUTM,
    projUTM: projUTM,
    areaInterseccionM2: areaInterseccionM2,
    areaShoelace: areaShoelace,
    sufijoMM: sufijoMM,
    es32t32r: es32t32r,
    hbeamCelda: hbeamCelda,
    azimuthEfectivo: azimuthEfectivo,
    numeroSector: numeroSector,
    rumbo: rumbo,
    confusionMod3: confusionMod3,
    radioTaFila: radioTaFila,
    mediana: mediana,
    PoolCarrier: PoolCarrier,
    Planificador: Planificador,
    auditarRed: auditarRed
  };

  if (typeof module !== 'undefined' && module.exports) module.exports = API;
  else root.PCIPlanner = API;
})(typeof self !== 'undefined' ? self : this);
