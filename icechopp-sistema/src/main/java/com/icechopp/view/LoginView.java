package com.icechopp.view;

import com.icechopp.dao.UsuarioDAO;
import com.icechopp.model.Usuario;
import com.icechopp.util.SessaoUsuario;
import com.icechopp.util.UITheme;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class LoginView extends JFrame {

    private final JTextField campoLogin = new JTextField(18);
    private final JPasswordField campoSenha = new JPasswordField(18);

    public LoginView() {
        super("icechopp - Login");
        montarTela();
    }

    private void montarTela() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 480);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(UITheme.GRAY_BG);
        setLayout(new BorderLayout());

        // Faixa superior com a identidade da marca
        JPanel topo = new JPanel();
        topo.setLayout(new BoxLayout(topo, BoxLayout.Y_AXIS));
        topo.setBackground(UITheme.NAVY);
        topo.setBorder(BorderFactory.createEmptyBorder(28, 20, 24, 20));

        JLabel marca = new JLabel("IceChopp", SwingConstants.CENTER);
        marca.setFont(new Font("SansSerif", Font.BOLD, 30));
        marca.setForeground(UITheme.WHITE);
        marca.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel slogan = new JLabel("CHOPP GELADO, EXPERIÊNCIA ÚNICA", SwingConstants.CENTER);
        slogan.setFont(new Font("SansSerif", Font.PLAIN, 12));
        slogan.setForeground(UITheme.LIGHT_BLUE);
        slogan.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel faixaGold = new JPanel();
        faixaGold.setBackground(UITheme.GOLD);
        faixaGold.setMaximumSize(new Dimension(60, 4));
        faixaGold.setPreferredSize(new Dimension(60, 4));
        faixaGold.setAlignmentX(Component.CENTER_ALIGNMENT);

        topo.add(marca);
        topo.add(Box.createVerticalStrut(6));
        topo.add(slogan);
        topo.add(Box.createVerticalStrut(10));
        topo.add(faixaGold);

        add(topo, BorderLayout.NORTH);

        // Formulário de login
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBackground(UITheme.GRAY_BG);
        painel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 6, 8, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel tituloForm = new JLabel("Acesse sua conta", SwingConstants.CENTER);
        tituloForm.setFont(UITheme.FONTE_SUBTITULO);
        tituloForm.setForeground(UITheme.NAVY);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        painel.add(tituloForm, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0;
        painel.add(new JLabel("Login:"), gbc);
        gbc.gridx = 1;
        painel.add(campoLogin, gbc);

        gbc.gridy = 2; gbc.gridx = 0;
        painel.add(new JLabel("Senha:"), gbc);
        gbc.gridx = 1;
        painel.add(campoSenha, gbc);

        JButton btnEntrar = new JButton("Entrar");
        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(18, 6, 6, 6);
        painel.add(btnEntrar, gbc);

        btnEntrar.addActionListener(e -> autenticar());
        campoSenha.addActionListener(e -> autenticar());

        add(painel, BorderLayout.CENTER);

        UITheme.estilizar(painel);
        btnEntrar.setBackground(UITheme.GOLD);
        btnEntrar.setForeground(UITheme.NAVY);
    }

    private void autenticar() {
        String login = campoLogin.getText().trim();
        String senha = new String(campoSenha.getPassword());

        if (login.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe login e senha.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Usuario u = new UsuarioDAO().autenticar(login, senha);
            if (u == null) {
                JOptionPane.showMessageDialog(this, "Login ou senha inválidos.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            SessaoUsuario.login(u);
            new MainView().setVisible(true);
            dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao conectar ao banco:\n" + ex.getMessage(),
                    "Erro de conexão", JOptionPane.ERROR_MESSAGE);
        }
    }
}
