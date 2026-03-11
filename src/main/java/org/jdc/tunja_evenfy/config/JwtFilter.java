package org.jdc.tunja_evenfy.config;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Rutas públicas que no requieren autenticación
        String uri = request.getRequestURI();
        if (isPublicRoute(uri)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = extractToken(request);
            if (token != null && jwtUtil.isTokenValid(token) && jwtUtil.isTokenNotExpired(token)) {
                // Pasar el token al request para que esté disponible en controllers
                request.setAttribute("userId", jwtUtil.extractUserId(token));
                request.setAttribute("email", jwtUtil.extractEmail(token));
                request.setAttribute("role", jwtUtil.extractRole(token));
            }
        } catch (JwtException e) {
            logger.error("JWT validation error: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extrae el token del header Authorization
     */
    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    /**
     * Define rutas públicas que no requieren autenticación
     */
    private boolean isPublicRoute(String uri) {
        return uri.contains("/auth/") ||
               uri.contains("/health") ||
               uri.contains("/swagger") ||
               uri.contains("/api-docs") ||
               uri.equals("/");
    }
}
