package org.jdc.tunja_evenfy.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jdc.tunja_evenfy.config.ApiPaths;
import org.jdc.tunja_evenfy.dto.FavoriteDTO;
import org.jdc.tunja_evenfy.service.FavoriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
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

    /**
     * Obtener favoritos del usuario autenticado
     */
    @GetMapping("/me")
    public List<FavoriteDTO> getMyFavorites(HttpServletRequest request) {
        UUID userId = (UUID) request.getAttribute("userId");
        return service.findByUserId(userId);
    }

    @GetMapping("/{id}")
    public FavoriteDTO findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    /**
     * Toggle favorito (create=agregar / si ya existe=remover)
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> toggleFavorite(@Valid @RequestBody FavoriteDTO dto) {
        FavoriteDTO result = service.create(dto);
        if (result == null) {
            return ResponseEntity.ok(Map.of("action", "removed", "message", "Favorito removido"));
        }
        return ResponseEntity.ok(Map.of("action", "added", "favorite", result));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
