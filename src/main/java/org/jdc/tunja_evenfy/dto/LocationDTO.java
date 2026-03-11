package org.jdc.tunja_evenfy.dto;

import lombok.*;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationDTO {
    private UUID id;
    private String placeName;
    private String address;
    private Double latitude;
    private Double longitude;
}
