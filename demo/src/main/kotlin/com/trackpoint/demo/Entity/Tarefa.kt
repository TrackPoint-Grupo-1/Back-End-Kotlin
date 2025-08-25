package com.trackpoint.demo.Entity

import com.fasterxml.jackson.annotation.JsonGetter
import com.trackpoint.demo.Enum.StatusTarefa
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class Tarefa (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,
    val nome: String,
    val descricao: String,
    val horasEstimadas: Int,
    val horasJaFeitas: Double = 0.0,
    @ManyToOne
    @JoinColumn(name = "usuarios_id")
    val usuario: Usuarios? = null,
    @ManyToOne
    @JoinColumn(name = "projeto_id")
    val projeto: Projeto? = null,
    var status: StatusTarefa = StatusTarefa.PENDENTE,
    val listaDataStatus: MutableList<String> = mutableListOf(),
    val dataCriacao: LocalDateTime = LocalDateTime.now(),
    var dataConclusao: LocalDateTime? = null
)