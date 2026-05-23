package com.tracker.web.sbsecurity.config;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        Collection<? extends GrantedAuthority> authorities =
                authentication.getAuthorities();

        for (GrantedAuthority authority : authorities) {

            // ✅ CONSULTOR → DIRECT TO LIBRARY
            if (authority.getAuthority().equals("ROLE_CONSULTOR")) {
                response.sendRedirect(request.getContextPath()
                        + "/archivador/listar-categorias");
                return;
            }
        }

        // ✅ DEFAULT (USER / ADMIN)
        response.sendRedirect(request.getContextPath() + "/home");
    }
}	