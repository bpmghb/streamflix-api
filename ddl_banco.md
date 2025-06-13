-- ========================================
-- CRIAÇÃO DAS TABELAS
-- ========================================

-- Tabela: usuarios
CREATE TABLE usuarios (
id BIGSERIAL PRIMARY KEY,
nome_usuario VARCHAR(50) UNIQUE NOT NULL,
email VARCHAR(100) UNIQUE NOT NULL,
senha VARCHAR(255) NOT NULL,
perfil VARCHAR(20) NOT NULL DEFAULT 'USUARIO',
ativo BOOLEAN DEFAULT true,
data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_perfil CHECK (perfil IN ('USUARIO', 'ADMINISTRADOR'))
);

-- Tabela: filmes
CREATE TABLE filmes (
id BIGSERIAL PRIMARY KEY,
titulo VARCHAR(255) NOT NULL,
descricao TEXT,
genero VARCHAR(100),
ano_lancamento INTEGER,
duracao INTEGER,
diretor VARCHAR(150),
url_poster VARCHAR(500),
ativo BOOLEAN DEFAULT true,
contador_acessos BIGINT DEFAULT 0,
data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
criado_por BIGINT,

    CONSTRAINT fk_filme_criado_por FOREIGN KEY (criado_por) REFERENCES usuarios(id),
    CONSTRAINT chk_ano_lancamento CHECK (ano_lancamento >= 1900 AND ano_lancamento <= EXTRACT(YEAR FROM CURRENT_DATE) + 5),
    CONSTRAINT chk_duracao CHECK (duracao > 0),
    CONSTRAINT chk_contador_acessos CHECK (contador_acessos >= 0)
);

-- Tabela: avaliacoes
CREATE TABLE avaliacoes (
id BIGSERIAL PRIMARY KEY,
nota INTEGER NOT NULL,
comentario TEXT,
usuario_id BIGINT NOT NULL,
filme_id BIGINT NOT NULL,
data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_avaliacao_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    CONSTRAINT fk_avaliacao_filme FOREIGN KEY (filme_id) REFERENCES filmes(id) ON DELETE CASCADE,
    CONSTRAINT chk_nota CHECK (nota >= 1 AND nota <= 5),
    CONSTRAINT uk_usuario_filme UNIQUE (usuario_id, filme_id)
);

