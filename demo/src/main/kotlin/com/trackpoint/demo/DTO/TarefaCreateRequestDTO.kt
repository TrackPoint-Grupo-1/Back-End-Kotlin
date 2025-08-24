package com.trackpoint.demo.DTO

import jakarta.validation.constraints.*

class TarefaCreateRequestDTO(

    @field:NotNull(message = "O ID do projeto é obrigatório")
    @field:Positive(message = "O ID do projeto deve ser positivo")
    val projeto: Int,

    @field:NotNull(message = "O ID do usuário responsável é obrigatório")
    @field:Positive(message = "O ID do usuário deve ser positivo")
    val usuarios: Int,

    @field:NotBlank(message = "O nome da tarefa é obrigatório")
    @field:Size(min = 3, max = 100, message = "O nome da tarefa deve ter entre 3 e 100 caracteres")
    val nome: String,

    @field:NotBlank(message = "A descrição é obrigatória")
    @field:Size(min = 5, max = 500, message = "A descrição deve ter entre 5 e 500 caracteres")
    val descricao: String,

    @field:NotNull(message = "As horas estimadas são obrigatórias")
    @field:Positive(message = "As horas estimadas devem ser maiores que zero")
    @field:Max(value = 1000, message = "As horas estimadas não podem ultrapassar 1000 horas")
    val horasEstimadas: Int,
)
