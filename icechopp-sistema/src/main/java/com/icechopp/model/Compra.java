package com.icechopp.model;

import java.time.LocalDateTime;

public class Compra {
    private int idCompra;
    private LocalDateTime dataCompra = LocalDateTime.now();
    private double valor;
    private int quantidade;
    private int idFornecedor;
    private int idProduto;
    private String cpfUsuario;

    public Compra() {}

    public Compra(int idCompra, LocalDateTime dataCompra, double valor, int quantidade,
                  int idFornecedor, int idProduto, String cpfUsuario) {
        this.idCompra = idCompra;
        this.dataCompra = dataCompra;
        this.valor = valor;
        this.quantidade = quantidade;
        this.idFornecedor = idFornecedor;
        this.idProduto = idProduto;
        this.cpfUsuario = cpfUsuario;
    }

    public int getIdCompra() { return idCompra; }
    public void setIdCompra(int idCompra) { this.idCompra = idCompra; }

    public LocalDateTime getDataCompra() { return dataCompra; }
    public void setDataCompra(LocalDateTime dataCompra) { this.dataCompra = dataCompra; }

    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public int getIdFornecedor() { return idFornecedor; }
    public void setIdFornecedor(int idFornecedor) { this.idFornecedor = idFornecedor; }

    public int getIdProduto() { return idProduto; }
    public void setIdProduto(int idProduto) { this.idProduto = idProduto; }

    public String getCpfUsuario() { return cpfUsuario; }
    public void setCpfUsuario(String cpfUsuario) { this.cpfUsuario = cpfUsuario; }
}
