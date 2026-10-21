package com.icechopp.dao;

import com.icechopp.model.Motorista;
import com.icechopp.util.ConexaoBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MotoristaDAO {

    public void inserir(Motorista m) throws SQLException {
        String sql = "INSERT INTO motorista (cpf, endereco, telefone, placa_caminhao, nome, salario) VALUES (?,?,?,?,?,?)";
        try (Connection con = ConexaoBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, m.getCpf());
            ps.setString(2, m.getEndereco());
            ps.setLong(3, m.getTelefone());
            ps.setString(4, m.getPlacaCaminhao());
            ps.setString(5, m.getNome());
            ps.setDouble(6, m.getSalario());
            ps.executeUpdate();
            try (ResultSet gk = ps.getGeneratedKeys()) {
                if (gk.next()) m.setIdMotorista(gk.getInt(1));
            }
        }
    }

    public void atualizar(Motorista m) throws SQLException {
        String sql = "UPDATE motorista SET cpf=?, endereco=?, telefone=?, placa_caminhao=?, nome=?, salario=? WHERE id_motorista=?";
        try (Connection con = ConexaoBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, m.getCpf());
            ps.setString(2, m.getEndereco());
            ps.setLong(3, m.getTelefone());
            ps.setString(4, m.getPlacaCaminhao());
            ps.setString(5, m.getNome());
            ps.setDouble(6, m.getSalario());
            ps.setInt(7, m.getIdMotorista());
            ps.executeUpdate();
        }
    }

    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM motorista WHERE id_motorista=?";
        try (Connection con = ConexaoBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Motorista> listarTodos() throws SQLException {
        List<Motorista> lista = new ArrayList<>();
        String sql = "SELECT * FROM motorista ORDER BY nome";
        try (Connection con = ConexaoBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    private Motorista mapear(ResultSet rs) throws SQLException {
        Motorista m = new Motorista();
        m.setIdMotorista(rs.getInt("id_motorista"));
        m.setCpf(rs.getString("cpf"));
        m.setEndereco(rs.getString("endereco"));
        m.setTelefone(rs.getLong("telefone"));
        m.setPlacaCaminhao(rs.getString("placa_caminhao"));
        m.setNome(rs.getString("nome"));
        m.setSalario(rs.getDouble("salario"));
        return m;
    }
}
