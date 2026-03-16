package org.jdc.tunja_evenfy.config;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

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
            try {
                String token = extractToken(request);
                if (token != null && jwtUtil.isTokenValid(token) && jwtUtil.isTokenNotExpired(token)) {
                    try {
                        setAuthentication(request, token);
                    } catch (Exception e) {
                        logger.error("Error extrayendo datos del token válido: " + e.getMessage());
                    }
                }
            } catch (JwtException e) {
                logger.error("JWT validation error: " + e.getMessage());
            }
            filterChain.doFilter(request, response);
            return;
        }

        // Rutas privadas que requieren autenticación
        try {
            String token = extractToken(request);
            if (token != null && jwtUtil.isTokenValid(token) && jwtUtil.isTokenNotExpired(token)) {
                setAuthentication(request, token);
            }
        } catch (JwtException e) {
            logger.error("JWT validation error: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extrae claims del token y establece la autenticación en el SecurityContext
     */
    private void setAuthentication(HttpServletRequest request, String token) {
        UUID userId = jwtUtil.extractUserId(token);
        String email = jwtUtil.extractEmail(token);
        String role = jwtUtil.extractRole(token);

        request.setAttribute("userId", userId);
        request.setAttribute("email", email);
        request.setAttribute("role", role);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                email, null, List.of(new SimpleGrantedAuthority("ROLE_" + role)));
        SecurityContextHolder.getContext().setAuthentication(auth);
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
        // Rutas que requieren autenticación aunque estén bajo /events
        if (uri.matches(".*/events/[^/]+/attend$") ||
            uri.matches(".*/events/[^/]+/rate$")) {
            return false;
        }
        // Rutas que requieren autenticación bajo /users
        if (uri.contains("/users/profile")) {
            return false;
        }
        return uri.contains("/auth/") ||
               uri.contains("/health") ||
               uri.contains("/swagger") ||
               uri.contains("/api-docs") ||
               uri.contains("/uploads/") ||
               uri.equals("/") ||
               uri.contains("/events") ||
               uri.contains("/categories") ||
               uri.contains("/locations");
    }
}
