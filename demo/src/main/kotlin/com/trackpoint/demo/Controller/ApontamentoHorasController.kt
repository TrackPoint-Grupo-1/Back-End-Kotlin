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

    @GetMapping("/usuario/{usuarioId}")
    fun listarApontamentosPorUsuarioData(
        @PathVariable usuarioId: Int,
        @RequestParam data: String,
    ): ResponseEntity<List<ApontamentoHorasResponseDTO>> {
        val apontamentos = apontamentoHorasService.listarApontamentosPorUsuarioData(usuarioId, data)
        return ResponseEntity.ok(apontamentos)
    }

    @DeleteMapping("/{id}")
    fun deletarApontamento(@PathVariable id: Long): ResponseEntity<Void> {
        apontamentoHorasService.deletarApontamento(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/gerente/{gerenteId}")
    fun listarApontamentosPorGerenteData(
        @PathVariable gerenteId: Int,
        @RequestParam dataInicio: String,
        @RequestParam dataFim: String
    ): ResponseEntity<List<ApontamentoHorasResponseDTO>> {
        val apontamentos = apontamentoHorasService.listarApontamentosPorGerenteData(gerenteId, dataInicio, dataFim)
        return ResponseEntity.ok(apontamentos)
    }

    @GetMapping("/gerente/{gerenteId}/horas-faltantes")
    fun calcularHorasFaltantesPorGerenteEPeriodo(
        @PathVariable gerenteId: Int,
        @RequestParam dataInicio: String,
        @RequestParam dataFim: String
    ): ResponseEntity<Double> {
        val totalHorasFaltantes = apontamentoHorasService
            .calcularTotalHorasFaltantesPorGerenteEPeriodo(gerenteId, dataInicio, dataFim)
        return ResponseEntity.ok(totalHorasFaltantes)
    }

}