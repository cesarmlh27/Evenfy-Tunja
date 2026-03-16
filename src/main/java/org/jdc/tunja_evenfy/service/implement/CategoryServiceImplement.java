package org.jdc.tunja_evenfy.service.implement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jdc.tunja_evenfy.dto.CategoryDTO;
import org.jdc.tunja_evenfy.entity.CategoryEntity;
import org.jdc.tunja_evenfy.exception.BadRequestException;
import org.jdc.tunja_evenfy.exception.NotFoundException;
import org.jdc.tunja_evenfy.repository.CategoryRepository;
import org.jdc.tunja_evenfy.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImplement implements CategoryService {

    private final CategoryRepository categoryRepository;

    private CategoryDTO toDTO(CategoryEntity e) {
        return CategoryDTO.builder()
                .id(e.getId())
                .name(e.getName())
                .build();
    }

    private CategoryEntity toEntity(CategoryDTO dto) {
        // En create normalmente NO se manda id desde el cliente
        return CategoryEntity.builder()
                .id(dto.getId())
                .name(dto.getName() != null ? dto.getName().trim() : null)
                .build();
    }

    @Override
    public List<CategoryDTO> findAll() {
        return categoryRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public CategoryDTO findById(UUID id) {
        CategoryEntity e = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found: " + id));
        return toDTO(e);
    }

    @Override
    public CategoryDTO create(CategoryDTO dto) {
        // Validación mínima (Paso 3 será DTO con @Valid, pero esto ya protege)
        if (dto == null || dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new BadRequestException("Category name is required");
        }

        CategoryEntity saved = categoryRepository.save(toEntity(dto));
        return toDTO(saved);
    }

    @Override
    public CategoryDTO update(UUID id, CategoryDTO dto) {
        if (dto == null || dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new BadRequestException("Category name is required");
        }

        CategoryEntity e = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found: " + id));

        e.setName(dto.getName().trim());
        return toDTO(categoryRepository.save(e));
    }

    @Override
    public void delete(UUID id) {
        CategoryEntity e = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found: " + id));

        categoryRepository.delete(e);
    }
}
