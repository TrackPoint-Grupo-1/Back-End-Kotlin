package com.trackpoint.demo.Service

import com.trackpoint.demo.DTO.ApontamentoHorasRequestDTO
import com.trackpoint.demo.DTO.ApontamentoHorasResponseDTO
import com.trackpoint.demo.Entity.ApontamentoHoras
import com.trackpoint.demo.Entity.Pontos
import com.trackpoint.demo.Enum.TipoPonto
import com.trackpoint.demo.Exeptions.PontosNaoEncontradosException
import com.trackpoint.demo.Exeptions.RegraDeNegocioException
import com.trackpoint.demo.Exeptions.UsuarioNotFoundException
import com.trackpoint.demo.Repository.ApontamentoHorasRepository
import com.trackpoint.demo.Repository.PontosRepository
import com.trackpoint.demo.Repository.UsuariosRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class ApontamentoHorasService(
    private val repository: ApontamentoHorasRepository,
    private val usuariosRepository: UsuariosRepository,
    private val pontosRepository: PontosRepository
) {

    fun criarApontamento(usuarioId: Int, request: ApontamentoHorasRequestDTO): ApontamentoHorasResponseDTO {
        val usuario = usuariosRepository.findById(usuarioId)
            .orElseThrow { UsuarioNotFoundException("Usuário não encontrado com id: $usuarioId") }

        val data = LocalDate.parse(request.data)
        val startOfDay = data.atStartOfDay()
        val endOfDay = data.atTime(23, 59, 59)

        // 1️⃣ Buscar pontos do dia usando dynamic finder
        val pontosDoDia = pontosRepository.findByUsuarioIdAndHorarioBetween(usuarioId, startOfDay, endOfDay)
        if (pontosDoDia.isEmpty()) {
            throw PontosNaoEncontradosException("Não é possível apontar horas, pois não foram registrados pontos nesta data.")
        }

        // 2️⃣ Calcular total de horas feitas
        val limiteHorasFeita = calcularTotalHoras(pontosDoDia)
        if (limiteHorasFeita <= 0) {
            throw PontosNaoEncontradosException("Não é possível apontar horas, pois não foram registradas horas trabalhadas nesta data.")
        }

        // 3️⃣ Buscar apontamentos já feitos no dia
        val apontamentosExistentes = repository.findByUsuarioIdAndData(usuarioId, data)
        val somaHorasApontadas = apontamentosExistentes.sumOf { it.horas }

        if (somaHorasApontadas + request.horas > limiteHorasFeita) {
            throw RegraDeNegocioException("Não é possível apontar mais horas do que as realizadas ($limiteHorasFeita h).")
        }

        // 4️⃣ Criar apontamento
        val apontamento = ApontamentoHoras(
            usuario = usuario,
            data = data,
            acao = request.acao,
            descricao = request.descricao,
            horas = request.horas,
            horasFeita = limiteHorasFeita
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
}

