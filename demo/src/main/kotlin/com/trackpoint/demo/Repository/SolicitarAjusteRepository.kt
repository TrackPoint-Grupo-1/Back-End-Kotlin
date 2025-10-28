package com.trackpoint.demo.Repository

import com.trackpoint.demo.Entity.Projeto
import com.trackpoint.demo.Entity.SolicitarAjuste
import com.trackpoint.demo.Enum.StatusSolicitacao
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.time.LocalDateTime

@Repository
interface SolicitarAjusteRepository : JpaRepository<SolicitarAjuste, Int> {

    fun findByUsuarioId(usuarioId: Int): List<SolicitarAjuste>

    fun findByStatus(status: StatusSolicitacao): List<SolicitarAjuste>

    fun findByUsuarioIdAndStatus(usuarioId: Int, status: StatusSolicitacao): List<SolicitarAjuste>

    fun countByUsuarioIdAndCriadoEmBetween(
        usuarioId: Int,
        inicio: LocalDateTime,
        fim: LocalDateTime
    ): Int

    fun findByUsuarioIdAndDataBetween(
        usuarioId: Int,
        dataInicio: LocalDate,
        dataFim: LocalDate
    ): List<SolicitarAjuste>

    @Query("""
    SELECT s 
    FROM SolicitarAjuste s 
    JOIN s.usuario u 
    JOIN Projeto p ON u MEMBER OF p.usuarios 
    WHERE p IN :projetos AND s.status = :status
""")
    fun findByProjetosAndStatus(
        @Param("projetos") projetos: List<Projeto>,
        @Param("status") status: StatusSolicitacao
    ): List<SolicitarAjuste>


}