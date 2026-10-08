package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import model.Aluno;
import model.Curso;
import model.Inscricao;
import util.ErroSql;
import util.SistemaException;

/** Acesso à tabela inscricao, que liga aluno e palestra (JOIN com quatro tabelas). */
public class InscricaoDAO {

    public static final String[] ORDENS = {"Data da palestra e horário", "Palestra", "Aluno", "Data da inscrição"};
    private static final String[] SQL_ORDEM = {
        "p.data, p.horario, a.nome",
        "p.titulo, a.nome",
        "a.nome, p.data",
        "i.data_inscricao, a.nome"
    };

    private static final String SELECT = "SELECT i.id_inscricao, i.data_inscricao, "
            + "a.id_aluno, a.nome, a.ra, a.email, c.id_curso, c.nome_curso, "
            + "p.id_palestra, p.titulo, p.descricao, p.data, p.horario, p.local, "
            + "pr.id_professor, pr.nome AS nome_professor, pr.email AS email_professor "
            + "FROM inscricao i "
            + "JOIN aluno a ON a.id_aluno = i.id_aluno "
            + "JOIN curso c ON c.id_curso = a.id_curso "
            + "JOIN palestra p ON p.id_palestra = i.id_palestra "
            + "JOIN professor pr ON pr.id_professor = p.id_professor ";

    public void inserir(Inscricao inscricao) throws SistemaException {
        String sql = "INSERT INTO inscricao (id_aluno, id_palestra, data_inscricao) VALUES (?, ?, ?)";
        try (Connection con = ConexaoDB.getConexao(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, inscricao.getAluno().getIdAluno());
            ps.setInt(2, inscricao.getPalestra().getIdPalestra());
            ps.setString(3, inscricao.getDataInscricao().toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw ErroSql.traduzir(e);
        }
    }

    public void excluir(int idInscricao) throws SistemaException {
        String sql = "DELETE FROM inscricao WHERE id_inscricao = ?";
        try (Connection con = ConexaoDB.getConexao(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idInscricao);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw ErroSql.traduzir(e);
        }
    }

    /** Informa se o aluno já está inscrito na palestra. */
    public boolean existe(int idAluno, int idPalestra) throws SistemaException {
        String sql = "SELECT id_inscricao FROM inscricao WHERE id_aluno = ? AND id_palestra = ?";
        try (Connection con = ConexaoDB.getConexao(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idAluno);
            ps.setInt(2, idPalestra);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw ErroSql.traduzir(e);
        }
    }

    /**
     * Lista inscrições filtrando pelo texto (aluno, RA, palestra ou professor) e,
     * se idAluno for maior que 0, somente as inscrições daquele aluno.
     */
    public List<Inscricao> listar(String busca, int idAluno, int ordem) throws SistemaException {
        String sql = SELECT + "WHERE (a.nome LIKE ? OR a.ra LIKE ? OR p.titulo LIKE ? OR pr.nome LIKE ?) ";
        if (idAluno > 0) {
            sql = sql + "AND i.id_aluno = ? ";
        }
        sql = sql + "ORDER BY " + SQL_ORDEM[ordem];

        List<Inscricao> lista = new ArrayList<>();
        try (Connection con = ConexaoDB.getConexao(); PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 1; i <= 4; i++) {
                ps.setString(i, "%" + busca + "%");
            }
            if (idAluno > 0) {
                ps.setInt(5, idAluno);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Aluno aluno = new Aluno(rs.getInt("id_aluno"), rs.getString("nome"), rs.getString("ra"),
                        rs.getString("email"), new Curso(rs.getInt("id_curso"), rs.getString("nome_curso")));
                Inscricao inscricao = new Inscricao();
                inscricao.setIdInscricao(rs.getInt("id_inscricao"));
                inscricao.setAluno(aluno);
                inscricao.setPalestra(PalestraDAO.montar(rs));
                inscricao.setDataInscricao(LocalDate.parse(rs.getString("data_inscricao")));
                lista.add(inscricao);
            }
        } catch (SQLException e) {
            throw ErroSql.traduzir(e);
        }
        return lista;
    }
}
