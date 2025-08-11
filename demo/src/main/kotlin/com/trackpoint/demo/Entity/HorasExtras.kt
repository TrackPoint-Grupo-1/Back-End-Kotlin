package com.trackpoint.demo.Entity

import jakarta.persistence.*
import java.time.LocalDate

@Entity
data class HorasExtras(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    val usuario: Usuarios,
    val data: LocalDate,
    val horas: Double,
    val motivo: String,
    val status: Boolean,
    val criadoEm: LocalDate = LocalDate.now()
)