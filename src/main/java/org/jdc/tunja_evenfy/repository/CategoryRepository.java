package org.jdc.tunja_evenfy.repository;

import org.jdc.tunja_evenfy.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<CategoryEntity, UUID> {
}
