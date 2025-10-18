package com.trackpoint.demo.Service

import com.trackpoint.demo.DTO.SolicitarAjusteRequestDTO
import com.trackpoint.demo.DTO.SolicitarAjusteResponseDTO
import com.trackpoint.demo.DTO.SolicitarAjusteStatusUpdateDTO
import com.trackpoint.demo.Entity.SolicitarAjuste
import com.trackpoint.demo.Enum.StatusSolicitacao
import com.trackpoint.demo.Exeptions.*
import com.trackpoint.demo.Repository.SolicitarAjusteRepository
import com.trackpoint.demo.Repository.UsuariosRepository
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class SolicitarAjusteService(
    private val usuariosRepository: UsuariosRepository,
    private val solicitarAjusteRepository: SolicitarAjusteRepository
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

        if (totalSolicitacoes >= 6) {
            throw RegraDeNegocioException("O limite máximo de 6 solicitações por mês já foi atingido.")
        }

        val solicitacao = SolicitarAjuste(
            usuario = usuario,
            data = request.data,
            horaEntrada = request.horaEntrada,
            horaAlmoco = request.horaAlmoco,
            horaVoltaAlmoco = request.horaVoltaAlmoco,
            horaSaida = request.horaSaida,
            motivo = request.motivo
        )

        return SolicitarAjusteResponseDTO.fromEntity(solicitarAjusteRepository.save(solicitacao))
    }

    fun listarSolicitacoesPorUsuario(usuarioId: Int): List<SolicitarAjusteResponseDTO> {
        return solicitarAjusteRepository.findByUsuarioId(usuarioId)
            .map { SolicitarAjusteResponseDTO.fromEntity(it) }
    }

    fun listarSolicitacoesPendentes(): List<SolicitarAjusteResponseDTO> {
        return solicitarAjusteRepository.findByStatus(StatusSolicitacao.PENDENTE)
            .map { SolicitarAjusteResponseDTO.fromEntity(it) }
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

}
