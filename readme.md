# 🎬 StreamFlix API

Sistema de catálogo de filmes com avaliações de usuários, listas de favoritos e ranking de popularidade.

## 👥 Integrantes do Grupo

- **[Seu Nome]** - [Seu RA]
- **[Nome do Colega 2]** - [RA do Colega 2]
- **[Nome do Colega 3]** - [RA do Colega 3]

## 📝 Descrição do Problema

O StreamFlix API resolve a necessidade de gerenciar um catálogo de filmes onde:

- **Administradores** podem gerenciar o catálogo completo (CRUD de filmes)
- **Usuários** podem explorar filmes, avaliar e criar listas de favoritos
- **Sistema de ranking** por popularidade baseado em número de acessos
- **Controle de acesso** diferenciado por perfil de usuário

## 🛠️ Tecnologias Utilizadas

- **Java 17** - Linguagem de programação
- **Spring Boot 3.5.0** - Framework principal
- **Spring Web** - Criação de APIs REST
- **Spring Data JPA** - Persistência de dados
- **Spring Security** - Autenticação e autorização
- **JWT** - Tokens de autenticação stateless
- **H2 Database** - Banco de dados para desenvolvimento
- **PostgreSQL** - Banco de dados para produção
- **Bean Validation** - Validação de dados
- **Maven** - Gerenciamento de dependências
- **Swagger/OpenAPI** - Documentação automática da API

## ⚠️ Limitações do Projeto

- Não implementa upload real de imagens para posters (apenas URLs)
- Cache não implementado para otimização de performance
- Sistema de notificações não desenvolvido
- Não possui funcionalidade de recuperação de senha
- Logs de auditoria não implementados

## 🗄️ Entidades do Sistema

### Usuario
Representa os usuários do sistema com diferentes perfis de acesso.

**Atributos:**
- `id` (Long) - Identificador único
- `nomeUsuario` (String) - Nome de usuário único
- `email` (String) - Email único
- `senha` (String) - Senha criptografada
- `perfil` (PerfilUsuario) - USUARIO ou ADMINISTRADOR
- `ativo` (Boolean) - Status de ativação
- `dataCriacao`, `dataAtualizacao` (LocalDateTime) - Timestamps

**Relacionamentos:**
- 1:N com Avaliacao (um usuário pode ter várias avaliações)
- 1:N com ListaFavoritos (um usuário pode ter várias listas)
- 1:N com Filme (filmes criados pelo administrador)

### Filme
Entidade principal que representa os filmes do catálogo.

**Atributos:**
- `id` (Long) - Identificador único
- `titulo` (String) - Título do filme
- `descricao` (Text) - Sinopse do filme
- `genero` (String) - Gênero cinematográfico
- `anoLancamento` (Integer) - Ano de lançamento
- `duracao` (Integer) - Duração em minutos
- `diretor` (String) - Nome do diretor
- `urlPoster` (String) - URL da imagem do poster
- `ativo` (Boolean) - Status de ativação
- `contadorAcessos` (Long) - **Contador para ranking de popularidade**
- `dataCriacao`, `dataAtualizacao` (LocalDateTime) - Timestamps
- `criadoPor` (Usuario) - Administrador que cadastrou

**Relacionamentos:**
- 1:N com Avaliacao (um filme pode ter várias avaliações)
- N:N com ListaFavoritos (através de ListaFavoritosFilmes)

### Avaliacao
Representa as avaliações que os usuários fazem dos filmes.

**Atributos:**
- `id` (Long) - Identificador único
- `nota` (Integer) - Nota de 1 a 5
- `comentario` (Text) - Comentário opcional
- `dataCriacao`, `dataAtualizacao` (LocalDateTime) - Timestamps

**Relacionamentos:**
- N:1 com Usuario (várias avaliações por usuário)
- N:1 com Filme (várias avaliações por filme)
- **Constraint única:** Um usuário só pode avaliar um filme uma vez

### ListaFavoritos
Listas personalizadas de filmes criadas pelos usuários.

**Atributos:**
- `id` (Long) - Identificador único
- `nome` (String) - Nome da lista
- `descricao` (Text) - Descrição opcional
- `publica` (Boolean) - Se outros usuários podem visualizar
- `dataCriacao`, `dataAtualizacao` (LocalDateTime) - Timestamps

**Relacionamentos:**
- N:1 com Usuario (várias listas por usuário)
- N:N com Filme (através de ListaFavoritosFilmes)

### ListaFavoritosFilmes
Tabela de junção que representa a relação N:N entre listas e filmes.

**Atributos:**
- `id` (Long) - Identificador único
- `dataAdicao` (LocalDateTime) - Quando o filme foi adicionado à lista

