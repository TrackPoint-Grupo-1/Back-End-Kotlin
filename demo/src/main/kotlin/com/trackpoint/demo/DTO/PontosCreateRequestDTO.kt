package com.trackpoint.demo.DTO

import jakarta.validation.constraints.NotNull
import java.sql.Time
import java.time.LocalDate
import java.time.LocalDateTime

data class PontosCreateRequestDTO(
    @field:NotNull(message = "O usuário é obrigatório")
    val usuarioId: Int,
    val horaEntrada: LocalDateTime? = null,
    val observacoes: String? = null
)