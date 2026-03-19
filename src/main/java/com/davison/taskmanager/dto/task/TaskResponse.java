package com.davison.taskmanager.dto.task;

import java.time.LocalDateTime;

public record TaskResponse(
    Long id,
    String titulo,
    String descricao,
    String status,
    String prioridade,
    LocalDateTime prazo,
    Long usuarioId,
    String usuarioNome
) {}