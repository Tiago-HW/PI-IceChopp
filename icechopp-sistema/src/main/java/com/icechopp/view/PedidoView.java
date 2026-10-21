package com.icechopp.view;

import com.icechopp.dao.MotoristaDAO;
import com.icechopp.util.UITheme;
import com.icechopp.dao.PedidoDAO;
import com.icechopp.model.Motorista;
import com.icechopp.model.Pedido;
import com.icechopp.view.components.DatePickerField;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Tela de acompanhamento de pedidos (entregas). Permite atribuir/trocar
 * o motorista responsável e atualizar o status da entrega.
 */
public class PedidoView extends JPanel {

    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final MotoristaDAO motoristaDAO = new MotoristaDAO();

    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"ID Pedido", "ID Venda", "Produto", "Qtd", "Data Entrega", "ID Motorista", "Status"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tabela = new JTable(modelo);

    private final JComboBox<Motorista> comboMotorista = new JComboBox<>();
    private final DatePickerField campoDataEntrega = new DatePickerField();
    private final JComboBox<String> comboStatus = new JComboBox<>(new String[]{"PENDENTE", "EM_ROTA", "ENTREGUE", "CANCELADO"});
    private Integer idPedidoSelecionado = null;

    public PedidoView() {
        montarTela();
        carregarMotoristas();
        carregar();
    }

    private void montarTela() {
        setLayout(new BorderLayout(10, 10));
        add(UITheme.criarFaixaTitulo("Pedidos / Entregas"), BorderLayout.NORTH);

        add(new JScrollPane(tabela), BorderLayout.CENTER);
        tabela.getSelectionModel().addListSelectionListener(e -> preencherComSelecao());

        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        form.add(new JLabel("Motorista:")); form.add(comboMotorista);
        form.add(new JLabel("Data de entrega:")); form.add(campoDataEntrega);
        form.add(new JLabel("Status:")); form.add(comboStatus);

        JButton btnAtribuir = new JButton("Atribuir motorista / data");
        JButton btnStatus = new JButton("Atualizar status");
        JButton btnAtualizar = new JButton("Recarregar lista");
        btnAtribuir.addActionListener(e -> atribuirMotorista());
        btnStatus.addActionListener(e -> atualizarStatus());
        btnAtualizar.addActionListener(e -> carregar());

        JPanel botoes = new JPanel();
        botoes.add(btnAtribuir); botoes.add(btnStatus); botoes.add(btnAtualizar);

        JPanel sul = new JPanel(new BorderLayout());
        sul.add(form, BorderLayout.CENTER);
        sul.add(botoes, BorderLayout.SOUTH);
        add(sul, BorderLayout.SOUTH);

        UITheme.estilizar(this);
    }

    private void carregarMotoristas() {
        try {
            comboMotorista.removeAllItems();
            List<Motorista> lista = motoristaDAO.listarTodos();
            for (Motorista m : lista) comboMotorista.addItem(m);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar motoristas:\n" + e.getMessage());
        }
    }

    private void carregar() {
        modelo.setRowCount(0);
        try {
            for (Pedido p : pedidoDAO.listarTodos()) {
                modelo.addRow(new Object[]{p.getIdPedido(), p.getIdVenda(), p.getNomeProduto(), p.getQuantidade(),
                        p.getDataEntrega(), p.getIdMotorista(), p.getStatus()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar pedidos:\n" + e.getMessage());
        }
    }

    private void preencherComSelecao() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) return;
        idPedidoSelecionado = (Integer) modelo.getValueAt(linha, 0);
        comboStatus.setSelectedItem(String.valueOf(modelo.getValueAt(linha, 6)));
        Object idMotorista = modelo.getValueAt(linha, 5);
        if (idMotorista != null) {
            for (int i = 0; i < comboMotorista.getItemCount(); i++) {
                if (comboMotorista.getItemAt(i).getIdMotorista() == (Integer) idMotorista) {
                    comboMotorista.setSelectedIndex(i);
                    break;
                }
            }
        }
        Object dataEntrega = modelo.getValueAt(linha, 4);
        campoDataEntrega.setDate(dataEntrega != null ? LocalDate.parse(String.valueOf(dataEntrega)) : null);
    }

    private void atribuirMotorista() {
        if (idPedidoSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um pedido na tabela.");
            return;
        }
        Motorista motorista = (Motorista) comboMotorista.getSelectedItem();
        if (motorista == null) {
            JOptionPane.showMessageDialog(this, "Cadastre e selecione um motorista.");
            return;
        }
        LocalDate data = campoDataEntrega.getDate();
        if (data == null) {
            JOptionPane.showMessageDialog(this, "Informe a data de entrega (digite aaaa-mm-dd ou use o calendário).");
            return;
        }
        try {
            pedidoDAO.atribuirMotorista(idPedidoSelecionado, motorista.getIdMotorista(), data);
            carregar();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao atribuir motorista:\n" + e.getMessage());
        }
    }

    private void atualizarStatus() {
        if (idPedidoSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um pedido na tabela.");
            return;
        }
        try {
            pedidoDAO.atualizarStatus(idPedidoSelecionado, (String) comboStatus.getSelectedItem());
            carregar();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar status:\n" + e.getMessage());
        }
    }
}
