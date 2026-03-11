package org.jdc.tunja_evenfy.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class EventDTO {
    private UUID id;
    private String title;
    private String description;
    private LocalDateTime eventDate;

    private UUID categoryId;
    private UUID locationId;
    private UUID organizerId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
