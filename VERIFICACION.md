# Verificación de entrega — 2026-10-26

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
