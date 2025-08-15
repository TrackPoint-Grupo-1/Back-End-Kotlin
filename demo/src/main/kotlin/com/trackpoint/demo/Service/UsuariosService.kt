package com.trackpoint.demo.Service

import com.trackpoint.demo.DTO.UsuariosCreateRequestDTO
import com.trackpoint.demo.DTO.UsuariosUpdateRequestDTO
import com.trackpoint.demo.Entity.Usuarios
import com.trackpoint.demo.Exeptions.EmailJaExisteException
import com.trackpoint.demo.Repository.UsuariosRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime

@Service
class UsuariosService(private val usuariosRepository: UsuariosRepository) {

    fun salvar(usuarioDTO: UsuariosCreateRequestDTO): Usuarios {
        val usuario = Usuarios(
            id = 0,
            nome = usuarioDTO.nome,
            email = usuarioDTO.email,
            senha = usuarioDTO.senha,
            cargo = usuarioDTO.cargo,
            ativo = true,
            criadoEm = LocalDateTime.now()
        )

        if (usuariosRepository.existsByEmail(usuario.email)) {
            throw EmailJaExisteException("O email já está em uso.")
        }

        return usuariosRepository.save(usuario)
    }

    fun atualizar(id: Int, usuarioDTO: UsuariosUpdateRequestDTO): Usuarios {
        val usuarioExistente = usuariosRepository.findById(id)
            .orElseThrow { RuntimeException("Usuário não encontrado com id: $id") }

        // Verifica se email novo já existe
        if (!usuarioDTO.email.isNullOrBlank() &&
            usuarioDTO.email != usuarioExistente.email &&
            usuariosRepository.existsByEmail(usuarioDTO.email)
        ) {
            throw EmailJaExisteException("O email já está em uso.")
        }

        val usuarioAtualizado = usuarioExistente.copy(
            nome = usuarioDTO.nome ?: usuarioExistente.nome,
            email = usuarioDTO.email ?: usuarioExistente.email,
            senha = usuarioDTO.senha ?: usuarioExistente.senha,
            cargo = usuarioDTO.cargo ?: usuarioExistente.cargo,
            ativo = usuarioDTO.ativo ?: usuarioExistente.ativo,
            criadoEm = usuarioExistente.criadoEm
        )

        return usuariosRepository.save(usuarioAtualizado)
    }

    fun findById(id: Int): Usuarios? {
        return usuariosRepository.findById(id).orElse(null)
    }

    fun findAll(): List<Usuarios> {
        return usuariosRepository.findAll()
    }

    fun save(usuario: Usuarios): Usuarios {
        return usuariosRepository.save(usuario)
    }

    @Scheduled(fixedRate = 18_000_000) // 5 horas
    fun deslogarUsuarioDepoisDeDezHoras() {
        val agora = LocalDateTime.now()
        val usuariosLogados = usuariosRepository.findByLogadoTrue()

        usuariosLogados.forEach { usuario ->
            usuario.horasUltimoLogin?.let {
                val horasDesdeLogin = Duration.between(it, agora).toHours()
                if (horasDesdeLogin >= 10) {
                    usuario.logado = false
                    usuariosRepository.save(usuario)
                    println("""
                        LOGGER: Usuário ${usuario.id} deslogado após $horasDesdeLogin horas.
                    """.trimIndent())
                }
            }
        }
    }

    fun tentarLogin(usuario: Usuarios): Boolean {
        if (usuario.ativo && !usuario.logado) {
            usuario.logado = true
            usuario.horasUltimoLogin = LocalDateTime.now()
            usuariosRepository.save(usuario)
            return true
        }
        return false
    }


}