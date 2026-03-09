package org.example.springmvc.users.dto;

import org.example.springmvc.users.model.UserRole;

import java.util.UUID;

public record UserDTO(
        UUID id,
        String email,
        UserRole role
) {
}
