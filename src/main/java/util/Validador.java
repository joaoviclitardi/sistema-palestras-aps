package util;

import java.time.DateTimeException;
import java.time.LocalDate;

/**
 * Validação dos dados digitados pelo usuário.
 * Quando algo está errado, o método lança SistemaException com a mensagem.
 */
public class Validador {

    /** Verifica se o campo não está vazio e devolve o texto sem espaços nas pontas. */
    public static String obrigatorio(String valor, String nomeCampo) throws SistemaException {
        if (valor == null || valor.trim().isEmpty()) {
            throw new SistemaException("O campo \"" + nomeCampo + "\" é obrigatório.");
        }
        return valor.trim();
    }

    /** Verifica o formato do e-mail (texto@dominio.com) e devolve em minúsculas. */
    public static String email(String valor) throws SistemaException {
        String email = obrigatorio(valor, "E-mail");
        if (!email.matches("^[A-Za-z0-9._-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new SistemaException("E-mail inválido. Exemplo: nome@dominio.com");
        }
        return email.toLowerCase();
    }

    /** O RA deve ter de 5 a 20 letras ou números, sem espaços. */
    public static String ra(String valor) throws SistemaException {
        String ra = obrigatorio(valor, "RA").toUpperCase();
        if (!ra.matches("[A-Z0-9]{5,20}")) {
            throw new SistemaException("RA inválido. Use de 5 a 20 letras ou números, sem espaços.");
        }
        return ra;
    }

    /** O nome deve ter só letras e espaços. */
    public static String nome(String valor) throws SistemaException {
        String nome = obrigatorio(valor, "Nome");
        if (!nome.matches("[\\p{L} .'-]+")) {
            throw new SistemaException("O nome deve ter apenas letras e espaços.");
        }
        return nome;
    }

    /** Converte o texto dd/mm/aaaa em data. Datas que não existem (31/02) são recusadas. */
    public static LocalDate data(String valor) throws SistemaException {
        String texto = obrigatorio(valor, "Data");
        String[] partes = texto.split("/");
        if (partes.length != 3) {
            throw new SistemaException("Data inválida. Use o formato dd/mm/aaaa.");
        }
        try {
            int dia = Integer.parseInt(partes[0]);
            int mes = Integer.parseInt(partes[1]);
            int ano = Integer.parseInt(partes[2]);
            return LocalDate.of(ano, mes, dia);
        } catch (NumberFormatException | DateTimeException e) {
            throw new SistemaException("Data inválida. Use o formato dd/mm/aaaa com uma data que exista.");
        }
    }

    /** Verifica o horário no formato HH:mm (ex.: 19:30) e devolve com dois dígitos na hora. */
    public static String horario(String valor) throws SistemaException {
        String texto = obrigatorio(valor, "Horário");
        if (!texto.matches("\\d{1,2}:\\d{2}")) {
            throw new SistemaException("Horário inválido. Use o formato HH:mm (ex.: 19:30).");
        }
        String[] partes = texto.split(":");
        int hora = Integer.parseInt(partes[0]);
        int minuto = Integer.parseInt(partes[1]);
        if (hora > 23 || minuto > 59) {
            throw new SistemaException("Horário inválido. Use o formato HH:mm (ex.: 19:30).");
        }
        return String.format("%02d:%02d", hora, minuto);
    }

    /** Converte a data para o texto dd/mm/aaaa usado nas telas. */
    public static String formatarData(LocalDate data) {
        return String.format("%02d/%02d/%04d", data.getDayOfMonth(), data.getMonthValue(), data.getYear());
    }
}
