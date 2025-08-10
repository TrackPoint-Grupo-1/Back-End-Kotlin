package com.trackpoint.demo.DTO

import com.trackpoint.demo.Entity.Usuarios
import com.trackpoint.demo.Enum.CargosEnum
import java.time.LocalDateTime

data class UsuariosResponseDTO(
    val id: Int,
    val nome: String,
    val email: String,
    val cargo: CargosEnum,
    val ativo: Boolean,
    val criadoEm: LocalDateTime
) {
    constructor(usuario: Usuarios) : this(
        id = usuario.id,
        nome = usuario.nome,
        email = usuario.email,
        cargo = usuario.cargo,
        ativo = usuario.ativo,
        criadoEm = usuario.criadoEm
    )
}
