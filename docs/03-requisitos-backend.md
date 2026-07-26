# Requisitos do Backend

## Stack

- **Java 25** (LTS) com **Spring Boot 4.1.x**
- API REST funcional
- Banco **PostgreSQL 18** com JPA/Hibernate
- Migrations com **Liquibase**

## Separacao de Camadas

O desafio exige **separacao clara** entre as camadas. Codigo concentrado em controllers sera avaliado negativamente.

```
src/main/java/com/matriculaonline/
├── controller/          # Controllers REST (entrada HTTP)
├── dto/                 # DTOs de request e response
├── service/             # Logica de aplicacao e orquestracao
├── domain/
│   ├── model/           # Entidades de dominio e enums
│   └── exception/       # Excecoes de dominio
├── repository/          # Interfaces JPA / persistencia
└── config/              # Configuracoes (Swagger, CORS, etc)
```

### Responsabilidades por camada

| Camada | Responsabilidade |
|--------|-----------------|
| **Controller** | Receber requisicoes HTTP, validar entrada (Bean Validation), delegar para service, retornar DTOs |
| **DTO** | Objetos de transferencia para request/response, desacoplados das entidades |
| **Service / Application** | Orquestrar logica de negocio, aplicar regras, gerenciar transacoes |
| **Domain / Model** | Entidades JPA, enums, regras intrinsecas ao dominio |
| **Repository / Persistence** | Acesso a dados via Spring Data JPA |
| **Config** | Configuracoes transversais (Swagger, CORS, exception handler global) |

## Identificadores na API

- Path params e responses usam **`uuid`** (nunca o `id` sequencial interno).
- DTOs de response incluem `uuid` e **nao** incluem `id`.
- Referencias entre recursos (ex: criar matricula) usam `alunoUuid` e `turmaUuid`.
- Ver detalhe em [01-modelo-dominio.md](01-modelo-dominio.md).

## Paginacao (obrigatorio)

Todos os endpoints de **listagem** (`GET` colecao) devem ser paginados via Spring Data `Pageable`.

| Query param | Padrao | Descricao |
|-------------|--------|-----------|
| `page` | `0` | Indice da pagina (zero-based) |
| `size` | `20` | Tamanho da pagina |
| `sort` | conforme recurso | Ex: `sort=nome,asc` |

Formato de resposta sugerido (`Page` do Spring ou DTO equivalente):

```json
{
  "content": [ /* itens */ ],
  "page": 0,
  "size": 20,
  "totalElements": 100,
  "totalPages": 5
}
```

Exemplos:
```
GET /api/alunos?page=0&size=10&sort=nome,asc
GET /api/matriculas?status=PENDENTE&page=0&size=20
GET /api/matriculas/aluno/{alunoUuid}?page=0&size=10
GET /api/turmas?page=0&size=10&sort=codigo,asc
```

## CRUDs Obrigatorios

Cada entidade deve ter operacoes basicas de CRUD (listagens **paginadas**):

| Entidade | Create | Read (lista paginada) | Read (por uuid) | Update | Delete |
|----------|--------|----------------------|-----------------|--------|--------|
| Aluno | POST /api/alunos | GET /api/alunos?page&size&sort | GET /api/alunos/{uuid} | PUT /api/alunos/{uuid} | DELETE /api/alunos/{uuid} |
| Curso | POST /api/cursos | GET /api/cursos?page&size&sort | GET /api/cursos/{uuid} | PUT /api/cursos/{uuid} | DELETE /api/cursos/{uuid} |
| Disciplina | POST /api/disciplinas | GET /api/disciplinas?page&size&sort | GET /api/disciplinas/{uuid} | PUT /api/disciplinas/{uuid} | DELETE /api/disciplinas/{uuid} |
| Turma | POST /api/turmas | GET /api/turmas?page&size&sort | GET /api/turmas/{uuid} | PUT /api/turmas/{uuid} | DELETE /api/turmas/{uuid} |

## Endpoints de Matricula

Alem do CRUD basico, a matricula possui operacoes especificas:

| Metodo | Endpoint | Descricao |
|--------|----------|-----------|
| POST | /api/matriculas | Criar matricula (status PENDENTE); body com `alunoUuid` e `turmaUuid` |
| GET | /api/matriculas?page&size&sort&status | Listar matriculas (paginado; filtro opcional por status) |
| GET | /api/matriculas/{uuid} | Buscar matricula por UUID |
| PATCH | /api/matriculas/{uuid}/confirmar | Confirmar matricula (idempotente se ja CONFIRMADA) |
| PATCH | /api/matriculas/{uuid}/cancelar | Cancelar matricula (idempotente se ja CANCELADA) |
| GET | /api/matriculas/aluno/{alunoUuid}?page&size&status | Matriculas por aluno (paginado) |
| GET | /api/matriculas/turma/{turmaUuid}?page&size&status | Matriculas por turma (paginado) |

## Validacoes de Entrada

- Usar **Bean Validation** (`@NotNull`, `@NotBlank`, `@Email`, `@Size`, etc.) nos DTOs de request.
- Mensagens de validacao devem ser **claras e consistentes**.
- Retornar HTTP 400 com detalhes dos campos invalidos.

Exemplo de resposta de validacao:

```json
{
  "status": 400,
  "error": "Erro de validacao",
  "details": [
    {
      "campo": "nome",
      "mensagem": "Nome e obrigatorio"
    },
    {
      "campo": "email",
      "mensagem": "Email deve ser valido"
    }
  ]
}
```

## Tratamento Padronizado de Erros

- Implementar um **`@RestControllerAdvice`** global para capturar excecoes.
- Evitar respostas genericas (ex: 500 sem detalhes).
- Padronizar o formato de erro em toda a API.

Tipos de erro a tratar:

| Excecao | HTTP Status | Descricao |
|---------|------------|-----------|
| Entidade nao encontrada | 404 | Recurso nao existe |
| Validacao de entrada | 400 | Campos invalidos |
| Regra de negocio violada | 422 ou 409 | Ex: turma lotada, matricula duplicada |
| Erro inesperado | 500 | Erro interno do servidor |

Formato padrao de erro:

```json
{
  "status": 422,
  "error": "Regra de negocio violada",
  "message": "Nao ha vagas disponiveis nesta turma",
  "timestamp": "2025-01-15T10:30:00"
}
```
