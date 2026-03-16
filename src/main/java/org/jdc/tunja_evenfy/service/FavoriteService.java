package org.jdc.tunja_evenfy.service;

import org.jdc.tunja_evenfy.dto.FavoriteDTO;
import java.util.List;
import java.util.UUID;

public interface FavoriteService {

    List<FavoriteDTO> findAll();
    List<FavoriteDTO> findByUserId(UUID userId);
    FavoriteDTO findById(UUID id);
    FavoriteDTO create(FavoriteDTO dto);
    void delete(UUID id);
}
