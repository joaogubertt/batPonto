package dev.OsRapazes.BatPonto.service;

import dev.OsRapazes.BatPonto.dto.User.RegisterUserDto;
import dev.OsRapazes.BatPonto.dto.User.UserResponseDto;
import dev.OsRapazes.BatPonto.entity.UserEntity;
import dev.OsRapazes.BatPonto.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    //injecao de dependencias
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponseDto registerUser(RegisterUserDto dto) {
        if(userRepository.existsByEmail(dto.email())){
            throw new RuntimeException("E-mail já cadastrado");
        }

        UserEntity newUser = new UserEntity();
        newUser.setName(dto.name());
        newUser.setEmail(dto.email());
        newUser.setRole(dto.role());

        newUser.setPasswordHash(passwordEncoder.encode(dto.password()));

        UserEntity savedUser = userRepository.save(newUser);

        return new UserResponseDto(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole()
        );
    }

    public UserEntity findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado pelo id fornecido"));
    }

}
