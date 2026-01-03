package dev.OsRapazes.BatPonto.dto.User;

import dev.OsRapazes.BatPonto.entity.enums.Role;

import java.util.UUID;

public record UserResponseDto(
        UUID id,
        String name,
        String email,
        Role role
) {}