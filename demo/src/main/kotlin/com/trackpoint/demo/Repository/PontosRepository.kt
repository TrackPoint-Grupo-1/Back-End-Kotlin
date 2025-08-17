package com.trackpoint.demo.Repository

import com.trackpoint.demo.Entity.Pontos
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface PontosRepository : JpaRepository<Pontos, Int>{
    fun findByUsuarioId(usuarioId: Int): List<Pontos>

    fun findByUsuarioIdAndCriadoEmBetween(
        usuarioId: Int,
        inicio: LocalDateTime,
        fim: LocalDateTime
    ): List<Pontos>
}
