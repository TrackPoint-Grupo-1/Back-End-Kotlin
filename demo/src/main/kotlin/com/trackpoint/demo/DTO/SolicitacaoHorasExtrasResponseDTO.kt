package com.trackpoint.demo.DTO

import com.fasterxml.jackson.annotation.JsonFormat
import com.trackpoint.demo.Entity.SolicitacaoHorasExtras
import com.trackpoint.demo.Enum.StatusSolicitacao
import java.time.LocalDate
import java.time.LocalTime

data class SolicitacaoHorasExtrasResponseDTO(
    val id: Int,
    val usuarioId: Int,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    val data: LocalDate,
    @JsonFormat(pattern = "HH:mm")
    val horasDe: LocalTime,
    @JsonFormat(pattern = "HH:mm")
    val horasAte: LocalTime,
    val codigoProjeto: Int?,
    val justificativa: String,
    val observacao: String,
    val foiSolicitado: Boolean,
    val foiFeita: Boolean,
    var foiAprovada: StatusSolicitacao,
    val criadoEm: LocalDate
) {
    constructor(solicitacaoHorasExtras: SolicitacaoHorasExtras) : this(
        id = solicitacaoHorasExtras.id,
        usuarioId = solicitacaoHorasExtras.usuario.id,
        data = solicitacaoHorasExtras.data,
        horasDe = solicitacaoHorasExtras.horasDe,
        horasAte = solicitacaoHorasExtras.horasAte,
        codigoProjeto = solicitacaoHorasExtras.projeto?.id,
        justificativa = solicitacaoHorasExtras.justificativa,
        observacao = solicitacaoHorasExtras.observacao,
        foiSolicitado = solicitacaoHorasExtras.foiSolicitada,
        foiFeita = solicitacaoHorasExtras.foiFeita,
        foiAprovada = solicitacaoHorasExtras.foiAprovada ?: StatusSolicitacao.PENDENTE,
        criadoEm = solicitacaoHorasExtras.criadoEm
    )
}
