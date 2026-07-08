package com.grapevine.purchase.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        log.warn(">>> JwtAuthenticationFilter ejecutándose para: {} {}", request.getMethod(), request.getRequestURI());

        String authHeader = request.getHeader("Authorization");
        log.warn(">>> Authorization header presente: {}", authHeader != null);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            String email = jwtService.extractEmail(token);
            String role = jwtService.extractRole(token);
            boolean expired = jwtService.isTokenExpired(token);

            log.warn(">>> email={} | role={} | expired={}", email, role, expired);

            if (email != null && role != null && !expired) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_" + role))
                        );

                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.warn(">>> Autenticación establecida con authorities: {}", authentication.getAuthorities());
            } else {
                log.warn(">>> NO se autenticó: alguna condición fue falsa");
            }
        } catch (Exception e) {
            log.warn(">>> Error validando JWT: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}