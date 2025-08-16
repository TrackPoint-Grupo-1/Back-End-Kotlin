package com.trackpoint.demo.Service

import com.trackpoint.demo.DTO.PontosCreateRequestDTO
import com.trackpoint.demo.DTO.PontosResponseDTO
import com.trackpoint.demo.DTO.PontosUpdateRequestDTO
import com.trackpoint.demo.Entity.Pontos
import com.trackpoint.demo.Exeptions.DiferencaAlmocoInvalidaException
import com.trackpoint.demo.Exeptions.PontoInvalidoException
import com.trackpoint.demo.Repository.PontosRepository
import com.trackpoint.demo.Repository.UsuariosRepository
import org.springframework.stereotype.Service
import java.sql.Time
import java.time.LocalDateTime

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

}