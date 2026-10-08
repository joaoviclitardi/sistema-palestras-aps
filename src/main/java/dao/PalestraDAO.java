package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import model.Palestra;
import model.Professor;
import util.ErroSql;
import util.SistemaException;

/** Acesso à tabela palestra (usa JOIN com professor). */
public class PalestraDAO {

    /** Cada opção ordena por mais de um critério (ex.: data, horário e título). */
    public static final String[] ORDENS = {"Data, horário e título", "Título", "Professor e data", "Local e data"};
    private static final String[] SQL_ORDEM = {
        "p.data, p.horario, p.titulo",
        "p.titulo",
        "pr.nome, p.data, p.horario",
        "p.local, p.data, p.horario"
    };

    private static final String SELECT = "SELECT p.id_palestra, p.titulo, p.descricao, p.data, p.horario, p.local, "
            + "pr.id_professor, pr.nome AS nome_professor, pr.email AS email_professor "
            + "FROM palestra p JOIN professor pr ON pr.id_professor = p.id_professor ";

    public void inserir(Palestra p) throws SistemaException {
        String sql = "INSERT INTO palestra (titulo, descricao, data, horario, local, id_professor) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexaoDB.getConexao(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getTitulo());
            ps.setString(2, p.getDescricao());
            ps.setString(3, p.getData().toString()); // yyyy-MM-dd
            ps.setString(4, p.getHorario() + ":00");
            ps.setString(5, p.getLocal());
            ps.setInt(6, p.getProfessor().getIdProfessor());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw ErroSql.traduzir(e);
        }
    }

    public void atualizar(Palestra p) throws SistemaException {
        String sql = "UPDATE palestra SET titulo = ?, descricao = ?, data = ?, horario = ?, local = ?, "
                + "id_professor = ? WHERE id_palestra = ?";
        try (Connection con = ConexaoDB.getConexao(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getTitulo());
            ps.setString(2, p.getDescricao());
            ps.setString(3, p.getData().toString());
            ps.setString(4, p.getHorario() + ":00");
            ps.setString(5, p.getLocal());
            ps.setInt(6, p.getProfessor().getIdProfessor());
            ps.setInt(7, p.getIdPalestra());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw ErroSql.traduzir(e);
        }
    }

    public void excluir(int idPalestra) throws SistemaException {
        String sql = "DELETE FROM palestra WHERE id_palestra = ?";
        try (Connection con = ConexaoDB.getConexao(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPalestra);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw ErroSql.traduzir(e);
        }
    }

    public Palestra buscarPorId(int idPalestra) throws SistemaException {
        String sql = SELECT + "WHERE p.id_palestra = ?";
        try (Connection con = ConexaoDB.getConexao(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPalestra);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return montar(rs);
            }
            return null;
        } catch (SQLException e) {
            throw ErroSql.traduzir(e);
        }
    }

    /**
     * Lista palestras filtrando pelo texto (título, descrição, local ou professor) e,
     * se idProfessor for maior que 0, pelo professor.
     */
    public List<Palestra> listar(String busca, int idProfessor, int ordem) throws SistemaException {
        String sql = SELECT + "WHERE (p.titulo LIKE ? OR p.descricao LIKE ? OR p.local LIKE ? OR pr.nome LIKE ?) ";
        if (idProfessor > 0) {
            sql = sql + "AND p.id_professor = ? ";
        }
        sql = sql + "ORDER BY " + SQL_ORDEM[ordem];

        List<Palestra> lista = new ArrayList<>();
        try (Connection con = ConexaoDB.getConexao(); PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 1; i <= 4; i++) {
                ps.setString(i, "%" + busca + "%");
            }
            if (idProfessor > 0) {
                ps.setInt(5, idProfessor);
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

    /** Transforma a linha do resultado em um objeto Palestra (com o Professor dentro). */
    static Palestra montar(ResultSet rs) throws SQLException {
        Professor professor = new Professor(rs.getInt("id_professor"), rs.getString("nome_professor"),
                rs.getString("email_professor"));
        Palestra p = new Palestra();
        p.setIdPalestra(rs.getInt("id_palestra"));
        p.setTitulo(rs.getString("titulo"));
        p.setDescricao(rs.getString("descricao"));
        p.setData(LocalDate.parse(rs.getString("data")));
        p.setHorario(rs.getString("horario").substring(0, 5)); // "19:30:00" vira "19:30"
        p.setLocal(rs.getString("local"));
        p.setProfessor(professor);
        return p;
    }
}
