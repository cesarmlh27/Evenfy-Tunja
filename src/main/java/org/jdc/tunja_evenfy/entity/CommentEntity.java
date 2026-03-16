package org.jdc.tunja_evenfy.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "comments")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 400)
    private String content;

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

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
