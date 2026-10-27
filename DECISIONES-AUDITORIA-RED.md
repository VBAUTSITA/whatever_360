# Decisiones y evidencia — Auditoría Red

Fecha de trabajo: 2026-10-27 (America/Lima). Documento vivo. La copia autoritativa es `auditoria-red-publicacion`, rama `feature/auditoria-red-dashboard`; los archivos del módulo se sincronizan al host sólo después de verificarlos y respaldarlos.

## Evaluación inicial

Confirmado: HEAD local y remoto `d2f10fa51ac3e78b6c15d067001628bc29b09a99`; checkout del módulo inicialmente limpio. El host continúa en `feature/mapa-osm` con trabajo pendiente ajeno, registrado en `target/verification/host-status-before.txt`. Java 8, Spring Boot 2.1.7 y Nashorn son restricciones de integración.

Oracle: servicios `OracleServiceFREE` y `OracleOraDB23Home1TNSListener` activos; conexión TCP a localhost:1521 comprobada. Identidad USERTEST, PDB, objetos, consultas y datos aún pendientes de autenticación y verificación. No se infiere un esquema productivo de nombres usados en Java.

Brechas de la línea base: agrupación carrier/banda sin canal RF comprobado; deduplicación que conserva la primera fila; TA parcial convertido en ceros y normalización con tolerancias amplias; respaldo geométrico heredado; afirmaciones antiguas de equivalencia productiva no demostradas. El fixture de navegador anterior sustituye el repositorio y no constituye integración Oracle.

## D-001 — Alcance, integridad y fuentes (confirmada)

- Problema: documentación histórica mezcla motores copiados, validación productiva y datos actuales.
- Evidencia: handoff completo, AGENTS.md, README/VERIFICACION/EXTENDER-FILTROS, encabezado técnico de MODULO_AUDITORIARED y AUDITORIA_RED_BASELINE_MOTORES; contraste con clases actuales. La línea base reconoce seis fallos PSS 4G históricos y diferencias de selección entre motores.
- Decisión: modificar exclusivamente Auditoría Red; conservar huellas de motores y distinguir comportamiento local demostrado, modelo geométrico y validación corporativa pendiente. No certificar ausencia de interferencia o confusión a partir de una lista vacía.
- Alternativas: confiar en afirmaciones antiguas de producción o ejecutar módulos completos; descartadas por falta de evidencia y riesgo de alterar trabajo ajeno.
- Archivos: paquete auditoriared, sus recursos/tests y documentación; ningún datasource corporativo se modifica.
- Verificación: coincidencia local/remota; estado host capturado; conexión TCP Oracle comprobada. Pendiente: ejecución funcional y sincronización con manifiesto.

## D-002 — Plan de evidencia y límites (provisional)

- Revisar PCI potencial, confusión servidora/dos vecinas, PSS mod-3, frecuencia, RSI derivado, TA, identidad, margen, filtros y caché por separado.
- Área de solapamiento: m². Índice área/distancia: m. Sin porcentaje de riesgo ni umbral físico inventado.
- Crear un fixture mínimo etiquetado en USERTEST sólo tras confirmar usuario, FREEPDB1, catálogo y privilegios; no modificar objetos originales existentes. Datos sintéticos no son red corporativa.
- Mantener reservas Recursos fuera del universo automático salvo que se implemente y verifique explícitamente una fuente autorizada con precedencia oficial; la selección de sitio sigue usando IDs autorizados.
- Interfaz: reutilizar estilos actuales Web Tracker; tabla y evidencia como centro del flujo y mapa del par seleccionado. Comprobar estados densos, vacíos, incompletos y móviles.
- Verificación pendiente: pruebas independientes de reglas, consultas Oracle reales, navegador real, compilación del host y comparación de archivos ajenos.

## D-003 — Definiciones físicas y evidencia requerida (confirmada; implementación en curso)

