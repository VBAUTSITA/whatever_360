package com.tracker.web.recursos.planner;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tracker.web.recursos.planner.dto.CeldaExistenteDTO;
import com.tracker.web.recursos.planner.dto.CeldaPlanDTO;

/**
 * Lee la red 4G ya desplegada uniendo tm_conf_actual_celda_4g (parametros de
 * radio: azimuth/carrier/etc, todo String en esa tabla) con
 * PLAN_MAP_SECT_CRIT_4GWEEK (trafico TA). La clave de join (sector_name) y el
 * formato de las columnas TA (0-100 vs 0-1) NO estan confirmados contra datos
 * reales (no hay acceso a la BD Oracle real desde el entorno donde se escribio
 * esto) - ver normalizarTA y el plan en C:\Users\k_ale\.claude\plans\.
 *
 * Acota la red existente a una caja geografica alrededor de las celdas a
 * planificar en vez de traer siempre la red nacional completa (~90 mil
 * celdas segun Algoritmo_PCI_RSI.md) - ver RADIO_SEGURIDAD_KM.
 */
@Service
public class RedExistenteServiceImpl implements RedExistenteService {

	private static final Logger log = LoggerFactory.getLogger(RedExistenteServiceImpl.class);

	private static final String SQL_RED_EXISTENTE_BASE =
			"SELECT r.cellname, r.carrier, r.phycellid, r.freqband, r.txrxmode, "
			+ "       r.latitud, r.longitud, r.azimuth, "
			+ "       m.ta_0_234_mts, m.ta_234_546_mts, m.ta_546_1014_mts, m.ta_1014_1950_mts, "
			+ "       m.ta_1950_3510_mts, m.ta_3510_6630_mts, m.ta_6630_14430_mts, m.ta_mayor_14430_mts "
			+ "FROM tm_conf_actual_celda_4g r "
			+ "LEFT JOIN PLAN_MAP_SECT_CRIT_4GWEEK m ON r.sector_name = m.sector_name";

	/**
	 * Pre-filtro SQL opcional por caja combinada (superset de todas las cajas
	 * individuales de construirCajas, asi que nunca excluye nada que la caja
	 * por celda no excluyera ya). r.latitud/r.longitud SIGUEN siendo
	 * VARCHAR2 - no se cambia el tipo de columna (afecta a Reporte01Controller
	 * y otros lectores de tm_conf_actual_celda_4g que no se auditaron para
	 * ese cambio). REGEXP_LIKE es defensivo (Oracle no garantiza el orden de
	 * evaluacion de un AND, asi que no es una garantia absoluta contra datos
	 * sucios) - confirmado con el usuario que latitud/longitud son confiables
	 * hoy. TO_NUMBER sin mascara de formato: el driver thin de Oracle (ojdbc6,
	 * sin NLS_LANG configurado en application.properties) usa por defecto '.'
	 * como separador decimal, igual que el formato que ya muestran estos datos
	 * (ej. "-77.0428"); no se fuerza un NLS_NUMERIC_CHARACTERS explicito para
	 * no introducir una mascara de formato que no se puede probar aqui.
	 */
	private static final String FILTRO_CAJA_SQL =
			" WHERE REGEXP_LIKE(r.latitud, '^\\s*-?[0-9]+\\.?[0-9]*\\s*$') "
			+ "   AND REGEXP_LIKE(r.longitud, '^\\s*-?[0-9]+\\.?[0-9]*\\s*$') "
			+ "   AND TO_NUMBER(r.latitud) BETWEEN :minLat AND :maxLat "
			+ "   AND TO_NUMBER(r.longitud) BETWEEN :minLon AND :maxLon";

