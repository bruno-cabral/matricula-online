# Visao Geral do Desafio

## Sobre

Desafio tecnico para a posicao de **Desenvolvedor(a) Pleno Full Stack** na **Tribe Lyceum - Techne**.
O dominio e um **sistema de matricula online**, com foco em organizacao em camadas, testabilidade, documentacao e evolucao sustentavel.

## Prazos e Dedicacao

| Item | Valor |
|------|-------|
| Prazo de entrega | 7 dias corridos a partir do envio |
| Dedicacao esperada | 8 a 16 horas reais |

## Stack Obrigatoria

| Camada | Tecnologia | Versao alvo |
|--------|-----------|-------------|
| Backend | Java (LTS) + Spring Boot | **Java 25** + **Spring Boot 4.1.x** |
| Frontend | **Angular** (TypeScript) | **Angular 22** (release ativa; LTS imediatamente anterior: 21) |
| Runtime frontend | Node.js (Active LTS) | **Node.js 24** |
| Banco de dados | **PostgreSQL** | **18** (major suportado mais recente) |
| Migrations | **Liquibase** | Gerenciado pelo Spring Boot 4.1.x |

> Versoes verificadas em jul/2026. Preferir sempre a linha LTS/estavel mais recente compativel no momento da implementacao.


## Formato de Entrega

- Repositorio acessivel (GitHub ou similar) com o codigo-fonte completo.
- README contendo: instrucoes de execucao, testes, tecnologias utilizadas, principais decisoes tecnicas e uso de IA.
- A solucao precisa ser **executavel de forma reproduzivel**: Docker Compose com banco de dados e instrucoes claras para execucao local.

## Politica de Uso de IA

- O uso de ferramentas de IA e **permitido e incentivado**.
- Informar no README: quais ferramentas utilizou, em quais partes do projeto, quais decisoes revisou manualmente e quais trechos considera mais criticos.
- O uso de IA **nao sera avaliado negativamente**. O ponto critico e a capacidade de **explicar o codigo, as decisoes tomadas e os trade-offs assumidos**.

## Entrevista Tecnica

Caso a entrega avance, a entrevista sera **baseada no codigo entregue**. Sera necessario explicar decisoes, regras criticas, testes e uso de IA. Exemplos de perguntas:

- Mostre o fluxo de matricula de aluno em turma.
- Onde esta protegida a regra de limite de vagas?
- Como voce testou as regras criticas?
- Que parte foi feita com ajuda de IA?
- Explique um trecho critico sem consultar documentacao.

## Nota Importante

> Nao e obrigatorio criar multiplos servicos, mensageria real ou arquitetura distribuida.
> Para este nivel, priorize uma solucao **bem organizada, testavel, clara e executavel**.
> Complexidade sem justificativa tecnica sera avaliada negativamente.
