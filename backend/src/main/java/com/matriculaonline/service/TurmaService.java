package com.matriculaonline.service;

import com.matriculaonline.domain.exception.DuplicateResourceException;
import com.matriculaonline.domain.exception.ResourceNotFoundException;
import com.matriculaonline.domain.model.Disciplina;
import com.matriculaonline.domain.model.Turma;
import com.matriculaonline.dto.request.TurmaRequest;
import com.matriculaonline.dto.response.PageResponse;
import com.matriculaonline.dto.response.TurmaResponse;
import com.matriculaonline.repository.DisciplinaRepository;
import com.matriculaonline.repository.TurmaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class TurmaService {

    private final TurmaRepository turmaRepository;
    private final DisciplinaRepository disciplinaRepository;

    public TurmaService(TurmaRepository turmaRepository, DisciplinaRepository disciplinaRepository) {
        this.turmaRepository = turmaRepository;
        this.disciplinaRepository = disciplinaRepository;
    }

    @Transactional
    public TurmaResponse criar(TurmaRequest request) {
        if (turmaRepository.existsByCodigo(request.codigo())) {
            throw new DuplicateResourceException("Ja existe uma turma com o codigo: " + request.codigo());
        }

        Disciplina disciplina = disciplinaRepository.findByUuid(request.disciplinaUuid())
                .orElseThrow(() -> new ResourceNotFoundException("Disciplina", request.disciplinaUuid().toString()));

        Turma turma = new Turma();
        turma.setCodigo(request.codigo());
        turma.setDisciplina(disciplina);
        turma.setProfessor(request.professor());
        turma.setSemestre(request.semestre());
        turma.setVagas(request.vagas());
        return TurmaResponse.fromEntity(turmaRepository.save(turma));
    }

    @Transactional(readOnly = true)
    public PageResponse<TurmaResponse> listar(Pageable pageable) {
        return PageResponse.from(turmaRepository.findAll(pageable), TurmaResponse::fromEntity);
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
        return TurmaResponse.fromEntity(turmaRepository.save(turma));
    }

    /**
     * Remove a turma. Idempotente: se o recurso ja nao existir, retorna sem erro (no-op).
     */
    @Transactional
    public void deletar(UUID uuid) {
        turmaRepository.findByUuid(uuid).ifPresent(turmaRepository::delete);
    }
}
