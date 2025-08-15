package com.trackpoint.demo.Service

import com.trackpoint.demo.DTO.HorasExtrasCreateRequestDTO
import com.trackpoint.demo.DTO.HorasExtrasUpdateRequestDTO
import com.trackpoint.demo.Entity.HorasExtras
import com.trackpoint.demo.Exeptions.InvalidDateFormatException
import com.trackpoint.demo.Exeptions.NenhumaHoraExtraEncontradaException
import com.trackpoint.demo.Exeptions.UsuarioNotFoundException
import com.trackpoint.demo.Repository.HorasExtrasRepository
import com.trackpoint.demo.Repository.UsuariosRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Service
class HorasExtrasService(
    private val horasExtrasRepository: HorasExtrasRepository,
    private val usuariosRepository: UsuariosRepository
) {

    fun criarHorasExtras(dto: HorasExtrasCreateRequestDTO): HorasExtras {
        val usuario = usuariosRepository.findById(dto.usuarioId)
            .orElseThrow { UsuarioNotFoundException("Usuário não encontrado com id: ${dto.usuarioId}") }

        val horasExtras = HorasExtras(
            usuario = usuario,
            data = dto.data,
            horas = dto.horas,
            motivo = dto.motivo,
            foiSolicitada = dto.foiSolicitado,
            criadoEm = LocalDate.now()
        )
        return horasExtrasRepository.save(horasExtras)
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

//    fun listarHorasPorUsuarioEntreDatasEStatus(
//        usuarioId: Int,
//        dataInicio: String,
//        dataFim: String,
//        foiSolicitada: Boolean
//    ): List<HorasExtras> {
//        val inicio = LocalDate.parse(dataInicio, formatter)
//        val fim = LocalDate.parse(dataFim, formatter)
//        return horasExtrasRepository.findByUsuarioIdAndDataBetweenAndFoiSolicitada(usuarioId, inicio, fim, foiSolicitada)
//    }

}