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

    @Scheduled(fixedRate = 36000000)
    fun deslogarUsuarioAutomaticamente() {
        println(
            """
            ╔═══════════════════════════════════════════════════════════════════╗
            ║                                                                   ║
            ║   🚀🚀🚀 INICIANDO VERIFICAÇÃO DE USUÁRIOS LOGADOS 🚀🚀🚀        ║
            ║       PARA DESLOGAR                                               ║
            ║       HORÁRIO: ${LocalDateTime.now()}                             ║
            ║                                                                   ║
            ╚═══════════════════════════════════════════════════════════════════╝
            """.trimIndent()
        )
        val agora = LocalDateTime.now()
        val usuariosLogados = usuariosRepository.findByLogadoTrue()

        usuariosLogados.forEach { usuario ->
            usuario.horasUltimoLogin?.let {
                val horasDesdeLogin = Duration.between(it, agora).toHours()
                if (horasDesdeLogin >= 10) {
                    usuario.logado = false
                    usuariosRepository.save(usuario)
                    println(
                        """
                        ╔═══════════════════════════════════════════════════════════════════╗
                        ║                                                                   ║
                        ║   🎯🎯🎯 PROCESSAMENTO CONCLUÍDO 🎯🎯🎯                          ║
                        ║       Usuário ${usuario.id} deslogado automaticamente             ║
                        ║       Após $horasDesdeLogin horas de login                        ║
                        ║                                                                   ║
                        ╚════════��══════════════════════════════════════════════════════════╝
                        """.trimIndent()
                    )
                }
            }
        }
    }

}