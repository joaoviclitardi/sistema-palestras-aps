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

import controller.CursoController;
import dao.CursoDAO;
import model.Curso;
import util.SistemaException;

/** Tela do coordenador para cadastrar, alterar, excluir, buscar e ordenar cursos. */
public class PainelCursos extends JPanel {

    private CursoController controller = new CursoController();
    private int idSelecionado = 0; // 0 = nenhum curso selecionado (novo cadastro)

    private JTextField txtNome = new JTextField(25);
    private JTextField txtBusca = new JTextField(15);
    private JComboBox<String> cmbOrdem = new JComboBox<>(CursoDAO.ORDENS);
    private DefaultTableModel modelo = new DefaultTableModel(new String[] {"Código", "Curso"}, 0);
    private JTable tabela = new JTable(modelo);

    public PainelCursos() {
        setLayout(new BorderLayout(5, 5));

        JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topo.add(new JLabel("Buscar:"));
        topo.add(txtBusca);
        topo.add(new JLabel("Ordenar por:"));
        topo.add(cmbOrdem);
        JButton btnBuscar = new JButton("Buscar");
        topo.add(btnBuscar);
        add(topo, BorderLayout.NORTH);

        tabela.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tabela.setDefaultEditor(Object.class, null);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rodape.add(new JLabel("Nome do curso:"));
        rodape.add(txtNome);
        JButton btnSalvar = new JButton("Salvar");
        JButton btnExcluir = new JButton("Excluir");
        JButton btnLimpar = new JButton("Limpar");
        rodape.add(btnSalvar);
        rodape.add(btnExcluir);
        rodape.add(btnLimpar);
        add(rodape, BorderLayout.SOUTH);

        btnBuscar.addActionListener(e -> carregar());
        txtBusca.addActionListener(e -> carregar());
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

    private void carregar() {
        try {
            List<Curso> lista = controller.listar(txtBusca.getText().trim(), cmbOrdem.getSelectedIndex());
            modelo.setRowCount(0);
            for (Curso c : lista) {
                modelo.addRow(new Object[] {c.getIdCurso(), c.getNomeCurso()});
            }
        } catch (SistemaException ex) {
            erro(ex);
        }
    }

    private void selecionar() {
        int linha = tabela.getSelectedRow();
        if (linha >= 0) {
            idSelecionado = (int) modelo.getValueAt(linha, 0);
            txtNome.setText((String) modelo.getValueAt(linha, 1));
        }
    }

    private void salvar() {
        try {
            if (idSelecionado == 0) {
                controller.cadastrar(txtNome.getText());
                JOptionPane.showMessageDialog(this, "Curso cadastrado com sucesso.");
            } else {
                controller.alterar(idSelecionado, txtNome.getText());
                JOptionPane.showMessageDialog(this, "Curso alterado com sucesso.");
            }
            limpar();
            carregar();
        } catch (SistemaException ex) {
            erro(ex);
        }
    }

    private void excluir() {
        if (idSelecionado == 0) {
            JOptionPane.showMessageDialog(this, "Selecione um curso na tabela.", "Atenção",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        int resposta = JOptionPane.showConfirmDialog(this, "Deseja excluir o curso selecionado?",
                "Confirmar exclusão", JOptionPane.YES_NO_OPTION);
        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            controller.excluir(idSelecionado);
            JOptionPane.showMessageDialog(this, "Curso excluído com sucesso.");
            limpar();
            carregar();
        } catch (SistemaException ex) {
            erro(ex);
        }
    }

    private void limpar() {
        idSelecionado = 0;
        txtNome.setText("");
        tabela.clearSelection();
    }

    private void erro(SistemaException ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Atenção", JOptionPane.WARNING_MESSAGE);
    }
}
