package com.icechopp.view;

import com.icechopp.dao.ProdutoDAO;
import com.icechopp.util.UITheme;
import com.icechopp.model.Produto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class ProdutoView extends JPanel {

    private final ProdutoDAO dao = new ProdutoDAO();
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"ID", "Nome", "Valor Un.", "Tipo"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tabela = new JTable(modelo);

    private final JTextField campoNome = new JTextField(20);
    private final JTextField campoValor = new JTextField(10);
    private final JComboBox<String> comboTipo = new JComboBox<>(new String[]{"PRODUTO_FINAL", "MATERIA_PRIMA"});
    private Integer idSelecionado = null;

    public ProdutoView() {
        montarTela();
        carregar();
    }

    private void montarTela() {
        setLayout(new BorderLayout(10, 10));
        add(UITheme.criarFaixaTitulo("Produtos (cadastro geral)"), BorderLayout.NORTH);

        add(new JScrollPane(tabela), BorderLayout.CENTER);
        tabela.getSelectionModel().addListSelectionListener(e -> preencherComSelecao());
        comboTipo.addActionListener(e -> atualizarCampoValor());

        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        form.add(new JLabel("Nome:")); form.add(campoNome);
        form.add(new JLabel("Tipo:")); form.add(comboTipo);
        form.add(new JLabel("Valor Unit. (vazio p/ matéria-prima):")); form.add(campoValor);

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

    private void atualizarCampoValor() {
        boolean materiaPrima = "MATERIA_PRIMA".equals(comboTipo.getSelectedItem());
        campoValor.setEnabled(!materiaPrima);
        if (materiaPrima) campoValor.setText("");
    }

    private void carregar() {
        modelo.setRowCount(0);
        try {
            for (Produto p : dao.listarTodos()) {
                modelo.addRow(new Object[]{p.getIdProduto(), p.getNome(),
                        p.getValorUn() == null ? "-" : p.getValorUn(), p.getTipo()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar produtos:\n" + e.getMessage());
        }
    }

    private void preencherComSelecao() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) return;
        idSelecionado = (Integer) modelo.getValueAt(linha, 0);
        campoNome.setText(String.valueOf(modelo.getValueAt(linha, 1)));
        Object valor = modelo.getValueAt(linha, 2);
        campoValor.setText("-".equals(valor) ? "" : String.valueOf(valor));
        comboTipo.setSelectedItem(String.valueOf(modelo.getValueAt(linha, 3)));
    }

    private void limpar() {
        idSelecionado = null;
        campoNome.setText(""); campoValor.setText("");
        comboTipo.setSelectedIndex(0);
        tabela.clearSelection();
    }

    private void salvar() {
        if (campoNome.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Informe o nome.");
            return;
        }
        String tipo = (String) comboTipo.getSelectedItem();
        Double valor = null;
        if ("PRODUTO_FINAL".equals(tipo)) {
            try {
                valor = Double.parseDouble(campoValor.getText().trim().replace(",", "."));
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Informe um valor unitário válido para produto final.");
                return;
            }
        }
        try {
            Produto p = new Produto();
            p.setNome(campoNome.getText().trim());
            p.setValorUn(valor);
            p.setTipo(tipo);

            if (idSelecionado == null) {
                dao.inserir(p);
            } else {
                p.setIdProduto(idSelecionado);
                dao.atualizar(p);
            }
            carregar();
            limpar();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar:\n" + e.getMessage());
        }
    }

    private void excluir() {
        if (idSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um produto.");
            return;
        }
        int conf = JOptionPane.showConfirmDialog(this, "Excluir produto selecionado?", "Confirmação", JOptionPane.YES_NO_OPTION);
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
