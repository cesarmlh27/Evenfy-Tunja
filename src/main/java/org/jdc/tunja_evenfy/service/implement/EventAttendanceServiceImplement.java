package org.jdc.tunja_evenfy.service.implement;

import lombok.RequiredArgsConstructor;
import org.jdc.tunja_evenfy.dto.EventAttendanceDTO;
import org.jdc.tunja_evenfy.entity.EventAttendanceEntity;
import org.jdc.tunja_evenfy.entity.EventEntity;
import org.jdc.tunja_evenfy.entity.UserEntity;
import org.jdc.tunja_evenfy.exception.BadRequestException;
import org.jdc.tunja_evenfy.exception.NotFoundException;
import org.jdc.tunja_evenfy.repository.EventAttendanceRepository;
import org.jdc.tunja_evenfy.repository.EventRepository;
import org.jdc.tunja_evenfy.repository.UserRepository;
import org.jdc.tunja_evenfy.service.EventAttendanceService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventAttendanceServiceImplement implements EventAttendanceService {

    private final EventAttendanceRepository attendanceRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    private EventAttendanceDTO toDTO(EventAttendanceEntity entity) {
        return EventAttendanceDTO.builder()
                .id(entity.getId())
                .eventId(entity.getEvent().getId())
                .userId(entity.getUser().getId())
                .registeredAt(entity.getRegisteredAt())
                .build();
    }

    @Override
    public List<EventAttendanceDTO> findAll() {
        return attendanceRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    public EventAttendanceDTO findById(UUID id) {
        EventAttendanceEntity entity = attendanceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Attendance record not found: " + id));
        return toDTO(entity);
    }

    @Override
    public EventAttendanceDTO create(EventAttendanceDTO dto) {
        if (dto == null || dto.getEventId() == null) {
            throw new BadRequestException("eventId is required");
        }
        if (dto.getUserId() == null) {
            throw new BadRequestException("userId is required");
        }

        EventEntity event = eventRepository.findById(dto.getEventId())
                .orElseThrow(() -> new NotFoundException("Event not found: " + dto.getEventId()));

        UserEntity user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found: " + dto.getUserId()));

        EventAttendanceEntity entity = EventAttendanceEntity.builder()
                .event(event)
                .user(user)
                .build();

        return toDTO(attendanceRepository.save(entity));
    }

    @Override
    public void delete(UUID id) {
        EventAttendanceEntity entity = attendanceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Attendance record not found: " + id));
        attendanceRepository.delete(entity);
    }
}
