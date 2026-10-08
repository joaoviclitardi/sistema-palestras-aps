import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import view.TelaInicial;

/** Ponto de entrada do sistema. */
public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            // se o visual Nimbus não existir, o Java usa o visual padrão
        }
        SwingUtilities.invokeLater(() -> new TelaInicial().setVisible(true));
    }
}
