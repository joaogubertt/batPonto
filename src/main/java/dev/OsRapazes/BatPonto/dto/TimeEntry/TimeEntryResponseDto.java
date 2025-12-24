package dev.OsRapazes.BatPonto.dto.TimeEntry;

import java.time.Instant;
import java.util.UUID;

public record TimeEntryResponseDto(
        UUID id,
        UUID userId,
        String userName,
        String entryType,
        Instant timestamp
) {}