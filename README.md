# Task Manager API

Uma API REST completa para gerenciamento de tarefas pessoais com autenticação JWT e integração com Brasil API para feriados.

## Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.5.11**
- **Spring Security** (JWT)
- **Spring Data JPA**
- **H2 Database** (desenvolvimento)
- **Flyway** (migrações)
- **SpringDoc OpenAPI** (Swagger)
- **WebClient** (Brasil API)
- **Lombok**
- **Bean Validation**

## Funcionalidades

- ✅ Autenticação JWT
- ✅ CRUD completo de tarefas
- ✅ Validação de entrada
- ✅ Tratamento global de exceções
- ✅ Integração com Brasil API (feriados)
- ✅ Documentação Swagger/OpenAPI
- ✅ Filtragem e ordenação de tarefas
- ✅ Verificação de tarefas vencidas

## Documentação da API

A documentação completa da API está disponível via Swagger UI:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## Endpoints Principais

### Autenticação

#### POST /auth/register
Registra um novo usuário no sistema.

**Request Body:**
```json
{
  "nome": "João Silva",
  "email": "joao@email.com",
  "senha": "senha123"
}
```

#### POST /auth/login
Autentica um usuário e retorna um token JWT.

**Request Body:**
```json
{
  "email": "joao@email.com",
  "senha": "senha123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tipo": "Bearer",
  "expiracaoEmMs": 86400000
}
```

### Tarefas

**Todos os endpoints de tarefas requerem autenticação JWT no header:**
```
Authorization: Bearer <token>
```

#### GET /tasks
Lista todas as tarefas do usuário autenticado.

**Parâmetros de Query:**
- `status` (opcional): Filtrar por status (PENDENTE, EM_ANDAMENTO, CONCLUIDA)
- `sort` (padrão: "prazo,asc"): Ordenação (ex: "titulo,desc")

#### GET /tasks/{id}
Busca uma tarefa específica por ID.

#### POST /tasks
Cria uma nova tarefa.

**Request Body:**
```json
{
  "titulo": "Reunião com cliente",
  "descricao": "Discutir requisitos do projeto",
  "prazo": "2026-03-20T14:00:00",
  "prioridade": "ALTA",
  "status": "PENDENTE"
}
```

#### PUT /tasks/{id}
Atualiza uma tarefa existente.

#### DELETE /tasks/{id}
Remove uma tarefa.

#### GET /tasks/vencidas
Lista todas as tarefas vencidas do usuário autenticado.

## Enums

### Status
- `PENDENTE`
- `EM_ANDAMENTO`
- `CONCLUIDA`

### Prioridade
- `BAIXA`
- `MEDIA`
- `ALTA`

## Validações

- **Título**: Obrigatório, mínimo 3 caracteres, máximo 100 caracteres
- **Descrição**: Opcional, máximo 500 caracteres
- **Prazo**: Deve ser uma data futura (não permite criar tarefas com prazo vencido)
- **Status**: Deve ser um dos valores do enum Status
- **Prioridade**: Deve ser um dos valores do enum Prioridade

## Tratamento de Erros

A API retorna respostas padronizadas para erros:

```json
{
  "timestamp": "2026-03-17T12:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Dados inválidos",
  "path": "/tasks"
}
```

## Executando a Aplicação

### Pré-requisitos
- Java 17 ou superior
- Maven 3.6+

### Passos

1. **Clone o repositório:**
```bash
git clone https://github.com/DavisonFNX/taskmanager.git
cd taskmanager
```

2. **Execute a aplicação:**
```bash
mvn spring-boot:run
```

3. **Acesse a documentação:**
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

## Testando a API

### 1. Registrar usuário
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva",
    "email": "joao@email.com",
    "senha": "senha123"
  }'
```

### 2. Fazer login
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "joao@email.com",
    "senha": "senha123"
  }'
```

### 3. Criar tarefa (usando o token JWT)
```bash
curl -X POST http://localhost:8080/tasks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN_JWT>" \
  -d '{
    "titulo": "Minha primeira tarefa",
    "descricao": "Descrição da tarefa",
    "prazo": "2026-03-25T10:00:00",
    "prioridade": "MEDIA",
    "status": "PENDENTE"
  }'
```

## Funcionalidades Especiais

### Verificação de Feriados
A API verifica automaticamente se o prazo de uma tarefa coincide com feriados brasileiros usando a Brasil API. Se houver feriado, a tarefa não poderá ser criada/movida para aquela data.

### Tarefas Vencidas
O endpoint `/tasks/vencidas` retorna todas as tarefas com prazo vencido, ajudando na gestão de prioridades.

### Ordenação Flexível
As tarefas podem ser ordenadas por qualquer campo (titulo, descricao, prazo, prioridade, status) em ordem ascendente ou descendente.

## Segurança

- Autenticação baseada em JWT
- Todas as operações de tarefas requerem token válido
- Validação rigorosa de entrada
- Tratamento seguro de senhas (BCrypt)

## Desenvolvimento

### Estrutura do Projeto
```
src/main/java/com/davison/taskmanager/
├── config/          # Configurações (OpenAPI)
├── controller/      # Controllers REST
├── dto/            # Data Transfer Objects
├── entity/         # Entidades JPA
├── enums/          # Enums do domínio
├── exception/      # Tratamento de exceções
├── integration/    # Integração com APIs externas
├── mapper/         # Mapeadores
├── repository/     # Repositórios JPA
├── security/       # Configurações de segurança
└── service/        # Lógica de negócio
```

### Banco de Dados
- **Desenvolvimento**: H2 (em memória)
- **Produção**: Configurável via `application.properties`

### Migrações
As migrações do banco são gerenciadas pelo Flyway e estão localizadas em `src/main/resources/db/migration/`.

## Contribuição

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/nova-feature`)
3. Commit suas mudanças (`git commit -am 'Adiciona nova feature'`)
4. Push para a branch (`git push origin feature/nova-feature`)
5. Abra um Pull Request

## Licença

Este projeto está sob a licença MIT.