package org.jdc.tunja_evenfy.rest;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.jdc.tunja_evenfy.config.ApiPaths;
import org.jdc.tunja_evenfy.dto.EventDTO;
import org.jdc.tunja_evenfy.exception.BadRequestException;
import org.jdc.tunja_evenfy.service.EventService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.API_V1 + "/events")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class EventRest {

    private final EventService eventService;

    @GetMapping
    public List<EventDTO> findAll() {
        return eventService.findAll();
    }

    @GetMapping("/{id}")
    public EventDTO findById(@PathVariable UUID id) {
        return eventService.findById(id);
    }

    /**
     * Solo ADMIN y ORGANIZER pueden crear eventos
     */
    @PostMapping
    public EventDTO create(@RequestBody EventDTO dto, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        if (role == null || (!role.equals("ADMIN") && !role.equals("ORGANIZER"))) {
            throw new BadRequestException("Solo administradores y organizadores pueden crear eventos");
        }
        UUID userId = (UUID) request.getAttribute("userId");
        dto.setOrganizerId(userId);
        return eventService.create(dto);
    }

    /**
     * Solo ADMIN y ORGANIZER pueden actualizar eventos
     */
    @PutMapping("/{id}")
    public EventDTO update(@PathVariable UUID id, @RequestBody EventDTO dto, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        if (role == null || (!role.equals("ADMIN") && !role.equals("ORGANIZER"))) {
            throw new BadRequestException("Solo administradores y organizadores pueden actualizar eventos");
        }
        return eventService.update(id, dto);
    }

    /**
     * Solo ADMIN y ORGANIZER pueden eliminar eventos
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        if (role == null || (!role.equals("ADMIN") && !role.equals("ORGANIZER"))) {
            throw new BadRequestException("Solo administradores y organizadores pueden eliminar eventos");
        }
        eventService.delete(id);
    }
}
