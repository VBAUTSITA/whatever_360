# Decisiones de interfaz — Auditoría Red

Registro de trabajo del 2026-10-27 (America/Lima), destinado a integrarse al registro principal `DECISIONES-AUDITORIA-RED.md`. Sin credenciales. Copia autoritativa: repositorio aislado; la sincronización al host la realiza el responsable de integración tras su respaldo y verificación.

## UI-001 — Identidad vigente de Web Tracker (confirmada)

- Problema: el panel anterior tenía una identidad visual separada de la aplicación.
- Evidencia: lectura de `web-tracker/src/main/resources/templates/layouts/base.html`, `base_home.html` y `static/css/estilos.css`, además de los recursos actuales del módulo. El host usa encabezado Claro `#da272c`, marca ActiTracker en Roboto/Arial de 28 px, navegación negra, botones azules `#0073b7` y tablas Bootstrap con bordes.
- Alternativas: importar el CSS global completo o introducir un sistema visual distinto. Se descartan: el global cambia dimensiones de inputs/mapas y elimina contornos de foco; un sistema distinto pierde la identidad establecida.
- Decisión: trasladar cuidadosamente esas convenciones a reglas del módulo, con logo local copiado del host, navegación activa, tipografía compacta, controles de 30–32 px, tablas rayadas con encabezado fijo, selección amarilla y bordes discretos. Sin tarjetas decorativas, gradientes ni hero sobredimensionado. No se añade una dependencia externa de tipografías.
- Archivos: `templates/layouts/base_auditoriared.html`, `static/css/auditoriared-dashboard.css`, `static/images/auditoriared/claro-logo.png` y dashboard.
- Verificación: Chrome/Playwright con Spring/Nashorn sintético; colores computados rojo/negro, enlace activo, carga de logo y capturas escritorio 1500 px/móvil 390 px inspeccionadas. Sin desbordamiento horizontal de página; las tablas densas se desplazan dentro de su región.

## UI-002 — Flujo de analista y evidencia (confirmada)

- Problema: un mapa denso o filtros que alteren el cálculo pueden inducir interpretaciones erróneas.
- Decisión: secuencia área → hallazgos → objetivo/vecinas → relación seleccionada. El mapa dibuja solo el par y un contexto opcional de hasta 200 marcadores. Al filtrar o paginar se conserva el par seleccionado; al cambiar de área o recalcular se limpia. Elegir objetivo muestra su ubicación/evidencia incluso sin vecinas. Si cambia la generación del cálculo, la selección se actualiza con los datos recibidos o se limpia cuando sus celdas ya no están disponibles, evitando evidencia gráfica obsoleta. Las geometrías se encuadran completas y la leyenda distingue celda A azul/B roja.
- Los controles siguen generándose de metadatos del registro de filtros. No se añaden listas de celdas ni membresías ficticias de clúster. El radio es circular. Se muestra el orden PCI → PSS → RSI y después distancia sin convertirlo en severidad RF.
- Evidencia visible del par: PCI/PSS, carrier/banda, canal radio, estado y fuente TA, radio estimado, origen del radio y cantidad de vecinas de respaldo cuando existe, azimut/haz y pertenencia a área/margen. Se advierte falta de canal radio y RSI derivado sin PRACH verificado.
- Unidades: distancia en m/km, área en m² e índice área/distancia en m. No se imprime un valor cero cuando la métrica es nula. TA `VALIDO_FRACCION`/`VALIDO_PORCENTAJE`, inválido y ausente se distinguen en español.
- Antigüedad: fecha/hora de cálculo en America/Lima, indicador caché y edad informada por servidor en segundos. No se confunde con fecha de actualización de la fuente.
- Archivos: dashboard HTML/JS/CSS. Verificación sintética: 1056 hallazgos, paginación de 25, 30 vecinas conservadas aunque filtros oculten todos los hallazgos; selección de par por teclado, búsqueda con flechas/Enter y `aria-expanded`; advertencias de frecuencia/RSI; texto malicioso representado como texto; CSRF y sesión expirada; cero errores JavaScript.

## UI-003 — Vacío, incompleto y alcance desconocido (confirmada)

