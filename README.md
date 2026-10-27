# Auditoría de red y panel PCI

Distribución exclusiva del módulo Auditoría Red de Web Tracker, Java 8 / Spring Boot 2.1.7. Rama `feature/auditoria-red-dashboard` destinada a `VBAUTSITA/whatever_360`. Es una rama independiente para distribuir el módulo: no incluye los demás módulos ni comparte el árbol completo de `main`. No debe fusionarse mediante una sustitución del proyecto.

## Uso

- `/auditoria-red/dashboard`: panel de hallazgos, filtros, paginación, vecinas de una celda y mapa de la relación seleccionada.
- `/auditoria-red`: auditoría de un área circular con margen y exportación CSV.
- `/auditoria-red/sitio?ids=12,13&pci=100,101`: auditoría de PCIs propuestos para registros autorizados de un solo sitio de Recursos. Los números del ejemplo son ficticios.

El panel permite buscar un sitio/celda o introducir coordenadas. El área actual es un círculo; no interpreta nombres como polígonos de clúster ni inventa una tabla de clústeres. Los filtros de hallazgos se aplican después del cálculo. No eliminan celdas del contexto que recibe el motor.

El mapa resalta un par seleccionado y evidencia de TA, frecuencia y geometría. Contexto añade hasta 200 marcadores del área sin cambiar el cálculo. Las vecinas provienen del contexto completo, aunque no tengan hallazgos: entre 1 y 200, con 30 por defecto. Sin canal RF se usa carrier/banda sólo como grupo candidato; otros grupos no quedan certificados como seguros. Con ambos canales explícitos, la igualdad de canal prevalece sobre las etiquetas de carrier/banda.

## Integración en Web Tracker

Este proyecto genera un **JAR de módulo**, no un servidor de producción autónomo. No contiene `application.properties`, credenciales, login propio ni configuración Oracle. No sustituya el `pom.xml` de Web Tracker por este POM.

1. Trabaje sobre una copia/commit de respaldo del Web Tracker completo. Compare e integre `src/main/java/com/tracker/web/auditoriared` y los recursos `scripts/auditoriared`, `templates/auditoriared`, `templates/layouts/base_auditoriared.html`, `static/css/auditoriared*.css`, `static/js/auditoriared*.js` y Leaflet. Use **una** copia de las clases, no fuente y JAR simultáneamente.
2. Mantenga el escaneo de componentes bajo `com.tracker.web`. El host debe aportar Spring MVC, Thymeleaf, JDBC, Spring Security, un `JdbcTemplate` configurado y su autenticación existente.
3. Proteja `/auditoria-red` y `/auditoria-red/**` con `hasAnyRole("USER", "ADMIN")`. Los controladores además verifican autenticación y rol. Mantenga CSRF del host; los POST envían el token de Thymeleaf cuando existe.
4. Para el modo sitio, mantenga `app.jefaturas` del host. `AccesoJefaturaAuditoria` incorpora la política del módulo para no importar clases Java de Recursos. Un usuario sin mapeo queda rechazado; `ROLE_ADMIN` conserva el acceso global. La política se debe mantener consistente con Recursos si cambia allí.
5. El enlace de Recursos a la auditoría es opcional. `auditoriared_enlace_asignar.js` se conserva como adaptador; esta distribución no modifica ninguna pantalla ni algoritmo de asignación de Recursos.
6. Compile y pruebe con **Java 8**, que incluye Nashorn. Se verificó el recorrido real contra Oracle local USERTEST/FREEPDB1 usando un fixture mínimo sintético. El esquema, permisos y capacidad corporativos requieren validación propia. Mantenga los datasources ajenos al módulo.

También puede instalar el módulo con `mvnw.cmd install` y añadir `com.tracker:auditoria-red:1.0.0-SNAPSHOT` al host, retirando antes las clases/recursos duplicados del host. La integración por copia es la opción comprobada en el Web Tracker migrado.

Configuración opcional (valores por defecto):

```properties
auditoriared.radio-reuso-m=10000
auditoriared.radio-max-km=20
auditoriared.max-celdas=8000
auditoriared.max-filas-leidas=100000
auditoriared.tope-hallazgos=8000
auditoriared.cache-minutos=15
auditoriared.motor-4g=scripts/auditoriared/planner_4g_2_MIRROR_FIXED.js
auditoriared.motor-5g=scripts/auditoriared/old_planner_5g.js
# Ejemplo ficticio; conservar el mapeo autorizado real del host:
# app.jefaturas=usuario_ejemplo:ZONA LIMA
```

`cache-minutos=0` desactiva la caché. Los límites se deben ajustar con mediciones reales; un límite no convierte el resultado parcial en una certificación de ausencia de conflictos.

## Datos y alcance de los indicadores

Fuentes actuales: `tm_conf_actual_celda_4g` + `PLAN_MAP_SECT_CRIT_4GWEEK`, y `tm_conf_actual_celda_5g` + `vw_plani_recursos_5g_ta_diario`. El modo sitio consulta `RECURSOS_FORMULARIO` únicamente por los IDs propuestos y verifica jefatura.

