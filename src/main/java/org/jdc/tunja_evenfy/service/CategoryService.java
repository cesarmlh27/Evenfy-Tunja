package org.jdc.tunja_evenfy.service;

import org.jdc.tunja_evenfy.dto.CategoryDTO;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    List<CategoryDTO> findAll();
    CategoryDTO findById(UUID id);
    CategoryDTO create(CategoryDTO dto);
    CategoryDTO update(UUID id, CategoryDTO dto);
    void delete(UUID id);
}
