package com.trackpoint.demo.DTO

import com.fasterxml.jackson.annotation.JsonGetter
import com.trackpoint.demo.Entity.Tarefa
import com.trackpoint.demo.Enum.StatusTarefa
import java.time.LocalDateTime

class TarefaResponseDTO(
    val id: Int,
    val usuarios: Int?,
    val projeto: Int?,
    val nome: String,
    val descricao: String,
    val horasEstimadas: Int,
    val horasJaFeitas: Double,
    val status: StatusTarefa,
    val listaDataStatus: MutableList<String>,
    val dataCriacao: LocalDateTime,
    val dataConclusao: LocalDateTime?
) {
    companion object {
        fun fromEntity(tarefa: Tarefa): TarefaResponseDTO {
            return TarefaResponseDTO(
                id = tarefa.id,
                usuarios = tarefa.usuario?.id,
                projeto = tarefa.projeto?.id,
                nome = tarefa.nome,
                descricao = tarefa.descricao,
                horasEstimadas = tarefa.horasEstimadas,
                horasJaFeitas = tarefa.horasJaFeitas,
                status = tarefa.status,
                listaDataStatus = tarefa.listaDataStatus,
                dataCriacao = tarefa.dataCriacao,
                dataConclusao = tarefa.dataConclusao
            )
        }
    }

    @JsonGetter("dataConclusao")
    fun getDataConclusaoOrNA(): String {
        return dataConclusao?.toString() ?: "N/A"
    }
}
