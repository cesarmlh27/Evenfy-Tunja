package org.jdc.tunja_evenfy.repository;

import org.jdc.tunja_evenfy.entity.TwoFactorTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface TwoFactorTokenRepository extends JpaRepository<TwoFactorTokenEntity, UUID> {
    Optional<TwoFactorTokenEntity> findByUserIdAndCodeAndUsedFalse(UUID userId, String code);
    
    void deleteByUserIdAndUsedTrue(UUID userId);
}
