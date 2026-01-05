package dev.OsRapazes.BatPonto.service;

import dev.OsRapazes.BatPonto.dto.TimeEntry.CreateTimeEntryDto;
import dev.OsRapazes.BatPonto.dto.TimeEntry.TimeEntryReportResponseDto;
import dev.OsRapazes.BatPonto.dto.TimeEntry.TimeEntryResponseDto;
import dev.OsRapazes.BatPonto.entity.TimeEntryEntity;
import dev.OsRapazes.BatPonto.entity.UserEntity;
import dev.OsRapazes.BatPonto.entity.enums.EntryType;
import dev.OsRapazes.BatPonto.entity.enums.Role;
import dev.OsRapazes.BatPonto.exception.BusinessException;
import dev.OsRapazes.BatPonto.repository.TimeEntryRepository;
import dev.OsRapazes.BatPonto.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimeEntryService {

    private final TimeEntryRepository timeEntryRepository;
    private final UserRepository userRepository;

    public TimeEntryResponseDto registerEntry(String authenticatedEmail) {

        ZoneId serverZoneId = ZoneId.systemDefault();
        LocalDate today = LocalDate.now(serverZoneId);
        ZonedDateTime startOfDayZoned = today.atStartOfDay(serverZoneId);
        Instant startOfDay = startOfDayZoned.toInstant();

        UserEntity user = userRepository.findByEmail(authenticatedEmail.toLowerCase())
                .orElseThrow(() -> BusinessException.unprocessable("USER_NOT_FOUND", "Usuário autenticado não encontrado"));

        // Apenas funcionários e superadmins podem registrar ponto
        if (user.getRole() != Role.FUNCIONARIO && user.getRole() != Role.SUPERADMIN) {
            throw BusinessException.unprocessable(
                    "ROLE_NOT_ALLOWED",
                    "Somente funcionários podem registrar ponto"
            );
        }

        EntryType entryType;

        TimeEntryEntity lastEntry = timeEntryRepository
                .findTopByUser_IdAndEntryAtAfterOrderByEntryAtDesc(
                        user.getId(),
                        startOfDay)
                .orElse(null);

        Instant now = Instant.now();

        if (lastEntry != null) {
            long minDiffSeconds = 60;
            long secondsSinceLastEntry = Duration.between(lastEntry.getEntryAt(), now).getSeconds();

            if (secondsSinceLastEntry < minDiffSeconds) {
                return new TimeEntryResponseDto(
                        lastEntry.getId(),
                        user.getId(),
                        user.getName(),
                        lastEntry.getEntryType().name(),
                        lastEntry.getEntryAt()
                );
            }
        }

        if (lastEntry != null){
            EntryType lastEntryType = lastEntry.getEntryType();
            System.out.println(lastEntryType);
            if(lastEntryType.equals(EntryType.ENTRADA)){
                entryType = EntryType.SAIDA;
            } else {
                entryType = EntryType.ENTRADA;
            }
        } else {
            entryType = EntryType.ENTRADA;
        }

        TimeEntryEntity entry = new TimeEntryEntity();
        entry.setUser(user);
        entry.setEntryType(entryType);
        entry.setEntryAt(Instant.now());

        TimeEntryEntity saved = timeEntryRepository.save((entry));

        return new TimeEntryResponseDto(
                saved.getId(),
                user.getId(),
                user.getName(),
                saved.getEntryType().name(),
                saved.getEntryAt()
    );
    }

    public TimeEntryReportResponseDto getMyReport(String authenticatedEmail, LocalDate from, LocalDate to) {
        Instant start = from.atStartOfDay(java.time.ZoneOffset.UTC).toInstant();
        Instant end = to.plusDays(1).atStartOfDay(java.time.ZoneOffset.UTC).toInstant(); // exclusivo

        UserEntity user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new RuntimeException("Usuário autenticado não encontrado"));

        List<TimeEntryEntity> entries =
                timeEntryRepository.findByUser_IdAndEntryAtBetweenOrderByEntryAtAsc(
                        user.getId(), start, end
                );


        return new TimeEntryReportResponseDto(
                user.getId(),
                from,
                to,
                entries.stream()
                        .map(e -> new TimeEntryReportResponseDto.EntryItem(
                                e.getId(),
                                e.getEntryType().name(),
                                e.getEntryAt()
                        ))
                        .toList()
        );
    }
    public TimeEntryReportResponseDto getUserReport(UUID targetUserId, LocalDate from, LocalDate to, String authenticatedEmail) {
        UserEntity requester = userRepository.findByEmail(authenticatedEmail.toLowerCase())
                .orElseThrow(() -> BusinessException.unprocessable("USER_NOT_FOUND", "Usuário autenticado não encontrado"));

        // Apenas RH e Superadmin
        if (requester.getRole() != Role.RH && requester.getRole() != Role.SUPERADMIN) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "FORBIDDEN", "Apenas RH pode consultar outros usuários.");
        }

        UserEntity user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "Usuário não encontrado"));

        Instant start = from.atStartOfDay(java.time.ZoneOffset.UTC).toInstant();
        Instant end = to.plusDays(1).atStartOfDay(java.time.ZoneOffset.UTC).toInstant();

        List<TimeEntryEntity> entries =
                timeEntryRepository.findByUser_IdAndEntryAtBetweenOrderByEntryAtAsc(
                        user.getId(), start, end
                );

        return new TimeEntryReportResponseDto(
                user.getId(),
                from,
                to,
                entries.stream()
                        .map(e -> new TimeEntryReportResponseDto.EntryItem(
                                e.getId(),
                                e.getEntryType().name(),
                                e.getEntryAt()
                        ))
                        .toList()
        );
    }
}
