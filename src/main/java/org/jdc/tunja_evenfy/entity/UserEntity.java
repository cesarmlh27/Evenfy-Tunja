    package org.jdc.tunja_evenfy.entity;

    import jakarta.persistence.*;
    import lombok.*;
    import java.time.LocalDateTime;
    import java.util.UUID;

    @Entity
    @Table(name = "users")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class UserEntity {

        @Id
        @Column(name = "id_user")
        @GeneratedValue
        private UUID id;

        @Column(nullable = false, length = 120)
        private String fullName;

        @Column(nullable = false, length = 120, unique = true)
        private String email;

        @Column(nullable = false, length = 255)
        private String password;

        @Column(nullable = false, length = 20)
        private String role; // USER, ORGANIZER, ADMIN

        @Column(columnDefinition = "boolean default false")
        private Boolean emailVerified; // ¿Email verificado?

        @Column(columnDefinition = "boolean default false")
        private Boolean twoFactorEnabled; // ¿2FA habilitado?

        @Column(length = 255)
        private String twoFactorSecret; // Token secreto temporal para 2FA

        @Column
        private LocalDateTime twoFactorExpiresAt; // Expiración del código 2FA

        @Column(columnDefinition = "boolean default true")
        private Boolean isActive; // Cuenta activa/desactivada

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime lastLogin;

        @PrePersist
        public void onCreate() {
            createdAt = LocalDateTime.now();
            updatedAt = createdAt;
            role = (role == null) ? "USER" : role;
            emailVerified = (emailVerified == null) ? false : emailVerified;
            twoFactorEnabled = (twoFactorEnabled == null) ? false : twoFactorEnabled;
            isActive = (isActive == null) ? true : isActive;
        }

        @PreUpdate
        public void onUpdate() {
            updatedAt = LocalDateTime.now();
        }
    }
