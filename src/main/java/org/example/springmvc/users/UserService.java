package org.example.springmvc.users;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public void create(CreateUserDTO dto) {
        if (repository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("Unable to create an account with this email");
        }

        String encryptedPassword = passwordEncoder.encode(dto.password());

        User user = UserMapper.fromDto(dto, UserRole.APP_USER, encryptedPassword);
        repository.save(user);
    }
}
