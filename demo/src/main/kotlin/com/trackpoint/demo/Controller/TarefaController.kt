package com.trackpoint.demo.Controller

import com.trackpoint.demo.DTO.TarefaCreateRequestDTO
import com.trackpoint.demo.DTO.TarefaResponseDTO
import com.trackpoint.demo.Service.TarefaService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/tarefas")
class TarefaController(
    private val tarefaService: TarefaService
) {

    @PostMapping
    fun criarTarefa(@RequestBody @Valid dto: TarefaCreateRequestDTO): ResponseEntity<TarefaResponseDTO> {
        val tarefaCriada = tarefaService.criarTarefa(dto)
        return ResponseEntity.status(HttpStatus.CREATED).body(tarefaCriada)
    }
}