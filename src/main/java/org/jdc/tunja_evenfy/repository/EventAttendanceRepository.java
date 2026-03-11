package org.jdc.tunja_evenfy.repository;

import org.jdc.tunja_evenfy.entity.EventAttendanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EventAttendanceRepository extends JpaRepository<EventAttendanceEntity, UUID> {
}
