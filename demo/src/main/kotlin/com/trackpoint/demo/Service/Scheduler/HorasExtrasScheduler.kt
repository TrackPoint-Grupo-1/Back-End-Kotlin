
package com.trackpoint.demo.Service.Scheduler

import com.trackpoint.demo.Entity.SolicitacaoHorasExtras
import com.trackpoint.demo.Entity.Usuarios
import com.trackpoint.demo.Enum.TipoPonto
import com.trackpoint.demo.Repository.PontosRepository
import com.trackpoint.demo.Repository.SolicitarHorasExtrasRepository
import jakarta.transaction.Transactional
import org.springframework.scheduling.annotation.Scheduled
import java.time.LocalDate

import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime

@Service
class HorasExtrasScheduler(
    private val pontosRepository: PontosRepository,
    private val horasExtrasRepository: SolicitarHorasExtrasRepository
) {

    private val limiteMinimoHorasExtras = Duration.ofMinutes(15) // só gera se exceder 15 minutos

    @Transactional
    @Scheduled(cron = "0 50 23 * * *") // todos os dias às 23:50
    fun verificarHorasExtras() {
        val hoje = LocalDate.now()
        val inicioDoDia = hoje.atStartOfDay()
        val fimDoDia = hoje.atTime(23, 59, 59)

        // Busca todos pontos do dia
        val pontosDoDia = pontosRepository.findByHorarioBetween(inicioDoDia, fimDoDia)

        // Agrupa pontos por usuário e turno
        pontosDoDia
            .groupBy { it.usuario to it.turno }
            .forEach { (usuarioTurno, pontosTurno) ->
                val (usuario, turno) = usuarioTurno

                val entrada = pontosTurno.minByOrNull { it.horario } // primeiro horário do turno
                val saida = pontosTurno.maxByOrNull { it.horario }   // último horário do turno

                // Se não tiver saída ainda, não calcula nada
                if (entrada == null || saida == null || entrada == saida) return@forEach

                val horarioEntrada = entrada.horario.toLocalTime()
                val horarioSaida = saida.horario.toLocalTime()

                // ✅ Calcula fim da jornada incluindo tempo de almoço de forma segura
                val fimJornada = calcularJornadaComAlmoco(usuario, turno, entrada.horario, saida.horario)

                // 1️⃣ Marcar horas extras solicitadas como feitas
                val horasExtrasSolicitadas = horasExtrasRepository.findByUsuarioAndData(usuario, hoje)
                horasExtrasSolicitadas.forEach { horaExtra ->
                    if (!horaExtra.foiFeita && horarioSaida >= horaExtra.horasAte) {
                        horaExtra.foiFeita = true
                        horasExtrasRepository.save(horaExtra)
                    }
                }

                // 2️⃣ Gerar hora extra não solicitada
                if (saida.horario > fimJornada) {
                    val duracaoExcedente = Duration.between(fimJornada, saida.horario)
                    if (duracaoExcedente >= limiteMinimoHorasExtras) {
                        val jaExiste = horasExtrasRepository.findByUsuarioAndDataAndHorasDeAndHorasAte(
                            usuario,
                            hoje,
                            fimJornada.toLocalTime(),
                            horarioSaida
                        ).isNotEmpty()

                        if (!jaExiste) {
                            val novaHoraExtra = SolicitacaoHorasExtras(
                                usuario = usuario,
                                projeto = null,
                                data = hoje,
                                horasDe = fimJornada.toLocalTime(),
                                horasAte = horarioSaida,
                                justificativa = "Hora extra não solicitada",
                                observacao = "Gerada automaticamente pelo sistema com base na jornada",
                                foiSolicitada = false,
                                turno = turno // mantém UUID do turno
                            )
                            horasExtrasRepository.save(novaHoraExtra)
                        }
                    }
                }
            }
    }

    /**
     * Calcula o fim da jornada somando a jornada normal do usuário + o tempo de almoço no turno.
     * Se houver ALMOÇO sem VOLTA_ALMOCO, considera horário de saída como volta.
     */
    private fun calcularJornadaComAlmoco(
        usuario: Usuarios,
        turno: String,
        horarioInicio: LocalDateTime,
        horarioSaida: LocalDateTime
    ): LocalDateTime {
        // Busca todos os pontos do turno
        val pontosDoTurno = pontosRepository.findByUsuarioAndHorarioBetweenOrderByHorarioAsc(
            usuario,
            horarioInicio.minusHours(12),
            horarioInicio.plusHours(12)
        ).filter { it.turno == turno }

        var duracaoAlmoco = Duration.ZERO
        var almocoInicio: LocalDateTime? = null

        pontosDoTurno.forEach { ponto ->
            when (ponto.tipo) {
                TipoPonto.ALMOCO -> almocoInicio = ponto.horario
                TipoPonto.VOLTA_ALMOCO -> {
                    if (almocoInicio != null) {
                        duracaoAlmoco += Duration.between(almocoInicio, ponto.horario)
                        almocoInicio = null
                    }
                }
                else -> {}
            }
        }

        // Se terminou o turno e ainda há ALMOÇO aberto, considera a saída como volta do almoço
        if (almocoInicio != null) {
            duracaoAlmoco += Duration.between(almocoInicio, horarioSaida)
        }

        // Retorna fim da jornada = entrada + jornada + tempo de almoço
        return horarioInicio.plusHours(usuario.jornada.toLong()).plus(duracaoAlmoco)
    }
}










