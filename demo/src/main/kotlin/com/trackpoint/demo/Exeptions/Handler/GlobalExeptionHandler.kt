package com.trackpoint.demo.Exeptions.Handler

import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import com.trackpoint.demo.Exeptions.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.FieldError
import org.springframework.web.ErrorResponse
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

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
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<Map<String, String>> {
        val errors = mutableMapOf<String, String>()
        ex.bindingResult.allErrors.forEach { error ->
            val fieldName = (error as FieldError).field
            val errorMessage = error.defaultMessage ?: "Campo inválido"
            errors[fieldName] = errorMessage
        }
        return ResponseEntity.badRequest().body(errors)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadable(ex: HttpMessageNotReadableException): ResponseEntity<com.trackpoint.demo.Exeptions.Handler.ErrorResponse> {
        val cause = ex.cause

        if (cause is MissingKotlinParameterException) {
            val missingField = cause.parameter.name ?: "campo"
            val message = "Campo obrigatório '$missingField' não foi informado."
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse(message))
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse("JSON inválido ou mal formatado."))
    }

    @ExceptionHandler(UsuarioNotFoundException::class)
    fun handleUsuarioNotFound(ex: UsuarioNotFoundException): ResponseEntity<com.trackpoint.demo.Exeptions.Handler.ErrorResponse> {
        val error = ErrorResponse(ex.message ?: "Usuário não encontrado")
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error)
    }

    @ExceptionHandler(NenhumaHoraExtraEncontradaException::class)
    fun handleNenhumaHoraExtraEncontrada(ex: NenhumaHoraExtraEncontradaException): ResponseEntity<Map<String, String>> {
        val body = mapOf("mensagem" to ex.message.orEmpty())
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body)
    }

    @ExceptionHandler(DataInvalidaException::class)
    fun handleDataInvalida(ex: DataInvalidaException): ResponseEntity<Map<String, String>> {
        val body = mapOf("erro" to ex.message.orEmpty())
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body)
    }

    @ExceptionHandler(InvalidDateFormatException::class)
    fun handleInvalidDate(ex: InvalidDateFormatException): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("mensagem" to ex.message.orEmpty()))
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatch(ex: MethodArgumentTypeMismatchException): ResponseEntity<Map<String, String>> {
        val paramName = ex.name
        val value = ex.value
        val requiredType = ex.requiredType?.simpleName ?: "tipo desconhecido"
        val message = "Valor inválido '$value' para o parâmetro '$paramName'. Esperado tipo: $requiredType."
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("mensagem" to message))
    }

    @ExceptionHandler(DiferencaAlmocoInvalidaException::class)
    fun handleDiferencaAlmoco(ex: DiferencaAlmocoInvalidaException): ResponseEntity<Map<String, String>> {
        val body = mapOf("erro" to ex.message.orEmpty())
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body)
    }

    @ExceptionHandler(PontoInvalidoException::class)
    fun handlePontoInvalidoException(ex: PontoInvalidoException): ResponseEntity<Map<String, String>> {
        val body = mapOf(
            "erro" to ex.message.orEmpty()
        )
        return ResponseEntity(body, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(NenhumaSolicitacaoEncontradaException::class)
    fun handleNenhumaSolicitacaoEncontradaException(
        ex: NenhumaSolicitacaoEncontradaException
    ): ResponseEntity<Map<String, String>> {
        val body = mapOf("mensagem" to ex.message.orEmpty())
        return ResponseEntity(body, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(StatusSolicitacaoInvalidoException::class)
    fun handleStatusInvalido(ex: StatusSolicitacaoInvalidoException): ResponseEntity<com.trackpoint.demo.Exeptions.Handler.ErrorResponse> {
        val response = ErrorResponse(
            message = ex.message ?: "Status inválido"
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }

    @ExceptionHandler(StatusNaoAtualizavelException::class)
    fun handleStatusNaoAtualizavel(ex: StatusNaoAtualizavelException): ResponseEntity<Map<String, String>> {
        val body = mapOf(
            "error" to ex.message.orEmpty()
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body)
    }

    @ExceptionHandler(PontosNaoEncontradosException::class)
    fun handlePontosNaoEncontradosException(ex: PontosNaoEncontradosException): ResponseEntity<Map<String, String>> {
        val body = mapOf(
            "mensagem" to ex.message.orEmpty()
        )
        return ResponseEntity(body, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(RegraDeNegocioException::class)
    fun handleRegraDeNegocioException(ex: RegraDeNegocioException): ResponseEntity<Map<String, String>> {
        val body = mapOf(
            "mensagem" to ex.message.orEmpty()
        )
        return ResponseEntity(body, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(GerenteInvalidoException::class)
    fun handleGerenteInvalidoException(ex: GerenteInvalidoException): ResponseEntity<com.trackpoint.demo.Exeptions.Handler.ErrorResponse> {
        val response = ErrorResponse(
            message = ex.message ?: "Todos os gerentes informados devem ter cargo de GERENTE"
        )
        return ResponseEntity(response, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(GerenteComoUsuarioException::class)
    fun handleGerenteComoUsuarioException(ex: GerenteComoUsuarioException): ResponseEntity<com.trackpoint.demo.Exeptions.Handler.ErrorResponse> {
        val response = ErrorResponse(
            message = ex.message ?: "Erro ao cadastrar projeto"
        )
        return ResponseEntity(response, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(ProjetoNomeDuplicadoException::class)
    fun handleProjetoNomeDuplicadoException(ex: ProjetoNomeDuplicadoException): ResponseEntity<Map<String, String>> {
        val response = mapOf("mensagem" to ex.message.orEmpty())
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }

    @ExceptionHandler(ProjetoNaoEncontradoException::class)
    fun handleProjetoNaoEncontradoException(ex: ProjetoNaoEncontradoException): ResponseEntity<Map<String, String>> {
        val response = mapOf("mensagem" to ex.message.orEmpty())
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response)
    }

}
