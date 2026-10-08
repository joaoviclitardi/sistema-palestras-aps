import java.util.List;

import controller.AlunoController;
import controller.CursoController;
import controller.InscricaoController;
import controller.PalestraController;
import controller.ProfessorController;
import dao.AlunoDAO;
import dao.PalestraDAO;
import model.Aluno;
import model.Curso;
import model.Inscricao;
import model.Palestra;
import model.Professor;
import util.SistemaException;

/**
 * Testes automáticos simples. Antes de rodar, execute no MySQL o schema.sql e o dados_teste.sql.
 * Os testes criam registros com a marca "Teste" e apagam tudo no final.
 * Saída: uma linha PASSOU ou FALHOU para cada caso e o total no fim.
 */
public class TestesSistema {

    /** Trecho de código que pode lançar erro. */
    interface Acao {
        void executar() throws Exception;
    }

    static int total = 0;
    static int passou = 0;
    static StringBuilder json = new StringBuilder();

    static void verificar(String id, String descricao, boolean condicao) {
        total++;
        if (condicao) {
            passou++;
        }
        System.out.println((condicao ? "PASSOU  " : "FALHOU  ") + id + " - " + descricao);
        if (total > 1) {
            json.append(",\n");
        }
        json.append("  {\"id\": \"").append(id).append("\", \"descricao\": \"").append(descricao)
                .append("\", \"resultado\": \"").append(condicao ? "PASSOU" : "FALHOU").append("\"}");
    }

    /** Devolve a mensagem do SistemaException lançado, ou null se não houve erro. */
    static String erroDe(Acao acao) {
        try {
            acao.executar();
            return null;
        } catch (SistemaException e) {
            return e.getMessage();
        } catch (Exception e) {
            return "ERRO INESPERADO: " + e;
        }
    }

    static boolean recusa(Acao acao, String trechoDaMensagem) {
        String msg = erroDe(acao);
        return msg != null && msg.contains(trechoDaMensagem);
    }

