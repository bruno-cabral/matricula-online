package com.matriculaonline.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record MatriculaRequest(
        @NotNull(message = "UUID do aluno e obrigatorio")
        UUID alunoUuid,

        @NotNull(message = "UUID da turma e obrigatorio")
        UUID turmaUuid
) {}
