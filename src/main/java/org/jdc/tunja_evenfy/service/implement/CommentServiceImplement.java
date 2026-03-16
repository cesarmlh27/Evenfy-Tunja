package org.jdc.tunja_evenfy.service.implement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jdc.tunja_evenfy.dto.CommentDTO;
import org.jdc.tunja_evenfy.entity.CommentEntity;
import org.jdc.tunja_evenfy.entity.EventEntity;
import org.jdc.tunja_evenfy.entity.UserEntity;
import org.jdc.tunja_evenfy.exception.BadRequestException;
import org.jdc.tunja_evenfy.exception.NotFoundException;
import org.jdc.tunja_evenfy.repository.CommentRepository;
import org.jdc.tunja_evenfy.repository.EventRepository;
import org.jdc.tunja_evenfy.repository.UserRepository;
import org.jdc.tunja_evenfy.service.CommentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImplement implements CommentService {

    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    private CommentDTO toDTO(CommentEntity e) {
        return CommentDTO.builder()
                .id(e.getId())
                .content(e.getContent())
                .userId(e.getUser().getId())
                .userName(e.getUser().getFullName())
                .userAvatar(e.getUser().getAvatarUrl())
                .eventId(e.getEvent().getId())
                .createdAt(e.getCreatedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDTO> findAll() {
        return commentRepository.findAll().stream().map(this::toDTO).toList();
    }

    public List<CommentDTO> findByEventId(UUID eventId) {
        return commentRepository.findByEventIdOrderByCreatedAtDesc(eventId).stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CommentDTO findById(UUID id) {
        CommentEntity c = commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Comment not found: " + id));
        return toDTO(c);
    }

    @Override
    @Transactional
    public CommentDTO create(CommentDTO dto) {
        if (dto == null || dto.getContent() == null || dto.getContent().trim().isEmpty()) {
            throw new BadRequestException("Comment content is required");
        }
        if (dto.getEventId() == null) {
            throw new BadRequestException("eventId is required");
        }
        if (dto.getUserId() == null) {
            throw new BadRequestException("userId is required");
        }

        EventEntity event = eventRepository.findById(dto.getEventId())
                .orElseThrow(() -> new NotFoundException("Event not found: " + dto.getEventId()));

        UserEntity user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found: " + dto.getUserId()));

        CommentEntity entity = CommentEntity.builder()
                .content(dto.getContent().trim())
                .event(event)
                .user(user)
                .build();

        log.info("Comentario creado por usuario {} en evento {}", dto.getUserId(), dto.getEventId());
        return toDTO(commentRepository.save(entity));
    }

    @Override
    @Transactional
    public CommentDTO update(UUID id, CommentDTO dto) {
        if (dto == null || dto.getContent() == null || dto.getContent().trim().isEmpty()) {
            throw new BadRequestException("Comment content is required");
        }

        CommentEntity c = commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Comment not found: " + id));

        c.setContent(dto.getContent().trim());

        return toDTO(commentRepository.save(c));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        CommentEntity c = commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Comment not found: " + id));
        commentRepository.delete(c);
    }

    @Override
    @Transactional
    public void deleteWithAuthorization(UUID id, UUID requesterId, String requesterRole) {
        if (requesterId == null) {
            throw new BadRequestException("Usuario no autenticado");
        }

        CommentEntity c = commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Comment not found: " + id));

        boolean isAdmin = "ADMIN".equals(requesterRole);
        boolean isCommentAuthor = c.getUser() != null && requesterId.equals(c.getUser().getId());
        boolean isEventCreator = c.getEvent() != null
                && c.getEvent().getOrganizer() != null
                && requesterId.equals(c.getEvent().getOrganizer().getId());

        if (!isAdmin && !isCommentAuthor && !isEventCreator) {
            throw new BadRequestException("No tienes permisos para eliminar este comentario");
        }

        commentRepository.delete(c);
    }
}
