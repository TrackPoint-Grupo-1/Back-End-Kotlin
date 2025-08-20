package com.trackpoint.demo.Service

import com.trackpoint.demo.DTO.ProjetoCreateRequestDTO
import com.trackpoint.demo.DTO.ProjetoResponseDTO
import com.trackpoint.demo.Entity.Projeto
import com.trackpoint.demo.Enum.CargosEnum
import com.trackpoint.demo.Enum.StatusProjeto
import com.trackpoint.demo.Exeptions.*
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

    fun buscarProjetoPorNome( nome: String): List<ProjetoResponseDTO> {
        val projetos = projetoRepository.findByNomeContainingIgnoreCase(nome)
        if (projetos.isEmpty()) {
            throw ProjetoNaoEncontradoException("Nenhum projeto encontrado com o nome '$nome'")
        }
        return projetos.map { ProjetoResponseDTO.fromEntity(it) }
    }

    fun buscarProjetosPorFuncionario(nome: String): List<Projeto> {
        val projetosUsuarios = projetoRepository.findByUsuarios_NomeContainingIgnoreCase(nome)
        val projetosGerente = projetoRepository.findByGerentes_NomeContainingIgnoreCase(nome)

        val projetos = (projetosUsuarios + projetosGerente).distinct()

        if (projetos.isEmpty()) {
            throw FuncionarioNaoEncontradoException("Nenhum projeto encontrado para o funcionário/gerente com nome: $nome")
        }

        return projetos
    }

    fun atualizarStatusProjeto(id: Int, novoStatus: String): ProjetoResponseDTO {
        val statusValido = try {
            StatusProjeto.valueOf(novoStatus.uppercase())
        } catch (e: IllegalArgumentException) {
            throw StatusInvalidoException("Status '$novoStatus' inválido. Valores permitidos: ${StatusProjeto.entries.joinToString()}")
        }

        val projeto = projetoRepository.findById(id)
            .orElseThrow { ProjetoNaoEncontradoException("Projeto com ID $id não encontrado") }

        projeto.status = statusValido

        val projetoAtualizado = projetoRepository.save(projeto)
        return ProjetoResponseDTO.fromEntity(projetoAtualizado)
    }
}