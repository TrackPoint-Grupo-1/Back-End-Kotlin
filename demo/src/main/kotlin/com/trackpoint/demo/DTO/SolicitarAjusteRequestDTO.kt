package com.trackpoint.demo.DTO

import jakarta.validation.constraints.NotBlank
import org.jetbrains.annotations.NotNull
import java.sql.Time
import java.time.LocalDate

class SolicitarAjusteRequestDTO(
    @field:NotNull
    val data: LocalDate,
    val horaEntrada: Time? = null,
    val horaAlmoco: Time? = null,
    val horaVoltaAlmoco: Time? = null,
    val horaSaida: Time? = null,
    @field:NotBlank(message = "O motivo é obrigatório")
    val motivo: String
)
