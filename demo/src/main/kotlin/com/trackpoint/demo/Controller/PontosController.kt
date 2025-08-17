package com.trackpoint.demo.Controller

import com.trackpoint.demo.DTO.PontosCreateRequestDTO
import com.trackpoint.demo.DTO.PontosResponseDTO
import com.trackpoint.demo.DTO.PontosUpdateRequestDTO
import com.trackpoint.demo.Service.PontosService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.format.DateTimeParseException

@RestController
@RequestMapping("/pontos")
class PontosController(private val pontosService: PontosService) {

    @PostMapping
    fun registrarPonto(
        @RequestBody @Valid request: PontosCreateRequestDTO
    ): ResponseEntity<PontosResponseDTO> {
        val response = pontosService.registrarPonto(request)
        return ResponseEntity.ok(response)
    }

    @PatchMapping("/{id}")
    fun atualizarPonto(
        @PathVariable id: Int,
        @RequestBody request: PontosUpdateRequestDTO
    ): ResponseEntity<PontosResponseDTO> {
        val pontoAtualizado = pontosService.atualizarPonto(id, request)
        return ResponseEntity.ok(pontoAtualizado)
    }

    @GetMapping("/{usuarioId}")
    fun listarPontosPorUsuarioPorData(
        @PathVariable usuarioId: Int,
        @RequestParam("data") data: String
    ): ResponseEntity<List<PontosResponseDTO>> {
        val pontos = pontosService.listarPontosPorUsuarioPorData(usuarioId, data)
        return ResponseEntity.ok(pontos.map { PontosResponseDTO.fromEntity(it) })
    }

    @GetMapping("/{usuarioId}/periodo")
    fun listarPontosPorUsuarioPorPeriodo(
        @PathVariable usuarioId: Int,
        @RequestParam("dataInicio") dataInicio: String,
        @RequestParam("dataFim") dataFim: String
    ): ResponseEntity<List<PontosResponseDTO>> {
        val pontos = pontosService.listarPontosPorUsuarioPorPeriodo(usuarioId, dataInicio, dataFim)
        return ResponseEntity.ok(pontos.map { PontosResponseDTO.fromEntity(it) })
    }


}