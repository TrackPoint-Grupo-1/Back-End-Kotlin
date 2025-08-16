package com.trackpoint.demo.Controller

import com.trackpoint.demo.DTO.PontosCreateRequestDTO
import com.trackpoint.demo.DTO.PontosResponseDTO
import com.trackpoint.demo.DTO.PontosUpdateRequestDTO
import com.trackpoint.demo.Service.PontosService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

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


}