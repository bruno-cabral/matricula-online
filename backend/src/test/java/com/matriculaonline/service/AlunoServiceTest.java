package com.matriculaonline.service;

import com.matriculaonline.domain.exception.DuplicateResourceException;
import com.matriculaonline.domain.exception.ResourceNotFoundException;
import com.matriculaonline.domain.model.Aluno;
import com.matriculaonline.dto.request.AlunoRequest;
import com.matriculaonline.dto.response.AlunoResponse;
import com.matriculaonline.repository.AlunoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlunoServiceTest {

    @Mock
    private AlunoRepository alunoRepository;

    @InjectMocks
    private AlunoService alunoService;

    private Aluno aluno;

    @BeforeEach
    void setUp() {
        aluno = new Aluno();
        aluno.setId(1L);
        aluno.setUuid(UUID.randomUUID());
        aluno.setNome("Maria Silva");
        aluno.setEmail("maria@email.com");
        aluno.setCpf("52998224725");
        aluno.setDataNascimento(LocalDate.of(2000, 1, 1));
    }

    private AlunoRequest request() {
        return new AlunoRequest("Maria Silva", "maria@email.com", "52998224725", LocalDate.of(2000, 1, 1));
    }

    @Test
    @DisplayName("Criar aluno com dados válidos - sucesso")
    void deveCriarAluno() {
        when(alunoRepository.existsByEmail(anyString())).thenReturn(false);
        when(alunoRepository.existsByCpf(anyString())).thenReturn(false);
        when(alunoRepository.save(any(Aluno.class))).thenReturn(aluno);

        AlunoResponse response = alunoService.criar(request());

        assertThat(response).isNotNull();
        assertThat(response.nome()).isEqualTo("Maria Silva");
        verify(alunoRepository).save(any(Aluno.class));
    }

    @Test
    @DisplayName("Criar aluno com email duplicado - erro")
    void deveRejeitarEmailDuplicado() {
        when(alunoRepository.existsByEmail("maria@email.com")).thenReturn(true);

        assertThatThrownBy(() -> alunoService.criar(request()))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("email");
        verify(alunoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Criar aluno com CPF duplicado - erro")
    void deveRejeitarCpfDuplicado() {
        when(alunoRepository.existsByEmail("maria@email.com")).thenReturn(false);
        when(alunoRepository.existsByCpf("52998224725")).thenReturn(true);

        assertThatThrownBy(() -> alunoService.criar(request()))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("CPF");
        verify(alunoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Buscar aluno por UUID existente - sucesso")
    void deveBuscarAlunoPorUuid() {
        when(alunoRepository.findByUuid(aluno.getUuid())).thenReturn(Optional.of(aluno));

        AlunoResponse response = alunoService.buscarPorUuid(aluno.getUuid());

        assertThat(response.uuid()).isEqualTo(aluno.getUuid());
    }

    @Test
    @DisplayName("Buscar aluno por UUID inexistente - erro")
    void deveLancarErroQuandoAlunoNaoEncontrado() {
        UUID uuid = UUID.randomUUID();
        when(alunoRepository.findByUuid(uuid)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> alunoService.buscarPorUuid(uuid))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Atualizar aluno existente - sucesso")
    void deveAtualizarAluno() {
        when(alunoRepository.findByUuid(aluno.getUuid())).thenReturn(Optional.of(aluno));
        when(alunoRepository.save(any(Aluno.class))).thenReturn(aluno);

        AlunoRequest update = new AlunoRequest("Maria Souza", "maria.souza@email.com", "52998224725", LocalDate.of(2000, 1, 1));
        AlunoResponse response = alunoService.atualizar(aluno.getUuid(), update);

        assertThat(response).isNotNull();
        assertThat(aluno.getNome()).isEqualTo("Maria Souza");
        verify(alunoRepository).save(aluno);
    }

    @Test
    @DisplayName("Atualizar aluno inexistente - erro")
    void deveLancarErroAoAtualizarAlunoInexistente() {
        UUID uuid = UUID.randomUUID();
        when(alunoRepository.findByUuid(uuid)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> alunoService.atualizar(uuid, request()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Deletar aluno existente - remove entidade")
    void deveDeletarAlunoExistente() {
        when(alunoRepository.findByUuid(aluno.getUuid())).thenReturn(Optional.of(aluno));

        alunoService.deletar(aluno.getUuid());

        verify(alunoRepository).delete(aluno);
    }

    @Test
    @DisplayName("Deletar aluno inexistente - idempotente (no-op, sem erro)")
    void deveSerIdempotenteAoDeletarInexistente() {
        UUID uuid = UUID.randomUUID();
        when(alunoRepository.findByUuid(uuid)).thenReturn(Optional.empty());

        alunoService.deletar(uuid);

        verify(alunoRepository, never()).delete(any());
    }
}
