package com.trackpoint.demo.Repository

import com.trackpoint.demo.Entity.Usuarios
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UsuariosRepository : JpaRepository<Usuarios, Int> {

    override fun findAll(): List<Usuarios>
    override fun findById(id: Int): Optional<Usuarios>
    fun save(usuario: Usuarios): Usuarios
    override fun deleteById(id: Int)
    fun existsByEmail(email: String): Boolean
    fun findByLogadoTrue(): List<Usuarios>

}