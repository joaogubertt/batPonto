package dev.OsRapazes.BatPonto.repository;

import dev.OsRapazes.BatPonto.entity.TimeEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TimeEntryRepository extends JpaRepository<TimeEntryEntity, UUID> {

    List<TimeEntryEntity> findByUserId(UUID userId);

    List<TimeEntryEntity> findByUser_IdAndEntryAtBetweenOrderByEntryAtAsc(UUID userId, Instant start, Instant end);

    Optional<TimeEntryEntity> findTopByUser_IdOrderByEntryAtDesc(UUID userId);

    List<TimeEntryEntity> findByUser_IdAndEntryAtBetween(UUID userId, Instant entryAtAfter, Instant entryAtBefore);
}
