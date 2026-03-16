package org.jdc.tunja_evenfy.service.implement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jdc.tunja_evenfy.dto.FavoriteDTO;
import org.jdc.tunja_evenfy.entity.EventEntity;
import org.jdc.tunja_evenfy.entity.FavoriteEntity;
import org.jdc.tunja_evenfy.entity.UserEntity;
import org.jdc.tunja_evenfy.exception.BadRequestException;
import org.jdc.tunja_evenfy.exception.NotFoundException;
import org.jdc.tunja_evenfy.repository.EventRepository;
import org.jdc.tunja_evenfy.repository.FavoriteRepository;
import org.jdc.tunja_evenfy.repository.UserRepository;
import org.jdc.tunja_evenfy.service.FavoriteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteServiceImplement implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    private FavoriteDTO toDTO(FavoriteEntity e) {
        return FavoriteDTO.builder()
                .id(e.getId())
                .userId(e.getUser().getId())
                .eventId(e.getEvent().getId())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FavoriteDTO> findAll() {
        return favoriteRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<FavoriteDTO> findByUserId(UUID userId) {
        return favoriteRepository.findByUserId(userId).stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public FavoriteDTO findById(UUID id) {
        FavoriteEntity f = favoriteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Favorite not found: " + id));
        return toDTO(f);
    }

    @Override
    @Transactional
    public FavoriteDTO create(FavoriteDTO dto) {
        if (dto == null || dto.getUserId() == null) {
            throw new BadRequestException("userId is required");
        }
        if (dto.getEventId() == null) {
            throw new BadRequestException("eventId is required");
        }

        // Toggle: si ya existe, eliminar; si no, crear
        var existing = favoriteRepository.findByUserIdAndEventId(dto.getUserId(), dto.getEventId());
        if (existing.isPresent()) {
            favoriteRepository.delete(existing.get());
            log.info("Favorito removido: usuario {} evento {}", dto.getUserId(), dto.getEventId());
            return null;
        }

        UserEntity user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found: " + dto.getUserId()));

        EventEntity event = eventRepository.findById(dto.getEventId())
                .orElseThrow(() -> new NotFoundException("Event not found: " + dto.getEventId()));

        FavoriteEntity entity = FavoriteEntity.builder()
                .event(event)
                .user(user)
                .build();

        log.info("Favorito agregado: usuario {} evento {}", dto.getUserId(), dto.getEventId());
        return toDTO(favoriteRepository.save(entity));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        FavoriteEntity f = favoriteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Favorite not found: " + id));
        favoriteRepository.delete(f);
    }
}
