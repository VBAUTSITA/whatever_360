# Extensión del panel

Los filtros de presentación se registran como beans de `FiltroDashboard`. `CatalogoFiltrosDashboard` rechaza identificadores repetidos o desconocidos, valida los valores antes de consultar la red y combina campos con AND; varios valores del mismo campo usan OR. No se concatenan filtros a SQL.

Para añadir otro campo con un operador existente:

```java
@Bean
public FiltroDashboard filtroPciObjetivo() {
    return new FiltroCampoDashboard("pci", "PCI", FiltroCampoDashboard.Tipo.SELECCION,
        h -> Arrays.asList(String.valueOf(h.getCeldaA().getPci()),
                           String.valueOf(h.getCeldaB().getPci())));
}
```

Este bean aparece en `/auditoria-red/dashboard/api/filtros` y en la respuesta de consulta. El navegador construye su control sin incorporar un `if` por nombre de campo. Las opciones se obtienen de los hallazgos completos antes de aplicar filtros. Los operadores disponibles son `SELECCION`, `TEXTO` y `MAXIMO`; la interfaz inicial permite un valor por campo y la API admite varios para `SELECCION`.

Un operador nuevo exige definir su validación y aceptación en el servidor y enseñar al cliente a representar ese **tipo** de control. Un filtro que use un nuevo dato de negocio requiere también cargar ese dato; no debe inferirse un clúster o una jefatura a partir del nombre de celda sin una fuente definida.

Ejemplo de consulta:

```json
{
  "latitud": -12.09, "longitud": -77.05, "radioKm": 3,
  "radioReusoKm": 10, "tecnologias": ["5G"],
  "filtros": {"banda": ["N78"], "distanciaMaxM": ["2000"]},
  "pagina": 1, "tamanoPagina": 25,
  "celdaObjetivo": "CELDA_EJEMPLO", "tecnologiaObjetivo": "5G",
  "limiteVecinas": 30
}
```

La celda objetivo debe pertenecer al área consultada. La lista de vecinas no se filtra mediante los filtros de hallazgos. Una banda o un texto que oculta una fila no puede alterar las reglas del motor.

Para soportar polígonos de clúster, implemente otro `Alcance`, con pertenencia exacta, caja con margen y clave de caché que incluya la geometría completa. Después añada el DTO de solicitud y un selector respaldado por una fuente de clústeres. La versión actual usa `ClusterAlcance` circular y no ofrece un selector de clústeres ficticio.
