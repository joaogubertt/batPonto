package dev.OsRapazes.BatPonto.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tb_time_entries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeEntryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false, name = "entry_type")
    private String entryType; // Fazer com Enum tb

    @CreationTimestamp
    @Column(nullable = false)
    private Instant timestamp;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant created_at;

    @UpdateTimestamp
    @Column(name = "update_at")
    private Instant updatedAt;
}
