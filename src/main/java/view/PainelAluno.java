package view;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import model.Aluno;

/** Janela do aluno: consulta as palestras, se inscreve e vê as próprias inscrições. */
public class PainelAluno extends JFrame {

    public PainelAluno(Aluno aluno) {
        setTitle("Sistema de Palestras - Aluno: " + aluno.getNome());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1000, 520);
        setLocationRelativeTo(null);

        PainelInscricoes minhasInscricoes = new PainelInscricoes(aluno.getIdAluno());
        JTabbedPane abas = new JTabbedPane();
        abas.addTab("Palestras", new PainelPalestrasAluno(aluno, minhasInscricoes));
        abas.addTab("Minhas inscrições", minhasInscricoes);
        add(abas);
    }
}
