package com.trackpoint.demo.Service

import com.trackpoint.demo.DTO.ProjetoCreateRequestDTO
import com.trackpoint.demo.DTO.ProjetoResponseDTO
import com.trackpoint.demo.Entity.Projeto
import com.trackpoint.demo.Enum.CargosEnum
import com.trackpoint.demo.Enum.StatusProjeto
import com.trackpoint.demo.Exeptions.GerenteComoUsuarioException
import com.trackpoint.demo.Exeptions.GerenteInvalidoException
import com.trackpoint.demo.Exeptions.ProjetoNaoEncontradoException
import com.trackpoint.demo.Exeptions.ProjetoNomeDuplicadoException
import com.trackpoint.demo.Repository.ProjetoRepository
import com.trackpoint.demo.Repository.UsuariosRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ProjetoService(
    private val projetoRepository: ProjetoRepository,
    private val usuarioRepository: UsuariosRepository
) {

    fun criarProjeto(dto: ProjetoCreateRequestDTO): ProjetoResponseDTO {
        // Verifica se já existe projeto com o mesmo nome
        if (projetoRepository.existsByNome(dto.nome)) {
            throw ProjetoNomeDuplicadoException("Já existe um projeto cadastrado com o nome '${dto.nome}'")
        }

        val gerentes = usuarioRepository.findAllById(dto.gerentesIds).toMutableList()
        if (gerentes.isEmpty()) throw GerenteInvalidoException("É necessário informar pelo menos um gerente válido")
        if (gerentes.any { it.cargo != CargosEnum.GERENTE })
            throw GerenteInvalidoException("Todos os gerentes informados devem ter cargo de GERENTE")

        val usuarios = usuarioRepository.findAllById(dto.usuariosIds).toMutableList()

        val idsGerentes = gerentes.map { it.id }.toSet()
        val idsUsuarios = usuarios.map { it.id }.toSet()
        val gerenteComoUsuario = idsGerentes.intersect(idsUsuarios)
        if (gerenteComoUsuario.isNotEmpty()) {
            throw GerenteComoUsuarioException("Os seguintes gerentes não podem ser usuários do mesmo projeto: $gerenteComoUsuario")
        }

        val projeto = Projeto(
            nome = dto.nome,
            descricao = dto.descricao,
            gerentes = gerentes,
            usuarios = usuarios,
            previsaoEntrega = dto.previsaoEntrega,
            status = StatusProjeto.ANDAMENTO,
            criadoEm = LocalDateTime.now()
        )

        return ProjetoResponseDTO.fromEntity(projetoRepository.save(projeto))
    }

    fun listarProjetos(): List<ProjetoResponseDTO> =
        projetoRepository.findAll().map { ProjetoResponseDTO.fromEntity(it) }

    fun buscarProjetoPorId( id: Int): ProjetoResponseDTO {
        val projeto = projetoRepository.findById(id)
            .orElseThrow { ProjetoNaoEncontradoException("Projeto com ID $id não encontrado") }
        return ProjetoResponseDTO.fromEntity(projeto)
    }
}