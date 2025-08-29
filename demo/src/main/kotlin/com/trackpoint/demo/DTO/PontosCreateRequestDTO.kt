package com.trackpoint.demo.DTO

import com.trackpoint.demo.Enum.TipoPonto
import jakarta.validation.constraints.NotNull
import java.sql.Time
import java.time.LocalDate
import java.time.LocalDateTime

data class PontosCreateRequestDTO(
    @field:NotNull(message = "O usuário é obrigatório")
    val usuarioId: Int,

    @field:NotNull(message = "O tipo de ponto é obrigatório")
    val tipo: TipoPonto,

    val localidade: String,

    val horario: LocalDateTime? = null, // se não enviar, usamos LocalDateTime.now()

    val observacoes: String? = null
)