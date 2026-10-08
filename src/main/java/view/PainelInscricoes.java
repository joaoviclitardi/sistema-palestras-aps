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
import dao.InscricaoDAO;
import model.Inscricao;
import util.SistemaException;
import util.Validador;

/**
 * Lista inscrições. O coordenador vê todas (idAluno = 0). O aluno vê só as suas
 * (idAluno com o código dele) e pode cancelar uma inscrição.
 */
public class PainelInscricoes extends JPanel {

    private InscricaoController controller = new InscricaoController();
    private int idAluno;
    private int idSelecionado = 0; // 0 = nenhuma inscrição selecionada

    private JTextField txtBusca = new JTextField(15);
    private JComboBox<String> cmbOrdem = new JComboBox<>(InscricaoDAO.ORDENS);
    private DefaultTableModel modelo = new DefaultTableModel(
            new String[] {"Código", "Aluno", "RA", "Palestra", "Data", "Horário", "Professor", "Inscrito em"}, 0);
    private JTable tabela = new JTable(modelo);

    public PainelInscricoes(int idAluno) {
        this.idAluno = idAluno;
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
        JButton btnExcluir = new JButton(idAluno > 0 ? "Cancelar inscrição" : "Excluir inscrição");
        rodape.add(btnExcluir);
        add(rodape, BorderLayout.SOUTH);

        btnBuscar.addActionListener(e -> carregar());
        txtBusca.addActionListener(e -> carregar());
        cmbOrdem.addActionListener(e -> carregar());
        btnExcluir.addActionListener(e -> excluir());
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabela.getSelectedRow() >= 0) {
                idSelecionado = (int) modelo.getValueAt(tabela.getSelectedRow(), 0);
            }
        });

        carregar();
    }

    public void carregar() {
        try {
            List<Inscricao> lista = controller.listar(txtBusca.getText().trim(), idAluno,
                    cmbOrdem.getSelectedIndex());
            modelo.setRowCount(0);
            for (Inscricao i : lista) {
                modelo.addRow(new Object[] {i.getIdInscricao(), i.getAluno().getNome(), i.getAluno().getRa(),
                        i.getPalestra().getTitulo(), Validador.formatarData(i.getPalestra().getData()),
                        i.getPalestra().getHorario(), i.getPalestra().getProfessor().getNome(),
                        Validador.formatarData(i.getDataInscricao())});
            }
            idSelecionado = 0;
        } catch (SistemaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Atenção", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void excluir() {
        if (idSelecionado == 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma inscrição na tabela.", "Atenção",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        int resposta = JOptionPane.showConfirmDialog(this, "Deseja excluir a inscrição selecionada?",
                "Confirmar exclusão", JOptionPane.YES_NO_OPTION);
        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            controller.excluir(idSelecionado);
            JOptionPane.showMessageDialog(this, "Inscrição excluída com sucesso.");
            carregar();
        } catch (SistemaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Atenção", JOptionPane.WARNING_MESSAGE);
        }
    }
}
