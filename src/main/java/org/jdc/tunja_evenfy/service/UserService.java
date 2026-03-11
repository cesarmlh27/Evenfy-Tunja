package org.jdc.tunja_evenfy.service;

import org.jdc.tunja_evenfy.dto.UserCreateDTO;
import org.jdc.tunja_evenfy.dto.UserDTO;

import java.util.List;
import java.util.UUID;

public interface UserService {
    List<UserDTO> findAll();
    UserDTO findById(UUID id);
    UserDTO create(UserCreateDTO dto);
    UserDTO update(UUID id, UserCreateDTO dto);
    void delete(UUID id);
}
