# Estratégia de Testes

## Visão Geral

Os testes são parte crítica da avaliação. O desafio exige dois níveis de teste:

1. **Testes unitários** cobrindo as regras críticas de matrícula.
2. **Testes de integração/API** validando fluxos principais e persistência.

## Testes Unitários (Obrigatório)

Foco: regras de negócio da camada de service/domain.

### Cenários obrigatórios para MatriculaService

| # | Cenário | Resultado esperado |
|---|---------|-------------------|
| 1 | Matricular aluno em turma aberta com vagas | Matrícula criada com status PENDENTE |
| 2 | Matricular aluno em turma fechada | Exceção: turma não está aberta |
| 3 | Matricular aluno em turma sem vagas | Exceção: sem vagas disponíveis |
| 4 | Matricular aluno já matriculado na mesma turma | Exceção: matrícula duplicada |
| 5 | Confirmar matrícula pendente | Status muda para CONFIRMADA, vagasOcupadas incrementa |
| 6 | Confirmar matrícula sem vagas disponíveis | Exceção: sem vagas |
| 7 | Cancelar matrícula confirmada | Status muda para CANCELADA, vagasOcupadas decrementa |
| 8 | Cancelar matrícula pendente | Status muda para CANCELADA, vagasOcupadas inalterado |
| 9 | Confirmar matrícula já confirmada | Sucesso; sem alteração no banco (idempotente) |
| 10 | Cancelar matrícula já cancelada | Sucesso; sem alteração no banco (idempotente) |
| 11 | Confirmar matrícula cancelada | Exceção: não permitido |
| 12 | Matricular aluno com matrícula cancelada na mesma turma | Matrícula criada (permitido rematricular) |

### Abordagem

- Usar **Mockito** para mockar repositories.
- Testar a lógica do service isoladamente.
- Cada regra de negócio deve ter pelo menos um teste de sucesso e um de falha.

### Exemplo de estrutura

```
src/test/java/com/matriculaonline/
├── service/
│   ├── MatriculaServiceTest.java      # Testes unitários das regras
│   ├── AlunoServiceTest.java
│   ├── TurmaServiceTest.java
│   └── ...
└── ...
```

## Testes de Integração / API (Obrigatório)

Foco: fluxos completos passando por controller -> service -> repository -> banco.

### Cenários obrigatórios

| # | Cenário | O que valida |
|---|---------|-------------|
| 1 | CRUD completo de Aluno via API | Persistência e endpoints funcionais |
| 2 | CRUD completo de Curso via API | Persistência e endpoints funcionais |
| 3 | Fluxo completo de matrícula | Criar -> Confirmar -> vaga consumida |
| 4 | Fluxo de cancelamento | Confirmar -> Cancelar -> vaga liberada |
| 5 | Tentativa de matrícula duplicada via API | Retorno HTTP 422/409 com mensagem |
| 6 | Tentativa de matrícula em turma lotada via API | Retorno HTTP 422/409 com mensagem |
| 7 | Consulta de matrículas por aluno | Endpoint retorna matrículas corretas |
| 8 | Consulta de matrículas por turma | Endpoint retorna matrículas corretas |
| 9 | Validação de campos obrigatórios | Retorno HTTP 400 com detalhes |

### Abordagem

- Usar **`@SpringBootTest`** com **`TestRestTemplate`** ou **`MockMvc`**.
- Banco de testes: **H2 em memória** ou **Testcontainers** com PostgreSQL.
- Cada teste deve ser independente (limpar dados entre execuções).

### Exemplo de estrutura

```
src/test/java/com/matriculaonline/
├── integration/
│   ├── MatriculaIntegrationTest.java
│   ├── AlunoIntegrationTest.java
│   └── ...
└── ...
```

## Diferenciais em Testes

Itens não obrigatórios, mas valorizados:

| Diferencial | Descrição |
|------------|-----------|
| Cenários de borda | Testar limites (vagas = 0, vagas = 1, última vaga) |
| Cenários de erro | Testar todos os caminhos de erro documentados |
| Concorrência | Testar duas matrículas simultâneas para a última vaga |
| Testcontainers | Usar PostgreSQL real nos testes de integração |
| Cobertura | Medir e documentar cobertura de testes |

## Pontos de Atenção

- "Não há testes para regras críticas de matrícula" é **critério eliminatório**.
- Na entrevista, será perguntado: "Como você testou as regras críticas?"
- Priorizar **qualidade dos cenários** sobre quantidade de testes.
- Testes devem ser **executáveis** com um único comando documentado no README.
