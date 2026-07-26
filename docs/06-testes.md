# Estrategia de Testes

## Visao Geral

Os testes sao parte critica da avaliacao. O desafio exige dois niveis de teste:

1. **Testes unitarios** cobrindo as regras criticas de matricula.
2. **Testes de integracao/API** validando fluxos principais e persistencia.

## Testes Unitarios (Obrigatorio)

Foco: regras de negocio da camada de service/domain.

### Cenarios obrigatorios para MatriculaService

| # | Cenario | Resultado esperado |
|---|---------|-------------------|
| 1 | Matricular aluno em turma aberta com vagas | Matricula criada com status PENDENTE |
| 2 | Matricular aluno em turma fechada | Excecao: turma nao esta aberta |
| 3 | Matricular aluno em turma sem vagas | Excecao: sem vagas disponiveis |
| 4 | Matricular aluno ja matriculado na mesma turma | Excecao: matricula duplicada |
| 5 | Confirmar matricula pendente | Status muda para CONFIRMADA, vagasOcupadas incrementa |
| 6 | Confirmar matricula sem vagas disponiveis | Excecao: sem vagas |
| 7 | Cancelar matricula confirmada | Status muda para CANCELADA, vagasOcupadas decrementa |
| 8 | Cancelar matricula pendente | Status muda para CANCELADA, vagasOcupadas inalterado |
| 9 | Confirmar matricula ja confirmada | Sucesso; sem alteracao no banco (idempotente) |
| 10 | Cancelar matricula ja cancelada | Sucesso; sem alteracao no banco (idempotente) |
| 11 | Confirmar matricula cancelada | Excecao: nao permitido |
| 12 | Matricular aluno com matricula cancelada na mesma turma | Matricula criada (permitido rematricular) |

### Abordagem

- Usar **Mockito** para mockar repositories.
- Testar a logica do service isoladamente.
- Cada regra de negocio deve ter pelo menos um teste de sucesso e um de falha.

### Exemplo de estrutura

```
src/test/java/com/matriculaonline/
├── service/
│   ├── MatriculaServiceTest.java      # Testes unitarios das regras
│   ├── AlunoServiceTest.java
│   ├── TurmaServiceTest.java
│   └── ...
└── ...
```

## Testes de Integracao / API (Obrigatorio)

Foco: fluxos completos passando por controller -> service -> repository -> banco.

### Cenarios obrigatorios

| # | Cenario | O que valida |
|---|---------|-------------|
| 1 | CRUD completo de Aluno via API | Persistencia e endpoints funcionais |
| 2 | CRUD completo de Curso via API | Persistencia e endpoints funcionais |
| 3 | Fluxo completo de matricula | Criar -> Confirmar -> vaga consumida |
| 4 | Fluxo de cancelamento | Confirmar -> Cancelar -> vaga liberada |
| 5 | Tentativa de matricula duplicada via API | Retorno HTTP 422/409 com mensagem |
| 6 | Tentativa de matricula em turma lotada via API | Retorno HTTP 422/409 com mensagem |
| 7 | Consulta de matriculas por aluno | Endpoint retorna matriculas corretas |
| 8 | Consulta de matriculas por turma | Endpoint retorna matriculas corretas |
| 9 | Validacao de campos obrigatorios | Retorno HTTP 400 com detalhes |

### Abordagem

- Usar **`@SpringBootTest`** com **`TestRestTemplate`** ou **`MockMvc`**.
- Banco de testes: **H2 em memoria** ou **Testcontainers** com PostgreSQL.
- Cada teste deve ser independente (limpar dados entre execucoes).

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

Itens nao obrigatorios, mas valorizados:

| Diferencial | Descricao |
|------------|-----------|
| Cenarios de borda | Testar limites (vagas = 0, vagas = 1, ultima vaga) |
| Cenarios de erro | Testar todos os caminhos de erro documentados |
| Concorrencia | Testar duas matriculas simultaneas para a ultima vaga |
| Testcontainers | Usar PostgreSQL real nos testes de integracao |
| Cobertura | Medir e documentar cobertura de testes |

## Pontos de Atencao

- "Nao ha testes para regras criticas de matricula" e **criterio eliminatorio**.
- Na entrevista, sera perguntado: "Como voce testou as regras criticas?"
- Priorizar **qualidade dos cenarios** sobre quantidade de testes.
- Testes devem ser **executaveis** com um unico comando documentado no README.
