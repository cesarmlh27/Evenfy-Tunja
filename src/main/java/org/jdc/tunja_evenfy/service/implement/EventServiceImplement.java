package org.jdc.tunja_evenfy.service.implement;

import lombok.RequiredArgsConstructor;
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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventServiceImplement implements EventService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;

    @Override
    public List<EventDTO> findAll() {
        return eventRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public EventDTO findById(UUID id) {
        EventEntity e = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event not found: " + id));
        return toDTO(e);
    }

    @Override
    public EventDTO create(EventDTO dto) {
        if (dto == null) throw new BadRequestException("Event body is required");
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty())
            throw new BadRequestException("title is required");
        if (dto.getEventDate() == null)
            throw new BadRequestException("eventDate is required");
        if (dto.getCategoryId() == null)
            throw new BadRequestException("categoryId is required");
        if (dto.getLocationId() == null)
            throw new BadRequestException("locationId is required");
        if (dto.getOrganizerId() == null)
            throw new BadRequestException("organizerId is required");

        CategoryEntity category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Category not found: " + dto.getCategoryId()));

        LocationEntity location = locationRepository.findById(dto.getLocationId())
                .orElseThrow(() -> new NotFoundException("Location not found: " + dto.getLocationId()));

        UserEntity organizer = userRepository.findById(dto.getOrganizerId())
                .orElseThrow(() -> new NotFoundException("Organizer (User) not found: " + dto.getOrganizerId()));

        EventEntity event = EventEntity.builder()
                .title(dto.getTitle().trim())
                .description(dto.getDescription())
                .eventDate(dto.getEventDate())
                .category(category)
                .location(location)
                .organizer(organizer)
                .build();

        return toDTO(eventRepository.save(event));
    }

    @Override
    public EventDTO update(UUID id, EventDTO dto) {
        if (dto == null) throw new BadRequestException("Event body is required");

        EventEntity event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event not found: " + id));

        // Relaciones (si llegan)
        if (dto.getCategoryId() != null) {
            CategoryEntity category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category not found: " + dto.getCategoryId()));
            event.setCategory(category);
        }

        if (dto.getLocationId() != null) {
            LocationEntity location = locationRepository.findById(dto.getLocationId())
                    .orElseThrow(() -> new NotFoundException("Location not found: " + dto.getLocationId()));
            event.setLocation(location);
        }

        if (dto.getOrganizerId() != null) {
            UserEntity organizer = userRepository.findById(dto.getOrganizerId())
                    .orElseThrow(() -> new NotFoundException("Organizer (User) not found: " + dto.getOrganizerId()));
            event.setOrganizer(organizer);
        }

        // Campos simples (solo si llegan)
        if (dto.getTitle() != null) event.setTitle(dto.getTitle().trim());
        if (dto.getDescription() != null) event.setDescription(dto.getDescription());
        if (dto.getEventDate() != null) event.setEventDate(dto.getEventDate());

        return toDTO(eventRepository.save(event));
    }

    @Override
    public void delete(UUID id) {
        EventEntity event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event not found: " + id));
        eventRepository.delete(event);
    }

    // ====================
    // MAPPER
    // ====================
    private EventDTO toDTO(EventEntity e) {
        EventDTO dto = new EventDTO();

        dto.setId(e.getId());
        dto.setTitle(e.getTitle());
        dto.setDescription(e.getDescription());
        dto.setEventDate(e.getEventDate());

        dto.setCategoryId(e.getCategory().getId());
        dto.setLocationId(e.getLocation().getId());
        dto.setOrganizerId(e.getOrganizer().getId());

        dto.setCreatedAt(e.getCreatedAt());
        dto.setUpdatedAt(e.getUpdatedAt());

        return dto;
    }
}
