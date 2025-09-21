package com.trackpoint.demo.Service

import com.trackpoint.demo.DTO.UsuariosCreateRequestDTO
import com.trackpoint.demo.DTO.UsuariosResponseDTO
import com.trackpoint.demo.DTO.UsuariosUpdateRequestDTO
import com.trackpoint.demo.Entity.Usuarios
import com.trackpoint.demo.Exeptions.EmailJaExisteException
import com.trackpoint.demo.Exeptions.UsuarioNotFoundException
import com.trackpoint.demo.Repository.UsuariosRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

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
            criadoEm = LocalDateTime.now(),
            jornada = usuarioDTO.jornada,
            limiteHorasExtrasMes = usuarioDTO.limiteHorasExtrasMes,
            area = usuarioDTO.area
        )

        if (usuariosRepository.existsByEmail(usuario.email)) {
            throw EmailJaExisteException("O email já está em uso.")
        }

        return usuariosRepository.save(usuario)
    }

    fun atualizar(id: Int, usuarioDTO: UsuariosUpdateRequestDTO): Usuarios {
        val usuarioExistente = usuariosRepository.findById(id)
            .orElseThrow { RuntimeException("Usuário não encontrado com id: $id") }

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
            jornada = usuarioDTO.jornada ?: usuarioExistente.jornada,
            limiteHorasExtrasMes = usuarioDTO.limiteHorasExtrasMes ?: usuarioExistente.limiteHorasExtrasMes,
            criadoEm = usuarioExistente.criadoEm,
            area = usuarioDTO.area ?: usuarioExistente.area
        )

        return usuariosRepository.save(usuarioAtualizado)
    }

    fun findById(id: Int): UsuariosResponseDTO? {
        val usuario = usuariosRepository.findById(id).orElse(null)
        return usuario?.let { UsuariosResponseDTO(it) }
    }

    fun findAll(): List<UsuariosResponseDTO> {
        val usuarios = usuariosRepository.findAll()

        if (usuarios.isEmpty()) {
            throw UsuarioNotFoundException("Nenhum usuário encontrado")
        }

        return usuarios.map { UsuariosResponseDTO(it) }
    }

    fun save(usuario: Usuarios): Usuarios {
        return usuariosRepository.save(usuario)
    }

    fun tentarLogin(id: Int): Boolean {
        val usuario = usuariosRepository.findById(id)
            .orElseThrow { UsuarioNotFoundException("Usuário com ID $id não encontrado") }

        if (usuario.ativo && !usuario.logado) {
            val atualizado = usuario.copy(
                logado = true,
                horasUltimoLogin = LocalDateTime.now()
            )
            usuariosRepository.save(atualizado)
            return true
        }

        return false
    }

    fun softDelete(id: Int) {
        val usuario = usuariosRepository.findById(id)
                .orElseThrow { UsuarioNotFoundException("Usuário com ID $id não encontrado") }

        val usuarioSoftDeleted = usuario.copy(ativo = false)
        usuariosRepository.save(usuarioSoftDeleted)
    }

    fun recuperar(id: Int): Usuarios {
        val usuario = usuariosRepository.findById(id)
            .orElseThrow { UsuarioNotFoundException("Usuário com ID $id não encontrado") }

        val usuarioRecuperado = usuario.copy(ativo = true)
        return usuariosRepository.save(usuarioRecuperado)
    }

    fun deslogar(id: Int): Boolean {
        val usuario = usuariosRepository.findById(id)
            .orElseThrow { UsuarioNotFoundException("Usuário com ID $id não encontrado") }

        if (!usuario.ativo || !usuario.logado) {
            return false
        }

        val usuarioAtualizado = usuario.copy(logado = false)
        usuariosRepository.save(usuarioAtualizado)

        return true
    }

    fun listarUsuariosPreLogin(email: String, senha: String): UsuariosResponseDTO {
        val usuario = usuariosRepository.findByEmailAndSenha(email, senha)
            ?: throw UsuarioNotFoundException("Nenhum usuário encontrado com o email e senha fornecidos")

        return UsuariosResponseDTO(usuario)
    }


}