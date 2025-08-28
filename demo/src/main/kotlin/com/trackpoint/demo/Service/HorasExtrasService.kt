package com.trackpoint.demo.Service

import com.trackpoint.demo.DTO.SolicitacaoHorasExtrasCreateRequestDTO
import com.trackpoint.demo.DTO.SolicitacaoHorasExtrasUpdateRequestDTO
import com.trackpoint.demo.Entity.SolicitacaoHorasExtras
import com.trackpoint.demo.Entity.Pontos
import com.trackpoint.demo.Exeptions.InvalidDateFormatException
import com.trackpoint.demo.Exeptions.NenhumaHoraExtraEncontradaException
import com.trackpoint.demo.Exeptions.UsuarioNotFoundException
import com.trackpoint.demo.Repository.HorasExtrasRepository
import com.trackpoint.demo.Repository.PontosRepository
import com.trackpoint.demo.Repository.UsuariosRepository
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Service
class HorasExtrasService(
    private val horasExtrasRepository: HorasExtrasRepository,
    private val usuariosRepository: UsuariosRepository,
    private val pontosRepository: PontosRepository
) {

    fun criarHorasExtras(dto: SolicitacaoHorasExtrasCreateRequestDTO): SolicitacaoHorasExtras {
        val usuario = usuariosRepository.findById(dto.usuarioId)
            .orElseThrow { UsuarioNotFoundException("Usuário não encontrado com id: ${dto.usuarioId}") }

        if (dto.horasAte.isBefore(dto.horasDe)) {
            throw IllegalArgumentException("O horário de término não pode ser anterior ao horário de início.")
        }

        val existente = horasExtrasRepository.findByUsuarioIdAndData(usuario.id, dto.data)

        return if (existente != null) {
            existente.apply {
                horasDe = dto.horasDe
                horasAte = dto.horasAte
                codigoProjeto = dto.codigoProjeto
                justificativa = dto.justificativa
                observacao = dto.observacao
            }.also { horasExtrasRepository.save(it) }
        } else {
            val solicitacaoHorasExtras = SolicitacaoHorasExtras(
                usuario = usuario,
                data = dto.data,
                horasDe = dto.horasDe,
                horasAte = dto.horasAte,
                codigoProjeto = dto.codigoProjeto,
                justificativa = dto.justificativa,
                observacao = dto.observacao,
                foiSolicitada = true,
                foiAprovada = false,
                foiFeita = false,
                criadoEm = LocalDate.now()
            )
            horasExtrasRepository.save(solicitacaoHorasExtras)
        }
    }


//    fun gerarHorasExtrasAutomaticas(usuarioId: Int, data: LocalDate) {
//        val usuario = usuariosRepository.findById(usuarioId)
//            .orElseThrow { UsuarioNotFoundException("Usuário não encontrado") }
//
//        val inicioDoDia = data.atStartOfDay()
//        val fimDoDia = data.atTime(LocalTime.MAX)
//
//        val pontosDoDia = pontosRepository.findByUsuarioIdAndCriadoEmBetween(usuarioId, inicioDoDia, fimDoDia)
//        if (pontosDoDia.isEmpty()) return
//
//        pontosDoDia.forEach { ponto ->
//            val periodoTrabalhado = calcularPeriodoTrabalhado(ponto) // Pair<LocalTime, LocalTime>
//            val horasDe = periodoTrabalhado.first
//            val horasAte = periodoTrabalhado.second
//
//            val existente = horasExtrasRepository.findByUsuarioIdAndData(usuarioId, data)
//
//            if (existente != null) {
//                // Caso tenha registro solicitado
//                when {
//                    // Caso 1: trabalhou menos do que o solicitado
//                    horasAte.isAfter(existente.horasDe) && horasAte.isBefore(existente.horasAte) -> {
//                        existente.horasAte = horasAte
//                        existente.foiFeita = true
//                        horasExtrasRepository.save(existente)
//                    }
//
//                    // Caso 2: trabalhou exatamente até o fim ou além do solicitado
//                    horasAte.isAfter(existente.horasAte) || horasAte == existente.horasAte -> {
//                        existente.foiFeita = true
//                        horasExtrasRepository.save(existente)
//
//                        // Se trabalhou além, cria indevida
//                        if (horasAte.isAfter(existente.horasAte)) {
//                            val indevida = HorasExtras(
//                                usuario = usuario,
//                                data = data,
//                                horasDe = existente.horasAte,
//                                horasAte = horasAte,
//                                justificativa = "",
//                                foiSolicitada = false,
//                                foiFeita = true
//                            )
//                            horasExtrasRepository.save(indevida)
//                        }
//                    }
//                }
//            } else {
//                // Nenhum registro solicitado → salva tudo como indevida
//                val indevida = HorasExtras(
//                    usuario = usuario,
//                    data = data,
//                    horasDe = horasDe,
//                    horasAte = horasAte,
//                    justificativa = "",
//                    foiSolicitada = false,
//                    foiFeita = true
//                )
//                horasExtrasRepository.save(indevida)
//            }
//        }
//    }

    fun calcularHorasTrabalhadas(ponto: Pontos): Double {
        val (inicio, fim) = calcularPeriodoTrabalhado(ponto)
        var duracao = Duration.between(inicio, fim).toMinutes().toDouble() / 60.0

        // Desconta almoço se registrado
        if (ponto.horaAlmoco != null && ponto.horaVoltaAlmoco != null) {
            val almoco = Duration.between(
                ponto.horaAlmoco!!.toLocalTime(),
                ponto.horaVoltaAlmoco!!.toLocalTime()
            ).toMinutes().toDouble() / 60.0
            duracao -= almoco
        }

        return duracao.coerceAtLeast(0.0)
    }

    fun calcularPeriodoTrabalhado(ponto: Pontos): Pair<LocalTime, LocalTime> {
        val entrada = ponto.horaEntrada?.toLocalTime()
            ?: throw IllegalArgumentException("Hora de entrada não registrada")
        val saida = ponto.horaSaida?.toLocalTime()
            ?: throw IllegalArgumentException("Hora de saída não registrada")

        val almocoInicio = ponto.horaAlmoco?.toLocalTime()
        val almocoFim = ponto.horaVoltaAlmoco?.toLocalTime()

        var inicioTrabalho = entrada
        var fimTrabalho = saida

        if (almocoInicio != null && almocoFim != null) {
            val duracaoTotal = Duration.between(entrada, saida).toMinutes()
            val duracaoAlmoco = Duration.between(almocoInicio, almocoFim).toMinutes()
            val duracaoEfetiva = duracaoTotal - duracaoAlmoco

            println("Duração efetiva descontando almoço: $duracaoEfetiva minutos")
        }

        return Pair(inicioTrabalho, fimTrabalho)
    }

    fun listarTodasHorasExtras(): List<SolicitacaoHorasExtras> {
        return horasExtrasRepository.findAll()
    }

    fun listarTodasHorasQueForamSolicitada(): List<SolicitacaoHorasExtras> {
        val horas = horasExtrasRepository.findByFoiSolicitadaTrue()
        if (horas.isEmpty()) {
            throw NenhumaHoraExtraEncontradaException("Nenhuma hora extra solicitada foi encontrada.")
        }
        return horas
    }

    fun listarTodasHorasQueNaoForamSolicitada(): List<SolicitacaoHorasExtras> {
        val horas = horasExtrasRepository.findByFoiSolicitadaFalse()
        if (horas.isEmpty()) {
            throw NenhumaHoraExtraEncontradaException("Nenhuma hora extra não solicitada foi encontrada.")
        }
        return horas
    }

    fun atualizarHorasExtras(id: Int, dto: SolicitacaoHorasExtrasUpdateRequestDTO): SolicitacaoHorasExtras {
        val horasExtrasExistente = horasExtrasRepository.findById(id)
            .orElseThrow { RuntimeException("Horas extras não encontrada com id: $id") }

        val usuario = dto.usuarioId?.let {
            usuariosRepository.findById(it)
                .orElseThrow { UsuarioNotFoundException("Usuário não encontrado com id: $it") }
        } ?: horasExtrasExistente.usuario

        val horasExtrasAtualizada = horasExtrasExistente.copy(
            usuario = usuario,
            data = dto.data ?: horasExtrasExistente.data,
            horasDe = dto.horasDe ?: horasExtrasExistente.horasDe,
            horasAte = dto.horasAte ?: horasExtrasExistente.horasAte,
            codigoProjeto = dto.codigoProjeto ?: horasExtrasExistente.codigoProjeto,
            justificativa = dto.justificativa ?: horasExtrasExistente.justificativa,
            observacao = dto.observacao ?: horasExtrasExistente.observacao,
            foiSolicitada = dto.foiSolicitada ?: horasExtrasExistente.foiSolicitada,
            foiFeita = dto.foiFeita ?: horasExtrasExistente.foiFeita,
        )

        return horasExtrasRepository.save(horasExtrasAtualizada)
    }

    fun cancelarHorasExtras(id: Int) {
        val horasExtras = horasExtrasRepository.findById(id)
            .orElseThrow { NenhumaHoraExtraEncontradaException("Horas extras não encontrada com id: $id") }
        horasExtrasRepository.delete(horasExtras)
    }

    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    fun listarHorasPorUsuarioEntreDatas(
        usuarioId: Int,
        dataInicio: String,
        dataFim: String,
        foiSolicitado: Boolean? = null
    ): List<SolicitacaoHorasExtras> {

        // Valida e parse das datas
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

        // Busca filtrando pelo status, se fornecido
        val horasExtrasList = if (foiSolicitado != null) {
            horasExtrasRepository.findByUsuarioIdAndDataBetweenAndFoiSolicitada(usuarioId, inicio, fim, foiSolicitado)
        } else {
            horasExtrasRepository.findByUsuarioIdAndDataBetween(usuarioId, inicio, fim)
        }

        // Lança exceção se não encontrar nada
        if (horasExtrasList.isEmpty()) {
            throw NenhumaHoraExtraEncontradaException(
                "Nenhuma hora extra ${foiSolicitado?.let { if (it) "solicitada" else "não solicitada" } ?: ""} encontrada para o usuário $usuarioId entre $dataInicio e $dataFim."
            )
        }

        return horasExtrasList
    }

}