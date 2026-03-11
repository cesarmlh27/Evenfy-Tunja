package org.jdc.tunja_evenfy.repository;

import org.jdc.tunja_evenfy.entity.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EventRepository extends JpaRepository<EventEntity, UUID> {
}
