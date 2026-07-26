# Checklist de Entrega

## Como usar

Marque cada item conforme for implementado. Use este checklist para acompanhar o progresso e garantir que nada obrigatorio ficou de fora.

---

## Obrigatorios (Eliminatorios)

### Backend

- [x] Java **25** com Spring Boot **4.1.x** configurado e funcional
- [x] API REST com endpoints acessiveis
- [x] Separacao clara de camadas (controller, service, domain, repository, DTOs)
- [x] Validacoes de entrada com Bean Validation e mensagens claras
- [x] Tratamento padronizado de erros (`@RestControllerAdvice`)
- [x] Paginacao (`page`/`size`/`sort`) em todos os endpoints de listagem

### CRUDs

- [x] CRUD de Aluno (POST, GET, GET/{uuid}, PUT, DELETE)
- [x] CRUD de Curso (POST, GET, GET/{uuid}, PUT, DELETE)
- [x] CRUD de Disciplina (POST, GET, GET/{uuid}, PUT, DELETE)
- [x] CRUD de Turma (POST, GET, GET/{uuid}, PUT, DELETE)
- [x] DTOs e path params usam apenas `uuid` (id interno Long nunca exposto)

### Matricula

- [x] Criar matricula (POST) com status PENDENTE (`alunoUuid`, `turmaUuid`)
- [x] Confirmar matricula (PATCH `/{uuid}/confirmar`) com consumo de vaga
- [x] Cancelar matricula (PATCH `/{uuid}/cancelar`) com liberacao de vaga se CONFIRMADA
- [x] Confirmar ja CONFIRMADA e cancelar ja CANCELADA: sucesso sem alterar banco (idempotente)
- [x] Consulta de matriculas por aluno (GET `/aluno/{alunoUuid}`)
- [x] Consulta de matriculas por turma (GET `/turma/{turmaUuid}`)
- [x] Listagem de matriculas com filtro por status (`?status=`)

### Regras de Negocio

- [x] RN01: Matricula apenas em turma aberta
- [x] RN02: Limite de vagas respeitado
- [x] RN03: Matricula duplicada impedida
- [x] RN04: Fluxo de status (PENDENTE -> CONFIRMADA -> CANCELADA)
- [x] RN05: Vaga consumida ao confirmar
- [x] RN06: Vaga liberada ao cancelar matricula confirmada
- [x] RN07: Consultas por aluno e por turma

### Persistencia

- [x] Banco de dados relacional (**PostgreSQL 18**)
- [x] JPA/Hibernate configurado
- [x] Migrations com **Liquibase**
- [x] Entidades mapeadas com relacionamentos corretos
- [x] Coluna `uuid` UNIQUE em todas as entidades + `findByUuid`

### Testes

- [x] Testes unitarios das regras criticas de matricula
- [x] Teste: matricula em turma fechada (deve falhar)
- [x] Teste: matricula em turma sem vagas (deve falhar)
- [x] Teste: matricula duplicada (deve falhar)
- [x] Teste: confirmar matricula (vaga consumida)
- [x] Teste: cancelar matricula confirmada (vaga liberada)
- [x] Teste: confirmar ja confirmada / cancelar ja cancelada (sucesso idempotente, sem alteracao)
- [x] Testes de integracao/API para fluxos principais

### Infraestrutura

- [x] Docker Compose com banco de dados
- [x] Instrucoes de execucao local claras e funcionais
- [x] Projeto roda com os passos do README

### Documentacao

- [x] Swagger/OpenAPI configurado e acessivel
- [x] README com instrucoes de execucao
- [x] README com instrucoes de testes
- [x] README com tecnologias usadas
- [x] README com decisoes tecnicas
- [x] README com protecao da regra de vagas
- [x] README com testes das regras criticas
- [x] README com limitacoes conhecidas
- [x] README com uso de IA

### Frontend

- [x] Frontend em **Angular 22** estruturado com componentes
- [x] Telas separadas por funcionalidade
- [x] Tela de matriculas com **filtro por status** e **paginacao**
- [x] Listagens consumindo paginacao da API
- [x] Tratamento de erros no frontend
- [x] Consumo organizado da API via services

---

## Diferenciais (Opcionais)

- [ ] D01: CI executando build e testes (GitHub Actions)
- [x] D02: Filtros avancados alem de status/paginacao obrigatorios
- [ ] D03: Logs estruturados (JSON)
- [ ] D04: Eventos internos de dominio
- [x] D05: Boa organizacao do frontend (rotas, guards, interceptors)
- [x] D06: Testes com cenarios de erro e borda
- [x] D07: Tratamento transacional consistente (lock otimista/pessimista)
- [x] D08: Uso de IA bem documentado e revisado criticamente

---

## Validacao Final

Antes de entregar, verificar:

- [ ] Projeto roda do zero seguindo o README (testar em terminal limpo)
- [ ] `docker-compose up -d db` funciona
- [ ] Backend inicia sem erros
- [ ] Frontend inicia e consome a API
- [ ] Swagger UI acessivel em http://localhost:8080/swagger-ui.html
- [ ] Testes passam com `./mvnw test`
- [ ] Migrations executam em banco limpo
- [ ] Fluxo completo de matricula funciona (criar -> confirmar -> consultar)
- [ ] Cancelamento funciona e libera vaga
- [ ] Erros retornam mensagens claras (nao stack traces)
- [ ] README esta completo e coerente com a implementacao
