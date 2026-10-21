package com.icechopp.model;

import java.time.LocalDate;

public class Pedido {
    private int idPedido;
    private int idVenda;
    private int idProduto;
    private String nomeProduto;
    private int quantidade;
    private LocalDate dataEntrega;
    private Integer idMotorista; // pode ser null até ser atribuído
    private String status;       // PENDENTE, EM_ROTA, ENTREGUE, CANCELADO

    public Pedido() {}

    public Pedido(int idPedido, int idVenda, int idProduto, String nomeProduto, int quantidade,
                  LocalDate dataEntrega, Integer idMotorista, String status) {
        this.idPedido = idPedido;
        this.idVenda = idVenda;
        this.idProduto = idProduto;
        this.nomeProduto = nomeProduto;
        this.quantidade = quantidade;
        this.dataEntrega = dataEntrega;
        this.idMotorista = idMotorista;
        this.status = status;
    }

    public int getIdPedido() { return idPedido; }
    public void setIdPedido(int idPedido) { this.idPedido = idPedido; }

    public int getIdVenda() { return idVenda; }
    public void setIdVenda(int idVenda) { this.idVenda = idVenda; }

    public int getIdProduto() { return idProduto; }
    public void setIdProduto(int idProduto) { this.idProduto = idProduto; }

    public String getNomeProduto() { return nomeProduto; }
    public void setNomeProduto(String nomeProduto) { this.nomeProduto = nomeProduto; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public LocalDate getDataEntrega() { return dataEntrega; }
    public void setDataEntrega(LocalDate dataEntrega) { this.dataEntrega = dataEntrega; }

    public Integer getIdMotorista() { return idMotorista; }
    public void setIdMotorista(Integer idMotorista) { this.idMotorista = idMotorista; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
