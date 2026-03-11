package org.jdc.tunja_evenfy.repository;

import org.jdc.tunja_evenfy.entity.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LocationRepository extends JpaRepository<LocationEntity, UUID> {
}
