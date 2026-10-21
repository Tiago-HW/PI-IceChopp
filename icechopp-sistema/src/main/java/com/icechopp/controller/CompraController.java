package com.icechopp.controller;

import com.icechopp.dao.CompraDAO;
import com.icechopp.dao.EstoqueDAO;
import com.icechopp.model.Compra;
import com.icechopp.model.Produto;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Orquestra a compra de matéria-prima: registra a compra e adiciona
 * a quantidade correspondente ao estoque (sem valor unitário, conforme
 * regra de negócio: matéria-prima só tem quantidade e data de entrada).
 */
public class CompraController {

    private final CompraDAO compraDAO = new CompraDAO();
    private final EstoqueDAO estoqueDAO = new EstoqueDAO();

    public void registrarCompra(Produto produto, int quantidade, double valorTotal,
                                 int idFornecedor, String cpfUsuario) throws SQLException {
        Compra compra = new Compra();
        compra.setDataCompra(LocalDateTime.now());
        compra.setValor(valorTotal);
        compra.setQuantidade(quantidade);
        compra.setIdFornecedor(idFornecedor);
        compra.setIdProduto(produto.getIdProduto());
        compra.setCpfUsuario(cpfUsuario);
        compraDAO.inserir(compra);

        // matéria-prima entra no estoque sem valor unitário
        estoqueDAO.entrada(produto.getIdProduto(), produto.getNome(), quantidade, null, LocalDate.from(LocalDateTime.now()));
    }
}
