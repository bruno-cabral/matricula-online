# Criterios de Avaliacao

## Criterios Eliminatorios

Itens que **podem impedir a continuidade** no processo seletivo. Cada um destes deve ser atendido obrigatoriamente.

| # | Tema | Ponto Critico |
|---|------|--------------|
| 1 | **Execucao** | Projeto nao roda ou nao ha instrucao clara de execucao |
| 2 | **Stack** | Backend nao foi desenvolvido em Spring Boot |
| 3 | **Persistencia** | Nao ha persistencia de dados em banco relacional |
| 4 | **Regras de negocio** | Regra de matricula nao esta implementada ou a regra de vagas pode ser quebrada facilmente |
| 5 | **Camadas** | Nao ha separacao clara de camadas ou o codigo esta concentrado em controllers |
| 6 | **Testes** | Nao ha testes para regras criticas de matricula |
| 7 | **Ambiente** | Nao ha Docker Compose ou instrucao equivalente confiavel para executar o banco |
| 8 | **Erros** | Nao ha tratamento adequado e padronizado de erros |
| 9 | **Documentacao** | O README nao explica como executar, testar e validar a solucao |
| 10 | **Entrevista** | O candidato nao consegue explicar as decisoes tecnicas, o codigo entregue ou o uso de IA |

## Dimensoes de Avaliacao

Alem dos criterios eliminatorios, a entrega sera avaliada nas seguintes dimensoes:

| Dimensao | O que sera observado |
|----------|---------------------|
| **Funcionalidade entregue** | Fluxos principais funcionando e aderentes as regras de negocio |
| **Organizacao do backend** | Separacao de responsabilidades, clareza de camadas e modelagem |
| **Regras de negocio** | Consistencia da matricula, controle de vagas e cancelamento |
| **Testes** | Cobertura das regras criticas e qualidade dos cenarios |
| **Frontend** | Organizacao, fluxo de uso e tratamento de erros |
| **Documentacao** | README, Swagger/OpenAPI e explicacao das decisoes tecnicas |
| **Arquitetura/desacoplamento** | Organizacao interna, baixo acoplamento e escolhas coerentes |

## Sinais Qualitativos Valorizados

Aspectos subjetivos que os avaliadores buscam:

- **Clareza de raciocinio** e simplicidade nas escolhas.
- **Capacidade de explicar** o proprio codigo.
- **Cuidado com erros**, validacoes e transacoes.
- **Consistencia** entre README, arquitetura e implementacao.
- **Pragmatismo:** nem codigo improvisado, nem complexidade gratuita.
- **Autonomia** para tomar boas decisoes sem perder clareza e manutencao.

## Mapeamento: Criterio x Documento de Implementacao

| Criterio Eliminatorio | Documento de Referencia |
|-----------------------|------------------------|
| Execucao | [07-infraestrutura.md](07-infraestrutura.md), [09-readme-requisitos.md](09-readme-requisitos.md) |
| Stack Spring Boot | [03-requisitos-backend.md](03-requisitos-backend.md) |
| Persistencia relacional | [05-persistencia-migrations.md](05-persistencia-migrations.md) |
| Regras de negocio | [02-regras-negocio.md](02-regras-negocio.md) |
| Separacao de camadas | [03-requisitos-backend.md](03-requisitos-backend.md) |
| Testes | [06-testes.md](06-testes.md) |
| Docker Compose | [07-infraestrutura.md](07-infraestrutura.md) |
| Tratamento de erros | [03-requisitos-backend.md](03-requisitos-backend.md) |
| README | [09-readme-requisitos.md](09-readme-requisitos.md) |
| Entrevista | [00-visao-geral.md](00-visao-geral.md) |
