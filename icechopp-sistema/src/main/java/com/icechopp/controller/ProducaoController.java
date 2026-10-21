package com.icechopp.controller;

import com.icechopp.dao.ProducaoDAO;
import com.icechopp.model.Producao;

public class ProducaoController {

    private final ProducaoDAO producaoDAO;

    public ProducaoController() {
        this.producaoDAO = new ProducaoDAO();
    }

    /**
     * Método chamado pela View para realizar uma nova produção.
     * @param idProduto O ID do Chopp fabricado (ex: Barril 5L, 50L)
     * @param quantidade A quantidade de unidades produzidas
     * @return true se a produção foi realizada com sucesso, false se falhou (ex: falta de malte)
     */
    public boolean executarProducao(int idProduto, int quantidade) {
        // Validação básica de entrada de dados
        if (idProduto <= 0 || quantidade <= 0) {
            System.out.println("Erro no Controller: ID do produto ou quantidade inválidos.");
            return false;
        }

        // Criamos o objeto de modelo preenchendo os dados vindos da tela
        Producao novaProducao = new Producao();
        novaProducao.setIdProduto(idProduto);
        novaProducao.setQuantidade(quantidade);

        // Disparamos a regra de negócio e gravação no banco via DAO
        return producaoDAO.registrarProducao(novaProducao);
    }
}