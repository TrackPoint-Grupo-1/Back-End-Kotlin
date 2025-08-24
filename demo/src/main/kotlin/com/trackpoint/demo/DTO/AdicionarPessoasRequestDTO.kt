package com.trackpoint.demo.DTO

data class AdicionarPessoasRequestDTO(
    val gerentesIds: List<Int> = emptyList(),
    val usuariosIds: List<Int> = emptyList()
)
