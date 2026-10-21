package com.icechopp.view;

import com.icechopp.controller.ItemVendaTemp;
import com.icechopp.controller.VendaController;
import com.icechopp.dao.ClienteDAO;
import com.icechopp.dao.MotoristaDAO;
import com.icechopp.dao.ProdutoDAO;
import com.icechopp.model.Cliente;
import com.icechopp.model.Motorista;
import com.icechopp.model.Produto;
import com.icechopp.util.SessaoUsuario;
import com.icechopp.util.UITheme;
import com.icechopp.view.components.DatePickerField;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VendaView extends JPanel {

    private final VendaController controller = new VendaController();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final ProdutoDAO produtoDAO = new ProdutoDAO();
    private final MotoristaDAO motoristaDAO = new MotoristaDAO();

    private List<Cliente> clientesTodos = new ArrayList<>();
    private List<Produto> produtosTodos = new ArrayList<>();

    private final JTextField campoBuscaCliente = new JTextField(18);
    private final JComboBox<Cliente> comboCliente = new JComboBox<>();
    private final JComboBox<Motorista> comboMotorista = new JComboBox<>();
    private final DatePickerField campoDataEntrega = new DatePickerField();

    private final JTextField campoBuscaProduto = new JTextField(18);
    private final JComboBox<Produto> comboProduto = new JComboBox<>();
    private final JTextField campoQuantidade = new JTextField(6);

    private final List<ItemVendaTemp> carrinho = new ArrayList<>();
    private final DefaultTableModel modeloCarrinho = new DefaultTableModel(
            new Object[]{"Produto", "Qtd", "Valor Un.", "Subtotal"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tabelaCarrinho = new JTable(modeloCarrinho);
    private final JLabel labelTotal = new JLabel("Total: R$ 0,00");

    public VendaView() {
        super(new BorderLayout(10, 10));
        montarTela();
        carregarCombos();
    }

    private void montarTela() {
        add(UITheme.criarFaixaTitulo("Realizar Venda"), BorderLayout.NORTH);

        JPanel corpo = new JPanel(new BorderLayout(10, 10));
        corpo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel painelCliente = new JPanel(new GridLayout(0, 2, 6, 6));
        painelCliente.setBorder(BorderFactory.createTitledBorder("Cliente e entrega"));
        painelCliente.add(new JLabel("Pesquisar cliente (nome, telefone ou e-mail):"));
        painelCliente.add(campoBuscaCliente);
        painelCliente.add(new JLabel("Cliente:"));
        painelCliente.add(comboCliente);
        painelCliente.add(new JLabel("Motorista (entrega):"));
        painelCliente.add(comboMotorista);
        painelCliente.add(new JLabel("Data de entrega:"));
        painelCliente.add(campoDataEntrega);

        JPanel itemPanel = new JPanel(new GridLayout(0, 2, 6, 6));
        itemPanel.setBorder(BorderFactory.createTitledBorder("Adicionar item"));
        itemPanel.add(new JLabel("Pesquisar produto (nome ou tipo):"));
        itemPanel.add(campoBuscaProduto);
        itemPanel.add(new JLabel("Produto:"));
        itemPanel.add(comboProduto);
        itemPanel.add(new JLabel("Quantidade:"));
        itemPanel.add(campoQuantidade);

        JButton btnAdicionar = new JButton("Adicionar ao carrinho");
        btnAdicionar.addActionListener(e -> adicionarItem());

        JPanel itemComBotao = new JPanel(new BorderLayout());
        itemComBotao.add(itemPanel, BorderLayout.CENTER);
        JPanel wrapperBotaoAdicionar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        wrapperBotaoAdicionar.add(btnAdicionar);
        itemComBotao.add(wrapperBotaoAdicionar, BorderLayout.SOUTH);

        JPanel norte = new JPanel();
        norte.setLayout(new BoxLayout(norte, BoxLayout.Y_AXIS));
        norte.add(painelCliente);
        norte.add(itemComBotao);

        corpo.add(norte, BorderLayout.NORTH);
        corpo.add(new JScrollPane(tabelaCarrinho), BorderLayout.CENTER);

        JPanel sul = new JPanel(new BorderLayout());
        labelTotal.setFont(new Font("SansSerif", Font.BOLD, 15));
        labelTotal.setForeground(UITheme.NAVY);
        labelTotal.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        JButton btnFinalizar = new JButton("Finalizar Venda");
        btnFinalizar.addActionListener(e -> finalizarVenda());
        JButton btnRemoverItem = new JButton("Remover item selecionado");
        btnRemoverItem.addActionListener(e -> removerItem());

        JPanel botoesSul = new JPanel();
        botoesSul.add(btnRemoverItem);
        botoesSul.add(btnFinalizar);

        sul.add(labelTotal, BorderLayout.WEST);
        sul.add(botoesSul, BorderLayout.EAST);
        corpo.add(sul, BorderLayout.SOUTH);

        add(corpo, BorderLayout.CENTER);

        campoBuscaCliente.getDocument().addDocumentListener(filtro(() -> filtrarClientes(campoBuscaCliente.getText())));
        campoBuscaProduto.getDocument().addDocumentListener(filtro(() -> filtrarProdutos(campoBuscaProduto.getText())));

        UITheme.estilizar(this);
    }

    private DocumentListener filtro(Runnable acao) {
        return new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { acao.run(); }
            @Override public void removeUpdate(DocumentEvent e) { acao.run(); }
            @Override public void changedUpdate(DocumentEvent e) { acao.run(); }
        };
    }

    private void carregarCombos() {
        try {
            clientesTodos = clienteDAO.listarTodos();
            comboCliente.removeAllItems();
            for (Cliente c : clientesTodos) comboCliente.addItem(c);

            produtosTodos = produtoDAO.listarPorTipo("PRODUTO_FINAL");
            comboProduto.removeAllItems();
            for (Produto p : produtosTodos) comboProduto.addItem(p);

            comboMotorista.removeAllItems();
            for (Motorista m : motoristaDAO.listarTodos()) comboMotorista.addItem(m);

            if (comboCliente.getItemCount() == 0) {
                JOptionPane.showMessageDialog(this, "Nenhum cliente cadastrado. Cadastre um cliente antes de vender.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados:\n" + e.getMessage());
        }
    }

    private void filtrarClientes(String termo) {
        String alvo = termo == null ? "" : termo.trim().toLowerCase(Locale.ROOT);
        Cliente selecionadoAntes = (Cliente) comboCliente.getSelectedItem();
        comboCliente.removeAllItems();
        for (Cliente c : clientesTodos) {
            boolean casaNome = c.getNome() != null && c.getNome().toLowerCase(Locale.ROOT).contains(alvo);
            boolean casaTelefone = String.valueOf(c.getTelefone()).contains(alvo);
            boolean casaEmail = c.getEmail() != null && c.getEmail().toLowerCase(Locale.ROOT).contains(alvo);
            if (alvo.isEmpty() || casaNome || casaTelefone || casaEmail) {
                comboCliente.addItem(c);
            }
        }
        if (selecionadoAntes != null) {
            for (int i = 0; i < comboCliente.getItemCount(); i++) {
                if (comboCliente.getItemAt(i).getIdCliente() == selecionadoAntes.getIdCliente()) {
                    comboCliente.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void filtrarProdutos(String termo) {
        String alvo = termo == null ? "" : termo.trim().toLowerCase(Locale.ROOT);
        Produto selecionadoAntes = (Produto) comboProduto.getSelectedItem();
        comboProduto.removeAllItems();
        for (Produto p : produtosTodos) {
            boolean casaNome = p.getNome() != null && p.getNome().toLowerCase(Locale.ROOT).contains(alvo);
            boolean casaTipo = p.getTipo() != null && p.getTipo().toLowerCase(Locale.ROOT).contains(alvo);
            if (alvo.isEmpty() || casaNome || casaTipo) {
                comboProduto.addItem(p);
            }
        }
        if (selecionadoAntes != null) {
            for (int i = 0; i < comboProduto.getItemCount(); i++) {
                if (comboProduto.getItemAt(i).getIdProduto() == selecionadoAntes.getIdProduto()) {
                    comboProduto.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void adicionarItem() {
        Produto produto = (Produto) comboProduto.getSelectedItem();
        if (produto == null) {
            JOptionPane.showMessageDialog(this, "Selecione um produto.");
            return;
        }
        int quantidade;
        try {
            quantidade = Integer.parseInt(campoQuantidade.getText().trim());
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Quantidade inválida.");
            return;
        }
        if (quantidade <= 0) {
            JOptionPane.showMessageDialog(this, "Quantidade deve ser maior que zero.");
            return;
        }
        double valorUn = produto.getValorUn() != null ? produto.getValorUn() : 0.0;
        ItemVendaTemp item = new ItemVendaTemp(produto.getIdProduto(), produto.getNome(), quantidade, valorUn);
        carrinho.add(item);
        modeloCarrinho.addRow(new Object[]{item.getNomeProduto(), item.getQuantidade(), item.getValorUnitario(), item.getSubtotal()});
        atualizarTotal();
        campoQuantidade.setText("");
    }

    private void removerItem() {
        int linha = tabelaCarrinho.getSelectedRow();
        if (linha < 0) return;
        carrinho.remove(linha);
        modeloCarrinho.removeRow(linha);
        atualizarTotal();
    }

    private void atualizarTotal() {
        double total = carrinho.stream().mapToDouble(ItemVendaTemp::getSubtotal).sum();
        labelTotal.setText(String.format("Total: R$ %.2f", total));
    }

    private void finalizarVenda() {
        Cliente cliente = (Cliente) comboCliente.getSelectedItem();
        if (cliente == null) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente.");
            return;
        }
        if (carrinho.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Adicione ao menos um item ao carrinho.");
            return;
        }
        if (campoDataEntrega.isTextoInvalido()) {
            JOptionPane.showMessageDialog(this, "Data de entrega inválida. Use o formato aaaa-mm-dd ou o calendário.");
            return;
        }
        Motorista motorista = (Motorista) comboMotorista.getSelectedItem();
        LocalDate dataEntrega = campoDataEntrega.getDate();

        try {
            controller.realizarVenda(cliente.getIdCliente(), SessaoUsuario.getUsuarioLogado().getCpf(),
                    carrinho, motorista != null ? motorista.getIdMotorista() : null, dataEntrega);
            JOptionPane.showMessageDialog(this, "Venda registrada com sucesso! Pedido(s) gerado(s) para entrega.");
            carrinho.clear();
            modeloCarrinho.setRowCount(0);
            atualizarTotal();
            campoDataEntrega.setDate(null);
            campoBuscaCliente.setText("");
            campoBuscaProduto.setText("");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao finalizar venda:\n" + e.getMessage());
        }
    }
}
