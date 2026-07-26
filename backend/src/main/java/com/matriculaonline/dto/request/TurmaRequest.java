package com.matriculaonline.dto.request;

import jakarta.validation.constraints.*;
import java.util.UUID;

public record TurmaRequest(
        @NotBlank(message = "Codigo e obrigatorio")
        @Size(max = 50, message = "Codigo deve ter no maximo 50 caracteres")
        String codigo,

        @NotNull(message = "UUID da disciplina e obrigatorio")
        UUID disciplinaUuid,

        @NotBlank(message = "Nome do professor e obrigatorio")
        @Size(max = 255, message = "Nome do professor deve ter no maximo 255 caracteres")
        String professor,

        @NotBlank(message = "Semestre e obrigatorio")
        @Size(max = 10, message = "Semestre deve ter no maximo 10 caracteres")
        String semestre,

        @NotNull(message = "Numero de vagas e obrigatorio")
        @Min(value = 1, message = "Numero de vagas deve ser maior que zero")
        Integer vagas
) {}
