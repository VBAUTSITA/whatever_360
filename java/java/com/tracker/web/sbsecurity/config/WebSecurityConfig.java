package com.tracker.web.sbsecurity.config;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.tracker.web.sbsecurity.config.CustomLoginSuccessHandler;

import com.tracker.web.sbsecurity.service.UserDetailsServiceImpl;
 


@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
 
    @Autowired
    UserDetailsServiceImpl userDetailsService;
    @Autowired
    CustomLoginSuccessHandler customLoginSuccessHandler;
    
 
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder;
    }
     
     
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception { 
 
        // Setting Service to find User in the database.
        // And Setting PassswordEncoder
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());     
 
    }
 
    @Override
    protected void configure(HttpSecurity http) throws Exception {
 
        http.csrf().disable();
 
        // The pages does not require login
        http.authorizeRequests().antMatchers("/seguimiento/addaccount", "/seguimiento/recuperarcuenta", "/", "/login", "/logout", "/add-account", "/account" , "/created").permitAll();
 
        // /userInfo page requires login as ROLE_USER or ROLE_ADMIN.
        // If no login, it will redirect to /login page.
        http.authorizeRequests().antMatchers("/homefile","/upload", "/home", "/panelUser", "/interferencia/list/**","/seguimiento/list", "/seguimiento/list_hist", "/seguimiento/add", "/seguimiento/hist/id/**", "/seguimiento/hist/**", "/mapa/**").access("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')");
 
        // For ADMIN only.
        http.authorizeRequests().antMatchers("/admin","/mapas/listar-admin","/baseline-admin","/archivador/descargar/581/**","/archivador/descargar/sol_privada-1/**",
        		"/archivador/descargar/sol_privada-2/**","/archivador/descargar/sol_privada-3/**","/archivador/descargar/sol_privada-4/**",
        		"/archivador/descargar/sol_privada-5/**","/archivador/descargar/sol_privada-6/**","/archivador/descargar/sol_privada-7/**").access("hasRole('ROLE_ADMIN')");
        
        //NUEVA SECCION
        
        // ⛔ UPLOAD: blocked for CONSULTOR — USER/ADMIN only
        http.authorizeRequests().antMatchers(
            "/archivador/subir",
            "/archivador-massive/upload",
            "/archivador-massive/upload-alt",
            "/archivador/listar",        // the full admin list view
            "/archivador/eliminar/**"    // delete if you have it
        ).access("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')");
     
        // ✅ CONSULTOR: only these two paths allowed
        http.authorizeRequests().antMatchers(
            "/archivador/listar-categorias",
            "/archivador/descargar/**",
            "/archivador/libreria/*/comentarios"   // ✅ ADD THIS
        ).access("hasAnyRole('ROLE_CONSULTOR', 'ROLE_USER', 'ROLE_ADMIN')");
     
        // ⛔ CATCH-ALL: anything not matched above → deny everyone not authenticated
        http.authorizeRequests().anyRequest().authenticated();
     
        
        
        //FIN NUEVA SECCION
        
        
        // When the user has logged in as XX.
        // But access a page that requires role YY,
        // AccessDeniedException will be thrown.
        http.authorizeRequests().and().exceptionHandling().accessDeniedPage("/403");
 
        // Config for Login Form ....
        http.authorizeRequests().and().formLogin()//
                // Submit URL of login page.
                .loginProcessingUrl("/j_spring_security_check") // Submit URL
                .loginPage("/login")//
                .successHandler(customLoginSuccessHandler)
                //.defaultSuccessUrl("/home")//
                .failureUrl("/login?error=true")//
                .usernameParameter("username")//
                .passwordParameter("password")
                // Config for Logout Page
                .and().logout().logoutUrl("/logout").logoutSuccessUrl("/login");
 
    }
    
    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers(
            "/css/**",
            "/js/**",
            "/images/**",
            "/fonts/**",
            "/webjars/**"
        );
    }
}