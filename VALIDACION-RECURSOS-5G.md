# Validación de Recursos 5G y pautas para otra IA

Fecha: **2026-10-28, America/Lima**. Alcance: cargar contexto, proponer PCI/RSI, conservar recursos existentes, guardar y comprobar acceso en Recursos. Esta es la versión publicable del expediente local: no contiene reportes de red, coordenadas, inventarios, credenciales ni resultados individuales de la red del usuario.

## Estado demostrado

**Recursos tiene defectos reproducidos y no está aprobado para producción.** Las comprobaciones de Auditoría Red documentadas en esta rama son independientes de esta validación.

| Capa | Resultado | Alcance y límite |
|---|---|---|
| Java 8/Nashorn, motor original | 0 de 3 nuevas celdas del lote real calculadas | `TypeError: minByPci.fill is not a function`, por `Float64Array.fill` ausente |
| Checks Java con servicios/controlador originales | 7 PASS, 9 FAIL, 3 BLOCKED | 19 checks locales, incluidos los tres casos con reportes privados; repositorio/fuente sustituidos explícitamente |
| Copia aislada con compatibilidad ES5 de `Float64Array.fill` | 3 de 3 propuestas; 12 PASS, 7 FAIL | Diagnóstico de reglas posteriores; el motor principal publicado conserva el fallo original |
| Controles independientes de ese lote de diagnóstico | 54 de 54 | Procedencia, IDs, rangos, PSS, lookup, Haversine, vecinas y reuso. No certifican optimalidad ni configuración RF completa |
| Node, motor original del host | 14 de 16 invariantes requeridas | Dos fallos de identidad/espejo al distinguir sectores 10 y 11. Node no demuestra compatibilidad Nashorn |
| Spring MVC con cadena de seguridad actual del host | 1 PASS, 4 FAIL | Guardado anónimo, otra jefatura, valores inválidos y autoasignación anónima no fueron rechazados; dependencias DB, motor, login y vistas sustituidas |
| Oracle de Recursos | PENDIENTE | Harness preparado; no se introdujo la credencial de la sesión. No atribuir a Recursos los PASS Oracle de Auditoría |
| Pantalla Recursos / Hibernate / despliegue corporativo | PENDIENTE | MockMvc y el adaptador JDBC no prueban estas capas |

Los conteos anteriores corresponden al expediente local completo. El runner publicado incluye 16 checks controlados sin reportes privados; los tres restantes requieren que el usuario prepare sus datos localmente. No comparar automáticamente 16 con 19 como si faltaran ejecuciones.

## Defectos y decisiones

1. **Runtime:** el motor 5G original utiliza `Float64Array.fill`. Reproducir con Java 8 real. Mantener el fallo original y la copia con compatibilidad separados por hash y etiqueta. No contar un bloqueo como prueba aprobada.
2. **Asignaciones parciales:** el servicio omite solamente filas con ambos recursos informados. Al faltar uno, elimina el PCI al construir el DTO y luego aplica ambos resultados. En un control, PCI 777 cambió a 316; en otro, RSI 838 cambió a 632. Regla de validación: conservar el dato informado o explicar la incompatibilidad antes de cambiarlo.
3. **Reservas:** la fuente 5G consulta red oficial y TA; no incorpora reservas no seleccionadas de Recursos. Comprobar todas las asignaciones guardadas, cualquiera sea su edad, y la precedencia de la red oficial para la misma identidad. La prueba de llamadas identificó la ausencia de reservas; los escenarios Oracle de edad/precedencia siguen pendientes.
4. **Datos inválidos:** latitud NaN/azimut 999 llegaron a producir PCI 0 con compatibilidad. TA negativo y distribución de 60% fueron aceptados. No convertir datos faltantes o inválidos en evidencia de una asignación válida.
5. **Guardado:** el controlador acepta PCI 1008/RSI −1 en una celda 5G. Validar rangos y permisos en servidor, antes del `save`, con relectura por ID.
6. **Acceso:** la cadena de seguridad local y controlador permitieron operaciones anónimas y sobre otra jefatura en MockMvc. La vista de prueba produjo HTTP 200; también se registraron invocaciones al servicio y llamadas de escritura. Esto demuestra el comportamiento de las fuentes locales, sin afirmar que otro despliegue corporativo sea accesible.
7. **Identidad:** el motor original toma un carácter del nombre como número de sector y puede confundir sufijos de dos dígitos. Probar sectores 10/11/12 con identidad explícita antes de permitir espejos.
8. **Frecuencia:** el reporte contiene varios DLNRARFCN bajo un mismo carrier/banda y canales compartidos por carriers distintos. C1/C2 son etiquetas, no prueba de frecuencia diferente. El canal de las nuevas propuestas no fue informado; la validación exacta por canal queda pendiente.
9. **RSI:** contrastar propuestas con la tabla de planificación. El `logicalrootsequenceindex` desplegado del reporte es una fuente distinta del RSI derivado por PCI. Una coincidencia requiere configuración PRACH/frecuencia para interpretarse.
10. **Geometría:** el contexto del lote real tuvo 1936 filas dentro de las cajas actuales de 150 km y no tuvo TA utilizable. Se probó fallback, no cobertura medida. Altura y HBEAM presentes en el libro de propuestas no viajan en el DTO actual. El radio cambia al incorporar propuestas previas, incluso por distancias co-sitio de 0 m.

## Procedencia y datos

