# Diferenciais

## Visao Geral

Itens **nao obrigatorios**, mas valorizados para o nivel Pleno. Implementar estes itens demonstra maturidade tecnica, mas apenas se feitos com qualidade. Complexidade sem justificativa sera avaliada negativamente.

---

## D01 - CI executando build e testes

**Descricao:** Configurar pipeline de integracao continua (GitHub Actions, GitLab CI, etc.) que execute build e testes automaticamente.

**Exemplo com GitHub Actions:**

```yaml
# .github/workflows/ci.yml
name: CI
on: [push, pull_request]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '25'
          distribution: 'temurin'
      - run: cd backend && ./mvnw verify
```

**Valor:** Mostra cuidado com qualidade e automacao.

---

## D02 - Filtros avancados alem do basico

**Descricao:** Alem da paginacao e do filtro de status de matricula (ja **obrigatorios** — ver [03-requisitos-backend.md](03-requisitos-backend.md) e [04-requisitos-frontend.md](04-requisitos-frontend.md)), oferecer filtros adicionais nas demais entidades.

**Exemplo (diferencial):**
```
GET /api/turmas?status=ABERTA&disciplinaUuid=a3f1c8e2-4b5d-4e9a-9c1f-2d8e7a6b5c4d&page=0&size=10
GET /api/alunos?nome=Maria&page=0&size=10
```

**Implementacao:** `Specification` ou query methods para filtros dinamicos.

**Valor:** Demonstra preocupacao com usabilidade alem do minimo exigido.

---

## D03 - Logs estruturados

**Descricao:** Logs com formato estruturado (JSON) e informacoes contextuais relevantes.

**Exemplo:**
```json
{
  "timestamp": "2025-01-15T10:30:00",
  "level": "INFO",
  "logger": "MatriculaService",
  "message": "Matricula confirmada",
  "context": {
    "matriculaUuid": "c1d2e3f4-5a6b-7c8d-9e0f-1a2b3c4d5e6f",
    "alunoUuid": "b2e4d9f1-3a6c-4f8b-8d0e-1c7f6a5b4e3d",
    "turmaUuid": "a3f1c8e2-4b5d-4e9a-9c1f-2d8e7a6b5c4d"
  }
}
```

**Implementacao:** Usar SLF4J + Logback com encoder JSON. Adicionar MDC para contexto transacional.

**Valor:** Mostra maturidade operacional e facilita diagnostico em producao.

---

## D04 - Eventos internos de dominio

**Descricao:** Usar eventos de dominio para desacoplar efeitos colaterais, mesmo sem mensageria externa.

**Exemplo:**
- Ao confirmar matricula, publicar `MatriculaConfirmadaEvent`.
- Um listener interno pode logar, notificar ou atualizar estatisticas.

**Implementacao:** Usar `ApplicationEventPublisher` do Spring.

```java
// Publicar
eventPublisher.publishEvent(new MatriculaConfirmadaEvent(matricula));

// Ouvir
@EventListener
public void onMatriculaConfirmada(MatriculaConfirmadaEvent event) {
    log.info("Matricula {} confirmada", event.getMatriculaId());
}
```

**Valor:** Demonstra conhecimento de padroes de dominio e desacoplamento sem over-engineering.

---

## D05 - Boa organizacao do frontend

**Descricao:** Frontend com organizacao que vai alem do minimo: rotas bem definidas, componentes reutilizaveis, interceptors HTTP, guards de navegacao.

**Valor:** Mostra dominio full stack real, nao apenas backend com tela colada.

---

## D06 - Testes com cenarios de erro e borda

**Descricao:** Testes que cobrem alem do caminho feliz: entradas invalidas, limites, estados inesperados.

**Exemplos de cenarios de borda:**
- Turma com exatamente 1 vaga e duas matriculas simultaneas.
- Cancelar e rematricular na mesma turma.
- Aluno com nome muito longo ou caracteres especiais.
- Turma sem disciplina associada.

**Valor:** Mostra maturidade na escrita de testes e pensamento defensivo.

---

## D07 - Tratamento transacional consistente

**Descricao:** Garantir que operacoes criticas (confirmar/cancelar matricula) sao atomicas e protegidas contra concorrencia.

**Implementacao:**
- `@Transactional` nos metodos de service.
- Lock otimista com `@Version` na entidade Turma.
- Ou lock pessimista com `@Lock(LockModeType.PESSIMISTIC_WRITE)` no repository.

**Valor:** Demonstra entendimento de problemas reais de concorrencia em sistemas de matricula.

---

## D08 - Uso de IA bem documentado e revisado criticamente

**Descricao:** Documentar de forma honesta e detalhada como a IA foi utilizada, quais partes foram revisadas, e quais decisoes foram tomadas manualmente.

**Valor:** Mostra maturidade profissional e capacidade de usar ferramentas de forma consciente.

---

## Prioridade Sugerida

Se houver tempo apos completar os obrigatorios, priorizar na seguinte ordem:

| Prioridade | Diferencial | Justificativa |
|-----------|------------|---------------|
| 1 | D07 - Tratamento transacional | Sera questionado na entrevista |
| 2 | D06 - Testes de borda | Reforca a cobertura de testes (critico) |
| 3 | D02 - Filtros avancados | Alem da paginacao/status ja obrigatorios |
| 4 | D01 - CI | Simples de configurar com GitHub Actions |
| 5 | D08 - IA documentada | Apenas documentacao, sem codigo |
| 6 | D03 - Logs estruturados | Melhoria pontual |
| 7 | D05 - Frontend organizado | Depende do tempo disponivel |
| 8 | D04 - Eventos de dominio | Pode adicionar complexidade desnecessaria |
