package com.trackpoint.demo.Repository

import com.trackpoint.demo.Entity.Projeto
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjetoRepository : JpaRepository<Projeto, Int> {

    fun existsByNome(nome: String): Boolean

    fun findByNomeContainingIgnoreCase(nome: String): List<Projeto>

    fun findByUsuarios_NomeContainingIgnoreCase(nome: String): List<Projeto>
    fun findByGerentes_NomeContainingIgnoreCase(nome: String): List<Projeto>

}