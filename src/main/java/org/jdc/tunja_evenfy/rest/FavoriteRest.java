package org.jdc.tunja_evenfy.rest;

import lombok.RequiredArgsConstructor;
import org.jdc.tunja_evenfy.config.ApiPaths;
import org.jdc.tunja_evenfy.dto.FavoriteDTO;
import org.jdc.tunja_evenfy.service.FavoriteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.API_V1 + "/favorites")
@RequiredArgsConstructor
public class FavoriteRest {

    private final FavoriteService service;

    @GetMapping
    public List<FavoriteDTO> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public FavoriteDTO findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @PostMapping
    public FavoriteDTO create(@RequestBody FavoriteDTO dto) {
        return service.create(dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
