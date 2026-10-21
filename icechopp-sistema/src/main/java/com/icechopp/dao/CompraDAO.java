package com.icechopp.dao;

import com.icechopp.model.Compra;
import com.icechopp.util.ConexaoBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CompraDAO {

    public void inserir(Compra c) throws SQLException {
        String sql = "INSERT INTO compra (data_compra, valor, quantidade, id_fornecedor, id_produto, cpf_usuario) " +
                "VALUES (?,?,?,?,?,?)";
        try (Connection con = ConexaoBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setTimestamp(1, java.sql.Timestamp.valueOf(c.getDataCompra()));
            ps.setDouble(2, c.getValor());
            ps.setInt(3, c.getQuantidade());
            ps.setInt(4, c.getIdFornecedor());
            ps.setInt(5, c.getIdProduto());
            ps.setString(6, c.getCpfUsuario());
            ps.executeUpdate();
            try (ResultSet gk = ps.getGeneratedKeys()) {
                if (gk.next()) c.setIdCompra(gk.getInt(1));
            }
        }
    }

    public List<Compra> listarTodas() throws SQLException {
        List<Compra> lista = new ArrayList<>();
        String sql = "SELECT * FROM compra ORDER BY data_compra DESC";
        try (Connection con = ConexaoBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    private Compra mapear(ResultSet rs) throws SQLException {
        Compra c = new Compra();
        c.setIdCompra(rs.getInt("id_compra"));
        c.setDataCompra(rs.getTimestamp("data_compra").toLocalDateTime());
        c.setValor(rs.getDouble("valor"));
        c.setQuantidade(rs.getInt("quantidade"));
        c.setIdFornecedor(rs.getInt("id_fornecedor"));
        c.setIdProduto(rs.getInt("id_produto"));
        c.setCpfUsuario(rs.getString("cpf_usuario"));
        return c;
    }
}