**Relacionamentos:**
- N:1 com ListaFavoritos
- N:1 com Filme
- **Constraint única:** Um filme não pode ser duplicado na mesma lista

## 🛣️ Rotas da API

### Autenticação (`/auth`)

#### POST `/auth/login`
Realiza login no sistema.

**Request:**
```json
{
  "login": "admin", 
  "senha": "123456"
}
```

**Response (200):**
```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tipo": "Bearer",
    "expiresIn": 86400,
    "usuario": {
      "id": 1,
      "nomeUsuario": "admin",
      "email": "admin@streamflix.com",
      "perfil": "ADMINISTRADOR"
    }
  }
}
```

#### POST `/auth/register`
Registra um novo usuário.

**Request:**
```json
{
  "nomeUsuario": "joao123",
  "email": "joao@email.com",
  "senha": "minhasenha"
}
```

### Filmes (`/api/filmes`)

#### GET `/api/filmes/ativos?orderByPopularidade=true`
Lista filmes ativos com opção de ordenação por popularidade (**REQUISITO OBRIGATÓRIO**).

**Response (200):**
```json
{
  "success": true,
  "message": "Filmes listados por popularidade",
  "data": [
    {
      "id": 1,
      "titulo": "Inception",
      "genero": "Ficção Científica",
      "anoLancamento": 2010,
      "diretor": "Christopher Nolan",
      "urlPoster": "https://exemplo.com/poster.jpg",
      "contadorAcessos": 150
    }
  ]
}
```

#### GET `/api/filmes/{id}/detalhes`
Obtém detalhes de um filme específico (**INCREMENTA CONTADOR DE ACESSOS**).

**Response (200):**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "titulo": "Inception",
    "descricao": "Um ladrão que invade sonhos...",
    "genero": "Ficção Científica",
    "anoLancamento": 2010,
    "duracao": 148,
    "diretor": "Christopher Nolan",
    "urlPoster": "https://exemplo.com/poster.jpg",
    "contadorAcessos": 151,
    "mediaAvaliacoes": 4.5,
    "totalAvaliacoes": 23
  }
}
```

#### GET `/api/filmes/ranking/popularidade?limit=10`
Ranking dos filmes mais populares (**REQUISITO OBRIGATÓRIO**).

**Response (200):**
```json
{
  "success": true,
  "message": "Top 10 filmes mais populares",
  "data": [
    {
      "id": 1,
      "titulo": "Inception",
      "genero": "Ficção Científica",
      "contadorAcessos": 151,
      "posicaoRanking": 1
    }
  ]
}
```

#### POST `/api/filmes` (Admin)
Cria um novo filme (apenas administradores).

**Headers:** `Authorization: Bearer {token}`

**Request:**
```json
{
  "titulo": "Matrix",
  "descricao": "Realidade virtual...",
  "genero": "Ficção Científica",
  "anoLancamento": 1999,
  "duracao": 136,
  "diretor": "Lana e Lilly Wachowski",
  "urlPoster": "https://exemplo.com/matrix.jpg"
}
```

### Avaliações (`/api/avaliacoes`)

#### POST `/api/avaliacoes`
Cria uma nova avaliação.

**Headers:** `Authorization: Bearer {token}`

**Request:**
```json
{
  "nota": 5,
  "comentario": "Filme excepcional!",
  "filmeId": 1
}
```

#### GET `/api/avaliacoes/filme/{filmeId}`
Lista avaliações de um filme específico.

**Response (200):**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "nota": 5,
      "comentario": "Filme excepcional!",
      "dataCriacao": "2025-01-15T10:30:00",
      "usuario": {
        "id": 2,
        "nomeUsuario": "joao123",
        "perfil": "USUARIO"
      }
    }
  ]
}
```

### Listas de Favoritos (`/api/listas-favoritos`)

#### POST `/api/listas-favoritos`
Cria uma nova lista de favoritos.

**Headers:** `Authorization: Bearer {token}`

**Request:**
```json
{
  "nome": "Meus Filmes de Ficção",
  "descricao": "Filmes de ficção científica que amo",
  "publica": true
}
```

#### POST `/api/listas-favoritos/{listaId}/filmes`
Adiciona um filme à lista.

**Request:**
```json
{
  "filmeId": 1
}
```

## ❌ Exemplos de Erros HTTP

### 400 - Bad Request (Dados Inválidos)
```json
{
  "timestamp": "2025-01-15T10:30:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Dados inválidos fornecidos",
  "validationErrors": {
    "titulo": "Título é obrigatório",
    "nota": "Nota deve ser entre 1 e 5"
  }
}
```

