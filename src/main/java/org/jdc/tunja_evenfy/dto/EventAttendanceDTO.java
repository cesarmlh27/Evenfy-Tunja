package org.jdc.tunja_evenfy.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventAttendanceDTO {
    private UUID id;
    private UUID eventId;
    private UUID userId;
    private LocalDateTime registeredAt;
}
