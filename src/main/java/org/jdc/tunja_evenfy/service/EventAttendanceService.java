package org.jdc.tunja_evenfy.service;

import org.jdc.tunja_evenfy.dto.EventAttendanceDTO;

import java.util.List;
import java.util.UUID;

public interface EventAttendanceService {
    List<EventAttendanceDTO> findAll();
    EventAttendanceDTO findById(UUID id);
    EventAttendanceDTO create(EventAttendanceDTO dto);
    void delete(UUID id);
}
