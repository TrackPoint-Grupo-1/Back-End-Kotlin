package com.trackpoint.demo.DTO

import com.trackpoint.demo.Entity.Pontos
import java.sql.Time
import java.time.LocalDateTime

data class PontosResponseDTO(
    val id: Int,
    val usuarioId: Int,
    val horaEntrada: LocalDateTime?,
    val horaAlmoco: LocalDateTime?,
    val horaVoltaAlmoco: LocalDateTime?,
    val horaSaida: LocalDateTime?,
    val observacoes: String?,
    val criadoEm: LocalDateTime
) {
    companion object {
        fun fromEntity(ponto: Pontos): PontosResponseDTO {
            return PontosResponseDTO(
                id = ponto.id,
                usuarioId = ponto.usuario.id,
                horaEntrada = ponto.horaEntrada,
                horaAlmoco = ponto.horaAlmoco,
                horaVoltaAlmoco = ponto.horaVoltaAlmoco,
                horaSaida = ponto.horaSaida,
                observacoes = ponto.observacoes,
                criadoEm = ponto.criadoEm
            )
        }
    }
}


