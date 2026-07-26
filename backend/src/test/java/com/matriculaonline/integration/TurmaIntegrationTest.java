package com.matriculaonline.integration;

import com.matriculaonline.domain.model.Curso;
import com.matriculaonline.domain.model.Disciplina;
import com.matriculaonline.domain.model.StatusTurma;
import com.matriculaonline.dto.request.TurmaRequest;
import com.matriculaonline.dto.response.TurmaResponse;
import com.matriculaonline.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@ActiveProfiles("test")
class TurmaIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MatriculaRepository matriculaRepository;

    @Autowired
    private TurmaRepository turmaRepository;

    @Autowired
    private DisciplinaRepository disciplinaRepository;

    @Autowired
    private CursoRepository cursoRepository;

    private Disciplina disciplina;

    @BeforeEach
    void setUp() {
        matriculaRepository.deleteAll();
        turmaRepository.deleteAll();
        disciplinaRepository.deleteAll();
        cursoRepository.deleteAll();

        Curso curso = new Curso();
        curso.setNome("Ciência da Computação");
        curso.setDescricao("Bacharelado");
        curso.setCargaHoraria(3200);
        curso = cursoRepository.save(curso);

        disciplina = new Disciplina();
        disciplina.setNome("Algoritmos");
        disciplina.setDescricao("Introdução");
        disciplina.setCargaHoraria(60);
        disciplina.setCurso(curso);
        disciplina = disciplinaRepository.save(disciplina);
    }

    @Test
    @DisplayName("CRUD completo de Turma via API")
    void crudCompletoTurma() {
        // CREATE
        TurmaRequest createRequest = new TurmaRequest(
                "ALG-2026-1", disciplina.getUuid(), "Prof. Ana", "2026.1", 30);

        ResponseEntity<TurmaResponse> createResponse = restTemplate.postForEntity(
                "/api/turmas", createRequest, TurmaResponse.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse.getBody()).isNotNull();
        assertThat(createResponse.getBody().codigo()).isEqualTo("ALG-2026-1");
        assertThat(createResponse.getBody().disciplinaUuid()).isEqualTo(disciplina.getUuid());
        assertThat(createResponse.getBody().status()).isEqualTo(StatusTurma.ABERTA);
        assertThat(createResponse.getBody().vagasOcupadas()).isZero();

        var uuid = createResponse.getBody().uuid();

        // READ by UUID
        ResponseEntity<TurmaResponse> readResponse = restTemplate.getForEntity(
                "/api/turmas/{uuid}", TurmaResponse.class, uuid);

        assertThat(readResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(readResponse.getBody().vagas()).isEqualTo(30);

        // UPDATE
        TurmaRequest updateRequest = new TurmaRequest(
                "ALG-2026-2", disciplina.getUuid(), "Prof. Carlos", "2026.2", 40);

        ResponseEntity<TurmaResponse> updateResponse = restTemplate.exchange(
                "/api/turmas/{uuid}", HttpMethod.PUT,
                new HttpEntity<>(updateRequest), TurmaResponse.class, uuid);

        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponse.getBody().codigo()).isEqualTo("ALG-2026-2");
        assertThat(updateResponse.getBody().vagas()).isEqualTo(40);

        // READ list (paginado)
        ResponseEntity<String> listResponse = restTemplate.getForEntity(
                "/api/turmas?page=0&size=10", String.class);

        assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(listResponse.getBody()).contains("ALG-2026-2");

        // DELETE
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                "/api/turmas/{uuid}", HttpMethod.DELETE, null, Void.class, uuid);

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // DELETE idempotente: recurso já removido ainda retorna 204
        ResponseEntity<Void> deleteAgainResponse = restTemplate.exchange(
                "/api/turmas/{uuid}", HttpMethod.DELETE, null, Void.class, uuid);

        assertThat(deleteAgainResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // Verify deleted
        ResponseEntity<String> verifyResponse = restTemplate.getForEntity(
                "/api/turmas/{uuid}", String.class, uuid);

        assertThat(verifyResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Criar turma com código duplicado retorna HTTP 409")
    void criarComCodigoDuplicadoRetornaConflito() {
        TurmaRequest request = new TurmaRequest(
                "ALG-2026-1", disciplina.getUuid(), "Prof. Ana", "2026.1", 30);

        restTemplate.postForEntity("/api/turmas", request, TurmaResponse.class);

        ResponseEntity<String> duplicadaResponse = restTemplate.postForEntity(
                "/api/turmas", request, String.class);

        assertThat(duplicadaResponse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    @DisplayName("Criar turma com disciplina inexistente retorna HTTP 404")
    void criarComDisciplinaInexistenteRetornaNotFound() {
        TurmaRequest request = new TurmaRequest(
                "ALG-2026-9", UUID.randomUUID(), "Prof. Ana", "2026.1", 30);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/turmas", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Validação de campos obrigatórios retorna HTTP 400")
    void validacaoCamposObrigatorios() {
        TurmaRequest invalidRequest = new TurmaRequest("", null, "", "", null);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/turmas", invalidRequest, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("codigo");
    }
}
