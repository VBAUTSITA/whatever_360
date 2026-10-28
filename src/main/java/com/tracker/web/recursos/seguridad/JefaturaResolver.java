package com.tracker.web.recursos.seguridad;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Unico punto del sistema que decide a que jefatura esta limitado el usuario
 * actual. NUNCA lee nada del request: la jefatura sale del usuario autenticado
 * y del mapeo declarado en application.properties.
 *
 * Formato esperado en application.properties (una sola linea):
 *
 *   app.jefaturas=C14563:ZONA LIMA,C27342:ZONA NORTE,C30011:ZONA SUR
 *
 * Se usa una sola propiedad con @Value en vez de un Map con
 * @ConfigurationProperties a proposito: funciona igual en Spring "pelado" que
 * en Spring Boot, y evita el relaxed binding, que puede pasar las claves a
 * minusculas y romper el lookup del username silenciosamente.
 *
 * REGLA DE ACCESO: estar listado en app.jefaturas es lo que habilita a asignar.
 * Un usuario autenticado que no aparezca aqui (y que no sea ROLE_ADMIN) recibe
 * AccessDenied. Falla CERRADO: si falta un mapeo, la persona avisa en 5 minutos;
 * si fallara abierto, tendria acceso a todas las zonas y nadie se enteraria.
 */
@Component
public class JefaturaResolver {

	private static final Logger log = LoggerFactory.getLogger(JefaturaResolver.class);

	/** Valor crudo de application.properties. Vacio por defecto. */
	@Value("${app.jefaturas:}")
	private String jefaturasRaw;

	/** username (tal cual se declaro) -> jefatura */
	private final Map<String, String> jefaturaPorUsuario = new HashMap<String, String>();

	@PostConstruct
	public void init() {
		jefaturaPorUsuario.clear();
		if (jefaturasRaw == null || jefaturasRaw.trim().isEmpty()) {
			log.warn("app.jefaturas esta vacio: NINGUN usuario podra asignar "
					+ "salvo los que tengan ROLE_ADMIN.");
			return;
		}
		String[] pares = jefaturasRaw.split(",");
		for (int i = 0; i < pares.length; i++) {
			String par = pares[i].trim();
			if (par.isEmpty()) {
				continue;
			}
			int sep = par.indexOf(':');
			if (sep <= 0 || sep == par.length() - 1) {
				log.error("Entrada mal formada en app.jefaturas (se esperaba "
						+ "usuario:JEFATURA): '{}'. Se ignora.", par);
				continue;
			}
			String usuario = par.substring(0, sep).trim();
			String jefatura = par.substring(sep + 1).trim();
			String previo = jefaturaPorUsuario.put(usuario, jefatura);
			if (previo != null) {
				log.error("Usuario '{}' declarado dos veces en app.jefaturas "
						+ "('{}' y '{}'). Prevalece la ultima.", usuario, previo, jefatura);
			}
		}
		log.info("Filtro por jefatura activo para {} usuario(s): {}",
				jefaturaPorUsuario.size(), jefaturaPorUsuario.keySet());
	}

	/**
	 * Jefatura a la que esta limitado el usuario actual.
	 *
	 * @return el nombre de la jefatura, o {@code null} si NO tiene limite
	 *         (unicamente ROLE_ADMIN).
	 * @throws AccessDeniedException si no hay usuario autenticado, o si el
	 *         usuario no es ADMIN y no esta declarado en app.jefaturas.
	 */
	public String jefaturaActual() {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		if (a == null || a.getName() == null) {
			throw new AccessDeniedException("Sin usuario autenticado");
		}

		// ADMIN por ROL, no por mapeo: no hay que declararlo en el properties.
		for (GrantedAuthority g : a.getAuthorities()) {
			if ("ROLE_ADMIN".equals(g.getAuthority())) {
				return null;
			}
		}

		String jefatura = buscarIgnorandoCase(a.getName());
		if (jefatura == null) {
			log.warn("Usuario '{}' no declarado en app.jefaturas: acceso denegado "
					+ "al modulo de asignacion PCI/RSI.", a.getName());
			throw new AccessDeniedException("Usuario sin jefatura asignada: " + a.getName());
		}
		return jefatura;
	}

	/**
	 * El username que devuelve Spring Security debe coincidir con APP_USER.USER_NAME,
	 * pero la comparacion se hace tolerante a mayusculas para que un typo en el
	 * properties no cause un fallo silencioso dificil de rastrear.
	 */
	private String buscarIgnorandoCase(String usuario) {
		String exacto = jefaturaPorUsuario.get(usuario);
		if (exacto != null) {
			return exacto;
		}
		for (Map.Entry<String, String> e : jefaturaPorUsuario.entrySet()) {
			if (e.getKey().equalsIgnoreCase(usuario)) {
				return e.getValue();
			}
		}
		return null;
	}

	/**
	 * Compara la jefatura de una fila contra la del usuario.
	 *
	 * RECURSOS_FORMULARIO.JEFATURA es texto libre y puede traer variantes de
	 * mayusculas o espacios; por eso se compara con trim() + equalsIgnoreCase()
	 * y no con equals(). Es el mismo problema que partia el pool de carriers
	 * "C1" vs "c1" en tm_conf_actual_celda_3g.
	 *
	 * @param jefaturaFila    valor de la fila; null = fila huerfana
	 * @param jefaturaUsuario null = sin limite (ADMIN), devuelve siempre true
	 */
	public boolean coincide(String jefaturaFila, String jefaturaUsuario) {
		if (jefaturaUsuario == null) {
			return true; // ADMIN / sin limite
		}
		if (jefaturaFila == null) {
			return false; // fila sin jefatura: solo la ve el ADMIN
		}
		return jefaturaFila.trim().equalsIgnoreCase(jefaturaUsuario.trim());
	}
}
