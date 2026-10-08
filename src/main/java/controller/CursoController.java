package controller;

import java.util.List;

import dao.CursoDAO;
import model.Curso;
import util.SistemaException;
import util.Validador;

/** Valida os dados de Curso e chama o DAO. A tela só conversa com o controller. */
public class CursoController {

    private CursoDAO dao = new CursoDAO();

    public void cadastrar(String nome) throws SistemaException {
        Curso curso = new Curso();
        curso.setNomeCurso(Validador.obrigatorio(nome, "Nome do curso"));
        dao.inserir(curso);
    }

    public void alterar(int id, String nome) throws SistemaException {
        Curso curso = new Curso(id, Validador.obrigatorio(nome, "Nome do curso"));
        dao.atualizar(curso);
    }

    public void excluir(int id) throws SistemaException {
        dao.excluir(id);
    }

    public Curso buscarPorId(int id) throws SistemaException {
        return dao.buscarPorId(id);
    }

    public List<Curso> listar(String busca, int ordem) throws SistemaException {
        return dao.listar(busca, ordem);
    }
}
