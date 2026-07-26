package com.matriculaonline.service;

import com.matriculaonline.domain.exception.ResourceNotFoundException;
import com.matriculaonline.domain.model.Curso;
import com.matriculaonline.domain.model.Disciplina;
import com.matriculaonline.dto.request.DisciplinaRequest;
import com.matriculaonline.dto.response.DisciplinaResponse;
import com.matriculaonline.dto.response.PageResponse;
import com.matriculaonline.repository.CursoRepository;
import com.matriculaonline.repository.DisciplinaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DisciplinaService {

    private final DisciplinaRepository disciplinaRepository;
    private final CursoRepository cursoRepository;

    public DisciplinaService(DisciplinaRepository disciplinaRepository, CursoRepository cursoRepository) {
        this.disciplinaRepository = disciplinaRepository;
        this.cursoRepository = cursoRepository;
    }

    @Transactional
    public DisciplinaResponse criar(DisciplinaRequest request) {
        Curso curso = cursoRepository.findByUuid(request.cursoUuid())
                .orElseThrow(() -> new ResourceNotFoundException("Curso", request.cursoUuid().toString()));

        Disciplina disciplina = new Disciplina();
        disciplina.setNome(request.nome());
        disciplina.setDescricao(request.descricao());
        disciplina.setCargaHoraria(request.cargaHoraria());
        disciplina.setCurso(curso);
        return DisciplinaResponse.fromEntity(disciplinaRepository.save(disciplina));
    }

    @Transactional(readOnly = true)
    public PageResponse<DisciplinaResponse> listar(Pageable pageable) {
        return PageResponse.from(disciplinaRepository.findAll(pageable), DisciplinaResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public DisciplinaResponse buscarPorUuid(UUID uuid) {
        Disciplina disciplina = disciplinaRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Disciplina", uuid.toString()));
        return DisciplinaResponse.fromEntity(disciplina);
    }

    @Transactional
    public DisciplinaResponse atualizar(UUID uuid, DisciplinaRequest request) {
        Disciplina disciplina = disciplinaRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Disciplina", uuid.toString()));

        Curso curso = cursoRepository.findByUuid(request.cursoUuid())
                .orElseThrow(() -> new ResourceNotFoundException("Curso", request.cursoUuid().toString()));

        disciplina.setNome(request.nome());
        disciplina.setDescricao(request.descricao());
        disciplina.setCargaHoraria(request.cargaHoraria());
        disciplina.setCurso(curso);
        return DisciplinaResponse.fromEntity(disciplinaRepository.save(disciplina));
    }

    /**
     * Remove a disciplina. Idempotente: se o recurso já não existir, retorna sem erro (no-op).
     */
    @Transactional
    public void deletar(UUID uuid) {
        disciplinaRepository.findByUuid(uuid).ifPresent(disciplinaRepository::delete);
    }
}
