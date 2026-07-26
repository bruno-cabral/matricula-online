# Persistência e Migrations

## Banco de Dados Relacional

- Banco escolhido: **PostgreSQL 18** (major suportado mais recente; sem LTS formal — suporte de 5 anos por major).
- Persistência via **JPA/Hibernate**.
- O banco deve ser provisionado via Docker Compose.
- Controle de schema: **Liquibase** (escolha fechada; não usar Flyway).

## JPA/Hibernate

### Entidades Mapeadas

Cada entidade do domínio deve ser mapeada com anotações JPA:

| Anotação | Uso |
|----------|-----|
| `@Entity` | Marcar como entidade JPA |
| `@Table` | Definir nome da tabela |
| `@Id` + `@GeneratedValue` | Chave primária interna (`Long`) auto-gerada |
| `@Column(unique = true, nullable = false)` | Coluna `uuid` pública (UUID) |
| `@ManyToOne` / `@OneToMany` | Relacionamentos entre entidades (via `id` Long) |
| `@Enumerated(EnumType.STRING)` | Enums persistidos como texto |
| `@Version` | Lock otimista (diferencial para proteção de concorrência) |

### Identificadores

- `id BIGSERIAL` = PK interna e alvo das FKs.
- `uuid UUID` = identificador público, UNIQUE, gerado na aplicação (`UUID.randomUUID()`) ou no banco (`gen_random_uuid()`).
- Repositories precisam de `findByUuid(UUID uuid)` para os endpoints.
- Ver [01-modelo-dominio.md](01-modelo-dominio.md).

### Constraints Importantes

| Constraint | Tabela | Descrição |
|-----------|--------|-----------|
| UNIQUE | aluno(uuid), curso(uuid), disciplina(uuid), turma(uuid), matricula(uuid) | UUID público único |
| UNIQUE | aluno(email) | Email do aluno único |
| UNIQUE | aluno(cpf) | CPF do aluno único |
| UNIQUE | turma(codigo) | Código da turma único |
| UNIQUE | matricula(aluno_id, turma_id) | Considerar constraint composta ou validação em service |
| FK | disciplina(curso_id) | Disciplina pertence a um curso |
| FK | turma(disciplina_id) | Turma pertence a uma disciplina |
| FK | matricula(aluno_id) | Matrícula referencia um aluno |
| FK | matricula(turma_id) | Matrícula referencia uma turma |

## Migrations

Obrigatório usar **Liquibase** para controle de evolução do schema.

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

## Configuração do Spring

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

## Pontos de Atenção

- **Não usar** `ddl-auto: create` ou `update` em produção/entrega. O Liquibase deve gerenciar o schema.
- Usar `ddl-auto: validate` para garantir que as entidades estão alinhadas com o banco.
- Garantir que os changesets são **idempotentes** (não reexecutar) e funcionam em um banco limpo.
- Para testes, considerar H2 em memória ou Testcontainers PostgreSQL com os mesmos changelogs.
