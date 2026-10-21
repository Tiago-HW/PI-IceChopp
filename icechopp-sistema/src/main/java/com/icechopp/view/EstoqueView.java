package com.icechopp.view;

import com.icechopp.dao.EstoqueDAO;
import com.icechopp.util.UITheme;
import com.icechopp.model.Estoque;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

/**
 * Tela somente leitura de estoque — todos os times podem visualizar,
 * conforme a regra de negócio (o time de vendas precisa consultar estoque).
 * Entradas de estoque acontecem via tela de Compra (matéria-prima) ou,
 * futuramente, via produção.
 */
public class EstoqueView extends JPanel {

    private final EstoqueDAO dao = new EstoqueDAO();
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"ID Produto", "Nome", "Quantidade", "Valor Un."}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tabela = new JTable(modelo);

    public EstoqueView() {
        montarTela();
        carregar();
    }

    private void montarTela() {
        setLayout(new BorderLayout(10, 10));
        add(UITheme.criarFaixaTitulo("Estoque"), BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        JButton btnAtualizar = new JButton("Atualizar");
        btnAtualizar.addActionListener(e -> carregar());
        JPanel sul = new JPanel();
        sul.add(btnAtualizar);
        add(sul, BorderLayout.SOUTH);

        UITheme.estilizar(this);
    }

    private void carregar() {
        modelo.setRowCount(0);
        try {
            for (Estoque e : dao.listarTodos()) {
                modelo.addRow(new Object[]{e.getIdProduto(), e.getNomeProduto(), e.getQuantidade(),
                        e.getValorUn() == null ? "-" : e.getValorUn(), e.getDataEntrada()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar estoque:\n" + e.getMessage());
        }
    }
}