**No se añaden automáticamente todas las reservas guardadas en Recursos al universo de red.** El modo sitio usa exclusivamente IDs autorizados y evalúa su PCI propuesto como escenario. La identidad oficial es tecnología + nombre normalizado. Duplicados equivalentes consolidan con representación determinista; una identidad con configuraciones contradictorias se excluye completa y se informa, porque las fuentes actuales no permiten elegir una versión autoritativa por fecha.

Los dos planners de referencia se conservan sin cambios. El adaptador aplica `auditoria_modelo.js`, propio de Auditoría Red, para corregir límites de vecinas, TA y excepciones sin evidencia. La versión muestra huellas del planner, modelo y geometría. La referencia `meg` no permite acreditar el JavaScript actualmente desplegado en producción.

- Colisión PCI potencial: PCI igual dentro del radio de reuso y compatibilidad de canal explícito, o grupo candidato carrier/banda. Se recorren todos los pares cargados, sin el anterior recorte nearest300/600. Requiere verificar frecuencia y cobertura.
- La regla RSI utiliza `TABLA_RSI` del motor derivada de PCI. No consulta una configuración PRACH/RSI independiente ni acredita su valor real en red.
- `PCI_MOD3`, **Riesgo PSS (mod-3)**: PCI distintos de igual mod-3, entre las 30 más cercanas de al menos una celda y dentro de radio/cono estimados de ambas. La confusión servidora/dos vecinas no se evalúa: faltan relaciones y frecuencia verificadas.
- Solapamiento estimado: área de intersección de los sectores del motor, en m². El índice normalizado es área/distancia, en metros (m²/m), no un porcentaje, potencia ni probabilidad. Se conserva la política de exclusión de cositios del motor para el índice.
- TA: ocho bins completos; suma 0,99–1,01 en fracción o 99–101 en porcentaje, sin negativos/no finitos y normalización exacta. P80 toma el primer límite superior que acumula ≥80 %. El intervalo abierto final da radio `null`. Sin TA válido se usa la distancia media a hasta 30 vecinas compatibles a más de 1 m; sin ninguna, radio desconocido. Es escala de densidad del contexto, no cobertura medida. Se muestran estado, fuente y cantidad de vecinas.
- Las vecinas y el respaldo de TA se limitan al contexto cargado. No representan toda la red ni un modelo de propagación. El margen no garantiza todas las interacciones geométricas posibles de sectores de gran alcance.
- El tope es por tipo y tecnología; se marca incompleto si existió un hallazgo adicional omitido. El subconjunto es determinista y no garantiza conservar los pares globalmente más cercanos. El aviso persiste con filtros vacíos; los conteos no representan toda la red.

Las coordenadas usan CASE, validación de formato y NLS explícito; comas y datos sucios se probaron en Oracle 23.26.3. Las filas no localizables quedan fuera antes del contador leído y no pueden contarse dentro del área. No se midió un plan de ejecución corporativo. La caché muestra fecha/edad del cálculo en America/Lima; la antigüedad de las fuentes y del TA no está disponible.

## Oracle local

El harness vive sólo en `src/test`, fuera del JAR principal. Crea cinco tablas y una vista mínima cuando sus nombres están libres, verifica catálogos/59 columnas/PK/índices y sólo reutiliza objetos con su marca sintética. Los SQL versionados están en `src/test/resources/oracle`; no son DDL productivo. Se preservan los objetos preexistentes de USERTEST y no se usa Hibernate DDL.

```powershell
.\mvnw.cmd -B test-compile dependency:build-classpath '-Dmdep.outputFile=target/test-classpath.txt'
powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\Run-OracleAudit.ps1 -Mode all
```

Ingrese la contraseña sólo en la ventana local enmascarada. Viaja por stdin privado, sin archivos, argumentos ni variables de entorno. El driver `ojdbc8.jar` instalado se agrega sólo al classpath local. El [dashboard de prueba](http://127.0.0.1:3299/tracker/auditoria-red/dashboard) escucha localhost, usa identidad sintética/CSRF y caché cero. Busque `AUDRED`, o use −12,09 / −77,05 y radio 1 km.

Decisiones y resultados: [registro principal](DECISIONES-AUDITORIA-RED.md), [detalle Oracle](DECISIONES-ORACLE.md), [verificación](VERIFICACION.md). `scripts/Sync-AuditoriaHost.ps1 -BackupRoot <directorio nuevo externo al host>` integra sólo archivos de Auditoría con respaldo y hashes; conserva el POM del host.

## Extender filtros

Consulte [EXTENDER-FILTROS.md](EXTENDER-FILTROS.md). Los filtros son beans registrados; el cliente genera controles desde los metadatos. Agregar un campo con operador existente no exige cambiar el controlador ni el JavaScript del panel.

## Verificación

```powershell
# JAVA_HOME debe apuntar a Java 8
.\mvnw.cmd -B test
.\mvnw.cmd -B package
```

La verificación de entrega y el ensayo de navegador están descritos en [VERIFICACION.md](VERIFICACION.md). El servidor sintético vive exclusivamente en `src/test` y no se empaqueta en el JAR de producción.

Leaflet 1.9.4 conserva su [licencia BSD](src/main/resources/static/js/leaflet/LICENSE). Los mosaicos de OpenStreetMap requieren conectividad y su atribución se mantiene visible. Para un despliegue con otro proveedor, configure un servicio de mapas autorizado y su atribución correspondiente.
