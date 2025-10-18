package com.trackpoint.demo.Service

import com.trackpoint.demo.DTO.*
import com.trackpoint.demo.Entity.Pontos
import com.trackpoint.demo.Enum.TipoPonto
import com.trackpoint.demo.Exeptions.*
import com.trackpoint.demo.Repository.PontosRepository
import com.trackpoint.demo.Repository.UsuariosRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*

@Service
class PontosService(
    private val pontosRepository: PontosRepository,
    private val usuariosRepository: UsuariosRepository
) {

    fun criarPonto(dto: PontosCreateRequestDTO): PontosResponseDTO {
        val usuario = usuariosRepository.findById(dto.usuarioId)
            .orElseThrow { UsuarioNotFoundException("Usuário não encontrado com id: ${dto.usuarioId}") }

        val horario = dto.horario ?: LocalDateTime.now()

        if (dto.tipo == TipoPonto.ENTRADA) {
            val turnoAberto = pontosRepository.findLastOpenTurn(usuario)
            if (turnoAberto != null) {
                throw RegraDeNegocioException(
                    "Não é possível iniciar um novo turno antes de registrar a SAÍDA do turno anterior"
                )
            }
        }

        // Determinar o turnoId
        val turnoId = when (dto.tipo) {
            TipoPonto.ENTRADA -> UUID.randomUUID().toString() // cria novo turno
            else -> {
                val ultimoTurnoAberto = pontosRepository.findFirstByUsuarioAndTipoNotOrderByHorarioDesc(usuario, TipoPonto.SAIDA)
                    ?: throw RegraDeNegocioException("Não existe turno aberto para registrar ${dto.tipo}")
                ultimoTurnoAberto.turno
            }
        }

        // Buscar todas as batidas do turno
        val batidasDoTurno = pontosRepository.findByUsuarioAndHorarioBetweenOrderByHorarioAsc(
            usuario,
            horario.minusHours(12), // busca últimas 12h para incluir overnight
            horario.plusHours(12)
        ).filter { it.turno == turnoId }

        // Validação mínima de consistência
        when (dto.tipo) {
            TipoPonto.SAIDA -> {
                val entradas = batidasDoTurno.count { it.tipo == TipoPonto.ENTRADA || it.tipo == TipoPonto.VOLTA_ALMOCO }
                val saidas = batidasDoTurno.count { it.tipo == TipoPonto.SAIDA || it.tipo == TipoPonto.ALMOCO }
                if (entradas <= saidas) {
                    throw RegraDeNegocioException("Não é possível registrar SAÍDA sem entrada correspondente")
                }
            }
            TipoPonto.ALMOCO -> {
                val entradas = batidasDoTurno.count { it.tipo == TipoPonto.ENTRADA || it.tipo == TipoPonto.VOLTA_ALMOCO }
                val almocos = batidasDoTurno.count { it.tipo == TipoPonto.ALMOCO }
                if (entradas <= almocos) {
                    throw RegraDeNegocioException("Não é possível registrar ALMOÇO sem entrada correspondente")
                }
            }
            TipoPonto.VOLTA_ALMOCO -> {
                val almocos = batidasDoTurno.count { it.tipo == TipoPonto.ALMOCO }
                val voltas = batidasDoTurno.count { it.tipo == TipoPonto.VOLTA_ALMOCO }
                if (almocos <= voltas) {
                    throw RegraDeNegocioException("Não é possível registrar VOLTA_ALMOCO sem ALMOÇO correspondente")
                }
            }
            else -> { /* ENTRADA sempre permitido */ }
        }

        val ponto = Pontos(
            usuario = usuario,
            tipo = dto.tipo,
            horario = horario,
            localidade = dto.localidade,
            observacoes = dto.observacoes,
            manual = false,
            turno = turnoId
        )

        val salvo = pontosRepository.save(ponto)
        return PontosResponseDTO.fromEntity(salvo)
    }

    fun criarPontoManual(dto: PontosCreateRequestDTO): PontosResponseDTO {
        val usuario = usuariosRepository.findById(dto.usuarioId)
            .orElseThrow { UsuarioNotFoundException("Usuário não encontrado com id: ${dto.usuarioId}") }

        val horario = dto.horario ?: LocalDateTime.now()

        if (dto.tipo == TipoPonto.ENTRADA) {
            val turnoAberto = pontosRepository.findLastOpenTurn(usuario)
            if (turnoAberto != null) {
                throw RegraDeNegocioException(
                    "Não é possível iniciar um novo turno antes de registrar a SAÍDA do turno anterior"
                )
            }
        }

        // Determinar o turnoId
        val turnoId = when (dto.tipo) {
            TipoPonto.ENTRADA -> UUID.randomUUID().toString() // cria novo turno
            else -> {
                val ultimoTurnoAberto = pontosRepository.findFirstByUsuarioAndTipoNotOrderByHorarioDesc(usuario, TipoPonto.SAIDA)
                    ?: throw RegraDeNegocioException("Não existe turno aberto para registrar ${dto.tipo}")
                ultimoTurnoAberto.turno
            }
        }

        // Buscar todas as batidas do turno
        val batidasDoTurno = pontosRepository.findByUsuarioAndHorarioBetweenOrderByHorarioAsc(
            usuario,
            horario.minusHours(12), // busca últimas 12h para incluir overnight
            horario.plusHours(12)
        ).filter { it.turno == turnoId }

        // Validação mínima de consistência
        when (dto.tipo) {
            TipoPonto.SAIDA -> {
                val entradas = batidasDoTurno.count { it.tipo == TipoPonto.ENTRADA || it.tipo == TipoPonto.VOLTA_ALMOCO }
                val saidas = batidasDoTurno.count { it.tipo == TipoPonto.SAIDA || it.tipo == TipoPonto.ALMOCO }
                if (entradas <= saidas) {
                    throw RegraDeNegocioException("Não é possível registrar SAÍDA sem entrada correspondente")
                }
            }
            TipoPonto.ALMOCO -> {
                val entradas = batidasDoTurno.count { it.tipo == TipoPonto.ENTRADA || it.tipo == TipoPonto.VOLTA_ALMOCO }
                val almocos = batidasDoTurno.count { it.tipo == TipoPonto.ALMOCO }
                if (entradas <= almocos) {
                    throw RegraDeNegocioException("Não é possível registrar ALMOÇO sem entrada correspondente")
                }
            }
            TipoPonto.VOLTA_ALMOCO -> {
                val almocos = batidasDoTurno.count { it.tipo == TipoPonto.ALMOCO }
                val voltas = batidasDoTurno.count { it.tipo == TipoPonto.VOLTA_ALMOCO }
                if (almocos <= voltas) {
                    throw RegraDeNegocioException("Não é possível registrar VOLTA_ALMOCO sem ALMOÇO correspondente")
                }
            }
            else -> { /* ENTRADA sempre permitido */ }
        }

        var qtdPontosManuais = pontosRepository.countByUsuarioAndManualTrue(usuario)

        if (qtdPontosManuais >= 20) {
            throw RegraDeNegocioException("Limite de 5 conjuntos pontos manuais atingido para o usuário ${usuario.id}")
        }

        val ponto = Pontos(
            usuario = usuario,
            tipo = dto.tipo,
            horario = horario,
            localidade = dto.localidade,
            observacoes = dto.observacoes,
            manual = true,
            turno = turnoId
        )

        val salvo = pontosRepository.save(ponto)
        return PontosResponseDTO.fromEntity(salvo)
    }

    fun atualizarPonto(id: Int, dto: PontosUpdateRequestDTO): PontosResponseDTO {
        val pontoExistente = pontosRepository.findById(id)
            .orElseThrow { PontosNaoEncontradosException("Ponto não encontrado com id: $id") }

        val usuario = usuariosRepository.findById(id)
            .orElseThrow { UsuarioNotFoundException("Usuário não encontrado com id: ${id}") }

        val horario = dto.horario ?: pontoExistente.horario

        val pontoAtualizado = pontoExistente.copy(
            usuario = usuario,
            tipo = dto.tipo ?: pontoExistente.tipo,
            horario = horario,
            localidade = dto.localidades,
            observacoes = dto.observacoes,
            modificado = true,
            modificadoEm = LocalDateTime.now()
        )

        val salvo = pontosRepository.save(pontoAtualizado)
        return PontosResponseDTO.fromEntity(salvo)

    }

    fun listarPontosPorUsuarioPorData(usuarioId: Int, dataInicio: LocalDateTime, dataFim: LocalDateTime): List<PontosResponseDTO> {
        val usuario = usuariosRepository.findById(usuarioId)
            .orElseThrow { UsuarioNotFoundException("Usuário não encontrado com id: $usuarioId") }

        val pontos = pontosRepository.findByUsuarioAndHorarioBetweenOrderByHorarioAsc(usuario, dataInicio, dataFim)

        if (pontos.isEmpty()) {
            throw PontosNaoEncontradosException("Nenhum ponto encontrado para o usuário $usuarioId no período especificado.")
        }

        return pontos.map { PontosResponseDTO.fromEntity(it) }
    }


    fun listarPontosPorUsuarioPorPeriodo(usuarioId: Int, dataInicio: LocalDateTime, dataFim: LocalDateTime): List<PontosResponseDTO> {
        val usuario = usuariosRepository.findById(usuarioId)
            .orElseThrow { UsuarioNotFoundException("Usuário não encontrado com id: $usuarioId") }

        val pontos = pontosRepository.findByUsuarioAndHorarioBetweenOrderByHorarioAsc(usuario, dataInicio, dataFim)

        if (pontos.isEmpty()) {
            throw PontosNaoEncontradosException("Nenhum ponto encontrado para o usuário $usuarioId no período especificado.")
        }

        return pontos.map { PontosResponseDTO.fromEntity(it) }
    }

    fun adicionarPontosFaltantes(
        usuarioId: Int,
        data: String,
        entradaHorario: String?,
        almocoHorario: String?,
        voltaAlmocoHorario: String?,
        saidaHorario: String?
    ): List<PontosResponseDTO> {

        val usuario = usuariosRepository.findById(usuarioId)
            .orElseThrow { UsuarioNotFoundException("Usuário não encontrado com id: $usuarioId") }

        val dataFormatada = LocalDate.parse(data, DateTimeFormatter.ofPattern("dd/MM/yyyy"))

        // Função auxiliar para validar horário
        fun parseHorario(horario: String?, tipo: TipoPonto): LocalTime? {
            return horario?.let {
                try {
                    LocalTime.parse(it)
                } catch (e: DateTimeParseException) {
                    throw RegraDeNegocioException("Horário inválido para $tipo: $it. Use HH:mm")
                }
            }
        }

        val entrada = parseHorario(entradaHorario, TipoPonto.ENTRADA)
        val almoco = parseHorario(almocoHorario, TipoPonto.ALMOCO)
        val voltaAlmoco = parseHorario(voltaAlmocoHorario, TipoPonto.VOLTA_ALMOCO)
        val saida = parseHorario(saidaHorario, TipoPonto.SAIDA)

        // Validação da sequência lógica
        if (entrada != null && almoco != null && entrada >= almoco) throw RegraDeNegocioException("ALMOÇO deve ser após ENTRADA")
        if (almoco != null && voltaAlmoco != null && almoco >= voltaAlmoco) throw RegraDeNegocioException("VOLTA_ALMOCO deve ser após ALMOÇO")
        if (voltaAlmoco != null && saida != null && voltaAlmoco >= saida) throw RegraDeNegocioException("SAÍDA deve ser após VOLTA_ALMOCO")
        if (entrada != null && saida != null && entrada >= saida) throw RegraDeNegocioException("SAÍDA deve ser após ENTRADA")

        // Buscar pontos existentes
        val pontosExistentes = pontosRepository.findByUsuarioAndHorarioBetweenOrderByHorarioAsc(
            usuario, dataFormatada.atStartOfDay(), dataFormatada.atTime(23, 59, 59)
        ).toMutableList()

        val turno = pontosExistentes.firstOrNull()?.turno ?: UUID.randomUUID().toString()
        val tiposExistentes = pontosExistentes.map { it.tipo }.toSet()
        val novosPontos = mutableListOf<Pontos>()

        // Adicionar pontos faltantes
        fun adicionarSeFaltante(tipo: TipoPonto, horario: LocalTime?) {
            if (horario != null && !tiposExistentes.contains(tipo)) {
                val ponto = Pontos(usuario = usuario, tipo = tipo, horario = dataFormatada.atTime(horario), turno = turno, manual = true)
                novosPontos.add(pontosRepository.save(ponto))
            }
        }

        adicionarSeFaltante(TipoPonto.ENTRADA, entrada)
        adicionarSeFaltante(TipoPonto.ALMOCO, almoco)
        adicionarSeFaltante(TipoPonto.VOLTA_ALMOCO, voltaAlmoco)
        adicionarSeFaltante(TipoPonto.SAIDA, saida)

        return novosPontos.map { PontosResponseDTO.fromEntity(it) }
    }

    fun listarPontosFaltantesPorUsuario(usuarioId: Int): List<PontosFaltantesDTO> {
        val usuario = usuariosRepository.findById(usuarioId)
            .orElseThrow { UsuarioNotFoundException("Usuário não encontrado com id: $usuarioId") }

        return pontosRepository.findByUsuario(usuario)
            .groupBy { it.usuario.id to it.turno }
            .mapNotNull { (key, pontos) ->
                val turno = key.second
                val tiposPresentes = pontos.map { it.tipo }.toSet()
                val dataDoTurno = pontos.minByOrNull { it.horario }?.horario?.toLocalDate() ?: return@mapNotNull null

                val tiposFaltantes = TipoPonto.values().filter { it !in tiposPresentes }
                if (tiposFaltantes.isEmpty()) null
                else PontosFaltantesDTO(
                    usuarioId = usuarioId,
                    turno = turno,
                    data = dataDoTurno,
                    tiposFaltantes = tiposFaltantes
                )
            }
    }

    fun listarPontosPorUsuarioEPeriodo(
        usuarioId: Int,
        dataInicio: LocalDate,
        dataFim: LocalDate
    ): TotalHorasDTO {
        val usuario = usuariosRepository.findById(usuarioId)
            .orElseThrow { UsuarioNotFoundException("Usuário não encontrado com id: $usuarioId") }

        val pontos = pontosRepository.findByUsuarioAndHorarioBetween(
            usuario,
            dataInicio.atStartOfDay(),
            dataFim.atTime(23, 59, 59)
        )

        if (pontos.isEmpty()) {
            throw PontosNaoEncontradosException("Nenhum ponto encontrado para o usuário $usuarioId no período de $dataInicio a $dataFim.")
        }

        // Converter para DTO
        val listaDTO = pontos.map { PontosResponseDTO.fromEntity(it) }

        // ---- Cálculo das horas totais ----
        val totalHoras = calcularHorasTrabalhadas(pontos)

        val rankingHorasExtrasDTO = RankingHorasExtrasDTO(
            usuarioId = usuario.id,
            nome = usuario.nome,
            totalHoras = totalHoras
        )

        return TotalHorasDTO(
            listaHoras = listaDTO,
            horasTotal = rankingHorasExtrasDTO
        )
    }

    private fun calcularHorasTrabalhadas(pontos: List<Pontos>): Double {
        val pontosOrdenados = pontos.sortedBy { it.horario }
        var totalHoras = 0.0

        var entrada: LocalDateTime? = null
        for (ponto in pontosOrdenados) {
            when (ponto.tipo) {
                TipoPonto.ENTRADA, TipoPonto.VOLTA_ALMOCO -> entrada = ponto.horario
                TipoPonto.SAIDA, TipoPonto.ALMOCO -> {
                    if (entrada != null) {
                        val duracao = java.time.Duration.between(entrada, ponto.horario).toHours().toDouble()
                        totalHoras += duracao
                        entrada = null
                    }
                }
                else -> {}
            }
        }

        return totalHoras
    }

}
