# Matricula Online

Sistema de gerenciamento de matriculas online desenvolvido como desafio tecnico para a posicao de Desenvolvedor(a) Pleno Full Stack na Tribe Lyceum - Techne.

## Pre-requisitos

| Ferramenta | Versao |
|-----------|--------|
| Docker e Docker Compose | Versao recente |

> Java, Maven e Node.js **nao** sao obrigatorios para executar o projeto. Eles so sao necessarios para desenvolvimento local fora do Docker.

## Como Executar (Docker)

Suba **banco + backend + frontend** com um unico comando:

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

### Comandos uteis

```bash
# Ver status dos containers
docker compose ps

# Acompanhar logs
docker compose logs -f

# Parar tudo
docker compose down

# Parar e apagar o banco (recria seed na proxima subida)
docker compose down -v
```

> **Nota (PostgreSQL 18):** o volume e montado em `/var/lib/postgresql`. Se o container `db` falhar apos atualizar o Compose, execute `docker compose down -v` e suba novamente.


### Dados iniciais

Na primeira execucao, o Liquibase cria o schema e carrega dados de exemplo (cursos, alunos, disciplinas, turmas e matriculas).

### Credenciais do banco

| Item | Valor |
|------|-------|
| Host | `localhost:5432` |
| Database | `matricula_online` |
| Usuario | `postgres` |
| Senha | `postgres` |

---

## Execucao local (alternativa)

Se preferir rodar backend/frontend na maquina e apenas o banco no Docker:

**Pre-requisitos extras:** Java 25, Maven 3.9+, Node.js 24

```bash
# 1. Banco
docker compose up -d db

# 2. Backend
cd backend
mvn spring-boot:run

# 3. Frontend
cd frontend
npm install
npm start
```

## Testes

Os testes do backend rodam na maquina (usam H2 em memoria, sem Docker):

```bash
cd backend
mvn test
```

Os testes unitarios cobrem todas as regras criticas de matricula (RN01-RN07).
Os testes de integracao validam os fluxos completos via API REST.

**Cenarios testados:**

| # | Cenario | Tipo |
|---|---------|------|
| 1 | Matricular aluno em turma aberta com vagas | Unitario |
| 2 | Matricular em turma fechada (RN01) | Unitario |
| 3 | Matricular em turma sem vagas (RN02) | Unitario |
| 4 | Matricula duplicada (RN03) | Unitario |
| 5 | Confirmar matricula pendente (RN05) | Unitario |
| 6 | Confirmar sem vagas disponiveis | Unitario |
| 7 | Cancelar matricula confirmada - vaga liberada (RN06) | Unitario |
| 8 | Cancelar matricula pendente - sem alterar vagas | Unitario |
| 9 | Confirmar ja confirmada - idempotente (RN04) | Unitario |
| 10 | Cancelar ja cancelada - idempotente (RN04) | Unitario |
| 11 | Confirmar matricula cancelada - erro (RN04) | Unitario |
| 12 | Rematricula apos cancelamento | Unitario |
| 13 | Fluxo completo criar -> confirmar | Integracao |
| 14 | Fluxo cancelamento com liberacao de vaga | Integracao |
| 15 | Matricula duplicada via API | Integracao |
| 16 | Matricula em turma lotada via API | Integracao |
| 17 | Consulta por aluno via API (RN07) | Integracao |
| 18 | Consulta por turma via API (RN07) | Integracao |
| 19 | CRUD completo de Aluno via API | Integracao |
| 20 | Validacao de campos obrigatorios | Integracao |

## Documentacao da API

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8080/v3/api-docs

## Tecnologias Utilizadas

| Categoria | Tecnologia | Versao |
|-----------|-----------|--------|
| Backend | Java (LTS) | 25 |
| Framework | Spring Boot | 4.1.0 |
| Persistencia | Spring Data JPA / Hibernate | Gerenciado pelo Spring Boot |
| Banco de dados | PostgreSQL | 18 |
| Migrations | Liquibase | Gerenciado pelo Spring Boot |
| Documentacao API | springdoc-openapi | 3.0.3 |
| Logs estruturados | logstash-logback-encoder | 8.1 |
| Testes | JUnit 5, Mockito, H2 | Gerenciado pelo Spring Boot |
| Frontend | Angular | 22 |
| Runtime frontend | Node.js (Active LTS) | 24 |
| Infraestrutura | Docker, Docker Compose, Nginx | - |

## Decisoes Tecnicas

### Arquitetura em Camadas

O backend segue uma separacao clara de responsabilidades:

- **Controller:** Recebe requisicoes HTTP, valida entrada (Bean Validation), delega para service e retorna DTOs.
- **Service:** Contem a logica de negocio e orquestracao. Todas as regras de matricula (RN01-RN07) estao nesta camada.
- **Domain/Model:** Entidades JPA com metodos de dominio (ex: `temVagasDisponiveis()`, `isAberta()`).
- **Repository:** Interfaces Spring Data JPA para acesso a dados.
- **DTO:** Records Java para request/response, desacoplados das entidades.

### UUID Publico + ID Long Interno

Todas as entidades possuem duplo identificador:

- `id` (Long): Chave primaria interna para FKs e joins. **Nunca exposta na API.**
- `uuid` (UUID): Identificador publico para rotas e DTOs. Evita enumeracao de IDs sequenciais.

### Paginacao Obrigatoria

