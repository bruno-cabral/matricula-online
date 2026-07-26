package com.matriculaonline.dto.response;

import com.matriculaonline.domain.model.Turma;
import com.matriculaonline.domain.model.StatusTurma;

import java.time.LocalDateTime;
import java.util.UUID;

public record TurmaResponse(
        UUID uuid,
        String codigo,
        UUID disciplinaUuid,
        String disciplinaNome,
        String professor,
        String semestre,
        Integer vagas,
        Integer vagasOcupadas,
        StatusTurma status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static TurmaResponse fromEntity(Turma turma) {
        return new TurmaResponse(
                turma.getUuid(),
                turma.getCodigo(),
                turma.getDisciplina().getUuid(),
                turma.getDisciplina().getNome(),
                turma.getProfessor(),
                turma.getSemestre(),
                turma.getVagas(),
                turma.getVagasOcupadas(),
                turma.getStatus(),
                turma.getCreatedAt(),
                turma.getUpdatedAt()
        );
    }
}
