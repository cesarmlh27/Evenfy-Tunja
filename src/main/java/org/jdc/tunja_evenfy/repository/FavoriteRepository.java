package org.jdc.tunja_evenfy.repository;

import org.jdc.tunja_evenfy.entity.FavoriteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface FavoriteRepository extends JpaRepository<FavoriteEntity, UUID> {
}
