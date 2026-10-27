# Verificación vigente — 2026-10-27

La versión actual supera la línea base histórica de abajo. Decisiones, fórmulas, fuentes, objetos Oracle y límites: [DECISIONES-AUDITORIA-RED.md](DECISIONES-AUDITORIA-RED.md), con [detalle Oracle](DECISIONES-ORACLE.md) y [detalle visual](DECISIONES-UI.md).

- Java 8u504 / Nashorn real, Spring Boot 2.1.7. **102 pruebas JUnit: 0 fallos, 0 errores, 0 omitidas.** Compilación y JAR correctos. Las mismas 102 pruebas pasan contra las clases/dependencias compiladas del host.
- Una ejecución intermedia usó clases temporales defectuosas escritas por el IDE en target. La verificación final compiló una copia idéntica fuera del IDE; hashes confirmaron las mismas fuentes Java. Después sólo se actualizaron dos recursos de navegador y se reempaquetó el JAR, manteniendo Java intacto y ejecutando las pruebas visuales finales.
- Oracle real: USERTEST/FREEPDB1, Oracle AI Database Free 23.26.3. Cinco tablas y una vista sintéticas, 59 columnas verificadas y cinco PK/índices. **71 comprobaciones pasan**, 1140 hallazgos sintéticos, 54 celdas de contexto (52 del área). La repetición del setup aplicó 123 INSERT con cero filas nuevas y cero objetos creados; no modificó las dos tablas preexistentes.
- Consultas LEFT JOIN y conversiones Oracle reales; TA fracción/porcentaje/coma/ausente/inválido/abierto, celdas aisladas, duplicados idénticos/contradictorios, vecina del margen, búsqueda literal, límites JDBC/motor, autorización/jefatura, filtros/paginación sin recortar contexto.
- Chrome/Playwright: **13 escenarios sobre Oracle** y **17 de regresión sintética**, escritorio 1500 px y móvil 390 px, cero errores JavaScript. Capturas inspeccionadas: identidad Claro/ActiTracker, tablas densas, selección/evidencia, vacío/incompleto, radio desconocido como punto, teclado, CSRF y sesión expirada. La simulación de estados de error/incompleto pertenece al ensayo sintético, no a la evidencia Oracle.
- Host: compilan las **284 fuentes principales**. La compilación normal de tests sigue fallando exclusivamente en Planning4gTest por `updateSolution` ausente; no se modificó ese módulo ni se afirma que su suite global pase.
- Sincronización: 67 archivos de Auditoría verificados, 34 actualizados; respaldo y manifiesto en `../web-tracker-migration/auditoria-red-2026-10-27`. La comparación de hashes de archivos ajenos dio cero diferencias; el host sigue en feature/mapa-osm.

Evidencia local: `target/verification/module-verified-build.log`, `host-compile.log`, `host-audit-tests.log`, `host-test-compile.log`; `target/oracle/initial-all.log`, `verified-all.log`, `setup.log`; `target/browser/result.json` y `target/browser/oracle/result.json` con sus capturas. Estos artefactos generados están fuera de Git.

```powershell
# Java 8; entorno normal sin clases de error temporales del IDE:
.\mvnw.cmd -B package dependency:build-classpath '-Dmdep.outputFile=target/test-classpath.txt'
powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\Run-OracleAudit.ps1 -Mode all
# Con Node/Playwright disponible:
node src/test/browser/dashboard-oracle.cjs
```

Dashboard de prueba: http://127.0.0.1:3299/tracker/auditoria-red/dashboard. Buscar AUDRED; centro −12,09 / −77,05, radio 1 km. La contraseña se ingresa sólo en el formulario local enmascarado.

Persisten límites corporativos: no se conoce DDL completo, canal RF, RSI/PRACH real ni fechas de fuente; TA/haz/fallback son modelos documentados; no se demuestra confusión PCI real ni cobertura de toda interacción geométrica. No se midió capacidad a escala productiva. Las reservas guardadas no se incorporan automáticamente y el alcance soportado es circular 4G/5G.

## Registro histórico de entrega — 2026-10-26

- Java 8u504 / Nashorn real; Spring Boot 2.1.7; Maven 3.6.0.
- 82 pruebas JUnit: 0 fallos, 0 errores, 0 omitidas, en el proyecto aislado.
- Incluyen agrupación carrier/banda, lectura/normalización, límites, autenticación/jefatura, motor 4G/5G, caché y concurrencia, API y renderizado Thymeleaf.
- Regresiones nuevas: centros separados por 0.000001° no comparten caché; respuestas mutables no contaminan resultados posteriores; caché cero; PCIs fraccionarios/fuera de rango; TA negativo/NaN; filtros extensibles y combinables; paginación; tope visible aun sin filas filtradas; contexto fuera del área disponible al explorar vecinas.
- Navegador Chrome/Playwright contra un servidor Spring real de prueba: escritorio 1500 px y móvil 390 px; búsqueda, consulta real con Nashorn, 25 filas/página, filtros, 30 vecinas, texto malicioso tratado como texto, selección de mapa, ausencia de desbordamiento horizontal de página, resultado incompleto, respuesta HTML de sesión, envío de CSRF y carga de la pantalla anterior. Sin errores JavaScript de página.
- Compilación del Web Tracker completo: 284 fuentes Java compilan con Java 8. La ejecución Maven de tests del host se detiene al compilar un test existente de Planning4G que invoca el método ausente `updateSolution`; no se modificó ese módulo. Las 82 pruebas de Auditoría también pasan al compilarlas y ejecutarlas aisladamente contra las clases y dependencias del host.
- Regresión geográfica: la caja SQL usa la misma esfera que el recorte exacto y no pierde el borde norte/sur; incluye cruces de antimeridiano y polos.
- Las capturas y resultados generados van a `target/browser` y no se publican como datos de producción.

## Repetir ensayo de navegador

Se necesita Playwright y Chrome instalados. El servidor de ensayo escucha únicamente en localhost, utiliza datos sintéticos y aporta identidad de prueba. **Nunca se debe desplegar esta clase**; reside en el classpath de tests y queda fuera del JAR.

```powershell
.\mvnw.cmd -B test dependency:build-classpath '-Dmdep.outputFile=target/test-classpath.txt'
$cp='target\test-classes;target\classes;'+[IO.File]::ReadAllText((Resolve-Path target\test-classpath.txt))
& "$env:JAVA_HOME\bin\java.exe" -cp $cp com.tracker.web.auditoriared.DashboardBrowserFixture
# Otra terminal, con Playwright disponible:
node src/test/browser/dashboard.cjs
```

Si Playwright no está en el path de módulos de Node, establezca `PLAYWRIGHT_MODULE` con la ruta de su instalación. El ensayo bloquea mosaicos externos para no depender de la red de mapas.

## Límites de la verificación

No se conectó a Oracle ni se comprobó el despliegue corporativo. Las consultas Oracle y los límites de capacidad requieren validación con datos y permisos reales. No se confirma equivalencia con JavaScript que pueda estar actualmente en producción. Las métricas son reglas/geométricas, no mediciones de interferencia, propagación ni confusión PCI completa. No se modificó la asignación de Recursos ni se ejecutó SQL de creación de vistas.
