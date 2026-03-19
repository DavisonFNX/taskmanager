package com.davison.taskmanager.dto.task;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record TaskUpdateRequest(
    @NotNull Long id,
    String titulo,
    String descricao,
    String status,
    String prioridade,
    LocalDateTime prazo
) {}