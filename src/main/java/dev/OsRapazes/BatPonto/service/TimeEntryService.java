package dev.OsRapazes.BatPonto.service;

import dev.OsRapazes.BatPonto.dto.TimeEntry.CreateTimeEntryDto;
import dev.OsRapazes.BatPonto.dto.TimeEntry.TimeEntryResponseDto;
import dev.OsRapazes.BatPonto.entity.TimeEntryEntity;
import dev.OsRapazes.BatPonto.entity.UserEntity;
import dev.OsRapazes.BatPonto.entity.enums.EntryType;
import dev.OsRapazes.BatPonto.entity.enums.Role;
import dev.OsRapazes.BatPonto.exception.BusinessException;
import dev.OsRapazes.BatPonto.repository.TimeEntryRepository;
import dev.OsRapazes.BatPonto.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
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

        if (user.getRole() != Role.FUNCIONARIO) {
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

    public List<TimeEntryResponseDto> listEntriesByUserPerPeriod(UUID userId, Instant start, Instant end){
        List<TimeEntryEntity> entries = timeEntryRepository.findByUser_IdAndEntryAtBetween(userId, start, end);

        return entries.stream()
                .map(entity -> new TimeEntryResponseDto(
                        entity.getId(),
                        entity.getUser().getId(),
                        entity.getUser().getName(),
                        entity.getEntryType().name(),
                        entity.getEntryAt()
                ))
                .collect(Collectors.toList());
    }
}
