package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import util.SistemaException;

/**
 * Cria a conexão com o MySQL. Se a senha do seu MySQL for diferente,
 * altere a constante SENHA abaixo.
 */
public class ConexaoDB {

    private static final String URL = "jdbc:mysql://localhost:3306/palestras_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo";
    private static final String USUARIO = "root";
    private static final String SENHA = "root";

    public static Connection getConexao() throws SistemaException {
        try {
            return DriverManager.getConnection(URL, USUARIO, SENHA);
        } catch (SQLException e) {
            throw new SistemaException("Não foi possível conectar ao MySQL. Verifique se ele está ligado, "
                    + "se o banco palestras_db foi criado e se usuário e senha estão corretos em ConexaoDB.java.");
        }
    }
}