-- Tabela: listas_favoritos
CREATE TABLE listas_favoritos (
id BIGSERIAL PRIMARY KEY,
nome VARCHAR(100) NOT NULL,
descricao TEXT,
usuario_id BIGINT NOT NULL,
publica BOOLEAN DEFAULT true,
data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_lista_favoritos_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- Tabela: lista_favoritos_filmes
CREATE TABLE lista_favoritos_filmes (
id BIGSERIAL PRIMARY KEY,
lista_favoritos_id BIGINT NOT NULL,
filme_id BIGINT NOT NULL,
data_adicao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_lista_favoritos_filmes_lista FOREIGN KEY (lista_favoritos_id) REFERENCES listas_favoritos(id) ON DELETE CASCADE,
    CONSTRAINT fk_lista_favoritos_filmes_filme FOREIGN KEY (filme_id) REFERENCES filmes(id) ON DELETE CASCADE,
    CONSTRAINT uk_lista_filme UNIQUE (lista_favoritos_id, filme_id)
);

-- ========================================
-- ÍNDICES PARA PERFORMANCE
-- ========================================

-- Índices para busca otimizada
CREATE INDEX idx_usuarios_nome_usuario ON usuarios(nome_usuario);
CREATE INDEX idx_usuarios_email ON usuarios(email);
CREATE INDEX idx_usuarios_ativo ON usuarios(ativo);

CREATE INDEX idx_filmes_titulo ON filmes(titulo);
CREATE INDEX idx_filmes_genero ON filmes(genero);
CREATE INDEX idx_filmes_ano_lancamento ON filmes(ano_lancamento);
CREATE INDEX idx_filmes_diretor ON filmes(diretor);
CREATE INDEX idx_filmes_ativo ON filmes(ativo);
CREATE INDEX idx_filmes_contador_acessos ON filmes(contador_acessos DESC); -- Para ranking
CREATE INDEX idx_filmes_data_criacao ON filmes(data_criacao);

CREATE INDEX idx_avaliacoes_usuario_id ON avaliacoes(usuario_id);
CREATE INDEX idx_avaliacoes_filme_id ON avaliacoes(filme_id);
CREATE INDEX idx_avaliacoes_nota ON avaliacoes(nota);
CREATE INDEX idx_avaliacoes_data_criacao ON avaliacoes(data_criacao);

CREATE INDEX idx_listas_favoritos_usuario_id ON listas_favoritos(usuario_id);
CREATE INDEX idx_listas_favoritos_publica ON listas_favoritos(publica);
CREATE INDEX idx_listas_favoritos_nome ON listas_favoritos(nome);

CREATE INDEX idx_lista_favoritos_filmes_lista_id ON lista_favoritos_filmes(lista_favoritos_id);
CREATE INDEX idx_lista_favoritos_filmes_filme_id ON lista_favoritos_filmes(filme_id);

-- ========================================
-- TRIGGERS PARA ATUALIZAÇÃO AUTOMÁTICA
-- ========================================

-- Função para atualizar data_atualizacao
CREATE OR REPLACE FUNCTION update_data_atualizacao()
RETURNS TRIGGER AS $$
BEGIN
NEW.data_atualizacao = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Triggers para atualização automática
CREATE TRIGGER trigger_usuarios_update
BEFORE UPDATE ON usuarios
FOR EACH ROW
EXECUTE FUNCTION update_data_atualizacao();

CREATE TRIGGER trigger_filmes_update
BEFORE UPDATE ON filmes
FOR EACH ROW
EXECUTE FUNCTION update_data_atualizacao();

CREATE TRIGGER trigger_avaliacoes_update
BEFORE UPDATE ON avaliacoes
FOR EACH ROW
EXECUTE FUNCTION update_data_atualizacao();

CREATE TRIGGER trigger_listas_favoritos_update
BEFORE UPDATE ON listas_favoritos
FOR EACH ROW
EXECUTE FUNCTION update_data_atualizacao();

-- ========================================
-- DADOS INICIAIS (SEEDS)
-- ========================================

-- Inserir usuário administrador padrão
INSERT INTO usuarios (nome_usuario, email, senha, perfil) VALUES
('admin', 'admin@streamflix.com', '$2a$12$NH1thbASZt6FesPOdNwuKOnYmIVOaLk5L8fk23EFSwlECE9jIVXS6', 'ADMINISTRADOR'); -- senha: senha12345

-- Inserir usuários de teste
INSERT INTO usuarios (nome_usuario, email, senha, perfil) VALUES
('matheus', 'matheus@email.com', '$2a$12$NH1thbASZt6FesPOdNwuKOnYmIVOaLk5L8fk23EFSwlECE9jIVXS6', 'USUARIO'), -- senha: senha12345
('bianca', 'bianca@email.com', '$2a$12$NH1thbASZt6FesPOdNwuKOnYmIVOaLk5L8fk23EFSwlECE9jIVXS6', 'USUARIO'), -- senha: senha12345
('pedro', 'pedro@email.com', '$2a$12$NH1thbASZt6FesPOdNwuKOnYmIVOaLk5L8fk23EFSwlECE9jIVXS6', 'USUARIO'); -- senha: senha12345

-- Inserir filmes de exemplo
INSERT INTO filmes (titulo, descricao, genero, ano_lancamento, duracao, diretor, url_poster, criado_por) VALUES
('Inception', 'Um ladrão que invade sonhos é dado a chance impossível de apagar seu registro criminal.', 'Ficção Científica', 2010, 148, 'Christopher Nolan', 'https://exemplo.com/inception.jpg', 1),
('The Matrix', 'Um programador descobre que a realidade como ele a conhece não existe.', 'Ficção Científica', 1999, 136, 'Lana Wachowski, Lilly Wachowski', 'https://exemplo.com/matrix.jpg', 1),
('Pulp Fiction', 'As vidas de dois assassinos da máfia se entrelaçam com as de outros criminosos.', 'Crime', 1994, 154, 'Quentin Tarantino', 'https://exemplo.com/pulpfiction.jpg', 1),
('The Godfather', 'O patriarca de uma dinastia do crime organizado transfere controle de seu império clandestino para seu filho relutante.', 'Crime', 1972, 175, 'Francis Ford Coppola', 'https://exemplo.com/godfather.jpg', 1),
('Interstellar', 'Uma equipe de exploradores viaja através de um buraco de minhoca no espaço.', 'Ficção Científica', 2014, 169, 'Christopher Nolan', 'https://exemplo.com/interstellar.jpg', 1);

-- Inserir avaliações de exemplo
INSERT INTO avaliacoes (nota, comentario, usuario_id, filme_id) VALUES
(5, 'Filme incrível! Christopher Nolan é um gênio.', 2, 1),
(4, 'Muito bom, mas confuso em algumas partes.', 3, 1),
(5, 'Revolucionário! Mudou minha visão sobre realidade.', 2, 2),
(4, 'Clássico do cinema, imperdível.', 3, 3),
(5, 'Uma obra-prima do cinema.', 4, 4),
(4, 'Visualmente impressionante.', 2, 5);

-- Inserir listas de favoritos de exemplo
INSERT INTO listas_favoritos (nome, descricao, usuario_id, publica) VALUES
('Meus Filmes de Ficção Científica', 'Os melhores filmes de ficção científica que já assisti', 2, true),
('Clássicos do Cinema', 'Filmes clássicos que todo cinéfilo deve assistir', 3, true),
('Lista Privada do João', 'Minha lista pessoal', 2, false);

-- Inserir filmes nas listas
INSERT INTO lista_favoritos_filmes (lista_favoritos_id, filme_id) VALUES
(1, 1), -- Inception na lista de Ficção Científica
(1, 2), -- Matrix na lista de Ficção Científica
(1, 5), -- Interstellar na lista de Ficção Científica
(2, 3), -- Pulp Fiction nos Clássicos
(2, 4), -- The Godfather nos Clássicos
(3, 1), -- Inception na lista privada
(3, 4); -- The Godfather na lista privada

-- ========================================
-- VIEWS ÚTEIS PARA CONSULTAS
-- ========================================

-- View para estatísticas de filmes
CREATE VIEW vw_estatisticas_filmes AS
SELECT
f.id,
f.titulo,
f.genero,
f.contador_acessos,
COUNT(a.id) as total_avaliacoes,
ROUND(AVG(a.nota), 2) as media_avaliacoes,
COUNT(DISTINCT lff.lista_favoritos_id) as total_listas_favoritos
FROM filmes f
LEFT JOIN avaliacoes a ON f.id = a.filme_id
LEFT JOIN lista_favoritos_filmes lff ON f.id = lff.filme_id
WHERE f.ativo = true
GROUP BY f.id, f.titulo, f.genero, f.contador_acessos;

-- View para ranking de popularidade
CREATE VIEW vw_ranking_popularidade AS
SELECT
f.id,
f.titulo,
f.genero,
f.contador_acessos,
ROW_NUMBER() OVER (ORDER BY f.contador_acessos DESC) as posicao_ranking
FROM filmes f
WHERE f.ativo = true
ORDER BY f.contador_acessos DESC;

-- ========================================
-- COMENTÁRIOS DAS TABELAS
-- ========================================

COMMENT ON TABLE usuarios IS 'Tabela de usuários do sistema';
COMMENT ON TABLE filmes IS 'Tabela de filmes do catálogo';
COMMENT ON TABLE avaliacoes IS 'Tabela de avaliações dos usuários para os filmes';
COMMENT ON TABLE listas_favoritos IS 'Tabela de listas de favoritos criadas pelos usuários';
COMMENT ON TABLE lista_favoritos_filmes IS 'Tabela de relacionamento N:N entre listas e filmes';

COMMENT ON COLUMN filmes.contador_acessos IS 'Contador para ranking de popularidade - incrementado a cada GET';
COMMENT ON COLUMN usuarios.perfil IS 'Perfil do usuário: USUARIO ou ADMINISTRADOR';
COMMENT ON COLUMN avaliacoes.nota IS 'Nota de 1 a 5 estrelas';
COMMENT ON COLUMN listas_favoritos.publica IS 'Define se a lista pode ser visualizada por outros usuários';

-- ========================================
-- GRANTS E PERMISSÕES
-- ========================================

-- Conceder permissões para o usuário da aplicação (opcional)
-- CREATE USER streamflix_user WITH PASSWORD 'sua_senha_segura';
-- GRANT CONNECT ON DATABASE streamflix_db TO streamflix_user;
-- GRANT USAGE ON SCHEMA public TO streamflix_user;
-- GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO streamflix_user;
-- GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO streamflix_user;

-- ========================================
-- VERIFICAÇÕES FINAIS
-- ========================================

-- Verificar se as tabelas foram criadas corretamente
SELECT table_name FROM information_schema.tables
WHERE table_schema = 'public'
ORDER BY table_name;

-- Verificar dados inseridos
SELECT 'usuarios' as tabela, COUNT(*) as total FROM usuarios
UNION ALL
SELECT 'filmes', COUNT(*) FROM filmes
UNION ALL
SELECT 'avaliacoes', COUNT(*) FROM avaliacoes
UNION ALL
SELECT 'listas_favoritos', COUNT(*) FROM listas_favoritos
UNION ALL
SELECT 'lista_favoritos_filmes', COUNT(*) FROM lista_favoritos_filmes;