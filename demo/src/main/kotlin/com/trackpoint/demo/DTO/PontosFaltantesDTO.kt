package com.trackpoint.demo.DTO

import com.trackpoint.demo.Enum.TipoPonto
import java.time.LocalDate

data class PontosFaltantesDTO(
    val usuarioId: Int,
    val turno: String,
    val data: LocalDate,
    val tiposFaltantes: List<TipoPonto>
)
