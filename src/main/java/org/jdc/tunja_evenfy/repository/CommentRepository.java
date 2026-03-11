package org.jdc.tunja_evenfy.repository;

import org.jdc.tunja_evenfy.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<CommentEntity, UUID> {
}
