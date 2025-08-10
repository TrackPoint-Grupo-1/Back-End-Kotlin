package com.trackpoint.demo.Controller

import com.trackpoint.demo.DTO.UsuariosDTO
import com.trackpoint.demo.Service.UsuariosService
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
    fun createUsuario(@RequestBody usuarioDTO: UsuariosDTO): ResponseEntity<UsuariosDTO> {
        val usuarioSalvo = usuariosService.salvar(usuarioDTO)
        val usuarioRetorno = UsuariosDTO(usuarioSalvo) // supondo que seu DTO tem construtor que aceita entidade
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioRetorno)
    }

}