package com.trackpoint.demo.DTO

data class ApontamentoHorasRequestDTO(
    val data: String,
    val acao: String,
    val descricao: String,
    val horas: Double,
    val projetoId: Int? = null
)
