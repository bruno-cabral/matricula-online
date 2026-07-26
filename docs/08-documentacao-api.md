# Documentação da API (Swagger/OpenAPI)

## Requisito

Swagger/OpenAPI deve estar disponível para consulta e teste dos endpoints. É item obrigatório.

## Configuração

### Dependência Maven

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.5.0</version>
</dependency>
```

### Acesso

| Recurso | URL |
|---------|-----|
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |

### Configuração no Spring

```yaml
# application.yml
springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
    operations-sorter: method
```

## Identificadores

- Todas as rotas usam **`{uuid}`** (UUID), nunca o `id` sequencial interno.
- Responses e refs entre recursos expõe apenas `uuid` / `alunoUuid` / `turmaUuid` / etc.
- Detalhes em [01-modelo-dominio.md](01-modelo-dominio.md).

## Paginação

Listagens retornam página (`page`, `size`, `sort`). Ver [03-requisitos-backend.md](03-requisitos-backend.md).

## Endpoints Esperados

### Aluno

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | /api/alunos | Cadastrar aluno |
| GET | /api/alunos?page&size&sort | Listar alunos (paginado) |
| GET | /api/alunos/{uuid} | Buscar aluno por UUID |
| PUT | /api/alunos/{uuid} | Atualizar aluno |
| DELETE | /api/alunos/{uuid} | Remover aluno |

### Curso

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | /api/cursos | Cadastrar curso |
| GET | /api/cursos?page&size&sort | Listar cursos (paginado) |
| GET | /api/cursos/{uuid} | Buscar curso por UUID |
| PUT | /api/cursos/{uuid} | Atualizar curso |
| DELETE | /api/cursos/{uuid} | Remover curso |

### Disciplina

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | /api/disciplinas | Cadastrar disciplina |
| GET | /api/disciplinas?page&size&sort | Listar disciplinas (paginado) |
| GET | /api/disciplinas/{uuid} | Buscar disciplina por UUID |
| PUT | /api/disciplinas/{uuid} | Atualizar disciplina |
| DELETE | /api/disciplinas/{uuid} | Remover disciplina |

### Turma

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | /api/turmas | Cadastrar turma |
| GET | /api/turmas?page&size&sort | Listar turmas (paginado) |
| GET | /api/turmas/{uuid} | Buscar turma por UUID |
| PUT | /api/turmas/{uuid} | Atualizar turma |
| DELETE | /api/turmas/{uuid} | Remover turma |

### Matrícula

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | /api/matriculas | Criar matrícula (`alunoUuid`, `turmaUuid`) |
| GET | /api/matriculas?page&size&sort&status | Listar matrículas (paginado; filtro status) |
| GET | /api/matriculas/{uuid} | Buscar matrícula por UUID |
| PATCH | /api/matriculas/{uuid}/confirmar | Confirmar matrícula (idempotente se já CONFIRMADA) |
| PATCH | /api/matriculas/{uuid}/cancelar | Cancelar matrícula (idempotente se já CANCELADA) |
| GET | /api/matriculas/aluno/{alunoUuid}?page&size&status | Matrículas por aluno (paginado) |
| GET | /api/matriculas/turma/{turmaUuid}?page&size&status | Matrículas por turma (paginado) |

## Respostas HTTP Padrão

| Status | Descrição | Quando usar |
|--------|-----------|-------------|
| 200 | OK | GET, PUT, PATCH com sucesso |
| 201 | Created | POST com sucesso |
| 204 | No Content | DELETE com sucesso |
| 400 | Bad Request | Validação de entrada falhou |
| 404 | Not Found | Recurso não encontrado |
| 409/422 | Conflict / Unprocessable | Regra de negócio violada |
| 500 | Internal Server Error | Erro inesperado |

## Pontos de Atenção

- Os endpoints devem ser testáveis diretamente pelo Swagger UI.
- Documentar os DTOs de request e response (o SpringDoc faz isso automaticamente com as classes Java).
- Incluir exemplos de payloads quando possível (usando `@Schema` do OpenAPI).
