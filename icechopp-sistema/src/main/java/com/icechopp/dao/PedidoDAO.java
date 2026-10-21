package com.icechopp.dao;

import com.icechopp.model.Pedido;
import com.icechopp.util.ConexaoBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {

    /** Insere o pedido usando uma conexão já aberta (para participar de uma transação maior). */
    public void inserir(Connection con, Pedido p) throws SQLException {
        String sql = "INSERT INTO pedido (id_venda, id_produto, nome_produto, quantidade, data_entrega, id_motorista, status) " +
                "VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, p.getIdVenda());
            ps.setInt(2, p.getIdProduto());
            ps.setString(3, p.getNomeProduto());
            ps.setInt(4, p.getQuantidade());
            if (p.getDataEntrega() != null) ps.setDate(5, Date.valueOf(p.getDataEntrega())); else ps.setNull(5, Types.DATE);
            if (p.getIdMotorista() != null) ps.setInt(6, p.getIdMotorista()); else ps.setNull(6, Types.INTEGER);
            ps.setString(7, p.getStatus());
            ps.executeUpdate();
            try (ResultSet gk = ps.getGeneratedKeys()) {
                if (gk.next()) p.setIdPedido(gk.getInt(1));
            }
        }
    }

    public void atualizarStatus(int idPedido, String status) throws SQLException {
        String sql = "UPDATE pedido SET status=? WHERE id_pedido=?";
        try (Connection con = ConexaoBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, idPedido);
            ps.executeUpdate();
        }
    }

    public void atribuirMotorista(int idPedido, int idMotorista, java.time.LocalDate dataEntrega) throws SQLException {
        String sql = "UPDATE pedido SET id_motorista=?, data_entrega=? WHERE id_pedido=?";
        try (Connection con = ConexaoBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idMotorista);
            ps.setDate(2, Date.valueOf(dataEntrega));
            ps.setInt(3, idPedido);
            ps.executeUpdate();
        }
    }

    public List<Pedido> listarTodos() throws SQLException {
        List<Pedido> lista = new ArrayList<>();
        String sql = "SELECT * FROM pedido ORDER BY id_pedido DESC";
        try (Connection con = ConexaoBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public List<Pedido> listarPorVenda(int idVenda) throws SQLException {
        List<Pedido> lista = new ArrayList<>();
        String sql = "SELECT * FROM pedido WHERE id_venda=?";
        try (Connection con = ConexaoBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idVenda);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    private Pedido mapear(ResultSet rs) throws SQLException {
        Pedido p = new Pedido();
        p.setIdPedido(rs.getInt("id_pedido"));
        p.setIdVenda(rs.getInt("id_venda"));
        p.setIdProduto(rs.getInt("id_produto"));
        p.setNomeProduto(rs.getString("nome_produto"));
        p.setQuantidade(rs.getInt("quantidade"));
        Date d = rs.getDate("data_entrega");
        p.setDataEntrega(d != null ? d.toLocalDate() : null);
        int idMot = rs.getInt("id_motorista");
        p.setIdMotorista(rs.wasNull() ? null : idMot);
        p.setStatus(rs.getString("status"));
        return p;
    }
}
