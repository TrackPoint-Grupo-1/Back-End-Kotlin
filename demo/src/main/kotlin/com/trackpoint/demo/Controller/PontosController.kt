package com.trackpoint.demo.Controller

import com.trackpoint.demo.DTO.PontosCreateRequestDTO
import com.trackpoint.demo.DTO.PontosResponseDTO
import com.trackpoint.demo.DTO.PontosUpdateRequestDTO
import com.trackpoint.demo.Service.PontosService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.format.DateTimeParseException

@RestController
@RequestMapping("/pontos")
class PontosController(private val pontosService: PontosService) {

    @PostMapping
    fun criarPonto(@RequestBody @Valid dto: PontosCreateRequestDTO): ResponseEntity<PontosResponseDTO> {
        val pontoCriado = pontosService.criarPonto(dto)
        return ResponseEntity.status(HttpStatus.CREATED).body(pontoCriado)
    }

//    @PatchMapping("/{id}")
//    fun atualizarPonto(
//        @PathVariable id: Int,
//        @RequestBody request: PontosUpdateRequestDTO
//    ): ResponseEntity<PontosResponseDTO> {
//        val pontoAtualizado = pontosService.atualizarPonto(id, request)
//        return ResponseEntity.ok(pontoAtualizado)
//    }
//
//    @GetMapping("/{usuarioId}")
//    fun listarPontosPorUsuarioPorData(
//        @PathVariable usuarioId: Int,
//        @RequestParam("data") data: String
//    ): ResponseEntity<List<PontosResponseDTO>> {
//        val pontos = pontosService.listarPontosPorUsuarioPorData(usuarioId, data)
//        return ResponseEntity.ok(pontos.map { PontosResponseDTO.fromEntity(it) })
//    }
//
//    @GetMapping("/{usuarioId}/periodo")
//    fun listarPontosPorUsuarioPorPeriodo(
//        @PathVariable usuarioId: Int,
//        @RequestParam("dataInicio") dataInicio: String,
//        @RequestParam("dataFim") dataFim: String
//    ): ResponseEntity<List<PontosResponseDTO>> {
//        val pontos = pontosService.listarPontosPorUsuarioPorPeriodo(usuarioId, dataInicio, dataFim)
//        return ResponseEntity.ok(pontos.map { PontosResponseDTO.fromEntity(it) })
//    }


}