El reporte nacional aportado tuvo 5098 filas y 5095 nombres; el servicio admitió 4326 filas y omitió 772 sin geometría suficiente. Se seleccionaron tres propuestas NR genuinas; no se convirtieron filas LTE en 5G. El usuario confirmó **32T32R para las nuevas celdas 5G**. El lote no estaba en la red existente por nombre, sitio ni co-sitio a 1 m.

Se revisaron 45 archivos originales sin cambios de SHA-256. Los dos snapshots de radio/TA no se unieron sólo por nombre cuando cambió PCI o azimut. La salida histórica de la suite es material de comparación, no un golden obligatorio: cambian motor, normalización, apertura y políticas.

Las fuentes actuales de Recursos y sus dos motores se publican sin aplicar correcciones de diagnóstico. Sus hashes están en `PUBLICACION-MANIFIESTO.json`. La referencia `meg`, el host y `planner_suite` difieren; no atribuir equivalencia desplegada a una coincidencia de nombre de archivo.

## Comprobaciones sencillas y deterministas

Una IA puede hacer una comprobación por vez y guardar **PASS / FAIL / BLOCKED / PENDIENTE**, entrada, observado, esperado y evidencia. No modificar esperados para obtener verde.

| Caso | Acción | Resultado que debe comprobar |
|---|---|---|
| Tecnología | Solicitar NR/5G y luego tecnología ambigua | Usar exclusivamente el motor/fuente 5G; omisión explícita si es ambigua |
| Recursos completos/parciales | Cuatro registros: vacío, completo, PCI solo y RSI solo | Conservar lo informado o bloquear con explicación; no sustitución silenciosa |
| Datos mínimos | Quitar carrier/azimut y probar NaN/coordenadas fuera de rango | Rechazo visible; no asignación inventada |
| Lookup | Comprobar PCI entero, cobertura y RSI por tabla aprobada | Outdoor 0–985 / indoor 986–1007 según esta política; PSS = PCI % 3. No convertir esa partición en norma universal |
| Lote y mapeo | Reordenar salida y duplicar nombre | Resultados correctos por nombre/ID, duplicados visibles |
| Reservas | Reserva antigua no seleccionada, reciente y fuera de primera página | Todas presentes en contexto; fecha y paginación no liberan recursos |
| Precedencia | Reserva y red oficial de la misma identidad | Una identidad, precedencia oficial y explicación de diferencias |
| TA | Fracciones, porcentajes, negativos, bins incompletos y bin abierto | Unidades y ausencias explícitas; no aceptar distribución incompleta como completa |
| Frecuencia | Carrier distinto con mismo canal; mismo carrier con canales distintos | No inferir compatibilidad sólo por etiqueta comercial |
| Autoasignar | Consultar DB antes/después de calcular | Sin escrituras explícitas; probar aparte dirty checking de Hibernate |
| Asignar | Guardar en fixture y releer desde otra sesión por ID | Coincidencia exacta y ninguna fila ajena modificada |
| Permisos | Anónimo, usuario sin mapeo y otro ID/jefatura | Rechazo antes del motor/guardado; probar CSRF en la cadena real |

## Repetir los checks publicados

Java 8 y Python 3. No se necesita contraseña para los checks controlados. Desde la raíz del repositorio:

```powershell
.\mvnw.cmd -B test-compile dependency:build-classpath '-Dmdep.outputFile=target/recursos-checks/dependencies.txt' '-Dmdep.includeScope=test'
python .\validacion\recursos-5g\run_checks.py --java-home $env:JAVA_HOME
```

El runner compila el harness fuera del árbol principal. Conserva salidas bajo `validacion/recursos-5g/resultados`, ignoradas por Git, y ejecuta `baseline` y `compatfill` por separado. Devuelve **1 si encuentra FAIL/BLOCKED**, y **2 si no puede completar el entorno/harness**. Un resultado fallido es evidencia del defecto, no un motivo para borrar la prueba.

Opcionalmente, preparar `datos/red5g-host.json`, `datos/red5g-cemento-host.json` y `datos/plan5g-host.json` dentro de esa carpeta para los tres checks adicionales del lote. Son listas de DTO con nombres JSON exactos del código; registrar procedencia, unidades, filtros y hashes. `red5g-cemento-host.json` debe respetar las cajas del servicio actual. Los datos privados no se incluyen ni deben añadirse al Git.

## Oracle y continuación

Antes de DDL, verificar conexión local USERTEST/FREEPDB1, identidad, catálogos, columnas, PK/índices, nombres libres y marcadores. El harness local usa `RECV5G_` para no reutilizar los objetos de Auditoría. La contraseña se introduce manualmente en un formulario local y pasa sólo por stdin privado en memoria. Ningún valor secreto va a argumentos, variables de entorno, documentación o Git.

El harness Oracle preparado adapta únicamente dos nombres del SELECT hacia tablas marcadas y usa un repositorio JDBC. Eso permitiría comprobar SQL y orquestación del servicio/controlador; no sustituye pruebas JPA/Hibernate ni HTTP. Al continuar, registrar sus resultados como una capa distinta. Oracle de Recursos permanece pendiente en esta publicación.

Prioridad de corrección: compatibilidad Nashorn, conservación parcial, contexto de reservas/frecuencia, validación del servidor y permisos. Cada cambio requiere reproducir el FAIL original y añadir la verificación de la versión corregida. El estado publicado no contiene estas correcciones.
