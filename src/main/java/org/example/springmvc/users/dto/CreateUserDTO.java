package org.example.springmvc.users.dto;

public record CreateUserDTO(
        String email,
        String password
) {
}
