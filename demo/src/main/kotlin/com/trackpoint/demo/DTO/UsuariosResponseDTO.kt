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
    var logado: Boolean,
    var horasUltimoLogin: String,
    var jornada: Double,
    var limiteHorasExtrasMes: Double,
    val criadoEm: LocalDateTime,
    val area: String?
) {
    constructor(usuario: Usuarios) : this(
        id = usuario.id,
        nome = usuario.nome,
        email = usuario.email,
        cargo = usuario.cargo,
        ativo = usuario.ativo,
        logado = usuario.logado,
        horasUltimoLogin = usuario.horasUltimoLogin?.toString() ?: "N/A",
        jornada = usuario.jornada,
        limiteHorasExtrasMes = usuario.limiteHorasExtrasMes,
        criadoEm = usuario.criadoEm,
        area = usuario.area
    )

    companion object {
        fun fromEntity(usuario: Usuarios): UsuariosResponseDTO {
            return UsuariosResponseDTO(
                id = usuario.id,
                nome = usuario.nome,
                email = usuario.email,
                cargo = usuario.cargo,
                ativo = usuario.ativo,
                logado = usuario.logado,
                horasUltimoLogin = usuario.horasUltimoLogin.toString(),
                jornada = usuario.jornada,
                limiteHorasExtrasMes = usuario.limiteHorasExtrasMes,
                criadoEm = usuario.criadoEm,
                area = usuario.area
            )
        }
    }
}
