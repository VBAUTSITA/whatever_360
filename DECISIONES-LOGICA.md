# Decisiones de lógica para integrar en DECISIONES-AUDITORIA-RED.md

2026-10-27, America/Lima. Copia autoritativa: auditoria-red-publicacion. Este documento complementa el registro principal que mantiene el coordinador.

## L-001 — Definiciones y separación del motor de asignación (confirmada localmente)

**Problema/evidencia:** auditarRed heredado limitaba pares a nearest300 (4G)/600 (5G), aun cuando el radio incluía otras celdas. 5G suprimía automáticamente pares con misma coordenada/sector/azimut como espejo sin probar portadoras ni excepción configurada. Se confundía nombre técnico PCI_MOD3 con confusión PCI real.

**Decisión:** conservar ambos planners originales sin cambios y ejecutar una capa `auditoria_modelo.js` propia de Auditoría Red. La huella de versión concatena hashes SHA256 de planner, capa y geometría. No altera asignación de Recursos. PCI_COLLISION significa **Colisión PCI potencial**: PCI idéntico y distancia haversine <= radio de reuso para pares compatibles. No excluye cosites ni espejos sólo por nombre. RSI_COLLISION significa **Coincidencia RSI derivada**: igualdad definida en TABLA_RSI para PCIs del par, dentro del radio; no se consulta RSI/PRACH real.

PCI_MOD3 significa **Riesgo PSS (mod-3)**: PCI distintos, PCI%3 iguales, par en unión de las 30 vecinas esféricas de al menos uno y visibilidad direccional mutua según radio/azimut/haz estimados. Cada centro queda dentro del radio y cono del otro; cosites <=1m se excluyen de este cálculo geométrico. No se conserva la antigua inferencia de adyacencia por números de sector/nombres, que no demuestra cobertura física. No se implementa confusión servidora/dos vecinas: falta fuente validada de relaciones y frecuencia. Alternativa descartada: presentar el algoritmo histórico como físicamente validado o conservar excepciones nominales sin evidencia.

**Fórmulas/unidades:** d=2*6371000*asin(sqrt(clamp(a,0,1))) en m; a=sin²(deltaLat/2)+cos(lat1)cos(lat2)sin²(deltaLon/2), radianes. Visibilidad usa diferencia angular circular <=hbeam/2 y d<=radioA y d<=radioB. radioReuso configurado 10000m; solicitud admite [500,configurado]. No son umbrales universales RF. Haz/offset MM/TXRX heredados son supuestos del planner; 5G convencional por defecto100°, no se lee patrón real ni beamforming.

**Archivos:** AdaptadorMotorAuditoria, auditoria_modelo.js, geometria_sectores.js, ChequeoColisionPci/Rsi, HallazgoAuditoriaDTO. Pruebas ModeloAuditoriaPrecisionTest prueban pareja ocultable detrás de605vecinas, cosites5G, PSS direccional, determinismo, canal y límites.

## L-002 — Frecuencia y alcance de candidatura (provisional respecto de producción)

SQL actual no entrega EARFCN/NRARFCN verificable. `canalRadio=null`, `fuenteFrecuencia=CARRIER_BANDA_SIN_CANAL` y hallazgos `FRECUENCIA_NO_VERIFICADA`; el motivo avisa falta de prueba co-canal. Sin ambos canales explícitos se consideran sólo iguales carrier/banda como grupo **candidato**, sin afirmar frecuencia igual ni ausencia de riesgos entre otros grupos. No se usa CARRIER_REGISTRY hardcodeado del planner5G como configuración de red actual.

DTO permite canal explícito: si ambos lo tienen, igualdad compara aunque las etiquetas carrier/banda difieran (p.ej. bandas NR solapadas); canales distintos excluyen el par. Tecnología separa EARFCN/NRARFCN. El soporte es probado con DTO; Oracle sigue sin fuente de canal y requiere validación productiva del campo/dirección/bandwidth antes de extender SQL. No analiza interferencia adyacente/solapamiento espectral de canales distintos. Vecinas y respaldo aplican la misma compatibilidad.

## L-003 — TA y respaldo de densidad (confirmada localmente, modelo provisional)

Entrada acepta ocho bins completos, fracción suma[.99,1.01] o porcentaje suma[99,101], sin negativos/no finitos; normaliza exactamente a uno. Nulos parciales/malformados/negativos se etiquetan INVALIDO; vector cero AUSENTE. Son datos faltantes, no cobertura cero. El repositorio mantiene fuente/estado TA.

Radio TA = primer límite superior cuyo acumulado alcanza >=80% (epsilon numérico1e-12). Límites4G [234,546,1014,1950,3510,6630,14430]m; 5G [117,507,975,1775,3315,7215,15015]m, conforme a columnas locales de entrada. Octavo bin abierto no tiene límite finito: si P80 cae allí radio=null y fuenteRadio=TA_DESBORDAMIENTO_DESCONOCIDO; no se inventan14400/15000m. TA puede ser válido y radio desconocido.

