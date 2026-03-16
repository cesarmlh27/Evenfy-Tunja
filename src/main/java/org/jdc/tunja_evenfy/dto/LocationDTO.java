package org.jdc.tunja_evenfy.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationDTO {
    private UUID id;

    @NotBlank(message = "El nombre del lugar es obligatorio")
    @Size(max = 150, message = "El nombre no puede exceder 150 caracteres")
    private String placeName;

    private String address;
    private Double latitude;
    private Double longitude;
}
