package com.matriculaonline.dto.response;

import com.matriculaonline.domain.model.Aluno;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record AlunoResponse(
        UUID uuid,
        String nome,
        String email,
        String cpf,
        LocalDate dataNascimento,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static AlunoResponse fromEntity(Aluno aluno) {
        return new AlunoResponse(
                aluno.getUuid(),
                aluno.getNome(),
                aluno.getEmail(),
                aluno.getCpf(),
                aluno.getDataNascimento(),
                aluno.getCreatedAt(),
                aluno.getUpdatedAt()
        );
    }
}
