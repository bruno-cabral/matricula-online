# Requisitos do Frontend

## Stack

**Angular 22** (TypeScript) com **Node.js 24** (Active LTS) — escolha fechada para este projeto.

## Requisitos Obrigatorios

### Estrutura com Componentes

- O frontend deve ser **estruturado com componentes** reutilizaveis.
- Cada funcionalidade/tela deve ter seus proprios componentes.
- Separacao clara entre componentes de apresentacao e logica.

### Telas Separadas

O frontend deve conter telas para as principais funcionalidades:

| Tela | Descricao |
|------|-----------|
| **Alunos** | Listagem, cadastro, edicao e exclusao de alunos |
| **Cursos** | Listagem, cadastro, edicao e exclusao de cursos |
| **Disciplinas** | Listagem, cadastro, edicao e exclusao de disciplinas |
| **Turmas** | Listagem, cadastro, edicao e exclusao de turmas |
| **Matriculas** | Realizar matricula, confirmar, cancelar, consultar por aluno e por turma |

### Filtro e paginacao na tela de Matriculas (obrigatorio)

A listagem de matriculas deve permitir:

| Controle | Comportamento |
|----------|---------------|
| **Filtro por status** | Todos / PENDENTE / CONFIRMADA / CANCELADA |
| **Paginacao** | Navegacao de paginas consumindo `page`/`size` da API |

- Usar `GET /api/matriculas?status=PENDENTE&page=0&size=20` (ou equivalente).
- Demais listagens (alunos, cursos, disciplinas, turmas) tambem devem respeitar a paginacao da API.
- Comportamento idempotente: confirmar ja CONFIRMADA ou cancelar ja CANCELADA retorna sucesso (ver [02-regras-negocio.md](02-regras-negocio.md)).

### Tratamento de Erros

- Exibir mensagens de erro claras ao usuario quando a API retornar erros.
- Tratar erros de validacao (400) mostrando os campos invalidos.
- Tratar erros de regra de negocio (422/409) com mensagem compreensivel.
- Tratar erros de rede/servidor (500) com mensagem generica amigavel.
- Evitar telas em branco ou travamentos em caso de falha.

### Consumo Organizado da API

- Centralizar chamadas HTTP em **services** dedicados (um por entidade ou dominio).
- Nao fazer chamadas HTTP diretamente nos componentes.
- Usar tipagem adequada para requests e responses.

## Estrutura Sugerida (Angular)

```
src/app/
├── components/
│   ├── aluno/
│   │   ├── aluno-list/
│   │   ├── aluno-form/
│   │   └── aluno-detail/
│   ├── curso/
│   ├── disciplina/
│   ├── turma/
│   └── matricula/
│       ├── matricula-list/
│       ├── matricula-form/
│       └── matricula-por-aluno/
├── services/
│   ├── aluno.service.ts
│   ├── curso.service.ts
│   ├── disciplina.service.ts
│   ├── turma.service.ts
│   └── matricula.service.ts
├── models/
│   ├── aluno.model.ts
│   ├── curso.model.ts
│   ├── disciplina.model.ts
│   ├── turma.model.ts
│   └── matricula.model.ts
├── shared/
│   ├── error-handler/
│   └── notification/
└── app-routing.module.ts
```

## Fluxos Principais

### Fluxo de Matricula

```mermaid
flowchart TD
    A[Tela de Matricula] --> B[Selecionar Aluno]
    B --> C[Selecionar Turma]
    C --> D[Enviar matricula via POST]
    D --> E{Resposta da API}
    E -->|Sucesso| F[Exibir confirmacao]
    E -->|Erro 422| G[Exibir mensagem de regra violada]
    E -->|Erro 400| H[Exibir campos invalidos]
    E -->|Erro 500| I[Exibir mensagem generica de erro]
```

### Fluxo de Confirmacao/Cancelamento

```mermaid
flowchart TD
    A[Lista de Matriculas com filtro de status] --> B[Selecionar Matricula]
    B --> C{Acao}
    C -->|Confirmar| D[PATCH /confirmar]
    C -->|Cancelar| E[PATCH /cancelar]
    D --> F{Resposta}
    E --> F
    F -->|Sucesso ou no-op idempotente| G[Atualizar lista]
    F -->|Erro| H[Exibir mensagem de erro]
```

## Pontos de Atencao

- O frontend sera avaliado quanto a **organizacao e fluxo de uso**, nao apenas se funciona.
- O **tratamento de erros** no frontend e item de avaliacao critica.
- Nao e necessario design sofisticado, mas deve ser **funcional e navegavel**.
- Boa organizacao do frontend e listada como **diferencial**.
- Todas as navegacoes e chamadas HTTP usam **`uuid`** do recurso (nunca `id` numerico interno). Ver [01-modelo-dominio.md](01-modelo-dominio.md).
