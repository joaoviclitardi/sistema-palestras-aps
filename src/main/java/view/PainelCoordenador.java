package view;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

/** Janela do coordenador: uma aba para cada cadastro. */
public class PainelCoordenador extends JFrame {

    public PainelCoordenador() {
        setTitle("Sistema de Palestras - Coordenador");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1000, 560);
        setLocationRelativeTo(null);

        JTabbedPane abas = new JTabbedPane();
        abas.addTab("Alunos", new PainelAlunos());
        abas.addTab("Palestras", new PainelPalestras());
        abas.addTab("Professores", new PainelProfessores());
        abas.addTab("Cursos", new PainelCursos());
        abas.addTab("Inscrições", new PainelInscricoes(0));
        add(abas);
    }
}
