package com.trackpoint.demo.DTO

import com.trackpoint.demo.Enum.CargosEnum
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class UsuariosCreateRequestDTO(
    @field:NotBlank(message = "O nome não pode estar vazio")
    val nome: String,

    @field:NotBlank(message = "O email não pode estar vazio")
    @field:Email(message = "O email deve ser válido")
    val email: String,

    @field:NotBlank(message = "A senha não pode estar vazia")
    @field:Size(min = 8, message = "A senha deve ter pelo menos 8 caracteres")
    @field:Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}$",
        message = "A senha deve conter ao menos uma letra maiúscula, uma minúscula, um número e um caractere especial"
    )
    val senha: String,

    @field:jakarta.validation.constraints.NotNull(message = "O cargo não pode ser nulo")
    val cargo: CargosEnum,

    @field:jakarta.validation.constraints.NotNull(message = "A jornada não pode ser nula")
    var jornada: Double
)
