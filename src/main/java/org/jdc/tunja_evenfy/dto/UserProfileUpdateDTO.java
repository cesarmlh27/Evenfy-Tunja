package org.jdc.tunja_evenfy.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileUpdateDTO {

    @Size(max = 120, message = "El nombre no puede exceder 120 caracteres")
    private String fullName;

    @Size(max = 500, message = "La bio no puede exceder 500 caracteres")
    private String bio;
}
