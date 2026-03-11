package org.jdc.tunja_evenfy.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * Envía un código 2FA por email
     */
    public void sendTwoFactorCode(String email, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("🔐 Código de verificación en dos pasos - Tunja Evenfy");
            message.setText(buildTwoFactorEmailBody(code));
            mailSender.send(message);
            log.info("Email de 2FA enviado exitosamente a: {}", email);
        } catch (Exception e) {
            log.warn("Advertencia - No se pudo enviar email de 2FA a {}: {}", email, e.getMessage());
            // No lanzar excepción - permitir que el proceso continúe
        }
    }

    /**
     * Envía un email de bienvenida
     */
    public void sendWelcomeEmail(String email, String fullName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("¡Bienvenido a Tunja Evenfy! 🎉");
            message.setText(buildWelcomeEmailBody(fullName));
            mailSender.send(message);
            log.info("Email de bienvenida enviado exitosamente a: {}", email);
        } catch (Exception e) {
            log.warn("Advertencia - No se pudo enviar email de bienvenida a {}: {}", email, e.getMessage());
            // No lanzar excepción - permitir que el proceso continúe
        }
    }

    /**
     * Construye el cuerpo del email de 2FA
     */
    private String buildTwoFactorEmailBody(String code) {
        return "Hola,\n\n" +
                "Tu código de verificación en dos pasos es:\n\n" +
                ">>> " + code + " <<<\n\n" +
                "Este código expira en 10 minutos.\n\n" +
                "Si no solicitaste este código, ignora este email.\n\n" +
                "Saludos,\n" +
                "Equipo Tunja Evenfy";
    }

    /**
     * Construye el cuerpo del email de bienvenida
     */
    private String buildWelcomeEmailBody(String fullName) {
        return "Hola " + fullName + ",\n\n" +
                "¡Bienvenido a Tunja Evenfy!\n\n" +
                "Tu cuenta ha sido creada exitosamente. Ahora puedes:\n" +
                "- Explorar eventos cercanos\n" +
                "- Agregar eventos a tus favoritos\n" +
                "- Registrarte en eventos que te interesen\n\n" +
                "Inicia sesión para comenzar:\n" +
                "https://tunja-evenfy.com/login\n\n" +
                "Saludos,\n" +
                "Equipo Tunja Evenfy";
    }
}
