package com.trackpoint.demo.Service

import com.trackpoint.demo.DTO.UsuariosRequestDTO
import com.trackpoint.demo.Entity.Usuarios
import com.trackpoint.demo.Exeptions.EmailJaExisteException
import com.trackpoint.demo.Repository.UsuariosRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class UsuariosService(private val usuariosRepository: UsuariosRepository) {

    fun salvar(usuarioDTO: UsuariosRequestDTO): Usuarios {
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

    fun atualizar(id: Int, usuarioDTO: UsuariosRequestDTO): Usuarios {
        val usuarioExistente = usuariosRepository.findById(id)
            .orElseThrow { RuntimeException("Usuário não encontrado com id: $id") }

        if (usuarioDTO.email != usuarioExistente.email &&
            usuariosRepository.existsByEmail(usuarioDTO.email)
        ) {
            throw EmailJaExisteException("O email já está em uso.")
        }

        val usuarioAtualizado = usuarioExistente.copy(
            nome = usuarioDTO.nome,
            email = usuarioDTO.email,
            senha = usuarioDTO.senha,
            cargo = usuarioDTO.cargo,
            ativo = usuarioExistente.ativo,
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


}