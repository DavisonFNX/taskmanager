
CREATE TABLE tarefa (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    descricao TEXT,
    status VARCHAR(50),
    prioridade VARCHAR(50),
    prazo TIMESTAMP,
    usuario_id BIGINT,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

-- Índices para otimizar buscas
CREATE INDEX idx_tarefa_usuario_id ON tarefa(usuario_id);
CREATE INDEX idx_tarefa_status ON tarefa(status);