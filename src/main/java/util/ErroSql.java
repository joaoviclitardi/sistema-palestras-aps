package util;

import java.sql.SQLException;

/**
 * Transforma os erros do MySQL em mensagens fáceis de entender.
 * Códigos do MySQL usados:
 * 1451 = não pode excluir (existe registro ligado por chave estrangeira)
 * 1452 = chave estrangeira inexistente
 * 1062 = valor repetido em campo único
 */
public class ErroSql {

    public static SistemaException traduzir(SQLException e) {
        int codigo = e.getErrorCode();
        String texto = e.getMessage() == null ? "" : e.getMessage();

        if (codigo == 1451) {
            return new SistemaException("Não é possível excluir: existem registros ligados a este item.");
        }
        if (codigo == 1452) {
            return new SistemaException("Registro relacionado não encontrado.");
        }
        if (codigo == 1062) {
            if (texto.contains("uk_aluno_ra")) {
                return new SistemaException("Já existe um aluno com esse RA.");
            }
            if (texto.contains("uk_aluno_email")) {
                return new SistemaException("Já existe um aluno com esse e-mail.");
            }
            if (texto.contains("uk_professor_email")) {
                return new SistemaException("Já existe um professor com esse e-mail.");
            }
            if (texto.contains("uk_curso_nome")) {
                return new SistemaException("Já existe um curso com esse nome.");
            }
            if (texto.contains("uk_inscricao")) {
                return new SistemaException("O aluno já está inscrito nesta palestra.");
            }
            return new SistemaException("Já existe um registro com esses dados.");
        }
        return new SistemaException("Erro no banco de dados: " + texto);
    }
}
