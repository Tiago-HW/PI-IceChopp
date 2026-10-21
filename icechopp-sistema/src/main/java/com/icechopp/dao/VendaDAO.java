package com.icechopp.dao;

import com.icechopp.model.Venda;
import com.icechopp.util.ConexaoBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VendaDAO {

    /** Insere a venda usando uma conexão já aberta (para participar de uma transação maior). */
    public void inserir(Connection con, Venda v) throws SQLException {
        String sql = "INSERT INTO venda (id_cliente, cpf_usuario, data_venda, valor_total) VALUES (?,?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, v.getIdCliente());
            ps.setString(2, v.getCpfUsuario());
            ps.setTimestamp(3, Timestamp.valueOf(v.getDataVenda()));
            ps.setDouble(4, v.getValorTotal());
            ps.executeUpdate();
            try (ResultSet gk = ps.getGeneratedKeys()) {
                if (gk.next()) v.setIdVenda(gk.getInt(1));
            }
        }
    }

    public List<Venda> listarTodas() throws SQLException {
        List<Venda> lista = new ArrayList<>();
        String sql = "SELECT * FROM venda ORDER BY data_venda DESC";
        try (Connection con = ConexaoBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    private Venda mapear(ResultSet rs) throws SQLException {
        Venda v = new Venda();
        v.setIdVenda(rs.getInt("id_venda"));
        v.setIdCliente(rs.getInt("id_cliente"));
        v.setCpfUsuario(rs.getString("cpf_usuario"));
        v.setDataVenda(rs.getTimestamp("data_venda").toLocalDateTime());
        v.setValorTotal(rs.getDouble("valor_total"));
        return v;
    }
}
