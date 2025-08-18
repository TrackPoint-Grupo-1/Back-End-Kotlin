package com.trackpoint.demo.Service.Scheduler

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
    private val pontosRepository: PontosRepository
) {

    @Scheduled(cron = "0 0 0 * * *")
    fun processarHorasExtrasAutomaticas() {
        println("Iniciando processamento automático de horas extras: ${LocalDateTime.now()}")

        val usuarios = usuariosRepository.findAll()
        val hoje = LocalDate.now()
        val inicioDoDia = hoje.atStartOfDay()
        val fimDoDia = hoje.atTime(LocalTime.MAX)

        usuarios.forEach { usuario ->
            // Busca todos os pontos do dia para o usuário
            val pontosDoDia = pontosRepository.findByUsuarioIdAndCriadoEmBetween(usuario.id, inicioDoDia, fimDoDia)
            if (pontosDoDia.isNotEmpty()) {
                horasExtrasService.gerarHorasExtrasAutomaticas(usuario.id, hoje)
            }
        }

        println("""
        ***
        ***
        ***
        Processamento automático de horas extras concluído: ${LocalDateTime.now()}
        ***
        ***
        ***
    """.trimIndent())
    }

}