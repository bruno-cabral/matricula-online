package com.matriculaonline.service;

import com.matriculaonline.domain.exception.BusinessException;
import com.matriculaonline.domain.exception.DuplicateResourceException;
import com.matriculaonline.domain.exception.ResourceNotFoundException;
import com.matriculaonline.domain.model.Aluno;
import com.matriculaonline.dto.request.AlunoRequest;
import com.matriculaonline.dto.response.AlunoResponse;
import com.matriculaonline.dto.response.PageResponse;
import com.matriculaonline.repository.AlunoRepository;
import com.matriculaonline.repository.MatriculaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final MatriculaRepository matriculaRepository;

    public AlunoService(AlunoRepository alunoRepository, MatriculaRepository matriculaRepository) {
        this.alunoRepository = alunoRepository;
        this.matriculaRepository = matriculaRepository;
    }

    @Transactional
    public AlunoResponse criar(AlunoRequest request) {
        if (alunoRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Já existe um aluno com o email: " + request.email());
        }
        if (alunoRepository.existsByCpf(request.cpf())) {
            throw new DuplicateResourceException("Já existe um aluno com o CPF: " + request.cpf());
        }

        Aluno aluno = new Aluno();
        aluno.setNome(request.nome());
        aluno.setEmail(request.email());
        aluno.setCpf(request.cpf());
        aluno.setDataNascimento(request.dataNascimento());
        return AlunoResponse.fromEntity(alunoRepository.save(aluno));
    }

    @Transactional(readOnly = true)
    public PageResponse<AlunoResponse> listar(Pageable pageable) {
        return PageResponse.from(alunoRepository.findAll(pageable), AlunoResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public AlunoResponse buscarPorUuid(UUID uuid) {
        Aluno aluno = alunoRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno", uuid.toString()));
        return AlunoResponse.fromEntity(aluno);
    }

    @Transactional
    public AlunoResponse atualizar(UUID uuid, AlunoRequest request) {
        Aluno aluno = alunoRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno", uuid.toString()));
        aluno.setNome(request.nome());
        aluno.setEmail(request.email());
        aluno.setCpf(request.cpf());
        aluno.setDataNascimento(request.dataNascimento());
        return AlunoResponse.fromEntity(alunoRepository.save(aluno));
    }

    /**
     * Remove o aluno. Idempotente: se o recurso já não existir, retorna sem erro (no-op).
     */
    @Transactional
    public void deletar(UUID uuid) {
        alunoRepository.findByUuid(uuid).ifPresent(aluno -> {
            if (matriculaRepository.existsByAlunoUuid(uuid)) {
                throw new BusinessException("Aluno possui matrículas vinculadas.");
            }
            alunoRepository.delete(aluno);
        });
    }
}
