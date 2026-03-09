package org.example.springmvc.users;

import org.example.springmvc.users.dto.CreateUserDTO;
import org.example.springmvc.users.dto.UserDTO;
import org.example.springmvc.users.model.User;
import org.example.springmvc.users.model.UserRole;

public class UserMapper {
    public static UserDTO toDto(User user) {
        return new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getRole()
        );
    }

    public static User fromDto(CreateUserDTO dto, UserRole role, String encryptedPassword) {
        return new User(
                dto.email(),
                encryptedPassword,
                role
        );
    }
}
