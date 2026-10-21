package com.icechopp.view;

import com.icechopp.controller.CompraController;
import com.icechopp.util.UITheme;
import com.icechopp.dao.FornecedorDAO;
import com.icechopp.dao.ProdutoDAO;
import com.icechopp.model.Fornecedor;
import com.icechopp.model.Produto;
import com.icechopp.util.SessaoUsuario;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Tela do time de compras: registra a compra de matéria-prima junto a
 * um fornecedor. A matéria-prima entra no estoque apenas com quantidade
 * e data de entrada (sem valor unitário), conforme regra de negócio.
 */
public class CompraView extends JPanel {

    private final CompraController controller = new CompraController();
    private final ProdutoDAO produtoDAO = new ProdutoDAO();
    private final FornecedorDAO fornecedorDAO = new FornecedorDAO();

    private final JComboBox<Produto> comboProduto = new JComboBox<>();
    private final JComboBox<Fornecedor> comboFornecedor = new JComboBox<>();
    private final JTextField campoQuantidade = new JTextField(10);
    private final JTextField campoValorTotal = new JTextField(10);

    public CompraView() {
        montarTela();
        carregarCombos();
    }

    private void montarTela() {

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        form.add(new JLabel("Matéria-prima:")); form.add(comboProduto);
        form.add(new JLabel("Fornecedor:")); form.add(comboFornecedor);
        form.add(new JLabel("Quantidade:")); form.add(campoQuantidade);
        form.add(new JLabel("Valor total da compra:")); form.add(campoValorTotal);

        JButton btnRegistrar = new JButton("Registrar Compra");
        btnRegistrar.addActionListener(e -> registrar());

        setLayout(new BorderLayout());
        add(UITheme.criarFaixaTitulo("Registrar Compra de Matéria-Prima"), BorderLayout.NORTH);
        add(form, BorderLayout.CENTER);
        JPanel sul = new JPanel();
        sul.add(btnRegistrar);
        add(sul, BorderLayout.SOUTH);

        UITheme.estilizar(this);
    }

    private void carregarCombos() {
        try {
            comboProduto.removeAllItems();
            List<Produto> materiasPrimas = produtoDAO.listarPorTipo("MATERIA_PRIMA");
            for (Produto p : materiasPrimas) comboProduto.addItem(p);
            if (materiasPrimas.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Nenhuma matéria-prima cadastrada. Peça ao admin para cadastrar em Produtos (tipo MATERIA_PRIMA).");
            }

            comboFornecedor.removeAllItems();
            List<Fornecedor> fornecedores = fornecedorDAO.listarTodos();
            for (Fornecedor f : fornecedores) comboFornecedor.addItem(f);
            if (fornecedores.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nenhum fornecedor cadastrado. Cadastre um fornecedor antes.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados:\n" + e.getMessage());
        }
    }

    private void registrar() {
        Produto produto = (Produto) comboProduto.getSelectedItem();
        Fornecedor fornecedor = (Fornecedor) comboFornecedor.getSelectedItem();
        if (produto == null || fornecedor == null) {
            JOptionPane.showMessageDialog(this, "Selecione produto e fornecedor.");
            return;
        }
        int quantidade;
        double valorTotal;
        try {
            quantidade = Integer.parseInt(campoQuantidade.getText().trim());
            valorTotal = Double.parseDouble(campoValorTotal.getText().trim().replace(",", "."));
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Quantidade e valor devem ser numéricos.");
            return;
        }
        if (quantidade <= 0) {
            JOptionPane.showMessageDialog(this, "Quantidade deve ser maior que zero.");
            return;
        }
        try {
            controller.registrarCompra(produto, quantidade, valorTotal, fornecedor.getIdFornecedor(),
                    SessaoUsuario.getUsuarioLogado().getCpf());
            JOptionPane.showMessageDialog(this, "Compra registrada e estoque atualizado com sucesso!");
            campoQuantidade.setText("");
            campoValorTotal.setText("");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao registrar compra:\n" + e.getMessage());
        }
    }
}
