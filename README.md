# Auditoría de red y panel PCI

Distribución exclusiva del módulo Auditoría Red de Web Tracker, Java 8 / Spring Boot 2.1.7. Rama `feature/auditoria-red-dashboard` destinada a `VBAUTSITA/whatever_360`. Es una rama independiente para distribuir el módulo: no incluye los demás módulos ni comparte el árbol completo de `main`. No debe fusionarse mediante una sustitución del proyecto.

## Uso

- `/auditoria-red/dashboard`: panel de hallazgos, filtros, paginación, vecinas de una celda y mapa de la relación seleccionada.
- `/auditoria-red`: auditoría de un área circular con margen y exportación CSV.
- `/auditoria-red/sitio?ids=12,13&pci=100,101`: auditoría de PCIs propuestos para registros autorizados de un solo sitio de Recursos. Los números del ejemplo son ficticios.

El panel permite buscar un sitio/celda o introducir coordenadas. El área actual es un círculo; no interpreta nombres como polígonos de clúster ni inventa una tabla de clústeres. Los filtros de hallazgos se aplican después del cálculo. No eliminan celdas del contexto que recibe el motor.

El mapa resalta un par seleccionado. La opción Contexto añade como máximo 200 marcadores del área, sin pintar toda la ciudad con sectores superpuestos. Las vecinas se obtienen del contexto completo cargado, con el mismo carrier/banda, aunque no tengan hallazgos. Puede consultar entre 1 y 200, con 30 por defecto.

## Integración en Web Tracker

Este proyecto genera un **JAR de módulo**, no un servidor de producción autónomo. No contiene `application.properties`, credenciales, login propio ni configuración Oracle. No sustituya el `pom.xml` de Web Tracker por este POM.

1. Trabaje sobre una copia/commit de respaldo del Web Tracker completo. Compare e integre `src/main/java/com/tracker/web/auditoriared` y los recursos `scripts/auditoriared`, `templates/auditoriared`, `templates/layouts/base_auditoriared.html`, `static/css/auditoriared*.css`, `static/js/auditoriared*.js` y Leaflet. Use **una** copia de las clases, no fuente y JAR simultáneamente.
2. Mantenga el escaneo de componentes bajo `com.tracker.web`. El host debe aportar Spring MVC, Thymeleaf, JDBC, Spring Security, un `JdbcTemplate` configurado y su autenticación existente.
3. Proteja `/auditoria-red` y `/auditoria-red/**` con `hasAnyRole("USER", "ADMIN")`. Los controladores además verifican autenticación y rol. Mantenga CSRF del host; los POST envían el token de Thymeleaf cuando existe.
4. Para el modo sitio, mantenga `app.jefaturas` del host. `AccesoJefaturaAuditoria` incorpora la política del módulo para no importar clases Java de Recursos. Un usuario sin mapeo queda rechazado; `ROLE_ADMIN` conserva el acceso global. La política se debe mantener consistente con Recursos si cambia allí.
5. El enlace de Recursos a la auditoría es opcional. `auditoriared_enlace_asignar.js` se conserva como adaptador; esta distribución no modifica ninguna pantalla ni algoritmo de asignación de Recursos.
6. Compile y pruebe con **Java 8**, que incluye Nashorn. Verifique en su entorno Oracle las consultas, permisos de solo lectura, tipos de columnas y tiempos de respuesta antes de desplegar. No se ejecutaron consultas ni DDL en Oracle durante esta entrega.

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

**Esta entrega no añade automáticamente todas las reservas guardadas en Recursos al universo de red.** No crea vistas, tablas ni migraciones SQL. Ese cambio de asignación se discutió anteriormente y requiere su propia integración con prioridad del reporte oficial y deduplicación.

Se mantienen los motores incluidos en la copia migrada. La referencia `meg` contiene Java, pero no permite acreditar cuál es el JavaScript actualmente desplegado. La interfaz informa archivo y huella SHA-256 abreviada del motor utilizado. Las reglas originales de PCI/RSI no se reescriben en el dashboard.

- Colisión PCI: regla de coincidencia de PCI del motor dentro del radio de reuso; no confirma una medición RF.
- La regla RSI utiliza `TABLA_RSI` del motor derivada de PCI. No consulta una configuración PRACH/RSI independiente ni acredita su valor real en red.
- `PCI_MOD3` se muestra como **Riesgo PSS (mod-3)**. No equivale a implementar la confusión PCI de una celda servidora con dos vecinas que comparten el mismo PCI.
- Solapamiento estimado: área de intersección de los sectores del motor, en m². El índice normalizado es área/distancia, en metros (m²/m), no un porcentaje, potencia ni probabilidad. Se conserva la política de exclusión de cositios del motor para el índice.
- La geometría proviene del TA o del respaldo del motor para celdas sin TA. La falta de datos se identifica; una métrica no disponible se transmite como `null`.
- Las vecinas y el respaldo de TA se limitan al contexto cargado. No representan toda la red ni un modelo de propagación. El margen no garantiza todas las interacciones geométricas posibles de sectores de gran alcance.
- Si se alcanza el tope de hallazgos, se muestra un resultado incompleto incluso cuando los filtros no dejan filas visibles. Los conteos nunca deben interpretarse como toda la red.

Las coordenadas textuales en SQL se convierten mediante `CASE`, validación de formato y NLS explícito. La conversión se basa en el [cortocircuito de CASE](https://docs.oracle.com/en/database/oracle/oracle-database/19/sqlrf/CASE-Expressions.html) y [TO_NUMBER con formato/NLS](https://docs.oracle.com/en/database/oracle/oracle-database/19/sqlrf/TO_NUMBER.html). Falta validación contra el esquema Oracle real, incluyendo su versión y el plan de ejecución.

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
