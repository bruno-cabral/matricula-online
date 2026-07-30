package com.matriculaonline.service;

import com.matriculaonline.domain.exception.BusinessException;
import com.matriculaonline.domain.exception.ResourceNotFoundException;
import com.matriculaonline.domain.model.Curso;
import com.matriculaonline.dto.request.CursoRequest;
import com.matriculaonline.dto.response.CursoResponse;
import com.matriculaonline.repository.CursoRepository;
import com.matriculaonline.repository.DisciplinaRepository;
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
class CursoServiceTest {

    @Mock
    private CursoRepository cursoRepository;

    @Mock
    private DisciplinaRepository disciplinaRepository;

    @InjectMocks
    private CursoService cursoService;

    private Curso curso;

    @BeforeEach
    void setUp() {
        curso = new Curso();
        curso.setId(1L);
        curso.setUuid(UUID.randomUUID());
        curso.setNome("Engenharia de Software");
        curso.setDescricao("Curso de ES");
        curso.setCargaHoraria(3600);
    }

    private CursoRequest request() {
        return new CursoRequest("Engenharia de Software", "Curso de ES", 3600);
    }

    @Test
    @DisplayName("Criar curso com dados válidos - sucesso")
    void deveCriarCurso() {
        when(cursoRepository.save(any(Curso.class))).thenReturn(curso);

        CursoResponse response = cursoService.criar(request());

        assertThat(response).isNotNull();
        assertThat(response.nome()).isEqualTo("Engenharia de Software");
        verify(cursoRepository).save(any(Curso.class));
    }

    @Test
    @DisplayName("Buscar curso por UUID existente - sucesso")
    void deveBuscarCursoPorUuid() {
        when(cursoRepository.findByUuid(curso.getUuid())).thenReturn(Optional.of(curso));

        CursoResponse response = cursoService.buscarPorUuid(curso.getUuid());

        assertThat(response.uuid()).isEqualTo(curso.getUuid());
    }

    @Test
    @DisplayName("Buscar curso por UUID inexistente - erro")
    void deveLancarErroQuandoCursoNaoEncontrado() {
        UUID uuid = UUID.randomUUID();
        when(cursoRepository.findByUuid(uuid)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cursoService.buscarPorUuid(uuid))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Atualizar curso existente - sucesso")
    void deveAtualizarCurso() {
        when(cursoRepository.findByUuid(curso.getUuid())).thenReturn(Optional.of(curso));
        when(cursoRepository.save(any(Curso.class))).thenReturn(curso);

        CursoRequest update = new CursoRequest("Engenharia de Computação", "Novo", 4000);
        CursoResponse response = cursoService.atualizar(curso.getUuid(), update);

        assertThat(response).isNotNull();
        assertThat(curso.getNome()).isEqualTo("Engenharia de Computação");
        assertThat(curso.getCargaHoraria()).isEqualTo(4000);
        verify(cursoRepository).save(curso);
    }

    @Test
    @DisplayName("Atualizar curso inexistente - erro")
    void deveLancarErroAoAtualizarCursoInexistente() {
        UUID uuid = UUID.randomUUID();
        when(cursoRepository.findByUuid(uuid)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cursoService.atualizar(uuid, request()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Deletar curso existente - remove entidade")
    void deveDeletarCursoExistente() {
        when(cursoRepository.findByUuid(curso.getUuid())).thenReturn(Optional.of(curso));
        when(disciplinaRepository.existsByCursoUuid(curso.getUuid())).thenReturn(false);

        cursoService.deletar(curso.getUuid());

        verify(cursoRepository).delete(curso);
    }

    @Test
    @DisplayName("Deletar curso com disciplinas vinculadas - erro")
    void deveRejeitarExclusaoComDisciplinasVinculadas() {
        when(cursoRepository.findByUuid(curso.getUuid())).thenReturn(Optional.of(curso));
        when(disciplinaRepository.existsByCursoUuid(curso.getUuid())).thenReturn(true);

        assertThatThrownBy(() -> cursoService.deletar(curso.getUuid()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("disciplinas vinculadas");
        verify(cursoRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Deletar curso inexistente - idempotente (no-op, sem erro)")
    void deveSerIdempotenteAoDeletarInexistente() {
        UUID uuid = UUID.randomUUID();
        when(cursoRepository.findByUuid(uuid)).thenReturn(Optional.empty());

        cursoService.deletar(uuid);

        verify(cursoRepository, never()).delete(any());
    }
}
