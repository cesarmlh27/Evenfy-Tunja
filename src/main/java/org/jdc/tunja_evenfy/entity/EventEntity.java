    package org.jdc.tunja_evenfy.entity;
    
    import jakarta.persistence.*;
    import lombok.*;
    import java.time.LocalDateTime;
import java.util.List;
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

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDateTime eventDate;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = true)
    private CategoryEntity category;

    @ManyToOne
    @JoinColumn(name = "location_id", nullable = true)
    private LocationEntity location;

    @Column(name = "location_text", length = 255)
    private String locationText;

    @ManyToOne
    @JoinColumn(name = "organizer_id", nullable = false)
    private UserEntity organizer;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<EventAttendeeEntity> attendees;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<EventRatingEntity> ratings;

    @Column(length = 500)
    private String imageUrl;

    @Column(columnDefinition = "boolean default true")
    @Builder.Default
    private Boolean isFree = true;

    @Column(length = 500)
    private String ticketPurchaseUrl;

    @Column(length = 500)
    private String infoUrl;

    private Integer maxCapacity;

    @Column(columnDefinition = "boolean default true")
    @Builder.Default
    private Boolean isActive = true;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
        if (isActive == null) isActive = true;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getAttendeeCount() {
        return attendees != null ? attendees.stream()
                .filter(a -> a.getStatus() == EventAttendeeEntity.AttendanceStatus.ATTENDING)
                .count() : 0;
    }

    public Double getAverageRating() {
        if (ratings == null || ratings.isEmpty()) return null;
        return ratings.stream()
                .mapToInt(EventRatingEntity::getRating)
                .average()
                .orElse(0.0);
    }
}
