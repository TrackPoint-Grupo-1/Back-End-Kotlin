package com.trackpoint.demo.Entity

import com.trackpoint.demo.Enum.StatusProjeto
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "projetos")
class Projeto(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,
    @field:NotBlank(message = "O nome do projeto é obrigatório")
    val nome: String,
    @field:NotBlank(message = "A descrição é obrigatória")
    val descricao: String,
    @ManyToMany
    @JoinTable(
        name = "projeto_gerentes",
        joinColumns = [JoinColumn(name = "projeto_id")],
        inverseJoinColumns = [JoinColumn(name = "usuario_id")]
    )
    val gerentes: MutableList<Usuarios> = mutableListOf(),
    @ManyToMany
    @JoinTable(
        name = "projeto_usuarios",
        joinColumns = [JoinColumn(name = "projeto_id")],
        inverseJoinColumns = [JoinColumn(name = "usuario_id")]
    )
    val usuarios: MutableList<Usuarios> = mutableListOf(),
    @field:NotNull(message = "A previsão de entrega é obrigatória")
    val previsaoEntrega: LocalDate,
    @Enumerated(EnumType.STRING)
    val status : StatusProjeto,
    val criadoEm: LocalDateTime = LocalDateTime.now()
)