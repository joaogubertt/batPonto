package dev.OsRapazes.BatPonto.dto.TimeEntry;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record TimeEntryReportResponseDto(
        UUID id,
        LocalDate from,
        LocalDate to,
        List<EntryItem> entries
) {
    public record EntryItem(
            UUID id,
            String type,
            Instant timestamp
    ) {}
}