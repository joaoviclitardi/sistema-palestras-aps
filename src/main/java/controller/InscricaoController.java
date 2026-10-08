package controller;

import java.time.LocalDate;
import java.util.List;

import dao.InscricaoDAO;
import model.Aluno;
import model.Inscricao;
import model.Palestra;
import util.SistemaException;

/** Regras de Inscrição: o aluno não pode se inscrever duas vezes na mesma palestra. */
public class InscricaoController {

    private InscricaoDAO dao = new InscricaoDAO();

    public void inscrever(Aluno aluno, Palestra palestra) throws SistemaException {
        if (aluno == null || palestra == null) {
            throw new SistemaException("Selecione uma palestra para se inscrever.");
        }
        if (dao.existe(aluno.getIdAluno(), palestra.getIdPalestra())) {
            throw new SistemaException("Você já está inscrito na palestra \"" + palestra.getTitulo() + "\".");
        }
        Inscricao inscricao = new Inscricao();
        inscricao.setAluno(aluno);
        inscricao.setPalestra(palestra);
        inscricao.setDataInscricao(LocalDate.now());
        dao.inserir(inscricao);
    }

    /** Lista todas as inscrições (coordenador) ou só as de um aluno (idAluno maior que 0). */
    public List<Inscricao> listar(String busca, int idAluno, int ordem) throws SistemaException {
        return dao.listar(busca, idAluno, ordem);
    }

    public void excluir(int id) throws SistemaException {
        dao.excluir(id);
    }
}
