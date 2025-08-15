package com.trackpoint.demo.DTO

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class HorasExtrasCreateRequestDTO(
    @field:NotNull(message = "O usuário é obrigatório")
    val usuarioId: Int,
    @field:NotNull(message = "A data é obrigatória")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    val data: LocalDate,
    @field:NotNull(message = "As horas são obrigatórias")
    val horas: Double,
    @field:NotBlank(message = "O motivo não pode estar vazio")
    val motivo: String,
    @field:NotNull(message = "O campo 'foiSolicitado' é obrigatório")
    val foiSolicitado: Boolean
)