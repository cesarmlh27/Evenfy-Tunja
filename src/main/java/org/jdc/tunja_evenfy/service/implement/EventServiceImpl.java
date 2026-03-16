package org.jdc.tunja_evenfy.service.implement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jdc.tunja_evenfy.dto.EventDTO;
import org.jdc.tunja_evenfy.entity.CategoryEntity;
import org.jdc.tunja_evenfy.entity.EventEntity;
import org.jdc.tunja_evenfy.entity.LocationEntity;
import org.jdc.tunja_evenfy.entity.UserEntity;
import org.jdc.tunja_evenfy.exception.BadRequestException;
import org.jdc.tunja_evenfy.exception.NotFoundException;
import org.jdc.tunja_evenfy.repository.CategoryRepository;
import org.jdc.tunja_evenfy.repository.EventRepository;
import org.jdc.tunja_evenfy.repository.LocationRepository;
import org.jdc.tunja_evenfy.repository.UserRepository;
import org.jdc.tunja_evenfy.service.EventService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<EventDTO> findAll() {
        return eventRepository.findByIsActiveTrue().stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventDTO> findAllPaged(Pageable pageable) {
        return eventRepository.findByIsActiveTrue(pageable).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventDTO> search(String title, UUID categoryId, UUID locationId,
                                  LocalDateTime from, LocalDateTime to, Pageable pageable) {
        // Some environments have schema drift on `events.title` that breaks SQL LOWER() usage.
        // We apply safe in-memory filters over active events to keep search fully functional.
        String normalizedTitle = title == null ? null : title.trim().toLowerCase();

        List<EventEntity> filtered = eventRepository.findByIsActiveTrue().stream()
            .filter(e -> normalizedTitle == null || normalizedTitle.isEmpty()
                || (e.getTitle() != null && e.getTitle().toLowerCase().contains(normalizedTitle)))
            .filter(e -> categoryId == null || (e.getCategory() != null && categoryId.equals(e.getCategory().getId())))
            .filter(e -> locationId == null || (e.getLocation() != null && locationId.equals(e.getLocation().getId())))
            .filter(e -> from == null || (e.getEventDate() != null && !e.getEventDate().isBefore(from)))
            .filter(e -> to == null || (e.getEventDate() != null && !e.getEventDate().isAfter(to)))
            .sorted(Comparator.comparing(EventEntity::getEventDate, Comparator.nullsLast(Comparator.naturalOrder())))
            .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filtered.size());
        List<EventDTO> pageContent = start >= filtered.size()
            ? List.of()
            : filtered.subList(start, end).stream().map(this::toDTO).toList();

        return new PageImpl<>(pageContent, pageable, filtered.size());
    }

    @Override
    @Transactional(readOnly = true)
    public EventDTO findById(UUID id) {
        EventEntity event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Evento no encontrado: " + id));
        return toDTO(event);
    }

    @Override
    @Transactional
    public EventDTO create(EventDTO dto) {
        if (dto == null || dto.getTitle() == null || dto.getTitle().trim().isEmpty())
            throw new BadRequestException("El título del evento es requerido");

        if (dto.getEventDate() == null)
            throw new BadRequestException("La fecha del evento es requerida");

        EventEntity event = new EventEntity();
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setEventDate(dto.getEventDate());
        event.setImageUrl(dto.getImageUrl());
        event.setMaxCapacity(dto.getMaxCapacity());
        event.setLocationText(trimToNull(dto.getLocation()));
        event.setIsFree(dto.getIsFree() == null ? true : dto.getIsFree());
        event.setTicketPurchaseUrl(trimToNull(dto.getTicketPurchaseUrl()));
        event.setInfoUrl(trimToNull(dto.getInfoUrl()));
        event.setIsActive(true);

        // Resolver categoría por ID
        if (dto.getCategoryId() != null) {
            CategoryEntity category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Categoría no encontrada"));
            event.setCategory(category);
        }

        // Resolver ubicación por ID
        if (dto.getLocationId() != null) {
            LocationEntity location = locationRepository.findById(dto.getLocationId())
                    .orElseThrow(() -> new NotFoundException("Ubicación no encontrada"));
            event.setLocation(location);
        }

        // Resolver organizador
        if (dto.getCreatedBy() != null) {
            UserEntity organizer = userRepository.findById(dto.getCreatedBy())
                    .orElseThrow(() -> new NotFoundException("Organizador no encontrado"));
            event.setOrganizer(organizer);
        }

        log.info("Creando evento: {}", dto.getTitle());
        EventEntity saved = eventRepository.save(event);
        return toDTO(saved);
    }

    @Override
    @Transactional
    public EventDTO update(UUID id, EventDTO dto) {
        EventEntity event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Evento no encontrado: " + id));

        if (dto.getTitle() != null) event.setTitle(dto.getTitle());
        if (dto.getDescription() != null) event.setDescription(dto.getDescription());
        if (dto.getEventDate() != null) event.setEventDate(dto.getEventDate());
        if (dto.getImageUrl() != null) event.setImageUrl(dto.getImageUrl());
        if (dto.getMaxCapacity() != null) event.setMaxCapacity(dto.getMaxCapacity());
        if (dto.getLocation() != null) event.setLocationText(trimToNull(dto.getLocation()));
        if (dto.getIsFree() != null) event.setIsFree(dto.getIsFree());
        if (dto.getTicketPurchaseUrl() != null) event.setTicketPurchaseUrl(trimToNull(dto.getTicketPurchaseUrl()));
        if (dto.getInfoUrl() != null) event.setInfoUrl(trimToNull(dto.getInfoUrl()));

        if (dto.getCategoryId() != null) {
            CategoryEntity category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Categoría no encontrada"));
            event.setCategory(category);
        }
        if (dto.getLocationId() != null) {
            LocationEntity location = locationRepository.findById(dto.getLocationId())
                    .orElseThrow(() -> new NotFoundException("Ubicación no encontrada"));
            event.setLocation(location);
        }

        log.info("Actualizando evento: {}", id);
        EventEntity saved = eventRepository.save(event);
        return toDTO(saved);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        EventEntity event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Evento no encontrado: " + id));
        // Soft delete
        event.setIsActive(false);
        eventRepository.save(event);
        log.info("Evento desactivado (soft delete): {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public int getAttendeesCount(UUID eventId) {
        EventEntity event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Evento no encontrado"));
        return event.getAttendeeCount().intValue();
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAverageRating(UUID eventId) {
        EventEntity event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Evento no encontrado"));
        return event.getAverageRating();
    }

    @Override
    public void rateEvent(UUID eventId, UUID userId, int rating) {
        throw new UnsupportedOperationException("Use EventAttendanceService.rateEvent()");
    }

    private EventDTO toDTO(EventEntity event) {
        EventDTO dto = new EventDTO();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setEventDate(event.getEventDate());
        String location = event.getLocationText();
        if (location == null || location.isBlank()) {
            location = event.getLocation() != null ? event.getLocation().getPlaceName() : "";
        }
        dto.setLocation(location);
        dto.setCategory(event.getCategory() != null ? event.getCategory().getName() : "");
        dto.setCategoryId(event.getCategory() != null ? event.getCategory().getId() : null);
        dto.setLocationId(event.getLocation() != null ? event.getLocation().getId() : null);
        dto.setCreatedBy(event.getOrganizer() != null ? event.getOrganizer().getId() : null);
        dto.setOrganizerName(event.getOrganizer() != null ? event.getOrganizer().getFullName() : "");
        dto.setImageUrl(event.getImageUrl());
        dto.setIsFree(event.getIsFree());
        dto.setTicketPurchaseUrl(event.getTicketPurchaseUrl());
        dto.setInfoUrl(event.getInfoUrl());
        dto.setMaxCapacity(event.getMaxCapacity());
        dto.setIsActive(event.getIsActive());
        dto.setAttendeeCount(event.getAttendeeCount());
        dto.setAverageRating(event.getAverageRating());
        dto.setRatingCount(event.getRatings() != null ? event.getRatings().size() : 0);
        dto.setCreatedAt(event.getCreatedAt());
        dto.setUpdatedAt(event.getUpdatedAt());
        return dto;
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}
