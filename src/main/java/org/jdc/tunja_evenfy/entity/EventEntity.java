    package org.jdc.tunja_evenfy.entity;
    
    import jakarta.persistence.*;
    import lombok.*;
    import java.time.LocalDateTime;
    import java.util.UUID;
    
    @Entity
    @Table(name = "events")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class EventEntity {
    
        @Id
        @GeneratedValue
        private UUID id;
    
        @Column(nullable = false, length = 150)
        private String title;
    
        @Column(nullable = false, columnDefinition = "TEXT")
        private String description;
    
        @Column(nullable = false)
        private LocalDateTime eventDate;
    
        @ManyToOne
        @JoinColumn(name = "category_id", nullable = false)
        private CategoryEntity category;
    
        @ManyToOne
        @JoinColumn(name = "location_id", nullable = false)
        private LocationEntity location;
    
        @ManyToOne
        @JoinColumn(name = "organizer_id", nullable = false)
        private UserEntity organizer;
    
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    
        @PrePersist
        public void onCreate() {
            createdAt = LocalDateTime.now();
            updatedAt = createdAt;
        }
    
        @PreUpdate
        public void onUpdate() {
            updatedAt = LocalDateTime.now();
        }
    }
    
