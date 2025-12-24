package dev.OsRapazes.BatPonto.entity;

import jakarta.persistence.Entity;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Entity
@Data
public class User {
    private UUID id;
    private String name;
    private String password;
    private String passwordHash;
    private String role;
    private Instant createdAt;
    private Instant updatedAt;
}
