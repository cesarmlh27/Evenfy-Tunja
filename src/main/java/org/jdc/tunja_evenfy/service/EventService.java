package org.jdc.tunja_evenfy.service;

import org.jdc.tunja_evenfy.dto.EventDTO;

import java.util.List;
import java.util.UUID;

public interface EventService {
    List<EventDTO> findAll();
    EventDTO findById(UUID id);
    EventDTO create(EventDTO dto);
    EventDTO update(UUID id, EventDTO dto);
    void delete(UUID id);
}
