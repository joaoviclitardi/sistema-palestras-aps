package controller;

import java.util.List;

import dao.PalestraDAO;
import model.Palestra;
import model.Professor;
import util.SistemaException;
import util.Validador;

/** Valida os dados de Palestra e chama o DAO. */
public class PalestraController {

    private PalestraDAO dao = new PalestraDAO();

    public void cadastrar(String titulo, String descricao, String data, String horario, String local,
                          Professor professor) throws SistemaException {
        Palestra palestra = montar(titulo, descricao, data, horario, local, professor);
        dao.inserir(palestra);
    }

    public void alterar(int id, String titulo, String descricao, String data, String horario, String local,
                        Professor professor) throws SistemaException {
        Palestra palestra = montar(titulo, descricao, data, horario, local, professor);
        palestra.setIdPalestra(id);
        dao.atualizar(palestra);
    }

    /** Valida todos os campos e monta o objeto Palestra. */
    private Palestra montar(String titulo, String descricao, String data, String horario, String local,
                            Professor professor) throws SistemaException {
        if (professor == null) {
            throw new SistemaException("Selecione o professor responsável.");
        }
        Palestra p = new Palestra();
        p.setTitulo(Validador.obrigatorio(titulo, "Título"));
        p.setDescricao(descricao == null ? "" : descricao.trim());
        p.setData(Validador.data(data));
        p.setHorario(Validador.horario(horario));
        p.setLocal(Validador.obrigatorio(local, "Local"));
        p.setProfessor(professor);
        return p;
    }

    public void excluir(int id) throws SistemaException {
        dao.excluir(id);
    }

    public Palestra buscarPorId(int id) throws SistemaException {
        return dao.buscarPorId(id);
    }

    public List<Palestra> listar(String busca, int idProfessor, int ordem) throws SistemaException {
        return dao.listar(busca, idProfessor, ordem);
    }
}
