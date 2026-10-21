package com.icechopp.view;

import com.icechopp.dao.ClienteDAO;
import com.icechopp.dao.CompraDAO;
import com.icechopp.dao.FornecedorDAO;
import com.icechopp.dao.ProdutoDAO;
import com.icechopp.dao.VendaDAO;
import com.icechopp.model.Cliente;
import com.icechopp.model.Compra;
import com.icechopp.model.Fornecedor;
import com.icechopp.model.Produto;
import com.icechopp.model.Venda;
import com.icechopp.util.UITheme;
import com.icechopp.view.components.DatePickerField;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Fluxo de Caixa: consolida o histórico de vendas (entradas) e compras
 * (saídas) em uma única linha do tempo, com saldo, e permite filtrar
 * o período de análise por uma janela de datas (calendário).
 */
public class CaixaView extends JPanel {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final VendaDAO vendaDAO = new VendaDAO();
    private final CompraDAO compraDAO = new CompraDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final FornecedorDAO fornecedorDAO = new FornecedorDAO();
    private final ProdutoDAO produtoDAO = new ProdutoDAO();

    private final DatePickerField campoDataInicio = new DatePickerField();
    private final DatePickerField campoDataFim = new DatePickerField();

    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"Data", "Tipo", "Descrição", "Entrada (R$)", "Saída (R$)"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tabela = new JTable(modelo);

    private final JLabel labelEntradas = UITheme.criarLabelDestaque("Entradas: R$ 0,00", new Color(0x1B, 0x8A, 0x3A));
    private final JLabel labelSaidas = UITheme.criarLabelDestaque("Saídas: R$ 0,00", UITheme.RED_DANGER);
    private final JLabel labelSaldo = UITheme.criarLabelDestaque("Saldo: R$ 0,00", UITheme.NAVY);

    public CaixaView() {
        super(new BorderLayout(10, 10));
        montarTela();
        // período padrão: últimos 30 dias
        campoDataFim.setDate(LocalDate.now());
        campoDataInicio.setDate(LocalDate.now().minusDays(30));
        carregar();
    }

    private void montarTela() {
        add(UITheme.criarFaixaTitulo("Fluxo de Caixa"), BorderLayout.NORTH);

        JPanel corpo = new JPanel(new BorderLayout(10, 10));
        corpo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel filtro = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        filtro.setBorder(BorderFactory.createTitledBorder("Período de análise"));
        filtro.add(new JLabel("De:"));
        filtro.add(campoDataInicio);
        filtro.add(new JLabel("Até:"));
        filtro.add(campoDataFim);
        JButton btnFiltrar = new JButton("Filtrar");
        btnFiltrar.addActionListener(e -> carregar());
        JButton btnUltimos7 = new JButton("Últimos 7 dias");
        btnUltimos7.addActionListener(e -> aplicarAtalho(7));
        JButton btnUltimos30 = new JButton("Últimos 30 dias");
        btnUltimos30.addActionListener(e -> aplicarAtalho(30));
        JButton btnMesAtual = new JButton("Mês atual");
        btnMesAtual.addActionListener(e -> {
            LocalDate hoje = LocalDate.now();
            campoDataInicio.setDate(hoje.withDayOfMonth(1));
            campoDataFim.setDate(hoje);
            carregar();
        });
        JButton btnTudo = new JButton("Tudo");
        btnTudo.addActionListener(e -> {
            campoDataInicio.setDate(null);
            campoDataFim.setDate(null);
            carregar();
        });
        filtro.add(btnFiltrar);
        filtro.add(btnUltimos7);
        filtro.add(btnUltimos30);
        filtro.add(btnMesAtual);
        filtro.add(btnTudo);

        JPanel resumo = new JPanel(new GridLayout(1, 3, 12, 0));
        resumo.setBorder(BorderFactory.createEmptyBorder(8, 4, 4, 4));
        resumo.add(labelEntradas);
        resumo.add(labelSaidas);
        resumo.add(labelSaldo);

        JPanel norte = new JPanel();
        norte.setLayout(new BoxLayout(norte, BoxLayout.Y_AXIS));
        norte.add(filtro);
        norte.add(resumo);

        corpo.add(norte, BorderLayout.NORTH);
        corpo.add(new JScrollPane(tabela), BorderLayout.CENTER);
        add(corpo, BorderLayout.CENTER);

        UITheme.estilizar(this);
        // labels de destaque não devem ficar navy (o estilizar genérico sobrescreveria a cor) — reforça as cores após estilizar:
        labelEntradas.setForeground(new Color(0x1B, 0x8A, 0x3A));
        labelSaidas.setForeground(UITheme.RED_DANGER);
        labelSaldo.setForeground(UITheme.NAVY);
    }

