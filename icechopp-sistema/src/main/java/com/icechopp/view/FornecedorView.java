package com.icechopp.view;

import com.icechopp.dao.FornecedorDAO;
import com.icechopp.util.UITheme;
import com.icechopp.model.Fornecedor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class FornecedorView extends JPanel {

    private final FornecedorDAO dao = new FornecedorDAO();
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"ID", "Nome", "Email", "Telefone"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tabela = new JTable(modelo);

    private final JTextField campoNome = new JTextField(20);
    private final JTextField campoEmail = new JTextField(20);
    private final JTextField campoTelefone = new JTextField(20);
    private Integer idSelecionado = null;

    public FornecedorView() {
        montarTela();
        carregar();
    }

    private void montarTela() {
        setLayout(new BorderLayout(10, 10));
        add(UITheme.criarFaixaTitulo("Fornecedores"), BorderLayout.NORTH);

        add(new JScrollPane(tabela), BorderLayout.CENTER);
        tabela.getSelectionModel().addListSelectionListener(e -> preencherComSelecao());

        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        form.add(new JLabel("Nome:")); form.add(campoNome);
        form.add(new JLabel("Email:")); form.add(campoEmail);
        form.add(new JLabel("Telefone:")); form.add(campoTelefone);

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
            for (Fornecedor f : dao.listarTodos()) {
                modelo.addRow(new Object[]{f.getIdFornecedor(), f.getNome(), f.getEmail(), f.getTelefone()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar fornecedores:\n" + e.getMessage());
        }
    }

    private void preencherComSelecao() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) return;
        idSelecionado = (Integer) modelo.getValueAt(linha, 0);
        campoNome.setText(String.valueOf(modelo.getValueAt(linha, 1)));
        campoEmail.setText(String.valueOf(modelo.getValueAt(linha, 2)));
        campoTelefone.setText(String.valueOf(modelo.getValueAt(linha, 3)));
    }

    private void limpar() {
        idSelecionado = null;
        campoNome.setText(""); campoEmail.setText(""); campoTelefone.setText("");
        tabela.clearSelection();
    }

    private void salvar() {
        if (campoNome.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Informe o nome.");
            return;
        }
        try {
            Fornecedor f = new Fornecedor();
            f.setNome(campoNome.getText().trim());
            f.setEmail(campoEmail.getText().trim());
            f.setTelefone(campoTelefone.getText().trim());

            if (idSelecionado == null) {
                dao.inserir(f);
            } else {
                f.setIdFornecedor(idSelecionado);
                dao.atualizar(f);
            }
            carregar();
            limpar();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar:\n" + e.getMessage());
        }
    }

    private void excluir() {
        if (idSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um fornecedor.");
            return;
        }
        int conf = JOptionPane.showConfirmDialog(this, "Excluir fornecedor selecionado?", "Confirmação", JOptionPane.YES_NO_OPTION);
        if (conf != JOptionPane.YES_OPTION) return;
        try {
            dao.deletar(idSelecionado);
            carregar();
            limpar();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao excluir:\n" + e.getMessage());
        }
    }
}
