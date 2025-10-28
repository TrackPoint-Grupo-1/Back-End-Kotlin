package com.trackpoint.demo.DTO

import jakarta.validation.constraints.NotBlank
import org.jetbrains.annotations.NotNull
import java.sql.Time
import java.time.LocalDate

class SolicitarAjusteRequestDTO(
    @field:NotNull
    val data: LocalDate,
    @field:NotBlank(message = "A justificativa é obrigatório")
    val justificativa: String,
    val observacao: String? = null
)
