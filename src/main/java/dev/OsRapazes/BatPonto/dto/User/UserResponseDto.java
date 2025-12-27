package dev.OsRapazes.BatPonto.dto.User;

import java.util.UUID;

public record UserResponseDto(
        UUID id,
        String name,
        String email,
        String role
) {}