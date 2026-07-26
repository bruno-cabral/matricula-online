package com.matriculaonline.integration;

import com.matriculaonline.domain.model.Curso;
import com.matriculaonline.dto.request.DisciplinaRequest;
import com.matriculaonline.dto.response.DisciplinaResponse;
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
class DisciplinaIntegrationTest {

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

    private Curso curso;

    @BeforeEach
    void setUp() {
        matriculaRepository.deleteAll();
        turmaRepository.deleteAll();
        disciplinaRepository.deleteAll();
        cursoRepository.deleteAll();

        curso = new Curso();
        curso.setNome("Ciência da Computação");
        curso.setDescricao("Bacharelado");
        curso.setCargaHoraria(3200);
        curso = cursoRepository.save(curso);
    }

    @Test
    @DisplayName("CRUD completo de Disciplina via API")
    void crudCompletoDisciplina() {
        // CREATE
        DisciplinaRequest createRequest = new DisciplinaRequest(
                "Algoritmos", "Introdução a algoritmos", 60, curso.getUuid());

        ResponseEntity<DisciplinaResponse> createResponse = restTemplate.postForEntity(
                "/api/disciplinas", createRequest, DisciplinaResponse.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse.getBody()).isNotNull();
        assertThat(createResponse.getBody().nome()).isEqualTo("Algoritmos");
        assertThat(createResponse.getBody().cursoUuid()).isEqualTo(curso.getUuid());

        var uuid = createResponse.getBody().uuid();

        // READ by UUID
        ResponseEntity<DisciplinaResponse> readResponse = restTemplate.getForEntity(
                "/api/disciplinas/{uuid}", DisciplinaResponse.class, uuid);

        assertThat(readResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(readResponse.getBody().cargaHoraria()).isEqualTo(60);

        // UPDATE
        DisciplinaRequest updateRequest = new DisciplinaRequest(
                "Estruturas de Dados", "Avançado", 120, curso.getUuid());

        ResponseEntity<DisciplinaResponse> updateResponse = restTemplate.exchange(
                "/api/disciplinas/{uuid}", HttpMethod.PUT,
                new HttpEntity<>(updateRequest), DisciplinaResponse.class, uuid);

        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponse.getBody().nome()).isEqualTo("Estruturas de Dados");
        assertThat(updateResponse.getBody().cargaHoraria()).isEqualTo(120);

        // READ list (paginado)
        ResponseEntity<String> listResponse = restTemplate.getForEntity(
                "/api/disciplinas?page=0&size=10", String.class);

        assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(listResponse.getBody()).contains("Estruturas de Dados");

        // DELETE
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                "/api/disciplinas/{uuid}", HttpMethod.DELETE, null, Void.class, uuid);

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // DELETE idempotente: recurso já removido ainda retorna 204
        ResponseEntity<Void> deleteAgainResponse = restTemplate.exchange(
                "/api/disciplinas/{uuid}", HttpMethod.DELETE, null, Void.class, uuid);

        assertThat(deleteAgainResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // Verify deleted
        ResponseEntity<String> verifyResponse = restTemplate.getForEntity(
                "/api/disciplinas/{uuid}", String.class, uuid);

        assertThat(verifyResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Criar disciplina com curso inexistente retorna HTTP 404")
    void criarComCursoInexistenteRetornaNotFound() {
        DisciplinaRequest request = new DisciplinaRequest(
                "Algoritmos", "Introdução", 60, UUID.randomUUID());

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/disciplinas", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Validação de campos obrigatórios retorna HTTP 400")
    void validacaoCamposObrigatorios() {
        DisciplinaRequest invalidRequest = new DisciplinaRequest("", null, null, null);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/disciplinas", invalidRequest, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("nome");
    }
}
