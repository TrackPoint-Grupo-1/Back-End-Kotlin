package com.trackpoint.demo.Service

import com.trackpoint.demo.DTO.TarefaCreateRequestDTO
import com.trackpoint.demo.DTO.TarefaResponseDTO
import com.trackpoint.demo.Entity.Tarefa
import com.trackpoint.demo.Exeptions.ProjetoNaoEncontradoException
import com.trackpoint.demo.Exeptions.UsuarioNotFoundException
import com.trackpoint.demo.Repository.ProjetoRepository
import com.trackpoint.demo.Repository.TarefaRepository
import com.trackpoint.demo.Repository.UsuariosRepository
import org.springframework.stereotype.Service

@Service
class TarefaService(
    private val tarefaRepository: TarefaRepository,
    private val usuarioRepository: UsuariosRepository,
    private val projetoRepository: ProjetoRepository
) {

    fun criarTarefa(dto: TarefaCreateRequestDTO): TarefaResponseDTO {
        val usuario = usuarioRepository.findById(dto.usuarios)
            .orElseThrow { UsuarioNotFoundException("Usuário com ID ${dto.usuarios} não encontrado") }

        val projeto = projetoRepository.findById(dto.projeto)
            .orElseThrow { ProjetoNaoEncontradoException("Projeto com ID ${dto.projeto} não encontrado") }

        val tarefa = Tarefa(
            nome = dto.nome,
            descricao = dto.descricao,
            horasEstimadas = dto.horasEstimadas,
            horasJaFeitas = 0.0,
            usuarios = usuario,
            projeto = projeto
        )

        return TarefaResponseDTO.fromEntity(tarefaRepository.save(tarefa))
    }
}