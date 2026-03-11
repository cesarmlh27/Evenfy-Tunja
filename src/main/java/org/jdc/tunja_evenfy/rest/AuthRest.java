package org.jdc.tunja_evenfy.rest;

import lombok.RequiredArgsConstructor;
import org.jdc.tunja_evenfy.config.ApiPaths;
import org.jdc.tunja_evenfy.config.JwtUtil;
import org.jdc.tunja_evenfy.dto.*;
import org.jdc.tunja_evenfy.exception.BadRequestException;
import org.jdc.tunja_evenfy.service.AuthService;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.API_V1 + "/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class AuthRest {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    /**
     * Registro de nuevo usuario
     * POST /api/v1/auth/register
     */
    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    /**
     * Login del usuario
     * POST /api/v1/auth/login
     */
    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    /**
     * Verifica código 2FA y genera token JWT
     * POST /api/v1/auth/verify-2fa
     */
    @PostMapping("/verify-2fa")
    public AuthResponse verifyTwoFactor(@RequestBody TwoFactorRequest request) {
        return authService.verifyTwoFactor(request);
    }

    /**
     * Habilita autenticación en dos pasos
     * POST /api/v1/auth/enable-2fa
     * Header: Authorization: Bearer <token>
     */
    @PostMapping("/enable-2fa")
    public AuthResponse enableTwoFactor(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BadRequestException("Token inválido o ausente");
        }

        String token = authHeader.substring(7); // Remover "Bearer "
        UUID userId = jwtUtil.extractUserId(token);

        return authService.enableTwoFactor(userId);
    }

    /**
     * Deshabilita autenticación en dos pasos
     * POST /api/v1/auth/disable-2fa
     * Header: Authorization: Bearer <token>
     */
    @PostMapping("/disable-2fa")
    public AuthResponse disableTwoFactor(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BadRequestException("Token inválido o ausente");
        }

        String token = authHeader.substring(7); // Remover "Bearer "
        UUID userId = jwtUtil.extractUserId(token);

        return authService.disableTwoFactor(userId);
    }
}
