package com.trackpoint.demo.DTO

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class ProjetoCreateRequestDTO(
    @field:NotBlank(message = "O nome do projeto é obrigatório")
    val nome: String,
    @field:NotBlank(message = "A descrição é obrigatória")
    val descricao: String,
    @field:NotNull(message = "Pelo menos um gerente deve ser informado")
    val gerentesIds: List<Int>,
    val usuariosIds: List<Int> = emptyList(),
    @field:NotNull(message = "A previsão de entrega é obrigatória")
    @field:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    val previsaoEntrega: LocalDate,
)
