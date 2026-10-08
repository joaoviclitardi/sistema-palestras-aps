package util;

/**
 * Exceção do sistema. É usada para avisar o usuário de qualquer problema
 * previsto: campo vazio, e-mail inválido, registro duplicado, exclusão
 * bloqueada ou falha no banco. A mensagem já vem pronta para ser mostrada na tela.
 */
public class SistemaException extends Exception {

    public SistemaException(String mensagem) {
        super(mensagem);
    }
}