	/**
	 * planner.js (PoolCarrier._cell = 0.02 grados ~2.2 km y el bucle
	 * `while (ring <= 60)` de consulta(), verificados en planner.js) nunca
	 * considera una celda existente a mas de ring(60) * cell(0.02 deg) ~= 1.2
	 * grados (~133 km) del punto consultado, sin importar cuan dispersa sea la
	 * red de ese carrier/banda - es el limite duro del propio motor, no una
	 * suposicion. 150 km deja ~13% de margen sobre ese limite verificado:
	 * filtrar aqui no puede cambiar el resultado de planner.js, solo evita
	 * serializar y parsear en Nashorn decenas de miles de celdas que el motor
	 * jamas iba a mirar. Si en el futuro suben ring<=60, _cell, radio_vecindad_m
	 * o max_vecindad en planner.js, este numero debe revisarse junto con eso.
	 */
	private static final double RADIO_SEGURIDAD_KM = 150.0;
	private static final double KM_POR_GRADO_LAT = 111.32;

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public ResultadoRedExistente obtenerCeldasExistentes(List<CeldaPlanDTO> aPlanificar) {
		List<double[]> cajas = construirCajas(aPlanificar);
		double[] cajaCombinada = construirCajaCombinada(cajas);

		List<Object[]> filas;
		if (cajaCombinada != null) {
			Query query = entityManager.createNativeQuery(SQL_RED_EXISTENTE_BASE + FILTRO_CAJA_SQL);
			query.setParameter("minLat", cajaCombinada[0]);
			query.setParameter("maxLat", cajaCombinada[1]);
			query.setParameter("minLon", cajaCombinada[2]);
			query.setParameter("maxLon", cajaCombinada[3]);
			filas = query.getResultList();
		} else {
			filas = entityManager.createNativeQuery(SQL_RED_EXISTENTE_BASE).getResultList();
		}

		List<CeldaExistenteDTO> celdas = new ArrayList<>();
		int conDatosTrafico = 0;
		int fueraDeCaja = 0;

		for (Object[] fila : filas) {
			Double latitud = parseDouble(fila[5]);
			Double longitud = parseDouble(fila[6]);
			Double azimuth = parseDouble(fila[7]);
			Integer phycellid = parseInteger(fila[2]);
			// Sin coordenadas/azimuth utilizables no se puede construir el wedge de
			// esta celda: mejor excluirla que dejar que Number("") => 0 la convierta
			// en una celda "real" parada en (0,0). Lo mismo con el PCI: una celda
			// "existente" sin Phycellid se convertiria en Number(null) => 0 dentro
			// de planner.js (PSS 0), contaminando esa bolsa de interferencia con un
			// dato falso en vez de quedar simplemente afuera de la comparacion.
			if (latitud == null || longitud == null || azimuth == null || phycellid == null) {
				continue;
			}

			if (!cajas.isEmpty() && !dentroDeAlgunaCaja(latitud, longitud, cajas)) {
				fueraDeCaja++;
				continue;
			}

			CeldaExistenteDTO dto = new CeldaExistenteDTO();
			dto.setCellname((String) fila[0]);
			dto.setCarrier((String) fila[1]);
			dto.setPhycellid(phycellid);
			dto.setFreqband((String) fila[3]);
			dto.setTxrxmode((String) fila[4]);
			dto.setLatitud(latitud);
			dto.setLongitud(longitud);
			dto.setAzimuth(azimuth);

			double[] taCrudo = new double[] {
					parseDoubleOrZero(fila[8]), parseDoubleOrZero(fila[9]),
					parseDoubleOrZero(fila[10]), parseDoubleOrZero(fila[11]),
					parseDoubleOrZero(fila[12]), parseDoubleOrZero(fila[13]),
					parseDoubleOrZero(fila[14]), parseDoubleOrZero(fila[15])
			};
			double[] taFraccion = normalizarTA(taCrudo);
			dto.setTa0234(taFraccion[0]);
			dto.setTa234546(taFraccion[1]);
			dto.setTa5461014(taFraccion[2]);
			dto.setTa10141950(taFraccion[3]);
			dto.setTa19503510(taFraccion[4]);
			dto.setTa35106630(taFraccion[5]);
			dto.setTa663014430(taFraccion[6]);
			dto.setTaMayorA14430(taFraccion[7]);

			double sumaCrudo = 0;
			for (double v : taCrudo) {
				sumaCrudo += v;
			}
			if (sumaCrudo > 1e-9) {
				conDatosTrafico++;
			}

			celdas.add(dto);
		}

		log.info("Red existente: {} filas crudas, {} fuera de la caja de {} km, {} usables ({} con trafico)",
				filas.size(), fueraDeCaja, (int) RADIO_SEGURIDAD_KM, celdas.size(), conDatosTrafico);

		return new ResultadoRedExistente(celdas, celdas.size(), conDatosTrafico);
	}

