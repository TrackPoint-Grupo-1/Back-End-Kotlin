package com.trackpoint.demo.Entity

import com.trackpoint.demo.Enum.CargosEnum
import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import lombok.Data
import java.time.LocalDateTime

@Entity
data class Usuarios(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int,
    val nome: String,
    val email: String,
    val senha: String,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val cargo: CargosEnum,
    var ativo: Boolean,
    val criadoEm: LocalDateTime
)
