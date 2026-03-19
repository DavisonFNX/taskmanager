package com.davison.taskmanager.dto.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record TaskCreateRequest(
    @NotBlank String titulo,
    String descricao,
    @NotBlank String status,
    @NotBlank String prioridade,
    LocalDateTime prazo,
    @NotNull Long usuarioId
) {}