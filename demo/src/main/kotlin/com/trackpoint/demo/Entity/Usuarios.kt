package com.trackpoint.demo.Entity

import com.trackpoint.demo.Enum.CargosEnum
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import lombok.Data
import java.time.LocalDateTime

@Data
@Entity
class Usuarios(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id : Int,
    @NotBlank(message = "O nome não pode estar vazio")
    val nome: String,

    @NotBlank(message = "O email não pode estar vazio")
    @Email(message = "O email deve ser válido")
    val email: String,

    @NotBlank(message = "A senha não pode estar vazia")
    @Size(min = 8, message = "A senha deve ter pelo menos 8 caracteres")
    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}$",
        message = "A senha deve conter ao menos uma letra maiúscula, uma minúscula, um número e um caractere especial"
    )
    val senha: String,

    @NotBlank(message = "O cargo não pode estar vazio")
    val cargo: CargosEnum,
    val ativo : Boolean,
    val criadoEm : LocalDateTime
) {
}