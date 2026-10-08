package controller;

import java.util.List;

import dao.ProfessorDAO;
import model.Professor;
import util.SistemaException;
import util.Validador;

/** Valida os dados de Professor e chama o DAO. */
public class ProfessorController {

    private ProfessorDAO dao = new ProfessorDAO();

    public void cadastrar(String nome, String email) throws SistemaException {
        Professor professor = new Professor(0, Validador.nome(nome), Validador.email(email));
        dao.inserir(professor);
    }

    public void alterar(int id, String nome, String email) throws SistemaException {
        Professor professor = new Professor(id, Validador.nome(nome), Validador.email(email));
        dao.atualizar(professor);
    }

    public void excluir(int id) throws SistemaException {
        dao.excluir(id);
    }

    public Professor buscarPorId(int id) throws SistemaException {
        return dao.buscarPorId(id);
    }

    public List<Professor> listar(String busca, int ordem) throws SistemaException {
        return dao.listar(busca, ordem);
    }
}
