package org.jdc.tunja_evenfy.service;

import lombok.RequiredArgsConstructor;
import org.jdc.tunja_evenfy.config.JwtUtil;
import org.jdc.tunja_evenfy.dto.*;
import org.jdc.tunja_evenfy.entity.TwoFactorTokenEntity;
import org.jdc.tunja_evenfy.entity.UserEntity;
import org.jdc.tunja_evenfy.exception.BadRequestException;
import org.jdc.tunja_evenfy.exception.NotFoundException;
import org.jdc.tunja_evenfy.repository.TwoFactorTokenRepository;
import org.jdc.tunja_evenfy.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final TwoFactorTokenRepository twoFactorTokenRepository;
    private final ResendEmailService emailService;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    private static final int CODE_LENGTH = 6;
    private static final long CODE_EXPIRATION_MINUTES = 10;

    /**
     * Registra un nuevo usuario
     */
    public AuthResponse register(RegisterRequest request) {
        // Validar que los campos no sean nulos
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new BadRequestException("Email es requerido");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new BadRequestException("Contraseña es requerida");
        }
        if (request.getFullName() == null || request.getFullName().trim().isEmpty()) {
            throw new BadRequestException("Nombre completo es requerido");
        }

        // Validar que las contraseñas coincidan
        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new BadRequestException("Las contraseñas no coinciden");
        }

        // Validar que el email no exista
        Optional<UserEntity> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            throw new BadRequestException("El email ya está registrado");
        }

        // Crear nuevo usuario
        UserEntity user = UserEntity.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("USER")
                .emailVerified(false)
                .twoFactorEnabled(false)
                .isActive(true)
                .build();

        userRepository.save(user);

        // Enviar email de bienvenida
        emailService.sendWelcomeEmail(user.getEmail(), user.getFullName());

        return AuthResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .message("Registro exitoso. Revisa tu email para confirmar tu cuenta.")
                .build();
    }

    /**
     * Login de usuario - genera código 2FA
     */
    public AuthResponse login(LoginRequest request) {
        if (request.getEmail() == null || request.getPassword() == null) {
            throw new BadRequestException("Email y contraseña son requeridos");
        }

        // Buscar usuario por email
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        // Validar contraseña
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Contraseña incorrecta");
        }

        // Validar que la cuenta esté activa
        if (!user.getIsActive()) {
            throw new BadRequestException("Cuenta desactivada");
        }

        // Si 2FA está habilitado, generar código y enviar
        if (user.getTwoFactorEnabled()) {
            String code = generateRandomCode();
            
            // Guardar código en BD
            TwoFactorTokenEntity tokenEntity = TwoFactorTokenEntity.builder()
                    .userId(user.getId())
                    .code(code)
                    .expiresAt(LocalDateTime.now().plusMinutes(CODE_EXPIRATION_MINUTES))
                    .used(false)
                    .build();
            twoFactorTokenRepository.save(tokenEntity);

            // Enviar código por email
            emailService.sendTwoFactorCode(user.getEmail(), code);

            return AuthResponse.builder()
                    .userId(user.getId())
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .role(user.getRole())
                    .twoFactorRequired(true)
                    .message("Código de 2FA enviado a tu email")
                    .build();
        }

        // Si 2FA no está habilitado, generar token JWT directamente
        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole());
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        return AuthResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .token(token)
                .twoFactorRequired(false)
                .message("Inicio de sesión exitoso")
                .build();
    }

    /**
     * Valida el código 2FA y genera token JWT
     */
    public AuthResponse verifyTwoFactor(TwoFactorRequest request) {
        if (request.getEmail() == null || request.getCode() == null) {
            throw new BadRequestException("Email y código son requeridos");
        }

        // Buscar usuario
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        // Buscar el token 2FA
        TwoFactorTokenEntity tokenEntity = twoFactorTokenRepository
                .findByUserIdAndCodeAndUsedFalse(user.getId(), request.getCode())
                .orElseThrow(() -> new BadRequestException("Código inválido o expirado"));

        // Validar que no haya expirado
        if (LocalDateTime.now().isAfter(tokenEntity.getExpiresAt())) {
            throw new BadRequestException("Código expirado");
        }

        // Marcar como usado
        tokenEntity.setUsed(true);
        tokenEntity.setUsedAt(LocalDateTime.now());
        twoFactorTokenRepository.save(tokenEntity);

        // Generar token JWT
        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole());
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        // Limpiar códigos antiguos
        twoFactorTokenRepository.deleteByUserIdAndUsedTrue(user.getId());

        return AuthResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .token(token)
                .twoFactorRequired(false)
                .message("Verificación 2FA exitosa")
                .build();
    }

    /**
     * Habilita 2FA para un usuario
     */
    public AuthResponse enableTwoFactor(UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        user.setTwoFactorEnabled(true);
        userRepository.save(user);

        return AuthResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .message("Autenticación en dos pasos habilitada")
                .build();
    }

    /**
     * Deshabilita 2FA para un usuario
     */
    public AuthResponse disableTwoFactor(UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        user.setTwoFactorEnabled(false);
        user.setTwoFactorSecret(null);
        userRepository.save(user);

        return AuthResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .message("Autenticación en dos pasos deshabilitada")
                .build();
    }

    /**
     * Genera un código aleatorio de 6 dígitos
     */
    private String generateRandomCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
}
