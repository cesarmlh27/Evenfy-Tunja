package org.jdc.tunja_evenfy.service;

import org.jdc.tunja_evenfy.entity.EventAttendeeEntity;
import org.jdc.tunja_evenfy.entity.EventRatingEntity;
import org.jdc.tunja_evenfy.dto.EventAttendanceDTO;
import org.jdc.tunja_evenfy.dto.EventRatingDTO;

import java.util.List;
import java.util.UUID;

public interface EventAttendanceService {
    EventAttendeeEntity markAttendance(UUID userId, UUID eventId, String status);
    EventRatingEntity rateEvent(UUID userId, UUID eventId, Integer rating, String comment);
    List<EventAttendeeEntity> getEventAttendees(UUID eventId);
    List<EventRatingEntity> getEventRatings(UUID eventId);
    boolean isUserAttending(UUID userId, UUID eventId);
    boolean hasUserRated(UUID userId, UUID eventId);
}
