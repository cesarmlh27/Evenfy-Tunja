package org.jdc.tunja_evenfy.dto;

import lombok.*;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private UUID id;
    private String fullName;
    private String email;
    private String role;
}
