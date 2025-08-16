package com.trackpoint.demo.Repository

import com.trackpoint.demo.Entity.SolicitarAjuste
import com.trackpoint.demo.Enum.StatusSolicitacao
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SolicitarAjusteRepository : JpaRepository<SolicitarAjuste, Int> {

    fun findByUsuarioId(usuarioId: Int): List<SolicitarAjuste>

    fun findByStatus(status: StatusSolicitacao): List<SolicitarAjuste>

    fun findByUsuarioIdAndStatus(usuarioId: Int, status: StatusSolicitacao): List<SolicitarAjuste>
}