Todos os endpoints de listagem usam `Pageable` do Spring Data com formato padronizado (`page`, `size`, `totalElements`, `totalPages`).

### Idempotencia

Confirmar uma matricula ja CONFIRMADA ou cancelar uma ja CANCELADA retorna sucesso (HTTP 200) sem alterar o banco (no-op), conforme especificado.

Os endpoints DELETE de Aluno, Curso, Disciplina e Turma tambem sao idempotentes: excluir um recurso inexistente retorna HTTP 204 sem erro.

### Liquibase para Migrations

O controle de schema e feito exclusivamente pelo Liquibase. O Hibernate esta configurado com `ddl-auto: validate` para garantir alinhamento entre entidades e banco.

### Infraestrutura Docker

Todo o stack sobe via Docker Compose:

- **db:** PostgreSQL 18
- **backend:** imagem multi-stage (Maven build + JRE 25)
- **frontend:** imagem multi-stage (Angular build + Nginx), com proxy de `/api` para o backend

### Frontend Organizado

O frontend Angular 22 utiliza:
- Standalone components (padrao do Angular 22)
- Services dedicados para cada entidade
- Tratamento centralizado de erros da API
- Filtro por status e paginacao na tela de matriculas

### Logs Estruturados (D03)

O backend utiliza SLF4J + Logback com `logstash-logback-encoder` para emitir logs em JSON estruturado:

- **Producao (Docker):** Saida JSON via `LogstashEncoder`, ideal para coleta por ferramentas como ELK, Datadog ou CloudWatch.
- **Desenvolvimento local (`dev`):** Formato texto legivel no console. Ativar com `spring.profiles.active=dev`.
- **Testes (`test`):** Nivel WARN para reduzir ruido.

O `MatriculaService` utiliza MDC (Mapped Diagnostic Context) para enriquecer os logs com contexto transacional (`matriculaUuid`, `alunoUuid`, `turmaUuid`). O `GlobalExceptionHandler` loga todas as excecoes tratadas (WARN para erros de negocio, ERROR com stack trace para erros inesperados).

Exemplo de saida JSON:
```json
{
  "timestamp": "2025-01-15T10:30:00.000-03:00",
  "level": "INFO",
  "logger_name": "c.m.service.MatriculaService",
  "message": "Matricula confirmada",
  "matriculaUuid": "c1d2e3f4-5a6b-7c8d-9e0f-1a2b3c4d5e6f",
  "alunoUuid": "b2e4d9f1-3a6c-4f8b-8d0e-1c7f6a5b4e3d",
  "turmaUuid": "a3f1c8e2-4b5d-4e9a-9c1f-2d8e7a6b5c4d"
}
```

## Protecao da Regra de Vagas

A protecao contra consumo excessivo de vagas e implementada com:

1. **Lock Otimista (`@Version`):** A entidade `Turma` possui um campo `version` anotado com `@Version`. Se duas requisicoes tentarem modificar `vagasOcupadas` simultaneamente, uma delas recebera `ObjectOptimisticLockingFailureException`, que e tratada pelo `GlobalExceptionHandler` retornando HTTP 409.
2. **Transacionalidade (`@Transactional`):** Os metodos `confirmar()` e `cancelar()` do `MatriculaService` sao transacionais, garantindo atomicidade na verificacao de vagas e atualizacao do contador.
3. **Validacao no Service:** Antes de confirmar, o sistema verifica se `turma.temVagasDisponiveis()`. Se nao, rejeita com excecao de negocio.

## Testes das Regras Criticas

As regras de matricula sao testadas em dois niveis:

- **Unitarios (`MatriculaServiceTest`):** 12 cenarios com Mockito, cobrindo todos os caminhos de sucesso e erro de RN01 a RN07.
- **Integracao (`MatriculaIntegrationTest`):** 6 cenarios end-to-end com `TestRestTemplate` e banco H2, validando persistencia e respostas HTTP.

## Limitacoes Conhecidas

- Frontend sem testes automatizados (priorizados os testes do backend por serem criticos na avaliacao).
- Sem CI/CD configurado (diferencial D01).
- Sem eventos de dominio (diferencial D04).
- A validacao de CPF e simplificada (apenas verifica tamanho, sem digito verificador).
- Sem tratamento de soft delete em cascata (deletar entidades com dependencias pode falhar).

## Uso de IA

- **Ferramenta utilizada:** Cursor IDE com assistente de IA (Claude).
- **Onde foi utilizado:** Em todas as etapas - estruturacao do projeto, implementacao das camadas, criacao dos testes, frontend e infraestrutura Docker.
- **Decisoes revisadas manualmente:**
  - Regras de negocio do `MatriculaService` - conferidas contra a especificacao documento por documento.
  - Fluxo de status e idempotencia - validados contra o diagrama de estados da especificacao.
  - Tratamento transacional e lock otimista - decisao consciente de usar `@Version` ao inves de lock pessimista.
  - Migrations Liquibase - verificadas para compatibilidade H2/PostgreSQL.
  - Compose/Dockerfiles - backend depende do healthcheck do banco; frontend usa Nginx com proxy `/api`.
- **Trechos mais criticos:**
  - `MatriculaService.confirmar()` e `MatriculaService.cancelar()` - regras de consumo/liberacao de vaga.
  - `GlobalExceptionHandler` - padronizacao de respostas de erro.
  - `MatriculaServiceTest` - cobertura dos 12 cenarios obrigatorios.
