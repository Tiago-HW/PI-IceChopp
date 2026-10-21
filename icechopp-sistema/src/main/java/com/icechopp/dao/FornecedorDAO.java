package com.icechopp.dao;

import com.icechopp.model.Fornecedor;
import com.icechopp.util.ConexaoBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FornecedorDAO {

    public void inserir(Fornecedor f) throws SQLException {
        String sql = "INSERT INTO fornecedor (nome, email, telefone) VALUES (?, ?, ?)";
        try (Connection con = ConexaoBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, f.getNome());
            ps.setString(2, f.getEmail());
            ps.setString(3, f.getTelefone());
            ps.executeUpdate();
            try (ResultSet gk = ps.getGeneratedKeys()) {
                if (gk.next()) f.setIdFornecedor(gk.getInt(1));
            }
        }
    }

    public void atualizar(Fornecedor f) throws SQLException {
        String sql = "UPDATE fornecedor SET nome=?, email=?, telefone=? WHERE id_fornecedor=?";
        try (Connection con = ConexaoBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, f.getNome());
            ps.setString(2, f.getEmail());
            ps.setString(3, f.getTelefone());
            ps.setInt(4, f.getIdFornecedor());
            ps.executeUpdate();
        }
    }

    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM fornecedor WHERE id_fornecedor=?";
        try (Connection con = ConexaoBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Fornecedor> listarTodos() throws SQLException {
        List<Fornecedor> lista = new ArrayList<>();
        String sql = "SELECT * FROM fornecedor ORDER BY nome";
        try (Connection con = ConexaoBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    private Fornecedor mapear(ResultSet rs) throws SQLException {
        Fornecedor f = new Fornecedor();
        f.setIdFornecedor(rs.getInt("id_fornecedor"));
        f.setNome(rs.getString("nome"));
        f.setEmail(rs.getString("email"));
        f.setTelefone(rs.getString("telefone"));
        return f;
    }
}
