package org.jdc.tunja_evenfy.repository;

import org.jdc.tunja_evenfy.entity.FavoriteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FavoriteRepository extends JpaRepository<FavoriteEntity, UUID> {
    List<FavoriteEntity> findByUserId(UUID userId);
    Optional<FavoriteEntity> findByUserIdAndEventId(UUID userId, UUID eventId);
    boolean existsByUserIdAndEventId(UUID userId, UUID eventId);
}
