package com.example.http_lab10.config;

import com.example.http_lab10.security.JwtAuthFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;


import java.nio.charset.StandardCharsets;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {

        http
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/auth/register",
                                "/auth/login",
                                "/auth/refresh",
                                "/auth/logout",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            log.warn("AUTHZ_401 unauthorized path={} ip={}",
                                    request.getRequestURI(),
                                    request.getRemoteAddr());

                            response.setStatus(401);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                            response.getWriter().write("{\"status\":401,\"error\":\"Unauthorized\",\"path\":\"" + request.getRequestURI() + "\"}");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            log.warn("AUTHZ_403 forbidden path={} ip={}",
                                    request.getRequestURI(),
                                    request.getRemoteAddr());

                            response.setStatus(403);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                            response.getWriter().write("{\"status\":403,\"error\":\"Forbidden\",\"path\":\"" + request.getRequestURI() + "\"}");
                        })
                )
                .addFilterBefore(jwtAuthFilter, org.springframework.security.web.authentication.AnonymousAuthenticationFilter.class);

        http.headers(h -> h
                .contentTypeOptions(cto -> {})
                .frameOptions(fo -> fo.deny())
                .referrerPolicy(rp -> rp.policy(
                        org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER))
                .contentSecurityPolicy(csp -> csp.policyDirectives(
                        "default-src 'self'; " +
                                "script-src 'self' 'unsafe-inline'; " +
                                "style-src 'self' 'unsafe-inline'; " +
                                "img-src 'self' data:; " +
                                "base-uri 'self'; " +
                                "frame-ancestors 'none'"
                ))
        );

        return http.build();
    }
}