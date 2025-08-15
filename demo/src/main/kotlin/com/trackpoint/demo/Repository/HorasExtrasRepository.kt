package com.trackpoint.demo.Repository

import com.trackpoint.demo.Entity.HorasExtras
import com.trackpoint.demo.Entity.Usuarios
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface HorasExtrasRepository : JpaRepository<HorasExtras, Int>{
    fun findByFoiSolicitadaTrue(): List<HorasExtras>
    fun findByFoiSolicitadaFalse(): List<HorasExtras>
    fun findByUsuarioId(usuarioId: Int): List<HorasExtras>
    fun findByUsuarioIdAndDataBetween(usuarioId: Int, dataInicio: LocalDate, dataFim: LocalDate): List<HorasExtras>
    fun findByUsuarioIdAndDataBetweenAndFoiSolicitada(
        usuarioId: Int,
        dataInicio: LocalDate,
        dataFim: LocalDate,
        foiSolicitada: Boolean
    ): List<HorasExtras>
}