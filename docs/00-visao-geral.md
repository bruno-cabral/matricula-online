# Visão Geral do Desafio

## Sobre

Desafio técnico para a posição de **Desenvolvedor(a) Pleno Full Stack** na **Tribe Lyceum - Techne**.
O domínio é um **sistema de matrícula online**, com foco em organização em camadas, testabilidade, documentação e evolução sustentável.

## Prazos e Dedicação

| Item | Valor |
|------|-------|
| Prazo de entrega | 7 dias corridos a partir do envio |
| Dedicação esperada | 8 a 16 horas reais |

## Stack Obrigatória

| Camada | Tecnologia | Versão alvo |
|--------|-----------|-------------|
| Backend | Java (LTS) + Spring Boot | **Java 25** + **Spring Boot 4.1.x** |
| Frontend | **Angular** (TypeScript) | **Angular 22** (release ativa; LTS imediatamente anterior: 21) |
| Runtime frontend | Node.js (Active LTS) | **Node.js 24** |
| Banco de dados | **PostgreSQL** | **18** (major suportado mais recente) |
| Migrations | **Liquibase** | Gerenciado pelo Spring Boot 4.1.x |

> Versões verificadas em jul/2026. Preferir sempre a linha LTS/estável mais recente compatível no momento da implementação.


## Formato de Entrega

- Repositório acessível (GitHub ou similar) com o código-fonte completo.
- README contendo: instruções de execução, testes, tecnologias utilizadas, principais decisões técnicas e uso de IA.
- A solução precisa ser **executável de forma reproduzível**: Docker Compose com banco de dados e instruções claras para execução local.

## Política de Uso de IA

- O uso de ferramentas de IA é **permitido e incentivado**.
- Informar no README: quais ferramentas utilizou, em quais partes do projeto, quais decisões revisou manualmente e quais trechos considera mais críticos.
- O uso de IA **não será avaliado negativamente**. O ponto crítico é a capacidade de **explicar o código, as decisões tomadas e os trade-offs assumidos**.

## Entrevista Técnica

Caso a entrega avance, a entrevista será **baseada no código entregue**. Será necessário explicar decisões, regras críticas, testes e uso de IA. Exemplos de perguntas:

- Mostre o fluxo de matrícula de aluno em turma.
- Onde está protegida a regra de limite de vagas?
- Como você testou as regras críticas?
- Que parte foi feita com ajuda de IA?
- Explique um trecho crítico sem consultar documentação.

## Nota Importante

> Não é obrigatório criar múltiplos serviços, mensageria real ou arquitetura distribuída.
> Para este nível, priorize uma solução **bem organizada, testável, clara e executável**.
> Complexidade sem justificativa técnica será avaliada negativamente.
