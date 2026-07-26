# Persistencia e Migrations

## Banco de Dados Relacional

- Banco escolhido: **PostgreSQL 18** (major suportado mais recente; sem LTS formal — suporte de 5 anos por major).
- Persistencia via **JPA/Hibernate**.
- O banco deve ser provisionado via Docker Compose.
- Controle de schema: **Liquibase** (escolha fechada; nao usar Flyway).

## JPA/Hibernate

### Entidades Mapeadas

Cada entidade do dominio deve ser mapeada com anotacoes JPA:

| Anotacao | Uso |
|----------|-----|
| `@Entity` | Marcar como entidade JPA |
| `@Table` | Definir nome da tabela |
| `@Id` + `@GeneratedValue` | Chave primaria interna (`Long`) auto-gerada |
| `@Column(unique = true, nullable = false)` | Coluna `uuid` publica (UUID) |
| `@ManyToOne` / `@OneToMany` | Relacionamentos entre entidades (via `id` Long) |
| `@Enumerated(EnumType.STRING)` | Enums persistidos como texto |
| `@Version` | Lock otimista (diferencial para protecao de concorrencia) |

### Identificadores

- `id BIGSERIAL` = PK interna e alvo das FKs.
- `uuid UUID` = identificador publico, UNIQUE, gerado na aplicacao (`UUID.randomUUID()`) ou no banco (`gen_random_uuid()`).
- Repositories precisam de `findByUuid(UUID uuid)` para os endpoints.
- Ver [01-modelo-dominio.md](01-modelo-dominio.md).

### Constraints Importantes

| Constraint | Tabela | Descricao |
|-----------|--------|-----------|
| UNIQUE | aluno(uuid), curso(uuid), disciplina(uuid), turma(uuid), matricula(uuid) | UUID publico unico |
| UNIQUE | aluno(email) | Email do aluno unico |
| UNIQUE | aluno(cpf) | CPF do aluno unico |
| UNIQUE | turma(codigo) | Codigo da turma unico |
| UNIQUE | matricula(aluno_id, turma_id) | Considerar constraint composta ou validacao em service |
| FK | disciplina(curso_id) | Disciplina pertence a um curso |
| FK | turma(disciplina_id) | Turma pertence a uma disciplina |
| FK | matricula(aluno_id) | Matricula referencia um aluno |
| FK | matricula(turma_id) | Matricula referencia uma turma |

## Migrations

Obrigatorio usar **Liquibase** para controle de evolucao do schema.

### Estrutura Liquibase

```
src/main/resources/db/changelog/
├── db.changelog-master.yaml
└── changes/
    ├── 001-criar-tabela-curso.yaml
    ├── 002-criar-tabela-disciplina.yaml
    ├── 003-criar-tabela-aluno.yaml
    ├── 004-criar-tabela-turma.yaml
    └── 005-criar-tabela-matricula.yaml
```

### Master changelog

```yaml
# db.changelog-master.yaml
databaseChangeLog:
  - include:
      file: db/changelog/changes/001-criar-tabela-curso.yaml
  - include:
      file: db/changelog/changes/002-criar-tabela-disciplina.yaml
  - include:
      file: db/changelog/changes/003-criar-tabela-aluno.yaml
  - include:
      file: db/changelog/changes/004-criar-tabela-turma.yaml
  - include:
      file: db/changelog/changes/005-criar-tabela-matricula.yaml
```

### Exemplo de changeset

```yaml
# 001-criar-tabela-curso.yaml
databaseChangeLog:
  - changeSet:
      id: 001-criar-tabela-curso
      author: matricula-online
      changes:
        - createTable:
            tableName: curso
            columns:
              - column:
                  name: id
                  type: BIGSERIAL
                  constraints:
                    primaryKey: true
              - column:
                  name: uuid
                  type: UUID
                  constraints:
                    nullable: false
                    unique: true
              - column:
                  name: nome
                  type: VARCHAR(255)
                  constraints:
                    nullable: false
              - column:
                  name: descricao
                  type: TEXT
              - column:
                  name: carga_horaria
                  type: INTEGER
                  constraints:
                    nullable: false
              - column:
                  name: created_at
                  type: TIMESTAMP
                  defaultValueComputed: CURRENT_TIMESTAMP
                  constraints:
                    nullable: false
              - column:
                  name: updated_at
                  type: TIMESTAMP
                  defaultValueComputed: CURRENT_TIMESTAMP
                  constraints:
                    nullable: false
```

Alternativa aceita: changesets em **SQL formatado** (`sqlFile` / `formatted-sql`) se preferir DDL explicitamente.

## Configuracao do Spring

```yaml
# application.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/matricula_online
    username: postgres
    password: postgres
  jpa:
    hibernate:
      ddl-auto: validate  # Liquibase cuida do schema
    show-sql: false
  liquibase:
    enabled: true
    change-log: classpath:db/changelog/db.changelog-master.yaml
```

## Pontos de Atencao

- **Nao usar** `ddl-auto: create` ou `update` em producao/entrega. O Liquibase deve gerenciar o schema.
- Usar `ddl-auto: validate` para garantir que as entidades estao alinhadas com o banco.
- Garantir que os changesets sao **idempotentes** (nao reexecutar) e funcionam em um banco limpo.
- Para testes, considerar H2 em memoria ou Testcontainers PostgreSQL com os mesmos changelogs.
