package com.trackpoint.demo.Service

import com.trackpoint.demo.DTO.HorasExtrasCreateRequestDTO
import com.trackpoint.demo.DTO.HorasExtrasUpdateRequestDTO
import com.trackpoint.demo.Entity.HorasExtras
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

    fun criarHorasExtras(dto: HorasExtrasCreateRequestDTO): HorasExtras {
        val usuario = usuariosRepository.findById(dto.usuarioId)
            .orElseThrow { UsuarioNotFoundException("Usuário não encontrado com id: ${dto.usuarioId}") }

        val existente = horasExtrasRepository.findByUsuarioIdAndData(usuario.id, dto.data)

        return if (existente != null) {
            existente.apply {
                horas = if (dto.horas <= usuario.jornada) dto.horas else usuario.jornada
                motivo = dto.motivo ?: motivo
                foiSolicitada = true
            }.also { horasExtrasRepository.save(it) }.also { existente ->
                if (dto.horas > existente.horas) {
                    val indevida = HorasExtras(
                        usuario = usuario,
                        data = dto.data,
                        horas = dto.horas - existente.horas,
                        motivo = "",
                        foiSolicitada = false,
                        criadoEm = LocalDate.now()
                    )
                    horasExtrasRepository.save(indevida)
                }
            }
        } else {
            val horasExtras = HorasExtras(
                usuario = usuario,
                data = dto.data,
                horas = dto.horas,
                motivo = dto.motivo ?: "",
                foiSolicitada = dto.foiSolicitado,
                criadoEm = LocalDate.now()
            )
            horasExtrasRepository.save(horasExtras)
        }
    }

    fun gerarHorasExtrasAutomaticas(usuarioId: Int, data: LocalDate) {
        val usuario = usuariosRepository.findById(usuarioId)
            .orElseThrow { UsuarioNotFoundException("Usuário não encontrado") }

        // Determina início e fim do dia
        val inicioDoDia = data.atStartOfDay()
        val fimDoDia = data.atTime(LocalTime.MAX)

        // Busca todos os pontos do usuário no dia
        val pontosDoDia = pontosRepository.findByUsuarioIdAndCriadoEmBetween(usuarioId, inicioDoDia, fimDoDia)
        if (pontosDoDia.isEmpty()) return

        pontosDoDia.forEach { ponto ->
            // Calcula horas trabalhadas corretamente
            val horasTrabalhadas = calcularHorasTrabalhadas(ponto)  // Deve descontar horário de almoço
            val jornadaNormal = usuario.jornada
            val horasExtrasTotais = (horasTrabalhadas - jornadaNormal).coerceAtLeast(0.0)

            if (horasExtrasTotais == 0.0) return@forEach

            // Verifica se já existe uma hora extra solicitada
            val existente = horasExtrasRepository.findByUsuarioIdAndData(usuarioId, data)

            if (existente != null) {
                if (horasExtrasTotais > existente.horas) {
                    // Calcula a diferença entre o que foi trabalhado e o que foi solicitado
                    val horasIndevidas = horasExtrasTotais - existente.horas

                    // Atualiza o registro existente com a quantidade combinada
                    existente.horas = existente.horas
                    horasExtrasRepository.save(existente)

                    // Cria novo registro para horas indevidas
                    val indevida = HorasExtras(
                        usuario = usuario,
                        data = data,
                        horas = horasIndevidas,
                        motivo = "",
                        foiSolicitada = false,
                        foiFeita = true
                    )
                    horasExtrasRepository.save(indevida)
                } else {
                    // Se trabalhou menos ou igual ao combinado, só atualiza se necessário
                    existente.horas = horasExtrasTotais
                    horasExtrasRepository.save(existente)
                }
            } else {
                // Nenhuma hora extra solicitada: registra todas como indevidas
                val indevida = HorasExtras(
                    usuario = usuario,
                    data = data,
                    horas = horasExtrasTotais,
                    motivo = "",
                    foiSolicitada = false
                )
                horasExtrasRepository.save(indevida)
            }
        }
    }

    private fun calcularHorasTrabalhadas(ponto: Pontos): Double {
        val entrada = ponto.horaEntrada ?: return 0.0
        val saida = ponto.horaSaida ?: return 0.0
        val almocoInicio = ponto.horaAlmoco
        val almocoFim = ponto.horaVoltaAlmoco

        var total = Duration.between(entrada, saida).toMinutes().toDouble() / 60.0
        if (almocoInicio != null && almocoFim != null) {
            total -= Duration.between(almocoInicio, almocoFim).toMinutes().toDouble() / 60.0
        }
        return total
    }


    fun listarTodasHorasExtras(): List<HorasExtras> {
        return horasExtrasRepository.findAll()
    }

    fun listarTodasHorasQueForamSolicitada(): List<HorasExtras> {
        val horas = horasExtrasRepository.findByFoiSolicitadaTrue()
        if (horas.isEmpty()) {
            throw NenhumaHoraExtraEncontradaException("Nenhuma hora extra solicitada foi encontrada.")
        }
        return horas
    }

    fun listarTodasHorasQueNaoForamSolicitada(): List<HorasExtras> {
        val horas = horasExtrasRepository.findByFoiSolicitadaFalse()
        if (horas.isEmpty()) {
            throw NenhumaHoraExtraEncontradaException("Nenhuma hora extra não solicitada foi encontrada.")
        }
        return horas
    }

    fun atualizarHorasExtras(id: Int, dto: HorasExtrasUpdateRequestDTO): HorasExtras {
        val horasExtrasExistente = horasExtrasRepository.findById(id)
            .orElseThrow { RuntimeException("Horas extras não encontrada com id: $id") }

        val usuario = dto.usuarioId?.let {
            usuariosRepository.findById(it)
                .orElseThrow { UsuarioNotFoundException("Usuário não encontrado com id: $it") }
        } ?: horasExtrasExistente.usuario

        val horasExtrasAtualizada = horasExtrasExistente.copy(
            usuario = usuario,
            data = dto.data ?: horasExtrasExistente.data,
            horas = dto.horas ?: horasExtrasExistente.horas,
            motivo = dto.motivo ?: horasExtrasExistente.motivo,
            foiSolicitada = dto.foiSolicitado ?: horasExtrasExistente.foiSolicitada
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
    ): List<HorasExtras> {

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