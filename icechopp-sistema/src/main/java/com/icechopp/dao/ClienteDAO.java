package com.icechopp.dao;

import com.icechopp.model.Cliente;
import com.icechopp.util.ConexaoBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public void inserir(Cliente c) throws SQLException {
        String sql = "INSERT INTO cliente (nome, endereco, telefone, email) VALUES (?, ?, ?, ?)";
        try (Connection con = ConexaoBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getNome());
            ps.setString(2, c.getEndereco());
            ps.setLong(3, c.getTelefone());
            ps.setString(4, c.getEmail());
            ps.executeUpdate();
            try (ResultSet gk = ps.getGeneratedKeys()) {
                if (gk.next()) c.setIdCliente(gk.getInt(1));
            }
        }
    }

    public void atualizar(Cliente c) throws SQLException {
        String sql = "UPDATE cliente SET nome=?, endereco=?, telefone=?, email=? WHERE id_cliente=?";
        try (Connection con = ConexaoBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getNome());
            ps.setString(2, c.getEndereco());
            ps.setLong(3, c.getTelefone());
            ps.setString(4, c.getEmail());
            ps.setInt(5, c.getIdCliente());
            ps.executeUpdate();
        }
    }

    public void deletar(int idCliente) throws SQLException {
        String sql = "DELETE FROM cliente WHERE id_cliente=?";
        try (Connection con = ConexaoBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            ps.executeUpdate();
        }
    }

    public List<Cliente> listarTodos() throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM cliente ORDER BY nome";
        try (Connection con = ConexaoBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public Cliente buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM cliente WHERE id_cliente=?";
        try (Connection con = ConexaoBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    private Cliente mapear(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setIdCliente(rs.getInt("id_cliente"));
        c.setNome(rs.getString("nome"));
        c.setEndereco(rs.getString("endereco"));
        c.setTelefone(rs.getLong("telefone"));
        c.setEmail(rs.getString("email"));
        return c;
    }
}
