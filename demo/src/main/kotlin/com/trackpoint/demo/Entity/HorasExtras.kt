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
    @Column(nullable = false)
    val motivo: String,
    val foiSolicitada: Boolean,
    val criadoEm: LocalDate = LocalDate.now()
)