package org.jdc.tunja_evenfy.rest;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.jdc.tunja_evenfy.config.ApiPaths;
import jakarta.validation.Valid;
import org.jdc.tunja_evenfy.dto.UserCreateDTO;
import org.jdc.tunja_evenfy.dto.UserDTO;
import org.jdc.tunja_evenfy.dto.UserProfileDTO;
import org.jdc.tunja_evenfy.dto.UserProfileUpdateDTO;
import org.jdc.tunja_evenfy.exception.BadRequestException;
import org.jdc.tunja_evenfy.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.API_V1 + "/users")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class UserRest {

    private final UserService userService;

    @GetMapping
    public List<UserDTO> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public UserDTO findById(@PathVariable UUID id) {
        return userService.findById(id);
    }

    /**
     * Obtener perfil del usuario autenticado
     */
    @GetMapping("/profile/me")
    public UserProfileDTO getProfile(HttpServletRequest request) {
        UUID userId = (UUID) request.getAttribute("userId");
        return userService.getUserProfile(userId);
    }

    /**
     * Actualizar perfil del usuario autenticado
     */
    @PatchMapping("/profile/me")
    public UserProfileDTO updateProfile(@Valid @RequestBody UserProfileUpdateDTO dto, HttpServletRequest request) {
        UUID userId = (UUID) request.getAttribute("userId");
        return userService.updateProfile(userId, dto);
    }

    @PostMapping
    public UserDTO create(@Valid @RequestBody UserCreateDTO dto) {
        return userService.create(dto);
    }

    @PutMapping("/{id}")
    public UserDTO update(@PathVariable UUID id, @Valid @RequestBody UserCreateDTO dto) {
        return userService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        userService.delete(id);
    }

    /**
     * Admin: cambiar el rol de un usuario
     * PATCH /api/v1/users/{id}/role
     * Body: { "role": "ORGANIZER" }
     */
    @PatchMapping("/{id}/role")
    public ResponseEntity<Map<String, Object>> changeRole(
            @PathVariable UUID id,
            @RequestBody Map<String, String> body,
            HttpServletRequest request) {
        String callerRole = (String) request.getAttribute("role");
        if (!"ADMIN".equals(callerRole)) {
            throw new BadRequestException("Solo administradores pueden cambiar roles");
        }
        String newRole = body.get("role");
        if (newRole == null || (!newRole.equals("USER") && !newRole.equals("ORGANIZER") && !newRole.equals("ADMIN"))) {
            throw new BadRequestException("Rol inválido. Valores permitidos: USER, ORGANIZER, ADMIN");
        }
        UserDTO updated = userService.update(id, UserCreateDTO.builder().role(newRole).build());
        return ResponseEntity.ok(Map.of(
                "message", "Rol actualizado a " + newRole,
                "user", updated
        ));
    }
}
