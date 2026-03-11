package org.jdc.tunja_evenfy.rest;

import lombok.RequiredArgsConstructor;
import org.jdc.tunja_evenfy.config.ApiPaths;
import org.jdc.tunja_evenfy.dto.CommentDTO;
import org.jdc.tunja_evenfy.service.CommentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.API_V1 + "/comments")
@RequiredArgsConstructor
public class CommentRest {

    private final CommentService service;

    @GetMapping
    public List<CommentDTO> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public CommentDTO findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @PostMapping
    public CommentDTO create(@RequestBody CommentDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public CommentDTO update(@PathVariable UUID id, @RequestBody CommentDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
