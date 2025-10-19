package com.trackpoint.demo.Repository

import com.trackpoint.demo.DTO.RankingHorasExtrasDTO
import com.trackpoint.demo.DTO.RankingHorasExtrasProjetoDTO
import com.trackpoint.demo.Entity.Projeto
import com.trackpoint.demo.Entity.SolicitacaoHorasExtras
import com.trackpoint.demo.Entity.Usuarios
import com.trackpoint.demo.Enum.StatusSolicitacao
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

interface SolicitarHorasExtrasRepository : JpaRepository<SolicitacaoHorasExtras, Int>{
    fun findByFoiSolicitadaTrue(): List<SolicitacaoHorasExtras>
    fun findByFoiSolicitadaFalse(): List<SolicitacaoHorasExtras>
    fun findByUsuarioId(usuarioId: Int): List<SolicitacaoHorasExtras>
    fun findByUsuarioIdAndDataBetween(usuarioId: Int, dataInicio: LocalDate, dataFim: LocalDate): List<SolicitacaoHorasExtras>
    fun findByUsuarioIdAndDataBetweenAndFoiSolicitada(
        usuarioId: Int,
        dataInicio: LocalDate,
        dataFim: LocalDate,
        foiSolicitada: Boolean
    ): List<SolicitacaoHorasExtras>
    fun findByUsuarioIdAndData(usuarioId: Int, data: LocalDate): SolicitacaoHorasExtras?

    @Query(
        """
    SELECT h
    FROM SolicitacaoHorasExtras h
    WHERE h.foiFeita = true
      AND MONTH(h.data) = MONTH(CURRENT_DATE)
      AND YEAR(h.data) = YEAR(CURRENT_DATE)
    """
    )
    fun findHorasExtrasFeitasNoMes(): List<SolicitacaoHorasExtras>

    @Query(
        """
    SELECT u.id AS usuarioId,
           u.nome AS nome,
           SUM(TIMESTAMPDIFF(MINUTE, h.horas_de, h.horas_ate)) / 60.0 AS totalHoras
    FROM solicitacao_horas_extras h
    JOIN usuarios u ON h.usuario_id = u.id
    WHERE h.projeto_id = :projetoId
    GROUP BY u.id, u.nome
    ORDER BY totalHoras DESC
    """,
        nativeQuery = true
    )
    fun buscarRankingPorProjeto(@Param("projetoId") projetoId: Int): List<RankingHorasExtrasProjetoDTO>

    @Query("""
        SELECT s 
        FROM SolicitacaoHorasExtras s
        WHERE s.foiFeita = false 
          AND s.horasDe BETWEEN :inicio AND :fim
    """)
    fun findByFoiFeitaFalse(): List<SolicitacaoHorasExtras>

    fun findByUsuarioAndData(
        usuario: Usuarios,
        data: LocalDate
    ): List<SolicitacaoHorasExtras>

    fun findByUsuarioAndDataAndHorasDeAndHorasAte(
        usuario: Usuarios,
        data: LocalDate,
        horasDe: LocalTime,
        horasAte: LocalTime
    ): List<SolicitacaoHorasExtras>

    fun findByProjetoInAndDataBetween(
        projetos: List<Projeto>,
        dataInicio: LocalDate,
        dataFim: LocalDate
    ): List<SolicitacaoHorasExtras>

    // Buscar todas as solicitações de projetos entre datas, só as feitas
    fun findByProjetoInAndDataBetweenAndFoiFeita(
        projetos: List<Projeto>,
        dataInicio: LocalDate,
        dataFim: LocalDate,
        foiFeita: Boolean = true
    ): List<SolicitacaoHorasExtras>

    // Buscar todas as solicitações de projetos entre datas, filtrando por foiSolicitada e só as feitas
    fun findByProjetoInAndDataBetweenAndFoiSolicitadaAndFoiFeita(
        projetos: List<Projeto>,
        dataInicio: LocalDate,
        dataFim: LocalDate,
        foiSolicitada: Boolean,
        foiFeita: Boolean = true
    ): List<SolicitacaoHorasExtras>

    fun findByProjetoInAndFoiSolicitadaAndFoiFeitaAndFoiAprovada(
        projetos: List<Projeto>,
        foiSolicitada: Boolean,
        foiFeita: Boolean,
        foiAprovada: StatusSolicitacao
    ): List<SolicitacaoHorasExtras>

}