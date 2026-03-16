package org.jdc.tunja_evenfy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import lombok.RequiredArgsConstructor;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    @Value("${app.cors.allowed-origins:}")
    private String allowedOrigins;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas - sin autenticación
                        .requestMatchers(HttpMethod.GET, "/", "/health").permitAll()
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/events", "/api/v1/events/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/categories/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/locations/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                        .requestMatchers("/uploads/**").permitAll()
                        
                        // Rutas protegidas - requieren autenticación
                        .requestMatchers(HttpMethod.POST, "/api/v1/events/*/attend").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/events/*/rate").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/events").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/events/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/events/**").authenticated()
                        .requestMatchers("/api/v1/users/profile/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/comments").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/comments/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/comments/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/favorites").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/favorites/me").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/users/*/role").hasRole("ADMIN")
                        .requestMatchers("/actuator/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/users").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/upload/**").authenticated()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        List<String> origins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        if (origins.isEmpty()) {
            origins = List.of("http://localhost:5173", "http://localhost:3000", "http://localhost:8080");
        }

        configuration.setAllowedOrigins(origins);
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

