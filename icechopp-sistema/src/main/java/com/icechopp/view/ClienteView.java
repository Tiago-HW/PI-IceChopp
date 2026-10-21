package com.icechopp.view;

import com.icechopp.dao.ClienteDAO;
import com.icechopp.util.UITheme;
import com.icechopp.model.Cliente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class ClienteView extends JPanel {

    private final ClienteDAO dao = new ClienteDAO();
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"ID", "Nome", "Endereço", "Telefone", "Email"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tabela = new JTable(modelo);

    private final JTextField campoNome = new JTextField(20);
    private final JTextField campoEndereco = new JTextField(20);
    private final JTextField campoTelefone = new JTextField(20);
    private final JTextField campoEmail = new JTextField(20);
    private Integer idSelecionado = null;

    public ClienteView() {
        montarTela();
        carregar();
    }

    private void montarTela() {
        setLayout(new BorderLayout(10, 10));
        add(UITheme.criarFaixaTitulo("Clientes"), BorderLayout.NORTH);

        add(new JScrollPane(tabela), BorderLayout.CENTER);
        tabela.getSelectionModel().addListSelectionListener(e -> preencherFormularioComSelecao());

        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        form.add(new JLabel("Nome:")); form.add(campoNome);
        form.add(new JLabel("Endereço:")); form.add(campoEndereco);
        form.add(new JLabel("Telefone:")); form.add(campoTelefone);
        form.add(new JLabel("Email:")); form.add(campoEmail);

        JButton btnNovo = new JButton("Novo");
        JButton btnSalvar = new JButton("Salvar");
        JButton btnExcluir = new JButton("Excluir");

        btnNovo.addActionListener(e -> limparFormulario());
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
            for (Cliente c : dao.listarTodos()) {
                modelo.addRow(new Object[]{c.getIdCliente(), c.getNome(), c.getEndereco(), c.getTelefone(), c.getEmail()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar clientes:\n" + e.getMessage());
        }
    }

    private void preencherFormularioComSelecao() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) return;
        idSelecionado = (Integer) modelo.getValueAt(linha, 0);
        campoNome.setText(String.valueOf(modelo.getValueAt(linha, 1)));
        campoEndereco.setText(String.valueOf(modelo.getValueAt(linha, 2)));
        campoTelefone.setText(String.valueOf(modelo.getValueAt(linha, 3)));
        campoEmail.setText(String.valueOf(modelo.getValueAt(linha, 4)));
    }

    private void limparFormulario() {
        idSelecionado = null;
        campoNome.setText("");
        campoEndereco.setText("");
        campoTelefone.setText("");
        campoEmail.setText("");
        tabela.clearSelection();
    }

    private void salvar() {
        try {
            long telefone;
            try {
                telefone = Long.parseLong(campoTelefone.getText().trim());
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Telefone deve conter apenas números.");
                return;
            }
            if (campoNome.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "Informe o nome.");
                return;
            }

            Cliente c = new Cliente();
            c.setNome(campoNome.getText().trim());
            c.setEndereco(campoEndereco.getText().trim());
            c.setTelefone(telefone);
            c.setEmail(campoEmail.getText().trim());

            if (idSelecionado == null) {
                dao.inserir(c);
            } else {
                c.setIdCliente(idSelecionado);
                dao.atualizar(c);
            }
            carregar();
            limparFormulario();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar:\n" + e.getMessage());
        }
    }

    private void excluir() {
        if (idSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente na tabela.");
            return;
        }
        int conf = JOptionPane.showConfirmDialog(this, "Excluir cliente selecionado?", "Confirmação", JOptionPane.YES_NO_OPTION);
        if (conf != JOptionPane.YES_OPTION) return;
        try {
            dao.deletar(idSelecionado);
            carregar();
            limparFormulario();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao excluir:\n" + e.getMessage());
        }
    }
}
