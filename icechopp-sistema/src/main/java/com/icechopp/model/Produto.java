package com.icechopp.model;

public class Produto {
    private int idProduto;
    private String nome;
    private Double valorUn; // pode ser null para matéria-prima
    private String tipo;    // "MATERIA_PRIMA" ou "PRODUTO_FINAL"

    public Produto() {}

    public Produto(int idProduto, String nome, Double valorUn, String tipo) {
        this.idProduto = idProduto;
        this.nome = nome;
        this.valorUn = valorUn;
        this.tipo = tipo;
    }

    public int getIdProduto() { return idProduto; }
    public void setIdProduto(int idProduto) { this.idProduto = idProduto; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Double getValorUn() { return valorUn; }
    public void setValorUn(Double valorUn) { this.valorUn = valorUn; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    @Override
    public String toString() { return idProduto + " - " + nome; }
}
