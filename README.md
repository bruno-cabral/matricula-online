# Matrícula Online

Sistema de gerenciamento de matrículas online desenvolvido como desafio técnico para a posição de Desenvolvedor(a) Pleno Full Stack na Tribe Lyceum - Techne.

## Pré-requisitos

| Ferramenta | Versão |
|-----------|--------|
| Docker e Docker Compose | Versão recente |

> Java, Maven e Node.js **não** são obrigatórios para executar o projeto. Eles só são necessários para desenvolvimento local fora do Docker.

## Como Executar (Docker)

Suba **banco + backend + frontend** com um único comando:

```bash
docker compose up --build -d
```

Na primeira vez o build pode demorar alguns minutos (Maven e npm).

### Acessos

| Recurso | URL |
|---------|-----|
| Frontend | http://localhost:4200 |
| API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |

### Comandos úteis

```bash
# Ver status dos containers
docker compose ps

# Acompanhar logs
docker compose logs -f

# Parar tudo
docker compose down

# Parar e apagar o banco (recria seed na próxima subida)
docker compose down -v
```

> **Nota (PostgreSQL 18):** o volume é montado em `/var/lib/postgresql`. Se o container `db` falhar após atualizar o Compose, execute `docker compose down -v` e suba novamente.


### Dados iniciais

Na primeira execução, o Liquibase cria o schema e carrega dados de exemplo (cursos, alunos, disciplinas, turmas e matrículas).

### Credenciais do banco

| Item | Valor |
|------|-------|
| Host | `localhost:5432` |
| Database | `matricula_online` |
| Usuário | `postgres` |
| Senha | `postgres` |

---

## Execução local (alternativa)

Se preferir rodar backend/frontend na máquina e apenas o banco no Docker:

**Pré-requisitos extras:** Java 25, Node.js 24

```bash
# 1. Banco
docker compose up -d db

# 2. Backend (Maven Wrapper incluso — não precisa de Maven instalado)
cd backend
./mvnw spring-boot:run

# 3. Frontend
cd frontend
npm install
npm start
```

## Testes

Os testes do backend rodam na máquina (usam H2 em memória, sem Docker):

```bash
cd backend
./mvnw test
```

Os testes unitários cobrem todas as regras críticas de matrícula (RN01-RN07) e CRUD das demais entidades.
Os testes de integração validam os fluxos completos via API REST para todas as entidades.

### Testes unitários de service

| Entidade | Cenários | Classe |
|----------|----------|--------|
| Matrícula | 12 | `MatriculaServiceTest` |
| Aluno | 9 | `AlunoServiceTest` |
| Turma | 9 | `TurmaServiceTest` |
| Disciplina | 8 | `DisciplinaServiceTest` |
| Curso | 7 | `CursoServiceTest` |
| CPF (validação) | 17 | `CpfValidatorTest` |

### Testes de integração (API end-to-end)

| Entidade | Cenários | Classe |
|----------|----------|--------|
| Matrícula | 6 | `MatriculaIntegrationTest` |
| Turma | 4 | `TurmaIntegrationTest` |
| Aluno | 3 | `AlunoIntegrationTest` |
| Disciplina | 3 | `DisciplinaIntegrationTest` |
| Curso | 2 | `CursoIntegrationTest` |

### Cenários de matrícula (regras críticas RN01-RN07)

| # | Cenário | Tipo |
|---|---------|------|
| 1 | Matricular aluno em turma aberta com vagas | Unitário |
| 2 | Matricular em turma fechada (RN01) | Unitário |
| 3 | Matricular em turma sem vagas (RN02) | Unitário |
| 4 | Matrícula duplicada (RN03) | Unitário |
| 5 | Confirmar matrícula pendente (RN05) | Unitário |
| 6 | Confirmar sem vagas disponíveis | Unitário |
| 7 | Cancelar matrícula confirmada - vaga liberada (RN06) | Unitário |
| 8 | Cancelar matrícula pendente - sem alterar vagas | Unitário |
| 9 | Confirmar já confirmada - idempotente (RN04) | Unitário |
| 10 | Cancelar já cancelada - idempotente (RN04) | Unitário |
| 11 | Confirmar matrícula cancelada - erro (RN04) | Unitário |
| 12 | Rematrícula após cancelamento | Unitário |
| 13 | Fluxo completo criar -> confirmar | Integração |
| 14 | Fluxo cancelamento com liberação de vaga | Integração |
| 15 | Matrícula duplicada via API | Integração |
| 16 | Matrícula em turma lotada via API | Integração |
| 17 | Consulta por aluno via API (RN07) | Integração |
| 18 | Consulta por turma via API (RN07) | Integração |

