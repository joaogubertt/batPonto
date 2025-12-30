package dev.OsRapazes.BatPonto.controller;

import dev.OsRapazes.BatPonto.dto.TimeEntry.CreateTimeEntryDto;
import dev.OsRapazes.BatPonto.dto.TimeEntry.TimeEntryResponseDto;
import dev.OsRapazes.BatPonto.entity.TimeEntryEntity;
import dev.OsRapazes.BatPonto.service.TimeEntryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/time-entries")
@RequiredArgsConstructor
public class TimeEntryController {

    public final TimeEntryService timeEntryService;

    @PostMapping
    public ResponseEntity<TimeEntryResponseDto> register(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        TimeEntryResponseDto response = timeEntryService.registerEntry(email);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping("/user/{userId}/filter")
    public ResponseEntity<List<TimeEntryResponseDto>> listByUserPerPeriod(
            @PathVariable("userId") UUID userId,
            @RequestParam("from") LocalDate from,
            @RequestParam("to") LocalDate to
    ){
        Instant startInstant = from.atStartOfDay(ZoneId.systemDefault()).toInstant();

        Instant endInstant = to.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant();

        List<TimeEntryResponseDto> entries = timeEntryService.listEntriesByUserPerPeriod(userId, startInstant, endInstant);

        return ResponseEntity.ok(entries);
    }
}
