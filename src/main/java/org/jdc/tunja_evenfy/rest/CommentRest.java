package org.jdc.tunja_evenfy.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jdc.tunja_evenfy.config.ApiPaths;
import org.jdc.tunja_evenfy.dto.CommentDTO;
import org.jdc.tunja_evenfy.exception.BadRequestException;
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

    @GetMapping("/event/{eventId}")
    public List<CommentDTO> findByEvent(@PathVariable UUID eventId) {
        return service.findByEventId(eventId);
    }

    @GetMapping("/{id}")
    public CommentDTO findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @PostMapping
    public CommentDTO create(@Valid @RequestBody CommentDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public CommentDTO update(@PathVariable UUID id, @Valid @RequestBody CommentDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id, HttpServletRequest request) {
        UUID requesterId = (UUID) request.getAttribute("userId");
        String requesterRole = (String) request.getAttribute("role");
        if (requesterId == null) {
            throw new BadRequestException("Usuario no autenticado");
        }
        service.deleteWithAuthorization(id, requesterId, requesterRole);
    }
}
