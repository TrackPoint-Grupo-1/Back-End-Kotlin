package com.trackpoint.demo.DTO

import com.trackpoint.demo.Entity.Projeto
import com.trackpoint.demo.Enum.StatusProjeto
import java.time.LocalDate
import java.time.LocalDateTime

class ProjetoResponseDTO(
    val id: Int,
    val nome: String,
    val descricao: String,
    val gerentes: List<String>,
    val usuarios: List<String>,
    val previsaoEntrega: LocalDate,
    val status : StatusProjeto,
    val criadoEm: LocalDateTime
) {
    companion object {
        fun fromEntity(projeto: Projeto): ProjetoResponseDTO {
            return ProjetoResponseDTO(
                id = projeto.id,
                nome = projeto.nome,
                descricao = projeto.descricao,
                gerentes = projeto.gerentes.map { "id: ${it.id},email: ${it.email},nome: ${it.nome}" },
                usuarios = projeto.usuarios.map {  "id: ${it.id},email: ${it.email},nome: ${it.nome}" },
                previsaoEntrega = projeto.previsaoEntrega,
                status = projeto.status,
                criadoEm = projeto.criadoEm
            )
        }
    }
}