package com.icechopp.controller;

import com.icechopp.dao.EstoqueDAO;
import com.icechopp.dao.PedidoDAO;
import com.icechopp.dao.VendaDAO;
import com.icechopp.model.Pedido;
import com.icechopp.model.Venda;
import com.icechopp.util.ConexaoBD;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Orquestra a criação de uma venda completa: grava o cabeçalho da venda,
 * gera um pedido para cada item (relacionado a um motorista, se informado)
 * e dá baixa no estoque — tudo em uma única transação.
 */
public class VendaController {

    private final VendaDAO vendaDAO = new VendaDAO();
    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final EstoqueDAO estoqueDAO = new EstoqueDAO();

    public Venda realizarVenda(int idCliente, String cpfVendedor, List<ItemVendaTemp> itens,
                                Integer idMotorista, java.time.LocalDate dataEntrega) throws SQLException {
        if (itens == null || itens.isEmpty()) {
            throw new IllegalArgumentException("A venda precisa ter ao menos um item.");
        }

        double total = itens.stream().mapToDouble(ItemVendaTemp::getSubtotal).sum();

        Connection con = null;
        try {
            con = ConexaoBD.conectar();
            con.setAutoCommit(false);

            // 1) valida estoque de todos os itens antes de gravar qualquer coisa
            for (ItemVendaTemp item : itens) {
                var est = estoqueDAO.buscarPorProduto(item.getIdProduto());
                if (est == null || est.getQuantidade() < item.getQuantidade()) {
                    throw new SQLException("Estoque insuficiente para o produto: " + item.getNomeProduto());
                }
            }

            // 2) grava o cabeçalho da venda
            Venda venda = new Venda();
            venda.setIdCliente(idCliente);
            venda.setCpfUsuario(cpfVendedor);
            venda.setDataVenda(LocalDateTime.now());
            venda.setValorTotal(total);
            vendaDAO.inserir(con, venda);

            // 3) gera um pedido por item e dá baixa no estoque
            for (ItemVendaTemp item : itens) {
                Pedido pedido = new Pedido();
                pedido.setIdVenda(venda.getIdVenda());
                pedido.setIdProduto(item.getIdProduto());
                pedido.setNomeProduto(item.getNomeProduto());
                pedido.setQuantidade(item.getQuantidade());
                pedido.setDataEntrega(dataEntrega);
                pedido.setIdMotorista(idMotorista);
                pedido.setStatus("PENDENTE");
                pedidoDAO.inserir(con, pedido);

                boolean ok = estoqueDAO.baixa(item.getIdProduto(), item.getQuantidade());
                if (!ok) throw new SQLException("Falha ao dar baixa no estoque do produto: " + item.getNomeProduto());
            }

            con.commit();
            return venda;
        } catch (SQLException e) {
            if (con != null) con.rollback();
            throw e;
        } finally {
            if (con != null) {
                con.setAutoCommit(true);
                con.close();
            }
        }
    }
}
