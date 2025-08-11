package com.trackpoint.demo.Service

import com.trackpoint.demo.DTO.HorasExtrasRequestDTO
import com.trackpoint.demo.Entity.HorasExtras
import com.trackpoint.demo.Repository.HorasExtrasRepository
import com.trackpoint.demo.Repository.UsuariosRepository
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class HorasExtrasService (private val usuariosRepository: UsuariosRepository, private val horasExtrasRepository: HorasExtrasRepository) {

    fun criarHorasExtras(dto: HorasExtrasRequestDTO): HorasExtras {
        val usuario = usuariosRepository.findById(dto.usuarioId)
            .orElseThrow { RuntimeException("Usuário não encontrado") }

        val horasExtras = HorasExtras(
            usuario = usuario,
            data = dto.data,
            horas = dto.horas,
            motivo = dto.motivo,
            status = dto.status,
            criadoEm = LocalDate.now()
        )
        return horasExtrasRepository.save(horasExtras)
    }


}