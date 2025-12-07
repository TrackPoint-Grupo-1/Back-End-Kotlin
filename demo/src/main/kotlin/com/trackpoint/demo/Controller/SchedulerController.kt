package com.trackpoint.demo.Controller

import com.trackpoint.demo.Service.Scheduler.HorasExtrasScheduler
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("/scheduler")
class SchedulerController(
    private val horasExtrasScheduler: HorasExtrasScheduler
) {

    @PostMapping("/horas-extras/reprocessar")
    fun reprocessarHorasExtras(
        @RequestParam("inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) inicio: LocalDate,
        @RequestParam("fim") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) fim: LocalDate
    ): ResponseEntity<String> {
        if (fim.isBefore(inicio)) {
            return ResponseEntity.badRequest().body("Parâmetro 'fim' não pode ser antes de 'inicio'.")
        }
        horasExtrasScheduler.processarEntreDatas(inicio, fim)
        return ResponseEntity.ok("Reprocessamento iniciado para ${inicio} até ${fim}.")
    }
}
