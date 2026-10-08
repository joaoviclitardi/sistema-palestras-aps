package view;

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import controller.AlunoController;
import model.Aluno;
import util.SistemaException;

/** O aluno digita o nome completo para entrar na Área do Aluno. */
public class TelaIdentificacaoAluno extends JFrame {

    private AlunoController controller = new AlunoController();
    private JTextField txtNome = new JTextField(25);

    public TelaIdentificacaoAluno() {
        setTitle("Identificação do aluno");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(480, 130);
        setLocationRelativeTo(null);
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));

        JButton btnEntrar = new JButton("Entrar");
        add(new JLabel("Nome completo:"));
        add(txtNome);
        add(btnEntrar);

        btnEntrar.addActionListener(e -> entrar());
        txtNome.addActionListener(e -> entrar());
    }

    private void entrar() {
        try {
            Aluno aluno = controller.identificar(txtNome.getText());
            new PainelAluno(aluno).setVisible(true);
            dispose();
        } catch (SistemaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Atenção", JOptionPane.WARNING_MESSAGE);
        }
    }
}
