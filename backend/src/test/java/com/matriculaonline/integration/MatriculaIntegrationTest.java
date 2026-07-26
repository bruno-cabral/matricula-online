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
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

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
        curso.setNome("Ciencia da Computacao");
        curso.setDescricao("Curso de CC");
        curso.setCargaHoraria(3200);
        curso = cursoRepository.save(curso);

        Disciplina disciplina = new Disciplina();
        disciplina.setNome("Algoritmos");
        disciplina.setDescricao("Introducao a algoritmos");
        disciplina.setCargaHoraria(60);
        disciplina.setCurso(curso);
        disciplina = disciplinaRepository.save(disciplina);

        aluno = new Aluno();
        aluno.setNome("Carlos Oliveira");
        aluno.setEmail("carlos@email.com");
        aluno.setCpf("11111111111");
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

        ResponseEntity<MatriculaResponse> confirmarResponse = restTemplate.patchForObject(
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
    @DisplayName("Matricula duplicada retorna erro via API")
    void matriculaDuplicadaRetornaErro() {
        MatriculaRequest request = new MatriculaRequest(aluno.getUuid(), turma.getUuid());

        restTemplate.postForEntity("/api/matriculas", request, MatriculaResponse.class);

        ResponseEntity<ErrorResponse> duplicadaResponse = restTemplate.postForEntity(
                "/api/matriculas", request, ErrorResponse.class);

        assertThat(duplicadaResponse.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(duplicadaResponse.getBody()).isNotNull();
        assertThat(duplicadaResponse.getBody().message()).contains("Aluno ja possui matricula nesta turma");
    }

    @Test
    @DisplayName("Matricula em turma lotada retorna erro via API")
    void matriculaEmTurmaLotadaRetornaErro() {
        turma.setVagas(1);
        turma.setVagasOcupadas(1);
        turmaRepository.save(turma);

        MatriculaRequest request = new MatriculaRequest(aluno.getUuid(), turma.getUuid());

        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
                "/api/matriculas", request, ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).contains("Nao ha vagas disponiveis");
    }

    @Test
    @DisplayName("Consulta de matriculas por aluno via API")
    void consultaMatriculasPorAluno() {
        MatriculaRequest request = new MatriculaRequest(aluno.getUuid(), turma.getUuid());
        restTemplate.postForEntity("/api/matriculas", request, MatriculaResponse.class);

        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/matriculas/aluno/{alunoUuid}", String.class, aluno.getUuid());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Carlos Oliveira");
    }

    @Test
    @DisplayName("Consulta de matriculas por turma via API")
    void consultaMatriculasPorTurma() {
        MatriculaRequest request = new MatriculaRequest(aluno.getUuid(), turma.getUuid());
        restTemplate.postForEntity("/api/matriculas", request, MatriculaResponse.class);

        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/matriculas/turma/{turmaUuid}", String.class, turma.getUuid());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("ALG-2026-1");
    }
}
