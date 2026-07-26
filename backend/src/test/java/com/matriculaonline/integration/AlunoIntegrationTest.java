package com.matriculaonline.integration;

import com.matriculaonline.dto.request.AlunoRequest;
import com.matriculaonline.dto.response.AlunoResponse;
import com.matriculaonline.repository.AlunoRepository;
import com.matriculaonline.repository.MatriculaRepository;
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

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@ActiveProfiles("test")
class AlunoIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private MatriculaRepository matriculaRepository;

    @BeforeEach
    void setUp() {
        matriculaRepository.deleteAll();
        alunoRepository.deleteAll();
    }

    @Test
    @DisplayName("CRUD completo de Aluno via API")
    void crudCompletoAluno() {
        // CREATE
        AlunoRequest createRequest = new AlunoRequest(
                "Ana Santos", "ana@email.com", "99988877766", LocalDate.of(1999, 3, 20));

        ResponseEntity<AlunoResponse> createResponse = restTemplate.postForEntity(
                "/api/alunos", createRequest, AlunoResponse.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse.getBody()).isNotNull();
        assertThat(createResponse.getBody().nome()).isEqualTo("Ana Santos");
        assertThat(createResponse.getBody().uuid()).isNotNull();

        var uuid = createResponse.getBody().uuid();

        // READ by UUID
        ResponseEntity<AlunoResponse> readResponse = restTemplate.getForEntity(
                "/api/alunos/{uuid}", AlunoResponse.class, uuid);

        assertThat(readResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(readResponse.getBody().email()).isEqualTo("ana@email.com");

        // UPDATE
        AlunoRequest updateRequest = new AlunoRequest(
                "Ana Santos Silva", "ana.silva@email.com", "99988877766", LocalDate.of(1999, 3, 20));

        ResponseEntity<AlunoResponse> updateResponse = restTemplate.exchange(
                "/api/alunos/{uuid}", HttpMethod.PUT,
                new HttpEntity<>(updateRequest), AlunoResponse.class, uuid);

        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponse.getBody().nome()).isEqualTo("Ana Santos Silva");

        // READ list (paginado)
        ResponseEntity<String> listResponse = restTemplate.getForEntity(
                "/api/alunos?page=0&size=10", String.class);

        assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(listResponse.getBody()).contains("Ana Santos Silva");

        // DELETE
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                "/api/alunos/{uuid}", HttpMethod.DELETE, null, Void.class, uuid);

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // DELETE idempotente: recurso ja removido ainda retorna 204
        ResponseEntity<Void> deleteAgainResponse = restTemplate.exchange(
                "/api/alunos/{uuid}", HttpMethod.DELETE, null, Void.class, uuid);

        assertThat(deleteAgainResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // Verify deleted
        ResponseEntity<String> verifyResponse = restTemplate.getForEntity(
                "/api/alunos/{uuid}", String.class, uuid);

        assertThat(verifyResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Validacao de campos obrigatorios retorna HTTP 400")
    void validacaoCamposObrigatorios() {
        AlunoRequest invalidRequest = new AlunoRequest("", "", "", null);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/alunos", invalidRequest, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("nome");
        assertThat(response.getBody()).contains("email");
    }
}
