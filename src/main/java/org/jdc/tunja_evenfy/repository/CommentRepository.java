package org.jdc.tunja_evenfy.repository;

import org.jdc.tunja_evenfy.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<CommentEntity, UUID> {
    List<CommentEntity> findByEventIdOrderByCreatedAtDesc(UUID eventId);
    List<CommentEntity> findByUserId(UUID userId);
}
