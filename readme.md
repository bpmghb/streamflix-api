# 🎬 StreamFlix API

Sistema de catálogo de filmes com avaliações de usuários, listas de favoritos e ranking de popularidade.

---

## 📝 Descrição do Projeto e Integrantes

### Integrantes do Grupo
- **Pedro Ernesto**
- **Matheus Henrique**
- **Bianca Alves**

### Sobre o Projeto
O **StreamFlix API** é um sistema backend completo para gerenciamento de catálogo de filmes, inspirado em plataformas como Netflix e IMDb. O sistema permite que usuários explorem filmes, criem avaliações, organizem listas de favoritos e acompanhem rankings de popularidade em tempo real.

O projeto foi desenvolvido como trabalho acadêmico, demonstrando conhecimentos em:
- Arquitetura REST API
- Autenticação e autorização
- Persistência de dados
- Padrões de desenvolvimento
- Documentação de APIs

---

## 🎯 Descrição do Problema

O projeto resolve a necessidade de criar um sistema completo de catalogação de filmes que atenda diferentes tipos de usuários:

### Problemas Resolvidos:
- **Catalogação Centralizada**: Administradores podem gerenciar um catálogo completo de filmes
- **Avaliação Social**: Usuários podem avaliar filmes e ler opiniões de outros usuários
- **Personalização**: Criação de listas personalizadas de filmes favoritos
- **Descoberta de Conteúdo**: Sistema de ranking por popularidade baseado em acessos reais
- **Controle de Acesso**: Diferentes permissões entre usuários comuns e administradores

### Funcionalidades Principais:
- Sistema de usuários com perfis diferenciados (USUARIO/ADMINISTRADOR)
- CRUD completo de filmes com controle de acesso
- Sistema de avaliações com notas de 1 a 5 e comentários opcionais
- Listas de favoritos públicas e privadas
- **Ranking de popularidade** baseado em número de acessos (Carta-Desafio)
- Estatísticas e relatórios em tempo real

---

## 🛠️ Tecnologias Utilizadas

### Backend Framework
- **Java 17** - Linguagem de programação
- **Spring Boot 3.5.0** - Framework principal
- **Spring Web** - Criação de APIs REST
- **Spring Data JPA** - Persistência e mapeamento objeto-relacional
- **Spring Security** - Autenticação e autorização
- **Bean Validation** - Validação de dados de entrada

### Segurança e Autenticação
- **JWT (JSON Web Tokens)** - Autenticação stateless
- **BCrypt** - Criptografia de senhas

### Banco de Dados
- **PostgreSQL** - Banco de dados principal (produção)
- **H2 Database** - Banco em memória (desenvolvimento)
- **Hibernate** - ORM para mapeamento objeto-relacional

### Ferramentas e Utilitários
- **Maven** - Gerenciamento de dependências
- **Swagger/OpenAPI 3** - Documentação automática da API
- **Spring Boot DevTools** - Ferramentas de desenvolvimento
- **Spring Boot Actuator** - Monitoramento da aplicação

### Arquitetura
- **Arquitetura em Camadas** - Controller → Service → Repository
- **Padrão DTO** - Data Transfer Objects para comunicação
- **Padrão Mapper** - Conversão entre entidades e DTOs
- **RESTful API** - Seguindo princípios REST

---

## ⚠️ Limitações do Projeto

### Limitações Técnicas
- **Upload de Imagens**: Não implementa upload real de arquivos para posters (apenas URLs)
- **Cache**: Sistema de cache não implementado para otimização de performance
- **Paginação**: Implementação básica de paginação, pode ser melhorada
- **Logs de Auditoria**: Não possui sistema de logs detalhados de ações dos usuários

### Limitações Funcionais
- **Notificações**: Sistema de notificações não desenvolvido
- **Recuperação de Senha**: Funcionalidade de "esqueci minha senha" não implementada
- **Sistema de Seguir Usuários**: Não possui funcionalidade social avançada
- **Integração Externa**: Não integra com APIs externas de filmes (TMDB, IMDB)

### Limitações de Infraestrutura
- **Deploy**: Configurado apenas para execução local
- **Monitoramento**: Métricas básicas, sem dashboard avançado
- **Rate Limiting**: Controle de taxa de requisições não implementado

### Considerações de Produção
- **CORS**: Configurado de forma permissiva (necessita ajuste para produção)
- **HTTPS**: Não configurado (recomendado para produção)
- **Variáveis de Ambiente**: Algumas configurações hardcoded

