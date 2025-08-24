package com.trackpoint.demo.Controller

import com.trackpoint.demo.DTO.ProjetoCreateRequestDTO
import com.trackpoint.demo.DTO.ProjetoResponseDTO
import com.trackpoint.demo.Entity.Projeto
import com.trackpoint.demo.Enum.StatusProjeto
import com.trackpoint.demo.Exeptions.ProjetoNaoEncontradoException
import com.trackpoint.demo.Exeptions.StatusInvalidoException
import com.trackpoint.demo.Service.ProjetoService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/projetos")
class ProjetoController(
    private val projetoService: ProjetoService
) {

    @PostMapping
    fun criarProjeto(@Valid @RequestBody dto: ProjetoCreateRequestDTO): ResponseEntity<ProjetoResponseDTO> {
        val projetoCriado = projetoService.criarProjeto(dto)
        return ResponseEntity.status(HttpStatus.CREATED).body(projetoCriado)
    }

    @GetMapping
    fun listarProjetos(): ResponseEntity<List<ProjetoResponseDTO>> {
        val projetos = projetoService.listarProjetos()
        return ResponseEntity.ok(projetos)
    }

    @GetMapping("/{id}")
    fun buscarProjetoPorId(@PathVariable id: Int): ResponseEntity<ProjetoResponseDTO> {
        val projeto = projetoService.buscarProjetoPorId(id)
        return ResponseEntity.ok(projeto)
    }

    @GetMapping("/buscar-por-nome")
    fun buscarProjetoPorNome(@RequestParam nome: String): ResponseEntity<List<ProjetoResponseDTO>> {
        val projetos = projetoService.buscarProjetoPorNome(nome)
        return ResponseEntity.ok(projetos)
    }

    @GetMapping("/buscar-por-funcionario")
    fun buscarPorNomeFuncionario(
        @RequestParam nome: String
    ): ResponseEntity<List<Projeto>> {
        val projetos = projetoService.buscarProjetosPorFuncionario(nome)
        return ResponseEntity.ok(projetos)
    }

    @PutMapping("/{id}/atualizar-status")
    fun atualizarStatusProjeto(
        @PathVariable id: Int,
        @RequestParam novoStatus: String
    ): ResponseEntity<ProjetoResponseDTO> {
        val projetoAtualizado = projetoService.atualizarStatusProjeto(id, novoStatus)
        return ResponseEntity.ok(projetoAtualizado)
    }

    @GetMapping("/status")
    fun buscarProjetosPorStatus(@RequestParam status: String): List<ProjetoResponseDTO> {
        return projetoService.buscarProjetosPorStatus(status)
    }

    @PutMapping("/{id}/atualizar-previsao-entrega")
    fun atualizarPrevisaoEntrega(
        @PathVariable id: Int,
        @RequestParam novaPrevisao: String
    ): ResponseEntity<ProjetoResponseDTO> {
        val projetoAtualizado = projetoService.atualizarPrevisaoEntrega(id, novaPrevisao)
        return ResponseEntity.ok(projetoAtualizado)
    }

    //TENHO Q ADICIONAR UM USUARIO A UM PROJETO

}