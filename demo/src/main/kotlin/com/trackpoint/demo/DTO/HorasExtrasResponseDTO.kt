package com.trackpoint.demo.DTO

import com.fasterxml.jackson.annotation.JsonFormat
import com.trackpoint.demo.Entity.HorasExtras
import java.time.LocalDate

data class HorasExtrasResponseDTO(
    val id: Int,
    val usuarioId: Int,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    val data: LocalDate,
    val horas: Double,
    val motivo: String,
    val foiSolicitado: Boolean,
    val foiFeita: Boolean,
    val criadoEm: LocalDate
) {
    constructor(horasExtras: HorasExtras) : this(
        id = horasExtras.id,
        usuarioId = horasExtras.usuario.id,
        data = horasExtras.data,
        horas = horasExtras.horas,
        motivo = horasExtras.motivo,
        foiSolicitado = horasExtras.foiSolicitada,
        foiFeita = horasExtras.foiFeita,
        criadoEm = horasExtras.criadoEm
    )
}
