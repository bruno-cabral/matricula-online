package com.matriculaonline.service;

import com.matriculaonline.domain.exception.BusinessException;
import com.matriculaonline.domain.exception.DuplicateResourceException;
import com.matriculaonline.domain.exception.ResourceNotFoundException;
import com.matriculaonline.domain.model.Disciplina;
import com.matriculaonline.domain.model.StatusTurma;
import com.matriculaonline.domain.model.Turma;
import com.matriculaonline.dto.request.TurmaRequest;
import com.matriculaonline.dto.response.PageResponse;
import com.matriculaonline.dto.response.TurmaResponse;
import com.matriculaonline.repository.DisciplinaRepository;
import com.matriculaonline.repository.MatriculaRepository;
import com.matriculaonline.repository.TurmaRepository;
import com.matriculaonline.repository.TurmaSpecifications;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class TurmaService {

    private final TurmaRepository turmaRepository;
    private final DisciplinaRepository disciplinaRepository;
    private final MatriculaRepository matriculaRepository;

    public TurmaService(TurmaRepository turmaRepository,
                        DisciplinaRepository disciplinaRepository,
                        MatriculaRepository matriculaRepository) {
        this.turmaRepository = turmaRepository;
        this.disciplinaRepository = disciplinaRepository;
        this.matriculaRepository = matriculaRepository;
    }

    @Transactional
    public TurmaResponse criar(TurmaRequest request) {
        if (turmaRepository.existsByCodigo(request.codigo())) {
            throw new DuplicateResourceException("Já existe uma turma com o código: " + request.codigo());
        }

        Disciplina disciplina = disciplinaRepository.findByUuid(request.disciplinaUuid())
                .orElseThrow(() -> new ResourceNotFoundException("Disciplina", request.disciplinaUuid().toString()));

        Turma turma = new Turma();
        turma.setCodigo(request.codigo());
        turma.setDisciplina(disciplina);
        turma.setProfessor(request.professor());
        turma.setSemestre(request.semestre());
        turma.setVagas(request.vagas());
        turma.setStatus(request.status());
        return TurmaResponse.fromEntity(turmaRepository.save(turma));
    }

    @Transactional(readOnly = true)
    public PageResponse<TurmaResponse> listar(StatusTurma status, Boolean lotada, String q, Pageable pageable) {
        return PageResponse.from(
                turmaRepository.findAll(TurmaSpecifications.comFiltros(status, lotada, q), pageable),
                TurmaResponse::fromEntity
        );
    }

    @Transactional(readOnly = true)
    public TurmaResponse buscarPorUuid(UUID uuid) {
        Turma turma = turmaRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Turma", uuid.toString()));
        return TurmaResponse.fromEntity(turma);
    }

    @Transactional
    public TurmaResponse atualizar(UUID uuid, TurmaRequest request) {
        Turma turma = turmaRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Turma", uuid.toString()));

        Disciplina disciplina = disciplinaRepository.findByUuid(request.disciplinaUuid())
                .orElseThrow(() -> new ResourceNotFoundException("Disciplina", request.disciplinaUuid().toString()));

        turma.setCodigo(request.codigo());
        turma.setDisciplina(disciplina);
        turma.setProfessor(request.professor());
        turma.setSemestre(request.semestre());
        turma.setVagas(request.vagas());
        turma.setStatus(request.status());
        return TurmaResponse.fromEntity(turmaRepository.save(turma));
    }

    /**
     * Remove a turma. Idempotente: se o recurso já não existir, retorna sem erro (no-op).
     */
    @Transactional
    public void deletar(UUID uuid) {
        turmaRepository.findByUuid(uuid).ifPresent(turma -> {
            if (matriculaRepository.existsByTurmaUuid(uuid)) {
                throw new BusinessException("Turma possui matrículas vinculadas.");
            }
            turmaRepository.delete(turma);
        });
    }
}
