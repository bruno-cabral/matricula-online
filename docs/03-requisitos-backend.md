# Requisitos do Backend

## Stack

- **Java 25** (LTS) com **Spring Boot 4.1.x**
- API REST funcional
- Banco **PostgreSQL 18** com JPA/Hibernate
- Migrations com **Liquibase**

## Separação de Camadas

O desafio exige **separação clara** entre as camadas. Código concentrado em controllers será avaliado negativamente.

```
src/main/java/com/matriculaonline/
├── controller/          # Controllers REST (entrada HTTP)
├── dto/                 # DTOs de request e response
├── service/             # Lógica de aplicação e orquestração
├── domain/
│   ├── model/           # Entidades de domínio e enums
│   └── exception/       # Exceções de domínio
├── repository/          # Interfaces JPA / persistência
└── config/              # Configurações (Swagger, CORS, etc)
```

### Responsabilidades por camada

| Camada | Responsabilidade |
|--------|-----------------|
| **Controller** | Receber requisições HTTP, validar entrada (Bean Validation), delegar para service, retornar DTOs |
| **DTO** | Objetos de transferência para request/response, desacoplados das entidades |
| **Service / Application** | Orquestrar lógica de negócio, aplicar regras, gerenciar transações |
| **Domain / Model** | Entidades JPA, enums, regras intrínsecas ao domínio |
| **Repository / Persistence** | Acesso a dados via Spring Data JPA |
| **Config** | Configurações transversais (Swagger, CORS, exception handler global) |

## Identificadores na API

- Path params e responses usam **`uuid`** (nunca o `id` sequencial interno).
- DTOs de response incluem `uuid` e **não** incluem `id`.
- Referências entre recursos (ex: criar matrícula) usam `alunoUuid` e `turmaUuid`.
- Ver detalhe em [01-modelo-dominio.md](01-modelo-dominio.md).

## Paginação (obrigatório)

Todos os endpoints de **listagem** (`GET` coleção) devem ser paginados via Spring Data `Pageable`.

| Query param | Padrão | Descrição |
|-------------|--------|-----------|
| `page` | `0` | Índice da página (zero-based) |
| `size` | `20` | Tamanho da página |
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

## CRUDs Obrigatórios

Cada entidade deve ter operações básicas de CRUD (listagens **paginadas**):

| Entidade | Create | Read (lista paginada) | Read (por uuid) | Update | Delete |
|----------|--------|----------------------|-----------------|--------|--------|
| Aluno | POST /api/alunos | GET /api/alunos?page&size&sort | GET /api/alunos/{uuid} | PUT /api/alunos/{uuid} | DELETE /api/alunos/{uuid} |
| Curso | POST /api/cursos | GET /api/cursos?page&size&sort | GET /api/cursos/{uuid} | PUT /api/cursos/{uuid} | DELETE /api/cursos/{uuid} |
| Disciplina | POST /api/disciplinas | GET /api/disciplinas?page&size&sort | GET /api/disciplinas/{uuid} | PUT /api/disciplinas/{uuid} | DELETE /api/disciplinas/{uuid} |
| Turma | POST /api/turmas | GET /api/turmas?page&size&sort | GET /api/turmas/{uuid} | PUT /api/turmas/{uuid} | DELETE /api/turmas/{uuid} |

## Endpoints de Matrícula

Além do CRUD básico, a matrícula possui operações específicas:

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | /api/matriculas | Criar matrícula (status PENDENTE); body com `alunoUuid` e `turmaUuid` |
| GET | /api/matriculas?page&size&sort&status | Listar matrículas (paginado; filtro opcional por status) |
| GET | /api/matriculas/{uuid} | Buscar matrícula por UUID |
| PATCH | /api/matriculas/{uuid}/confirmar | Confirmar matrícula (idempotente se já CONFIRMADA) |
| PATCH | /api/matriculas/{uuid}/cancelar | Cancelar matrícula (idempotente se já CANCELADA) |
| GET | /api/matriculas/aluno/{alunoUuid}?page&size&status | Matrículas por aluno (paginado) |
| GET | /api/matriculas/turma/{turmaUuid}?page&size&status | Matrículas por turma (paginado) |

## Validações de Entrada

- Usar **Bean Validation** (`@NotNull`, `@NotBlank`, `@Email`, `@Size`, etc.) nos DTOs de request.
- Mensagens de validação devem ser **claras e consistentes**.
- Retornar HTTP 400 com detalhes dos campos inválidos.

Exemplo de resposta de validação:

```json
{
  "status": 400,
  "error": "Erro de validação",
  "details": [
    {
      "campo": "nome",
      "mensagem": "Nome é obrigatório"
    },
    {
      "campo": "email",
      "mensagem": "Email deve ser válido"
    }
  ]
}
```

## Tratamento Padronizado de Erros

- Implementar um **`@RestControllerAdvice`** global para capturar exceções.
- Evitar respostas genéricas (ex: 500 sem detalhes).
- Padronizar o formato de erro em toda a API.

Tipos de erro a tratar:

| Exceção | HTTP Status | Descrição |
|---------|------------|-----------|
| Entidade não encontrada | 404 | Recurso não existe |
| Validação de entrada | 400 | Campos inválidos |
| Regra de negócio violada | 422 ou 409 | Ex: turma lotada, matrícula duplicada |
| Erro inesperado | 500 | Erro interno do servidor |

Formato padrão de erro:

```json
{
  "status": 422,
  "error": "Regra de negócio violada",
  "message": "Não há vagas disponíveis nesta turma",
  "timestamp": "2025-01-15T10:30:00"
}
```
