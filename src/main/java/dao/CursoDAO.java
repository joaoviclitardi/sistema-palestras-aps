package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Curso;
import util.ErroSql;
import util.SistemaException;

/** Acesso à tabela curso (todo o SQL de Curso fica aqui). */
public class CursoDAO {

    /** Opções de ordenação mostradas na tela. */
    public static final String[] ORDENS = {"Nome do curso", "Código"};
    private static final String[] SQL_ORDEM = {"nome_curso", "id_curso"};

    public void inserir(Curso curso) throws SistemaException {
        String sql = "INSERT INTO curso (nome_curso) VALUES (?)";
        try (Connection con = ConexaoDB.getConexao(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, curso.getNomeCurso());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw ErroSql.traduzir(e);
        }
    }

    public void atualizar(Curso curso) throws SistemaException {
        String sql = "UPDATE curso SET nome_curso = ? WHERE id_curso = ?";
        try (Connection con = ConexaoDB.getConexao(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, curso.getNomeCurso());
            ps.setInt(2, curso.getIdCurso());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw ErroSql.traduzir(e);
        }
    }

    public void excluir(int idCurso) throws SistemaException {
        String sql = "DELETE FROM curso WHERE id_curso = ?";
        try (Connection con = ConexaoDB.getConexao(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCurso);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw ErroSql.traduzir(e);
        }
    }

    /** Devolve o curso ou null se não existir. */
    public Curso buscarPorId(int idCurso) throws SistemaException {
        String sql = "SELECT id_curso, nome_curso FROM curso WHERE id_curso = ?";
        try (Connection con = ConexaoDB.getConexao(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCurso);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Curso(rs.getInt("id_curso"), rs.getString("nome_curso"));
            }
            return null;
        } catch (SQLException e) {
            throw ErroSql.traduzir(e);
        }
    }

    /** Lista os cursos filtrando pelo texto digitado e ordenando pela opção escolhida. */
    public List<Curso> listar(String busca, int ordem) throws SistemaException {
        String sql = "SELECT id_curso, nome_curso FROM curso WHERE nome_curso LIKE ? ORDER BY " + SQL_ORDEM[ordem];
        List<Curso> lista = new ArrayList<>();
        try (Connection con = ConexaoDB.getConexao(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + busca + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Curso(rs.getInt("id_curso"), rs.getString("nome_curso")));
            }
        } catch (SQLException e) {
            throw ErroSql.traduzir(e);
        }
        return lista;
    }
}
