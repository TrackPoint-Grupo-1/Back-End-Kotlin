package com.trackpoint.demo.Controller

import com.trackpoint.demo.DTO.TarefaCreateRequestDTO
import com.trackpoint.demo.DTO.TarefaResponseDTO
import com.trackpoint.demo.DTO.TarefaStatusUpdateDTO
import com.trackpoint.demo.Service.TarefaService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

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

    @GetMapping("/{projetoId}/projeto")
    fun listarTarefasProjeto(@PathVariable projetoId: Int): ResponseEntity<List<TarefaResponseDTO>> {
        val tarefas = tarefaService.listarTarefasProjeto(projetoId)
        return ResponseEntity.ok(tarefas)
    }

    @GetMapping("/{id}")
    fun buscarTarefaPorId(@PathVariable id: Int): ResponseEntity<TarefaResponseDTO> {
        val tarefa = tarefaService.buscarTarefaPorId(id)
        return ResponseEntity.ok(tarefa)
    }

    @GetMapping("/usuario/{usuarioId}")
    fun listarTarefasPorUsuario(@PathVariable usuarioId: Int): ResponseEntity<List<TarefaResponseDTO>> {
        val tarefas = tarefaService.listarTarefasPorUsuario(usuarioId)
        return ResponseEntity.ok(tarefas)
    }

    @PutMapping("/atualizar/status/{tarefaId}")
    fun atualizarStatusTarefa(
        @PathVariable tarefaId: Int,
        @RequestBody dto: TarefaStatusUpdateDTO
    ): ResponseEntity<TarefaResponseDTO> {
        val tarefaAtualizada = tarefaService.atualizarStatusTarefa(tarefaId, dto)
        return ResponseEntity.ok(tarefaAtualizada)
    }

    @GetMapping("/projeto/{projetoId}/status")
    fun listarTarefasPorStatus(
        @PathVariable projetoId: Int,
        @RequestParam status: String
    ): ResponseEntity<List<TarefaResponseDTO>> {
        val tarefas = tarefaService.listarTarefasPorStatus(projetoId, status)
        return ResponseEntity.ok(tarefas)
    }

}