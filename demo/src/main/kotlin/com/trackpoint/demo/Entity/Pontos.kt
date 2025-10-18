package com.trackpoint.demo.Entity

import com.trackpoint.demo.Config.ConversorJPA.LocalidadeCryptoConverter
import com.trackpoint.demo.Enum.TipoPonto
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
data class Pontos(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id")
    val usuario: Usuarios,

    @Enumerated(EnumType.STRING)
    val tipo: TipoPonto,

    @Convert(converter = LocalidadeCryptoConverter::class)
    var localidade: String? = null,

    var manual: Boolean,

    val horario: LocalDateTime = LocalDateTime.now(),

    val observacoes: String? = null,

    val turno: String = UUID.randomUUID().toString(),

    val modificado: Boolean = false,

    val modificadoEm: LocalDateTime? = null
)
