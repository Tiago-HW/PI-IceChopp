package com.icechopp.dao;

import com.icechopp.model.Caminhao;
import com.icechopp.util.ConexaoBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CaminhaoDAO {

    public void inserir(Caminhao c) throws SQLException {
        String sql = "INSERT INTO caminhao (placa, marca, ano, modelo) VALUES (?,?,?,?)";
        try (Connection con = ConexaoBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getPlaca());
            ps.setString(2, c.getMarca());
            ps.setInt(3, c.getAno());
            ps.setString(4, c.getModelo());
            ps.executeUpdate();
        }
    }

    public void atualizar(Caminhao c) throws SQLException {
        String sql = "UPDATE caminhao SET marca=?, ano=?, modelo=? WHERE placa=?";
        try (Connection con = ConexaoBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getMarca());
            ps.setInt(2, c.getAno());
            ps.setString(3, c.getModelo());
            ps.setString(4, c.getPlaca());
            ps.executeUpdate();
        }
    }

    public void deletar(String placa) throws SQLException {
        String sql = "DELETE FROM caminhao WHERE placa=?";
        try (Connection con = ConexaoBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, placa);
            ps.executeUpdate();
        }
    }

    public List<Caminhao> listarTodos() throws SQLException {
        List<Caminhao> lista = new ArrayList<>();
        String sql = "SELECT * FROM caminhao ORDER BY placa";
        try (Connection con = ConexaoBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    private Caminhao mapear(ResultSet rs) throws SQLException {
        Caminhao c = new Caminhao();
        c.setPlaca(rs.getString("placa"));
        c.setMarca(rs.getString("marca"));
        c.setAno(rs.getInt("ano"));
        c.setModelo(rs.getString("modelo"));
        return c;
    }
}
