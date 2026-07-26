# Critérios de Avaliação

## Critérios Eliminatórios

Itens que **podem impedir a continuidade** no processo seletivo. Cada um destes deve ser atendido obrigatoriamente.

| # | Tema | Ponto Crítico |
|---|------|--------------|
| 1 | **Execução** | Projeto não roda ou não há instrução clara de execução |
| 2 | **Stack** | Backend não foi desenvolvido em Spring Boot |
| 3 | **Persistência** | Não há persistência de dados em banco relacional |
| 4 | **Regras de negócio** | Regra de matrícula não está implementada ou a regra de vagas pode ser quebrada facilmente |
| 5 | **Camadas** | Não há separação clara de camadas ou o código está concentrado em controllers |
| 6 | **Testes** | Não há testes para regras críticas de matrícula |
| 7 | **Ambiente** | Não há Docker Compose ou instrução equivalente confiável para executar o banco |
| 8 | **Erros** | Não há tratamento adequado e padronizado de erros |
| 9 | **Documentação** | O README não explica como executar, testar e validar a solução |
| 10 | **Entrevista** | O candidato não consegue explicar as decisões técnicas, o código entregue ou o uso de IA |

## Dimensões de Avaliação

Além dos critérios eliminatórios, a entrega será avaliada nas seguintes dimensões:

| Dimensão | O que será observado |
|----------|---------------------|
| **Funcionalidade entregue** | Fluxos principais funcionando e aderentes às regras de negócio |
| **Organização do backend** | Separação de responsabilidades, clareza de camadas e modelagem |
| **Regras de negócio** | Consistência da matrícula, controle de vagas e cancelamento |
| **Testes** | Cobertura das regras críticas e qualidade dos cenários |
| **Frontend** | Organização, fluxo de uso e tratamento de erros |
| **Documentação** | README, Swagger/OpenAPI e explicação das decisões técnicas |
| **Arquitetura/desacoplamento** | Organização interna, baixo acoplamento e escolhas coerentes |

## Sinais Qualitativos Valorizados

Aspectos subjetivos que os avaliadores buscam:

- **Clareza de raciocínio** e simplicidade nas escolhas.
- **Capacidade de explicar** o próprio código.
- **Cuidado com erros**, validações e transações.
- **Consistência** entre README, arquitetura e implementação.
- **Pragmatismo:** nem código improvisado, nem complexidade gratuita.
- **Autonomia** para tomar boas decisões sem perder clareza e manutenção.

## Mapeamento: Critério x Documento de Implementação

| Critério Eliminatório | Documento de Referência |
|-----------------------|------------------------|
| Execução | [07-infraestrutura.md](07-infraestrutura.md), [09-readme-requisitos.md](09-readme-requisitos.md) |
| Stack Spring Boot | [03-requisitos-backend.md](03-requisitos-backend.md) |
| Persistência relacional | [05-persistencia-migrations.md](05-persistencia-migrations.md) |
| Regras de negócio | [02-regras-negocio.md](02-regras-negocio.md) |
| Separação de camadas | [03-requisitos-backend.md](03-requisitos-backend.md) |
| Testes | [06-testes.md](06-testes.md) |
| Docker Compose | [07-infraestrutura.md](07-infraestrutura.md) |
| Tratamento de erros | [03-requisitos-backend.md](03-requisitos-backend.md) |
| README | [09-readme-requisitos.md](09-readme-requisitos.md) |
| Entrevista | [00-visao-geral.md](00-visao-geral.md) |
