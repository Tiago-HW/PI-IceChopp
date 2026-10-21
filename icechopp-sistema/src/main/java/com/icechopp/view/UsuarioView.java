package com.icechopp.view;

import com.icechopp.dao.UsuarioDAO;
import com.icechopp.util.UITheme;
import com.icechopp.model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

/**
 * Tela restrita ao admin: cadastro de usuários e definição de time.
 */
public class UsuarioView extends JPanel {

    private final UsuarioDAO dao = new UsuarioDAO();
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"CPF", "Nome", "Login", "Time", "Telefone", "Endereço", "Salário"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tabela = new JTable(modelo);

    private final JTextField campoCpf = new JTextField(15);
    private final JTextField campoNome = new JTextField(20);
    private final JTextField campoLogin = new JTextField(15);
    private final JPasswordField campoSenha = new JPasswordField(15);
    private final JComboBox<String> comboTime = new JComboBox<>(new String[]{"vendas", "compras", "admin"});
    private final JTextField campoTelefone = new JTextField(15);
    private final JTextField campoEndereco = new JTextField(20);
    private final JTextField campoSalario = new JTextField(10);
    private String cpfSelecionado = null;

    public UsuarioView() {
        montarTela();
        carregar();
    }

    private void montarTela() {
        setLayout(new BorderLayout(10, 10));
        add(UITheme.criarFaixaTitulo("Usuários (admin)"), BorderLayout.NORTH);

        add(new JScrollPane(tabela), BorderLayout.CENTER);
        tabela.getSelectionModel().addListSelectionListener(e -> preencherComSelecao());

        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        form.add(new JLabel("CPF:")); form.add(campoCpf);
        form.add(new JLabel("Nome:")); form.add(campoNome);
        form.add(new JLabel("Login:")); form.add(campoLogin);
        form.add(new JLabel("Senha:")); form.add(campoSenha);
        form.add(new JLabel("Time:")); form.add(comboTime);
        form.add(new JLabel("Telefone:")); form.add(campoTelefone);
        form.add(new JLabel("Endereço:")); form.add(campoEndereco);
        form.add(new JLabel("Salário:")); form.add(campoSalario);

        JButton btnNovo = new JButton("Novo");
        JButton btnSalvar = new JButton("Salvar");
        JButton btnExcluir = new JButton("Excluir");
        btnNovo.addActionListener(e -> limpar());
        btnSalvar.addActionListener(e -> salvar());
        btnExcluir.addActionListener(e -> excluir());

        JPanel botoes = new JPanel();
        botoes.add(btnNovo); botoes.add(btnSalvar); botoes.add(btnExcluir);

        JPanel sul = new JPanel(new BorderLayout());
        sul.add(form, BorderLayout.CENTER);
        sul.add(botoes, BorderLayout.SOUTH);
        add(sul, BorderLayout.SOUTH);

        UITheme.estilizar(this);
    }

    private void carregar() {
        modelo.setRowCount(0);
        try {
            for (Usuario u : dao.listarTodos()) {
                modelo.addRow(new Object[]{u.getCpf(), u.getNomeFuncionario(), u.getLogin(), u.getTime(),
                        u.getTelefone(), u.getEndereco(), u.getSalario()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar usuários:\n" + e.getMessage());
        }
    }

    private void preencherComSelecao() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) return;
        cpfSelecionado = String.valueOf(modelo.getValueAt(linha, 0));
        campoCpf.setText(cpfSelecionado);
        campoCpf.setEditable(false);
        campoNome.setText(String.valueOf(modelo.getValueAt(linha, 1)));
        campoLogin.setText(String.valueOf(modelo.getValueAt(linha, 2)));
        campoSenha.setText(""); // por segurança, não recarrega a senha atual
        comboTime.setSelectedItem(String.valueOf(modelo.getValueAt(linha, 3)));
        campoTelefone.setText(String.valueOf(modelo.getValueAt(linha, 4)));
        campoEndereco.setText(String.valueOf(modelo.getValueAt(linha, 5)));
        campoSalario.setText(String.valueOf(modelo.getValueAt(linha, 6)));
    }

    private void limpar() {
        cpfSelecionado = null;
        campoCpf.setText(""); campoCpf.setEditable(true);
        campoNome.setText(""); campoLogin.setText(""); campoSenha.setText("");
        campoTelefone.setText(""); campoEndereco.setText(""); campoSalario.setText("");
        comboTime.setSelectedIndex(0);
        tabela.clearSelection();
    }

    private void salvar() {
        if (campoCpf.getText().isBlank() || campoNome.getText().isBlank() || campoLogin.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Informe CPF, nome e login.");
            return;
        }
        if (cpfSelecionado == null && campoSenha.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this, "Informe uma senha para o novo usuário.");
            return;
        }
        long telefone; double salario;
        try {
            telefone = Long.parseLong(campoTelefone.getText().trim());
            salario = Double.parseDouble(campoSalario.getText().trim().replace(",", "."));
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Telefone e salário devem ser numéricos.");
            return;
        }
        try {
            Usuario u = new Usuario();
            u.setCpf(campoCpf.getText().trim());
            u.setNomeFuncionario(campoNome.getText().trim());
            u.setTelefone(telefone);
            u.setEndereco(campoEndereco.getText().trim());
            u.setSalario(salario);
            u.setLogin(campoLogin.getText().trim());
            u.setTime((String) comboTime.getSelectedItem());

            String novaSenha = new String(campoSenha.getPassword());

            if (cpfSelecionado == null) {
                u.setSenha(novaSenha);
                dao.inserir(u);
            } else {
                // mantém a senha atual se o campo for deixado em branco na edição
                if (novaSenha.isBlank()) {
                    Usuario existente = dao.listarTodos().stream()
                            .filter(x -> x.getCpf().equals(cpfSelecionado)).findFirst().orElse(null);
                    u.setSenha(existente != null ? existente.getSenha() : "");
                } else {
                    u.setSenha(novaSenha);
                }
                dao.atualizar(u);
            }
            carregar();
            limpar();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar:\n" + e.getMessage());
        }
    }

    private void excluir() {
        if (cpfSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um usuário.");
            return;
        }
        int conf = JOptionPane.showConfirmDialog(this, "Excluir usuário selecionado?", "Confirmação", JOptionPane.YES_NO_OPTION);
        if (conf != JOptionPane.YES_OPTION) return;
        try {
            dao.deletar(cpfSelecionado);
            carregar();
            limpar();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao excluir:\n" + e.getMessage());
        }
    }
}
