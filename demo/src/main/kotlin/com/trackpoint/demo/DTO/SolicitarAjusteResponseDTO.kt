package com.trackpoint.demo.DTO

import com.trackpoint.demo.Entity.SolicitarAjuste
import com.trackpoint.demo.Enum.StatusSolicitacao
import java.sql.Time
import java.time.LocalDate
import java.time.LocalDateTime

data class SolicitarAjusteResponseDTO(
    val id: Int,
    val usuarioId: Int,
    val data: LocalDate,
    val horaEntrada: Time?,
    val horaAlmoco: Time?,
    val horaVoltaAlmoco: Time?,
    val horaSaida: Time?,
    val motivo: String,
    val status: StatusSolicitacao,
    val criadoEm: LocalDateTime
) {
    companion object {
        fun fromEntity(solicitacao: SolicitarAjuste) = SolicitarAjusteResponseDTO(
            id = solicitacao.id,
            usuarioId = solicitacao.usuario.id,
            data = solicitacao.data,
            horaEntrada = solicitacao.horaEntrada,
            horaAlmoco = solicitacao.horaAlmoco,
            horaVoltaAlmoco = solicitacao.horaVoltaAlmoco,
            horaSaida = solicitacao.horaSaida,
            motivo = solicitacao.motivo,
            status = solicitacao.status,
            criadoEm = solicitacao.criadoEm
        )
    }
}
