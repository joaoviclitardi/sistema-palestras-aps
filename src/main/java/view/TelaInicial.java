package view;

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

/** Tela inicial: o usuário escolhe se entra como aluno ou como coordenador. */
public class TelaInicial extends JFrame {

    public TelaInicial() {
        setTitle("Sistema de Palestras");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(380, 200);
        setLocationRelativeTo(null);
        setLayout(new FlowLayout(FlowLayout.CENTER, 15, 20));

        JButton btnAluno = new JButton("Área do Aluno");
        JButton btnCoordenador = new JButton("Área do Coordenador");

        add(new JLabel("Sistema de Gerenciamento de Palestras"));
        add(btnAluno);
        add(btnCoordenador);

        btnAluno.addActionListener(e -> new TelaIdentificacaoAluno().setVisible(true));
        btnCoordenador.addActionListener(e -> new PainelCoordenador().setVisible(true));
    }
}
