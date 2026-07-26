package com.matriculaonline.service;

import com.matriculaonline.domain.exception.BusinessException;
import com.matriculaonline.domain.exception.ResourceNotFoundException;
import com.matriculaonline.domain.model.*;
import com.matriculaonline.dto.request.MatriculaRequest;
import com.matriculaonline.dto.response.MatriculaResponse;
import com.matriculaonline.dto.response.PageResponse;
import com.matriculaonline.repository.AlunoRepository;
import com.matriculaonline.repository.MatriculaRepository;
import com.matriculaonline.repository.TurmaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class MatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final AlunoRepository alunoRepository;
    private final TurmaRepository turmaRepository;

    public MatriculaService(MatriculaRepository matriculaRepository,
                            AlunoRepository alunoRepository,
                            TurmaRepository turmaRepository) {
        this.matriculaRepository = matriculaRepository;
        this.alunoRepository = alunoRepository;
        this.turmaRepository = turmaRepository;
    }

    /**
     * Cria uma nova matrícula com status PENDENTE.
     * Aplica RN01 (turma aberta), RN02 (limite de vagas) e RN03 (duplicata).
     */
    @Transactional
    public MatriculaResponse criar(MatriculaRequest request) {
        Aluno aluno = alunoRepository.findByUuid(request.alunoUuid())
                .orElseThrow(() -> new ResourceNotFoundException("Aluno", request.alunoUuid().toString()));

        Turma turma = turmaRepository.findByUuid(request.turmaUuid())
                .orElseThrow(() -> new ResourceNotFoundException("Turma", request.turmaUuid().toString()));

        // RN01: Matrícula apenas em turma aberta
        if (!turma.isAberta()) {
            throw new BusinessException("Turma nao esta aberta para matriculas");
        }

        // RN02: Limite de vagas
        if (!turma.temVagasDisponiveis()) {
            throw new BusinessException("Nao ha vagas disponiveis nesta turma");
        }

        // RN03: Matrícula duplicada (apenas PENDENTE ou CONFIRMADA bloqueiam)
        boolean matriculaAtiva = matriculaRepository.existsByAlunoIdAndTurmaIdAndStatusIn(
                aluno.getId(), turma.getId(),
                List.of(StatusMatricula.PENDENTE, StatusMatricula.CONFIRMADA)
        );
        if (matriculaAtiva) {
            throw new BusinessException("Aluno ja possui matricula nesta turma");
        }

        Matricula matricula = new Matricula();
        matricula.setAluno(aluno);
        matricula.setTurma(turma);
        matricula.setStatus(StatusMatricula.PENDENTE);

        return MatriculaResponse.fromEntity(matriculaRepository.save(matricula));
    }

    /**
     * Confirma uma matrícula. Aplica RN04 (fluxo de status) e RN05 (consumo de vaga).
     * Idempotente: confirmar matrícula já CONFIRMADA retorna sucesso sem alterar banco.
     */
    @Transactional
    public MatriculaResponse confirmar(UUID matriculaUuid) {
        Matricula matricula = matriculaRepository.findByUuid(matriculaUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Matricula", matriculaUuid.toString()));

        // Idempotência: já confirmada -> no-op
        if (matricula.isConfirmada()) {
            return MatriculaResponse.fromEntity(matricula);
        }

        // RN04: Transição inválida CANCELADA -> CONFIRMADA
        if (matricula.isCancelada()) {
            throw new BusinessException("Nao e permitido confirmar uma matricula cancelada");
        }

        // RN05: Verifica vagas antes de confirmar
        Turma turma = matricula.getTurma();
        if (!turma.temVagasDisponiveis()) {
            throw new BusinessException("Nao ha vagas disponiveis nesta turma");
        }

        // RN05: Consumo de vaga
        turma.incrementarVagasOcupadas();
        turmaRepository.save(turma);

        matricula.setStatus(StatusMatricula.CONFIRMADA);
        return MatriculaResponse.fromEntity(matriculaRepository.save(matricula));
    }

    /**
     * Cancela uma matrícula. Aplica RN04 (fluxo de status) e RN06 (liberação de vaga).
     * Idempotente: cancelar matrícula já CANCELADA retorna sucesso sem alterar banco.
     */
    @Transactional
    public MatriculaResponse cancelar(UUID matriculaUuid) {
        Matricula matricula = matriculaRepository.findByUuid(matriculaUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Matricula", matriculaUuid.toString()));

        // Idempotência: já cancelada -> no-op
        if (matricula.isCancelada()) {
            return MatriculaResponse.fromEntity(matricula);
        }

        // RN06: Se era CONFIRMADA, libera a vaga
        if (matricula.isConfirmada()) {
            Turma turma = matricula.getTurma();
            turma.decrementarVagasOcupadas();
            turmaRepository.save(turma);
        }

        // PENDENTE -> CANCELADA: sem alteração de vagas
        matricula.setStatus(StatusMatricula.CANCELADA);
        return MatriculaResponse.fromEntity(matriculaRepository.save(matricula));
    }

    @Transactional(readOnly = true)
    public MatriculaResponse buscarPorUuid(UUID uuid) {
        Matricula matricula = matriculaRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Matricula", uuid.toString()));
        return MatriculaResponse.fromEntity(matricula);
    }

    // RN07: Consultas de matrículas

    @Transactional(readOnly = true)
    public PageResponse<MatriculaResponse> listar(StatusMatricula status, Pageable pageable) {
        if (status != null) {
            return PageResponse.from(
                    matriculaRepository.findByStatus(status, pageable),
                    MatriculaResponse::fromEntity
            );
        }
        return PageResponse.from(
                matriculaRepository.findAll(pageable),
                MatriculaResponse::fromEntity
        );
    }

    @Transactional(readOnly = true)
    public PageResponse<MatriculaResponse> listarPorAluno(UUID alunoUuid, StatusMatricula status, Pageable pageable) {
        if (status != null) {
            return PageResponse.from(
                    matriculaRepository.findByAlunoUuidAndStatus(alunoUuid, status, pageable),
                    MatriculaResponse::fromEntity
            );
        }
        return PageResponse.from(
                matriculaRepository.findByAlunoUuid(alunoUuid, pageable),
                MatriculaResponse::fromEntity
        );
    }

    @Transactional(readOnly = true)
    public PageResponse<MatriculaResponse> listarPorTurma(UUID turmaUuid, StatusMatricula status, Pageable pageable) {
        if (status != null) {
            return PageResponse.from(
                    matriculaRepository.findByTurmaUuidAndStatus(turmaUuid, status, pageable),
                    MatriculaResponse::fromEntity
            );
        }
        return PageResponse.from(
                matriculaRepository.findByTurmaUuid(turmaUuid, pageable),
                MatriculaResponse::fromEntity
        );
    }
}
