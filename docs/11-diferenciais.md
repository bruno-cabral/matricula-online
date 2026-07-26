# Diferenciais

## Visão Geral

Itens **não obrigatórios**, mas valorizados para o nível Pleno. Implementar estes itens demonstra maturidade técnica, mas apenas se feitos com qualidade. Complexidade sem justificativa será avaliada negativamente.

---

## D01 - CI executando build e testes

**Descrição:** Configurar pipeline de integração contínua (GitHub Actions, GitLab CI, etc.) que execute build e testes automaticamente.

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

**Valor:** Mostra cuidado com qualidade e automação.

---

## D02 - Filtros avançados além do básico

**Descrição:** Além da paginação e do filtro de status de matrícula (já **obrigatórios** — ver [03-requisitos-backend.md](03-requisitos-backend.md) e [04-requisitos-frontend.md](04-requisitos-frontend.md)), oferecer filtros adicionais nas demais entidades.

**Exemplo (diferencial):**
```
GET /api/turmas?status=ABERTA&disciplinaUuid=a3f1c8e2-4b5d-4e9a-9c1f-2d8e7a6b5c4d&page=0&size=10
GET /api/alunos?nome=Maria&page=0&size=10
```

**Implementação:** `Specification` ou query methods para filtros dinâmicos.

**Valor:** Demonstra preocupação com usabilidade além do mínimo exigido.

---

## D03 - Logs estruturados

**Descrição:** Logs com formato estruturado (JSON) e informações contextuais relevantes.

**Exemplo:**
```json
{
  "timestamp": "2025-01-15T10:30:00",
  "level": "INFO",
  "logger": "MatriculaService",
  "message": "Matrícula confirmada",
  "context": {
    "matriculaUuid": "c1d2e3f4-5a6b-7c8d-9e0f-1a2b3c4d5e6f",
    "alunoUuid": "b2e4d9f1-3a6c-4f8b-8d0e-1c7f6a5b4e3d",
    "turmaUuid": "a3f1c8e2-4b5d-4e9a-9c1f-2d8e7a6b5c4d"
  }
}
```

**Implementação:** Usar SLF4J + Logback com encoder JSON. Adicionar MDC para contexto transacional.

**Valor:** Mostra maturidade operacional e facilita diagnóstico em produção.

---

## D04 - Eventos internos de domínio

**Descrição:** Usar eventos de domínio para desacoplar efeitos colaterais, mesmo sem mensageria externa.

**Exemplo:**
- Ao confirmar matrícula, publicar `MatriculaConfirmadaEvent`.
- Um listener interno pode logar, notificar ou atualizar estatísticas.

**Implementação:** Usar `ApplicationEventPublisher` do Spring.

```java
// Publicar
eventPublisher.publishEvent(new MatriculaConfirmadaEvent(matricula));

// Ouvir
@EventListener
public void onMatriculaConfirmada(MatriculaConfirmadaEvent event) {
    log.info("Matrícula {} confirmada", event.getMatriculaId());
}
```

**Valor:** Demonstra conhecimento de padrões de domínio e desacoplamento sem over-engineering.

---

## D05 - Boa organização do frontend

**Descrição:** Frontend com organização que vai além do mínimo: rotas bem definidas, componentes reutilizáveis, interceptors HTTP, guards de navegação.

**Valor:** Mostra domínio full stack real, não apenas backend com tela colada.

---

## D06 - Testes com cenários de erro e borda

**Descrição:** Testes que cobrem além do caminho feliz: entradas inválidas, limites, estados inesperados.

**Exemplos de cenários de borda:**
- Turma com exatamente 1 vaga e duas matrículas simultâneas.
- Cancelar e rematricular na mesma turma.
- Aluno com nome muito longo ou caracteres especiais.
- Turma sem disciplina associada.

**Valor:** Mostra maturidade na escrita de testes e pensamento defensivo.

---

## D07 - Tratamento transacional consistente

**Descrição:** Garantir que operações críticas (confirmar/cancelar matrícula) são atômicas e protegidas contra concorrência.

**Implementação:**
- `@Transactional` nos métodos de service.
- Lock otimista com `@Version` na entidade Turma.
- Ou lock pessimista com `@Lock(LockModeType.PESSIMISTIC_WRITE)` no repository.

**Valor:** Demonstra entendimento de problemas reais de concorrência em sistemas de matrícula.

---

## D08 - Uso de IA bem documentado e revisado criticamente

**Descrição:** Documentar de forma honesta e detalhada como a IA foi utilizada, quais partes foram revisadas, e quais decisões foram tomadas manualmente.

**Valor:** Mostra maturidade profissional e capacidade de usar ferramentas de forma consciente.

---

## Prioridade Sugerida

Se houver tempo após completar os obrigatórios, priorizar na seguinte ordem:

| Prioridade | Diferencial | Justificativa |
|-----------|------------|---------------|
| 1 | D07 - Tratamento transacional | Será questionado na entrevista |
| 2 | D06 - Testes de borda | Reforça a cobertura de testes (crítico) |
| 3 | D02 - Filtros avançados | Além da paginação/status já obrigatórios |
| 4 | D01 - CI | Simples de configurar com GitHub Actions |
| 5 | D08 - IA documentada | Apenas documentação, sem código |
| 6 | D03 - Logs estruturados | Melhoria pontual |
| 7 | D05 - Frontend organizado | Depende do tempo disponível |
| 8 | D04 - Eventos de domínio | Pode adicionar complexidade desnecessária |
