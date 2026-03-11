package org.jdc.tunja_evenfy.rest;

import lombok.RequiredArgsConstructor;
import org.jdc.tunja_evenfy.config.ApiPaths;
import org.jdc.tunja_evenfy.dto.CategoryDTO;
import org.jdc.tunja_evenfy.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.API_V1 + "/categories")
@RequiredArgsConstructor
public class CategoryRest {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDTO> findAll() {
        return categoryService.findAll();
    }

    @GetMapping("/{id}")
    public CategoryDTO findById(@PathVariable UUID id) {
        return categoryService.findById(id);
    }

    @PostMapping
    public CategoryDTO create(@RequestBody CategoryDTO dto) {
        return categoryService.create(dto);
    }

    @PutMapping("/{id}")
    public CategoryDTO update(@PathVariable UUID id, @RequestBody CategoryDTO dto) {
        return categoryService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        categoryService.delete(id);
    }
}
