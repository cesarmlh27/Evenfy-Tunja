package org.jdc.tunja_evenfy.repository;

import org.jdc.tunja_evenfy.entity.EventRatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventRatingRepository extends JpaRepository<EventRatingEntity, Long> {
    List<EventRatingEntity> findByEventId(UUID eventId);
    Optional<EventRatingEntity> findByUserIdAndEventId(UUID userId, UUID eventId);
    boolean existsByUserIdAndEventId(UUID userId, UUID eventId);
}
