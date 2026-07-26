# Documentacao da API (Swagger/OpenAPI)

## Requisito

Swagger/OpenAPI deve estar disponivel para consulta e teste dos endpoints. E item obrigatorio.

## Configuracao

### Dependencia Maven

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

### Configuracao no Spring

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
- Responses e refs entre recursos expoe apenas `uuid` / `alunoUuid` / `turmaUuid` / etc.
- Detalhes em [01-modelo-dominio.md](01-modelo-dominio.md).

## Paginacao

Listagens retornam pagina (`page`, `size`, `sort`). Ver [03-requisitos-backend.md](03-requisitos-backend.md).

## Endpoints Esperados

### Aluno

| Metodo | Endpoint | Descricao |
|--------|----------|-----------|
| POST | /api/alunos | Cadastrar aluno |
| GET | /api/alunos?page&size&sort | Listar alunos (paginado) |
| GET | /api/alunos/{uuid} | Buscar aluno por UUID |
| PUT | /api/alunos/{uuid} | Atualizar aluno |
| DELETE | /api/alunos/{uuid} | Remover aluno |

### Curso

| Metodo | Endpoint | Descricao |
|--------|----------|-----------|
| POST | /api/cursos | Cadastrar curso |
| GET | /api/cursos?page&size&sort | Listar cursos (paginado) |
| GET | /api/cursos/{uuid} | Buscar curso por UUID |
| PUT | /api/cursos/{uuid} | Atualizar curso |
| DELETE | /api/cursos/{uuid} | Remover curso |

### Disciplina

| Metodo | Endpoint | Descricao |
|--------|----------|-----------|
| POST | /api/disciplinas | Cadastrar disciplina |
| GET | /api/disciplinas?page&size&sort | Listar disciplinas (paginado) |
| GET | /api/disciplinas/{uuid} | Buscar disciplina por UUID |
| PUT | /api/disciplinas/{uuid} | Atualizar disciplina |
| DELETE | /api/disciplinas/{uuid} | Remover disciplina |

### Turma

| Metodo | Endpoint | Descricao |
|--------|----------|-----------|
| POST | /api/turmas | Cadastrar turma |
| GET | /api/turmas?page&size&sort | Listar turmas (paginado) |
| GET | /api/turmas/{uuid} | Buscar turma por UUID |
| PUT | /api/turmas/{uuid} | Atualizar turma |
| DELETE | /api/turmas/{uuid} | Remover turma |

### Matricula

| Metodo | Endpoint | Descricao |
|--------|----------|-----------|
| POST | /api/matriculas | Criar matricula (`alunoUuid`, `turmaUuid`) |
| GET | /api/matriculas?page&size&sort&status | Listar matriculas (paginado; filtro status) |
| GET | /api/matriculas/{uuid} | Buscar matricula por UUID |
| PATCH | /api/matriculas/{uuid}/confirmar | Confirmar matricula (idempotente se ja CONFIRMADA) |
| PATCH | /api/matriculas/{uuid}/cancelar | Cancelar matricula (idempotente se ja CANCELADA) |
| GET | /api/matriculas/aluno/{alunoUuid}?page&size&status | Matriculas por aluno (paginado) |
| GET | /api/matriculas/turma/{turmaUuid}?page&size&status | Matriculas por turma (paginado) |

## Respostas HTTP Padrao

| Status | Descricao | Quando usar |
|--------|-----------|-------------|
| 200 | OK | GET, PUT, PATCH com sucesso |
| 201 | Created | POST com sucesso |
| 204 | No Content | DELETE com sucesso |
| 400 | Bad Request | Validacao de entrada falhou |
| 404 | Not Found | Recurso nao encontrado |
| 409/422 | Conflict / Unprocessable | Regra de negocio violada |
| 500 | Internal Server Error | Erro inesperado |

## Pontos de Atencao

- Os endpoints devem ser testáveis diretamente pelo Swagger UI.
- Documentar os DTOs de request e response (o SpringDoc faz isso automaticamente com as classes Java).
- Incluir exemplos de payloads quando possivel (usando `@Schema` do OpenAPI).
