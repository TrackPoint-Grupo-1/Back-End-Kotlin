package com.trackpoint.demo.Controller

import com.trackpoint.demo.DTO.ProjetoCreateRequestDTO
import com.trackpoint.demo.DTO.ProjetoResponseDTO
import com.trackpoint.demo.Service.ProjetoService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/projetos")
class ProjetoController(
    private val projetoService: ProjetoService
) {

    @PostMapping
    fun criarProjeto(@Valid @RequestBody dto: ProjetoCreateRequestDTO): ResponseEntity<ProjetoResponseDTO> {
        val projetoCriado = projetoService.criarProjeto(dto)
        return ResponseEntity.status(HttpStatus.CREATED).body(projetoCriado)
    }

    @GetMapping
    fun listarProjetos(): ResponseEntity<List<ProjetoResponseDTO>> {
        val projetos = projetoService.listarProjetos()
        return ResponseEntity.ok(projetos)
    }

    @GetMapping("/{id}")
    fun buscarProjetoPorId(@PathVariable id: Int): ResponseEntity<ProjetoResponseDTO> {
        val projeto = projetoService.buscarProjetoPorId(id)
        return ResponseEntity.ok(projeto)
    }
}