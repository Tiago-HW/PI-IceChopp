package com.icechopp.view;

import com.icechopp.util.SessaoUsuario;
import com.icechopp.util.UITheme;

import javax.swing.*;
import java.awt.*;

/**
 * Janela principal do sistema — uma ÚNICA janela com várias abas
 * (JTabbedPane), de acordo com o time (perfil) do usuário logado.
 * Nenhum botão abre uma nova janela: cada funcionalidade é uma aba.
 */
public class MainView extends JFrame {

    private JTabbedPane abas;

    public MainView() {
        super("icechopp - " + SessaoUsuario.getUsuarioLogado().getNomeFuncionario() +
                " [" + SessaoUsuario.getUsuarioLogado().getTime() + "]");
        montarTela();
    }

    private void montarTela() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 780);
        setMinimumSize(new Dimension(980, 620));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.GRAY_BG);

        add(montarCabecalho(), BorderLayout.NORTH);
        add(montarAbas(), BorderLayout.CENTER);
    }

    private JPanel montarCabecalho() {
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setBackground(UITheme.NAVY);
        cabecalho.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));

        JLabel marca = new JLabel("IceChopp");
        marca.setFont(new Font("SansSerif", Font.BOLD, 22));
        marca.setForeground(UITheme.WHITE);

        JLabel slogan = new JLabel("  Chopp gelado, experiência única.");
        slogan.setFont(new Font("SansSerif", Font.PLAIN, 13));
        slogan.setForeground(UITheme.LIGHT_BLUE);

        JPanel esquerda = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        esquerda.setOpaque(false);
        esquerda.add(marca);
        esquerda.add(slogan);

        JLabel usuario = new JLabel(SessaoUsuario.getUsuarioLogado().getNomeFuncionario() +
                " (" + SessaoUsuario.getUsuarioLogado().getTime() + ")");
        usuario.setFont(new Font("SansSerif", Font.PLAIN, 13));
        usuario.setForeground(UITheme.WHITE);

        JButton btnSair = new JButton("Sair");
        UITheme.estilizarBotao(btnSair);
        btnSair.setBackground(UITheme.GOLD);
        btnSair.setForeground(UITheme.NAVY);
        btnSair.addActionListener(e -> sair());

        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        direita.setOpaque(false);
        direita.add(usuario);
        direita.add(btnSair);

        cabecalho.add(esquerda, BorderLayout.WEST);
        cabecalho.add(direita, BorderLayout.EAST);
        return cabecalho;
    }

    private JTabbedPane montarAbas() {
        abas = new JTabbedPane();
        abas.setFont(UITheme.FONTE_SUBTITULO);
        abas.setBackground(UITheme.PALE_BLUE);
        abas.setForeground(UITheme.NAVY);

        boolean isAdmin = SessaoUsuario.isAdmin();
        boolean isVendas = SessaoUsuario.isVendas();
        boolean isCompras = SessaoUsuario.isCompras();

        if (isAdmin || isVendas) {
            abas.addTab("Realizar Venda", new VendaView());
            abas.addTab("Clientes", new ClienteView());
            abas.addTab("Pedidos / Entregas", new PedidoView());
            abas.addTab("Motoristas", new MotoristaView());
            abas.addTab("Caminhões", new CaminhaoView());
        }

        if (isAdmin || isCompras) {
            abas.addTab("Registrar Compra", new CompraView());
            abas.addTab("Fornecedores", new FornecedorView());
        }

        // Estoque é visível para todos os times (regra de negócio: vendas pode visualizar)
        abas.addTab("Estoque", new EstoqueView());

        // Fluxo de Caixa é visível para todos os times logados
        abas.addTab("Fluxo de Caixa", new CaixaView());

        if (isAdmin) {
            abas.addTab("Produção", new ProducaoView());
            abas.addTab("Produtos", new ProdutoView());
            abas.addTab("Usuários", new UsuarioView());
        }

        return abas;
    }

    private void sair() {
        int conf = JOptionPane.showConfirmDialog(this, "Deseja realmente sair do sistema?",
                "Confirmação", JOptionPane.YES_NO_OPTION);
        if (conf != JOptionPane.YES_OPTION) return;
        SessaoUsuario.logout();
        new LoginView().setVisible(true);
        dispose();
    }
}
