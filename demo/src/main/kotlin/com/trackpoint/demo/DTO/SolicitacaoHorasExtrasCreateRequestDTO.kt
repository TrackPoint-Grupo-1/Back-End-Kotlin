package com.trackpoint.demo.DTO

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate
import java.time.LocalTime

data class SolicitacaoHorasExtrasCreateRequestDTO(
    @field:NotNull(message = "O usuário é obrigatório")
    val usuarioId: Int,
    @field:NotNull(message = "A data é obrigatória")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    val data: LocalDate,
    @field:NotNull(message = "O horário 'de' é obrigatório")
    val horasDe: LocalTime,
    @field:NotNull(message = "O horário 'até' é obrigatório")
    val horasAte: LocalTime,
    @field:NotNull(message = "O código do projeto é obrigatório")
    val codigoProjeto: Int,
    @field:NotBlank(message = "O motivo não pode estar vazio")
    val justificativa: String,
    val observacao: String
)