---

## 🗄️ Descrição das Entidades

### Usuario
Representa os usuários do sistema com diferentes níveis de acesso.

**Atributos:**
- `id` (Long) - Identificador único
- `nomeUsuario` (String) - Nome de usuário único (3-50 caracteres)
- `email` (String) - Email único e válido
- `senha` (String) - Senha criptografada (mínimo 6 caracteres)
- `perfil` (PerfilUsuario) - USUARIO ou ADMINISTRADOR
- `ativo` (Boolean) - Status de ativação da conta
- `dataCriacao`, `dataAtualizacao` (LocalDateTime) - Auditoria automática

**Relacionamentos:**
- 1:N com Avaliacao (um usuário pode avaliar vários filmes)
- 1:N com ListaFavoritos (um usuário pode ter várias listas)
- 1:N com Filme (como criador - apenas administradores)

**Regras de Negócio:**
- Nome de usuário e email devem ser únicos
- Perfil padrão é USUARIO
- Soft delete (marcação como inativo) para preservar integridade

### Filme
Entidade central que representa os filmes do catálogo.

**Atributos:**
- `id` (Long) - Identificador único
- `titulo` (String) - Título do filme (obrigatório, máximo 255 caracteres)
- `descricao` (Text) - Sinopse detalhada do filme
- `genero` (String) - Gênero cinematográfico (máximo 100 caracteres)
- `anoLancamento` (Integer) - Ano de lançamento (mínimo 1900)
- `duracao` (Integer) - Duração em minutos (mínimo 1)
- `diretor` (String) - Nome do diretor (máximo 150 caracteres)
- `urlPoster` (String) - URL da imagem do poster (máximo 500 caracteres)
- `ativo` (Boolean) - Status de ativação (padrão: true)
- `contadorAcessos` (Long) - **Contador para ranking de popularidade** (padrão: 0)
- `dataCriacao`, `dataAtualizacao` (LocalDateTime) - Auditoria automática
- `criadoPor` (Usuario) - Administrador que cadastrou o filme

**Relacionamentos:**
- 1:N com Avaliacao (um filme pode receber várias avaliações)
- N:N com ListaFavoritos (através de ListaFavoritosFilmes)

**Regras de Negócio:**
- Apenas administradores podem criar/editar filmes
- Contador de acessos incrementa automaticamente a cada visualização
- Soft delete para preservar histórico de avaliações

### Avaliacao
Representa as avaliações que usuários fazem dos filmes.

**Atributos:**
- `id` (Long) - Identificador único
- `nota` (Integer) - Nota de 1 a 5 (obrigatório)
- `comentario` (Text) - Comentário opcional do usuário
- `dataCriacao`, `dataAtualizacao` (LocalDateTime) - Auditoria automática

**Relacionamentos:**
- N:1 com Usuario (várias avaliações por usuário)
- N:1 com Filme (várias avaliações por filme)

**Regras de Negócio:**
- **Constraint única**: Um usuário só pode avaliar um filme uma vez
- Nota obrigatória entre 1 e 5
- Usuário pode atualizar sua própria avaliação
- Hard delete permitido (remove avaliação específica)

### ListaFavoritos
Listas personalizadas de filmes criadas pelos usuários.

**Atributos:**
- `id` (Long) - Identificador único
- `nome` (String) - Nome da lista (obrigatório, máximo 100 caracteres)
- `descricao` (Text) - Descrição opcional da lista
- `publica` (Boolean) - Define se outros usuários podem visualizar (padrão: true)
- `dataCriacao`, `dataAtualizacao` (LocalDateTime) - Auditoria automática

**Relacionamentos:**
- N:1 com Usuario (várias listas por usuário)
- N:N com Filme (através de ListaFavoritosFilmes)

**Regras de Negócio:**
- Apenas o proprietário pode editar a lista
- Listas públicas são visíveis para todos os usuários
- Listas privadas são visíveis apenas para o proprietário

### ListaFavoritosFilmes
Tabela associativa que gerencia a relação N:N entre listas e filmes.

**Atributos:**
- `id` (Long) - Identificador único
- `dataAdicao` (LocalDateTime) - Timestamp de quando o filme foi adicionado

**Relacionamentos:**
- N:1 com ListaFavoritos
- N:1 com Filme

**Regras de Negócio:**
- **Constraint única**: Um filme não pode ser duplicado na mesma lista
- Auditoria automática da data de adição
- Exclusão em cascata quando lista ou filme são removidos

