package com.trackpoint.demo.Repository

import com.trackpoint.demo.Entity.HorasExtras
import com.trackpoint.demo.Entity.Usuarios
import org.springframework.data.jpa.repository.JpaRepository

interface HorasExtrasRepository : JpaRepository<HorasExtras, Int>