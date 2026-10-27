package com.tracker.web.auditoriared;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/** Defense in depth: host URL rules are not the only protection for this read-only module. */
public final class SeguridadAuditoria {
    private SeguridadAuditoria() { }

    public static void exigirLectura() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (a == null || !a.isAuthenticated() || a instanceof AnonymousAuthenticationToken) {
            throw new AccessDeniedException("Inicie sesion para consultar la auditoria");
        }
        boolean permitido = a.getAuthorities().stream().anyMatch(g ->
                "ROLE_USER".equals(g.getAuthority()) || "ROLE_ADMIN".equals(g.getAuthority()));
        if (!permitido) throw new AccessDeniedException("Su usuario no tiene acceso a la auditoria");
    }
}
