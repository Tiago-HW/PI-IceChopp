package com.icechopp.model;

import java.time.LocalDate;

public class Estoque {
    private int idProduto;
    private String nomeProduto;
    private int quantidade;
    private Double valorUn; // null para matéria-prima
    private LocalDate dataEntrada;

    public Estoque() {}

    public Estoque(int idProduto, String nomeProduto, int quantidade, Double valorUn, LocalDate dataEntrada) {
        this.idProduto = idProduto;
        this.nomeProduto = nomeProduto;
        this.quantidade = quantidade;
        this.valorUn = valorUn;
        this.dataEntrada = dataEntrada;
    }

    public int getIdProduto() { return idProduto; }
    public void setIdProduto(int idProduto) { this.idProduto = idProduto; }

    public String getNomeProduto() { return nomeProduto; }
    public void setNomeProduto(String nomeProduto) { this.nomeProduto = nomeProduto; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public Double getValorUn() { return valorUn; }
    public void setValorUn(Double valorUn) { this.valorUn = valorUn; }

    public LocalDate getDataEntrada() { return dataEntrada; }
    public void setDataEntrada(LocalDate dataEntrada) { this.dataEntrada = dataEntrada; }
}
