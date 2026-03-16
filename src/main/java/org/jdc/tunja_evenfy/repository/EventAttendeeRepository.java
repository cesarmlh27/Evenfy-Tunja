package org.jdc.tunja_evenfy.repository;

import org.jdc.tunja_evenfy.entity.EventAttendeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventAttendeeRepository extends JpaRepository<EventAttendeeEntity, Long> {
    List<EventAttendeeEntity> findByEventId(UUID eventId);
    Optional<EventAttendeeEntity> findByUserIdAndEventId(UUID userId, UUID eventId);
    boolean existsByUserIdAndEventId(UUID userId, UUID eventId);
    List<EventAttendeeEntity> findByUserId(UUID userId);
    long countByEventIdAndStatus(UUID eventId, EventAttendeeEntity.AttendanceStatus status);
}
