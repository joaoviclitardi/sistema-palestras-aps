package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import controller.InscricaoController;
import controller.PalestraController;
import controller.ProfessorController;
import dao.PalestraDAO;
import model.Aluno;
import model.Palestra;
import model.Professor;
import util.SistemaException;
import util.Validador;

/** Tela do aluno: consulta as palestras e se inscreve na palestra selecionada. */
public class PainelPalestrasAluno extends JPanel {

    private PalestraController controller = new PalestraController();
    private ProfessorController professorController = new ProfessorController();
    private InscricaoController inscricaoController = new InscricaoController();
    private Aluno aluno;
    private PainelInscricoes painelInscricoes; // atualizado depois de cada inscrição

    private JTextField txtBusca = new JTextField(15);
    private JComboBox<Professor> cmbFiltroProfessor = new JComboBox<>();
    private JComboBox<String> cmbOrdem = new JComboBox<>(PalestraDAO.ORDENS);

    private DefaultTableModel modelo = new DefaultTableModel(
            new String[] {"Código", "Título", "Descrição", "Data", "Horário", "Local", "Professor"}, 0);
    private JTable tabela = new JTable(modelo);

    public PainelPalestrasAluno(Aluno aluno, PainelInscricoes painelInscricoes) {
        this.aluno = aluno;
        this.painelInscricoes = painelInscricoes;
        setLayout(new BorderLayout(5, 5));

        JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topo.add(new JLabel("Buscar:"));
        topo.add(txtBusca);
        topo.add(new JLabel("Professor:"));
        topo.add(cmbFiltroProfessor);
        topo.add(new JLabel("Ordenar por:"));
        topo.add(cmbOrdem);
        JButton btnBuscar = new JButton("Buscar");
        topo.add(btnBuscar);
        add(topo, BorderLayout.NORTH);

        tabela.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tabela.setDefaultEditor(Object.class, null);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnInscrever = new JButton("Inscrever-se na palestra selecionada");
        rodape.add(btnInscrever);
        add(rodape, BorderLayout.SOUTH);

        carregarProfessores();

        btnBuscar.addActionListener(e -> carregar());
        txtBusca.addActionListener(e -> carregar());
        cmbFiltroProfessor.addActionListener(e -> carregar());
        cmbOrdem.addActionListener(e -> carregar());
        btnInscrever.addActionListener(e -> inscrever());

        carregar();
    }

    private void carregarProfessores() {
        try {
            cmbFiltroProfessor.removeAllItems();
            cmbFiltroProfessor.addItem(new Professor(0, "Todos", ""));
            for (Professor p : professorController.listar("", 0)) {
                cmbFiltroProfessor.addItem(p);
            }
        } catch (SistemaException ex) {
            erro(ex);
        }
    }

    private void carregar() {
        try {
            Professor filtro = (Professor) cmbFiltroProfessor.getSelectedItem();
            int idProfessor = filtro == null ? 0 : filtro.getIdProfessor();
            List<Palestra> lista = controller.listar(txtBusca.getText().trim(), idProfessor,
                    cmbOrdem.getSelectedIndex());
            modelo.setRowCount(0);
            for (Palestra p : lista) {
                modelo.addRow(new Object[] {p.getIdPalestra(), p.getTitulo(), p.getDescricao(),
                        Validador.formatarData(p.getData()), p.getHorario(), p.getLocal(),
                        p.getProfessor().getNome()});
            }
        } catch (SistemaException ex) {
            erro(ex);
        }
    }

    private void inscrever() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma palestra na tabela.", "Atenção",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Palestra palestra = controller.buscarPorId((int) modelo.getValueAt(linha, 0));
            inscricaoController.inscrever(aluno, palestra);
            JOptionPane.showMessageDialog(this, "Inscrição realizada na palestra \"" + palestra.getTitulo() + "\".");
            painelInscricoes.carregar();
        } catch (SistemaException ex) {
            erro(ex);
        }
    }

    private void erro(SistemaException ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Atenção", JOptionPane.WARNING_MESSAGE);
    }
}
