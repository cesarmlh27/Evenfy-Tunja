package org.jdc.tunja_evenfy.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDTO {

    private UUID id;

    @NotBlank(message = "El contenido del comentario es obligatorio")
    @Size(max = 400, message = "El comentario no puede exceder 400 caracteres")
    private String content;

    @NotNull(message = "El userId es obligatorio")
    private UUID userId;

    private String userName;
    private String userAvatar;

    @NotNull(message = "El eventId es obligatorio")
    private UUID eventId;

    private LocalDateTime createdAt;
}

