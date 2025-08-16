package com.trackpoint.demo.Controller

import com.trackpoint.demo.DTO.SolicitarAjusteRequestDTO
import com.trackpoint.demo.DTO.SolicitarAjusteResponseDTO
import com.trackpoint.demo.DTO.SolicitarAjusteStatusUpdateDTO
import com.trackpoint.demo.Enum.StatusSolicitacao
import com.trackpoint.demo.Service.SolicitarAjusteService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/solicitacoes")
class SolicitarAjusteController(
    private val service: SolicitarAjusteService
) {

    @PostMapping
    fun criarSolicitacao(
        @RequestBody @Valid request: SolicitarAjusteRequestDTO,
        @RequestParam usuarioId: Int
    ): ResponseEntity<SolicitarAjusteResponseDTO> {
        val response = service.criarSolicitacao(request, usuarioId)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/{usuarioId}")
    fun listarSolicitacoesUsuario(@PathVariable usuarioId: Int): ResponseEntity<List<SolicitarAjusteResponseDTO>> {
        val response = service.listarSolicitacoesPorUsuario(usuarioId)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/pendentes")
    fun listarPendentes(): ResponseEntity<List<SolicitarAjusteResponseDTO>> {
        val response = service.listarSolicitacoesPendentes()
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{usuarioId}/{status}")
    fun listarSolicitacoesPorUsuarioEStatus(
        @PathVariable usuarioId: Int,
        @PathVariable status: String
    ): ResponseEntity<List<SolicitarAjusteResponseDTO>> {
        val response = service.listarSolicitacoesPorUsuarioEStatus(usuarioId, status)
        return ResponseEntity.ok(response)
    }

    @PatchMapping("/{id}/status")
    fun atualizarStatus(
        @PathVariable id: Int,
        @RequestBody @Valid statusDTO: SolicitarAjusteStatusUpdateDTO
    ): ResponseEntity<SolicitarAjusteResponseDTO> {
        val response = service.atualizarStatus(id, statusDTO)
        return ResponseEntity.ok(response)
    }
}
