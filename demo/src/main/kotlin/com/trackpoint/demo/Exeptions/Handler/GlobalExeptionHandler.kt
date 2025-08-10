package com.trackpoint.demo.Exeptions.Handler

import com.trackpoint.demo.Exeptions.EmailJaExisteException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExeptionHandler {
    @ExceptionHandler(EmailJaExisteException::class)
    fun handleEmailJaExiste(ex: EmailJaExisteException): ResponseEntity<Map<String, String>> {
        val body = mapOf("erro" to ex.message.orEmpty())
        return ResponseEntity.badRequest().body(body)
    }
}