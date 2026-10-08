package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Professor;
import util.ErroSql;
import util.SistemaException;

/** Acesso à tabela professor. */
public class ProfessorDAO {

    public static final String[] ORDENS = {"Nome", "E-mail", "Código"};
    private static final String[] SQL_ORDEM = {"nome", "email", "id_professor"};

    public void inserir(Professor professor) throws SistemaException {
        String sql = "INSERT INTO professor (nome, email) VALUES (?, ?)";
        try (Connection con = ConexaoDB.getConexao(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, professor.getNome());
            ps.setString(2, professor.getEmail());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw ErroSql.traduzir(e);
        }
    }

    public void atualizar(Professor professor) throws SistemaException {
        String sql = "UPDATE professor SET nome = ?, email = ? WHERE id_professor = ?";
        try (Connection con = ConexaoDB.getConexao(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, professor.getNome());
            ps.setString(2, professor.getEmail());
            ps.setInt(3, professor.getIdProfessor());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw ErroSql.traduzir(e);
        }
    }

    public void excluir(int idProfessor) throws SistemaException {
        String sql = "DELETE FROM professor WHERE id_professor = ?";
        try (Connection con = ConexaoDB.getConexao(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProfessor);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw ErroSql.traduzir(e);
        }
    }

    public Professor buscarPorId(int idProfessor) throws SistemaException {
        String sql = "SELECT id_professor, nome, email FROM professor WHERE id_professor = ?";
        try (Connection con = ConexaoDB.getConexao(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProfessor);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Professor(rs.getInt("id_professor"), rs.getString("nome"), rs.getString("email"));
            }
            return null;
        } catch (SQLException e) {
            throw ErroSql.traduzir(e);
        }
    }

    /** Lista professores filtrando por nome ou e-mail. */
    public List<Professor> listar(String busca, int ordem) throws SistemaException {
        String sql = "SELECT id_professor, nome, email FROM professor "
                + "WHERE nome LIKE ? OR email LIKE ? ORDER BY " + SQL_ORDEM[ordem];
        List<Professor> lista = new ArrayList<>();
        try (Connection con = ConexaoDB.getConexao(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + busca + "%");
            ps.setString(2, "%" + busca + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Professor(rs.getInt("id_professor"), rs.getString("nome"), rs.getString("email")));
            }
        } catch (SQLException e) {
            throw ErroSql.traduzir(e);
        }
        return lista;
    }
}
