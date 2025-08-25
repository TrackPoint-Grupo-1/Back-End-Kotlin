package com.trackpoint.demo.DTO

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate
import java.time.LocalTime

data class HorasExtrasUpdateRequestDTO(
    val usuarioId: Int?,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    val data: LocalDate?,
    val horasDe: LocalTime?,
    val horasAte: LocalTime?,
    val motivo: String?,
    val foiSolicitado: Boolean?,
    val foiFeita: Boolean?
)
