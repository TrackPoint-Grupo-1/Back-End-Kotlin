package com.trackpoint.demo.Service

import com.trackpoint.demo.DTO.SolicitarAjusteRequestDTO
import com.trackpoint.demo.DTO.SolicitarAjusteResponseDTO
import com.trackpoint.demo.DTO.SolicitarAjusteStatusUpdateDTO
import com.trackpoint.demo.Entity.SolicitarAjuste
import com.trackpoint.demo.Enum.StatusSolicitacao
import com.trackpoint.demo.Exeptions.*
import com.trackpoint.demo.Repository.ProjetoRepository
import com.trackpoint.demo.Repository.SolicitarAjusteRepository
import com.trackpoint.demo.Repository.UsuariosRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class SolicitarAjusteService(
    private val usuariosRepository: UsuariosRepository,
    private val solicitarAjusteRepository: SolicitarAjusteRepository,
    private val projetosRepository: ProjetoRepository
) {

    fun criarSolicitacao(request: SolicitarAjusteRequestDTO, usuarioId: Int): SolicitarAjusteResponseDTO {
        val usuario = usuariosRepository.findById(usuarioId)
            .orElseThrow { UsuarioNotFoundException("Usuário não encontrado com id: $usuarioId") }

        if (request.data.isAfter(LocalDate.now())) {
            throw InvalidDateFormatException("Não é permitido criar solicitação para data futura")
        }

        val inicioDoMes = LocalDate.now().withDayOfMonth(1).atStartOfDay()
        val fimDoMes = inicioDoMes.plusMonths(1).minusNanos(1) // ⬅️ usei minusNanos pra precisão

        val totalSolicitacoes = solicitarAjusteRepository
            .countByUsuarioIdAndCriadoEmBetween(usuario.id, inicioDoMes, fimDoMes)

        if (totalSolicitacoes >= 5) {
            throw RegraDeNegocioException("O limite máximo de 5 solicitações por mês já foi atingido.")
        }

        val solicitacao = SolicitarAjuste(
            usuario = usuario,
            data = request.data,
            justificativa = request.justificativa,
            observacao = request.observacao
        )

        return SolicitarAjusteResponseDTO.fromEntity(solicitarAjusteRepository.save(solicitacao))
    }

    fun listarSolicitacoesPorUsuario(usuarioId: Int): List<SolicitarAjusteResponseDTO> {
        return solicitarAjusteRepository.findByUsuarioId(usuarioId)
            .map { SolicitarAjusteResponseDTO.fromEntity(it) }
    }

    fun listarSolicitacoesPendentesPorGestor(gestorId: Int): List<SolicitarAjusteResponseDTO> {
        val projetosDoGestor = projetosRepository.findByGerenteIdInList(gestorId)
        if (projetosDoGestor.isEmpty()) {
            throw RegraDeNegocioException("O gestor com id $gestorId não possui projetos vinculados.")
        }

        val usuariosDosProjetos = projetosDoGestor.flatMap { it.usuarios }.map { it.id }.toSet()
        if (usuariosDosProjetos.isEmpty()) {
            throw RegraDeNegocioException("Nenhum usuário encontrado nos projetos do gestor com id $gestorId.")
        }

        val solicitacoesPendentes = solicitarAjusteRepository.findByProjetosAndStatus(
            projetosDoGestor,
            StatusSolicitacao.PENDENTE
        )

        if (solicitacoesPendentes.isEmpty()) {
            throw NenhumaSolicitacaoEncontradaException(
                "Nenhuma solicitação pendente encontrada para o gestor com id $gestorId."
            )
        }

        return solicitacoesPendentes.map { SolicitarAjusteResponseDTO.fromEntity(it) }
    }

    fun atualizarStatus(id: Int, statusDTO: SolicitarAjusteStatusUpdateDTO): SolicitarAjusteResponseDTO {
        val solicitacao = solicitarAjusteRepository.findById(id)
            .orElseThrow { RuntimeException("Solicitação não encontrada com id: $id") }

        if (solicitacao.status != StatusSolicitacao.PENDENTE) {
            throw StatusNaoAtualizavelException("Somente solicitações com status PENDENTE podem ser atualizadas")
        }

        val statusUpper = statusDTO.status.uppercase()
        val statusEnum = try {
            StatusSolicitacao.valueOf(statusUpper)
        } catch (e: IllegalArgumentException) {
            throw StatusNaoAtualizavelException("Status inválido: ${statusDTO.status}. Apenas PENDENTE, APROVADO ou REJEITADO são permitidos")
        }

        solicitacao.status = statusEnum
        return SolicitarAjusteResponseDTO.fromEntity(solicitarAjusteRepository.save(solicitacao))
    }


    fun listarSolicitacoesPorUsuarioEStatus(usuarioId: Int, statusString: String): List<SolicitarAjusteResponseDTO> {
        val statusEnum = try {
            StatusSolicitacao.valueOf(statusString.uppercase())
        } catch (e: IllegalArgumentException) {
            throw StatusSolicitacaoInvalidoException(
                "Status inválido: $statusString. Apenas PENDENTE, APROVADO ou REJEITADO são permitidos"
            )
        }

        val solicitacoes = solicitarAjusteRepository.findByUsuarioIdAndStatus(usuarioId, statusEnum)
        if (solicitacoes.isEmpty()) {
            throw NenhumaSolicitacaoEncontradaException("Nenhuma solicitação $statusEnum encontrada para o usuário $usuarioId")
        }

        return solicitacoes.map { SolicitarAjusteResponseDTO.fromEntity(it) }
    }

    fun listarSolicitacoesPorUsuarioEMes(
        usuarioId: Int,
        dataInicio: String,
        dataFim: String
    ): List<SolicitarAjusteResponseDTO> {
        val inicio = try {
            LocalDate.parse(dataInicio, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        } catch (e: Exception) {
            throw InvalidDateFormatException("Formato de data inválido para dataInicio: $dataInicio. Use o formato dd/MM/yyyy.")
        }

        val fim = try {
            LocalDate.parse(dataFim, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        } catch (e: Exception) {
            throw InvalidDateFormatException("Formato de data inválido para dataFim: $dataFim. Use o formato dd/MM/yyyy.")
        }

        if (fim.isBefore(inicio)) {
            throw InvalidDateFormatException("dataFim não pode ser anterior a dataInicio.")
        }

        val solicitacoes = solicitarAjusteRepository
            .findByUsuarioIdAndDataBetween(usuarioId, inicio, fim)

        if (solicitacoes.isEmpty()) {
            throw NenhumaSolicitacaoEncontradaException("Nenhuma solicitação encontrada para o usuário $usuarioId no período especificado.")
        }

        return solicitacoes.map { SolicitarAjusteResponseDTO.fromEntity(it) }
    }

}
