package com.matriculaonline.integration;

import com.matriculaonline.dto.request.CursoRequest;
import com.matriculaonline.dto.response.CursoResponse;
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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@ActiveProfiles("test")
class CursoIntegrationTest {

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

    @BeforeEach
    void setUp() {
        matriculaRepository.deleteAll();
        turmaRepository.deleteAll();
        disciplinaRepository.deleteAll();
        cursoRepository.deleteAll();
    }

    @Test
    @DisplayName("CRUD completo de Curso via API")
    void crudCompletoCurso() {
        // CREATE
        CursoRequest createRequest = new CursoRequest("Ciencia da Computacao", "Bacharelado", 3200);

        ResponseEntity<CursoResponse> createResponse = restTemplate.postForEntity(
                "/api/cursos", createRequest, CursoResponse.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse.getBody()).isNotNull();
        assertThat(createResponse.getBody().nome()).isEqualTo("Ciencia da Computacao");
        assertThat(createResponse.getBody().uuid()).isNotNull();

        var uuid = createResponse.getBody().uuid();

        // READ by UUID
        ResponseEntity<CursoResponse> readResponse = restTemplate.getForEntity(
                "/api/cursos/{uuid}", CursoResponse.class, uuid);

        assertThat(readResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(readResponse.getBody().cargaHoraria()).isEqualTo(3200);

        // UPDATE
        CursoRequest updateRequest = new CursoRequest("Ciencia da Computacao", "Bacharelado atualizado", 3600);

        ResponseEntity<CursoResponse> updateResponse = restTemplate.exchange(
                "/api/cursos/{uuid}", HttpMethod.PUT,
                new HttpEntity<>(updateRequest), CursoResponse.class, uuid);

        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponse.getBody().cargaHoraria()).isEqualTo(3600);

        // READ list (paginado)
        ResponseEntity<String> listResponse = restTemplate.getForEntity(
                "/api/cursos?page=0&size=10", String.class);

        assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(listResponse.getBody()).contains("Ciencia da Computacao");

        // DELETE
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                "/api/cursos/{uuid}", HttpMethod.DELETE, null, Void.class, uuid);

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // DELETE idempotente: recurso ja removido ainda retorna 204
        ResponseEntity<Void> deleteAgainResponse = restTemplate.exchange(
                "/api/cursos/{uuid}", HttpMethod.DELETE, null, Void.class, uuid);

        assertThat(deleteAgainResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // Verify deleted
        ResponseEntity<String> verifyResponse = restTemplate.getForEntity(
                "/api/cursos/{uuid}", String.class, uuid);

        assertThat(verifyResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Validacao de campos obrigatorios retorna HTTP 400")
    void validacaoCamposObrigatorios() {
        CursoRequest invalidRequest = new CursoRequest("", null, null);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/cursos", invalidRequest, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("nome");
        assertThat(response.getBody()).contains("cargaHoraria");
    }
}
