package com.trackpoint.demo.DTO

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate
import java.time.LocalTime

data class SolicitacaoHorasExtrasUpdateRequestDTO(
    val usuarioId: Int?,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    val data: LocalDate?,
    val horasDe: LocalTime?,
    val horasAte: LocalTime?,
    val codigoProjeto: Int?,
    val justificativa: String?,
    val observacao: String?,
    var foiSolicitada: Boolean?,
    var foiFeita: Boolean?,
    var foiAprovada: Boolean?
)