### Cenários das demais entidades

| # | Cenário | Entidade | Tipo |
|---|---------|----------|------|
| 1 | CRUD completo via API | Aluno | Integração |
| 2 | Validação de campos obrigatórios | Aluno | Integração |
| 3 | CPF com dígito verificador inválido (HTTP 400) | Aluno | Integração |
| 4 | CRUD completo via API | Curso | Integração |
| 5 | Validação de campos obrigatórios | Curso | Integração |
| 6 | CRUD completo via API | Disciplina | Integração |
| 7 | Criar com curso inexistente (HTTP 404) | Disciplina | Integração |
| 8 | Validação de campos obrigatórios | Disciplina | Integração |
| 9 | CRUD completo via API | Turma | Integração |
| 10 | Código duplicado (HTTP 409) | Turma | Integração |
| 11 | Criar com disciplina inexistente (HTTP 404) | Turma | Integração |
| 12 | Validação de campos obrigatórios | Turma | Integração |

## Documentação da API

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8080/v3/api-docs

## Tecnologias Utilizadas

| Categoria | Tecnologia | Versão |
|-----------|-----------|--------|
| Backend | Java (LTS) | 25 |
| Framework | Spring Boot | 4.1.0 |
| Persistência | Spring Data JPA / Hibernate | Gerenciado pelo Spring Boot |
| Banco de dados | PostgreSQL | 18 |
| Migrations | Liquibase | Gerenciado pelo Spring Boot |
| Documentação API | springdoc-openapi | 3.0.3 |
| Logs estruturados | logstash-logback-encoder | 8.1 |
| Testes | JUnit 5, Mockito, H2 | Gerenciado pelo Spring Boot |
| Frontend | Angular | 22 |
| Runtime frontend | Node.js (Active LTS) | 24 |
| Infraestrutura | Docker, Docker Compose, Nginx | - |

## Decisões Técnicas

### Arquitetura em Camadas

O backend segue uma separação clara de responsabilidades:

- **Controller:** Recebe requisições HTTP, valida entrada (Bean Validation), delega para service e retorna DTOs.
- **Service:** Contém a lógica de negócio e orquestração. Todas as regras de matrícula (RN01-RN07) estão nesta camada.
- **Domain/Model:** Entidades JPA com métodos de domínio (ex: `temVagasDisponiveis()`, `isAberta()`).
- **Repository:** Interfaces Spring Data JPA para acesso a dados.
- **DTO:** Records Java para request/response, desacoplados das entidades.
- **Validation:** Anotações customizadas de Bean Validation (`@Cpf`) para regras específicas.

### UUID Público + ID Long Interno

Todas as entidades possuem duplo identificador:

- `id` (Long): Chave primária interna para FKs e joins. **Nunca exposta na API.**
- `uuid` (UUID): Identificador público para rotas e DTOs. Evita enumeração de IDs sequenciais.

### Paginação Obrigatória

Todos os endpoints de listagem usam `Pageable` do Spring Data com formato padronizado (`page`, `size`, `totalElements`, `totalPages`).

### Idempotência

Confirmar uma matrícula já CONFIRMADA ou cancelar uma já CANCELADA retorna sucesso (HTTP 200) sem alterar o banco (no-op), conforme especificado.

Os endpoints DELETE de Aluno, Curso, Disciplina e Turma também são idempotentes: excluir um recurso inexistente retorna HTTP 204 sem erro.

### Liquibase para Migrations

O controle de schema é feito exclusivamente pelo Liquibase. O Hibernate está configurado com `ddl-auto: validate` para garantir alinhamento entre entidades e banco.

### Infraestrutura Docker

Todo o stack sobe via Docker Compose:

- **db:** PostgreSQL 18
- **backend:** imagem multi-stage (Maven build + JRE 25)
- **frontend:** imagem multi-stage (Angular build + Nginx), com proxy de `/api` para o backend

### Validação de CPF com Dígitos Verificadores

O CPF é validado com algoritmo completo de dígitos verificadores (mod 11), tanto no backend (`@Cpf` — anotação customizada de Bean Validation) quanto no frontend (diretiva Angular `cpfValidator`). CPFs com todos os dígitos iguais ou com dígitos verificadores incorretos são rejeitados com HTTP 400 e feedback visual no formulário.

### Maven Wrapper

O projeto inclui o Maven Wrapper (`mvnw` / `mvnw.cmd`), dispensando a instalação global do Maven. O wrapper baixa automaticamente o Maven 3.9.16 na primeira execução.

