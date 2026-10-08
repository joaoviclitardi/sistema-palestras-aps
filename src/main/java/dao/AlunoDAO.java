package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Aluno;
import model.Curso;
import util.ErroSql;
import util.SistemaException;

/** Acesso à tabela aluno (usa JOIN com curso para trazer o nome do curso). */
public class AlunoDAO {

    /** Cada opção ordena por mais de um critério (ex.: curso e depois nome). */
    public static final String[] ORDENS = {"Nome", "Curso e nome", "RA", "E-mail"};
    private static final String[] SQL_ORDEM = {"a.nome", "c.nome_curso, a.nome", "a.ra", "a.email"};

    private static final String SELECT = "SELECT a.id_aluno, a.nome, a.ra, a.email, c.id_curso, c.nome_curso "
            + "FROM aluno a JOIN curso c ON c.id_curso = a.id_curso ";

    public void inserir(Aluno aluno) throws SistemaException {
        String sql = "INSERT INTO aluno (nome, ra, email, id_curso) VALUES (?, ?, ?, ?)";
        try (Connection con = ConexaoDB.getConexao(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, aluno.getNome());
            ps.setString(2, aluno.getRa());
            ps.setString(3, aluno.getEmail());
            ps.setInt(4, aluno.getCurso().getIdCurso());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw ErroSql.traduzir(e);
        }
    }

    public void atualizar(Aluno aluno) throws SistemaException {
        String sql = "UPDATE aluno SET nome = ?, ra = ?, email = ?, id_curso = ? WHERE id_aluno = ?";
        try (Connection con = ConexaoDB.getConexao(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, aluno.getNome());
            ps.setString(2, aluno.getRa());
            ps.setString(3, aluno.getEmail());
            ps.setInt(4, aluno.getCurso().getIdCurso());
            ps.setInt(5, aluno.getIdAluno());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw ErroSql.traduzir(e);
        }
    }

    public void excluir(int idAluno) throws SistemaException {
        String sql = "DELETE FROM aluno WHERE id_aluno = ?";
        try (Connection con = ConexaoDB.getConexao(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idAluno);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw ErroSql.traduzir(e);
        }
    }

    public Aluno buscarPorId(int idAluno) throws SistemaException {
        String sql = SELECT + "WHERE a.id_aluno = ?";
        try (Connection con = ConexaoDB.getConexao(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idAluno);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return montar(rs);
            }
            return null;
        } catch (SQLException e) {
            throw ErroSql.traduzir(e);
        }
    }

    /** Busca pelo nome completo (usado na identificação da Área do Aluno). */
    public List<Aluno> buscarPorNome(String nome) throws SistemaException {
        String sql = SELECT + "WHERE a.nome = ?";
        List<Aluno> lista = new ArrayList<>();
        try (Connection con = ConexaoDB.getConexao(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nome);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(montar(rs));
            }
        } catch (SQLException e) {
            throw ErroSql.traduzir(e);
        }
        return lista;
    }

    /**
     * Lista alunos filtrando pelo texto (nome, RA ou e-mail) e, se idCurso for maior que 0,
     * pelo curso. A ordenação usa uma das opções de ORDENS.
     */
    public List<Aluno> listar(String busca, int idCurso, int ordem) throws SistemaException {
        String sql = SELECT + "WHERE (a.nome LIKE ? OR a.ra LIKE ? OR a.email LIKE ?) ";
        if (idCurso > 0) {
            sql = sql + "AND a.id_curso = ? ";
        }
        sql = sql + "ORDER BY " + SQL_ORDEM[ordem];

        List<Aluno> lista = new ArrayList<>();
        try (Connection con = ConexaoDB.getConexao(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + busca + "%");
            ps.setString(2, "%" + busca + "%");
            ps.setString(3, "%" + busca + "%");
            if (idCurso > 0) {
                ps.setInt(4, idCurso);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(montar(rs));
            }
        } catch (SQLException e) {
            throw ErroSql.traduzir(e);
        }
        return lista;
    }

    /** Transforma a linha do resultado em um objeto Aluno. */
    private Aluno montar(ResultSet rs) throws SQLException {
        Curso curso = new Curso(rs.getInt("id_curso"), rs.getString("nome_curso"));
        return new Aluno(rs.getInt("id_aluno"), rs.getString("nome"), rs.getString("ra"),
                rs.getString("email"), curso);
    }
}
