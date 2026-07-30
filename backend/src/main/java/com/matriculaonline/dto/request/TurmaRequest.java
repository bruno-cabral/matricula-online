package com.matriculaonline.dto.request;

import com.matriculaonline.domain.model.StatusTurma;
import jakarta.validation.constraints.*;
import java.util.UUID;

public record TurmaRequest(
        @NotBlank(message = "Código é obrigatório")
        @Size(max = 50, message = "Código deve ter no máximo 50 caracteres")
        String codigo,

        @NotNull(message = "UUID da disciplina é obrigatório")
        UUID disciplinaUuid,

        @NotBlank(message = "Nome do professor é obrigatório")
        @Size(max = 255, message = "Nome do professor deve ter no máximo 255 caracteres")
        String professor,

        @NotBlank(message = "Semestre é obrigatório")
        @Size(max = 10, message = "Semestre deve ter no máximo 10 caracteres")
        String semestre,

        @NotNull(message = "Número de vagas é obrigatório")
        @Min(value = 1, message = "Número de vagas deve ser maior que zero")
        Integer vagas,

        @NotNull(message = "Status é obrigatório")
        StatusTurma status
) {}
