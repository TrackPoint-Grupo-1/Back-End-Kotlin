package com.trackpoint.demo.DTO

import com.trackpoint.demo.Entity.Pontos
import com.trackpoint.demo.Enum.TipoPonto
import java.sql.Time
import java.time.LocalDateTime

data class PontosResponseDTO(
    val id: Int,
    val usuarioId: Int,
    val tipo: TipoPonto,
    val horario: LocalDateTime,
    val localidade: String?,
    val manual: Boolean,
    val observacoes: String?,
    val turno: String
) {
    companion object {
        fun fromEntity(ponto: Pontos): PontosResponseDTO {
            return PontosResponseDTO(
                id = ponto.id,
                usuarioId = ponto.usuario.id,
                tipo = ponto.tipo,
                horario = ponto.horario,
                localidade = ponto.localidade ?: "Não Informado",
                observacoes = ponto.observacoes ?: "Não Informado",
                manual = ponto.manual,
                turno = ponto.turno
            )
        }
    }
}



