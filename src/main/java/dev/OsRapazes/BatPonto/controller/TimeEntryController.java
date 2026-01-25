package dev.OsRapazes.BatPonto.controller;

import dev.OsRapazes.BatPonto.dto.TimeEntry.CreateTimeEntryDto;
import dev.OsRapazes.BatPonto.dto.TimeEntry.TimeEntryReportResponseDto;
import org.springframework.format.annotation.DateTimeFormat;
import dev.OsRapazes.BatPonto.dto.TimeEntry.TimeEntryResponseDto;
import dev.OsRapazes.BatPonto.service.TimeEntryPdfService;
import dev.OsRapazes.BatPonto.entity.TimeEntryEntity;
import dev.OsRapazes.BatPonto.service.TimeEntryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

import java.time.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/time-entries")
@RequiredArgsConstructor
public class TimeEntryController {

    public final TimeEntryService timeEntryService;
    @Autowired
    private TimeEntryPdfService timeEntryPdfService;

    @PostMapping
    public ResponseEntity<TimeEntryResponseDto> register(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        TimeEntryResponseDto response = timeEntryService.registerEntry(email);
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
            @PathVariable("userId") UUID userId,
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(timeEntryService.getUserReport(userId, from, to, email));
    }

    @GetMapping(value = "/my/pdf", produces = "application/pdf")
    public ResponseEntity<byte[]> myReportPdf(
            @RequestParam("from") LocalDate from,
            @RequestParam("to") LocalDate to
    ) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        byte[] pdf = timeEntryPdfService.generateMyReportPdf(email, from, to);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=meu-relatorio-ponto.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping(value = "/user/{userId}/pdf", produces = "application/pdf")
    public ResponseEntity<byte[]> userReportPdf(
            @PathVariable("userId") UUID userId,
            @RequestParam("from") LocalDate from,
            @RequestParam("to") LocalDate to
    ) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        byte[] pdf = timeEntryPdfService.generateUserReportPdf(userId, email, from, to);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=relatorio-ponto.pdf" + userId)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
