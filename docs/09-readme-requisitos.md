# Requisitos do README

## Importância

O README é **parte essencial da avaliação**. Um README incompleto ou que não permite reproduzir a execução é critério eliminatório.

## Seções Obrigatórias

### 1. Como rodar o projeto localmente

- Listar pré-requisitos (Docker, Java, Node.js com versões).
- Passo a passo para subir toda a aplicação.
- Comando único quando possível (ex: `docker-compose up`).

### 2. Como subir o banco de dados com Docker Compose

- Comando exato para iniciar o banco.
- Porta e credenciais de acesso.
- Como verificar que o banco está rodando.

### 3. Como executar os testes automatizados

- Comando para testes unitários.
- Comando para testes de integração.
- Pré-requisitos para rodar os testes (banco em pé, profile de test, etc.).

### 4. Como acessar a documentação Swagger/OpenAPI

- URL do Swagger UI.
- URL do JSON do OpenAPI (se aplicável).

### 5. Tecnologias utilizadas

Lista com versões das principais tecnologias:

| Categoria | Tecnologia |
|-----------|-----------|
| Backend | Java 25 (LTS), Spring Boot 4.1.x, Spring Data JPA |
| Banco de dados | PostgreSQL 18 |
| Migrations | Liquibase |
| Testes | JUnit 5, Mockito, H2/Testcontainers |
| Frontend | Angular 22, TypeScript, Node.js 24 (Active LTS) |
| Documentação | SpringDoc OpenAPI |
| Infraestrutura | Docker, Docker Compose |

### 6. Principais decisões técnicas

Explicar as escolhas feitas e por quê:

- Por que escolheu determinada estrutura de camadas.
- Por que usou **Liquibase** / **PostgreSQL 18** / **Angular 22** / **Java 25**.
- Como decidiu o modelo de dados.
- Por que UUID público + id Long interno (não expor IDs sequenciais na API).
- Paginação obrigatória nas listagens.
- Idempotência de confirmar/cancelar (no-op quando já no status alvo).
- Quais trade-offs foram feitos (ex: simplicidade vs extensibilidade).

### 7. Como a regra de vagas foi protegida

Explicar especificamente:

- Qual mecanismo protege contra consumo excessivo de vagas.
- Se usa lock otimista (`@Version`), lock pessimista (`SELECT FOR UPDATE`), ou outro mecanismo.
- Como foi testado esse cenário.

### 8. Como foram testadas as regras críticas de matrícula

- Quais regras possuem testes.
- Tipos de teste usados (unitário, integração).
- Cenários cobertos.

### 9. Limitações conhecidas

Ser honesto sobre o que não foi implementado ou pode ser melhorado:

- Funcionalidades não implementadas.
- Cenários não cobertos por testes.
- Melhorias que faria com mais tempo.

### 10. Uso de IA

- Quais ferramentas de IA foram utilizadas.
- Em quais partes do projeto foram usadas.
- Quais decisões foram revisadas manualmente.
- Quais trechos considera mais críticos.

## Formato Sugerido

```markdown
# Matrícula Online

## Sobre o Projeto
Breve descrição...

## Pré-requisitos
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

## Documentação da API
Swagger UI: http://localhost:8080/swagger-ui.html

## Tecnologias
...

## Decisões Técnicas
...

## Proteção da Regra de Vagas
...

## Testes das Regras Críticas
...

## Limitações Conhecidas
...

## Uso de IA
...
```

## Pontos de Atenção

- O README será lido **antes** do código. Primeira impressão importa.
- Instruções que não funcionam desqualificam a entrega.
- Ser claro e conciso. Não é necessário escrever um livro, mas cobrir todos os pontos.
