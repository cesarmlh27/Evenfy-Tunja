package org.jdc.tunja_evenfy.service.implement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jdc.tunja_evenfy.entity.EventAttendeeEntity;
import org.jdc.tunja_evenfy.entity.EventEntity;
import org.jdc.tunja_evenfy.entity.EventRatingEntity;
import org.jdc.tunja_evenfy.entity.UserEntity;
import org.jdc.tunja_evenfy.exception.BadRequestException;
import org.jdc.tunja_evenfy.exception.NotFoundException;
import org.jdc.tunja_evenfy.repository.EventAttendeeRepository;
import org.jdc.tunja_evenfy.repository.EventRatingRepository;
import org.jdc.tunja_evenfy.repository.EventRepository;
import org.jdc.tunja_evenfy.repository.UserRepository;
import org.jdc.tunja_evenfy.service.EventAttendanceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventAttendanceServiceImpl implements EventAttendanceService {

    private final EventAttendeeRepository attendeeRepository;
    private final EventRatingRepository ratingRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public EventAttendeeEntity markAttendance(UUID userId, UUID eventId, String status) {
        if (userId == null || eventId == null) {
            throw new BadRequestException("userId and eventId are required");
        }

        EventEntity event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found: " + eventId));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        // Validar status
        try {
            EventAttendeeEntity.AttendanceStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid status. Use: ATTENDING, INTERESTED, NOT_ATTENDING");
        }

        // Si ya existe, actualizar; si no, crear
        EventAttendeeEntity attendee = attendeeRepository.findByUserIdAndEventId(userId, eventId)
                .orElse(new EventAttendeeEntity());

        attendee.setUser(user);
        attendee.setEvent(event);
        attendee.setStatus(EventAttendeeEntity.AttendanceStatus.valueOf(status.toUpperCase()));

        return attendeeRepository.save(attendee);
    }

    @Override
    @Transactional
    public EventRatingEntity rateEvent(UUID userId, UUID eventId, Integer rating, String comment) {
        if (userId == null || eventId == null || rating == null) {
            throw new BadRequestException("userId, eventId, and rating are required");
        }

        if (rating < 1 || rating > 5) {
            throw new BadRequestException("Rating must be between 1 and 5");
        }

        EventEntity event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found: " + eventId));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        // Si ya existe calificación, actualizar; si no, crear
        EventRatingEntity ratingEntity = ratingRepository.findByUserIdAndEventId(userId, eventId)
                .orElse(new EventRatingEntity());

        ratingEntity.setUser(user);
        ratingEntity.setEvent(event);
        ratingEntity.setRating(rating);
        ratingEntity.setComment(comment);

        return ratingRepository.save(ratingEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventAttendeeEntity> getEventAttendees(UUID eventId) {
        return attendeeRepository.findByEventId(eventId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventRatingEntity> getEventRatings(UUID eventId) {
        return ratingRepository.findByEventId(eventId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUserAttending(UUID userId, UUID eventId) {
        return attendeeRepository.findByUserIdAndEventId(userId, eventId)
                .map(a -> a.getStatus() == EventAttendeeEntity.AttendanceStatus.ATTENDING)
                .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUserRated(UUID userId, UUID eventId) {
        return ratingRepository.existsByUserIdAndEventId(userId, eventId);
    }
}
