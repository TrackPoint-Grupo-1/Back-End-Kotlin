package com.trackpoint.demo.Controller

import com.trackpoint.demo.DTO.*
import com.trackpoint.demo.Service.PontosService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@RestController
@RequestMapping("/pontos")
class PontosController(private val pontosService: PontosService) {

    @PostMapping
    fun criarPonto(@RequestBody @Valid dto: PontosCreateRequestDTO): ResponseEntity<PontosResponseDTO> {
        val pontoCriado = pontosService.criarPonto(dto)
        return ResponseEntity.status(HttpStatus.CREATED).body(pontoCriado)
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
        val dataFormatada = LocalDate.parse(data, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        val dataInicio = dataFormatada.atStartOfDay()
        val dataFim = dataFormatada.atTime(23, 59, 59)

        val pontos = pontosService.listarPontosPorUsuarioPorData(usuarioId, dataInicio, dataFim)
        return ResponseEntity.ok(pontos)
    }

    @PostMapping("/{usuarioId}/adicionar-pontos-faltantes")
    fun adicionarPontosFaltantes(
        @PathVariable usuarioId: Int,
        @RequestParam data: String,
        @RequestParam entrada: String?,
        @RequestParam almoco: String?,
        @RequestParam voltaAlmoco: String?,
        @RequestParam saida: String?
    ): ResponseEntity<List<PontosResponseDTO>> {

        val pontosAdicionados = pontosService.adicionarPontosFaltantes(
            usuarioId, data, entrada, almoco, voltaAlmoco, saida
        )

        return if (pontosAdicionados.isEmpty()) {
            ResponseEntity.noContent().build() // 204 No Content
        } else {
            ResponseEntity.status(HttpStatus.CREATED).body(pontosAdicionados) // 201 Created
        }
    }

    @GetMapping("/{usuarioId}/faltantes")
    fun listarPontosFaltantesPorUsuario(@PathVariable usuarioId: Int): ResponseEntity<List<PontosFaltantesDTO>> {
        val pontosFaltantes = pontosService.listarPontosFaltantesPorUsuario(usuarioId)
        return if (pontosFaltantes.isEmpty()) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.ok(pontosFaltantes)
        }
    }

    @GetMapping("{usuarioId}/periodo")
    fun listarPontosPorUsuarioEPeriodo(
        @PathVariable usuarioId: Int,
        @RequestParam dataInicio: String,
        @RequestParam dataFim: String
    ): ResponseEntity<TotalHorasDTO> {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val inicio = LocalDate.parse(dataInicio, formatter)
        val fim = LocalDate.parse(dataFim, formatter)

        val pontos = pontosService.listarPontosPorUsuarioEPeriodo(usuarioId, inicio, fim)
        return ResponseEntity.ok(pontos)
    }




}