    private void aplicarAtalho(int dias) {
        campoDataFim.setDate(LocalDate.now());
        campoDataInicio.setDate(LocalDate.now().minusDays(dias));
        carregar();
    }

    private boolean dentroDoPeriodo(LocalDate data, LocalDate inicio, LocalDate fim) {
        if (inicio != null && data.isBefore(inicio)) return false;
        if (fim != null && data.isAfter(fim)) return false;
        return true;
    }

    private void carregar() {
        if (campoDataInicio.isTextoInvalido() || campoDataFim.isTextoInvalido()) {
            JOptionPane.showMessageDialog(this, "Data inválida. Use o formato aaaa-mm-dd ou o calendário.");
            return;
        }
        LocalDate inicio = campoDataInicio.getDate();
        LocalDate fim = campoDataFim.getDate();
        if (inicio != null && fim != null && inicio.isAfter(fim)) {
            JOptionPane.showMessageDialog(this, "A data inicial não pode ser depois da data final.");
            return;
        }

        modelo.setRowCount(0);
        double totalEntradas = 0;
        double totalSaidas = 0;

        try {
            Map<Integer, String> nomesClientes = new HashMap<>();
            for (Cliente c : clienteDAO.listarTodos()) nomesClientes.put(c.getIdCliente(), c.getNome());

            Map<Integer, String> nomesFornecedores = new HashMap<>();
            for (Fornecedor f : fornecedorDAO.listarTodos()) nomesFornecedores.put(f.getIdFornecedor(), f.getNome());

            Map<Integer, String> nomesProdutos = new HashMap<>();
            for (Produto p : produtoDAO.listarTodos()) nomesProdutos.put(p.getIdProduto(), p.getNome());

            List<Object[]> linhas = new ArrayList<>();

            for (Venda v : vendaDAO.listarTodas()) {
                LocalDate data = v.getDataVenda().toLocalDate();
                if (!dentroDoPeriodo(data, inicio, fim)) continue;
                String cliente = nomesClientes.getOrDefault(v.getIdCliente(), "Cliente #" + v.getIdCliente());
                String descricao = "Venda #" + v.getIdVenda() + " - " + cliente;
                linhas.add(new Object[]{data, "Venda", descricao, v.getValorTotal(), 0.0});
                totalEntradas += v.getValorTotal();
            }

            for (Compra c : compraDAO.listarTodas()) {
                LocalDate data = c.getDataCompra().toLocalDate();
                if (!dentroDoPeriodo(data, inicio, fim)) continue;
                String produto = nomesProdutos.getOrDefault(c.getIdProduto(), "Produto #" + c.getIdProduto());
                String fornecedor = nomesFornecedores.getOrDefault(c.getIdFornecedor(), "Fornecedor #" + c.getIdFornecedor());
                String descricao = "Compra #" + c.getIdCompra() + " - " + produto + " (" + fornecedor + ")";
                linhas.add(new Object[]{data, "Compra", descricao, 0.0, c.getValor()});
                totalSaidas += c.getValor();
            }

            linhas.sort(Comparator.comparing((Object[] l) -> (LocalDate) l[0]).reversed());

            for (Object[] linha : linhas) {
                LocalDate data = (LocalDate) linha[0];
                double entrada = (double) linha[3];
                double saida = (double) linha[4];
                modelo.addRow(new Object[]{
                        data.format(FORMATO_DATA),
                        linha[1],
                        linha[2],
                        entrada > 0 ? String.format(new Locale("pt", "BR"), "%,.2f", entrada) : "-",
                        saida > 0 ? String.format(new Locale("pt", "BR"), "%,.2f", saida) : "-"
                });
            }

            double saldo = totalEntradas - totalSaidas;
            labelEntradas.setText(String.format(new Locale("pt", "BR"), "Entradas: R$ %,.2f", totalEntradas));
            labelSaidas.setText(String.format(new Locale("pt", "BR"), "Saídas: R$ %,.2f", totalSaidas));
            labelSaldo.setText(String.format(new Locale("pt", "BR"), "Saldo: R$ %,.2f", saldo));
            labelSaldo.setForeground(saldo >= 0 ? new Color(0x1B, 0x8A, 0x3A) : UITheme.RED_DANGER);

            if (linhas.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nenhuma venda ou compra encontrada no período selecionado.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar fluxo de caixa:\n" + e.getMessage());
        }
    }
}
