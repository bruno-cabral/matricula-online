package com.matriculaonline.dto.response;

import com.matriculaonline.domain.model.Curso;

import java.time.LocalDateTime;
import java.util.UUID;

public record CursoResponse(
        UUID uuid,
        String nome,
        String descricao,
        Integer cargaHoraria,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static CursoResponse fromEntity(Curso curso) {
        return new CursoResponse(
                curso.getUuid(),
                curso.getNome(),
                curso.getDescricao(),
                curso.getCargaHoraria(),
                curso.getCreatedAt(),
                curso.getUpdatedAt()
        );
    }
}
