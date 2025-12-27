package dev.OsRapazes.BatPonto.dto.Auth;

import java.util.UUID;

public record LoginResponseDto(
        String accessToken,
        String tokenType,
        long expiresIn,
        UserSummary user
) {
    public record UserSummary(UUID id, String name, String role) {}
}
