package com.trackpoint.demo.Repository

import com.trackpoint.demo.Entity.ApontamentoHoras
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface ApontamentoHorasRepository : JpaRepository<ApontamentoHoras, Long> {
    fun findByUsuarioIdAndData(usuarioId: Int, data: LocalDate): List<ApontamentoHoras>
}