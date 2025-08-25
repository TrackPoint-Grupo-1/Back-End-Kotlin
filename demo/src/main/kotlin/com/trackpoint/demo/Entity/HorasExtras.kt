package com.trackpoint.demo.Entity

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalTime

@Entity
data class HorasExtras(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    val usuario: Usuarios,
    val data: LocalDate,
    var horasDe: LocalTime,
    var horasAte: LocalTime,
    @Column(nullable = false)
    var motivo: String,
    var foiSolicitada: Boolean,
    var foiFeita: Boolean = false,
    val criadoEm: LocalDate = LocalDate.now()
)