package com.trackpoint.demo.Controller

import com.trackpoint.demo.DTO.ApontamentoHorasRequestDTO
import com.trackpoint.demo.DTO.ApontamentoHorasResponseDTO
import com.trackpoint.demo.Service.ApontamentoHorasService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/apontamento-horas")
class ApontamentoHorasController (private val apontamentoHorasService: ApontamentoHorasService){

    @PostMapping("/usuario/{usuarioId}")
    fun criarApontamento(@PathVariable usuarioId: Int, @RequestBody request: ApontamentoHorasRequestDTO):
            ResponseEntity<ApontamentoHorasResponseDTO> {
        val response = apontamentoHorasService.criarApontamento(usuarioId, request)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    fun deletarApontamento(@PathVariable id: Long): ResponseEntity<Void> {
        apontamentoHorasService.deletarApontamento(id)
        return ResponseEntity.noContent().build()
    }
}