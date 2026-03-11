package org.jdc.tunja_evenfy.service.implement;

import lombok.RequiredArgsConstructor;
import org.jdc.tunja_evenfy.dto.UserCreateDTO;
import org.jdc.tunja_evenfy.dto.UserDTO;
import org.jdc.tunja_evenfy.entity.UserEntity;
import org.jdc.tunja_evenfy.exception.BadRequestException;
import org.jdc.tunja_evenfy.exception.NotFoundException;
import org.jdc.tunja_evenfy.repository.UserRepository;
import org.jdc.tunja_evenfy.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImplement implements UserService {

    private final UserRepository userRepository;

    private UserDTO toDTO(UserEntity entity) {
        return UserDTO.builder()
                .id(entity.getId())
                .fullName(entity.getFullName())
                .email(entity.getEmail())
                .role(entity.getRole())
                .build();
    }

    @Override
    public List<UserDTO> findAll() {
        return userRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public UserDTO findById(UUID id) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));
        return toDTO(entity);
    }

    @Override
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
                .password(dto.getPassword())
                .role(dto.getRole() != null ? dto.getRole() : "USER")
                .build();

        return toDTO(userRepository.save(entity));
    }

    @Override
    public UserDTO update(UUID id, UserCreateDTO dto) {
        if (dto == null) throw new BadRequestException("User body is required");

        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));

        if (dto.getFullName() != null) entity.setFullName(dto.getFullName().trim());
        if (dto.getEmail() != null) entity.setEmail(dto.getEmail().trim());
        if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
            entity.setPassword(dto.getPassword());
        }
        if (dto.getRole() != null) entity.setRole(dto.getRole());

        return toDTO(userRepository.save(entity));
    }

    @Override
    public void delete(UUID id) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));
        userRepository.delete(entity);
    }
}
