package com.trackpoint.demo.Service

import com.trackpoint.demo.DTO.TarefaCreateRequestDTO
import com.trackpoint.demo.DTO.TarefaResponseDTO
import com.trackpoint.demo.DTO.TarefaStatusUpdateDTO
import com.trackpoint.demo.Entity.Tarefa
import com.trackpoint.demo.Enum.StatusTarefa
import com.trackpoint.demo.Exeptions.*
import com.trackpoint.demo.Repository.ProjetoRepository
import com.trackpoint.demo.Repository.TarefaRepository
import com.trackpoint.demo.Repository.UsuariosRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class TarefaService(
    private val tarefaRepository: TarefaRepository,
    private val usuarioRepository: UsuariosRepository,
    private val projetoRepository: ProjetoRepository
) {

    fun criarTarefa(dto: TarefaCreateRequestDTO): TarefaResponseDTO {
        val usuario = usuarioRepository.findById(dto.usuario)
            .orElseThrow { UsuarioNotFoundException("Usuário com ID ${dto.usuario} não encontrado") }

        val projeto = projetoRepository.findById(dto.projeto)
            .orElseThrow { ProjetoNaoEncontradoException("Projeto com ID ${dto.projeto} não encontrado") }

        val tarefa = Tarefa(
            nome = dto.nome,
            descricao = dto.descricao,
            horasEstimadas = dto.horasEstimadas,
            horasJaFeitas = 0.0,
            usuario = usuario,
            projeto = projeto
        )

        return TarefaResponseDTO.fromEntity(tarefaRepository.save(tarefa))
    }

    fun listarTarefasProjeto(projetoId: Int): List<TarefaResponseDTO> {
        val projeto = projetoRepository.findById(projetoId)
            .orElseThrow { ProjetoNaoEncontradoException("Projeto com id $projetoId não encontrado.") }

        val tarefas = tarefaRepository.findByProjetoId(projeto.id)

        if (tarefas.isEmpty()) {
            throw NenhumaTarefaEncontradaException("Nenhuma tarefa encontrada para o projeto ${projeto.nome}.")
        }

        return tarefas.map { TarefaResponseDTO.fromEntity(it) }
    }

    fun buscarTarefaPorId(tarefaId: Int): TarefaResponseDTO {
        val tarefa = tarefaRepository.findById(tarefaId)
            .orElseThrow { NenhumaTarefaEncontradaException("Tarefa com id $tarefaId não encontrada.") }

        return TarefaResponseDTO.fromEntity(tarefa)
    }

    fun listarTarefasPorUsuario(usuarioId: Int): List<TarefaResponseDTO> {
        val usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow { UsuarioNotFoundException("Usuário com ID $usuarioId não encontrado") }

        val tarefas = tarefaRepository.findByUsuarioId(usuario.id)

        if (tarefas.isEmpty()) {
            throw NenhumaTarefaEncontradaException("Nenhuma tarefa encontrada para o usuário ${usuario.nome}.")
        }

        return tarefas.map { TarefaResponseDTO.fromEntity(it) }
    }

    fun atualizarStatusTarefa(tarefaId: Int, dto: TarefaStatusUpdateDTO): TarefaResponseDTO {
        val tarefa = tarefaRepository.findById(tarefaId)
            .orElseThrow { NenhumaTarefaEncontradaException("Tarefa com id $tarefaId não encontrada.") }

        val novoStatus = try {
            StatusTarefa.valueOf(dto.status.uppercase())
        } catch (e: IllegalArgumentException) {
            throw StatusInvalidoException(
                "Status '${dto.status}' inválido. Valores aceitos: ${StatusTarefa.entries.joinToString()}"
            )
        }

        if (tarefa.status == novoStatus) {
            throw StatusIgualException(
                "A tarefa já está com o status '${novoStatus.name}', não é possível atualizar para o mesmo status."
            )
        }

        tarefa.status = novoStatus
        val agora = LocalDateTime.now()
        tarefa.listaDataStatus.add("${novoStatus.name} = $agora")

        if (novoStatus == StatusTarefa.CONCLUIDO) {
            tarefa.dataConclusao = agora
        }

        val tarefaSalva = tarefaRepository.save(tarefa)
        return TarefaResponseDTO.fromEntity(tarefaSalva)
    }

    fun listarTarefasPorStatus(projetoId: Int, status: String): List<TarefaResponseDTO> {
        val projeto = projetoRepository.findById(projetoId)
            .orElseThrow { ProjetoNaoEncontradoException("Projeto com id $projetoId não encontrado.") }

        val statusEnum = try {
            StatusTarefa.valueOf(status.uppercase())
        } catch (e: IllegalArgumentException) {
            throw StatusInvalidoException(
                "Status '$status' inválido. Valores aceitos: ${StatusTarefa.entries.joinToString()}"
            )
        }

        val tarefas = tarefaRepository.findByProjetoIdAndStatus(projeto.id, statusEnum)

        if (tarefas.isEmpty()) {
            throw NenhumaTarefaEncontradaException("Nenhuma tarefa encontrada para o status '${statusEnum.name}'.")
        }

        return tarefas.map { TarefaResponseDTO.fromEntity(it) }
    }


}