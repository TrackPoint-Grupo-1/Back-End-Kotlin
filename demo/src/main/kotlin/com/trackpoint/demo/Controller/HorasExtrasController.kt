package com.trackpoint.demo.Controller

import com.trackpoint.demo.DTO.SolicitacaoHorasExtrasCreateRequestDTO
import com.trackpoint.demo.DTO.SolicitacaoHorasExtrasResponseDTO
import com.trackpoint.demo.DTO.SolicitacaoHorasExtrasUpdateRequestDTO
import com.trackpoint.demo.Service.HorasExtrasService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/horas-extras")
class HorasExtrasController (private val horasExtrasService: HorasExtrasService) {

    @PostMapping
    fun criarHorasExtras(@RequestBody @Valid dto: SolicitacaoHorasExtrasCreateRequestDTO): ResponseEntity<SolicitacaoHorasExtrasResponseDTO> {
        val horasExtrasCriada = horasExtrasService.criarHorasExtras(dto)
        val responseDto = SolicitacaoHorasExtrasResponseDTO(horasExtrasCriada)
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto)
    }

    @GetMapping
    fun listarHorasExtras(): ResponseEntity<List<SolicitacaoHorasExtrasResponseDTO>> {
        val horasExtrasList = horasExtrasService.listarTodasHorasExtras()
        val responseList = horasExtrasList.map { SolicitacaoHorasExtrasResponseDTO(it) }
        return ResponseEntity.ok(responseList)
    }

    @GetMapping("/solicitadas")
    fun listarHorasQueFoiSolicitada(): ResponseEntity<List<SolicitacaoHorasExtrasResponseDTO>> {
        val horasExtrasList = horasExtrasService.listarTodasHorasQueForamSolicitada()
        val responseList = horasExtrasList.filter { it.foiSolicitada }.map { SolicitacaoHorasExtrasResponseDTO(it) }
        return ResponseEntity.ok(responseList)
    }

    @GetMapping("/nao-solicitadas")
    fun listarHorasQueNaoFoiSolicitada(): ResponseEntity<List<SolicitacaoHorasExtrasResponseDTO>> {
        val horasExtrasList = horasExtrasService.listarTodasHorasQueNaoForamSolicitada()
        val responseList = horasExtrasList.filter { !it.foiSolicitada }.map { SolicitacaoHorasExtrasResponseDTO(it) }
        return ResponseEntity.ok(responseList)
    }

    @PatchMapping("/{id}")
    fun atualizarHorasExtras(@PathVariable id: Int, @RequestBody dto: SolicitacaoHorasExtrasUpdateRequestDTO
    ): ResponseEntity<SolicitacaoHorasExtrasResponseDTO> {
        val horasExtrasAtualizada = horasExtrasService.atualizarHorasExtras(id, dto)
        return ResponseEntity.ok(SolicitacaoHorasExtrasResponseDTO(horasExtrasAtualizada))
    }

    @DeleteMapping("/{id}")
    fun cancelarHorasExtras(@PathVariable id: Int): ResponseEntity<Void> {
        horasExtrasService.cancelarHorasExtras(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/listar-horas/{usuarioId}")
    fun listarHorasPorUsuarioEntreDatas(
        @PathVariable usuarioId: Int,
        @RequestParam dataInicio: String,
        @RequestParam dataFim: String,
        @RequestParam foiSolicitado: Boolean?
    ): ResponseEntity<List<SolicitacaoHorasExtrasResponseDTO>> {

        val horasExtrasList = horasExtrasService.listarHorasPorUsuarioEntreDatas(usuarioId, dataInicio, dataFim, foiSolicitado)
        return ResponseEntity.ok(horasExtrasList.map { SolicitacaoHorasExtrasResponseDTO(it) })
    }

}