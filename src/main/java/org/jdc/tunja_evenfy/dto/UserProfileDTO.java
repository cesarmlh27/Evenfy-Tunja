package org.jdc.tunja_evenfy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileDTO {
    private UUID userId;
    private String fullName;
    private String email;
    private String role;
    private String avatarUrl;
    private String bio;
    
    // Eventos creados
    private List<EventDTO> createdEvents;
    
    // Eventos que asiste
    private List<EventDTO> attendingEvents;
    
    // Total de eventos
    private int totalEventsCreated;
    private int totalEventsAttending;
}
