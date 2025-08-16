package com.trackpoint.demo.Repository

import com.trackpoint.demo.Entity.Pontos
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PontosRepository : JpaRepository<Pontos, Int>
