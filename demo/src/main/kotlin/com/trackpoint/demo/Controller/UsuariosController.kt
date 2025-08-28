package com.trackpoint.demo.Controller

import com.trackpoint.demo.DTO.UsuariosCreateRequestDTO
import com.trackpoint.demo.DTO.UsuariosResponseDTO
import com.trackpoint.demo.DTO.UsuariosUpdateRequestDTO
import com.trackpoint.demo.Service.UsuariosService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/usuarios")
class UsuariosController (private val usuariosService: UsuariosService){


    @PostMapping
    fun createUsuario(@RequestBody @Valid usuarioDTO: UsuariosCreateRequestDTO): ResponseEntity<UsuariosResponseDTO> {
        val usuarioSalvo = usuariosService.salvar(usuarioDTO)
        val usuarioRetorno = UsuariosResponseDTO(usuarioSalvo)
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioRetorno)
    }

    @PutMapping("/{id}")
    fun updateUsuario(
        @PathVariable id: Int,
        @RequestBody usuarioDTO: UsuariosUpdateRequestDTO
    ): ResponseEntity<UsuariosResponseDTO> {
        val usuarioAtualizado = usuariosService.atualizar(id, usuarioDTO)
        return ResponseEntity.ok(UsuariosResponseDTO(usuarioAtualizado))
    }


    @GetMapping("/{id}")
    fun getUsuarioById(@PathVariable id: Int): ResponseEntity<Any> {
        val usuario = usuariosService.findById(id)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("message" to "Usuário com ID $id não encontrado"))

        return ResponseEntity.ok(usuario)
    }

    @GetMapping
    fun getAllUsuarios(): ResponseEntity<Any> {
        return try {
            val usuariosResponse = usuariosService.findAll()
            ResponseEntity.ok(usuariosResponse)
        } catch (ex: NoSuchElementException) {
            ResponseEntity.noContent().build()
        }
    }


    @DeleteMapping("/{id}/soft-delete")
    fun softDeleteUsuario(@PathVariable id: Int): ResponseEntity<Map<String, String>> {
        usuariosService.softDelete(id)
        return ResponseEntity.ok(mapOf("message" to "Usuário com ID $id deletado com sucesso"))
    }


    @PutMapping("/{id}/recuperar-usuario")
    fun recuperarUsuario(@PathVariable id: Int): ResponseEntity<Map<String, String>> {
        usuariosService.recuperar(id)
        return ResponseEntity.ok(mapOf("message" to "Usuário com ID $id recuperado com sucesso"))
    }

    @DeleteMapping("/{id}")
    fun deleteUsuario(@PathVariable id: Int): ResponseEntity<Map<String, String>> {
        usuariosService.softDelete(id)
        return ResponseEntity.ok(mapOf("message" to "Usuário com ID $id deletado com sucesso"))
    }

    @PatchMapping("/{id}/logar")
    fun logarUsuario(@PathVariable id: Int): ResponseEntity<Map<String, String>> {
        val sucesso = usuariosService.tentarLogin(id)

        return if (sucesso) {
            ResponseEntity.ok(mapOf("message" to "Usuário com ID $id logado com sucesso"))
        } else {
            ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(mapOf("message" to "Usuário com ID $id já está logado ou está inativo"))
        }
    }

    @PatchMapping("/{id}/deslogar")
    fun deslogarUsuario(@PathVariable id: Int): ResponseEntity<Map<String, String>> {
        val sucesso = usuariosService.deslogar(id)

        return if (sucesso) {
            ResponseEntity.ok(mapOf("message" to "Usuário com ID $id deslogado com sucesso"))
        } else {
            ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(mapOf("message" to "Usuário com ID $id está inativo ou já está deslogado"))
        }
    }

}