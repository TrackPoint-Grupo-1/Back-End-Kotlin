package com.trackpoint.demo.Entity

import jakarta.persistence.*
import java.time.LocalDate

@Entity
data class ApontamentoHoras(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @ManyToOne
    val usuario: Usuarios,

    val data: LocalDate,

    val horasFeita: Double? = 0.0,

    val acao: String,

    val descricao: String,

    val horas: Double,

    @ManyToOne
    val projeto: Projeto? = null
)

