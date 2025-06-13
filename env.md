# ========================================
# STREAMFLIX API - VARIÁVEIS DE AMBIENTE
# ========================================

# Configurações da Aplicação
SPRING_APPLICATION_NAME=streamflix-api
SERVER_PORT=8080

# ========================================
# CONFIGURAÇÕES DO BANCO DE DADOS
# ========================================

# Banco H2 (Desenvolvimento) - COMENTAR ESTAS LINHAS
# SPRING_DATASOURCE_URL=jdbc:h2:file:./streamflix.db
# SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.h2.Driver
# SPRING_DATASOURCE_USERNAME=sa
# SPRING_DATASOURCE_PASSWORD=password

# Banco PostgreSQL (Produção) - DESCOMENTAR E CONFIGURAR
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/streamflix_db
SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.postgresql.Driver
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=bpm@2025!

# ========================================
# CONFIGURAÇÕES JPA/HIBERNATE
# ========================================

# Para H2 - COMENTAR
# SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.H2Dialect

# Para PostgreSQL - DESCOMENTAR
SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.PostgreSQLDialect
SPRING_JPA_HIBERNATE_DDL_AUTO=validate
SPRING_JPA_SHOW_SQL=true
SPRING_JPA_PROPERTIES_HIBERNATE_FORMAT_SQL=true

# ========================================
# CONFIGURAÇÕES H2 CONSOLE - DESABILITAR
# ========================================

SPRING_H2_CONSOLE_ENABLED=false
# SPRING_H2_CONSOLE_PATH=/h2-console

# ========================================
# CONFIGURAÇÕES H2 CONSOLE
# ========================================

SPRING_H2_CONSOLE_ENABLED=true
SPRING_H2_CONSOLE_PATH=/h2-console

# ========================================
# CONFIGURAÇÕES JWT
# ========================================

# Chave secreta para assinatura dos tokens JWT
# IMPORTANTE: Mudar em produção para uma chave mais segura!
APP_JWT_SECRET=mySecretKey123456789012345678901234567890StreamFlixApiJwtSecret2025
APP_JWT_EXPIRATION=86400000

# ========================================
# CONFIGURAÇÕES DE LOGGING
# ========================================

LOGGING_LEVEL_ORG_SPRINGFRAMEWORK_SECURITY=DEBUG
LOGGING_LEVEL_COM_STREAMFLIX_API=DEBUG
LOGGING_LEVEL_ORG_HIBERNATE_SQL=DEBUG
LOGGING_LEVEL_ORG_HIBERNATE_TYPE_DESCRIPTOR_SQL_BASICBINDER=TRACE

# ========================================
# CONFIGURAÇÕES DE PERFIL
# ========================================

# Perfil ativo (dev, test, prod)
SPRING_PROFILES_ACTIVE=dev

# ========================================
# CONFIGURAÇÕES DE CORS
# ========================================

# Origens permitidas para CORS (separadas por vírgula)
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:4200,http://localhost:8081

# ========================================
# CONFIGURAÇÕES DE UPLOAD DE ARQUIVOS
# ========================================

# Tamanho máximo para upload de arquivos (posters de filmes)
SPRING_SERVLET_MULTIPART_MAX_FILE_SIZE=10MB
SPRING_SERVLET_MULTIPART_MAX_REQUEST_SIZE=10MB

# ========================================
# CONFIGURAÇÕES DE ACTUATOR (Monitoramento)
# ========================================

MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=health,info,metrics
MANAGEMENT_ENDPOINT_HEALTH_SHOW_DETAILS=when_authorized

# ========================================
# CONFIGURAÇÕES DE SWAGGER/OpenAPI
# ========================================

SPRINGDOC_API_DOCS_PATH=/v3/api-docs
SPRINGDOC_SWAGGER_UI_PATH=/swagger-ui.html

# ========================================
# CONFIGURAÇÕES DE DESENVOLVIMENTO
# ========================================

# Recarregamento automático
SPRING_DEVTOOLS_RESTART_ENABLED=true
SPRING_DEVTOOLS_LIVERELOAD_ENABLED=true

# ========================================
# CONFIGURAÇÕES DE PRODUÇÃO
# ========================================

# Configurações para quando for para produção
# SPRING_PROFILES_ACTIVE=prod
# LOGGING_LEVEL_ROOT=WARN
# SPRING_JPA_SHOW_SQL=false
# SPRING_H2_CONSOLE_ENABLED=false
# SERVER_ERROR_INCLUDE_STACKTRACE=never