package controller;

import java.util.List;

import dao.AlunoDAO;
import model.Aluno;
import model.Curso;
import util.SistemaException;
import util.Validador;

/** Valida os dados de Aluno, chama o DAO e faz a identificação da Área do Aluno. */
public class AlunoController {

    private AlunoDAO dao = new AlunoDAO();

    public void cadastrar(String nome, String ra, String email, Curso curso) throws SistemaException {
        if (curso == null) {
            throw new SistemaException("Selecione o curso.");
        }
        Aluno aluno = new Aluno(0, Validador.nome(nome), Validador.ra(ra), Validador.email(email), curso);
        dao.inserir(aluno);
    }

    public void alterar(int id, String nome, String ra, String email, Curso curso) throws SistemaException {
        if (curso == null) {
            throw new SistemaException("Selecione o curso.");
        }
        Aluno aluno = new Aluno(id, Validador.nome(nome), Validador.ra(ra), Validador.email(email), curso);
        dao.atualizar(aluno);
    }

    public void excluir(int id) throws SistemaException {
        dao.excluir(id);
    }

    public Aluno buscarPorId(int id) throws SistemaException {
        return dao.buscarPorId(id);
    }

    public List<Aluno> listar(String busca, int idCurso, int ordem) throws SistemaException {
        return dao.listar(busca, idCurso, ordem);
    }

    /** Identifica o aluno pelo nome completo. Se não achar, avisa o usuário. */
    public Aluno identificar(String nome) throws SistemaException {
        String nomeDigitado = Validador.obrigatorio(nome, "Nome");
        List<Aluno> encontrados = dao.buscarPorNome(nomeDigitado);
        if (encontrados.isEmpty()) {
            throw new SistemaException("Aluno não encontrado. Digite o nome completo, como está no cadastro.");
        }
        if (encontrados.size() > 1) {
            throw new SistemaException("Há mais de um aluno com esse nome. Procure o coordenador.");
        }
        return encontrados.get(0);
    }
}