### 401 - Unauthorized (Token Inválido)
```json
{
  "error": "INVALID_TOKEN",
  "message": "Token inválido ou expirado",
  "timestamp": 1642248600000
}
```

### 403 - Forbidden (Sem Permissão)
```json
{
  "success": false,
  "message": "Acesso negado: Apenas administradores podem criar filmes",
  "timestamp": "2025-01-15T10:30:00"
}
```

### 404 - Not Found
```json
{
  "success": false,
  "message": "Filme não encontrado",
  "timestamp": "2025-01-15T10:30:00"
}
```

## 🚀 Como Executar o Projeto Localmente

### Pré-requisitos
- Java 17 ou superior
- Maven 3.6+
- Git

### Passo a Passo

1. **Clone o repositório:**
```bash
git clone https://github.com/seu-usuario/streamflix-api.git
cd streamflix-api
```

2. **Configure as variáveis de ambiente:**
```bash
# Copie o arquivo .env.example para .env
cp .env.example .env

# Edite as configurações se necessário
nano .env
```

3. **Execute o projeto:**
```bash
# Compile e execute
mvn spring-boot:run

# Ou compile e execute o JAR
mvn clean package
java -jar target/streamflix-api-1.0.0.jar
```

4. **Acesse a aplicação:**
- **API:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **H2 Console:** http://localhost:8080/h2-console
    - JDBC URL: `jdbc:h2:file:./streamflix.db`
    - User: `sa`
    - Password: `password`

### Configuração para Produção (PostgreSQL)

1. **Instale o PostgreSQL**

2. **Crie o banco de dados:**
```sql
CREATE DATABASE streamflix_db;
CREATE USER streamflix_user WITH PASSWORD 'streamflix_password';
GRANT ALL PRIVILEGES ON DATABASE streamflix_db TO streamflix_user;
```

3. **Atualize o arquivo .env:**
```bash
# Comente as configurações do H2 e descomente as do PostgreSQL
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/streamflix_db
SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.postgresql.Driver
SPRING_DATASOURCE_USERNAME=streamflix_user
SPRING_DATASOURCE_PASSWORD=streamflix_password
SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.PostgreSQLDialect
```

## 🎯 Funcionalidades Implementadas

### ✅ Requisitos Obrigatórios
- **Serviço backend HTTP** - API REST completa
- **Arquitetura em camadas** - Controller → Service → Repository
- **Arquivo .env** - Variáveis de ambiente configuradas
- **Persistência com SGBD** - JPA + H2/PostgreSQL
- **3+ entidades com relacionamentos** - 5 entidades implementadas
- **CRUD completo** - Todos os métodos obrigatórios (getOne, getAll, create, update, delete)

### ⭐ Carta-Desafio: Ranking de Popularidade
- **Campo contadorAcessos** na entidade Filme
- **Incremento automático** a cada GET do filme
- **Ordenação por popularidade** no endpoint GET ALL
- **Endpoint específico** para ranking dos mais populares

### 🚀 Funcionalidades Extras
- **Autenticação JWT** com roles (USUARIO/ADMINISTRADOR)
- **Documentação Swagger** automática
- **Validações robustas** com Bean Validation
- **Sistema de avaliações** com estatísticas
- **Listas de favoritos** públicas e privadas
- **Controle de acesso** granular por endpoint
- **Tratamento de exceções** padronizado
- **Suporte a PostgreSQL** para produção

## 📊 Ranking de Popularidade (Carta-Desafio)

O sistema implementa um ranking baseado no número de acessos aos filmes:

1. **Contador automático:** Cada vez que um filme é acessado via GET, o `contadorAcessos` é incrementado
2. **Ordenação por popularidade:** Endpoint `/api/filmes/ativos?orderByPopularidade=true` retorna filmes ordenados por acessos
3. **Ranking específico:** Endpoint `/api/filmes/ranking/popularidade` retorna top N filmes mais populares
4. **Posição no ranking:** Cada filme recebe sua posição no ranking de popularidade

## 📱 Testando a API

### Usando cURL

**Login:**
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login":"admin","senha":"123456"}'
```

**Listar filmes por popularidade:**
```bash
curl -X GET "http://localhost:8080/api/filmes/ativos?orderByPopularidade=true"
```

**Criar avaliação:**
```bash
curl -X POST http://localhost:8080/api/avaliacoes \
  -H "Authorization: Bearer SEU_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"nota":5,"comentario":"Excelente!","filmeId":1}'
```

### Usando Postman

1. Importe a collection do Swagger: `http://localhost:8080/v3/api-docs`
2. Configure a autenticação Bearer Token
3. Teste os endpoints conforme a documentação


---

⚡ **Desenvolvido com Spring Boot + Java 17** | 🎬 **StreamFlix API v1.0.0**