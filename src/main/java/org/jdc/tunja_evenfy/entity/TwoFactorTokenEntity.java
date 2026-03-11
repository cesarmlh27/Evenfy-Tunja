package org.jdc.tunja_evenfy.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "two_factor_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TwoFactorTokenEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false, length = 6)
    private String code; // Código de 6 dígitos

    @Column(nullable = false)
    private LocalDateTime expiresAt; // Vence en 10 minutos

    @Column(columnDefinition = "boolean default false")
    private Boolean used; // ¿Ya fue usado?

    private LocalDateTime createdAt;
    private LocalDateTime usedAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        used = (used == null) ? false : used;
    }
}
