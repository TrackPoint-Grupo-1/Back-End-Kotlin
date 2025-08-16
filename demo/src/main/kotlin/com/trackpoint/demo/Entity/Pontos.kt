package com.trackpoint.demo.Entity

import jakarta.persistence.*
import java.sql.Time
import java.time.LocalDateTime

@Entity
data class Pontos(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int =0,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    val usuario: Usuarios,
    val horaEntrada: LocalDateTime? = null,
    val horaAlmoco: LocalDateTime? = null,
    val horaVoltaAlmoco: LocalDateTime? = null,
    val horaSaida: LocalDateTime? = null,
    val observacoes: String? = null,
    val criadoEm: LocalDateTime = LocalDateTime.now()
)
