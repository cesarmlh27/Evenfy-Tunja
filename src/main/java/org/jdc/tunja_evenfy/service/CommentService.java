package org.jdc.tunja_evenfy.service;

import org.jdc.tunja_evenfy.dto.CommentDTO;
import java.util.List;
import java.util.UUID;

public interface CommentService {

    List<CommentDTO> findAll();
    List<CommentDTO> findByEventId(UUID eventId);
    CommentDTO findById(UUID id);
    CommentDTO create(CommentDTO dto);
    CommentDTO update(UUID id, CommentDTO dto);
    void delete(UUID id);
    void deleteWithAuthorization(UUID id, UUID requesterId, String requesterRole);
}
