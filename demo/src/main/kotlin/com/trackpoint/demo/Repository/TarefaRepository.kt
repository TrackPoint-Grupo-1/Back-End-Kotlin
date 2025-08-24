package com.trackpoint.demo.Repository

import com.trackpoint.demo.Entity.Tarefa
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TarefaRepository : JpaRepository<Tarefa, Int>