# Checklist de Entrega

## Como usar

Marque cada item conforme for implementado. Use este checklist para acompanhar o progresso e garantir que nada obrigatório ficou de fora.

---

## Obrigatórios (Eliminatórios)

### Backend

- [x] Java **25** com Spring Boot **4.1.x** configurado e funcional
- [x] API REST com endpoints acessíveis
- [x] Separação clara de camadas (controller, service, domain, repository, DTOs)
- [x] Validações de entrada com Bean Validation e mensagens claras
- [x] Tratamento padronizado de erros (`@RestControllerAdvice`)
- [x] Paginação (`page`/`size`/`sort`) em todos os endpoints de listagem

### CRUDs

- [x] CRUD de Aluno (POST, GET, GET/{uuid}, PUT, DELETE)
- [x] CRUD de Curso (POST, GET, GET/{uuid}, PUT, DELETE)
- [x] CRUD de Disciplina (POST, GET, GET/{uuid}, PUT, DELETE)
- [x] CRUD de Turma (POST, GET, GET/{uuid}, PUT, DELETE)
- [x] DTOs e path params usam apenas `uuid` (id interno Long nunca exposto)

### Matrícula

- [x] Criar matrícula (POST) com status PENDENTE (`alunoUuid`, `turmaUuid`)
- [x] Confirmar matrícula (PATCH `/{uuid}/confirmar`) com consumo de vaga
- [x] Cancelar matrícula (PATCH `/{uuid}/cancelar`) com liberação de vaga se CONFIRMADA
- [x] Confirmar já CONFIRMADA e cancelar já CANCELADA: sucesso sem alterar banco (idempotente)
- [x] Consulta de matrículas por aluno (GET `/aluno/{alunoUuid}`)
- [x] Consulta de matrículas por turma (GET `/turma/{turmaUuid}`)
- [x] Listagem de matrículas com filtro por status (`?status=`)

### Regras de Negócio

- [x] RN01: Matrícula apenas em turma aberta
- [x] RN02: Limite de vagas respeitado
- [x] RN03: Matrícula duplicada impedida
- [x] RN04: Fluxo de status (PENDENTE -> CONFIRMADA -> CANCELADA)
- [x] RN05: Vaga consumida ao confirmar
- [x] RN06: Vaga liberada ao cancelar matrícula confirmada
- [x] RN07: Consultas por aluno e por turma

### Persistência

- [x] Banco de dados relacional (**PostgreSQL 18**)
- [x] JPA/Hibernate configurado
- [x] Migrations com **Liquibase**
- [x] Entidades mapeadas com relacionamentos corretos
- [x] Coluna `uuid` UNIQUE em todas as entidades + `findByUuid`

### Testes

- [x] Testes unitários das regras críticas de matrícula
- [x] Teste: matrícula em turma fechada (deve falhar)
- [x] Teste: matrícula em turma sem vagas (deve falhar)
- [x] Teste: matrícula duplicada (deve falhar)
- [x] Teste: confirmar matrícula (vaga consumida)
- [x] Teste: cancelar matrícula confirmada (vaga liberada)
- [x] Teste: confirmar já confirmada / cancelar já cancelada (sucesso idempotente, sem alteração)
- [x] Testes de integração/API para fluxos principais

### Infraestrutura

- [x] Docker Compose com banco de dados
- [x] Instruções de execução local claras e funcionais
- [x] Projeto roda com os passos do README

### Documentação

- [x] Swagger/OpenAPI configurado e acessível
- [x] README com instruções de execução
- [x] README com instruções de testes
- [x] README com tecnologias usadas
- [x] README com decisões técnicas
- [x] README com proteção da regra de vagas
- [x] README com testes das regras críticas
- [x] README com limitações conhecidas
- [x] README com uso de IA

### Frontend

- [x] Frontend em **Angular 22** estruturado com componentes
- [x] Telas separadas por funcionalidade
- [x] Tela de matrículas com **filtro por status** e **paginação**
- [x] Listagens consumindo paginação da API
- [x] Tratamento de erros no frontend
- [x] Consumo organizado da API via services

---

## Diferenciais (Opcionais)

- [ ] D01: CI executando build e testes (GitHub Actions)
- [x] D02: Filtros avançados além de status/paginação obrigatórios
- [ ] D03: Logs estruturados (JSON)
- [ ] D04: Eventos internos de domínio
- [x] D05: Boa organização do frontend (rotas, guards, interceptors)
- [x] D06: Testes com cenários de erro e borda
- [x] D07: Tratamento transacional consistente (lock otimista/pessimista)
- [x] D08: Uso de IA bem documentado e revisado criticamente

---

## Validação Final

Antes de entregar, verificar:

- [ ] Projeto roda do zero seguindo o README (testar em terminal limpo)
- [ ] `docker-compose up -d db` funciona
- [ ] Backend inicia sem erros
- [ ] Frontend inicia e consome a API
- [ ] Swagger UI acessível em http://localhost:8080/swagger-ui.html
- [ ] Testes passam com `./mvnw test`
- [ ] Migrations executam em banco limpo
- [ ] Fluxo completo de matrícula funciona (criar -> confirmar -> consultar)
- [ ] Cancelamento funciona e libera vaga
- [ ] Erros retornam mensagens claras (não stack traces)
- [ ] README está completo e coerente com a implementação
