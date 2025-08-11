package com.trackpoint.demo.Controller

import com.trackpoint.demo.DTO.HorasExtrasRequestDTO
import com.trackpoint.demo.DTO.HorasExtrasResponseDTO
import com.trackpoint.demo.Service.HorasExtrasService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/horas-extras")
class HorasExtrasController (private val horasExtrasService: HorasExtrasService) {

    @PostMapping
    fun criarHorasExtras(@RequestBody @Valid dto: HorasExtrasRequestDTO): ResponseEntity<HorasExtrasResponseDTO> {
        val horasExtrasCriada = horasExtrasService.criarHorasExtras(dto)
        val responseDto = HorasExtrasResponseDTO(horasExtrasCriada)
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto)
    }

}