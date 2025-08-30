package com.trackpoint.demo.Repository

import com.trackpoint.demo.Entity.Pontos
import com.trackpoint.demo.Entity.Usuarios
import com.trackpoint.demo.Enum.TipoPonto
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.time.LocalDateTime

@Repository
interface PontosRepository : JpaRepository<Pontos, Int>{
    fun findByUsuarioAndHorarioBetweenOrderByHorarioAsc(
        usuario: Usuarios,
        inicio: LocalDateTime,
        fim: LocalDateTime
    ): List<Pontos>

    fun findFirstByUsuarioAndTipoNotOrderByHorarioDesc(usuario: Usuarios, tipo: TipoPonto): Pontos?

    @Query("""
    SELECT p FROM Pontos p 
    WHERE p.usuario = :usuario 
      AND p.turno IN (
          SELECT p2.turno FROM Pontos p2 
          GROUP BY p2.turno 
          HAVING SUM(CASE WHEN p2.tipo = 'ENTRADA' THEN 1 ELSE 0 END) >
                 SUM(CASE WHEN p2.tipo = 'SAIDA' THEN 1 ELSE 0 END)
      )
    ORDER BY p.horario DESC
""")
    fun findLastOpenTurn(usuario: Usuarios): Pontos?

    fun findByHorarioBetween(inicio: LocalDateTime, fim: LocalDateTime): List<Pontos>

    fun findByUsuarioIdAndHorarioBetween(
        usuarioId: Long,
        inicio: LocalDateTime,
        fim: LocalDateTime
    ): List<Pontos>

    fun findByUsuario(usuario: Usuarios): List<Pontos>

}
