package com.matriculaonline.service;

import com.matriculaonline.domain.exception.BusinessException;
import com.matriculaonline.domain.exception.ResourceNotFoundException;
import com.matriculaonline.domain.model.*;
import com.matriculaonline.dto.request.MatriculaRequest;
import com.matriculaonline.dto.response.MatriculaResponse;
import com.matriculaonline.repository.AlunoRepository;
import com.matriculaonline.repository.MatriculaRepository;
import com.matriculaonline.repository.TurmaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatriculaServiceTest {

    @Mock
    private MatriculaRepository matriculaRepository;

    @Mock
    private AlunoRepository alunoRepository;

    @Mock
    private TurmaRepository turmaRepository;

    @InjectMocks
    private MatriculaService matriculaService;

    private Aluno aluno;
    private Turma turma;
    private Disciplina disciplina;
    private Curso curso;

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
        disciplina.setCurso(curso);

        aluno = new Aluno();
        aluno.setId(1L);
        aluno.setUuid(UUID.randomUUID());
        aluno.setNome("Maria Silva");
        aluno.setEmail("maria@email.com");
        aluno.setCpf("52998224725");
        aluno.setDataNascimento(LocalDate.of(2000, 1, 1));

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

    private Matricula criarMatricula(StatusMatricula status) {
        Matricula matricula = new Matricula();
        matricula.setId(1L);
        matricula.setUuid(UUID.randomUUID());
        matricula.setAluno(aluno);
        matricula.setTurma(turma);
        matricula.setStatus(status);
        matricula.setDataMatricula(LocalDateTime.now());
        return matricula;
    }

    @Nested
    @DisplayName("Criar Matrícula")
    class CriarMatricula {

        @Test
        @DisplayName("Cenário 1: Matricular aluno em turma aberta com vagas - sucesso")
        void deveMatricularAlunoEmTurmaAbertaComVagas() {
            MatriculaRequest request = new MatriculaRequest(aluno.getUuid(), turma.getUuid());
            Matricula matriculaSalva = criarMatricula(StatusMatricula.PENDENTE);

            when(alunoRepository.findByUuid(aluno.getUuid())).thenReturn(Optional.of(aluno));
            when(turmaRepository.findByUuid(turma.getUuid())).thenReturn(Optional.of(turma));
            when(matriculaRepository.existsByAlunoIdAndTurmaIdAndStatusIn(any(), any(), any())).thenReturn(false);
            when(matriculaRepository.save(any(Matricula.class))).thenReturn(matriculaSalva);

            MatriculaResponse response = matriculaService.criar(request);

            assertThat(response).isNotNull();
            assertThat(response.status()).isEqualTo(StatusMatricula.PENDENTE);
            verify(matriculaRepository).save(any(Matricula.class));
        }

        @Test
        @DisplayName("Cenário 2: Matricular aluno em turma fechada - erro RN01")
        void deveRejeitarMatriculaEmTurmaFechada() {
            turma.setStatus(StatusTurma.FECHADA);
            MatriculaRequest request = new MatriculaRequest(aluno.getUuid(), turma.getUuid());

            when(alunoRepository.findByUuid(aluno.getUuid())).thenReturn(Optional.of(aluno));
            when(turmaRepository.findByUuid(turma.getUuid())).thenReturn(Optional.of(turma));

            assertThatThrownBy(() -> matriculaService.criar(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Turma não está aberta para matrículas");
        }

        @Test
        @DisplayName("Cenário 3: Matricular aluno em turma sem vagas - erro RN02")
        void deveRejeitarMatriculaEmTurmaSemVagas() {
            turma.setVagas(30);
            turma.setVagasOcupadas(30);
            MatriculaRequest request = new MatriculaRequest(aluno.getUuid(), turma.getUuid());

            when(alunoRepository.findByUuid(aluno.getUuid())).thenReturn(Optional.of(aluno));
            when(turmaRepository.findByUuid(turma.getUuid())).thenReturn(Optional.of(turma));

            assertThatThrownBy(() -> matriculaService.criar(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Não há vagas disponíveis nesta turma");
        }

        @Test
        @DisplayName("Cenário 4: Matrícula duplicada - erro RN03")
        void deveRejeitarMatriculaDuplicada() {
            MatriculaRequest request = new MatriculaRequest(aluno.getUuid(), turma.getUuid());

            when(alunoRepository.findByUuid(aluno.getUuid())).thenReturn(Optional.of(aluno));
            when(turmaRepository.findByUuid(turma.getUuid())).thenReturn(Optional.of(turma));
            when(matriculaRepository.existsByAlunoIdAndTurmaIdAndStatusIn(
                    aluno.getId(), turma.getId(),
                    List.of(StatusMatricula.PENDENTE, StatusMatricula.CONFIRMADA)
            )).thenReturn(true);

            assertThatThrownBy(() -> matriculaService.criar(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Aluno já possui matrícula nesta turma");
        }

        @Test
        @DisplayName("Cenário 12: Rematrícula após cancelamento - permitido")
        void devePermitirRematriculaAposCancelamento() {
            MatriculaRequest request = new MatriculaRequest(aluno.getUuid(), turma.getUuid());
            Matricula novaMatricula = criarMatricula(StatusMatricula.PENDENTE);

            when(alunoRepository.findByUuid(aluno.getUuid())).thenReturn(Optional.of(aluno));
            when(turmaRepository.findByUuid(turma.getUuid())).thenReturn(Optional.of(turma));
            when(matriculaRepository.existsByAlunoIdAndTurmaIdAndStatusIn(any(), any(), any())).thenReturn(false);
            when(matriculaRepository.save(any(Matricula.class))).thenReturn(novaMatricula);

            MatriculaResponse response = matriculaService.criar(request);

            assertThat(response).isNotNull();
            assertThat(response.status()).isEqualTo(StatusMatricula.PENDENTE);
        }
    }

    @Nested
    @DisplayName("Confirmar Matrícula")
    class ConfirmarMatricula {

        @Test
        @DisplayName("Cenário 5: Confirmar matrícula pendente - sucesso RN05")
        void deveConfirmarMatriculaPendente() {
            Matricula matricula = criarMatricula(StatusMatricula.PENDENTE);

            when(matriculaRepository.findByUuid(matricula.getUuid())).thenReturn(Optional.of(matricula));
            when(turmaRepository.save(any(Turma.class))).thenReturn(turma);
            when(matriculaRepository.save(any(Matricula.class))).thenReturn(matricula);

            MatriculaResponse response = matriculaService.confirmar(matricula.getUuid());

            assertThat(response.status()).isEqualTo(StatusMatricula.CONFIRMADA);
            assertThat(turma.getVagasOcupadas()).isEqualTo(1);
            verify(turmaRepository).save(turma);
        }

        @Test
        @DisplayName("Cenário 6: Confirmar matrícula sem vagas disponíveis - erro")
        void deveRejeitarConfirmacaoSemVagas() {
            turma.setVagas(1);
            turma.setVagasOcupadas(1);
            Matricula matricula = criarMatricula(StatusMatricula.PENDENTE);

            when(matriculaRepository.findByUuid(matricula.getUuid())).thenReturn(Optional.of(matricula));

            assertThatThrownBy(() -> matriculaService.confirmar(matricula.getUuid()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Não há vagas disponíveis nesta turma");
        }

        @Test
        @DisplayName("Cenário 9: Confirmar matrícula já confirmada - idempotente (no-op)")
        void deveRetornarSucessoQuandoJaConfirmada() {
            Matricula matricula = criarMatricula(StatusMatricula.CONFIRMADA);

            when(matriculaRepository.findByUuid(matricula.getUuid())).thenReturn(Optional.of(matricula));

            MatriculaResponse response = matriculaService.confirmar(matricula.getUuid());

            assertThat(response.status()).isEqualTo(StatusMatricula.CONFIRMADA);
            verify(turmaRepository, never()).save(any());
            verify(matriculaRepository, never()).save(any());
        }

        @Test
        @DisplayName("Cenário 11: Confirmar matrícula cancelada - erro RN04")
        void deveRejeitarConfirmacaoDeMatriculaCancelada() {
            Matricula matricula = criarMatricula(StatusMatricula.CANCELADA);

            when(matriculaRepository.findByUuid(matricula.getUuid())).thenReturn(Optional.of(matricula));

            assertThatThrownBy(() -> matriculaService.confirmar(matricula.getUuid()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Não é permitido confirmar uma matrícula cancelada");
        }
    }

    @Nested
    @DisplayName("Cancelar Matrícula")
    class CancelarMatricula {

        @Test
        @DisplayName("Cenário 7: Cancelar matrícula confirmada - libera vaga RN06")
        void deveCancelarMatriculaConfirmadaELiberarVaga() {
            turma.setVagasOcupadas(1);
            Matricula matricula = criarMatricula(StatusMatricula.CONFIRMADA);

            when(matriculaRepository.findByUuid(matricula.getUuid())).thenReturn(Optional.of(matricula));
            when(turmaRepository.save(any(Turma.class))).thenReturn(turma);
            when(matriculaRepository.save(any(Matricula.class))).thenReturn(matricula);

            MatriculaResponse response = matriculaService.cancelar(matricula.getUuid());

            assertThat(response.status()).isEqualTo(StatusMatricula.CANCELADA);
            assertThat(turma.getVagasOcupadas()).isEqualTo(0);
            verify(turmaRepository).save(turma);
        }

        @Test
        @DisplayName("Cenário 8: Cancelar matrícula pendente - sem alteração de vagas")
        void deveCancelarMatriculaPendenteSemAlterarVagas() {
            turma.setVagasOcupadas(0);
            Matricula matricula = criarMatricula(StatusMatricula.PENDENTE);

            when(matriculaRepository.findByUuid(matricula.getUuid())).thenReturn(Optional.of(matricula));
            when(matriculaRepository.save(any(Matricula.class))).thenReturn(matricula);

            MatriculaResponse response = matriculaService.cancelar(matricula.getUuid());

            assertThat(response.status()).isEqualTo(StatusMatricula.CANCELADA);
            assertThat(turma.getVagasOcupadas()).isEqualTo(0);
            verify(turmaRepository, never()).save(any());
        }

        @Test
        @DisplayName("Cenário 10: Cancelar matrícula já cancelada - idempotente (no-op)")
        void deveRetornarSucessoQuandoJaCancelada() {
            Matricula matricula = criarMatricula(StatusMatricula.CANCELADA);

            when(matriculaRepository.findByUuid(matricula.getUuid())).thenReturn(Optional.of(matricula));

            MatriculaResponse response = matriculaService.cancelar(matricula.getUuid());

            assertThat(response.status()).isEqualTo(StatusMatricula.CANCELADA);
            verify(turmaRepository, never()).save(any());
            verify(matriculaRepository, never()).save(any());
        }
    }
}
