package com.matriculaonline.service;

import com.matriculaonline.domain.exception.BusinessException;
import com.matriculaonline.domain.exception.ResourceNotFoundException;
import com.matriculaonline.domain.model.Curso;
import com.matriculaonline.domain.model.Disciplina;
import com.matriculaonline.dto.request.DisciplinaRequest;
import com.matriculaonline.dto.response.DisciplinaResponse;
import com.matriculaonline.repository.CursoRepository;
import com.matriculaonline.repository.DisciplinaRepository;
import com.matriculaonline.repository.TurmaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DisciplinaServiceTest {

    @Mock
    private DisciplinaRepository disciplinaRepository;

    @Mock
    private CursoRepository cursoRepository;

    @Mock
    private TurmaRepository turmaRepository;

    @InjectMocks
    private DisciplinaService disciplinaService;

    private Curso curso;
    private Disciplina disciplina;

    @BeforeEach
    void setUp() {
        curso = new Curso();
        curso.setId(1L);
        curso.setUuid(UUID.randomUUID());
        curso.setNome("Engenharia de Software");

        disciplina = new Disciplina();
        disciplina.setId(1L);
        disciplina.setUuid(UUID.randomUUID());
        disciplina.setNome("Programação");
        disciplina.setDescricao("Introdução");
        disciplina.setCargaHoraria(80);
        disciplina.setCurso(curso);
    }

    private DisciplinaRequest request() {
        return new DisciplinaRequest("Programação", "Introdução", 80, curso.getUuid());
    }

    @Test
    @DisplayName("Criar disciplina com curso válido - sucesso")
    void deveCriarDisciplina() {
        when(cursoRepository.findByUuid(curso.getUuid())).thenReturn(Optional.of(curso));
        when(disciplinaRepository.save(any(Disciplina.class))).thenReturn(disciplina);

        DisciplinaResponse response = disciplinaService.criar(request());

        assertThat(response).isNotNull();
        assertThat(response.nome()).isEqualTo("Programação");
        assertThat(response.cursoUuid()).isEqualTo(curso.getUuid());
        verify(disciplinaRepository).save(any(Disciplina.class));
    }

    @Test
    @DisplayName("Criar disciplina com curso inexistente - erro")
    void deveRejeitarCriacaoComCursoInexistente() {
        when(cursoRepository.findByUuid(curso.getUuid())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> disciplinaService.criar(request()))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(disciplinaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Buscar disciplina por UUID existente - sucesso")
    void deveBuscarDisciplinaPorUuid() {
        when(disciplinaRepository.findByUuid(disciplina.getUuid())).thenReturn(Optional.of(disciplina));

        DisciplinaResponse response = disciplinaService.buscarPorUuid(disciplina.getUuid());

        assertThat(response.uuid()).isEqualTo(disciplina.getUuid());
    }

    @Test
    @DisplayName("Buscar disciplina por UUID inexistente - erro")
    void deveLancarErroQuandoDisciplinaNaoEncontrada() {
        UUID uuid = UUID.randomUUID();
        when(disciplinaRepository.findByUuid(uuid)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> disciplinaService.buscarPorUuid(uuid))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Atualizar disciplina existente - sucesso")
    void deveAtualizarDisciplina() {
        when(disciplinaRepository.findByUuid(disciplina.getUuid())).thenReturn(Optional.of(disciplina));
        when(cursoRepository.findByUuid(curso.getUuid())).thenReturn(Optional.of(curso));
        when(disciplinaRepository.save(any(Disciplina.class))).thenReturn(disciplina);

        DisciplinaRequest update = new DisciplinaRequest("Algoritmos", "Avançado", 120, curso.getUuid());
        DisciplinaResponse response = disciplinaService.atualizar(disciplina.getUuid(), update);

        assertThat(response).isNotNull();
        assertThat(disciplina.getNome()).isEqualTo("Algoritmos");
        assertThat(disciplina.getCargaHoraria()).isEqualTo(120);
        verify(disciplinaRepository).save(disciplina);
    }

    @Test
    @DisplayName("Atualizar disciplina inexistente - erro")
    void deveLancarErroAoAtualizarDisciplinaInexistente() {
        UUID uuid = UUID.randomUUID();
        when(disciplinaRepository.findByUuid(uuid)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> disciplinaService.atualizar(uuid, request()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Deletar disciplina existente - remove entidade")
    void deveDeletarDisciplinaExistente() {
        when(disciplinaRepository.findByUuid(disciplina.getUuid())).thenReturn(Optional.of(disciplina));
        when(turmaRepository.existsByDisciplinaUuid(disciplina.getUuid())).thenReturn(false);

        disciplinaService.deletar(disciplina.getUuid());

        verify(disciplinaRepository).delete(disciplina);
    }

    @Test
    @DisplayName("Deletar disciplina com turmas vinculadas - erro")
    void deveRejeitarExclusaoComTurmasVinculadas() {
        when(disciplinaRepository.findByUuid(disciplina.getUuid())).thenReturn(Optional.of(disciplina));
        when(turmaRepository.existsByDisciplinaUuid(disciplina.getUuid())).thenReturn(true);

        assertThatThrownBy(() -> disciplinaService.deletar(disciplina.getUuid()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("turmas vinculadas");
        verify(disciplinaRepository, never()).delete(any(Disciplina.class));
    }

    @Test
    @DisplayName("Deletar disciplina inexistente - idempotente (no-op, sem erro)")
    void deveSerIdempotenteAoDeletarInexistente() {
        UUID uuid = UUID.randomUUID();
        when(disciplinaRepository.findByUuid(uuid)).thenReturn(Optional.empty());

        disciplinaService.deletar(uuid);

        verify(disciplinaRepository, never()).delete(any(Disciplina.class));
    }
}