- Problema: mezclar igualdad de PCI, PSS y confusión produce conclusiones físicas falsas.
- Fuentes primarias consultadas el 2026-10-27: [ETSI/3GPP TS 36.211 §6.11](https://www.etsi.org/deliver/etsi_ts/136200_136299/136211/11.05.00_60/ts_136211v110500p.pdf), [TS 38.211 §7.4.2](https://www.etsi.org/deliver/etsi_ts/138200_138299/138211/16.07.00_60/ts_138211v160700p.pdf), y [Qualcomm, LTE Small Cell SON Test Cases, PCI collision/confusion](https://www.qualcomm.com/media/documents/files/lte-small-cell-son-test-cases.pdf).
- Fórmula: PCI = 3·N_ID_1 + N_ID_2; N_ID_2 = PCI mod 3 identifica la secuencia PSS. Rangos PCI LTE 0–503 y NR 0–1007. Esto explica el término riesgo PSS; no establece por sí solo interferencia.
- Decisión: igualdad de PCI dentro de distancia configurable => candidato a colisión potencial, condicionada a frecuencia y cobertura. Confusión requiere una servidora con dos vecinas distintas de igual PCI y frecuencia; no hay tabla de relaciones ni mediciones para demostrarla con estas fuentes.
- Carrier+banda son grupos de planificación heredados, no un canal RF. No se inventarán EARFCN/NR-ARFCN a partir del nombre. Los datos actuales requieren advertencia de frecuencia no verificada. Un canal explícito y comparable puede restringir comparaciones en el modelo; su carga productiva queda pendiente de esquema real.
- Alternativa descartada: anunciar «colisión confirmada» o «confusión completa» por igualdad de banda/PCI/mod-3.
- RSI: [TS 36.331 PRACH-Config](https://www.etsi.org/deliver/etsi_TS/136300_136399/136331/10.12.00_60/ts_136331v101200p.pdf) configura rootSequenceIndex separadamente; [TS 38.331 RACH configuration](https://www.etsi.org/deliver/etsi_ts/138300_138399/138331/18.01.00_60/ts_138331v180100p.pdf) define prach-RootSequenceIndex. Inferencia: una tabla local derivada de PCI no demuestra valores configurados PRACH, formato, cyclic shift ni su compatibilidad. Se conservará como coincidencia de mapeo, con procedencia explícita.
- Archivos afectados: chequeos, DTO de hallazgo/celda, adaptador y panel. Verificación pendiente: regresiones y Oracle.

## D-004 — Credenciales y aislamiento de ejecución local (confirmada)

- Problema: ejecutar Oracle real sin persistir ni exponer credenciales ni cambiar datasources ajenos.
- Decisión: `scripts/Run-OracleAudit.ps1` solicita entrada en formulario local con caracteres ocultos y envía la contraseña exclusivamente a stdin privado del proceso Java. El modo opcional de sesión conserva SecureString en memoria del lanzador para recompilar/repetir pruebas sin reentrada. Se libera al finalizar la sesión; no se escribe en archivos, argumentos, variables de entorno ni Git.
- El harness vive sólo en src/test, conecta por JDBC a localhost/FREEPDB1 y valida USERTEST + PDB antes de cualquier DDL. El servidor de prueba, si arranca, escucha 127.0.0.1:3299 y usa identidad sintética y CSRF. No es el login corporativo ni despliegue productivo.
- Verificación: test-compile inicial satisfactorio con Java 8. Ventana de credenciales lanzada; inspección SQL pendiente de entrada del usuario. Los objetos existentes ajenos no se modifican.

## D-005 — Catálogo Oracle real y fixture mínimo (confirmada para el catálogo; ejecución fixture pendiente)

- Evidencia ejecutada: `target/oracle/inspect.log`, exitCode 0. `SELECT USER, SYS_CONTEXT('USERENV','CON_NAME') FROM DUAL` devuelve USERTEST/FREEPDB1; JDBC reporta Oracle AI Database 26ai Free 23.26.3.0.0. SESSION_PRIVS confirma CREATE TABLE/VIEW/SEQUENCE/PROCEDURE.
- USER_OBJECTS antes del setup: únicamente ORACLE_TEST_TABLE y TM_CONF_CATUAL_3G_AUX (TABLE, VALID). Las consultas USER_TAB_COLUMNS para los seis nombres del fixture devuelven cero filas. No existe colisión con un objeto original.
- Decisión: emplear los nombres exactos requeridos por las consultas para las fuentes locales 4G/5G, tabla semanal TA y Recursos; crear AUDRED_TA5G_AUX como tabla auxiliar para la vista TA 5G. El esquema mínimo proviene de las proyecciones SELECT y la referencia Java suministrada, no de DDL corporativo.
- Objetos previstos: TM_CONF_ACTUAL_CELDA_4G, TM_CONF_ACTUAL_CELDA_5G, PLAN_MAP_SECT_CRIT_4GWEEK, AUDRED_TA5G_AUX, VW_PLANI_RECURSOS_5G_TA_DIARIO (vista), RECURSOS_FORMULARIO. Sólo fixtures propios con comentario AUDITORIA_RED_SYNTHETIC_V1_20261027 pueden reutilizarse. Preflight de todos los nombres y restricciones/índices antes del primer DDL; no DROP/UPDATE/DELETE.
- Las 59 columnas/tipos requeridos se enumeran en src/test/resources/oracle/schema-columns.properties; longitudes y PK figuran en auditoria-red-fixture.sql. Cinco PK AUDRED_* generan sus índices; no se añaden índices de optimización corporativa ni secuencias/procedimientos.
- Datos previstos: 117 filas sintéticas insertadas por IDs si aún no existen; nombres AUDRED_* y comentarios documentan procedencia. Las tablas previas y 3G quedan fuera del análisis.
- Verificación pendiente: setup idempotente, conteos/constraints/vista, consultas JOIN reales, escenarios y navegador con este datasource.

Las decisiones específicas y los resultados se incorporarán durante la implementación; este registro no representa una verificación terminada.

## D-006 — Cierre de implementación y verificación (confirmada localmente)

2026-10-27. Esta entrada y los anexos L/UI/ORA siguientes sustituyen explícitamente los pendientes de D-002, D-004 y D-005. Se conserva su estado inicial como historia; no debe leerse como resultado final.

- Compilación Java 8/Nashorn y JAR: 102 pruebas, cero fallos/errores/omitidas, en una copia de fuentes idénticas fuera del IDE. Un intento anterior encontró clases de error temporales generadas por JDT y un import de test faltante; éste se corrigió y la copia final se compiló completamente con javac/Maven. Evidencia: target/verification/module-verified-build.log y source-hashes-verified.json. Sólo se actualizó después JavaScript/ensayo de navegador; sin cambios Java, el JAR se reempaquetó y pasó las verificaciones visuales finales.
- Host: las 284 fuentes principales compilan. Las mismas 102 pruebas de Auditoría pasan contra clases/dependencias del host. La compilación global de tests conserva el fallo ajeno Planning4gTest/updateSolution; no se modifica ni se oculta. Logs host-compile.log, host-audit-tests.log y host-test-compile.log.
- Oracle real: USERTEST/FREEPDB1 23.26.3. Primera creación: cinco tablas, una vista, 59 columnas verificadas, cinco PK con índices únicos y 117 filas sintéticas. Se ampliaron seis semillas para celdas aisladas e intervalo TA abierto: total 123 filas en tablas. Conteos finales: TM_CONF_ACTUAL_CELDA_4G=17; TM_CONF_ACTUAL_CELDA_5G=48; PLAN_MAP_SECT_CRIT_4GWEEK=12; AUDRED_TA5G_AUX=44; su vista=44; RECURSOS_FORMULARIO=2.
- Ejecución final: ORACLE_VERIFICATION_PASS checks=71 findings=1140 context=54. Las 54 celdas se distribuyen en 52 dentro del área y dos en margen. Los SELECT leen 16 filas 4G y 48 5G y descartan un PCI fraccionario por tecnología; dos coordenadas inválidas quedan fuera en SQL. Se prueban duplicados oficiales, TA contradictorio, radio desconocido, unidades, límites, jefatura, filtros y controladores reales. Logs initial-all.log y verified-all.log preservan ambas etapas.
- Idempotencia: setup.log exitCode=0, 123 INSERT con cero filas insertadas, cero objetos creados; las dos tablas previas permanecen intactas. No hay secuencias, procedimientos ni índices adicionales de rendimiento.
- Navegador: 13 escenarios Oracle y 17 sintéticos, cero errores de página. Se inspeccionaron escritorio/móvil, tablas densas, evidencia/vecinas/margen, vacío/incompleto, TA aislado/abierto, teclado y mapa con radio desconocido representado sólo por punto. Datos Oracle sintéticos: 1140 hallazgos, 52 celdas en área, 37 vecinas disponibles del objetivo de prueba. Capturas/JSON en target/browser/oracle y target/browser.
- Integración al host: scripts/Sync-AuditoriaHost.ps1 copió 67 archivos del módulo, 34 con cambios. Respaldo anterior y applied-files.json con SHA256 en web-tracker-migration/auditoria-red-2026-10-27. Comparación posterior: cero diferencias fuera de los archivos de Auditoría sincronizados; rama host feature/mapa-osm preservada. El repositorio aislado continúa siendo autoritativo; POM/configuración/datasources ajenos sin cambios.
- Alcance demostrado: funcionamiento local sobre fixture mínimo inferido de consultas Java; no es copia de esquema ni red corporativa. Canal RF no cargado, RSI configurado no leído, confusión real no evaluada, fechas de fuente desconocidas. Grupos distintos carrier/banda sin canal no se comparan ni quedan declarados seguros. TA 4G sin sufijo de unidad necesita contrato de fuente para excluir que sean contadores; la normalización no prueba ese contrato. Geometría/haz/respaldo siguen siendo supuestos, con margen y complejidad documentados. Reservas automáticas y polígonos de clúster siguen fuera del alcance implementado.

## Anexos de decisiones detalladas

Los anexos siguientes forman parte de este registro mantenido. Las fuentes primarias están enlazadas en D-003. Los resultados definitivos de D-006 prevalecen sobre resultados preliminares de cada anexo.

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


# Evidencia Oracle local de Auditoría Red

## ORA-01 · 2026-10-27 · Confirmado contra Oracle local

El esquema corporativo completo no está disponible. Las proyecciones de `AuditoriaRedRepository.SQL_4G/SQL_5G` y `LectorSitioPlanificado.SQL` definen un **fixture mínimo sintético**, no una copia acreditada de producción. No se activa Hibernate DDL ni se cambia ningún datasource del host. Los archivos autoritativos están en el repositorio aislado.

Se usan los nombres exactos de consulta únicamente en USERTEST/FREEPDB1 local cuando están libres. Esto permite ejecutar SQL, repositorios, servicios, controladores y motores reales sin reemplazar el repositorio por un mock. `AUDRED_TA5G_AUX` conserva el sufijo auxiliar para el respaldo sintético de la vista. El nombre `TM_CONF_CATUAL_3G_AUX`, `ORACLE_TEST_TABLE` y todos los otros objetos existentes quedan fuera del fixture.

Antes del primer DDL, el harness comprueba la identidad USERTEST y PDB FREEPDB1, consulta `SESSION_PRIVS`, `USER_OBJECTS` y `USER_TAB_COLUMNS`, exige CREATE TABLE/VIEW y revisa todos los nombres de objetos, constraints e índices. Un objeto existente sólo se reutiliza cuando su comentario empieza por `AUDITORIA_RED_SYNTHETIC_V1_20261027 - ` y su tipo coincide. Una colisión sin esa marca interrumpe el proceso sin modificar ni borrar el objeto. No hay DROP, UPDATE ni DELETE. Los DDL pueden confirmar implícitamente: si falla la creación o el comentario, se detiene y se requiere revisar el objeto sin marca; no hay limpieza destructiva automática.

## ORA-02 · 2026-10-27 · Fixture creado y verificado en Oracle local

| Objeto | Tipo | Columnas | Semillas esperadas |
|---|---|---:|---:|
| TM_CONF_ACTUAL_CELDA_4G | TABLE | 10 | 17 |
| TM_CONF_ACTUAL_CELDA_5G | TABLE | 9 | 48 |
| PLAN_MAP_SECT_CRIT_4GWEEK | TABLE | 10 | 12 |
| AUDRED_TA5G_AUX | TABLE | 10 | 44 |
| VW_PLANI_RECURSOS_5G_TA_DIARIO | VIEW | 9 | 44, desde AUX |
| RECURSOS_FORMULARIO | TABLE | 11 | 2 |

Las cinco tablas tienen una clave primaria numérica de fixture: `FIXTURE_ID`, salvo `RECURSOS_FORMULARIO.ID`. Los constraints/índices únicos asociados son `AUDRED_4G_PK`, `AUDRED_5G_PK`, `AUDRED_TA4G_PK`, `AUDRED_TA5G_PK` y `AUDRED_RECURSOS_PK`. No se agregan índices de optimización: estos volúmenes no justifican conclusiones de capacidad. Se permite duplicar la identidad de celda y la clave de join TA para probar conflictos reales del SELECT.

`schema-columns.properties` verifica 59 nombres/tipos proyectados y el número exacto de columnas. Los catálogos muestran longitudes, precisión, escala, nulabilidad, constraints, índices y conteos. Las coordenadas, PCI, azimuth y TA sintéticos son VARCHAR2 para probar lectura textual, comas decimales, porcentajes y datos inválidos. FREQBAND 4G es NUMBER para probar el TO_CHAR existente. Estos tipos son una decisión de fixture y no acreditan tipos corporativos.

Las 123 inserciones usan `INSERT ... SELECT ... FROM DUAL WHERE NOT EXISTS` por ID, en una transacción y sin sobrescribir datos. Una repetición debe insertar cero filas; cualquier cambio manual que contradiga resultados esperados falla en la verificación en vez de corregirse silenciosamente.

## ORA-03 · 2026-10-27 · Escenarios y expectativas independientes

Se incluyen 4G/5G: mismo PCI en el mismo carrier/banda, carrier y banda incompatibles, mismo mod-3 con distinto PCI, cositios, una vecina a unos 6,5 km fuera del círculo de 1 km pero dentro del margen, TA porcentaje/fracción/redondeado/ausente/inválido, coordenadas con coma y basura, PCI fraccionario, duplicados idénticos, identidad con PCIs incompatibles y dos filas TA distintas que multiplican el LEFT JOIN 5G. Cada tecnología también tiene `SPARSE`, con TA ausente y carrier aislado sin vecinas, y `OVERFLOW`, con 100% en el intervalo terminal abierto de TA y carrier aislado. Se espera radio no disponible con procedencia explícita para ambas; no se inventa un alcance finito. Treinta celdas 5G densas verifican paginación y topes. Dos reservas seleccionadas verifican autorización por jefatura; no se incorporan automáticamente todas las reservas.

Expectativas: las coordenadas inválidas no abortan Oracle; PCI fraccionario se descarta; TA válido se normaliza a suma 1; TA faltante/incorrecto mantiene estado explícito; duplicados idénticos colapsan y los conflictos de identidad se excluyen con advertencia. Los pares de otro carrier/banda no se comparan. La vecina del margen sí participa. Un canal EARFCN/NRARFCN desconocido sigue desconocido; el fixture no transforma carrier/banda en prueba de misma frecuencia. El área/distancia conserva unidades m²/m y no es porcentaje. Los filtros de presentación conservan el universo de vecinas. Un cap sigue siendo incompleto. El acceso sin identidad y a otra jefatura falla.

La tolerancia de suma de TA es 0,99–1,01 para fracciones sin símbolo `%`, o 99–101 para porcentajes; luego cada bin se divide por la suma exacta. Se requieren ocho bins completos, finitos y no negativos. Estos ensayos prueban esas unidades declaradas, sin acreditar que las columnas 4G sin sufijo `_PORC` del entorno corporativo representen porcentajes en vez de contadores. Si son contadores, se requiere una definición de fuente y un adaptador explícito; no se normalizan totales arbitrarios silenciosamente. El join 4G exige coincidencia exacta de SECTOR_NAME y el 5G ignora mayúsculas de CELLNAME; sus reglas de limpieza/formato deberán contrastarse con la fuente corporativa.

`OracleIntegrationVerification` contiene aserciones ejecutadas contra el JdbcTemplate real y motores Nashorn. El servidor `OracleBrowserFixture`, sólo en `src/test`, carga las clases reales e identidad local de prueba en 127.0.0.1:3299/tracker, con filtro CSRF real y caché desactivada. No hay login ni configuración de prueba empaquetados en el JAR principal.

## ORA-04 · 2026-10-27 · Credenciales y repetición

Java 8 y el driver instalado `C:\app\zuoti\product\26ai\dbhomeFree\jdbc\lib\ojdbc8.jar` se agregan sólo al classpath de ejecución. La contraseña se ingresa directamente en la ventana enmascarada de `scripts/Run-OracleAudit.ps1` y viaja por stdin privado. No se acepta contraseña en argumentos, propiedades ni variables de entorno; no se imprime ni se escribe en archivos. El modo opcional de sesión del launcher conserva sólo SecureString en la memoria del proceso para repetir después de recompilar, hasta cerrar el launcher.

```powershell
.\mvnw.cmd -B test-compile dependency:build-classpath '-Dmdep.outputFile=target/test-classpath.txt'
.\scripts\Run-OracleAudit.ps1 -Mode inspect
.\scripts\Run-OracleAudit.ps1 -Mode all
```

`all` inspecciona, prepara de forma protegida, verifica y mantiene el servidor abierto. `inspect`, `setup`, `verify` y `serve` permiten separar pasos. La salida de catálogos, semillas y aserciones se guarda en `target/oracle/<modo>.log` sin secretos. La URL del dashboard local es http://127.0.0.1:3299/tracker/auditoria-red/dashboard. Los datos se buscan con AUDRED y el centro representativo es -12.09, -77.05.

## Resultados observados

La conexión real confirmó **USERTEST/FREEPDB1**, Oracle AI Database Free **23.26.3.0.0**, y los cuatro permisos CREATE TABLE/VIEW/SEQUENCE/PROCEDURE. `target/oracle/inspect.log` mostró únicamente `ORACLE_TEST_TABLE` y `TM_CONF_CATUAL_3G_AUX` preexistentes; ninguno de los seis nombres de fixture existía. No se usó SYS, SYSTEM ni PDBADMIN.

La primera ejecución `all` creó los seis objetos y aplicó **117 INSERT con una fila cada uno**. Verificó los 59 nombres/tipos, números exactos de columnas y conteos iniciales 15/46/11/43/43/2, cinco constraints P habilitados y sus cinco índices UNIQUE/VALID. La marca quedó aplicada en todos los objetos. No se agregaron secuencias ni procedimientos. Su salida está preservada en `target/oracle/initial-all.log`.

El SELECT real 4G devolvió 14 filas; el 5G devolvió 46, incluida la multiplicación deliberada por dos TA diferentes y el join case insensitive. Cada tecnología descartó un PCI fraccionario; las coordenadas basura quedaron fuera en SQL sin ORA-01722. Las comas decimales, porcentajes, fracciones y LEFT JOIN sin TA dieron los resultados esperados.

El primer resultado fue **`ORACLE_VERIFICATION_PASS checks=60 findings=1140 context=50`**. Esos hallazgos sintéticos corresponden a círculo de 1 km, margen de 10,1 km y ambas tecnologías. Las identidades oficiales con PCI incompatible y la identidad 5G con TA contradictorio se excluyeron del contexto; los duplicados idénticos colapsaron. Quedaron diez celdas 4G y cuarenta 5G, incluidas dos vecinas del margen. Se comprobaron pares de borde, exclusión carrier/banda, unidades m²/m, índice nulo en cositio, cache cero, límites reales del motor, contexto conservado al filtrar/paginar, lectura planificada autorizada y rechazo de otra jefatura. El controlador sin identidad rechazó la consulta. Los detalles SQL y PASS están en `target/oracle/all.log`.

Tomcat inició únicamente en **127.0.0.1:3299**, con contexto `/tracker`, usando el repositorio Oracle real y CSRF real. Se añadieron después seis filas sintéticas `SPARSE`/`OVERFLOW` y once comprobaciones: rechazo de los dos objetos originales sin marca, límite real de filas JDBC y ocho aserciones del radio/procedencia de esos escenarios.

La repetición final contra clases verificadas registró **`ORACLE_VERIFICATION_PASS checks=71 findings=1140 context=54`**, preservado en `target/oracle/verified-all.log`. No recreó objetos: seis INSERT añadieron las nuevas filas y 117 INSERT omitieron IDs existentes. Los conteos finales fueron **17/48/12/44/44/2**, en el orden de la tabla anterior. Las cuatro celdas aisladas `SPARSE`/`OVERFLOW` devolvieron radio nulo y procedencia explícita, sin inventar sectores. Se demostró el rechazo de los dos objetos originales sin marca y el corte explícito del SELECT Oracle al exceder dos filas.

La repetición independiente `setup` terminó con **exitCode 0, 123 INSERT omitidos, cero filas insertadas y cero objetos creados**, según `target/oracle/setup.log` y `target/oracle/exit.json`. Se confirma así la idempotencia con el fixture completo; las tablas y los datos existentes no se reemplazaron. A continuación `serve` reinició únicamente el servidor local en **127.0.0.1:3299/tracker**, conservando el mismo datasource Oracle y el fixture verificado. El resultado visual se registra en la entrega principal. La contraseña no se volvió a solicitar ni se persistió: la repetición utilizó la credencial sólo en memoria del lanzador.

Las filas con coordenadas inválidas se excluyen en la cláusula WHERE antes de formar el área: no es posible contabilizarlas como descartes de un círculo al que no pueden asignarse. El conteo `descartadasSinDatos` sólo cubre filas seleccionadas con otros campos inválidos. Esta limitación se informa en el dashboard. Una ejecución exitosa local no acredita el esquema corporativo, configuración RF de la red ni capacidad con el volumen de producción.


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

## D-007 — Publicación en rama nueva (confirmada, 2026-10-27)

- Solicitud explícita del usuario: publicar el trabajo en otra rama.
- Rama nueva: feature/auditoria-red-oracle-validada, en VBAUTSITA/whatever_360. La primera publicación contiene el código verificado del commit fd89aad133c80220d70b2913384a93967d3cb07c; esta actualización sólo registra la publicación.
- Se conserva la rama remota feature/auditoria-red-dashboard en d2f10fa51ac3e78b6c15d067001628bc29b09a99 y main en 2b4dd40a9cd3c7ead545b21fefb35c3f229516a5. Sin merge ni force-push.
- La copia autoritativa continúa en auditoria-red-publicacion, ahora con checkout y seguimiento de feature/auditoria-red-oracle-validada. La rama indicada en la evaluación inicial describe el punto de partida histórico.
- Verificación: push inicial completado; publicación final cotejada mediante git ls-remote y HEAD. Las pruebas de D-006 corresponden al mismo código; este registro no cambia lógica ni recursos ejecutables.