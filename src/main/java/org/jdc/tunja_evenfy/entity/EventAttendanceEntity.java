package org.jdc.tunja_evenfy.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "event_attendance")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventAttendanceEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(
            name = "event_id",
            referencedColumnName = "id",
            nullable = false
    )
    private EventEntity event;

    @ManyToOne
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id_user",
            nullable = false
    )
    private UserEntity user;

    private LocalDateTime registeredAt;

    @PrePersist
    public void onCreate() {
        registeredAt = LocalDateTime.now();
    }
}
