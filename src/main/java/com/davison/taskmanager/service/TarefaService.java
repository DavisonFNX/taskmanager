package com.davison.taskmanager.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.davison.taskmanager.dto.task.TaskCreateRequest;
import com.davison.taskmanager.dto.task.TaskCreateResponse;
import com.davison.taskmanager.dto.task.TaskResponse;
import com.davison.taskmanager.dto.task.TaskUpdateRequest;
import com.davison.taskmanager.entity.Tarefa;
import com.davison.taskmanager.entity.Usuario;
import com.davison.taskmanager.enums.Prioridade;
import com.davison.taskmanager.enums.Status;
import com.davison.taskmanager.exception.BusinessException;
import com.davison.taskmanager.exception.ResourceNotFoundException;
import com.davison.taskmanager.integration.brasilapi.HolidayService;
import com.davison.taskmanager.repository.TarefaRepository;
import com.davison.taskmanager.repository.UsuarioRepository;

@Service
public class TarefaService {

    private final TarefaRepository tarefaRepository;
    private final HolidayService holidayService;
    private final UsuarioRepository usuarioRepository;

    public TarefaService(TarefaRepository tarefaRepository, HolidayService holidayService, UsuarioRepository usuarioRepository) {
        this.tarefaRepository = tarefaRepository;
        this.holidayService = holidayService;
        this.usuarioRepository = usuarioRepository;
    }

    public List<TaskResponse> listarTarefas(Usuario usuario, Status status, Sort sort) {
        List<Tarefa> tarefas;
        if (status == null) {
            tarefas = tarefaRepository.findByUsuario(usuario, sort);
        } else {
            tarefas = tarefaRepository.findByUsuarioAndStatus(usuario, status, sort);
        }
        return tarefas.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public TaskResponse buscarPorId(Long id, Usuario usuario) {
        Tarefa tarefa = tarefaRepository.findByIdAndUsuario(id, usuario)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada"));
        return toResponse(tarefa);
    }

    public TaskCreateResponse criar(TaskCreateRequest request, Usuario usuario) {
        validarPrazo(request.prazo());
        String avisoFeriado = null;
        Tarefa tarefa = new Tarefa();
        tarefa.setTitulo(request.titulo());
        tarefa.setDescricao(request.descricao());
        tarefa.setStatus(request.status());
        tarefa.setPrioridade(request.prioridade());
        tarefa.setPrazo(request.prazo());
        tarefa.setUsuario(usuario);
        String feriado = holidayService.findHoliday(request.prazo().toLocalDate());
        
         if (feriado != null) {
            avisoFeriado = "Atencao: o prazo informado cai em um feriado nacional (" + feriado + ").";
        }

        Tarefa saved = tarefaRepository.save(tarefa);

        return new TaskCreateResponse(saved.getId(), avisoFeriado);
    }

    public TaskResponse atualizar(Long id, TaskUpdateRequest request, Usuario usuario) {
        Tarefa tarefa = tarefaRepository.findByIdAndUsuario(id, usuario)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada"));

        if (request.titulo() != null) {
            tarefa.setTitulo(request.titulo());
        }

        if (request.descricao() != null) {
            tarefa.setDescricao(request.descricao());
        }

        if (request.status() != null) {
            try {
                tarefa.setStatus(Status.valueOf(request.status()));
            } catch (IllegalArgumentException e) {
                throw new BusinessException("Status inválido");
            }
        }

        if (request.prioridade() != null) {
            try {
                tarefa.setPrioridade(Prioridade.valueOf(request.prioridade()));
            } catch (IllegalArgumentException e) {
                throw new BusinessException("Prioridade inválida");
            }
        }

        if (request.prazo() != null) {
            validarPrazo(request.prazo());
            tarefa.setPrazo(request.prazo());
        }

        Tarefa updated = tarefaRepository.save(tarefa);
        return toResponse(updated);
    }

    public void remover(Long id, Usuario usuario) {
        Tarefa tarefa = tarefaRepository.findByIdAndUsuario(id, usuario)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada"));
        tarefaRepository.delete(tarefa);
    }

    public List<TaskResponse> listarVencidas(Usuario usuario) {
        List<Tarefa> tarefas = tarefaRepository.findTarefasVencidas(usuario, LocalDateTime.now());
        return tarefas.stream().map(this::toResponse).collect(Collectors.toList());
    }

    private void validarPrazo(LocalDateTime prazo) {
        if (prazo == null) {
            throw new BusinessException("O prazo é obrigatório");
        }

        if (prazo.isBefore(LocalDateTime.now())) {
            throw new BusinessException("O prazo deve ser uma data futura");
        }

    }

    private TaskResponse toResponse(Tarefa tarefa) {
        return new TaskResponse(
                tarefa.getId(),
                tarefa.getTitulo(),
                tarefa.getDescricao(),
                tarefa.getStatus().name(),
                tarefa.getPrioridade().name(),
                tarefa.getPrazo(),
                tarefa.getUsuario().getId(),
                tarefa.getUsuario().getNome()
        );
    }

    public Usuario findUserByEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
    }
}