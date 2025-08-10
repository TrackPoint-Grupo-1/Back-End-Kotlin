package com.trackpoint.demo.DTO

import com.trackpoint.demo.Enum.CargosEnum
import java.time.LocalDateTime

data class UsuariosResponseDTO(
    val id: Int,
    val nome: String,
    val email: String,
    val cargo: CargosEnum,
    val ativo: Boolean,
    val criadoEm: LocalDateTime
)
