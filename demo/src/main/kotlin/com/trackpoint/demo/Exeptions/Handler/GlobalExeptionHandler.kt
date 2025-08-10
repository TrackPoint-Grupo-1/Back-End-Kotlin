package com.trackpoint.demo.Exeptions.Handler

import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import com.trackpoint.demo.Exeptions.EmailJaExisteException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.FieldError
import org.springframework.web.ErrorResponse
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

data class ErrorResponse(val message: String)

data class ValidationErrorResponse(val message: String)

@ControllerAdvice
class GlobalExeptionHandler {
    @ExceptionHandler(EmailJaExisteException::class)
    fun handleEmailJaExiste(ex: EmailJaExisteException): ResponseEntity<Map<String, String>> {
        val body = mapOf("erro" to ex.message.orEmpty())
        return ResponseEntity.badRequest().body(body)
    }

    @ExceptionHandler(InvalidFormatException::class)
    fun handleInvalidFormatException(ex: InvalidFormatException): ResponseEntity<com.trackpoint.demo.Exeptions.Handler.ErrorResponse> {
        val errorResponse = ErrorResponse(message = "Campo cargo inválido")
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<List<ValidationErrorResponse>> {
        val errors = ex.bindingResult.allErrors.map { error ->
            val fieldName = (error as? FieldError)?.field ?: "campo"
            val message = error.defaultMessage ?: "Erro de validação"
            ValidationErrorResponse(message = "$fieldName: $message")
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadable(ex: HttpMessageNotReadableException): ResponseEntity<com.trackpoint.demo.Exeptions.Handler.ErrorResponse> {
        val cause = ex.cause

        if (cause is MissingKotlinParameterException) {
            val missingField = cause.parameter.name ?: "campo"
            val message = "Campo obrigatório '$missingField' não foi informado."
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse(message))
        }

        // Caso não seja esse erro específico, retorna mensagem genérica
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse("JSON inválido ou mal formatado."))
    }

}