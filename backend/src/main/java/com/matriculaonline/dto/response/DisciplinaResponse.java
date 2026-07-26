package com.matriculaonline.dto.response;

import com.matriculaonline.domain.model.Disciplina;

import java.time.LocalDateTime;
import java.util.UUID;

public record DisciplinaResponse(
        UUID uuid,
        String nome,
        String descricao,
        Integer cargaHoraria,
        UUID cursoUuid,
        String cursoNome,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static DisciplinaResponse fromEntity(Disciplina disciplina) {
        return new DisciplinaResponse(
                disciplina.getUuid(),
                disciplina.getNome(),
                disciplina.getDescricao(),
                disciplina.getCargaHoraria(),
                disciplina.getCurso().getUuid(),
                disciplina.getCurso().getNome(),
                disciplina.getCreatedAt(),
                disciplina.getUpdatedAt()
        );
    }
}
