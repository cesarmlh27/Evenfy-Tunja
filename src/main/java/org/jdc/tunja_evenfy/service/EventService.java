package org.jdc.tunja_evenfy.service;

import org.jdc.tunja_evenfy.dto.EventDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface EventService {
    List<EventDTO> findAll();
    Page<EventDTO> findAllPaged(Pageable pageable);
    Page<EventDTO> search(String title, UUID categoryId, UUID locationId,
                          LocalDateTime from, LocalDateTime to, Pageable pageable);
    EventDTO findById(UUID id);
    EventDTO create(EventDTO dto);
    EventDTO update(UUID id, EventDTO dto);
    void delete(UUID id);
    int getAttendeesCount(UUID eventId);
    Double getAverageRating(UUID eventId);
    void rateEvent(UUID eventId, UUID userId, int rating);
}
