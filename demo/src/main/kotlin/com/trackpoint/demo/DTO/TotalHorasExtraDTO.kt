package com.trackpoint.demo.DTO

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate
import java.time.LocalTime

class TotalHorasExtraDTO(
    var listaHoras: List<SolicitacaoHorasExtrasResponseDTO>,
    var horasTotal: RankingHorasExtrasDTO,
)