### Frontend Organizado

O frontend Angular 22 utiliza:
- Standalone components (padrão do Angular 22)
- Services dedicados para cada entidade
- Tratamento centralizado de erros da API
- Validação de CPF client-side com diretiva customizada
- Filtro por status e paginação na tela de matrículas

### Logs Estruturados (D03)

O backend utiliza SLF4J + Logback com `logstash-logback-encoder` para emitir logs em JSON estruturado:

- **Produção (Docker):** Saída JSON via `LogstashEncoder`, ideal para coleta por ferramentas como ELK, Datadog ou CloudWatch.
- **Desenvolvimento local (`dev`):** Formato texto legível no console. Ativar com `spring.profiles.active=dev`.
- **Testes (`test`):** Nível WARN para reduzir ruído.

O `MatriculaService` utiliza MDC (Mapped Diagnostic Context) para enriquecer os logs com contexto transacional (`matriculaUuid`, `alunoUuid`, `turmaUuid`). O `GlobalExceptionHandler` loga todas as exceções tratadas (WARN para erros de negócio, ERROR com stack trace para erros inesperados).

Exemplo de saída JSON:
```json
{
  "timestamp": "2025-01-15T10:30:00.000-03:00",
  "level": "INFO",
  "logger_name": "c.m.service.MatriculaService",
  "message": "Matrícula confirmada",
  "matriculaUuid": "c1d2e3f4-5a6b-7c8d-9e0f-1a2b3c4d5e6f",
  "alunoUuid": "b2e4d9f1-3a6c-4f8b-8d0e-1c7f6a5b4e3d",
  "turmaUuid": "a3f1c8e2-4b5d-4e9a-9c1f-2d8e7a6b5c4d"
}
```

## Proteção da Regra de Vagas

A proteção contra consumo excessivo de vagas é implementada com:

1. **Lock Otimista (`@Version`):** A entidade `Turma` possui um campo `version` anotado com `@Version`. Se duas requisições tentarem modificar `vagasOcupadas` simultaneamente, uma delas receberá `ObjectOptimisticLockingFailureException`, que é tratada pelo `GlobalExceptionHandler` retornando HTTP 409.
2. **Transacionalidade (`@Transactional`):** Os métodos `confirmar()` e `cancelar()` do `MatriculaService` são transacionais, garantindo atomicidade na verificação de vagas e atualização do contador.
3. **Validação no Service:** Antes de confirmar, o sistema verifica se `turma.temVagasDisponiveis()`. Se não, rejeita com exceção de negócio.

## Testes das Regras Críticas

As regras de matrícula são testadas em dois níveis:

- **Unitários (`MatriculaServiceTest`):** 12 cenários com Mockito, cobrindo todos os caminhos de sucesso e erro de RN01 a RN07.
- **Integração (`MatriculaIntegrationTest`):** 6 cenários end-to-end com `TestRestTemplate` e banco H2, validando persistência e respostas HTTP.

As demais entidades (Aluno, Curso, Disciplina, Turma) também possuem testes unitários de service e testes de integração end-to-end, cobrindo CRUD, validações e cenários de erro (recurso inexistente, duplicidade, campos obrigatórios).

## Limitações Conhecidas

- Frontend sem testes automatizados (priorizados os testes do backend por serem críticos na avaliação).
- Sem CI/CD configurado (diferencial D01).
- Sem eventos de domínio (diferencial D04).
- Sem tratamento de soft delete em cascata (deletar entidades com dependências pode falhar).

## Uso de IA

- **Ferramenta utilizada:** Cursor IDE com assistente de IA (Claude).
- **Onde foi utilizado:** Em todas as etapas - estruturação do projeto, implementação das camadas, criação dos testes, frontend e infraestrutura Docker.
- **Decisões revisadas manualmente:**
  - Regras de negócio do `MatriculaService` - conferidas contra a especificação documento por documento.
  - Fluxo de status e idempotência - validados contra o diagrama de estados da especificação.
  - Tratamento transacional e lock otimista - decisão consciente de usar `@Version` ao invés de lock pessimista.
  - Migrations Liquibase - verificadas para compatibilidade H2/PostgreSQL.
  - Compose/Dockerfiles - backend depende do healthcheck do banco; frontend usa Nginx com proxy `/api`.
- **Trechos mais críticos:**
  - `MatriculaService.confirmar()` e `MatriculaService.cancelar()` - regras de consumo/liberação de vaga.
  - `GlobalExceptionHandler` - padronização de respostas de erro.
  - `MatriculaServiceTest` - cobertura dos 12 cenários obrigatórios.