	/** Una caja [minLat, maxLat, minLon, maxLon] por cada celda a planificar. */
	private static List<double[]> construirCajas(List<CeldaPlanDTO> aPlanificar) {
		List<double[]> cajas = new ArrayList<>();
		if (aPlanificar == null) {
			return cajas;
		}
		for (CeldaPlanDTO p : aPlanificar) {
			if (p.getLatitud() == null || p.getLongitud() == null) {
				continue;
			}
			double lat = p.getLatitud();
			double lon = p.getLongitud();
			double margenLat = RADIO_SEGURIDAD_KM / KM_POR_GRADO_LAT;
			// Un grado de longitud cubre menos km mientras mas lejos del ecuador
			// (factor cos(lat)): dividir por ese factor agranda el margen en
			// grados para seguir cubriendo los mismos km reales en longitud.
			// El piso de 0.05 es solo defensivo (lat invalida cerca de +-90),
			// irrelevante en la practica para coordenadas de Peru.
			double margenLon = RADIO_SEGURIDAD_KM / (KM_POR_GRADO_LAT * Math.max(0.05, Math.cos(Math.toRadians(lat))));
			cajas.add(new double[] { lat - margenLat, lat + margenLat, lon - margenLon, lon + margenLon });
		}
		return cajas;
	}

	/**
	 * Caja [minLat, maxLat, minLon, maxLon] que envuelve TODAS las cajas
	 * individuales - se usa solo como pre-filtro SQL (mas barato traer menos
	 * filas), nunca como el filtro final: dentroDeAlgunaCaja despues sigue
	 * siendo el que decide con precision por celda.
	 */
	private static double[] construirCajaCombinada(List<double[]> cajas) {
		if (cajas.isEmpty()) {
			return null;
		}
		double minLat = Double.POSITIVE_INFINITY, maxLat = Double.NEGATIVE_INFINITY;
		double minLon = Double.POSITIVE_INFINITY, maxLon = Double.NEGATIVE_INFINITY;
		for (double[] c : cajas) {
			minLat = Math.min(minLat, c[0]);
			maxLat = Math.max(maxLat, c[1]);
			minLon = Math.min(minLon, c[2]);
			maxLon = Math.max(maxLon, c[3]);
		}
		return new double[] { minLat, maxLat, minLon, maxLon };
	}

	private static boolean dentroDeAlgunaCaja(double lat, double lon, List<double[]> cajas) {
		for (double[] c : cajas) {
			if (lat >= c[0] && lat <= c[1] && lon >= c[2] && lon <= c[3]) {
				return true;
			}
		}
		return false;
	}

	/**
	 * planner.js espera 8 fracciones que suman ~1.0 (cascada del paso 6 con
	 * umbral 0.80). No esta confirmado si PLAN_MAP_SECT_CRIT_4GWEEK guarda estas
	 * columnas como 0-100 ("23.5") o ya como 0-1 ("0.235") - la UI del modulo
	 * mapa solo les pone un sufijo "(%)" sin transformarlas, lo cual es
	 * consistente con cualquiera de las dos convenciones. Se detecta por la
	 * suma de las 8 en vez de asumir: si suma ~1 ya son fracciones, si suma
	 * ~100 se divide por 100, y si no cae en ninguno de los dos rangos se trata
	 * como "sin datos de trafico" (planner.js ya tiene un fallback para eso:
	 * usa el promedio de las 30 vecinas mas cercanas en vez de la cascada TA).
	 */
	static double[] normalizarTA(double[] crudo) {
		double suma = 0;
		for (double v : crudo) {
			suma += v;
		}
		double factor;
		if (suma >= 0.5 && suma <= 1.5) {
			factor = 1.0;
		} else if (suma >= 50 && suma <= 150) {
			factor = 0.01;
		} else {
			return new double[8]; // sin datos de trafico => fallback de promedioCercanas
		}
		double[] out = new double[8];
		for (int i = 0; i < 8; i++) {
			out[i] = crudo[i] * factor;
		}
		return out;
	}

	private static Double parseDouble(Object raw) {
		if (raw == null) {
			return null;
		}
		try {
			return Double.parseDouble(String.valueOf(raw).trim());
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private static double parseDoubleOrZero(Object raw) {
		Double v = parseDouble(raw);
		return v == null ? 0.0 : v;
	}

	private static Integer parseInteger(Object raw) {
		Double v = parseDouble(raw);
		return v == null ? null : (int) Math.round(v);
	}
}
