# Infraestrutura e Ambiente

## Docker Compose (Obrigatorio)

O projeto deve incluir um `docker-compose.yml` que provisione pelo menos o banco de dados. A ausencia de Docker Compose ou instrucao equivalente confiavel e **criterio eliminatorio**.

### Estrutura minima

```yaml
# docker-compose.yml
version: '3.8'

services:
  db:
    image: postgres:18-alpine
    container_name: matricula-online-db
    environment:
      POSTGRES_DB: matricula_online
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

volumes:
  postgres_data:
```

### Opcional: incluir backend e frontend no Compose

```yaml
services:
  db:
    # ... (como acima)

  backend:
    build: ./backend
    container_name: matricula-online-backend
    ports:
      - "8080:8080"
    depends_on:
      - db
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/matricula_online
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: postgres

  frontend:
    build: ./frontend
    container_name: matricula-online-frontend
    ports:
      - "4200:80"
    depends_on:
      - backend
```

## Execucao Local

O README deve documentar passos claros para rodar o projeto:

### Passo a passo minimo

1. **Pre-requisitos:** Docker, Docker Compose, **Java 25**, **Node.js 24** (LTS)
2. **Subir o banco:**
   ```bash
   docker-compose up -d db
   ```
3. **Rodar o backend:**
   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```
4. **Rodar o frontend:**
   ```bash
   cd frontend
   npm install
   npm start
   ```
5. **Acessar:**
   - Frontend: http://localhost:4200
   - API: http://localhost:8080
   - Swagger: http://localhost:8080/swagger-ui.html

### Alternativa: tudo via Docker Compose

```bash
docker-compose up -d
```

## Configuracao de Ambiente

### Profiles do Spring Boot

| Profile | Banco | Uso |
|---------|-------|-----|
| `default` / `dev` | PostgreSQL (Docker) | Desenvolvimento local |
| `test` | H2 em memoria | Execucao de testes |

### Arquivos de configuracao

```
src/main/resources/
├── application.yml              # Configuracao padrao
├── application-dev.yml          # Sobrescritas para desenvolvimento
└── application-test.yml         # Sobrescritas para testes
```

## Pontos de Atencao

- O projeto **deve rodar** com as instrucoes do README. "Projeto nao roda" e criterio eliminatorio.
- Testar o fluxo completo em maquina limpa (ou pelo menos com um `docker-compose down -v` antes).
- Garantir que as migrations executam corretamente em um banco vazio.
- Nao deixar credenciais sensiveis no docker-compose (para o desafio, valores simples como `postgres/postgres` sao aceitaveis).
