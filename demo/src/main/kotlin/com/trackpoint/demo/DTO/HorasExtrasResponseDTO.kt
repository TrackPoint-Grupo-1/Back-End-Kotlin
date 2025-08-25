package com.trackpoint.demo.DTO

import com.fasterxml.jackson.annotation.JsonFormat
import com.trackpoint.demo.Entity.HorasExtras
import java.sql.Time
import java.time.LocalDate
import java.time.LocalTime

data class HorasExtrasResponseDTO(
    val id: Int,
    val usuarioId: Int,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    val data: LocalDate,
    @JsonFormat(pattern = "HH:mm")
    val horasDe: LocalTime,
    @JsonFormat(pattern = "HH:mm")
    val horasAte: LocalTime,
    val motivo: String,
    val foiSolicitado: Boolean,
    val foiFeita: Boolean,
    val criadoEm: LocalDate
) {
    constructor(horasExtras: HorasExtras) : this(
        id = horasExtras.id,
        usuarioId = horasExtras.usuario.id,
        data = horasExtras.data,
        horasDe = horasExtras.horasDe,
        horasAte = horasExtras.horasAte,
        motivo = horasExtras.motivo,
        foiSolicitado = horasExtras.foiSolicitada,
        foiFeita = horasExtras.foiFeita,
        criadoEm = horasExtras.criadoEm
    )
}
