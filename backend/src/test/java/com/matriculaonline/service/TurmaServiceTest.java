package com.matriculaonline.service;

import com.matriculaonline.domain.exception.DuplicateResourceException;
import com.matriculaonline.domain.exception.ResourceNotFoundException;
import com.matriculaonline.domain.model.Disciplina;
import com.matriculaonline.domain.model.StatusTurma;
import com.matriculaonline.domain.model.Turma;
import com.matriculaonline.dto.request.TurmaRequest;
import com.matriculaonline.dto.response.TurmaResponse;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TurmaServiceTest {

    @Mock
    private TurmaRepository turmaRepository;

    @Mock
    private DisciplinaRepository disciplinaRepository;

    @InjectMocks
    private TurmaService turmaService;

    private Disciplina disciplina;
    private Turma turma;

    @BeforeEach
    void setUp() {
        disciplina = new Disciplina();
        disciplina.setId(1L);
        disciplina.setUuid(UUID.randomUUID());
        disciplina.setNome("Programação");

        turma = new Turma();
        turma.setId(1L);
        turma.setUuid(UUID.randomUUID());
        turma.setCodigo("PROG-2026-1");
        turma.setDisciplina(disciplina);
        turma.setProfessor("Dr. João");
        turma.setSemestre("2026.1");
        turma.setVagas(30);
        turma.setVagasOcupadas(0);
        turma.setStatus(StatusTurma.ABERTA);
    }

    private TurmaRequest request() {
        return new TurmaRequest("PROG-2026-1", disciplina.getUuid(), "Dr. João", "2026.1", 30);
    }

    @Test
    @DisplayName("Criar turma com dados válidos - sucesso")
    void deveCriarTurma() {
        when(turmaRepository.existsByCodigo("PROG-2026-1")).thenReturn(false);
        when(disciplinaRepository.findByUuid(disciplina.getUuid())).thenReturn(Optional.of(disciplina));
        when(turmaRepository.save(any(Turma.class))).thenReturn(turma);

        TurmaResponse response = turmaService.criar(request());

        assertThat(response).isNotNull();
        assertThat(response.codigo()).isEqualTo("PROG-2026-1");
        assertThat(response.disciplinaUuid()).isEqualTo(disciplina.getUuid());
        verify(turmaRepository).save(any(Turma.class));
    }

    @Test
    @DisplayName("Criar turma com código duplicado - erro")
    void deveRejeitarCodigoDuplicado() {
        when(turmaRepository.existsByCodigo("PROG-2026-1")).thenReturn(true);

        assertThatThrownBy(() -> turmaService.criar(request()))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("código");
        verify(turmaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Criar turma com disciplina inexistente - erro")
    void deveRejeitarCriacaoComDisciplinaInexistente() {
        when(turmaRepository.existsByCodigo(anyString())).thenReturn(false);
        when(disciplinaRepository.findByUuid(disciplina.getUuid())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> turmaService.criar(request()))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(turmaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Buscar turma por UUID existente - sucesso")
    void deveBuscarTurmaPorUuid() {
        when(turmaRepository.findByUuid(turma.getUuid())).thenReturn(Optional.of(turma));

        TurmaResponse response = turmaService.buscarPorUuid(turma.getUuid());

        assertThat(response.uuid()).isEqualTo(turma.getUuid());
    }

    @Test
    @DisplayName("Buscar turma por UUID inexistente - erro")
    void deveLancarErroQuandoTurmaNaoEncontrada() {
        UUID uuid = UUID.randomUUID();
        when(turmaRepository.findByUuid(uuid)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> turmaService.buscarPorUuid(uuid))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Atualizar turma existente - sucesso")
    void deveAtualizarTurma() {
        when(turmaRepository.findByUuid(turma.getUuid())).thenReturn(Optional.of(turma));
        when(disciplinaRepository.findByUuid(disciplina.getUuid())).thenReturn(Optional.of(disciplina));
        when(turmaRepository.save(any(Turma.class))).thenReturn(turma);

        TurmaRequest update = new TurmaRequest("PROG-2026-2", disciplina.getUuid(), "Dra. Ana", "2026.2", 40);
        TurmaResponse response = turmaService.atualizar(turma.getUuid(), update);

        assertThat(response).isNotNull();
        assertThat(turma.getCodigo()).isEqualTo("PROG-2026-2");
        assertThat(turma.getProfessor()).isEqualTo("Dra. Ana");
        assertThat(turma.getVagas()).isEqualTo(40);
        verify(turmaRepository).save(turma);
    }

    @Test
    @DisplayName("Atualizar turma inexistente - erro")
    void deveLancarErroAoAtualizarTurmaInexistente() {
        UUID uuid = UUID.randomUUID();
        when(turmaRepository.findByUuid(uuid)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> turmaService.atualizar(uuid, request()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Deletar turma existente - remove entidade")
    void deveDeletarTurmaExistente() {
        when(turmaRepository.findByUuid(turma.getUuid())).thenReturn(Optional.of(turma));

        turmaService.deletar(turma.getUuid());

        verify(turmaRepository).delete(turma);
    }

    @Test
    @DisplayName("Deletar turma inexistente - idempotente (no-op, sem erro)")
    void deveSerIdempotenteAoDeletarInexistente() {
        UUID uuid = UUID.randomUUID();
        when(turmaRepository.findByUuid(uuid)).thenReturn(Optional.empty());

        turmaService.deletar(uuid);

        verify(turmaRepository, never()).delete(any());
    }
}