---

## 🛣️ Descrição das Rotas

### Autenticação

#### POST `/auth/login`
Realiza autenticação no sistema.

**Requisição:**
```json
{
  "login": "admin",
  "senha": "123456"
}
```

**Resposta (200 - Sucesso):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tipo": "Bearer",
  "expiresIn": 86400,
  "usuario": {
    "id": 1,
    "nomeUsuario": "admin",
    "email": "admin@streamflix.com",
    "perfil": "ADMINISTRADOR",
    "ativo": true,
    "dataCriacao": "2025-01-15T10:00:00"
  }
}
```

#### POST `/auth/register`
Registra um novo usuário no sistema.

**Requisição:**
```json
{
  "nomeUsuario": "joao123",
  "email": "joao@email.com",
  "senha": "minhasenha"
}
```

**Resposta (201 - Criado):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tipo": "Bearer",
  "expiresIn": 86400,
  "usuario": {
    "id": 2,
    "nomeUsuario": "joao123",
    "email": "joao@email.com",
    "perfil": "USUARIO",
    "ativo": true
  }
}
```

### Filmes

#### GET `/api/filmes/ativos`
Lista filmes ativos com opção de ordenação por popularidade.

**Parâmetros de Query:**
- `orderByPopularidade` (boolean, opcional) - Ordena por número de acessos

**Exemplo de Requisição:**
```
GET /api/filmes/ativos?orderByPopularidade=true
```

**Resposta (200):**
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
      "urlPoster": "https://exemplo.com/inception.jpg",
      "contadorAcessos": 150
    }
  ],
  "timestamp": "2025-01-15T10:30:00"
}
```

#### GET `/api/filmes/{id}/detalhes`
Obtém detalhes completos de um filme. **Incrementa automaticamente o contador de acessos**.

**Resposta (200):**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "titulo": "Inception",
    "descricao": "Um ladrão que invade sonhos é dado a chance impossível...",
    "genero": "Ficção Científica",
    "anoLancamento": 2010,
    "duracao": 148,
    "diretor": "Christopher Nolan",
    "urlPoster": "https://exemplo.com/inception.jpg",
    "contadorAcessos": 151,
    "mediaAvaliacoes": 4.5,
    "totalAvaliacoes": 23,
    "dataCriacao": "2025-01-10T14:20:00",
    "criadoPor": {
      "id": 1,
      "nomeUsuario": "admin",
      "perfil": "ADMINISTRADOR"
    }
  }
}
```

#### GET `/api/filmes/ranking/popularidade`
Retorna ranking dos filmes mais populares baseado em número de acessos.

**Parâmetros de Query:**
- `limit` (integer, opcional, padrão: 10) - Número máximo de filmes no ranking

