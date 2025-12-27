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

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimeEntryService {

    private final TimeEntryRepository timeEntryRepository;
    private final UserRepository userRepository;

    public TimeEntryResponseDto registerEntry(CreateTimeEntryDto dto, String authenticatedEmail) {
        UserEntity user = userRepository.findByEmail(authenticatedEmail.toLowerCase())
                .orElseThrow(() -> BusinessException.unprocessable("USER_NOT_FOUND", "Usuário autenticado não encontrado"));

        if (user.getRole() != Role.FUNCIONARIO) {
            throw BusinessException.unprocessable(
                    "ROLE_NOT_ALLOWED",
                    "Somente funcionários podem registrar ponto"
            );
        }

        EntryType newType = EntryType.valueOf(dto.entryType().toUpperCase());

        TimeEntryEntity lastEntry = timeEntryRepository
                .findTopByUser_IdOrderByEntryAtDesc(user.getId())
                .orElse(null);

        if (lastEntry == null && newType != EntryType.ENTRADA) {
            throw BusinessException.unprocessable(
                    "INVALID_FIRST_ENTRY",
                    "O primeiro registro deve ser ENTRADA"
            );
        }

        if (lastEntry != null && lastEntry.getEntryType() == newType) {
            throw BusinessException.unprocessable(
                    "INVALID_SEQUENCE",
                    "Não é permitido registrar " + newType + " duas vezes seguidas"
            );
        }

        TimeEntryEntity entry = new TimeEntryEntity();
        entry.setUser(user);
        entry.setEntryType(newType);
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
