package com.trackpoint.demo.Service.Scheduler

import com.trackpoint.demo.Repository.HorasExtrasRepository
import com.trackpoint.demo.Repository.PontosRepository
import com.trackpoint.demo.Repository.UsuariosRepository
import com.trackpoint.demo.Service.HorasExtrasService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Service
class HorasExtrasScheduler(
    private val usuariosRepository: UsuariosRepository,
    private val horasExtrasService: HorasExtrasService,
    private val pontosRepository: PontosRepository,
    private val horasExtrasRepository: HorasExtrasRepository
) {

    // @Scheduled(cron = "0 50 23 * * *")
    // @Scheduled(fixedRate = 10000)
    fun processarHorasExtrasAutomaticas() {
        println(
            """
        ╔═════════════════════════════════════════════╗
        ║                                             ║
        ║   🚀🚀🚀 INÍCIO DO PROCESSAMENTO 🚀🚀🚀    ║ 
        ║       Horas extras automáticas              ║
        ║       Início: ${LocalDateTime.now()}        ║     
        ║                                             ║
        ╚═════════════════════════════════════════════╝
    """.trimIndent()
        )

        val usuarios = usuariosRepository.findAll()
        println("👥 Total de usuários encontrados: ${usuarios.size}")

        val hoje = LocalDate.now()
        val inicioDoDia = hoje.atStartOfDay()
        val fimDoDia = hoje.atTime(LocalTime.MAX)
        println("📅 Processando horas extras para o dia: $hoje (intervalo $inicioDoDia → $fimDoDia)\n")

        usuarios.forEach { usuario ->
            println("──────────────────────────────────────────")
            println("👤 Usuário ID: ${usuario.id}, Nome: ${usuario.nome}")
            val pontosDoDia = pontosRepository.findByUsuarioIdAndCriadoEmBetween(usuario.id, inicioDoDia, fimDoDia)
            println("🕒 Pontos encontrados: ${pontosDoDia.size}")

            if (pontosDoDia.isNotEmpty()) {
                val ultimoPonto = pontosDoDia.maxByOrNull { it.horaSaida ?: it.horaEntrada ?: throw IllegalArgumentException("Hora de entrada e saída não registradas") }
                    ?: run { println("⚠️ Nenhum ponto válido encontrado, pulando usuário."); return@forEach }

                println("📝 Último ponto do dia: Entrada=${ultimoPonto.horaEntrada}, Saída=${ultimoPonto.horaSaida}")

                val horasExtras = horasExtrasRepository.findByUsuarioIdAndData(usuario.id, hoje)
                if (horasExtras != null) {
                    println("⏱️ Horas extras registradas previamente: ${horasExtras.horasDe} → ${horasExtras.horasAte} (Solicitadas: ${horasExtras.foiSolicitada}, Feitas: ${horasExtras.foiFeita})")
                } else {
                    println("❌ Nenhuma hora extra registrada anteriormente")
                }

                val horaLimite = horasExtras?.horasAte

                if (horaLimite != null && ultimoPonto.horaSaida?.toLocalTime()?.isBefore(horaLimite.plusMinutes(1)) == true) {
                    println("✅ Usuário já teve horas extras processadas até o limite ($horaLimite), ignorando...")
                    return@forEach
                }

                println("⚡ Gerando novas horas extras para o usuário...")
                //horasExtrasService.gerarHorasExtrasAutomaticas(usuario.id, hoje)
                println("✅ Processamento de horas extras concluído para o usuário ${usuario.id}")
            } else {
                println("❌ Nenhum ponto registrado para o dia, nada a processar para o usuário ${usuario.id}")
            }
        }

        println(
            """
        ╔═══════════════════════════════════════════════════════════════════╗
        ║                                                                   ║
        ║   🎯🎯🎯 PROCESSAMENTO CONCLUÍDO 🎯🎯🎯                          ║
        ║       Horas extras automáticas                                    ║
        ║       Concluído em: ${LocalDateTime.now()}                        ║
        ║                                                                   ║
        ╚═══════════════════════════════════════════════════════════════════╝
    """.trimIndent()
        )
    }
}
