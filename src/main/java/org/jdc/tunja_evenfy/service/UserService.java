package org.jdc.tunja_evenfy.service;

import org.jdc.tunja_evenfy.dto.UserCreateDTO;
import org.jdc.tunja_evenfy.dto.UserDTO;
import org.jdc.tunja_evenfy.dto.UserProfileDTO;

import java.util.List;
import java.util.UUID;

public interface UserService {
    List<UserDTO> findAll();
    UserDTO findById(UUID id);
    UserDTO create(UserCreateDTO dto);
    UserDTO update(UUID id, UserCreateDTO dto);
    void delete(UUID id);
    
    // Nuevo método para obtener perfil completo
    UserProfileDTO getUserProfile(UUID userId);
    
    // Actualizar perfil del usuario autenticado
    UserProfileDTO updateProfile(UUID userId, UserCreateDTO dto);
}
