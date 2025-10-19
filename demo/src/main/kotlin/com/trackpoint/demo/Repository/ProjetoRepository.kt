package com.trackpoint.demo.Repository

import com.trackpoint.demo.Entity.Projeto
import com.trackpoint.demo.Enum.StatusProjeto
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ProjetoRepository : JpaRepository<Projeto, Int> {

    fun existsByNome(nome: String): Boolean

    fun findByNomeContainingIgnoreCase(nome: String): List<Projeto>

    fun findByUsuarios_NomeContainingIgnoreCase(nome: String): List<Projeto>
    fun findByGerentes_NomeContainingIgnoreCase(nome: String): List<Projeto>

    fun findByStatus(status: StatusProjeto): List<Projeto>

    fun findByStatusAndUsuarios_IdOrGerentes_Id(
        status: StatusProjeto,
        usuarioId1: Int,
        usuarioId2: Int
    ): List<Projeto>

    fun findByUsuarios_Id(usuarioId: Int): List<Projeto>

    fun findByGerentes_Id(usuarioId: Int): List<Projeto>

    @Query("SELECT p FROM Projeto p JOIN p.gerentes g WHERE g.id = :gerenteId")
    fun findByGerenteIdInList(@Param("gerenteId") gerenteId: Int): List<Projeto>

}