    public static void main(String[] args) throws Exception {
        CursoController cursos = new CursoController();
        ProfessorController professores = new ProfessorController();
        AlunoController alunos = new AlunoController();
        PalestraController palestras = new PalestraController();
        InscricaoController inscricoes = new InscricaoController();

        long marca = System.currentTimeMillis() % 100000;
        String nomeCurso = "Curso Teste " + marca;
        String emailProf = "prof" + marca + "@teste.com";
        String raAluno = "TST" + marca;
        String emailAluno = "aluno" + marca + "@teste.com";

        try {
            // ---------------- Curso ----------------
            verificar("C01", "Cadastrar curso válido", erroDe(() -> cursos.cadastrar(nomeCurso)) == null);
            verificar("C02", "Recusar curso sem nome", recusa(() -> cursos.cadastrar("  "), "obrigatório"));
            verificar("C03", "Recusar curso com nome repetido", recusa(() -> cursos.cadastrar(nomeCurso), "Já existe"));
            Curso curso = cursos.listar(nomeCurso, 0).get(0);
            verificar("C04", "Buscar curso por parte do nome", cursos.listar("Curso Teste", 0).size() >= 1);
            verificar("C05", "Alterar curso", erroDe(() -> cursos.alterar(curso.getIdCurso(), nomeCurso + " B")) == null
                    && cursos.buscarPorId(curso.getIdCurso()).getNomeCurso().endsWith(" B"));

            // ---------------- Professor ----------------
            verificar("P01", "Cadastrar professor válido", erroDe(() -> professores.cadastrar("Professor Teste", emailProf)) == null);
            verificar("P02", "Recusar e-mail inválido", recusa(() -> professores.cadastrar("Professor Teste", "sem-arroba"), "E-mail inválido"));
            verificar("P03", "Recusar e-mail repetido", recusa(() -> professores.cadastrar("Outro Professor", emailProf), "Já existe"));
            verificar("P04", "Recusar nome com número", recusa(() -> professores.cadastrar("Prof 123", "x" + emailProf), "apenas letras"));
            Professor prof = professores.listar(emailProf, 0).get(0);
            verificar("P05", "Alterar professor", erroDe(() -> professores.alterar(prof.getIdProfessor(), "Professor Teste Alterado", emailProf)) == null
                    && professores.buscarPorId(prof.getIdProfessor()).getNome().endsWith("Alterado"));

            // ---------------- Aluno ----------------
            verificar("A01", "Cadastrar aluno válido", erroDe(() -> alunos.cadastrar("Aluno Teste", raAluno, emailAluno, curso)) == null);
            verificar("A02", "Recusar RA inválido", recusa(() -> alunos.cadastrar("Aluno Dois", "12", "a" + emailAluno, curso), "RA inválido"));
            verificar("A03", "Recusar RA repetido", recusa(() -> alunos.cadastrar("Aluno Dois", raAluno, "b" + emailAluno, curso), "RA"));
            verificar("A04", "Recusar e-mail repetido", recusa(() -> alunos.cadastrar("Aluno Dois", "OUT" + marca, emailAluno, curso), "e-mail"));
            verificar("A05", "Recusar aluno sem curso", recusa(() -> alunos.cadastrar("Aluno Dois", "SEM" + marca, "c" + emailAluno, null), "curso"));
            Aluno aluno = alunos.listar(raAluno, 0, 0).get(0);
            verificar("A06", "Alterar aluno", erroDe(() -> alunos.alterar(aluno.getIdAluno(), "Aluno Teste Alterado", raAluno, emailAluno, curso)) == null
                    && alunos.buscarPorId(aluno.getIdAluno()).getNome().endsWith("Alterado"));
            verificar("A07", "Identificar aluno pelo nome",
                    alunos.identificar("Aluno Teste Alterado").getIdAluno() == aluno.getIdAluno());
            verificar("A08", "Recusar nome inexistente", recusa(() -> alunos.identificar("Fulano Que Nao Existe"), "não encontrado"));
            verificar("A09", "Filtrar alunos por curso", alunos.listar("", curso.getIdCurso(), 0).size() == 1);
            verificar("A10", "Listar todos os alunos (filtro 0)", alunos.listar("", 0, 0).size() >= 30);
            List<Aluno> porNome = alunos.listar("", 0, 0);
            boolean ordenado = true;
            for (int i = 1; i < porNome.size(); i++) {
                if (porNome.get(i - 1).getNome().compareToIgnoreCase(porNome.get(i).getNome()) > 0) {
                    ordenado = false;
                }
            }
            verificar("A11", "Ordenar alunos por nome", ordenado);
            verificar("A12", "Todas as ordenações de aluno funcionam", ordensFuncionam(AlunoDAO.ORDENS.length, o -> alunos.listar("", 0, o)));
            verificar("A13", "Recusar excluir curso que tem aluno", recusa(() -> cursos.excluir(curso.getIdCurso()), "ligados"));

            // ---------------- Palestra ----------------
            verificar("L01", "Cadastrar palestra válida", erroDe(() -> palestras.cadastrar("Palestra Teste", "Descrição", "10/12/2026", "19:30", "Sala 1", prof)) == null);
            verificar("L02", "Recusar data inexistente (31/02)", recusa(() -> palestras.cadastrar("X Teste", "", "31/02/2026", "19:30", "Sala 1", prof), "Data inválida"));
            verificar("L03", "Recusar horário inválido (25:00)", recusa(() -> palestras.cadastrar("X Teste", "", "10/12/2026", "25:00", "Sala 1", prof), "Horário inválido"));
            verificar("L04", "Recusar palestra sem professor", recusa(() -> palestras.cadastrar("X Teste", "", "10/12/2026", "19:30", "Sala 1", null), "professor"));
            verificar("L05", "Recusar palestra sem título", recusa(() -> palestras.cadastrar(" ", "", "10/12/2026", "19:30", "Sala 1", prof), "obrigatório"));
            Palestra palestra = palestras.listar("Palestra Teste", 0, 0).get(0);
            verificar("L06", "Palestra guarda data e horário corretos",
                    palestra.getData().toString().equals("2026-12-10") && palestra.getHorario().equals("19:30"));
            verificar("L07", "Alterar palestra", erroDe(() -> palestras.alterar(palestra.getIdPalestra(), "Palestra Teste B", "Nova", "11/12/2026", "20:00", "Sala 2", prof)) == null
                    && palestras.buscarPorId(palestra.getIdPalestra()).getLocal().equals("Sala 2"));
            verificar("L08", "Filtrar palestras por professor", palestras.listar("", prof.getIdProfessor(), 0).size() == 1);
            verificar("L09", "Buscar palestra por título", palestras.listar("Teste B", 0, 0).size() == 1);
            List<Palestra> porData = palestras.listar("", 0, 0);
            boolean dataOrdenada = true;
            for (int i = 1; i < porData.size(); i++) {
                if (porData.get(i - 1).getData().isAfter(porData.get(i).getData())) {
                    dataOrdenada = false;
                }
            }
            verificar("L10", "Ordenar palestras por data", dataOrdenada);
            verificar("L11", "Todas as ordenações de palestra funcionam", ordensFuncionam(PalestraDAO.ORDENS.length, o -> palestras.listar("", 0, o)));

            // ---------------- Inscrição ----------------
            Palestra palestraAtual = palestras.buscarPorId(palestra.getIdPalestra());
            verificar("I01", "Inscrever aluno em palestra", erroDe(() -> inscricoes.inscrever(aluno, palestraAtual)) == null);
            verificar("I02", "Recusar inscrição repetida", recusa(() -> inscricoes.inscrever(aluno, palestraAtual), "já está inscrito"));
            verificar("I03", "Recusar inscrição sem palestra", recusa(() -> inscricoes.inscrever(aluno, null), "Selecione"));
            List<Inscricao> doAluno = inscricoes.listar("", aluno.getIdAluno(), 0);
            verificar("I04", "Listar inscrições só do aluno", doAluno.size() == 1
                    && doAluno.get(0).getPalestra().getIdPalestra() == palestra.getIdPalestra());
            verificar("I05", "Coordenador vê todas as inscrições", inscricoes.listar("", 0, 0).size() >= 49);
            verificar("I06", "Todas as ordenações de inscrição funcionam", ordensFuncionam(dao.InscricaoDAO.ORDENS.length, o -> inscricoes.listar("", 0, o)));
            verificar("I07", "Recusar excluir palestra com inscrição", recusa(() -> palestras.excluir(palestra.getIdPalestra()), "ligados"));
            verificar("I08", "Recusar excluir aluno com inscrição", recusa(() -> alunos.excluir(aluno.getIdAluno()), "ligados"));
            verificar("I09", "Recusar excluir professor com palestra", recusa(() -> professores.excluir(prof.getIdProfessor()), "ligados"));

            // ---------------- Exclusões ----------------
            verificar("E01", "Excluir inscrição", erroDe(() -> inscricoes.excluir(doAluno.get(0).getIdInscricao())) == null
                    && inscricoes.listar("", aluno.getIdAluno(), 0).isEmpty());
            verificar("E02", "Excluir palestra sem inscrição", erroDe(() -> palestras.excluir(palestra.getIdPalestra())) == null);
            verificar("E03", "Excluir professor sem palestra", erroDe(() -> professores.excluir(prof.getIdProfessor())) == null);
            verificar("E04", "Excluir aluno sem inscrição", erroDe(() -> alunos.excluir(aluno.getIdAluno())) == null);
            verificar("E05", "Excluir curso sem aluno", erroDe(() -> cursos.excluir(curso.getIdCurso())) == null);
        } catch (Exception e) {
            verificar("ERRO", "Teste interrompido: " + e, false);
        }

        System.out.println();
        System.out.println("Resultado: " + passou + " de " + total + " testes passaram.");
        if (args.length > 0) {
            java.nio.file.Files.writeString(java.nio.file.Path.of(args[0]),
                    "{\n \"total\": " + total + ",\n \"passou\": " + passou + ",\n \"casos\": [\n" + json + "\n ]\n}\n");
        }
        System.exit(passou == total ? 0 : 1);
    }

    interface Consulta {
        List<?> listar(int ordem) throws Exception;
    }

    /** Testa se cada opção de ordenação roda sem erro de SQL. */
    static boolean ordensFuncionam(int quantidade, Consulta consulta) {
        for (int i = 0; i < quantidade; i++) {
            try {
                consulta.listar(i);
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }
}
