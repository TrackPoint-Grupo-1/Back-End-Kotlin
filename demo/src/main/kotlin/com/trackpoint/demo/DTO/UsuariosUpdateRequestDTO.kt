package com.trackpoint.demo.DTO

import com.trackpoint.demo.Enum.CargosEnum

data class UsuariosUpdateRequestDTO(
    val nome: String?,
    val email: String?,
    val senha: String?,
    val cargo: CargosEnum?,
    val ativo: Boolean?
)