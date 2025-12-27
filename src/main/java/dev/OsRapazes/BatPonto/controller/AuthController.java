package dev.OsRapazes.BatPonto.controller;

import dev.OsRapazes.BatPonto.dto.Auth.LoginRequestDto;
import dev.OsRapazes.BatPonto.dto.Auth.LoginResponseDto;
import dev.OsRapazes.BatPonto.entity.UserEntity;
import dev.OsRapazes.BatPonto.exception.BusinessException;
import dev.OsRapazes.BatPonto.repository.UserRepository;
import dev.OsRapazes.BatPonto.service.TokenService;
import dev.OsRapazes.BatPonto.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginRequestDto dto) {

        UserEntity user = userRepository.findByEmail(dto.email().toLowerCase())
                .orElseThrow(() -> BusinessException.unauthorized(
                        "INVALID_CREDENTIALS",
                        "Credenciais inválidas"
                ));

        if(!passwordEncoder.matches(dto.password(), user.getPassword())) {
            throw BusinessException.unauthorized(
                    "INVALID_CREDENTIALS",
                    "Credenciais inválidas"
            );
        }
        String token = tokenService.generateToken(user);

        return ResponseEntity.ok(new LoginResponseDto(
                token,
                "Bearer",
                tokenService.getExpiresInSeconds(),
                new LoginResponseDto.UserSummary(user.getId(), user.getName(), user.getRole())
        ));
    }
}

