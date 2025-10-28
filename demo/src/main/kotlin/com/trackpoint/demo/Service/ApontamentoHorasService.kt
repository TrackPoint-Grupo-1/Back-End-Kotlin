package com.trackpoint.demo.Service

import com.trackpoint.demo.DTO.ApontamentoHorasRequestDTO
import com.trackpoint.demo.DTO.ApontamentoHorasResponseDTO
import com.trackpoint.demo.Entity.ApontamentoHoras
import com.trackpoint.demo.Entity.Pontos
import com.trackpoint.demo.Enum.TipoPonto
import com.trackpoint.demo.Exeptions.PontosNaoEncontradosException
import com.trackpoint.demo.Exeptions.ProjetoNaoEncontradoException
import com.trackpoint.demo.Exeptions.RegraDeNegocioException
import com.trackpoint.demo.Exeptions.UsuarioNotFoundException
import com.trackpoint.demo.Repository.ApontamentoHorasRepository
import com.trackpoint.demo.Repository.PontosRepository
import com.trackpoint.demo.Repository.ProjetoRepository
import com.trackpoint.demo.Repository.UsuariosRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class ApontamentoHorasService(
    private val repository: ApontamentoHorasRepository,
    private val usuariosRepository: UsuariosRepository,
    private val pontosRepository: PontosRepository,
    private val projetoRepository: ProjetoRepository
) {

    fun criarApontamento(usuarioId: Int, request: ApontamentoHorasRequestDTO): ApontamentoHorasResponseDTO {
        val usuario = usuariosRepository.findById(usuarioId)
            .orElseThrow { UsuarioNotFoundException("Usuário não encontrado com id: $usuarioId") }

        val data = LocalDate.parse(request.data)
        val startOfDay = data.atStartOfDay()
        val endOfDay = data.atTime(23, 59, 59)

        val pontosDoDia = pontosRepository.findByUsuarioIdAndHorarioBetween(usuarioId, startOfDay, endOfDay)
        if (pontosDoDia.isEmpty()) {
            throw PontosNaoEncontradosException("Não é possível apontar horas, pois não foram registrados pontos nesta data.")
        }

        val limiteHorasFeita = calcularTotalHoras(pontosDoDia)
        if (limiteHorasFeita <= 0) {
            throw PontosNaoEncontradosException("Não é possível apontar horas, pois não foram registradas horas trabalhadas nesta data.")
        }

        val apontamentosExistentes = repository.findByUsuarioIdAndData(usuarioId, data)
        val somaHorasApontadas = apontamentosExistentes.sumOf { it.horas }

        if (somaHorasApontadas + request.horas > limiteHorasFeita) {
            throw RegraDeNegocioException("Não é possível apontar mais horas do que as realizadas ($limiteHorasFeita h).")
        }

        // ✅ Busca o projeto diretamente do repositório (evita null)
        val projeto = request.projetoId?.let { projetoId ->
            projetoRepository.findById(projetoId)
                .orElseThrow { RegraDeNegocioException("Projeto com id $projetoId não encontrado.") }
        }

        val apontamento = ApontamentoHoras(
            usuario = usuario,
            data = data,
            acao = request.acao,
            descricao = request.descricao,
            horas = request.horas,
            horasFeita = limiteHorasFeita,
            projeto = projeto
        )

        val salvo = repository.save(apontamento)
        return ApontamentoHorasResponseDTO.fromEntity(salvo)!!
    }


    fun listarApontamentos(usuarioId: Int, data: String): List<ApontamentoHorasResponseDTO> {
        val dataConvertida = LocalDate.parse(data)
        return repository.findByUsuarioIdAndData(usuarioId, dataConvertida)
            .mapNotNull { ApontamentoHorasResponseDTO.fromEntity(it) }
    }

    fun deletarApontamento(id: Long) {
        if (!repository.existsById(id)) throw RegraDeNegocioException("Apontamento não encontrado com id: $id")
        repository.deleteById(id)
    }

    // Função para calcular total de horas do dia
    private fun calcularTotalHoras(pontosDoDia: List<Pontos>): Double {
        var totalMinutos = 0L
        var entrada: LocalDateTime? = null
        var inicioAlmoco: LocalDateTime? = null

        for (ponto in pontosDoDia.sortedBy { it.horario }) {
            when (ponto.tipo) {
                TipoPonto.ENTRADA -> entrada = ponto.horario
                TipoPonto.ALMOCO -> inicioAlmoco = ponto.horario
                TipoPonto.VOLTA_ALMOCO -> {
                    if (inicioAlmoco != null && entrada != null) {
                        totalMinutos += java.time.Duration.between(entrada, inicioAlmoco).toMinutes()
                        entrada = ponto.horario
                        inicioAlmoco = null
                    }
                }
                TipoPonto.SAIDA -> {
                    if (entrada != null) {
                        totalMinutos += java.time.Duration.between(entrada, ponto.horario).toMinutes()
                        entrada = null
                    }
                }
            }
        }

        return totalMinutos / 60.0
    }

    fun listarApontamentosPorUsuarioData(usuarioId: Int, data: String): List<ApontamentoHorasResponseDTO> {
        val dataConvertida = LocalDate.parse(data, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        val apontamentos = repository.findByUsuarioIdAndData(usuarioId, dataConvertida)
            .mapNotNull { ApontamentoHorasResponseDTO.fromEntity(it) }

        if (apontamentos.isEmpty()) {
            throw PontosNaoEncontradosException("Nenhum apontamento encontrado para o usuário com id: $usuarioId na data: $data")
        }

        return apontamentos
    }

    fun listarApontamentosPorGerenteData(gerenteId: Int, dataInicio: String, dataFim: String): List<ApontamentoHorasResponseDTO> {
        // Converter as datas recebidas em LocalDate
        val inicio = LocalDate.parse(dataInicio, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        val fim = LocalDate.parse(dataFim, DateTimeFormatter.ofPattern("dd/MM/yyyy"))

        // 1️⃣ Buscar todos os projetos que pertencem ao gerente
        val projetos = projetoRepository.findByGerenteIdInList(gerenteId)
        if (projetos.isEmpty()) {
            throw ProjetoNaoEncontradoException("Nenhum projeto encontrado para o gerente com id: $gerenteId")
        }

        // 2️⃣ Buscar todos os apontamentos de horas nesses projetos dentro do intervalo de datas
        val apontamentos = repository.findByProjetoInAndDataBetween(projetos, inicio, fim)
        if (apontamentos.isEmpty()) {
            throw PontosNaoEncontradosException("Nenhum apontamento encontrado para o gerente com id: $gerenteId entre $inicio e $fim")
        }

        // 3️⃣ Converter para DTO e retornar
        return apontamentos.map { ApontamentoHorasResponseDTO.fromEntity(it)!! }
    }

}

