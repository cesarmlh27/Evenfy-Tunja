package org.jdc.tunja_evenfy.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "favorites")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FavoriteEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id_user",
            nullable = false
    )
    private UserEntity user;

    @ManyToOne
    @JoinColumn(
            name = "event_id",
            referencedColumnName = "id",
            nullable = false
    )
    private EventEntity event;

    private LocalDateTime createdAt = LocalDateTime.now();
}
