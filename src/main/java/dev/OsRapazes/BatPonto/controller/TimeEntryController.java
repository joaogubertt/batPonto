package dev.OsRapazes.BatPonto.controller;

import dev.OsRapazes.BatPonto.dto.TimeEntry.CreateTimeEntryDto;
import dev.OsRapazes.BatPonto.dto.TimeEntry.TimeEntryReportResponseDto;
import dev.OsRapazes.BatPonto.dto.TimeEntry.TimeEntryResponseDto;
import dev.OsRapazes.BatPonto.service.TimeEntryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/time-entries")
@RequiredArgsConstructor
public class TimeEntryController {

    public final TimeEntryService timeEntryService;

    @PostMapping
    public ResponseEntity<TimeEntryResponseDto> register(@RequestBody @Valid CreateTimeEntryDto dto){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        TimeEntryResponseDto response = timeEntryService.registerEntry(dto, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    // GET /api/time-entries/my?from=YYYY-MM-DD&to=YYYY-MM-DD
    @GetMapping("/my")
    public ResponseEntity<TimeEntryReportResponseDto> myReport(
            @RequestParam("from") LocalDate from,
            @RequestParam("to") LocalDate to
    ) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(timeEntryService.getMyReport(email, from, to));
    }

    // GET /api/time-entries/user/{userId}?from=YYYY-MM-DD&to=YYYY-MM-DD
    @GetMapping("/user/{userId}")
    public ResponseEntity<TimeEntryReportResponseDto> userReport(
            @PathVariable UUID userId,
            @RequestParam("from") LocalDate from,
            @RequestParam("to") LocalDate to
    ) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(timeEntryService.getUserReport(userId, from, to, email));
    }
}
