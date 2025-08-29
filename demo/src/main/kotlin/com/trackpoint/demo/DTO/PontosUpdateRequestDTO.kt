package com.trackpoint.demo.DTO

import com.trackpoint.demo.Enum.TipoPonto
import java.time.LocalDateTime

data class PontosUpdateRequestDTO(
    val tipo: TipoPonto? = null,
    val horario: LocalDateTime? = null,
    val localidades: List<String>? = null,
    val observacoes: String? = null
)
