# Requisitos do README

## Importancia

O README e **parte essencial da avaliacao**. Um README incompleto ou que nao permite reproduzir a execucao e criterio eliminatorio.

## Secoes Obrigatorias

### 1. Como rodar o projeto localmente

- Listar pre-requisitos (Docker, Java, Node.js com versoes).
- Passo a passo para subir toda a aplicacao.
- Comando unico quando possivel (ex: `docker-compose up`).

### 2. Como subir o banco de dados com Docker Compose

- Comando exato para iniciar o banco.
- Porta e credenciais de acesso.
- Como verificar que o banco esta rodando.

### 3. Como executar os testes automatizados

- Comando para testes unitarios.
- Comando para testes de integracao.
- Pré-requisitos para rodar os testes (banco em pé, profile de test, etc.).

### 4. Como acessar a documentacao Swagger/OpenAPI

- URL do Swagger UI.
- URL do JSON do OpenAPI (se aplicavel).

### 5. Tecnologias utilizadas

Lista com versoes das principais tecnologias:

| Categoria | Tecnologia |
|-----------|-----------|
| Backend | Java 25 (LTS), Spring Boot 4.1.x, Spring Data JPA |
| Banco de dados | PostgreSQL 18 |
| Migrations | Liquibase |
| Testes | JUnit 5, Mockito, H2/Testcontainers |
| Frontend | Angular 22, TypeScript, Node.js 24 (Active LTS) |
| Documentacao | SpringDoc OpenAPI |
| Infraestrutura | Docker, Docker Compose |

### 6. Principais decisoes tecnicas

Explicar as escolhas feitas e por que:

- Por que escolheu determinada estrutura de camadas.
- Por que usou **Liquibase** / **PostgreSQL 18** / **Angular 22** / **Java 25**.
- Como decidiu o modelo de dados.
- Por que UUID publico + id Long interno (nao expor IDs sequenciais na API).
- Paginacao obrigatoria nas listagens.
- Idempotencia de confirmar/cancelar (no-op quando ja no status alvo).
- Quais trade-offs foram feitos (ex: simplicidade vs extensibilidade).

### 7. Como a regra de vagas foi protegida

Explicar especificamente:

- Qual mecanismo protege contra consumo excessivo de vagas.
- Se usa lock otimista (`@Version`), lock pessimista (`SELECT FOR UPDATE`), ou outro mecanismo.
- Como foi testado esse cenario.

### 8. Como foram testadas as regras criticas de matricula

- Quais regras possuem testes.
- Tipos de teste usados (unitario, integracao).
- Cenarios cobertos.

### 9. Limitacoes conhecidas

Ser honesto sobre o que nao foi implementado ou pode ser melhorado:

- Funcionalidades nao implementadas.
- Cenarios nao cobertos por testes.
- Melhorias que faria com mais tempo.

### 10. Uso de IA

- Quais ferramentas de IA foram utilizadas.
- Em quais partes do projeto foram usadas.
- Quais decisoes foram revisadas manualmente.
- Quais trechos considera mais criticos.

## Formato Sugerido

```markdown
# Matricula Online

## Sobre o Projeto
Breve descricao...

## Pre-requisitos
- Docker e Docker Compose
- Java 25 (LTS)
- Node.js 24 (Active LTS)

## Como Executar

### Banco de Dados
\```bash
docker-compose up -d db
\```

### Backend
\```bash
cd backend
./mvnw spring-boot:run
\```

### Frontend
\```bash
cd frontend
npm install && npm start
\```

## Testes
\```bash
cd backend
./mvnw test
\```

## Documentacao da API
Swagger UI: http://localhost:8080/swagger-ui.html

## Tecnologias
...

## Decisoes Tecnicas
...

## Protecao da Regra de Vagas
...

## Testes das Regras Criticas
...

## Limitacoes Conhecidas
...

## Uso de IA
...
```

## Pontos de Atencao

- O README sera lido **antes** do codigo. Primeira impressao importa.
- Instrucoes que nao funcionam desqualificam a entrega.
- Ser claro e conciso. Nao e necessario escrever um livro, mas cobrir todos os pontos.
