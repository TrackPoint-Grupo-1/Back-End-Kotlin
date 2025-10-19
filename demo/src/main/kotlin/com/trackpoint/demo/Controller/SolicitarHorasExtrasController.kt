package com.trackpoint.demo.Controller

import com.trackpoint.demo.DTO.*
import com.trackpoint.demo.Service.SolicitarHorasExtrasService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/horas-extras")
class SolicitarHorasExtrasController (private val solicitarHorasExtrasService: SolicitarHorasExtrasService) {

    @PostMapping
    fun criarHorasExtras(
        @RequestBody @Valid dto: SolicitacaoHorasExtrasCreateRequestDTO
    ): ResponseEntity<SolicitacaoHorasExtrasResponseDTO> {
        val horasExtrasCriada = solicitarHorasExtrasService.criarHorasExtras(dto)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(SolicitacaoHorasExtrasResponseDTO(horasExtrasCriada))
    }


    @GetMapping
    fun listarHorasExtras(): ResponseEntity<List<SolicitacaoHorasExtrasResponseDTO>> {
        val horasExtrasList = solicitarHorasExtrasService.listarTodasHorasExtras()
        val responseList = horasExtrasList.map { SolicitacaoHorasExtrasResponseDTO(it) }
        return ResponseEntity.ok(responseList)
    }

    @GetMapping("/solicitadas")
    fun listarHorasQueFoiSolicitada(): ResponseEntity<List<SolicitacaoHorasExtrasResponseDTO>> {
        val horasExtrasList = solicitarHorasExtrasService.listarTodasHorasQueForamSolicitada()
        val responseList = horasExtrasList.filter { it.foiSolicitada }.map { SolicitacaoHorasExtrasResponseDTO(it) }
        return ResponseEntity.ok(responseList)
    }

    @GetMapping("/nao-solicitadas")
    fun listarHorasQueNaoFoiSolicitada(): ResponseEntity<List<SolicitacaoHorasExtrasResponseDTO>> {
        val horasExtrasList = solicitarHorasExtrasService.listarTodasHorasQueNaoForamSolicitada()
        val responseList = horasExtrasList.filter { !it.foiSolicitada }.map { SolicitacaoHorasExtrasResponseDTO(it) }
        return ResponseEntity.ok(responseList)
    }

    @PatchMapping("/{id}")
    fun atualizarHorasExtras(@PathVariable id: Int, @RequestBody dto: SolicitacaoHorasExtrasUpdateRequestDTO
    ): ResponseEntity<SolicitacaoHorasExtrasResponseDTO> {
        val horasExtrasAtualizada = solicitarHorasExtrasService.atualizarHorasExtras(id, dto)
        return ResponseEntity.ok(SolicitacaoHorasExtrasResponseDTO(horasExtrasAtualizada))
    }

    @DeleteMapping("/{id}")
    fun cancelarHorasExtras(@PathVariable id: Int): ResponseEntity<Void> {
        solicitarHorasExtrasService.cancelarHorasExtras(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/listar-horas/{usuarioId}")
    fun listarHorasPorUsuarioEntreDatas(
        @PathVariable usuarioId: Int,
        @RequestParam dataInicio: String,
        @RequestParam dataFim: String,
        @RequestParam(required = false) foiSolicitado: Boolean?
    ): ResponseEntity<TotalHorasExtraDTO> {

        val resultado = solicitarHorasExtrasService.listarHorasPorUsuarioEntreDatas(usuarioId, dataInicio, dataFim, foiSolicitado)
        return ResponseEntity.ok(resultado)
    }

    @GetMapping("/ranking-geral")
    fun rankingFuncionariosComMaisHorasExtrasNoMes(): ResponseEntity<List<RankingHorasExtrasDTO>> {
        val ranking = solicitarHorasExtrasService.rankingFuncionariosHorasExtrasNoMes()
        return ResponseEntity.ok(ranking)
    }

    @GetMapping("/ranking/projeto/{idProjeto}")
    fun rankingPorProjeto(@PathVariable idProjeto: Int): ResponseEntity<List<RankingHorasExtrasProjetoDTO>> {
        val ranking = solicitarHorasExtrasService.rankingUsuariosPorProjeto(idProjeto)
        return ResponseEntity.ok(ranking)
    }

    @GetMapping("/total-horas-extras/projetos-gerente/{idGerente}")
    fun listarTotalHorasExtrasDeTodosOsProjetoPorGerente(
        @PathVariable idGerente: Int,
        @RequestParam dataInicio: String,
        @RequestParam dataFim: String,
        @RequestParam foiSolicitado: Boolean?
    ): ResponseEntity<TotalHorasExtrasDTO> {

        val totalHorasExtras = solicitarHorasExtrasService
            .listarTotalHorasExtrasDeTodosOsProjetoPorGerente(idGerente, dataInicio, dataFim, foiSolicitado)

        return ResponseEntity.ok(totalHorasExtras)
    }

    @GetMapping("/solicitacoes-pendentes/gerente/{idGerente}")
    fun listarSolicitacoesPendentesPorGerente(
        @PathVariable idGerente: Int
    ): ResponseEntity<List<SolicitacaoHorasExtrasResponseDTO>> {
        val solicitacoesPendentes = solicitarHorasExtrasService.listarSolicitacoesPendentesPorGerente(idGerente)
        val responseList = solicitacoesPendentes.map { SolicitacaoHorasExtrasResponseDTO(it) }
        return ResponseEntity.ok(responseList)
    }

}