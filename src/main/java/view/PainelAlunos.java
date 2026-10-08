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

import controller.AlunoController;
import controller.CursoController;
import dao.AlunoDAO;
import model.Aluno;
import model.Curso;
import util.SistemaException;

/** Tela do coordenador para cadastrar, alterar, excluir, buscar, filtrar e ordenar alunos. */
public class PainelAlunos extends JPanel {

    private AlunoController controller = new AlunoController();
    private CursoController cursoController = new CursoController();
    private int idSelecionado = 0; // 0 = nenhum aluno selecionado (novo cadastro)

    private JTextField txtNome = new JTextField(18);
    private JTextField txtRa = new JTextField(8);
    private JTextField txtEmail = new JTextField(20);
    private JComboBox<Curso> cmbCurso = new JComboBox<>();

    private JTextField txtBusca = new JTextField(12);
    private JComboBox<Curso> cmbFiltroCurso = new JComboBox<>();
    private JComboBox<String> cmbOrdem = new JComboBox<>(AlunoDAO.ORDENS);

    private DefaultTableModel modelo = new DefaultTableModel(
            new String[] {"Código", "Nome", "RA", "E-mail", "Curso"}, 0);
    private JTable tabela = new JTable(modelo);

    public PainelAlunos() {
        setLayout(new BorderLayout(5, 5));

        // Linha de cima: busca, filtro e ordenação
        JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topo.add(new JLabel("Buscar:"));
        topo.add(txtBusca);
        topo.add(new JLabel("Curso:"));
        topo.add(cmbFiltroCurso);
        topo.add(new JLabel("Ordenar por:"));
        topo.add(cmbOrdem);
        JButton btnBuscar = new JButton("Buscar");
        topo.add(btnBuscar);
        add(topo, BorderLayout.NORTH);

        tabela.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tabela.setDefaultEditor(Object.class, null);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        // Parte de baixo: formulário em duas linhas
        JPanel linha1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        linha1.add(new JLabel("Nome:"));
        linha1.add(txtNome);
        linha1.add(new JLabel("RA:"));
        linha1.add(txtRa);
        linha1.add(new JLabel("E-mail:"));
        linha1.add(txtEmail);
        JPanel linha2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        linha2.add(new JLabel("Curso:"));
        linha2.add(cmbCurso);
        JButton btnSalvar = new JButton("Salvar");
        JButton btnExcluir = new JButton("Excluir");
        JButton btnLimpar = new JButton("Limpar");
        linha2.add(btnSalvar);
        linha2.add(btnExcluir);
        linha2.add(btnLimpar);
        JPanel rodape = new JPanel(new BorderLayout());
        rodape.add(linha1, BorderLayout.NORTH);
        rodape.add(linha2, BorderLayout.SOUTH);
        add(rodape, BorderLayout.SOUTH);

        carregarCursos();

        btnBuscar.addActionListener(e -> carregar());
        txtBusca.addActionListener(e -> carregar());
        cmbFiltroCurso.addActionListener(e -> carregar());
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

    /** Preenche as duas caixas de curso. A do filtro tem a opção "Todos" (código 0). */
    private void carregarCursos() {
        try {
            cmbFiltroCurso.removeAllItems();
            cmbCurso.removeAllItems();
            cmbFiltroCurso.addItem(new Curso(0, "Todos"));
            for (Curso c : cursoController.listar("", 0)) {
                cmbFiltroCurso.addItem(c);
                cmbCurso.addItem(c);
            }
        } catch (SistemaException ex) {
            erro(ex);
        }
    }

    private void carregar() {
        try {
            Curso filtro = (Curso) cmbFiltroCurso.getSelectedItem();
            int idCurso = filtro == null ? 0 : filtro.getIdCurso();
            List<Aluno> lista = controller.listar(txtBusca.getText().trim(), idCurso, cmbOrdem.getSelectedIndex());
            modelo.setRowCount(0);
            for (Aluno a : lista) {
                modelo.addRow(new Object[] {a.getIdAluno(), a.getNome(), a.getRa(), a.getEmail(),
                        a.getCurso().getNomeCurso()});
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
        txtNome.setText((String) modelo.getValueAt(linha, 1));
        txtRa.setText((String) modelo.getValueAt(linha, 2));
        txtEmail.setText((String) modelo.getValueAt(linha, 3));
        String nomeCurso = (String) modelo.getValueAt(linha, 4);
        for (int i = 0; i < cmbCurso.getItemCount(); i++) {
            if (cmbCurso.getItemAt(i).getNomeCurso().equals(nomeCurso)) {
                cmbCurso.setSelectedIndex(i);
            }
        }
    }

    private void salvar() {
        try {
            Curso curso = (Curso) cmbCurso.getSelectedItem();
            if (idSelecionado == 0) {
                controller.cadastrar(txtNome.getText(), txtRa.getText(), txtEmail.getText(), curso);
                JOptionPane.showMessageDialog(this, "Aluno cadastrado com sucesso.");
            } else {
                controller.alterar(idSelecionado, txtNome.getText(), txtRa.getText(), txtEmail.getText(), curso);
                JOptionPane.showMessageDialog(this, "Aluno alterado com sucesso.");
            }
            limpar();
            carregar();
        } catch (SistemaException ex) {
            erro(ex);
        }
    }

    private void excluir() {
        if (idSelecionado == 0) {
            JOptionPane.showMessageDialog(this, "Selecione um aluno na tabela.", "Atenção",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        int resposta = JOptionPane.showConfirmDialog(this, "Deseja excluir o aluno selecionado?",
                "Confirmar exclusão", JOptionPane.YES_NO_OPTION);
        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            controller.excluir(idSelecionado);
            JOptionPane.showMessageDialog(this, "Aluno excluído com sucesso.");
            limpar();
            carregar();
        } catch (SistemaException ex) {
            erro(ex);
        }
    }

    private void limpar() {
        idSelecionado = 0;
        txtNome.setText("");
        txtRa.setText("");
        txtEmail.setText("");
        tabela.clearSelection();
    }

    private void erro(SistemaException ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Atenção", JOptionPane.WARNING_MESSAGE);
    }
}
