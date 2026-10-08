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

import controller.PalestraController;
import controller.ProfessorController;
import dao.PalestraDAO;
import model.Palestra;
import model.Professor;
import util.SistemaException;
import util.Validador;

/** Tela do coordenador para cadastrar, alterar, excluir, buscar, filtrar e ordenar palestras. */
public class PainelPalestras extends JPanel {

    private PalestraController controller = new PalestraController();
    private ProfessorController professorController = new ProfessorController();
    private int idSelecionado = 0; // 0 = nenhuma palestra selecionada (novo cadastro)

    private JTextField txtTitulo = new JTextField(20);
    private JTextField txtDescricao = new JTextField(20);
    private JTextField txtData = new JTextField(8);
    private JTextField txtHorario = new JTextField(5);
    private JTextField txtLocal = new JTextField(12);
    private JComboBox<Professor> cmbProfessor = new JComboBox<>();

    private JTextField txtBusca = new JTextField(12);
    private JComboBox<Professor> cmbFiltroProfessor = new JComboBox<>();
    private JComboBox<String> cmbOrdem = new JComboBox<>(PalestraDAO.ORDENS);

    private DefaultTableModel modelo = new DefaultTableModel(
            new String[] {"Código", "Título", "Descrição", "Data", "Horário", "Local", "Professor"}, 0);
    private JTable tabela = new JTable(modelo);

    public PainelPalestras() {
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

        JPanel linha1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        linha1.add(new JLabel("Título:"));
        linha1.add(txtTitulo);
        linha1.add(new JLabel("Descrição:"));
        linha1.add(txtDescricao);
        linha1.add(new JLabel("Local:"));
        linha1.add(txtLocal);
        JPanel linha2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        linha2.add(new JLabel("Data (dd/mm/aaaa):"));
        linha2.add(txtData);
        linha2.add(new JLabel("Horário (hh:mm):"));
        linha2.add(txtHorario);
        linha2.add(new JLabel("Professor:"));
        linha2.add(cmbProfessor);
        JPanel linha3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnSalvar = new JButton("Salvar");
        JButton btnExcluir = new JButton("Excluir");
        JButton btnLimpar = new JButton("Limpar");
        linha3.add(btnSalvar);
        linha3.add(btnExcluir);
        linha3.add(btnLimpar);
        JPanel rodape = new JPanel(new java.awt.GridLayout(3, 1));
        rodape.add(linha1);
        rodape.add(linha2);
        rodape.add(linha3);
        add(rodape, BorderLayout.SOUTH);

        carregarProfessores();

        btnBuscar.addActionListener(e -> carregar());
        txtBusca.addActionListener(e -> carregar());
        cmbFiltroProfessor.addActionListener(e -> carregar());
        cmbOrdem.addActionListener(e -> carregar());
        btnSalvar.addActionListener(e -> salvar());
        btnExcluir.addActionListener(e -> excluir());
        btnLimpar.addActionListener(e -> limpar());
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selecionar();
            }
        });

        carregar();
    }

    /** Preenche as caixas de professor. A do filtro tem a opção "Todos" (código 0). */
    private void carregarProfessores() {
        try {
            cmbFiltroProfessor.removeAllItems();
            cmbProfessor.removeAllItems();
            cmbFiltroProfessor.addItem(new Professor(0, "Todos", ""));
            for (Professor p : professorController.listar("", 0)) {
                cmbFiltroProfessor.addItem(p);
                cmbProfessor.addItem(p);
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

    private void selecionar() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            return;
        }
        idSelecionado = (int) modelo.getValueAt(linha, 0);
        txtTitulo.setText((String) modelo.getValueAt(linha, 1));
        txtDescricao.setText((String) modelo.getValueAt(linha, 2));
        txtData.setText((String) modelo.getValueAt(linha, 3));
        txtHorario.setText((String) modelo.getValueAt(linha, 4));
        txtLocal.setText((String) modelo.getValueAt(linha, 5));
        String nomeProfessor = (String) modelo.getValueAt(linha, 6);
        for (int i = 0; i < cmbProfessor.getItemCount(); i++) {
            if (cmbProfessor.getItemAt(i).getNome().equals(nomeProfessor)) {
                cmbProfessor.setSelectedIndex(i);
            }
        }
    }

    private void salvar() {
        try {
            Professor professor = (Professor) cmbProfessor.getSelectedItem();
            if (idSelecionado == 0) {
                controller.cadastrar(txtTitulo.getText(), txtDescricao.getText(), txtData.getText(),
                        txtHorario.getText(), txtLocal.getText(), professor);
                JOptionPane.showMessageDialog(this, "Palestra cadastrada com sucesso.");
            } else {
                controller.alterar(idSelecionado, txtTitulo.getText(), txtDescricao.getText(), txtData.getText(),
                        txtHorario.getText(), txtLocal.getText(), professor);
                JOptionPane.showMessageDialog(this, "Palestra alterada com sucesso.");
            }
            limpar();
            carregar();
        } catch (SistemaException ex) {
            erro(ex);
        }
    }

    private void excluir() {
        if (idSelecionado == 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma palestra na tabela.", "Atenção",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        int resposta = JOptionPane.showConfirmDialog(this, "Deseja excluir a palestra selecionada?",
                "Confirmar exclusão", JOptionPane.YES_NO_OPTION);
        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            controller.excluir(idSelecionado);
            JOptionPane.showMessageDialog(this, "Palestra excluída com sucesso.");
            limpar();
            carregar();
        } catch (SistemaException ex) {
            erro(ex);
        }
    }

    private void limpar() {
        idSelecionado = 0;
        txtTitulo.setText("");
        txtDescricao.setText("");
        txtData.setText("");
        txtHorario.setText("");
        txtLocal.setText("");
        tabela.clearSelection();
    }

    private void erro(SistemaException ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Atenção", JOptionPane.WARNING_MESSAGE);
    }
}
