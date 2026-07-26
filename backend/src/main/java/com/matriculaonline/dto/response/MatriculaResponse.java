package com.matriculaonline.dto.response;

import com.matriculaonline.domain.model.Matricula;
import com.matriculaonline.domain.model.StatusMatricula;

import java.time.LocalDateTime;
import java.util.UUID;

public record MatriculaResponse(
        UUID uuid,
        UUID alunoUuid,
        String alunoNome,
        UUID turmaUuid,
        String turmaCodigo,
        StatusMatricula status,
        LocalDateTime dataMatricula,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static MatriculaResponse fromEntity(Matricula matricula) {
        return new MatriculaResponse(
                matricula.getUuid(),
                matricula.getAluno().getUuid(),
                matricula.getAluno().getNome(),
                matricula.getTurma().getUuid(),
                matricula.getTurma().getCodigo(),
                matricula.getStatus(),
                matricula.getDataMatricula(),
                matricula.getCreatedAt(),
                matricula.getUpdatedAt()
        );
    }
}
