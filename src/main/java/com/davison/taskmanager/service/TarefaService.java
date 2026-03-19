package com.davison.taskmanager.service;

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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TarefaService {

    private final TarefaRepository tarefaRepository;
    private final HolidayService holidayService;

    public TarefaService(TarefaRepository tarefaRepository, HolidayService holidayService) {
        this.tarefaRepository = tarefaRepository;
        this.holidayService = holidayService;
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
        Status status;
        Prioridade prioridade;
        try {
            status = Status.valueOf(request.status());
            prioridade = Prioridade.valueOf(request.prioridade());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Status ou prioridade inválida");
        }

        Tarefa tarefa = new Tarefa();
        tarefa.setTitulo(request.titulo());
        tarefa.setDescricao(request.descricao());
        tarefa.setStatus(status);
        tarefa.setPrioridade(prioridade);
        tarefa.setPrazo(request.prazo());
        tarefa.setUsuario(usuario);

        Tarefa saved = tarefaRepository.save(tarefa);

        String aviso = null;
        if (request.prazo() != null) {
            LocalDateTime prazo = request.prazo();
            if (holidayService.isHoliday(prazo.toLocalDate())) {
                aviso = "A data do prazo cai em um feriado nacional.";
            }
        }

        return new TaskCreateResponse(saved.getId(), aviso);
    }

    public TaskResponse atualizar(Long id, TaskUpdateRequest request, Usuario usuario) {
        Tarefa tarefa = tarefaRepository.findByIdAndUsuario(id, usuario)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada"));

        if (request.titulo() != null)
            tarefa.setTitulo(request.titulo());
        if (request.descricao() != null)
            tarefa.setDescricao(request.descricao());
        if (request.status() != null) {
            try {
                tarefa.setStatus(Status.valueOf(request.status()));
            } catch (IllegalArgumentException e) {
                throw new BusinessException("Status inválido");
            }
        }
        if (request.prioridade() != null) {
            try {
                tarefa.setPrioridade(com.davison.taskmanager.enums.Prioridade.valueOf(request.prioridade()));
            } catch (IllegalArgumentException e) {
                throw new BusinessException("Prioridade inválida");
            }
        }
        if (request.prazo() != null)
            tarefa.setPrazo(request.prazo());

        Tarefa updated = tarefaRepository.save(Objects.requireNonNull(tarefa));
        return toResponse(updated);
    }

    public void remover(Long id, Usuario usuario) {
        Tarefa tarefa = tarefaRepository.findByIdAndUsuario(id, usuario)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada"));
        tarefaRepository.delete(Objects.requireNonNull(tarefa));
    }

    public List<TaskResponse> listarVencidas(Usuario usuario) {
        List<Tarefa> tarefas = tarefaRepository.findTarefasVencidas(usuario, LocalDateTime.now());
        return tarefas.stream().map(this::toResponse).collect(Collectors.toList());
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
                tarefa.getUsuario().getNome());
    }
}