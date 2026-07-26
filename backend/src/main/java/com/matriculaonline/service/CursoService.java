package com.matriculaonline.service;

import com.matriculaonline.domain.exception.ResourceNotFoundException;
import com.matriculaonline.domain.model.Curso;
import com.matriculaonline.dto.request.CursoRequest;
import com.matriculaonline.dto.response.CursoResponse;
import com.matriculaonline.dto.response.PageResponse;
import com.matriculaonline.repository.CursoRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CursoService {

    private final CursoRepository cursoRepository;

    public CursoService(CursoRepository cursoRepository) {
        this.cursoRepository = cursoRepository;
    }

    @Transactional
    public CursoResponse criar(CursoRequest request) {
        Curso curso = new Curso();
        curso.setNome(request.nome());
        curso.setDescricao(request.descricao());
        curso.setCargaHoraria(request.cargaHoraria());
        return CursoResponse.fromEntity(cursoRepository.save(curso));
    }

    @Transactional(readOnly = true)
    public PageResponse<CursoResponse> listar(Pageable pageable) {
        return PageResponse.from(cursoRepository.findAll(pageable), CursoResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public CursoResponse buscarPorUuid(UUID uuid) {
        Curso curso = cursoRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Curso", uuid.toString()));
        return CursoResponse.fromEntity(curso);
    }

    @Transactional
    public CursoResponse atualizar(UUID uuid, CursoRequest request) {
        Curso curso = cursoRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Curso", uuid.toString()));
        curso.setNome(request.nome());
        curso.setDescricao(request.descricao());
        curso.setCargaHoraria(request.cargaHoraria());
        return CursoResponse.fromEntity(cursoRepository.save(curso));
    }

    /**
     * Remove o curso. Idempotente: se o recurso já não existir, retorna sem erro (no-op).
     */
    @Transactional
    public void deletar(UUID uuid) {
        cursoRepository.findByUuid(uuid).ifPresent(cursoRepository::delete);
    }
}
