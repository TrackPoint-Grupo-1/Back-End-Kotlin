package com.trackpoint.demo.DTO

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate
import java.time.LocalTime

class TotalHorasDTO(
    var listaHoras: List<PontosResponseDTO>,
    var horasTotal: RankingHorasExtrasDTO,
)