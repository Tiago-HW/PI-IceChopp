package com.icechopp.controller;

/**
 * Estrutura auxiliar usada apenas em memória enquanto a tela de venda
 * monta o carrinho, antes de gravar a venda + os pedidos no banco.
 */
public class ItemVendaTemp {
    private final int idProduto;
    private final String nomeProduto;
    private final int quantidade;
    private final double valorUnitario;

    public ItemVendaTemp(int idProduto, String nomeProduto, int quantidade, double valorUnitario) {
        this.idProduto = idProduto;
        this.nomeProduto = nomeProduto;
        this.quantidade = quantidade;
        this.valorUnitario = valorUnitario;
    }

    public int getIdProduto() { return idProduto; }
    public String getNomeProduto() { return nomeProduto; }
    public int getQuantidade() { return quantidade; }
    public double getValorUnitario() { return valorUnitario; }
    public double getSubtotal() { return quantidade * valorUnitario; }
}
