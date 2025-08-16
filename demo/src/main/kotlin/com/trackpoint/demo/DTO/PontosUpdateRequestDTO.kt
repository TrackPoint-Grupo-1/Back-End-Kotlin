package com.trackpoint.demo.DTO

import java.time.LocalDateTime

data class PontosUpdateRequestDTO(
    val horaEntrada: LocalDateTime? = null,
    val horaAlmoco: LocalDateTime? = null,
    val horaVoltaAlmoco: LocalDateTime? = null,
    val horaSaida: LocalDateTime? = null,
    val observacoes: String? = null
)