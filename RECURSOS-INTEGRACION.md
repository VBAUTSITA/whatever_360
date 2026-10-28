# Recursos: contenido e integración

Fecha: 2026-10-28. Esta rama incorpora una copia exacta del módulo Recursos de la aplicación activa al momento de la validación. No sustituye el módulo por la referencia `meg` ni por la aplicación `planner_suite`.

## Archivos incluidos

- `src/main/java/com/tracker/web/recursos`: 21 clases del controlador, entidad, repositorio, política de jefatura, servicios y DTOs de planificación.
- `src/main/resources/templates/recursos`: alta, listado, modificación y asignación.
- `src/main/resources/templates/layouts/base_recursos.html`.
- `src/main/resources/scripts/planner_4g.js` y `planner_5g.js`, sin aplicar la corrección diagnóstica.
- `VALIDACION-RECURSOS-5G.md` y `validacion/recursos-5g`: guía y checks controlados de Java 8/Nashorn.
- `PUBLICACION-MANIFIESTO.json`: origen relativo y SHA-256 de los archivos copiados.

## Dependencias del host

Se mantiene Java 8, Spring Boot 2.1.7, Spring MVC, Thymeleaf, Spring Data JPA y Spring Security. El POM del módulo agrega JPA para compilar la entidad/repositorio; no configura un datasource ni genera esquemas. El host debe aportar su configuración de conexión y esquema comprobado. No usar `ddl-auto=create/update` para integrar esta distribución.

Las pantallas conservan sus convenciones actuales. Requieren los layouts compartidos `layouts/base` y, en modificación, `layouts/base_reportes`, además de Bootstrap, jQuery/DataTables, `estilos.css` y otros recursos ya aportados por Web Tracker. Esta rama no reemplaza los archivos globales del host. La compilación Java no demuestra que esos layouts/assets estén presentes en otro despliegue.

El modelo JPA utiliza `RECURSOS_FORMULARIO` y `RECURSOS_SEQ`; la lectura de red referencia `TM_CONF_ACTUAL_CELDA_4G/5G`, `PLAN_MAP_SECT_CRIT_4GWEEK` y `VW_PLANI_RECURSOS_5G_TA_DIARIO`. Comprobar DDL, columnas, permisos y tipos antes de ejecutar. Las tablas mínimas de prueba de Auditoría no representan la entidad completa Recursos.

## Estado que debe revisar quien integra

Recursos conserva defectos identificados en esta misma versión. La publicación no aplica cambios al algoritmo, permisos globales, conservación parcial o reservas. Ver el registro de validación antes de habilitar asignaciones. La configuración global de seguridad de la aplicación activa no se publica ni se incorpora como cambio lateral: las pruebas MVC locales usaron esa configuración y detectaron accesos permitidos que deben corregirse y probarse en el host.

No mezclar clases del módulo copiadas y el mismo JAR como segunda dependencia, para evitar duplicados. Conservar los cambios pendientes ajenos a estos módulos y validar la integración en una copia respaldada del host.
