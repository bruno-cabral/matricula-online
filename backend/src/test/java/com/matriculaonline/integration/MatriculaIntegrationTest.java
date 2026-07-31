package com.matriculaonline.integration;

import com.matriculaonline.domain.model.*;
import com.matriculaonline.dto.request.MatriculaRequest;
import com.matriculaonline.dto.response.ErrorResponse;
import com.matriculaonline.dto.response.MatriculaResponse;
import com.matriculaonline.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MatriculaIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private DisciplinaRepository disciplinaRepository;

    @Autowired
    private TurmaRepository turmaRepository;

    @Autowired
    private MatriculaRepository matriculaRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private Aluno aluno;
    private Turma turma;

    @BeforeEach
    void setUp() {
        matriculaRepository.deleteAll();
        turmaRepository.deleteAll();
        disciplinaRepository.deleteAll();
        alunoRepository.deleteAll();
        cursoRepository.deleteAll();

        Curso curso = new Curso();
        curso.setNome("Ciência da Computação");
        curso.setDescricao("Curso de CC");
        curso.setCargaHoraria(3200);
        curso = cursoRepository.save(curso);

        Disciplina disciplina = new Disciplina();
        disciplina.setNome("Algoritmos");
        disciplina.setDescricao("Introdução a algoritmos");
        disciplina.setCargaHoraria(60);
        disciplina.setCurso(curso);
        disciplina = disciplinaRepository.save(disciplina);

        aluno = new Aluno();
        aluno.setNome("Carlos Oliveira");
        aluno.setEmail("carlos@email.com");
        aluno.setCpf("11144477735");
        aluno.setDataNascimento(LocalDate.of(2000, 5, 15));
        aluno = alunoRepository.save(aluno);

        turma = new Turma();
        turma.setCodigo("ALG-2026-1");
        turma.setDisciplina(disciplina);
        turma.setProfessor("Prof. Ana");
        turma.setSemestre("2026.1");
        turma.setVagas(2);
        turma.setVagasOcupadas(0);
        turma.setStatus(StatusTurma.ABERTA);
        turma = turmaRepository.save(turma);
    }

    @Test
    @DisplayName("Fluxo completo: criar -> confirmar -> vaga consumida")
    void fluxoCompletoCriarConfirmarMatricula() {
        MatriculaRequest request = new MatriculaRequest(aluno.getUuid(), turma.getUuid());

        ResponseEntity<MatriculaResponse> criarResponse = restTemplate.postForEntity(
                "/api/matriculas", request, MatriculaResponse.class);

        assertThat(criarResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(criarResponse.getBody()).isNotNull();
        assertThat(criarResponse.getBody().status()).isEqualTo(StatusMatricula.PENDENTE);

        restTemplate.patchForObject(
                "/api/matriculas/{uuid}/confirmar",
                null, MatriculaResponse.class, criarResponse.getBody().uuid());

        Turma turmaAtualizada = turmaRepository.findByUuid(turma.getUuid()).orElseThrow();
        assertThat(turmaAtualizada.getVagasOcupadas()).isEqualTo(1);
    }

    @Test
    @DisplayName("Fluxo cancelamento: confirmar -> cancelar -> vaga liberada")
    void fluxoCancelamentoLiberaVaga() {
        MatriculaRequest request = new MatriculaRequest(aluno.getUuid(), turma.getUuid());

        ResponseEntity<MatriculaResponse> criarResponse = restTemplate.postForEntity(
                "/api/matriculas", request, MatriculaResponse.class);

        restTemplate.patchForObject(
                "/api/matriculas/{uuid}/confirmar",
                null, MatriculaResponse.class, criarResponse.getBody().uuid());

        restTemplate.patchForObject(
                "/api/matriculas/{uuid}/cancelar",
                null, MatriculaResponse.class, criarResponse.getBody().uuid());

        Turma turmaAtualizada = turmaRepository.findByUuid(turma.getUuid()).orElseThrow();
        assertThat(turmaAtualizada.getVagasOcupadas()).isEqualTo(0);
    }

    @Test
    @DisplayName("Matrícula duplicada retorna erro via API")
    void matriculaDuplicadaRetornaErro() {
        MatriculaRequest request = new MatriculaRequest(aluno.getUuid(), turma.getUuid());

        restTemplate.postForEntity("/api/matriculas", request, MatriculaResponse.class);

        ResponseEntity<ErrorResponse> duplicadaResponse = restTemplate.postForEntity(
                "/api/matriculas", request, ErrorResponse.class);

        assertThat(duplicadaResponse.getStatusCode().value()).isEqualTo(422);
        assertThat(duplicadaResponse.getBody()).isNotNull();
        assertThat(duplicadaResponse.getBody().message()).contains("Aluno já possui matrícula nesta turma");
    }

    @Test
    @DisplayName("Matrícula em turma lotada retorna erro via API")
    void matriculaEmTurmaLotadaRetornaErro() {
        turma.setVagas(1);
        turma.setVagasOcupadas(1);
        turmaRepository.save(turma);

        MatriculaRequest request = new MatriculaRequest(aluno.getUuid(), turma.getUuid());

        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
                "/api/matriculas", request, ErrorResponse.class);

        assertThat(response.getStatusCode().value()).isEqualTo(422);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).contains("Não há vagas disponíveis");
    }

    @Test
    @DisplayName("Consulta de matrículas por aluno via API")
    void consultaMatriculasPorAluno() {
        MatriculaRequest request = new MatriculaRequest(aluno.getUuid(), turma.getUuid());
        restTemplate.postForEntity("/api/matriculas", request, MatriculaResponse.class);

        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/matriculas/aluno/{alunoUuid}", String.class, aluno.getUuid());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Carlos Oliveira");
    }

    @Test
    @DisplayName("Consulta de matrículas por turma via API")
    void consultaMatriculasPorTurma() {
        MatriculaRequest request = new MatriculaRequest(aluno.getUuid(), turma.getUuid());
        restTemplate.postForEntity("/api/matriculas", request, MatriculaResponse.class);

        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/matriculas/turma/{turmaUuid}", String.class, turma.getUuid());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("ALG-2026-1");
    }

    @Test
    @DisplayName("Concorrência da última vaga: duas confirmações simultâneas permitem apenas uma confirmação")
    void concorrenciaUltimaVagaPermiteApenasUmaConfirmacao() throws Exception {
        turma.setVagas(1);
        turma.setVagasOcupadas(0);
        turma.setStatus(StatusTurma.ABERTA);
        turma = turmaRepository.saveAndFlush(turma);

        Aluno aluno2 = new Aluno();
        aluno2.setNome("Julia Rocha");
        aluno2.setEmail("julia@email.com");
        aluno2.setCpf("12345678909");
        aluno2.setDataNascimento(LocalDate.of(2001, 8, 10));
        aluno2 = alunoRepository.saveAndFlush(aluno2);

        Matricula matricula1 = criarMatriculaPendente(aluno, turma);
        Matricula matricula2 = criarMatriculaPendente(aluno2, turma);

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CyclicBarrier barrier = new CyclicBarrier(2);

        try {
            Future<ResultadoConfirmacao> f1 = executor.submit(() -> confirmarConcorrente(matricula1.getUuid(), barrier));
            Future<ResultadoConfirmacao> f2 = executor.submit(() -> confirmarConcorrente(matricula2.getUuid(), barrier));

            ResultadoConfirmacao r1 = f1.get(10, TimeUnit.SECONDS);
            ResultadoConfirmacao r2 = f2.get(10, TimeUnit.SECONDS);

            List<ResultadoConfirmacao> resultados = Arrays.asList(r1, r2);
            assertThat(resultados).containsExactlyInAnyOrder(
                    ResultadoConfirmacao.CONFIRMADA,
                    ResultadoConfirmacao.CONFLITO_OTIMISTA
            );

            Turma turmaAtualizada = turmaRepository.findByUuid(turma.getUuid()).orElseThrow();
            assertThat(turmaAtualizada.getVagasOcupadas()).isEqualTo(1);

            Matricula m1Atualizada = matriculaRepository.findByUuid(matricula1.getUuid()).orElseThrow();
            Matricula m2Atualizada = matriculaRepository.findByUuid(matricula2.getUuid()).orElseThrow();

            long confirmadas = List.of(m1Atualizada, m2Atualizada).stream()
                    .filter(m -> m.getStatus() == StatusMatricula.CONFIRMADA)
                    .count();
            long pendentes = List.of(m1Atualizada, m2Atualizada).stream()
                    .filter(m -> m.getStatus() == StatusMatricula.PENDENTE)
                    .count();

            assertThat(confirmadas).isEqualTo(1);
            assertThat(pendentes).isEqualTo(1);
        } finally {
            executor.shutdownNow();
        }
    }

    private Matricula criarMatriculaPendente(Aluno aluno, Turma turma) {
        Matricula matricula = new Matricula();
        matricula.setAluno(aluno);
        matricula.setTurma(turma);
        matricula.setStatus(StatusMatricula.PENDENTE);
        return matriculaRepository.saveAndFlush(matricula);
    }

    private ResultadoConfirmacao confirmarConcorrente(UUID matriculaUuid, CyclicBarrier barrier) {
        TransactionTemplate tx = new TransactionTemplate(transactionManager);
        tx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        try {
            return tx.execute(status -> {
                Matricula matricula = matriculaRepository.findByUuid(matriculaUuid).orElseThrow();
                Turma turmaDaMatricula = matricula.getTurma();

                assertThat(turmaDaMatricula.temVagasDisponiveis()).isTrue();

                try {
                    barrier.await(5, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("Thread interrompida durante sincronização de concorrência", e);
                } catch (BrokenBarrierException | TimeoutException e) {
                    throw new IllegalStateException("Falha ao sincronizar confirmações concorrentes", e);
                }

                turmaDaMatricula.incrementarVagasOcupadas();
                turmaRepository.saveAndFlush(turmaDaMatricula);

                matricula.setStatus(StatusMatricula.CONFIRMADA);
                matriculaRepository.saveAndFlush(matricula);

                return ResultadoConfirmacao.CONFIRMADA;
            });
        } catch (ObjectOptimisticLockingFailureException ex) {
            return ResultadoConfirmacao.CONFLITO_OTIMISTA;
        }
    }

    private enum ResultadoConfirmacao {
        CONFIRMADA,
        CONFLITO_OTIMISTA
    }
}
