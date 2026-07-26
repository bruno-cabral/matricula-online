# Regras de Negocio

## Visao Geral

O sistema de matriculas academicas possui 7 regras de negocio minimas que devem ser implementadas e testadas. Estas regras sao o nucleo da avaliacao tecnica.

---

## RN01 - Matricula apenas em turma aberta

**Descricao:** Um aluno so pode ser matriculado em turmas com status ABERTA.

**Comportamento esperado:**
- Ao tentar matricular um aluno, verificar se a turma possui `status = ABERTA`.
- Se a turma estiver FECHADA, a operacao deve ser rejeitada com mensagem clara.

**Cenarios:**
| Cenario | Resultado esperado |
|---------|-------------------|
| Turma ABERTA com vagas | Matricula criada com sucesso |
| Turma FECHADA | Erro: "Turma nao esta aberta para matriculas" |

---

## RN02 - Limite de vagas da turma

**Descricao:** Uma turma possui limite de vagas. Nao e possivel matricular alem do limite.

**Comportamento esperado:**
- Antes de confirmar uma matricula, verificar se `vagasOcupadas < vagas`.
- Se nao houver vagas disponiveis, a operacao deve ser rejeitada.
- A verificacao deve ser **protegida contra concorrencia** (tratamento transacional).

**Cenarios:**
| Cenario | Resultado esperado |
|---------|-------------------|
| Turma com vagas disponiveis | Matricula permitida |
| Turma lotada (vagasOcupadas == vagas) | Erro: "Nao ha vagas disponiveis nesta turma" |
| Duas requisicoes simultaneas para ultima vaga | Apenas uma deve ser aceita |

**Ponto critico:** Esta regra sera questionada na entrevista. Deve haver protecao transacional (ex: `@Transactional` + lock otimista/pessimista ou controle no banco).

---

## RN03 - Matricula duplicada

**Descricao:** Um aluno nao pode se matricular duas vezes na mesma turma.

**Comportamento esperado:**
- Antes de criar uma matricula, verificar se ja existe uma matricula ativa (PENDENTE ou CONFIRMADA) do mesmo aluno na mesma turma.
- Se ja existir, a operacao deve ser rejeitada.

**Cenarios:**
| Cenario | Resultado esperado |
|---------|-------------------|
| Aluno sem matricula na turma | Matricula criada |
| Aluno com matricula PENDENTE na turma | Erro: "Aluno ja possui matricula nesta turma" |
| Aluno com matricula CONFIRMADA na turma | Erro: "Aluno ja possui matricula nesta turma" |
| Aluno com matricula CANCELADA na turma | Matricula permitida (nova matricula) |

---

## RN04 - Status da matricula

**Descricao:** Uma matricula possui status que segue um fluxo definido: PENDENTE, CONFIRMADA ou CANCELADA.

**Comportamento esperado:**
- Ao criar uma matricula, o status inicial e PENDENTE.
- Transicoes com efeito: PENDENTE -> CONFIRMADA, PENDENTE -> CANCELADA, CONFIRMADA -> CANCELADA.
- **Idempotencia:** confirmar uma matricula ja CONFIRMADA ou cancelar uma ja CANCELADA deve retornar **sucesso sem alterar o banco** (no-op).
- Transicao invalida a rejeitar: CANCELADA -> CONFIRMADA (nao e permitido "reativar" via confirmar).

**Fluxo de status:**

```mermaid
stateDiagram-v2
    [*] --> PENDENTE: Criar matricula
    PENDENTE --> CONFIRMADA: Confirmar
    PENDENTE --> CANCELADA: Cancelar
    CONFIRMADA --> CANCELADA: Cancelar
    CONFIRMADA --> CONFIRMADA: Confirmar (idempotente, no-op)
    CANCELADA --> CANCELADA: Cancelar (idempotente, no-op)
```

---

## RN05 - Consumo de vaga ao confirmar

**Descricao:** Ao confirmar uma matricula, a vaga da turma deve ser consumida.

**Comportamento esperado:**
- Ao mudar o status de PENDENTE para CONFIRMADA, incrementar `vagasOcupadas` na turma.
- Verificar se ainda ha vagas antes de confirmar.
- A operacao deve ser atomica (transacional).
- Se a matricula **ja estiver CONFIRMADA**, retornar sucesso **sem** alterar status nem `vagasOcupadas` (idempotente).

**Cenarios:**
| Cenario | Resultado esperado |
|---------|-------------------|
| Confirmar matricula PENDENTE com vagas | Status muda para CONFIRMADA, vagasOcupadas++ |
| Confirmar matricula PENDENTE sem vagas | Erro: "Nao ha vagas disponiveis" |
| Confirmar matricula ja CONFIRMADA | Sucesso HTTP 200; banco inalterado (no-op) |
| Confirmar matricula CANCELADA | Erro: nao e permitido confirmar matricula cancelada |

---

## RN06 - Liberacao de vaga ao cancelar

**Descricao:** Ao cancelar uma matricula que estava CONFIRMADA, a vaga deve ser liberada.

**Comportamento esperado:**
- Ao mudar o status de CONFIRMADA para CANCELADA, decrementar `vagasOcupadas` na turma.
- Ao cancelar uma matricula PENDENTE, **nao alterar** vagasOcupadas (vaga nao havia sido consumida).
- Se a matricula **ja estiver CANCELADA**, retornar sucesso **sem** alterar status nem `vagasOcupadas` (idempotente).

**Cenarios:**
| Cenario | Resultado esperado |
|---------|-------------------|
| Cancelar matricula CONFIRMADA | Status muda para CANCELADA, vagasOcupadas-- |
| Cancelar matricula PENDENTE | Status muda para CANCELADA, vagasOcupadas inalterado |
| Cancelar matricula ja CANCELADA | Sucesso HTTP 200; banco inalterado (no-op) |

---

## RN07 - Consultas de matriculas

**Descricao:** Deve haver consulta de matriculas por aluno e por turma.

**Comportamento esperado:**
- Endpoint para listar todas as matriculas de um aluno especifico.
- Endpoint para listar todas as matriculas de uma turma especifica.
- Os resultados devem incluir informacoes relevantes (dados do aluno, turma, status da matricula).

**Endpoints sugeridos:**
| Metodo | Endpoint | Descricao |
|--------|----------|-----------|
| GET | `/api/matriculas/aluno/{alunoUuid}?page&size&status` | Matriculas de um aluno (paginado) |
| GET | `/api/matriculas/turma/{turmaUuid}?page&size&status` | Matriculas de uma turma (paginado) |
| GET | `/api/matriculas?page&size&status` | Listagem geral com filtro de status (paginado) |

---

## Resumo das Regras

| ID | Regra | Prioridade |
|----|-------|-----------|
| RN01 | Matricula apenas em turma aberta | Critica |
| RN02 | Limite de vagas | Critica |
| RN03 | Matricula duplicada | Critica |
| RN04 | Status da matricula (fluxo) | Critica |
| RN05 | Consumo de vaga ao confirmar | Critica |
| RN06 | Liberacao de vaga ao cancelar | Critica |
| RN07 | Consultas por aluno e por turma | Obrigatoria |
