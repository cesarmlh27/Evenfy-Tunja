package org.jdc.tunja_evenfy.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jdc.tunja_evenfy.config.ApiPaths;
import org.jdc.tunja_evenfy.dto.EventDTO;
import org.jdc.tunja_evenfy.entity.EventAttendeeEntity;
import org.jdc.tunja_evenfy.entity.EventRatingEntity;
import org.jdc.tunja_evenfy.exception.BadRequestException;
import org.jdc.tunja_evenfy.service.EventService;
import org.jdc.tunja_evenfy.service.EventAttendanceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.API_V1 + "/events")
@RequiredArgsConstructor
public class EventRest {

    private final EventService eventService;
    private final EventAttendanceService attendanceService;

    @GetMapping
    public List<EventDTO> findAll() {
        return eventService.findAll();
    }

    /**
     * Búsqueda con paginación y filtros
     * GET /api/v1/events/search?title=xxx&categoryId=xxx&locationId=xxx&from=xxx&to=xxx&page=0&size=10
     */
    @GetMapping("/search")
    public Page<EventDTO> search(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) UUID locationId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("eventDate").ascending());
        return eventService.search(title, categoryId, locationId, from, to, pageable);
    }

    @GetMapping("/{id}")
    public EventDTO findById(@PathVariable UUID id) {
        return eventService.findById(id);
    }

    @PostMapping
    public EventDTO create(@Valid @RequestBody EventDTO dto, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        if (role == null || (!role.equals("ADMIN") && !role.equals("ORGANIZER"))) {
            throw new BadRequestException("Solo administradores y organizadores pueden crear eventos");
        }
        return eventService.create(dto);
    }

    @PutMapping("/{id}")
    public EventDTO update(@PathVariable UUID id, @Valid @RequestBody EventDTO dto, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        UUID userId = (UUID) request.getAttribute("userId");
        if (role == null || (!role.equals("ADMIN") && !role.equals("ORGANIZER"))) {
            throw new BadRequestException("Solo administradores y organizadores pueden actualizar eventos");
        }

        // ADMIN puede editar cualquier evento; ORGANIZER solo sus propios eventos.
        if (!"ADMIN".equals(role)) {
            EventDTO existing = eventService.findById(id);
            if (userId == null || existing.getCreatedBy() == null || !existing.getCreatedBy().equals(userId)) {
                throw new BadRequestException("Solo el creador del evento puede editarlo");
            }
        }
        return eventService.update(id, dto);
    }

    /**
     * Solo ADMIN y ORGANIZER pueden eliminar eventos
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        UUID userId = (UUID) request.getAttribute("userId");
        if (role == null || (!role.equals("ADMIN") && !role.equals("ORGANIZER"))) {
            throw new BadRequestException("Solo administradores y organizadores pueden eliminar eventos");
        }
        // ORGANIZER solo puede eliminar sus propios eventos
        if (!"ADMIN".equals(role)) {
            EventDTO existing = eventService.findById(id);
            if (userId == null || existing.getCreatedBy() == null || !existing.getCreatedBy().equals(userId)) {
                throw new BadRequestException("Solo el creador del evento puede eliminarlo");
            }
        }
        eventService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Marcar asistencia a un evento
     * POST /api/v1/events/{id}/attend
     */
    @PostMapping("/{id}/attend")
    public ResponseEntity<Map<String, Object>> markAttendance(
            @PathVariable UUID id,
            @RequestBody Map<String, String> request,
            HttpServletRequest httpRequest) {
        
        UUID userId = (UUID) httpRequest.getAttribute("userId");
        if (userId == null) {
            throw new BadRequestException("Usuario no autenticado");
        }

        String status = request == null ? "ATTENDING" : request.getOrDefault("status", "ATTENDING");
        
        EventAttendeeEntity attendee = attendanceService.markAttendance(userId, id, status);
        
        return ResponseEntity.ok(Map.of(
                "message", "Asistencia marcada",
                "status", attendee.getStatus().toString()
        ));
    }

    /**
     * Calificar un evento
     * POST /api/v1/events/{id}/rate
     */
    @PostMapping("/{id}/rate")
    public ResponseEntity<Map<String, Object>> rateEvent(
            @PathVariable UUID id,
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {
        
        UUID userId = (UUID) httpRequest.getAttribute("userId");
        if (userId == null) {
            throw new BadRequestException("Usuario no autenticado");
        }

        if (request == null || !(request.get("rating") instanceof Number ratingNumber)) {
            throw new BadRequestException("El campo 'rating' es requerido y debe ser numérico");
        }

        Integer rating = ratingNumber.intValue();
        String comment = (String) request.get("comment");
        
        EventRatingEntity ratingEntity = attendanceService.rateEvent(userId, id, rating, comment);
        
        return ResponseEntity.ok(Map.of(
                "message", "Evento calificado",
                "rating", ratingEntity.getRating()
        ));
    }

    /**
     * Obtener lista de asistentes
     * GET /api/v1/events/{id}/attendees
     */
    @GetMapping("/{id}/attendees")
    public ResponseEntity<Map<String, Object>> getAttendees(@PathVariable UUID id) {
        var attendees = attendanceService.getEventAttendees(id);
        long attendingCount = attendees.stream()
                .filter(a -> a.getStatus() == EventAttendeeEntity.AttendanceStatus.ATTENDING)
                .count();
        return ResponseEntity.ok(Map.of(
                "attendees", attendees.stream().map(a -> Map.of(
                        "userId", a.getUser().getId(),
                        "fullName", a.getUser().getFullName(),
                        "status", a.getStatus().toString()
                )).toList(),
                "count", attendingCount
        ));
    }

    /**
     * Obtener calificaciones del evento
     * GET /api/v1/events/{id}/ratings
     */
    @GetMapping("/{id}/ratings")
    public ResponseEntity<Map<String, Object>> getRatings(@PathVariable UUID id) {
        var ratings = attendanceService.getEventRatings(id);
        double avg = ratings.stream().mapToInt(EventRatingEntity::getRating).average().orElse(0.0);
        return ResponseEntity.ok(Map.of(
                "ratings", ratings.stream().map(r -> Map.of(
                        "userId", r.getUser().getId(),
                        "fullName", r.getUser().getFullName(),
                        "rating", r.getRating(),
                        "comment", r.getComment() != null ? r.getComment() : ""
                )).toList(),
                "averageRating", avg,
                "count", ratings.size()
        ));
    }

    /**
     * Verificar si el usuario actual asiste a un evento
     * GET /api/v1/events/{id}/my-attendance
     */
    @GetMapping("/{id}/my-attendance")
    public ResponseEntity<Map<String, Object>> getMyAttendance(
            @PathVariable UUID id,
            HttpServletRequest httpRequest) {
        UUID userId = (UUID) httpRequest.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.ok(Map.of("attending", false, "rated", false));
        }
        boolean attending = attendanceService.isUserAttending(userId, id);
        boolean rated = attendanceService.hasUserRated(userId, id);
        return ResponseEntity.ok(Map.of("attending", attending, "rated", rated));
    }
}
