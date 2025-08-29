package com.trackpoint.demo.Service

import com.trackpoint.demo.DTO.PontosCreateRequestDTO
import com.trackpoint.demo.DTO.PontosResponseDTO
import com.trackpoint.demo.Entity.Pontos
import com.trackpoint.demo.Enum.TipoPonto
import com.trackpoint.demo.Exeptions.*
import com.trackpoint.demo.Repository.PontosRepository
import com.trackpoint.demo.Repository.UsuariosRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class PontosService(
    private val pontosRepository: PontosRepository,
    private val usuariosRepository: UsuariosRepository
) {

    fun criarPonto(dto: PontosCreateRequestDTO): PontosResponseDTO {
        val usuario = usuariosRepository.findById(dto.usuarioId)
            .orElseThrow { UsuarioNotFoundException("Usuário não encontrado com id: ${dto.usuarioId}") }

        val horario = dto.horario ?: LocalDateTime.now()

        if (dto.tipo == TipoPonto.ENTRADA) {
            val turnoAberto = pontosRepository.findLastOpenTurn(usuario)
            if (turnoAberto != null) {
                throw RegraDeNegocioException(
                    "Não é possível iniciar um novo turno antes de registrar a SAÍDA do turno anterior"
                )
            }
        }

        // Determinar o turnoId
        val turnoId = when (dto.tipo) {
            TipoPonto.ENTRADA -> UUID.randomUUID().toString() // cria novo turno
            else -> {
                val ultimoTurnoAberto = pontosRepository.findFirstByUsuarioAndTipoNotOrderByHorarioDesc(usuario, TipoPonto.SAIDA)
                    ?: throw RegraDeNegocioException("Não existe turno aberto para registrar ${dto.tipo}")
                ultimoTurnoAberto.turno
            }
        }

        // Buscar todas as batidas do turno
        val batidasDoTurno = pontosRepository.findByUsuarioAndHorarioBetweenOrderByHorarioAsc(
            usuario,
            horario.minusHours(12), // busca últimas 12h para incluir overnight
            horario.plusHours(12)
        ).filter { it.turno == turnoId }

        // Validação mínima de consistência
        when (dto.tipo) {
            TipoPonto.SAIDA -> {
                val entradas = batidasDoTurno.count { it.tipo == TipoPonto.ENTRADA || it.tipo == TipoPonto.VOLTA_ALMOCO }
                val saidas = batidasDoTurno.count { it.tipo == TipoPonto.SAIDA || it.tipo == TipoPonto.ALMOCO }
                if (entradas <= saidas) {
                    throw RegraDeNegocioException("Não é possível registrar SAÍDA sem entrada correspondente")
                }
            }
            TipoPonto.ALMOCO -> {
                val entradas = batidasDoTurno.count { it.tipo == TipoPonto.ENTRADA || it.tipo == TipoPonto.VOLTA_ALMOCO }
                val almocos = batidasDoTurno.count { it.tipo == TipoPonto.ALMOCO }
                if (entradas <= almocos) {
                    throw RegraDeNegocioException("Não é possível registrar ALMOÇO sem entrada correspondente")
                }
            }
            TipoPonto.VOLTA_ALMOCO -> {
                val almocos = batidasDoTurno.count { it.tipo == TipoPonto.ALMOCO }
                val voltas = batidasDoTurno.count { it.tipo == TipoPonto.VOLTA_ALMOCO }
                if (almocos <= voltas) {
                    throw RegraDeNegocioException("Não é possível registrar VOLTA_ALMOCO sem ALMOÇO correspondente")
                }
            }
            else -> { /* ENTRADA sempre permitido */ }
        }

        val ponto = Pontos(
            usuario = usuario,
            tipo = dto.tipo,
            horario = horario,
            localidade = dto.localidade,
            observacoes = dto.observacoes,
            turno = turnoId
        )

        val salvo = pontosRepository.save(ponto)
        return PontosResponseDTO.fromEntity(salvo)
    }
}
