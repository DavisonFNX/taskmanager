package com.davison.taskmanager.repository;

import com.davison.taskmanager.entity.Tarefa;
import com.davison.taskmanager.entity.Usuario;
import com.davison.taskmanager.enums.Status;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TarefaRepository extends JpaRepository<Tarefa, Long> {

    List<Tarefa> findByUsuario(Usuario usuario, Sort sort);

    Optional<Tarefa> findByIdAndUsuario(Long id, Usuario usuario);

    List<Tarefa> findByUsuarioAndStatus(Usuario usuario, Status status, Sort sort);

    @Query("SELECT t FROM Tarefa t WHERE t.usuario = :usuario AND t.prazo < :hoje AND t.status <> 'CONCLUIDA'")
    List<Tarefa> findTarefasVencidas(@Param("usuario") Usuario usuario, @Param("hoje") LocalDateTime hoje);
}