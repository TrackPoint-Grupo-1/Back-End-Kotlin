package com.trackpoint.demo.Service.Scheduler

import com.trackpoint.demo.Repository.UsuariosRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime

@Service
class DeslogarUsuarioScheduler (
    private val usuariosRepository: UsuariosRepository
 ){

    @Scheduled(cron = "0 0 0 * * *")
    fun deslogarUsuarioAutomaticamente() {
        println("""
            ***
            ***
            ***
            Iniciando verificação de usuários logados para deslogar às 00:00 horas: ${LocalDateTime.now()}
            ***
            ***
            ***
        """.trimIndent())
        val agora = LocalDateTime.now()
        val usuariosLogados = usuariosRepository.findByLogadoTrue()

        usuariosLogados.forEach { usuario ->
            usuario.horasUltimoLogin?.let {
                val horasDesdeLogin = Duration.between(it, agora).toHours()
                if (horasDesdeLogin >= 10) {
                    usuario.logado = false
                    usuariosRepository.save(usuario)
                    println("""
                        ***
                        ***
                        ***
                        Usuário ${usuario.id} deslogado automaticamente após $horasDesdeLogin horas de login.
                        ***
                        ***
                        ***
                    """.trimIndent())
                }
            }
        }
    }

}