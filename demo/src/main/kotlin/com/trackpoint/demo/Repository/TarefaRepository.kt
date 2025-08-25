package com.trackpoint.demo.Repository

import com.trackpoint.demo.Entity.Tarefa
import com.trackpoint.demo.Enum.StatusTarefa
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TarefaRepository : JpaRepository<Tarefa, Int>{

    fun findByProjetoId(projetoId: Int): List<Tarefa>

    fun findByUsuarioId(usuarioId: Int): List<Tarefa>

    fun findByProjetoIdAndStatus(projetoId: Int, status: StatusTarefa): List<Tarefa>


}