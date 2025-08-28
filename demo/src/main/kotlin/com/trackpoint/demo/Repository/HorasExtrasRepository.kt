package com.trackpoint.demo.Repository

import com.trackpoint.demo.Entity.SolicitacaoHorasExtras
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface HorasExtrasRepository : JpaRepository<SolicitacaoHorasExtras, Int>{
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
}