package com.trackpoint.demo.Entity

import com.trackpoint.demo.Enum.StatusSolicitacao
import jakarta.persistence.*
import java.sql.Time
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
class SolicitarAjuste(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    val usuario: Usuarios,

    @Column(nullable = false)
    val data: LocalDate,

    val horaEntrada: Time? = null,
    val horaAlmoco: Time? = null,
    val horaVoltaAlmoco: Time? = null,
    val horaSaida: Time? = null,

    @Column(nullable = false)
    val motivo: String,

    var status: StatusSolicitacao = StatusSolicitacao.PENDENTE,

    val criadoEm: LocalDateTime = LocalDateTime.now()
)