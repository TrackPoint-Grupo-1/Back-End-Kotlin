package com.trackpoint.demo.Controller

import com.trackpoint.demo.DTO.UsuariosCreateRequestDTO
import com.trackpoint.demo.DTO.UsuariosResponseDTO
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
        @RequestBody @Valid usuarioDTO: UsuariosCreateRequestDTO
    ): ResponseEntity<UsuariosResponseDTO> {
        val usuarioAtualizado = usuariosService.atualizar(id, usuarioDTO)

        val usuarioRetorno = UsuariosResponseDTO(usuarioAtualizado)

        return ResponseEntity.ok(usuarioRetorno)
    }

    @GetMapping("/{id}")
    fun getUsuarioById(@PathVariable id: Int): ResponseEntity<Map<String, String>> {
        val usuario = usuariosService.findById(id)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("message" to "Usuário com ID $id não encontrado"))

        return ResponseEntity.ok(mapOf(
            "id" to usuario.id.toString(),
            "nome" to usuario.nome,
            "email" to usuario.email,
            "ativo" to usuario.ativo.toString(),
            "logado" to usuario.logado.toString(),
            "cargo" to usuario.cargo.toString(),
            "criadoEm" to usuario.criadoEm.toString(),
            "horasUltimoLogin" to usuario.horasUltimoLogin.toString()))
    }

    @GetMapping
    fun getAllUsuarios(): ResponseEntity<Any> {
        val usuarios = usuariosService.findAll()
        if (usuarios.isEmpty()) {
            return ResponseEntity.noContent().build() // status 204, sem corpo
            return ResponseEntity.status(HttpStatus.OK).body(mapOf("message" to "Nenhum usuário encontrado"))
        }

        val usuariosResponse = usuarios.map { UsuariosResponseDTO(it) }
        return ResponseEntity.ok(usuariosResponse)
    }

    @PutMapping("/{id}/soft-delete")
    fun softDeleteUsuario(@PathVariable id: Int): ResponseEntity<Map<String, String>> {
        val usuario = usuariosService.findById(id)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("message" to "Usuário com ID $id não encontrado"))

        val usuarioSoftDeleted = usuario.copy(ativo = false)
        usuariosService.save(usuarioSoftDeleted)

        return ResponseEntity.ok(mapOf("message" to "Usuário com ID $id deletado com sucesso"))
    }

    @PutMapping("/{id}/recuperar-usuario")
    fun recuperarUsuario(@PathVariable id: Int): ResponseEntity<Map<String, String>> {
        val usuario = usuariosService.findById(id)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("message" to "Usuário com ID $id não encontrado"))

        val usuarioSoftDeleted = usuario.copy(ativo = true)
        usuariosService.save(usuarioSoftDeleted)

        return ResponseEntity.ok(mapOf("message" to "Usuário com ID $id recuperado com sucesso"))
    }

    @DeleteMapping("/{id}")
    fun deleteUsuario(@PathVariable id: Int): ResponseEntity<Map<String, String>> {
        val usuario = usuariosService.findById(id)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("message" to "Usuário com ID $id não encontrado"))

        usuariosService.save(usuario.copy(ativo = false)) // Soft delete
        return ResponseEntity.ok(mapOf("message" to "Usuário com ID $id deletado com sucesso"))
    }

    @PatchMapping("/{id}/logar")
    fun logarUsuario(@PathVariable id: Int): ResponseEntity<Map<String, String>> {
        val usuario = usuariosService.findById(id)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("message" to "Usuário com ID $id não encontrado"))

        val logado = usuariosService.tentarLogin(usuario)

        return if (logado) {
            ResponseEntity.ok(mapOf("message" to "Usuário com ID $id logado com sucesso"))
        } else {
            ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(mapOf("message" to "Usuário com ID $id já está logado ou está inativo"))
        }
    }

    @PatchMapping("/{id}/deslogar")
    fun deslogarUsuario(@PathVariable id: Int): ResponseEntity<Map<String, String>> {
        val usuario = usuariosService.findById(id)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("message" to "Usuário com ID $id não encontrado"))

        if (!usuario.ativo) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(mapOf("message" to "Usuário com ID $id está inativo"))
        }

        usuario.logado = false
        usuariosService.save(usuario)

        return ResponseEntity.ok(mapOf("message" to "Usuário com ID $id deslogado com sucesso"))
    }

}