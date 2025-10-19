package com.trackpoint.demo.Service

import com.trackpoint.demo.DTO.*
import com.trackpoint.demo.Entity.SolicitacaoHorasExtras
import com.trackpoint.demo.Entity.Pontos
import com.trackpoint.demo.Enum.StatusSolicitacao
import com.trackpoint.demo.Exeptions.InvalidDateFormatException
import com.trackpoint.demo.Exeptions.NenhumaHoraExtraEncontradaException
import com.trackpoint.demo.Exeptions.RegraDeNegocioException
import com.trackpoint.demo.Exeptions.UsuarioNotFoundException
import com.trackpoint.demo.Repository.SolicitarHorasExtrasRepository
import com.trackpoint.demo.Repository.PontosRepository
import com.trackpoint.demo.Repository.ProjetoRepository
import com.trackpoint.demo.Repository.UsuariosRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Service
class SolicitarHorasExtrasService(
    private val solicitarHorasExtrasRepository: SolicitarHorasExtrasRepository,
    private val usuariosRepository: UsuariosRepository,
    private val pontosRepository: PontosRepository,
    private val projetosRepository: ProjetoRepository
) {

    fun criarHorasExtras(dto: SolicitacaoHorasExtrasCreateRequestDTO): SolicitacaoHorasExtras {
        val usuario = usuariosRepository.findById(dto.usuarioId)
            .orElseThrow { UsuarioNotFoundException("Usuário não encontrado com id: ${dto.usuarioId}") }

        if (dto.horasAte.isBefore(dto.horasDe)) {
            throw RegraDeNegocioException("O horário de término não pode ser anterior ao horário de início.")
        }

        val projeto = projetosRepository.findById(dto.projeto)
            .orElseThrow { RegraDeNegocioException("Projeto não encontrado com id: ${dto.projeto}") }

        val usuarioVinculado = projeto.usuarios.any { it.id == usuario.id } || projeto.gerentes.any { it.id == usuario.id }
        if (!usuarioVinculado) {
            throw RegraDeNegocioException("Usuário não está vinculado ao projeto ${dto.projeto}")
        }

        val existente = solicitarHorasExtrasRepository.findByUsuarioIdAndData(usuario.id, dto.data)

        return if (existente != null) {
            existente.apply {
                horasDe = dto.horasDe
                horasAte = dto.horasAte
                this.projeto = projeto
                justificativa = dto.justificativa
                observacao = dto.observacao
                foiSolicitada = true
            }.also { solicitarHorasExtrasRepository.save(it) }
        } else {
            val novaSolicitacao = SolicitacaoHorasExtras(
                usuario = usuario,
                projeto = projeto,
                data = dto.data,
                horasDe = dto.horasDe,
                horasAte = dto.horasAte,
                justificativa = dto.justificativa,
                observacao = dto.observacao,
                foiSolicitada = true,
                foiAprovada = StatusSolicitacao.PENDENTE,
                foiFeita = false,
                criadoEm = LocalDate.now()
            )
            solicitarHorasExtrasRepository.save(novaSolicitacao)
        }
    }

    fun listarTodasHorasExtras(): List<SolicitacaoHorasExtras> {
        return solicitarHorasExtrasRepository.findAll()
    }

    fun listarTodasHorasQueForamSolicitada(): List<SolicitacaoHorasExtras> {
        val horas = solicitarHorasExtrasRepository.findByFoiSolicitadaTrue()
        if (horas.isEmpty()) {
            throw NenhumaHoraExtraEncontradaException("Nenhuma hora extra solicitada foi encontrada.")
        }
        return horas
    }

    fun listarTodasHorasQueNaoForamSolicitada(): List<SolicitacaoHorasExtras> {
        val horas = solicitarHorasExtrasRepository.findByFoiSolicitadaFalse()
        if (horas.isEmpty()) {
            throw NenhumaHoraExtraEncontradaException("Nenhuma hora extra não solicitada foi encontrada.")
        }
        return horas
    }

    fun atualizarHorasExtras(id: Int, dto: SolicitacaoHorasExtrasUpdateRequestDTO): SolicitacaoHorasExtras {
        val horasExtrasExistente = solicitarHorasExtrasRepository.findById(id)
            .orElseThrow { NenhumaHoraExtraEncontradaException("Horas extras não encontrada com id: $id") }

        val usuario = dto.usuarioId?.let {
            usuariosRepository.findById(it)
                .orElseThrow { UsuarioNotFoundException("Usuário não encontrado com id: $it") }
        } ?: horasExtrasExistente.usuario

        val projetoAtualizado = dto.projeto?.let { projetoId ->
            val projeto = projetosRepository.findById(projetoId)
                .orElseThrow { RegraDeNegocioException("Projeto não encontrado com id: $projetoId") }

            val usuarioVinculado = projeto.usuarios.any { it.id == usuario.id } ||
                    projeto.gerentes.any { it.id == usuario.id }

            if (!usuarioVinculado) {
                throw RegraDeNegocioException("O usuário ${usuario.id} não está vinculado ao projeto $projetoId")
            }

            projeto
        } ?: horasExtrasExistente.projeto

        val horasExtrasAtualizada = horasExtrasExistente.copy(
            usuario = usuario,
            data = dto.data ?: horasExtrasExistente.data,
            horasDe = dto.horasDe ?: horasExtrasExistente.horasDe,
            horasAte = dto.horasAte ?: horasExtrasExistente.horasAte,
            projeto = projetoAtualizado, // agora é do tipo Projeto
            justificativa = dto.justificativa ?: horasExtrasExistente.justificativa,
            observacao = dto.observacao ?: horasExtrasExistente.observacao,
            foiSolicitada = dto.foiSolicitada ?: horasExtrasExistente.foiSolicitada,
            foiFeita = dto.foiFeita ?: horasExtrasExistente.foiFeita,
            foiAprovada = dto.foiAprovada ?: horasExtrasExistente.foiAprovada
        )

        return solicitarHorasExtrasRepository.save(horasExtrasAtualizada)
    }


    fun cancelarHorasExtras(id: Int) {
        val horasExtras = solicitarHorasExtrasRepository.findById(id)
            .orElseThrow { NenhumaHoraExtraEncontradaException("Horas extras não encontrada com id: $id") }
        solicitarHorasExtrasRepository.delete(horasExtras)
    }

    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    fun listarHorasPorUsuarioEntreDatas(
        usuarioId: Int,
        dataInicio: String,
        dataFim: String,
        foiSolicitado: Boolean? = null
    ): TotalHorasExtraDTO {

        val inicio = try {
            LocalDate.parse(dataInicio, formatter)
        } catch (e: DateTimeParseException) {
            throw InvalidDateFormatException("Data de início '$dataInicio' está em formato inválido. Use dd/MM/yyyy.")
        }

        val fim = try {
            LocalDate.parse(dataFim, formatter)
        } catch (e: DateTimeParseException) {
            throw InvalidDateFormatException("Data de fim '$dataFim' está em formato inválido. Use dd/MM/yyyy.")
        }

        val horasExtrasList = if (foiSolicitado != null) {
            solicitarHorasExtrasRepository.findByUsuarioIdAndDataBetweenAndFoiSolicitada(usuarioId, inicio, fim, foiSolicitado)
        } else {
            solicitarHorasExtrasRepository.findByUsuarioIdAndDataBetween(usuarioId, inicio, fim)
        }

        if (horasExtrasList.isEmpty()) {
            throw NenhumaHoraExtraEncontradaException(
                "Nenhuma hora extra ${foiSolicitado?.let { if (it) "solicitada" else "não solicitada" } ?: ""} encontrada para o usuário $usuarioId entre $dataInicio e $dataFim."
            )
        }

        // transforma em DTO de resposta
        val listaHorasDTO = horasExtrasList.map { SolicitacaoHorasExtrasResponseDTO(it) }

        // soma as horas feitas
        val totalHoras = horasExtrasList.sumOf {
            val duracao = Duration.between(it.horasDe, it.horasAte)
            duracao.toMinutes().toDouble() / 60 // converte minutos para horas decimais
        }

        val usuario = horasExtrasList.first().usuario

        val rankingDTO = RankingHorasExtrasDTO(
            usuarioId = usuario.id,
            nome = usuario.nome,
            totalHoras = totalHoras
        )

        return TotalHorasExtraDTO(
            listaHoras = listaHorasDTO,
            horasTotal = rankingDTO
        )
    }

    fun rankingFuncionariosHorasExtrasNoMes(): List<RankingHorasExtrasDTO> {
        val solicitacoes = solicitarHorasExtrasRepository.findHorasExtrasFeitasNoMes()

        if (solicitacoes.isEmpty()) {
            throw NenhumaHoraExtraEncontradaException("Nenhuma hora extra feita encontrada no mês atual.")
        }

        val horasPorUsuario = solicitacoes
            .groupBy { it.usuario }
            .map { (usuario, lista) ->
                val totalHoras = lista.sumOf { solicitacao ->
                    val minutos = Duration.between(solicitacao.horasDe, solicitacao.horasAte).toMinutes()
                    if (minutos > 0) minutos / 60.0 else 0.0
                }
                RankingHorasExtrasDTO(
                    usuarioId = usuario.id,
                    nome = usuario.nome,
                    totalHoras = totalHoras
                )
            }
            .sortedByDescending { it.totalHoras }

        return horasPorUsuario
    }

    fun rankingUsuariosPorProjeto(projetoId: Int): List<RankingHorasExtrasProjetoDTO> {
        val ranking = solicitarHorasExtrasRepository.buscarRankingPorProjeto(projetoId)
        if (ranking.isEmpty()) {
            throw NenhumaHoraExtraEncontradaException("Nenhuma hora extra encontrada para o projeto com id $projetoId")
        }
        return ranking
    }

    fun listarTotalHorasExtrasDeTodosOsProjetoPorGerente(
        gerenteId: Int,
        dataInicio: String,
        dataFim: String,
        foiSolicitado: Boolean? = null
    ): TotalHorasExtrasDTO {

        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val inicio = LocalDate.parse(dataInicio, formatter)
        val fim = LocalDate.parse(dataFim, formatter)

        // 1️⃣ Buscar todos os projetos do gerente
        val projetos = projetosRepository.findByGerenteIdInList(gerenteId)
        if (projetos.isEmpty()) return TotalHorasExtrasDTO("00:00:00")

        // 2️⃣ Buscar todas as solicitações no período, filtrando só as feitas
        val solicitacoes = if (foiSolicitado != null) {
            solicitarHorasExtrasRepository.findByProjetoInAndDataBetweenAndFoiSolicitadaAndFoiFeita(
                projetos, inicio, fim, foiSolicitado, true
            )
        } else {
            solicitarHorasExtrasRepository.findByProjetoInAndDataBetweenAndFoiFeita(
                projetos, inicio, fim, true
            )
        }

        // 3️⃣ Somar todas as horas extras
        var totalHorasExtrasMinutos = 0
        solicitacoes.forEach { s ->
            val duracao = java.time.Duration.between(s.horasDe, s.horasAte)
            val minutos = duracao.toMinutes().toInt()
            totalHorasExtrasMinutos += minutos
        }

        // 4️⃣ Converter para HH:mm:ss
        val h = totalHorasExtrasMinutos / 60
        val m = totalHorasExtrasMinutos % 60
        val horaExtraFormatada = "%02d:%02d:00".format(h, m)

        return TotalHorasExtrasDTO(horaExtraFormatada)
    }

    fun listarSolicitacoesPendentesPorGerente(idGerente: Int): List<SolicitacaoHorasExtras> {
        val projetosDoGerente = projetosRepository.findByGerenteIdInList(idGerente)
        if (projetosDoGerente.isEmpty()) {
            throw RegraDeNegocioException("O gerente com id $idGerente não possui projetos vinculados.")
        }

        val solicitacoesPendentes = solicitarHorasExtrasRepository.findByProjetoInAndFoiSolicitadaAndFoiFeitaAndFoiAprovada(
            projetosDoGerente,
            foiSolicitada = true,
            foiFeita = false,
            foiAprovada = StatusSolicitacao.PENDENTE
        )

        if (solicitacoesPendentes.isEmpty()) {
            throw NenhumaHoraExtraEncontradaException(
                "Nenhuma solicitação de horas extras pendente encontrada para o gerente com id $idGerente."
            )
        }

        return solicitacoesPendentes
    }



}