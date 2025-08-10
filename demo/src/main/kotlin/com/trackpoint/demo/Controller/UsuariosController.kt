package com.trackpoint.demo.Controller

import com.trackpoint.demo.DTO.UsuariosRequestDTO
import com.trackpoint.demo.DTO.UsuariosResponseDTO
import com.trackpoint.demo.Service.UsuariosService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/usuarios")
class UsuariosController (private val usuariosService: UsuariosService){


    @PostMapping
    fun createUsuario(@RequestBody @Valid usuarioDTO: UsuariosRequestDTO): ResponseEntity<UsuariosResponseDTO> {
        val usuarioSalvo = usuariosService.salvar(usuarioDTO)

        val usuarioRetorno = UsuariosResponseDTO(
            id = usuarioSalvo.id,
            nome = usuarioSalvo.nome,
            email = usuarioSalvo.email,
            cargo = usuarioSalvo.cargo,
            ativo = usuarioSalvo.ativo,
            criadoEm = usuarioSalvo.criadoEm
        )

        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioRetorno)
    }

}