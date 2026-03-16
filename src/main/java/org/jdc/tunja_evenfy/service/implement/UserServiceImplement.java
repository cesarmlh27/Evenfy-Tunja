package org.jdc.tunja_evenfy.service.implement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jdc.tunja_evenfy.dto.UserCreateDTO;
import org.jdc.tunja_evenfy.dto.UserDTO;
import org.jdc.tunja_evenfy.dto.UserProfileDTO;
import org.jdc.tunja_evenfy.dto.EventDTO;
import org.jdc.tunja_evenfy.entity.UserEntity;
import org.jdc.tunja_evenfy.exception.BadRequestException;
import org.jdc.tunja_evenfy.exception.NotFoundException;
import org.jdc.tunja_evenfy.repository.UserRepository;
import org.jdc.tunja_evenfy.repository.EventRepository;
import org.jdc.tunja_evenfy.repository.EventAttendeeRepository;
import org.jdc.tunja_evenfy.service.UserService;
import org.jdc.tunja_evenfy.service.EventService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImplement implements UserService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final EventAttendeeRepository attendeeRepository;
    private final EventService eventService;
    private final BCryptPasswordEncoder passwordEncoder;

    private UserDTO toDTO(UserEntity entity) {
        return UserDTO.builder()
                .id(entity.getId())
                .fullName(entity.getFullName())
                .email(entity.getEmail())
                .role(entity.getRole())
                .avatarUrl(entity.getAvatarUrl())
                .bio(entity.getBio())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> findAll() {
        return userRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO findById(UUID id) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));
        return toDTO(entity);
    }

    @Override
    @Transactional
    public UserDTO create(UserCreateDTO dto) {
        if (dto == null) throw new BadRequestException("User body is required");
        if (dto.getFullName() == null || dto.getFullName().trim().isEmpty())
            throw new BadRequestException("fullName is required");
        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty())
            throw new BadRequestException("email is required");
        if (dto.getPassword() == null || dto.getPassword().trim().isEmpty())
            throw new BadRequestException("password is required");

        UserEntity entity = UserEntity.builder()
                .fullName(dto.getFullName().trim())
                .email(dto.getEmail().trim())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(dto.getRole() != null ? dto.getRole() : "USER")
                .build();

        return toDTO(userRepository.save(entity));
    }

    @Override
    @Transactional
    public UserDTO update(UUID id, UserCreateDTO dto) {
        if (dto == null) throw new BadRequestException("User body is required");

        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));

        if (dto.getFullName() != null) entity.setFullName(dto.getFullName().trim());
        if (dto.getEmail() != null) entity.setEmail(dto.getEmail().trim());
        if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
            entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        if (dto.getRole() != null) entity.setRole(dto.getRole());

        return toDTO(userRepository.save(entity));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));
        userRepository.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileDTO getUserProfile(UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        // Eventos creados por el usuario
        var createdEvents = eventRepository.findByOrganizerIdAndIsActiveTrue(userId)
                .stream()
                .map(e -> eventService.findById(e.getId()))
                .toList();

        // Eventos a los que asiste
        var attendingEventIds = attendeeRepository.findByUserId(userId)
                .stream()
                .map(ea -> ea.getEvent().getId())
                .toList();

        var attendingEvents = attendingEventIds.stream()
                .map(eventService::findById)
                .toList();

        return UserProfileDTO.builder()
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .avatarUrl(user.getAvatarUrl())
                .bio(user.getBio())
                .createdEvents(createdEvents)
                .attendingEvents(attendingEvents)
                .totalEventsCreated(createdEvents.size())
                .totalEventsAttending(attendingEvents.size())
                .build();
    }

    @Override
    @Transactional
    public UserProfileDTO updateProfile(UUID userId, UserCreateDTO dto) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        if (dto.getFullName() != null && !dto.getFullName().trim().isEmpty()) {
            user.setFullName(dto.getFullName().trim());
        }
        if (dto.getAvatarUrl() != null) {
            user.setAvatarUrl(dto.getAvatarUrl().trim());
        }
        if (dto.getBio() != null) {
            user.setBio(dto.getBio().trim());
        }

        userRepository.save(user);
        return getUserProfile(userId);
    }
}
