package dev.OsRapazes.BatPonto.dto.Auth;

import dev.OsRapazes.BatPonto.entity.enums.Role;

import java.util.UUID;

public record LoginResponseDto(
        String accessToken,
        String tokenType,
        long expiresIn,
        UserSummary user
) {
    public record UserSummary(UUID id, String name, Role role) {}
}
