package com.trackpoint.demo.DTO

import java.time.LocalDate

data class HorasExtrasRequestDTO(
    val usuarioId: Int,
    val data: LocalDate,
    val horas: Double,
    val motivo: String,
    val status: Boolean
)