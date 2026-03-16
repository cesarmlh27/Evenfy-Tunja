package org.jdc.tunja_evenfy.repository;

import org.jdc.tunja_evenfy.entity.EventEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface EventRepository extends JpaRepository<EventEntity, UUID> {
       List<EventEntity> findByOrganizerId(UUID organizerId);
       List<EventEntity> findByOrganizerIdAndIsActiveTrue(UUID organizerId);

    List<EventEntity> findByIsActiveTrue();

    Page<EventEntity> findByIsActiveTrue(Pageable pageable);

    @Query("SELECT e FROM EventEntity e WHERE e.isActive = true " +
           "AND (:title IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :title, '%'))) " +
           "AND (:categoryId IS NULL OR e.category.id = :categoryId) " +
           "AND (:locationId IS NULL OR e.location.id = :locationId) " +
           "AND (:from IS NULL OR e.eventDate >= :from) " +
           "AND (:to IS NULL OR e.eventDate <= :to)")
    Page<EventEntity> searchEvents(
            @Param("title") String title,
            @Param("categoryId") UUID categoryId,
            @Param("locationId") UUID locationId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable);
}
