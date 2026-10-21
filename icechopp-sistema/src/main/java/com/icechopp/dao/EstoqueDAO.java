package com.icechopp.dao;

import com.icechopp.model.Estoque;
import com.icechopp.util.ConexaoBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstoqueDAO {

    /** Cria (ou soma quantidade, se já existir) o registro de estoque de um produto. */
    public void entrada(int idProduto, String nomeProduto, int quantidade, Double valorUn, java.time.LocalDate dataEntrada) throws SQLException {
        Estoque existente = buscarPorProduto(idProduto);
        try (Connection con = ConexaoBD.conectar()) {
            if (existente == null) {
                String sql = "INSERT INTO estoque (id_produto, nome_produto, quantidade, valorUn) VALUES (?,?,?,?)";
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setInt(1, idProduto);
                    ps.setString(2, nomeProduto);
                    ps.setInt(3, quantidade);
                    if (valorUn != null) ps.setDouble(4, valorUn); else ps.setNull(4, Types.DOUBLE);
                    ps.executeUpdate();
                }
            } else {
                String sql = "UPDATE estoque SET quantidade = quantidade + ? WHERE id_produto=?";
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setInt(1, quantidade);
                    ps.setInt(2, idProduto);
                    ps.executeUpdate();
                }
            }
        }
    }

    /** Dá baixa em estoque (usado nas vendas). Retorna false se não houver quantidade suficiente. */
    public boolean baixa(int idProduto, int quantidade) throws SQLException {
        Estoque atual = buscarPorProduto(idProduto);
        if (atual == null || atual.getQuantidade() < quantidade) return false;
        String sql = "UPDATE estoque SET quantidade = quantidade - ? WHERE id_produto=?";
        try (Connection con = ConexaoBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, quantidade);
            ps.setInt(2, idProduto);
            ps.executeUpdate();
        }
        return true;
    }

    public Estoque buscarPorProduto(int idProduto) throws SQLException {
        String sql = "SELECT * FROM estoque WHERE id_produto=?";
        try (Connection con = ConexaoBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProduto);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    public List<Estoque> listarTodos() throws SQLException {
        List<Estoque> lista = new ArrayList<>();
        String sql = "SELECT * FROM estoque ORDER BY id_produto";
        try (Connection con = ConexaoBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    private Estoque mapear(ResultSet rs) throws SQLException {
        Estoque e = new Estoque();
        e.setIdProduto(rs.getInt("id_produto"));
        e.setNomeProduto(rs.getString("nome_produto"));
        e.setQuantidade(rs.getInt("quantidade"));
        double v = rs.getDouble("valorUn");
        e.setValorUn(rs.wasNull() ? null : v);
        return e;
    }
}
