package com.davison.taskmanager.dto.task;

import com.davison.taskmanager.enums.Prioridade;
import com.davison.taskmanager.enums.Status;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record TaskCreateRequest(

    @NotBlank(message = "Título é obrigatório")
    @Size(min = 3, max = 100, message = "Título deve ter entre 3 e 100 caracteres")
    String titulo,

    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    String descricao,

    @NotNull(message = "Status é obrigatório")
    Status status,

    @NotNull(message = "Prioridade é obrigatória")
    Prioridade prioridade,

    @NotNull(message = "Prazo é obrigatório")
    @Future(message = "Prazo deve ser uma data futura")
    LocalDateTime prazo
) {}