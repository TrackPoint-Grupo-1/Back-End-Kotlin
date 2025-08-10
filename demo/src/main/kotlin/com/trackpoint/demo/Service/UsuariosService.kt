package com.trackpoint.demo.Service

import com.trackpoint.demo.DTO.UsuariosDTO
import com.trackpoint.demo.Entity.Usuarios
import com.trackpoint.demo.Exeptions.EmailJaExisteException
import com.trackpoint.demo.Repository.UsuariosRepository
import org.springframework.stereotype.Service

@Service
class UsuariosService(private val usuariosRepository: UsuariosRepository) {

    fun salvar(usuarioDTO: UsuariosDTO): Usuarios {
        val usuario = Usuarios(
            id = 0,
            nome = usuarioDTO.usuario.nome,
            email = usuarioDTO.usuario.email,
            senha = usuarioDTO.usuario.senha,
            cargo = usuarioDTO.usuario.cargo,
            ativo = usuarioDTO.usuario.ativo,
            criadoEm = usuarioDTO.usuario.criadoEm
        )

        if (usuariosRepository.existsByEmail(usuario.email)) {
            throw EmailJaExisteException("O email já está em uso.")
        }

        return usuariosRepository.save(usuario)
    }





}