package org.jdc.tunja_evenfy.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TwoFactorRequest {
    private String email;
    private String code;
}