- Problema: una tabla sin filas o un sector inventado puede transmitir seguridad injustificada.
- Decisión: vacío explica que los filtros/reglas/contexto no certifican un PCI seguro; incompleto tiene aviso persistente y mensaje explícito incluso con cero filas. Las tablas vacías ocultan encabezados para que la advertencia pueda leerse en móvil sin desplazamiento horizontal. Las cifras calculadas y filtradas son independientes.
- Un radio nulo dibuja solamente la ubicación. Se elimina también el sector genérico de largo fijo en píxeles de la pantalla anterior `/auditoria-red`; los popups y textos ya no hablan de radio real, interferencia calculada, ni de carrier/banda como únicas celdas capaces de interferir. El respaldo se describe por las vecinas disponibles, hasta 30; TA en intervalo abierto no determina un radio finito.
- Archivos: `templates/auditoriared/index.html`, `static/js/auditoriared.js` y dashboard. No se modifica la asignación de Recursos.
- Verificación: sintaxis de ambos JavaScript con Node; regresión de pantalla anterior en móvil, resultado vacío e incompleto inyectado exclusivamente en el ensayo sintético de navegador. La inyección verifica presentación de estados y no se presenta como evidencia de reglas/Oracle.

## UI-004 — Ensayos locales de navegador (confirmada)

- `src/test/browser/dashboard.cjs`: 17 escenarios de presentación/regresión sobre `DashboardBrowserFixture` sintético, puerto 3298. Capturas y resultado en `target/browser`; ningún test visual depende de mosaicos OSM externos.
- `src/test/browser/dashboard-oracle.cjs`: 13 escenarios aprobados, flujo HTTP sin sustitución de respuestas, real repositorio/servicio/controlador/motores sobre USERTEST/FREEPDB1, puerto 3299; datos mínimos sintéticos etiquetados AUDRED. Escenarios: ambas tecnologías, paginación densa, TA ausente/inválido, duplicadas conflictivas, falta de canal, contexto geográfico fuera del área, filtrado y vecinas independientes, trazabilidad, selección por teclado, fuente de radio desconocida y móvil. Resultado y capturas en `target/browser/oracle`.
- Resultado final, radio 1 km, centro −12.09/−77.05: 1140 hallazgos, 52 celdas del área (11 4G y 41 5G), contexto calculado de 54 (dos de margen), 37 vecinas del grupo del objetivo. Lectura SQL: 16 filas 4G y 48 filas 5G; una fila inválida por tecnología, tres identidades contradictorias excluidas; cuatro celdas sin radio calculable (una aislada y una con TA en intervalo abierto por tecnología). Cero errores JavaScript. Las cifras sustituyen el ensayo preliminar de 48 celdas, anterior a añadir SPARSE/OVERFLOW.
- Los escenarios Oracle `AUDRED_5G_SPARSE` y `AUDRED_5G_OVERFLOW` entregaron `radioM=null`; fuentes `SIN_VECINAS` y `TA_DESBORDAMIENTO_DESCONOCIDO`. La evidencia visible indica «No disponible» y motivo; las capturas inspeccionadas muestran únicamente el punto, sin sectores inventados. TA válido (%) no se confunde con radio finito cuando su P80 cae en intervalo abierto.
- Capturas finales inspeccionadas: `dense-selected-evidence-desktop.png`, `selected-evidence-mobile.png`, `sparse-unknown-range.png`, `overflow-unknown-range.png`, además de vacío e inicial. Las cuatro primeras se recapturaron con helper temporal en `target/browser/oracle/capture-stable.cjs` tras asentarse animación Leaflet y mosaicos, porque la primera captura instantánea coincidió con una transición de zoom. No se alteraron datos ni respuestas para estas capturas. Basemap OSM visible y atribuido.
- Verificación adicional: el ensayo Oracle detectó URL de navegación reescrita con `;jsessionid`; se corrigió la comparación de ruta para que el enlace del panel conserve estado activo en sesiones con CSRF. El ensayo final lo comprueba. El buscador con múltiples resultados comprueba flecha arriba → último resultado, abajo → primero y Enter para elegir.
- El login/identidad del fixture no es el login corporativo; los mosaicos externos no son evidencia RF ni Oracle. El esquema y datos corporativos continúan pendientes de validación productiva.
