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