Sin TA válido: promedio de distancia a hasta30celdas compatibles más cercanas, excluyendo self y distancias<=1m; fuente RESPALDO_VECINAS y vecinasRespaldo informa cantidad. Sin ninguna, radio=null y fuente SIN_VECINAS. No se ejecuta el fallback heredado de201anillos ni se devuelve el radio defecto de un singleton. Respaldo es escala de densidad del contexto, no cobertura medida; puede cambiar si cambia contexto. La tabla de siete límites finitos también sustituye la cascada antigua que colapsaba séptimo/abierto. Alternativa descartada: inventar un límite del bin abierto o usar fallback silencioso para TA válido desbordado.

## L-004 — Geografía, cosites, contexto y proyección (confirmada localmente)

Las distancias y nearest30 usan haversine exacto para el radio terrestre elegido, con desempate por nombre normalizado; se elimina ranking planar y anillos aproximados. PCI/RSI recorren todos los pares compatibles cargados (O(n²), n limitado por maxCeldas8000); cap independiente por tipo en4G/5G. `capped` sólo cuando existe al menos un hallazgo adicional omitido, no cuando el total es exactamente cap. Lista con cap es subconjunto determinista, no garantiza conservar globalmente los pares más próximos; UI ordena sólo lo disponible. Reducir área ante cap.

El margen=reusoKm+0.1km garantiza parejas PCI/RSI de centros en área dentro de ese radio, salvo datos ausentes/excluidos. No garantiza que todo sector externo que interseque esté cargado, ni que nearest30 global/fallback sea completo. Dashboard lo declara. Filtros/páginas/visibilidad no recortan contexto de cálculo.

Área de intersección en m²; índice=área/d en m cuando d>1m y geometría finita. Cosites<=1m entregan ambosnull; no se divide por max(d,1) ni se inventa un índice gigante. Geometría UTM local se omite para centros fuera de |lat|<=80° o pares con |deltaLon|>6° (incluye antimeridiano); distancia/PCI siguen disponibles. Es modelo geométrico sin propagación ni RF. No porcentajes de riesgo.

Centro de sitio planificado calculado por media de vectores unitarios esféricos (atan2), evitando media aritmética de179.999/-179.999. Se rechazan coordenadas/tecnologías inválidas y centro antipodal indefinido. Prueba muestra sitio pequeño en antimeridiano.

## L-005 — Identidad, duplicados y fuentes (confirmada localmente)

Identidad oficial dentro del contexto leído = tecnología+trim(nombre).toUpperCase(ROOT). Filas equivalentes consolidan; representación se desempata por firma estable de nombre/carrier/banda/TXRX/estado-fuenteTA/fuenteFrecuencia/vectorTA, nunca por orden Oracle. Histogramas fracción/porcentaje físicamente equivalentes tras normalizar también consolidan. Si la identidad tiene coordenadas/PCI/azimut/carrier/banda/canal/TXRX/histograma contradictorios, se excluye la identidad completa: no hay timestamp/precedencia fiable que permita elegir ganador. `duplicadas` cuenta filas extra e `duplicadasConflictivas` identidades excluidas. Pruebas permutan filas y comparan payload elegido, además conflictos PCI y TA.

Alcance de la evidencia: sólo filas devueltas por consulta/contexto; una configuración contradictoria fuera del área consultada puede no ser conocida. Filas con coordenadas SQL no convertibles se excluyen antes del contador leído y no se pueden ubicar/contar por área; UI lo declara. Guardar reservas Recursos no cambia universo automáticamente. Modo sitio usa sólo filas explícitas autorizadas y PCI propuesto como escenario, reemplazando identidad oficial para evaluar la propuesta; no pretende representar una reserva oficial ni cambia flujo de guardado.

## L-006 — Caché, orden y calidad de evidencia (confirmada localmente)

Se mantienen caché20entradas/TTL15min configurable, snapshots independientes y exclusión concurrente fail-fast (409), motores Nashorn sincronizados. Modo sitio no usa caché. Respuesta añade generadoEnEpochMs, edadSegundos y zonaHoraria America/Lima; edad desde cálculo/extracción, no edad del reporte ni del TA, cuyos timestamps no están en SQL. Refrescar recalcula. Guardado evita sustituir resultado por uno de fecha de cálculo anterior.

Orden de revisión: PCI potencial→PSS→RSI derivada, luego distancia/tipo/nombres; es clasificación de trabajo, sin severidad física inventada. Filtro calidad trata radio=null como geometría no disponible aunque TA válido desbordado. Resumen añade sinFrecuencia, TA ausente/inválido, radios mostrados desconocidos y overflow mostrado. Avisos conservan límites ante filtros vacíos/cap. Documentar toda exclusión; una tabla vacía no certifica seguridad.

## Verificación registrada

- Primera ejecución seleccionada con Java8/Nashorn:61pruebas,0fallos,0errores,0omitidas. Precision11=6.863s (incluye607celdas en prueba de frontera nearest600).
- Se agregaron después5pruebas de servicio +1determinismo de motor (totales módulo101esperados). Primera ejecución global afectada por clases temporales compiladas por IDE en target y un import estático faltante en prueba nueva; import corregido. Se solicita compilación Maven limpia/separada y resultados finales al coordinador; no presentar aquella ejecución como aprobada.
- Coordinador verificó primer Oracle fixture:60assertions,1140hallazgos, servicios/repositorio/engine reales; pendiente ejecución final actualizada con canales explícitos sólo DTO y nuevos casos singleton/overflow Oracle. Registro principal recogerá conteos finales y build aislado del IDE.
