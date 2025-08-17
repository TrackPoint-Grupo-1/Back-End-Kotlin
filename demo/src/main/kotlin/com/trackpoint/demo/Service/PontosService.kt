package com.trackpoint.demo.Service

import com.trackpoint.demo.DTO.PontosCreateRequestDTO
import com.trackpoint.demo.DTO.PontosResponseDTO
import com.trackpoint.demo.DTO.PontosUpdateRequestDTO
import com.trackpoint.demo.Entity.Pontos
import com.trackpoint.demo.Exeptions.*
import com.trackpoint.demo.Repository.PontosRepository
import com.trackpoint.demo.Repository.UsuariosRepository
import org.springframework.stereotype.Service
import java.sql.Time
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Service
class PontosService(
    private val pontosRepository: PontosRepository,
    private val usuariosRepository: UsuariosRepository
) {

    fun registrarPonto(request: PontosCreateRequestDTO): PontosResponseDTO {
        val usuario = usuariosRepository.findById(request.usuarioId)
            .orElseThrow { RuntimeException("Usuário não encontrado com id: ${request.usuarioId}") }

        val ponto = Pontos(
            usuario = usuario,
            horaEntrada = request.horaEntrada,
            observacoes = request.observacoes
        )

        val pontoSalvo = pontosRepository.save(ponto)
        return PontosResponseDTO.fromEntity(pontoSalvo)
    }


    fun atualizarPonto(id: Int, request: PontosUpdateRequestDTO): PontosResponseDTO {
        val pontoExistente = pontosRepository.findById(id)
            .orElseThrow { RuntimeException("Ponto não encontrado com id: $id") }

        val horaAlmocoFinal = request.horaAlmoco ?: pontoExistente.horaAlmoco
        val horaVoltaAlmocoFinal = request.horaVoltaAlmoco ?: pontoExistente.horaVoltaAlmoco
        val horaSaidaFinal = request.horaSaida ?: pontoExistente.horaSaida

        validarPontoUpdate(horaAlmocoFinal, horaVoltaAlmocoFinal, horaSaidaFinal)

        val pontoAtualizado = pontoExistente.copy(
            horaEntrada = request.horaEntrada ?: pontoExistente.horaEntrada,
            horaAlmoco = horaAlmocoFinal,
            horaVoltaAlmoco = horaVoltaAlmocoFinal,
            horaSaida = horaSaidaFinal,
            observacoes = request.observacoes ?: pontoExistente.observacoes
        )

        val salvo = pontosRepository.save(pontoAtualizado)
        return PontosResponseDTO.fromEntity(salvo)
    }

    private fun validarPontoUpdate(
        horaAlmoco: LocalDateTime?,
        horaVoltaAlmoco: LocalDateTime?,
        horaSaida: LocalDateTime?
    ) {
        if (horaVoltaAlmoco != null && horaAlmoco == null) {
            throw PontoInvalidoException("Hora de volta do almoço não pode ser registrada sem a hora de almoço.")
        }

        if (horaSaida != null && horaVoltaAlmoco == null) {
            throw PontoInvalidoException("Hora de saída não pode ser registrada sem a volta do almoço.")
        }

        if (horaAlmoco != null && horaVoltaAlmoco != null) {
            val diferencaEmHoras = java.time.Duration.between(horaAlmoco, horaVoltaAlmoco).toMinutes().toDouble() / 60
            if (diferencaEmHoras < 1) {
                throw PontoInvalidoException("O intervalo de almoço deve ser de pelo menos 1 hora.")
            }
        }
    }

    fun listarPontosPorUsuarioPorData(usuarioId: Int, data: String): List<Pontos> {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

        val localDate = try {
            LocalDate.parse(data, formatter)
        } catch (e: DateTimeParseException) {
            throw InvalidDateFormatException("Formato de data inválido. Use dd-MM-yyyy")
        }

        val usuario = usuariosRepository.findById(usuarioId)
            .orElseThrow { UsuarioNotFoundException("Usuário com ID $usuarioId não encontrado") }

        val inicioDoDia = localDate.atStartOfDay()
        val fimDoDia = localDate.atTime(LocalTime.MAX)

        val pontos = pontosRepository.findByUsuarioIdAndCriadoEmBetween(usuarioId, inicioDoDia, fimDoDia)

        if (pontos.isEmpty()) {
            throw PontosNaoEncontradosException("Nenhum ponto encontrado para o usuário ${usuario.id} na data $localDate")
        }

        return pontos
    }

    fun listarPontosPorUsuarioPorPeriodo(usuarioId: Int, dataInicioStr: String, dataFimStr: String): List<Pontos> {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

        val dataInicio = try {
            LocalDate.parse(dataInicioStr, formatter)
        } catch (e: DateTimeParseException) {
            throw IllegalArgumentException("Formato da data de início inválido. Use dd-MM-yyyy")
        }

        val dataFim = try {
            LocalDate.parse(dataFimStr, formatter)
        } catch (e: DateTimeParseException) {
            throw IllegalArgumentException("Formato da data de fim inválido. Use dd-MM-yyyy")
        }

        if (dataInicio.isAfter(dataFim)) {
            throw InvalidDateFormatException("A data de início não pode ser posterior à data de fim.")
        }

        val usuario = usuariosRepository.findById(usuarioId)
            .orElseThrow { UsuarioNotFoundException("Usuário com ID $usuarioId não encontrado") }

        val inicioDoPeriodo = dataInicio.atStartOfDay()
        val fimDoPeriodo = dataFim.atTime(LocalTime.MAX)

        val pontos = pontosRepository.findByUsuarioIdAndCriadoEmBetween(usuarioId, inicioDoPeriodo, fimDoPeriodo)

        if (pontos.isEmpty()) {
            throw PontosNaoEncontradosException("Nenhum ponto encontrado para o usuário ${usuario.id} no período ${dataInicioStr} a ${dataFimStr}")
        }

        return pontos
    }



}