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

import com.tracker.web.recursos.planner.dto.CeldaExistenteDTO5G;
import com.tracker.web.recursos.planner.dto.CeldaPlanDTO;

/**
 * ============================ MULTI-TECNOLOGIA ============================
 * Contraparte 5G de RedExistenteServiceImpl (4G): lee la red 5G NR ya
 * desplegada uniendo tm_conf_actual_celda_5g (parametros de radio) con
 * vw_plani_recursos_5g_ta_diario (trafico TA diario, clave = cellname).
 *
 * [CRITICO] El SELECT esta ordenado A PROPOSITO en el MISMO orden posicional
 * que el SELECT 4G (0=cellname, 1=carrier, 2=pci, 3=banda, 4=txrxmode,
 * 5=latitud, 6=longitud, 7=azimuth, 8..15=TA), aunque las columnas fisicas
 * se llamen distinto en la tabla 5G (physicalcellid, frequencyband). Asi el
 * bucle de mapeo fila[i] es identico al 4G y no hay riesgo de leer el PCI
 * donde estaba la banda (ese era el bug de la version anterior: el SELECT 5G
 * tenia 11 columnas delante de los TA y el bucle seguia usando los indices
 * del 4G, con lo que TODAS las celdas existentes llegaban corruptas al motor).
 * ==========================================================================
 */
@Service
public class RedExistenteServiceImpl5G implements RedExistenteService5G {

	private static final Logger log = LoggerFactory.getLogger(RedExistenteServiceImpl5G.class);

	// Mismo orden posicional que el SELECT de RedExistenteServiceImpl (4G):
	// las columnas fisicas 5G (physicalcellid/frequencyband) se proyectan en
	// las posiciones 2 y 3 igual que phycellid/freqband en 4G.
	private static final String SQL_RED_EXISTENTE_BASE =
			"SELECT a.cellname, a.carrier, a.physicalcellid, a.frequencyband, a.txrxmode, "
			+ "       a.latitud, a.longitud, a.azimuth, "
			+ "       b.ta_0_117_mts_porc, b.ta_117_507_mts_porc, "
			+ "       b.ta_507_975_mts_porc, b.ta_975_1775_mts_porc, "
			+ "       b.ta_1775_3315_mts_porc, b.ta_3315_7215_mts_porc, "
			+ "       b.ta_7215_15015_mts_porc, b.ta_mayor_a_15015_mts_porc "
			+ "FROM tm_conf_actual_celda_5g a "
			+ "LEFT JOIN vw_plani_recursos_5g_ta_diario b "
			+ "       ON UPPER(a.cellname) = UPPER(b.cellname)";

	/**
	 * Pre-filtro SQL por caja combinada (superset de todas las cajas
	 * individuales de construirCajas, asi que nunca excluye nada que la caja
	 * por celda no excluyera ya).
	 *
	 * [CRITICO - fix] El alias de la tabla 5G es "a" (la version anterior
	 * copiaba "r.latitud" del 4G y Oracle lanzaba ORA-00904 invalid identifier
	 * en CADA corrida con plan). A diferencia del 4G (latitud/longitud
	 * VARCHAR2, que exige REGEXP_LIKE + TO_NUMBER), en tm_conf_actual_celda_5g
	 * lat/lon se comparan directo — si en la BD real resultaran ser VARCHAR2
	 * con datos sucios, restaurar aqui el patron REGEXP_LIKE/TO_NUMBER del 4G.
	 */
	private static final String FILTRO_CAJA_SQL =
			" WHERE a.latitud BETWEEN :minLat AND :maxLat "
			+ "   AND a.longitud BETWEEN :minLon AND :maxLon";

	/**
	 * Mismo razonamiento que en 4G: planner_5g.js (PoolCarrier._cell = 0.02
	 * grados y el bucle `while (ring <= 60)` de consulta()) nunca considera
	 * una celda existente a mas de ~1.2 grados (~133 km) del punto consultado.
	 * 150 km deja margen sobre ese limite: filtrar aqui no puede cambiar el
	 * resultado del motor, solo evita serializar en Nashorn celdas que el
	 * motor jamas iba a mirar.
	 */
	private static final double RADIO_SEGURIDAD_KM = 150.0;
	private static final double KM_POR_GRADO_LAT = 111.32;

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public ResultadoRedExistente5G obtenerCeldasExistentes(List<CeldaPlanDTO> aPlanificar) {
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

		List<CeldaExistenteDTO5G> celdas = new ArrayList<>();
		int conDatosTrafico = 0;
		int fueraDeCaja = 0;

		for (Object[] fila : filas) {
			// Indices identicos al 4G porque el SELECT proyecta en el mismo orden.
			Double latitud = parseDouble(fila[5]);
			Double longitud = parseDouble(fila[6]);
			Double azimuth = parseDouble(fila[7]);
			Integer phycellid = parseInteger(fila[2]);
			// Sin coordenadas/azimuth utilizables no se puede construir el wedge de
			// esta celda: mejor excluirla que dejar que Number("") => 0 la convierta
			// en una celda "real" parada en (0,0). Lo mismo con el PCI.
			if (latitud == null || longitud == null || azimuth == null || phycellid == null) {
				continue;
			}

			if (!cajas.isEmpty() && !dentroDeAlgunaCaja(latitud, longitud, cajas)) {
				fueraDeCaja++;
				continue;
			}

			CeldaExistenteDTO5G dto = new CeldaExistenteDTO5G();
			dto.setCellname(String.valueOf(fila[0]));
			dto.setCarrier(fila[1] == null ? null : String.valueOf(fila[1]));
			dto.setPhycellid(phycellid);
			// La banda 5G puede ser alfanumerica ("N78") o numerica segun la
			// fila: String.valueOf tolera ambos tipos de columna.
			dto.setFreqband(fila[3] == null ? null : String.valueOf(fila[3]));
			dto.setTxrxmode(fila[4] == null ? null : String.valueOf(fila[4]));
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
			dto.setTa0117(taFraccion[0]);
			dto.setTa117507(taFraccion[1]);
			dto.setTa507975(taFraccion[2]);
			dto.setTa9751775(taFraccion[3]);
			dto.setTa17753315(taFraccion[4]);
			dto.setTa33157215(taFraccion[5]);
			dto.setTa721515015(taFraccion[6]);
			dto.setTaMayorA15015(taFraccion[7]);

			double sumaCrudo = 0;
			for (double v : taCrudo) {
				sumaCrudo += v;
			}
			if (sumaCrudo > 1e-9) {
				conDatosTrafico++;
			}

			celdas.add(dto);
		}

		log.info("Red existente 5G: {} filas crudas, {} fuera de la caja de {} km, {} usables ({} con trafico)",
				filas.size(), fueraDeCaja, (int) RADIO_SEGURIDAD_KM, celdas.size(), conDatosTrafico);

		return new ResultadoRedExistente5G(celdas, celdas.size(), conDatosTrafico);
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
	 * planner_5g.js espera 8 fracciones que suman ~1.0 (cascada del paso 6 con
	 * umbral 0.80). La vista diaria puede guardar porcentajes 0-100 o
	 * fracciones 0-1; se detecta por la suma de las 8 en vez de asumir. Si no
	 * cae en ninguno de los dos rangos se trata como "sin datos de trafico"
	 * (planner_5g.js usa el fallback de promedio de vecinas cercanas).
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
			String str = String.valueOf(raw).trim();
			// La vista TA "_porc" puede traer el simbolo % pegado al numero.
			if (str.endsWith("%")) {
				str = str.substring(0, str.length() - 1).trim();
			}
			return Double.parseDouble(str);
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
