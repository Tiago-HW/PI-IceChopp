package com.icechopp.view;

import com.icechopp.controller.ProducaoController;
import com.icechopp.util.UITheme;
import com.icechopp.dao.ProdutoDAO;
import com.icechopp.model.Produto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Tela de lançamento e histórico de ordens de produção.
 * Deduz matéria-prima (Malte) e adiciona ao estoque do produto final.
 */
public class ProducaoView extends JPanel {

    private final ProducaoController producaoController = new ProducaoController();
    private final ProdutoDAO produtoDAO = new ProdutoDAO();

    // Modelo da tabela mostrando o histórico ou estado atual do estoque/produção
    // Para fins informativos da tela, exibiremos os produtos finais disponíveis e quantidades
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"ID Produto", "Nome do Produto", "Valor Un.", "Tipo"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tabela = new JTable(modelo);

    private final JComboBox<Produto> comboProdutoFinal = new JComboBox<>();
    private final JTextField campoQuantidade = new JTextField(10);

    public ProducaoView() {
        montarTela();
        carregarProdutosFinais();
        carregarTabelaProdutos();
    }

    private void montarTela() {
        setLayout(new BorderLayout(10, 10));
        add(UITheme.criarFaixaTitulo("Registro de Produção Industrial"), BorderLayout.NORTH);

        // Tabela no topo/centro dentro de um painel de rolagem
        add(new JScrollPane(tabela), BorderLayout.CENTER);
        tabela.getSelectionModel().addListSelectionListener(e -> preencherComSelecao());

        // Formulário de dados
        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        form.add(new JLabel("Selecionar Produto Final:")); form.add(comboProdutoFinal);
        form.add(new JLabel("Quantidade Produzida (unidades):")); form.add(campoQuantidade);

        // Botões de comando
        JButton btnRegistrar = new JButton("Registrar Produção");
        JButton btnLimpar = new JButton("Limpar Campo");
        JButton btnAtualizar = new JButton("Recarregar Lista");

        btnRegistrar.addActionListener(e -> registrarNovaProducao());
        btnLimpar.addActionListener(e -> campoQuantidade.setText(""));
        btnAtualizar.addActionListener(e -> carregarTabelaProdutos());

        JPanel botoes = new JPanel();
        botoes.add(btnRegistrar); botoes.add(btnLimpar); botoes.add(btnAtualizar);

        // Agrupamento da parte inferior (Sul)
        JPanel sul = new JPanel(new BorderLayout());
        sul.add(form, BorderLayout.CENTER);
        sul.add(botoes, BorderLayout.SOUTH);
        add(sul, BorderLayout.SOUTH);

        UITheme.estilizar(this);
    }

    /**
     * Carrega apenas os produtos do tipo PRODUTO_FINAL no ComboBox de escolha
     */
    private void carregarProdutosFinais() {
        try {
            comboProdutoFinal.removeAllItems();
            // Puxa todos os produtos cadastrados do seu ProdutoDAO existente
            List<Produto> lista = produtoDAO.listarTodos();
            for (Produto p : lista) {
                // Filtra para exibir apenas os Barris e Garrafas no ComboBox
                if ("PRODUTO_FINAL".equals(p.getTipo())) {
                    comboProdutoFinal.addItem(p);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar produtos no combo:\n" + e.getMessage());
        }
    }

    /**
     * Alimenta a tabela visual com os dados atuais dos produtos
     */
    private void carregarTabelaProdutos() {
        modelo.setRowCount(0);
        try {
            for (Produto p : produtoDAO.listarTodos()) {
                modelo.addRow(new Object[]{
                        p.getIdProduto(),
                        p.getNome(),
                        p.getValorUn(),
                        p.getTipo()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar tabela de produtos:\n" + e.getMessage());
        }
    }

    /**
     * Executa o autopreenchimento do ComboBox ao clicar em uma linha da tabela
     */
    private void preencherComSelecao() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) return;

        String tipo = String.valueOf(modelo.getValueAt(linha, 3));

        // Só permite selecionar no formulário se for um Produto Final
        if ("PRODUTO_FINAL".equals(tipo)) {
            int idProduto = (Integer) modelo.getValueAt(linha, 0);
            for (int i = 0; i < comboProdutoFinal.getItemCount(); i++) {
                if (comboProdutoFinal.getItemAt(i).getIdProduto() == idProduto) {
                    comboProdutoFinal.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    /**
     * Coleta as informações e aciona a regra de negócio do Controller
     */
    private void registrarNovaProducao() {
        Produto produtoSelecionado = (Produto) comboProdutoFinal.getSelectedItem();
        if (produtoSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um produto final válido para fabricação.");
            return;
        }

        int quantidade;
        try {
            quantidade = Integer.parseInt(campoQuantidade.getText().trim());
            if (quantidade <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Informe uma quantidade inteira válida e maior que zero.");
            return;
        }

        // Envia para o controller executar a transação (Validar Malte -> Retirar Malte -> Adicionar Chopp)
        boolean sucesso = producaoController.executarProducao(produtoSelecionado.getIdProduto(), quantidade);

        if (sucesso) {
            JOptionPane.showMessageDialog(this, "Produção confirmada com sucesso!\nO estoque de Malte foi consumido e o de Chopp reabastecido.");
            campoQuantidade.setText("");
            carregarTabelaProdutos(); // Atualiza a tabela para exibir os novos saldos de estoque
        } else {
            JOptionPane.showMessageDialog(this, "Falha na produção!\nEstoque insuficiente de matéria-prima (Malte) para concluir esta quantidade.", "Estoque Insuficiente", JOptionPane.WARNING_MESSAGE);
        }
    }
}