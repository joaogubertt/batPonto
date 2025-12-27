package dev.OsRapazes.BatPonto.dto.TimeEntry;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

// Usado para BATER O PONTO
public record CreateTimeEntryDto(
        @NotNull(message = "O ID do usuário é obrigatório") // Remover, pois precisa pegar do token
        UUID userId,

        @NotBlank(message = "O tipo de entrada é obrigatório")
        String entryType
) {}