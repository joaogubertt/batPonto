package dev.OsRapazes.BatPonto.service;

import dev.OsRapazes.BatPonto.dto.User.RegisterUserDto;
import dev.OsRapazes.BatPonto.dto.User.UserResponseDto;
import dev.OsRapazes.BatPonto.entity.UserEntity;
import dev.OsRapazes.BatPonto.entity.enums.Role;
import dev.OsRapazes.BatPonto.exception.BusinessException;
import dev.OsRapazes.BatPonto.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    //injecao de dependencias
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponseDto registerUser(RegisterUserDto dto, String authenticatedEmail) {

        UserEntity requester = userRepository.findByEmail(authenticatedEmail.toLowerCase())
                .orElseThrow(() ->  BusinessException.unprocessable("USER_NOT_FOUND", "Usuário autenticado não encontrado"));

        // RH e Superadmin podem criar usuários
        if (requester.getRole() != Role.RH && requester.getRole() != Role.SUPERADMIN) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "FORBIDDEN", "Apenas RH pode criar usuários.");
        }

        String email = dto.email().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw BusinessException.unprocessable("EMAIL_ALREADY_EXISTS", "E-mail já cadastrado");
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
