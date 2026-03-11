package org.jdc.tunja_evenfy.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResendEmailService {

    @Value("${resend.api.key:}")
    private String resendApiKey;

    private final RestTemplate restTemplate;

    private static final String RESEND_API_URL = "https://api.resend.com/emails";
    private static final String FROM_EMAIL = "noreply@evenfytunja.dev";

    /**
     * Envía un código 2FA por email usando Resend
     */
    public void sendTwoFactorCode(String email, String code) {
        try {
            if (resendApiKey == null || resendApiKey.isEmpty()) {
                log.warn("Resend API key no configurada");
                return;
            }

            Map<String, Object> payload = new HashMap<>();
            payload.put("from", FROM_EMAIL);
            payload.put("to", email);
            payload.put("subject", "🔐 Código de verificación en dos pasos - Tunja Evenfy");
            payload.put("html", buildTwoFactorEmailHtml(code));

            sendEmail(payload);
            log.info("Email de 2FA enviado exitosamente a: {}", email);
        } catch (Exception e) {
            log.warn("Advertencia - No se pudo enviar email de 2FA a {}: {}", email, e.getMessage());
        }
    }

    /**
     * Envía un email de bienvenida usando Resend
     */
    public void sendWelcomeEmail(String email, String fullName) {
        try {
            if (resendApiKey == null || resendApiKey.isEmpty()) {
                log.warn("Resend API key no configurada");
                return;
            }

            Map<String, Object> payload = new HashMap<>();
            payload.put("from", FROM_EMAIL);
            payload.put("to", email);
            payload.put("subject", "¡Bienvenido a Tunja Evenfy! 🎉");
            payload.put("html", buildWelcomeEmailHtml(fullName));

            sendEmail(payload);
            log.info("Email de bienvenida enviado exitosamente a: {}", email);
        } catch (Exception e) {
            log.warn("Advertencia - No se pudo enviar email de bienvenida a {}: {}", email, e.getMessage());
        }
    }

    /**
     * Envía email a través de la API de Resend
     */
    private void sendEmail(Map<String, Object> payload) {
        try {
            org.springframework.http.HttpEntity<Map<String, Object>> request =
                    new org.springframework.http.HttpEntity<>(payload, createHeaders());

            restTemplate.postForObject(RESEND_API_URL, request, Map.class);
        } catch (Exception e) {
            log.error("Error al enviar email con Resend: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Crea los headers con autenticación de Resend
     */
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + resendApiKey);
        return headers;
    }

    /**
     * Construye el HTML del email de 2FA
     */
    private String buildTwoFactorEmailHtml(String code) {
        return "<html><head><style>" +
                "body { font-family: Arial, sans-serif; background-color: #f5f5f5; }" +
                ".container { max-width: 600px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px; }" +
                ".header { color: #333; margin-bottom: 20px; }" +
                ".code-box { background: #f0f0f0; padding: 20px; text-align: center; border-radius: 5px; margin: 20px 0; }" +
                ".code { font-size: 32px; font-weight: bold; letter-spacing: 5px; color: #007bff; }" +
                ".footer { color: #666; font-size: 12px; margin-top: 20px; }" +
                "</style></head><body><div class='container'>" +
                "<h2 class='header'>Verificación en Dos Pasos</h2>" +
                "<p>Hola,</p>" +
                "<p>Tu código de verificación en dos pasos es:</p>" +
                "<div class='code-box'><div class='code'>" + code + "</div></div>" +
                "<p>Este código expira en 10 minutos.</p>" +
                "<p>Si no solicitaste este código, ignora este email.</p>" +
                "<div class='footer'><p>© 2026 Tunja Evenfy. Todos los derechos reservados.</p></div>" +
                "</div></body></html>";
    }

    /**
     * Construye el HTML del email de bienvenida
     */
    private String buildWelcomeEmailHtml(String fullName) {
        return "<html><head><style>" +
                "body { font-family: Arial, sans-serif; background-color: #f5f5f5; }" +
                ".container { max-width: 600px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px; }" +
                ".header { color: #333; margin-bottom: 20px; }" +
                ".button { background: #007bff; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; display: inline-block; margin: 20px 0; }" +
                ".footer { color: #666; font-size: 12px; margin-top: 20px; }" +
                "</style></head><body><div class='container'>" +
                "<h2 class='header'>¡Bienvenido a Tunja Evenfy! 🎉</h2>" +
                "<p>Hola <strong>" + fullName + "</strong>,</p>" +
                "<p>Tu cuenta ha sido creada exitosamente. Estamos emocionados de tenerte con nosotros.</p>" +
                "<p>Ya puedes comenzar a explorar eventos en tu ciudad.</p>" +
                "<div class='footer'><p>© 2026 Tunja Evenfy. Todos los derechos reservados.</p></div>" +
                "</div></body></html>";
    }
}
