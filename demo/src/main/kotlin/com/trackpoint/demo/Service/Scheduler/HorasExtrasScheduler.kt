
package com.trackpoint.demo.Service.Scheduler

import com.trackpoint.demo.Entity.SolicitacaoHorasExtras
import com.trackpoint.demo.Entity.Usuarios
import com.trackpoint.demo.Enum.TipoPonto
import com.trackpoint.demo.Repository.PontosRepository
import com.trackpoint.demo.Repository.SolicitarHorasExtrasRepository
import jakarta.transaction.Transactional
import org.springframework.scheduling.annotation.Scheduled
import java.time.LocalDate
import java.time.YearMonth

import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime

@Service
class HorasExtrasScheduler(
    private val pontosRepository: PontosRepository,
    private val horasExtrasRepository: SolicitarHorasExtrasRepository
) {

    private val limiteMinimoHorasExtras = Duration.ofMinutes(15) // só gera se exceder 15 minutos

    //@Scheduled(cron = "0 50 23 * * *") // todos os dias às 23:50
    @Scheduled(fixedRate = 10000) // a cada 10 segundos
    @Transactional
    fun verificarHorasExtras() {
        val hoje = LocalDate.now()
        val anoMes = YearMonth.from(hoje)
        val inicioMes = anoMes.atDay(1)
        val fimMes = anoMes.atEndOfMonth()
        processarEntreDatas(inicioMes, fimMes)
    }

    @Transactional
    fun processarEntreDatas(inicio: LocalDate, fim: LocalDate) {
        println("Processando horas extras de ${inicio} até ${fim}...")

        var dia = inicio
        while (!dia.isAfter(fim)) {

            val inicioDoDia = dia.atStartOfDay()
            val fimDoDia = dia.atTime(23, 59, 59)

            // Busca todos pontos do dia
            val pontosDoDia = pontosRepository.findByHorarioBetween(inicioDoDia, fimDoDia)

            // Agrupa pontos por usuário e turno
            pontosDoDia
                .groupBy { it.usuario to it.turno }
                .forEach { (usuarioTurno, pontosTurno) ->
                    val (usuario, turno) = usuarioTurno

                    val entrada = pontosTurno.minByOrNull { it.horario }
                    val saida = pontosTurno.maxByOrNull { it.horario }

                    if (entrada == null || saida == null || entrada == saida) return@forEach

                    val horarioEntrada = entrada.horario.toLocalTime()
                    val horarioSaida = saida.horario.toLocalTime()

                    // calcula jornada real
                    val fimJornada = calcularJornadaComAlmoco(usuario, turno, entrada.horario, saida.horario)

                    // Logs de diagnóstico do cálculo
                    println(
                        "[HorasExtras] usuario=${usuario.id} nome=${usuario.nome} dia=${dia} " +
                        "entrada=${entrada.horario} saida=${saida.horario} jornada=${usuario.jornada}h " +
                        "fimJornada=${fimJornada} excedenteMin=${java.time.Duration.between(fimJornada, saida.horario).toMinutes()}"
                    )

                    // --- 1) marcar horas extras solicitadas como feitas ---
                    val horasExtrasSolicitadas = horasExtrasRepository.findByUsuarioAndData(usuario, dia)
                    horasExtrasSolicitadas.forEach { horaExtra ->
                        if (!horaExtra.foiFeita && horarioSaida >= horaExtra.horasAte) {
                            horaExtra.foiFeita = true
                            horasExtrasRepository.save(horaExtra)
                        }
                    }

                    // --- 2) gerar horas extras não solicitadas ---
                    if (saida.horario > fimJornada) {
                        val duracaoExcedente = Duration.between(fimJornada, saida.horario)
                        if (duracaoExcedente >= limiteMinimoHorasExtras) {
                            println("[HorasExtras] Excedente >= ${limiteMinimoHorasExtras.toMinutes()}min; gerando não solicitada para usuario=${usuario.id} dia=${dia}")

                            val jaExiste = horasExtrasRepository.findByUsuarioAndDataAndHorasDeAndHorasAte(
                                usuario,
                                dia,
                                fimJornada.toLocalTime(),
                                horarioSaida
                            ).isNotEmpty()

                            if (!jaExiste) {
                                println("[HorasExtras] Não existe registro igual; salvando hora extra não solicitada usuario=${usuario.id} turno=${turno} de=${fimJornada.toLocalTime()} até=${horarioSaida}")
                                horasExtrasRepository.save(
                                    SolicitacaoHorasExtras(
                                        usuario = usuario,
                                        projeto = null,
                                        data = dia,
                                        horasDe = fimJornada.toLocalTime(),
                                        horasAte = horarioSaida,
                                        justificativa = "Hora extra não solicitada",
                                        observacao = "Gerada automaticamente pelo sistema",
                                        foiSolicitada = false,
                                        foiFeita = true, 
                                        turno = turno
                                    )
                                )
                            } else {
                                println("[HorasExtras] Já existe hora extra registrada para usuario=${usuario.id} dia=${dia} de=${fimJornada.toLocalTime()} até=${horarioSaida}")
                            }
                        }
                        else {
                            println("[HorasExtras] Excedente < ${limiteMinimoHorasExtras.toMinutes()}min; não gera (usuario=${usuario.id} dia=${dia})")
                        }
                    }
                    else {
                        println("[HorasExtras] Saída não excede fim da jornada (usuario=${usuario.id} dia=${dia})")
                    }
                }
            dia = dia.plusDays(1)
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
        return horarioInicio
            .plusHours(usuario.jornada.toLong())
            .plus(duracaoAlmoco)
    }

}










