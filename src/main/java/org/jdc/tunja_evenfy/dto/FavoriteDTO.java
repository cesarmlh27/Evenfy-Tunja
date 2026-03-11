package org.jdc.tunja_evenfy.dto;

import lombok.*;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FavoriteDTO {

    private UUID id;
    private UUID userId;
    private UUID eventId;
}
