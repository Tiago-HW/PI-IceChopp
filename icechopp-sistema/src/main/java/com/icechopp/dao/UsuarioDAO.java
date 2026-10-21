package com.icechopp.dao;

import com.icechopp.model.Usuario;
import com.icechopp.util.ConexaoBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public void inserir(Usuario u) throws SQLException {
        String sql = "INSERT INTO usuario (cpf, nome_funcionario, telefone, endereco, salario, login, senha, time) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexaoBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getCpf());
            ps.setString(2, u.getNomeFuncionario());
            ps.setLong(3, u.getTelefone());
            ps.setString(4, u.getEndereco());
            ps.setDouble(5, u.getSalario());
            ps.setString(6, u.getLogin());
            ps.setString(7, u.getSenha());
            ps.setString(8, u.getTime());
            ps.executeUpdate();
        }
    }

    public void atualizar(Usuario u) throws SQLException {
        String sql = "UPDATE usuario SET nome_funcionario=?, telefone=?, endereco=?, salario=?, " +
                "login=?, senha=?, time=? WHERE cpf=?";
        try (Connection con = ConexaoBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getNomeFuncionario());
            ps.setLong(2, u.getTelefone());
            ps.setString(3, u.getEndereco());
            ps.setDouble(4, u.getSalario());
            ps.setString(5, u.getLogin());
            ps.setString(6, u.getSenha());
            ps.setString(7, u.getTime());
            ps.setString(8, u.getCpf());
            ps.executeUpdate();
        }
    }

    public void deletar(String cpf) throws SQLException {
        String sql = "DELETE FROM usuario WHERE cpf=?";
        try (Connection con = ConexaoBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cpf);
            ps.executeUpdate();
        }
    }

    public Usuario autenticar(String login, String senha) throws SQLException {
        String sql = "SELECT * FROM usuario WHERE login=? AND senha=?";
        try (Connection con = ConexaoBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, login);
            ps.setString(2, senha);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    public List<Usuario> listarTodos() throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuario ORDER BY nome_funcionario";
        try (Connection con = ConexaoBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setCpf(rs.getString("cpf"));
        u.setNomeFuncionario(rs.getString("nome_funcionario"));
        u.setTelefone(rs.getLong("telefone"));
        u.setEndereco(rs.getString("endereco"));
        u.setSalario(rs.getDouble("salario"));
        u.setLogin(rs.getString("login"));
        u.setSenha(rs.getString("senha"));
        u.setTime(rs.getString("time"));
        return u;
    }
}
