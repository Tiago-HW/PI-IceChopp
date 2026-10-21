package com.icechopp.dao;

import com.icechopp.model.Produto;
import com.icechopp.util.ConexaoBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {

    public void inserir(Produto p) throws SQLException {
        String sql = "INSERT INTO produto (nome, valorUn, tipo) VALUES (?, ?, ?)";
        try (Connection con = ConexaoBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getNome());
            if (p.getValorUn() != null) ps.setDouble(2, p.getValorUn()); else ps.setNull(2, Types.DOUBLE);
            ps.setString(3, p.getTipo());
            ps.executeUpdate();
            try (ResultSet gk = ps.getGeneratedKeys()) {
                if (gk.next()) p.setIdProduto(gk.getInt(1));
            }
        }
    }

    public void atualizar(Produto p) throws SQLException {
        String sql = "UPDATE produto SET nome=?, valorUn=?, tipo=? WHERE id_produto=?";
        try (Connection con = ConexaoBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getNome());
            if (p.getValorUn() != null) ps.setDouble(2, p.getValorUn()); else ps.setNull(2, Types.DOUBLE);
            ps.setString(3, p.getTipo());
            ps.setInt(4, p.getIdProduto());
            ps.executeUpdate();
        }
    }

    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM produto WHERE id_produto=?";
        try (Connection con = ConexaoBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Produto> listarTodos() throws SQLException {
        List<Produto> lista = new ArrayList<>();
        String sql = "SELECT * FROM produto ORDER BY nome";
        try (Connection con = ConexaoBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public List<Produto> listarPorTipo(String tipo) throws SQLException {
        List<Produto> lista = new ArrayList<>();
        String sql = "SELECT * FROM produto WHERE tipo=? ORDER BY nome";
        try (Connection con = ConexaoBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tipo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public Produto buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM produto WHERE id_produto=?";
        try (Connection con = ConexaoBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    private Produto mapear(ResultSet rs) throws SQLException {
        Produto p = new Produto();
        p.setIdProduto(rs.getInt("id_produto"));
        p.setNome(rs.getString("nome"));
        double v = rs.getDouble("valorUn");
        p.setValorUn(rs.wasNull() ? null : v);
        p.setTipo(rs.getString("tipo"));
        return p;
    }
}
