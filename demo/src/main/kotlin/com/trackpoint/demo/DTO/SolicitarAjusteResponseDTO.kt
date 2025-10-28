package com.trackpoint.demo.DTO

import com.trackpoint.demo.Entity.SolicitarAjuste
import com.trackpoint.demo.Enum.StatusSolicitacao
import io.micrometer.observation.Observation
import java.sql.Time
import java.time.LocalDate
import java.time.LocalDateTime

data class SolicitarAjusteResponseDTO(
    val id: Int,
    val usuarioId: Int,
    val data: LocalDate,
    val justificativa: String,
    val observacao: String? = "",
    val status: StatusSolicitacao,
    val criadoEm: LocalDateTime
) {
    companion object {
        fun fromEntity(solicitacao: SolicitarAjuste) = SolicitarAjusteResponseDTO(
            id = solicitacao.id,
            usuarioId = solicitacao.usuario.id,
            data = solicitacao.data,
            justificativa = solicitacao.justificativa,
            observacao = solicitacao.observacao,
            status = solicitacao.status,
            criadoEm = solicitacao.criadoEm
        )
    }
}
