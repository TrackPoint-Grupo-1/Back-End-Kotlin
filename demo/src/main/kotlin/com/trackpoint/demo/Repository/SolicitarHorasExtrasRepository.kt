package com.trackpoint.demo.Repository

import com.trackpoint.demo.DTO.RankingHorasExtrasDTO
import com.trackpoint.demo.Entity.SolicitacaoHorasExtras
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDate

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

}