**Resposta (200):**
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
    },
    {
      "id": 2,
      "titulo": "The Matrix",
      "genero": "Ficção Científica",
      "contadorAcessos": 142,
      "posicaoRanking": 2
    }
  ]
}
```

#### POST `/api/filmes` (Requer autenticação de ADMINISTRADOR)
Cria um novo filme no catálogo.

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Requisição:**
```json
{
  "titulo": "Matrix",
  "descricao": "Um programador descobre que a realidade como ele a conhece não existe.",
  "genero": "Ficção Científica",
  "anoLancamento": 1999,
  "duracao": 136,
  "diretor": "Lana e Lilly Wachowski",
  "urlPoster": "https://exemplo.com/matrix.jpg"
}
```

**Resposta (201 - Criado):**
```json
{
  "success": true,
  "message": "Filme criado com sucesso",
  "data": {
    "id": 3,
    "titulo": "Matrix",
    "descricao": "Um programador descobre que a realidade...",
    "genero": "Ficção Científica",
    "anoLancamento": 1999,
    "duracao": 136,
    "diretor": "Lana e Lilly Wachowski",
    "urlPoster": "https://exemplo.com/matrix.jpg",
    "ativo": true,
    "contadorAcessos": 0,
    "criadoPor": {
      "id": 1,
      "nomeUsuario": "admin",
      "perfil": "ADMINISTRADOR"
    }
  }
}
```

### Avaliações

#### POST `/api/avaliacoes`
Cria uma nova avaliação para um filme.

**Headers:**
```
Authorization: Bearer {token}
```

**Requisição:**
```json
{
  "nota": 5,
  "comentario": "Filme excepcional! Christopher Nolan é um gênio.",
  "filmeId": 1
}
```

**Resposta (201):**
```json
{
  "success": true,
  "message": "Avaliação criada com sucesso",
  "data": {
    "id": 1,
    "nota": 5,
    "comentario": "Filme excepcional! Christopher Nolan é um gênio.",
    "dataCriacao": "2025-01-15T10:30:00",
    "dataAtualizacao": "2025-01-15T10:30:00",
    "usuario": {
      "id": 2,
      "nomeUsuario": "joao123",
      "perfil": "USUARIO"
    },
    "filme": {
      "id": 1,
      "titulo": "Inception",
      "genero": "Ficção Científica",
      "anoLancamento": 2010,
      "diretor": "Christopher Nolan",
      "urlPoster": "https://exemplo.com/inception.jpg",
      "contadorAcessos": 151
    }
  }
}
```

#### GET `/api/avaliacoes/filme/{filmeId}`
Lista todas as avaliações de um filme específico.

**Resposta (200):**
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

### Listas de Favoritos

#### POST `/api/listas-favoritos`
Cria uma nova lista de favoritos.

**Headers:**
```
Authorization: Bearer {token}
```

**Requisição:**
```json
{
  "nome": "Meus Filmes de Ficção Científica",
  "descricao": "Os melhores filmes de ficção científica que já assisti",
  "publica": true
}
```

**Resposta (201):**
```json
{
  "success": true,
  "message": "Lista criada com sucesso",
  "data": {
    "id": 1,
    "nome": "Meus Filmes de Ficção Científica",
    "descricao": "Os melhores filmes de ficção científica que já assisti",
    "publica": true,
    "dataCriacao": "2025-01-15T10:30:00",
    "dataAtualizacao": "2025-01-15T10:30:00",
    "usuario": {
      "id": 2,
      "nomeUsuario": "joao123",
      "perfil": "USUARIO"
    },
    "totalFilmes": 0
  }
}
```

#### POST `/api/listas-favoritos/{listaId}/filmes`
Adiciona um filme a uma lista de favoritos.

**Headers:**
```
Authorization: Bearer {token}
```

**Requisição:**
```json
{
  "filmeId": 1
}
```

**Resposta (200):**
```json
{
  "success": true,
  "message": "Filme adicionado à lista com sucesso",
  "data": null
}
```

### Dashboard

#### GET `/api/dashboard/publico`
Retorna estatísticas públicas do sistema.

**Resposta (200):**
```json
{
  "success": true,
  "data": {
    "totalFilmesAtivos": 25,
    "topFilmesPopulares": [
      {
        "id": 1,
        "titulo": "Inception",
        "genero": "Ficção Científica",
        "contadorAcessos": 151,
        "posicaoRanking": 1
      }
    ],
    "generos": [
      {
        "genero": "Ficção Científica",
        "totalFilmes": 8,
        "totalAcessos": 450,
        "mediaAvaliacoes": 4.2
      }
    ]
  }
}
```

---

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
    "nota": "Nota deve ser entre 1 e 5",
    "email": "Email deve ser válido"
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

### 409 - Conflict (Recurso Duplicado)
```json
{
  "timestamp": "2025-01-15T10:30:00",
  "status": 409,
  "error": "Business Logic Error",
  "message": "Usuário já avaliou este filme. Use o método update para alterar a avaliação."
}
```

---

## 🚀 Como Executar o Projeto Localmente

### Pré-requisitos
- **Java 17** ou superior
- **Maven 3.6+**
- **Git**
- **PostgreSQL** (opcional - pode usar H2)

### Passo a Passo

#### 1. Clone o Repositório
```bash
git clone https://github.com/bpmghb/streamflix-api.git
cd streamflix-api
```

#### 2. Configure o Banco de Dados

**Opção A: PostgreSQL (Recomendado para produção)**
```bash
# Instale o PostgreSQL e crie o banco
sudo -u postgres psql
CREATE DATABASE streamflix_db;
CREATE USER streamflix_user WITH PASSWORD 'streamflix_password';
GRANT ALL PRIVILEGES ON DATABASE streamflix_db TO streamflix_user;
\q
```

**Opção B: H2 (Desenvolvimento rápido)**
```properties
# Edite src/main/resources/application.properties
# Comente as linhas do PostgreSQL e descomente as do H2:

# spring.datasource.url=jdbc:postgresql://localhost:5432/streamflix_db
# spring.datasource.driver-class-name=org.postgresql.Driver
# spring.datasource.username=postgres
# spring.datasource.password=bpm@2025!

