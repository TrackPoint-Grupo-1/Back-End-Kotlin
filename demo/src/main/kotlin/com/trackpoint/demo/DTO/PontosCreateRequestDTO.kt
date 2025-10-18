package com.trackpoint.demo.DTO

import com.trackpoint.demo.Config.ConversorJPA.LocalidadeCryptoConverter
import com.trackpoint.demo.Enum.TipoPonto
import jakarta.persistence.Convert
import jakarta.validation.constraints.NotNull
import java.sql.Time
import java.time.LocalDate
import java.time.LocalDateTime

data class PontosCreateRequestDTO(
    @field:NotNull(message = "O usuário é obrigatório")
    val usuarioId: Int,

    @field:NotNull(message = "O tipo de ponto é obrigatório")
    val tipo: TipoPonto,

    val localidade: String?,

    val manual: Boolean,

    val horario: LocalDateTime? = null,

    val observacoes: String? = null
)