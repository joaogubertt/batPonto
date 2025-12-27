package dev.OsRapazes.BatPonto.service;

import dev.OsRapazes.BatPonto.dto.TimeEntry.CreateTimeEntryDto;
import dev.OsRapazes.BatPonto.dto.TimeEntry.TimeEntryResponseDto;
import dev.OsRapazes.BatPonto.entity.TimeEntryEntity;
import dev.OsRapazes.BatPonto.entity.UserEntity;
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

    public void registerEntry(CreateTimeEntryDto dto){
        UserEntity user = userRepository.findById(dto.userId())
                .orElseThrow(()-> new RuntimeException("Usuário não encontrado pelo ID: " + dto.userId()));

        TimeEntryEntity entry = new TimeEntryEntity();
        entry.setUser(user);
        entry.setEntryType(dto.entryType());

        timeEntryRepository.save((entry));
    }

    public List<TimeEntryResponseDto> listEntriesByUserPerPeriod(UUID userId, Instant start, Instant end){
        List<TimeEntryEntity> entries = timeEntryRepository.findByUserIdAndEntryAt(userId, start, end);

        return entries.stream()
                .map(entity -> new TimeEntryResponseDto(
                        entity.getId(),
                        entity.getUser().getId(),
                        entity.getUser().getName(),
                        entity.getEntryType(),
                        entity.getEntryAt()
                ))
                .collect(Collectors.toList());
    }
}
