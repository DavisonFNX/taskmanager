package com.davison.taskmanager.controller;

import com.davison.taskmanager.dto.task.TaskCreateRequest;
import com.davison.taskmanager.dto.task.TaskCreateResponse;
import com.davison.taskmanager.dto.task.TaskResponse;
import com.davison.taskmanager.dto.task.TaskUpdateRequest;
import com.davison.taskmanager.entity.Usuario;
import com.davison.taskmanager.enums.Status;
import com.davison.taskmanager.exception.BusinessException;
import com.davison.taskmanager.exception.ResourceNotFoundException;
import com.davison.taskmanager.repository.UsuarioRepository;
import com.davison.taskmanager.service.TarefaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@Tag(name = "Tarefas", description = "Endpoints para gerenciamento de tarefas")
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

    private final TarefaService tarefaService;
    private final UsuarioRepository usuarioRepository;

    public TaskController(TarefaService tarefaService, UsuarioRepository usuarioRepository) {
        this.tarefaService = tarefaService;
        this.usuarioRepository = usuarioRepository;
    }

    private Usuario getUsuarioFromAuth(Authentication authentication) {
        String email = authentication.getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
    }

    @GetMapping
    @Operation(summary = "Listar tarefas", description = "Lista todas as tarefas do usuário autenticado, com opção de filtro por status e ordenação")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de tarefas retornada com sucesso",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskResponse.class))),
        @ApiResponse(responseCode = "401", description = "Token JWT inválido ou ausente", content = @Content)
    })
    public ResponseEntity<List<TaskResponse>> listarTarefas(
            @Parameter(description = "Filtrar por status (PENDENTE, EM_ANDAMENTO, CONCLUIDA)")
            @RequestParam(required = false) String status,
            @Parameter(description = "Ordenação (ex: prazo,asc ou titulo,desc)", example = "prazo,asc")
            @RequestParam(defaultValue = "prazo,asc") String sort,
            Authentication authentication) {

        Usuario usuario = getUsuarioFromAuth(authentication);
        Status statusEnum = parseStatus(status);
        Sort sortObj = parseSort(sort);

        List<TaskResponse> tarefas = tarefaService.listarTarefas(usuario, statusEnum, sortObj);
        return ResponseEntity.ok(tarefas);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar tarefa por ID", description = "Retorna uma tarefa específica do usuário autenticado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tarefa encontrada",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskResponse.class))),
        @ApiResponse(responseCode = "404", description = "Tarefa não encontrada", content = @Content),
        @ApiResponse(responseCode = "401", description = "Token JWT inválido ou ausente", content = @Content)
    })
    public ResponseEntity<TaskResponse> buscarPorId(
            @Parameter(description = "ID da tarefa") @PathVariable Long id,
            Authentication authentication) {

        Usuario usuario = getUsuarioFromAuth(authentication);
        TaskResponse tarefa = tarefaService.buscarPorId(id, usuario);
        return ResponseEntity.ok(tarefa);
    }

    @PostMapping
    @Operation(summary = "Criar nova tarefa", description = "Cria uma nova tarefa para o usuário autenticado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tarefa criada com sucesso",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskCreateResponse.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
        @ApiResponse(responseCode = "401", description = "Token JWT inválido ou ausente", content = @Content)
    })
    public ResponseEntity<TaskCreateResponse> criar(
            @Valid @RequestBody TaskCreateRequest request,
            Authentication authentication) {

        Usuario usuario = getUsuarioFromAuth(authentication);
        TaskCreateResponse response = tarefaService.criar(request, usuario);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar tarefa", description = "Atualiza uma tarefa existente do usuário autenticado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tarefa atualizada com sucesso",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskResponse.class))),
        @ApiResponse(responseCode = "404", description = "Tarefa não encontrada", content = @Content),
        @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
        @ApiResponse(responseCode = "401", description = "Token JWT inválido ou ausente", content = @Content)
    })
    public ResponseEntity<TaskResponse> atualizar(
            @Parameter(description = "ID da tarefa") @PathVariable Long id,
            @Valid @RequestBody TaskUpdateRequest request,
            Authentication authentication) {

        Usuario usuario = getUsuarioFromAuth(authentication);
        TaskResponse response = tarefaService.atualizar(id, request, usuario);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover tarefa", description = "Remove uma tarefa do usuário autenticado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Tarefa removida com sucesso"),
        @ApiResponse(responseCode = "404", description = "Tarefa não encontrada", content = @Content),
        @ApiResponse(responseCode = "401", description = "Token JWT inválido ou ausente", content = @Content)
    })
    public ResponseEntity<Void> remover(
            @Parameter(description = "ID da tarefa") @PathVariable Long id,
            Authentication authentication) {

        Usuario usuario = getUsuarioFromAuth(authentication);
        tarefaService.remover(id, usuario);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/vencidas")
    @Operation(summary = "Listar tarefas vencidas", description = "Lista todas as tarefas vencidas do usuário autenticado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de tarefas vencidas retornada com sucesso",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskResponse.class))),
        @ApiResponse(responseCode = "401", description = "Token JWT inválido ou ausente", content = @Content)
    })
    public ResponseEntity<List<TaskResponse>> listarVencidas(Authentication authentication) {
        Usuario usuario = getUsuarioFromAuth(authentication);
        List<TaskResponse> tarefas = tarefaService.listarVencidas(usuario);
        return ResponseEntity.ok(tarefas);
    }

    private Status parseStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }

        try {
            return Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Status inválido");
        }
    }

    private Sort parseSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by("prazo").ascending();
        }

        String[] parts = sort.split(",");
        String property = parts[0];
        Sort.Direction direction = parts.length > 1 && "desc".equalsIgnoreCase(parts[1])
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        return Sort.by(direction, property);
    }
}