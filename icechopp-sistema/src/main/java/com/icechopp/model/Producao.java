package com.icechopp.model;
import java.time.LocalDateTime;

public class Producao {
    private int id;
    private int idProduto;
    private int quantidade;
    private LocalDateTime dataProducao;

    public Producao(){}

    public Producao(int id, int idProduto, int quantidade, LocalDateTime dataProducao){
        this.id = id;
        this.idProduto = idProduto;
        this.quantidade = quantidade;
        this.dataProducao = dataProducao;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getIdProduto() {
        return idProduto;
    }
    public void setIdProduto(int idProduto) {
        this.idProduto = idProduto;
    }

    public int getQuantidade() {
        return quantidade;
    }
    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public LocalDateTime getDataProducao() {
        return dataProducao;
    }
    public void setDataProducao(LocalDateTime dataProducao) {
        this.dataProducao = dataProducao;
    }
}

