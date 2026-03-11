package org.jdc.tunja_evenfy.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDTO {

    private UUID id;
    private String content;
    private UUID userId;
    private UUID eventId;
    private LocalDateTime createdAt;
}

