package org.jdc.tunja_evenfy.dto;

import lombok.*;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private UUID userId;
    private String email;
    private String fullName;
    private String role;
    private String token;
    private Boolean twoFactorRequired;
    private String message;
}
