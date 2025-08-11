package com.trackpoint.demo.DTO

import com.trackpoint.demo.Entity.HorasExtras
import java.time.LocalDate

data class HorasExtrasResponseDTO(
    val id: Int,
    val usuarioId: Int,
    val data: LocalDate,
    val horas: Double,
    val motivo: String,
    val status: Boolean,
    val criadoEm: LocalDate
) {
    constructor(horasExtras: HorasExtras) : this(
        id = horasExtras.id,
        usuarioId = horasExtras.usuario.id,
        data = horasExtras.data,
        horas = horasExtras.horas,
        motivo = horasExtras.motivo,
        status = horasExtras.status,
        criadoEm = horasExtras.criadoEm
    )
}