spring.datasource.url=jdbc:h2:file:./streamflix.db
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.h2.console.enabled=true
```

#### 3. Configure as Variáveis de Ambiente (Opcional)
```bash
# Copie o arquivo de exemplo
cp .env.example .env

# Edite as configurações conforme necessário
nano .env
```

#### 4. Execute o Projeto
```bash
# Compile e execute
mvn clean install
mvn spring-boot:run

# OU execute o JAR diretamente
mvn clean package
java -jar target/streamflix-api-0.0.1-SNAPSHOT.jar
```

#### 5. Verificar se Está Funcionando

**Teste da API:**
```bash
# Health check
curl http://localhost:8080/api/dashboard/publico

# Login do administrador padrão
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login":"admin","senha":"123456"}'
```

**Acesse a Documentação:**
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/v3/api-docs

**Console H2 (se usar H2):**
- **URL**: http://localhost:8080/h2-console
- **JDBC URL**: `jdbc:h2:file:./streamflix.db`
- **Username**: `sa`
- **Password**: `password`

### Usuários Padrão

O sistema vem com usuários pré-cadastrados para teste:

| Usuário | Senha | Perfil |
|---------|-------|--------|
| admin   | 123456 | ADMINISTRADOR |
| matheus | 123456 | USUARIO |
| bianca  | 123456 | USUARIO |
| pedro   | 123456 | USUARIO |

### Estrutura de Portas
- **Aplicação**: http://localhost:8080
- **H2 Console**: http://localhost:8080/h2-console
- **Swagger**: http://localhost:8080/swagger-ui.html

---

## 🚀 Outros Conteúdos Relevantes

### Carta-Desafio: Ranking de Popularidade
O projeto implementa um sistema avançado de ranking baseado em acessos reais:

**Funcionamento:**
1. **Contador Automático**: Cada acesso a um filme via GET incrementa o `contadorAcessos`
2. **Ordenação Dinâmica**: Endpoint `/api/filmes/ativos?orderByPopularidade=true` retorna filmes ordenados por popularidade
3. **Top N**: Endpoint `/api/filmes/ranking/popularidade?limit=10` retorna os filmes mais populares
4. **Posicionamento**: Cada filme recebe sua posição exata no ranking

**Implementação Técnica:**
```java
// Na entidade Filme
public void incrementarAcessos() {
    this.contadorAcessos++;
}

// No FilmeService
public Optional<Filme> getOne(Long id) {
    Optional<Filme> filme = filmeRepository.findById(id);
    if (filme.isPresent()) {
        filme.get().incrementarAcessos();
        filmeRepository.save(filme.get());
    }
    return filme;
}
```

### Arquitetura de Segurança
- **JWT Stateless**: Tokens auto-contidos com claims do usuário
- **Role-based Access**: Diferenciação entre USUARIO e ADMINISTRADOR
- **Password Encryption**: BCrypt para senhas
- **CORS Configuration**: Configurável para diferentes ambientes

### Padrões Implementados
- **Repository Pattern**: Abstração da camada de dados
- **DTO Pattern**: Separação entre entidades e dados de transferência
- **Mapper Pattern**: Conversão automática entre entidades e DTOs
- **Soft Delete**: Preservação de integridade referencial
- **Upsert Pattern**: Criar ou atualizar em uma operação

### Sistema de Validações
- **Bean Validation**: Validações declarativas nas entidades
- **Business Rules**: Validações de negócio nos services
- **Security Checks**: Validações de permissão nos controllers
- **Data Integrity**: Constraints de banco de dados

### Documentação Automática (não funciona mas foi tentaado)
- **Swagger/OpenAPI 3**: Documentação interativa completa
- **Schemas**: Definição automática de modelos de dados
- **Exemplos**: Requests e responses de exemplo
- **Try it out**: Interface para testar endpoints diretamente

### Performance e Otimizações
- **Lazy Loading**: Carregamento sob demanda de relacionamentos
- **Query Optimization**: Queries específicas para cada caso de uso
- **Indexação**: Índices em campos de busca frequente
- **Pagination**: Suporte a paginação para grandes datasets

### Monitoramento
- **Spring Actuator**: Endpoints de health check e métricas
- **Logging**: Logs estruturados para debugging
- **Exception Handling**: Tratamento centralizado de erros
- **Audit Trail**: Timestamps automáticos de criação/atualização

---

**Repositório**: https://github.com/bpmghb/streamflix-api

---