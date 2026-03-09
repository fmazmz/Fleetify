package org.example.springmvc.users;

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
