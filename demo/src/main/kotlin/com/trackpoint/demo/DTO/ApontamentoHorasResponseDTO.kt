package com.trackpoint.demo.DTO

import com.trackpoint.demo.Entity.ApontamentoHoras
import com.trackpoint.demo.Entity.Projeto
import java.time.LocalDate

data class ApontamentoHorasResponseDTO(
    val id: Int,
    val usuarioId: Int,
    val data: LocalDate,
    val acao: String,
    val horasFeita: Double,
    val descricao: String,
    val horas: Double,
    val Projeto: Projeto?
) {
    companion object {
        fun fromEntity(entity: ApontamentoHoras) = entity.horasFeita?.let {
            ApontamentoHorasResponseDTO(
                id = entity.id,
                usuarioId = entity.usuario.id,
                data = entity.data,
                acao = entity.acao,
                descricao = entity.descricao,
                horas = entity.horas,
                horasFeita = it,
                Projeto = entity.projeto
            )
        }
    }
}