package com.trackpoint.demo.Entity

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalTime

@Entity
data class SolicitacaoHorasExtras(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    val usuario: Usuarios,
    val data: LocalDate,
    var horasDe: LocalTime,
    var horasAte: LocalTime,
    var codigoProjeto: Int,
    @Column(nullable = false)
    var justificativa: String,
    var observacao: String,
    var foiSolicitada: Boolean,
    var foiFeita: Boolean = false,
    var foiAprovada: Boolean = false,
    val criadoEm: LocalDate = LocalDate.now()
)