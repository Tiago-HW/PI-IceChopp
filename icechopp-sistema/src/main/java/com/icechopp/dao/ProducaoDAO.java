package com.icechopp.dao;
import com.icechopp.model.Estoque;
import com.icechopp.model.Producao;
import com.icechopp.util.ConexaoBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProducaoDAO {

    private int ObtergastoMateriaPrima(int idProduto) {
        switch (idProduto) {

            case 4:
                return 15; //barril 5L
            case 5:
                return 30; //barril 10L
            case 6:
                return 60; //barril 20L
            case 7:
                return 100;//barril 50L
            case 8:
                return 2; //garrafa chopp 1,5L
            default:return 0;
        }
    }

    public boolean registrarProducao(Producao producao) {
        String sqlInserir = "INSERT INTO producao (id_produto, quantidade) VALUES(?,?)";
        String sqlAtualizarProduto = "UPDATE estoque SET quantidade = quantidade + ? WHERE id_produto=?";
        String sqlAtualizarMalte = "UPDATE estoque set quantidade = quantidade - ? WHERE id_produto=1";

        try {
            Connection con = ConexaoBD.conectar(); // Substitua pela sua chamada de conexão, ex: Conexao.getConexao();
            PreparedStatement stmtInsert = null;
            PreparedStatement stmtProd = null;
            PreparedStatement stmtMalte = null;
            try {
                con.setAutoCommit(false);

                //calcula o gasto do malte
                double gastoPorUnidade = ObtergastoMateriaPrima(producao.getIdProduto());
                double gastoTotalMalte = gastoPorUnidade * producao.getQuantidade();

                //verifica se tem malte sufuciente
                String sqlverificacao = "SELECT quantidade FROM estoque WHERE id_produto = 1";
                try (PreparedStatement stmtVerif = con.prepareStatement(sqlverificacao);
                     ResultSet rs = stmtVerif.executeQuery()) {
                    if (rs.next()) {
                        double quantidadeAtualMalte = rs.getInt("quantidade");

                        if (quantidadeAtualMalte < gastoTotalMalte) {
                            System.out.println("Erro: Estoque de malte Insuficiente! Atual: " + quantidadeAtualMalte);
                            return false;
                        }
                    } else {
                        System.out.println("Erro: Matéria Prima: Malte não foi encontrada no Banco de dados");
                        return false;
                    }
                }
                //inserir o registro na tabela producao
                stmtInsert = con.prepareStatement(sqlInserir);
                stmtInsert.setInt(1, producao.getIdProduto());
                stmtInsert.setInt(2, producao.getQuantidade());
                stmtInsert.executeUpdate();

                //adicionar qauntidade produzida no estoque do produto final
                stmtProd = con.prepareStatement(sqlAtualizarProduto);
                stmtProd.setInt(1, producao.getQuantidade());
                stmtProd.setInt(2, producao.getIdProduto());
                stmtProd.executeUpdate();

                //subtrair o malte gasto do estoque de matérias-primas
                stmtMalte = con.prepareStatement(sqlAtualizarMalte);
                stmtMalte.setDouble(1, gastoTotalMalte);
                stmtMalte.executeUpdate();

                con.commit();
                return true;

            } catch (SQLException e) {
                if (con != null) {
                    try {
                        con.rollback();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
                e.printStackTrace();
                return false;
            } finally {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}

