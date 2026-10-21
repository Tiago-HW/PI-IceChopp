package com.icechopp.model;

import java.time.LocalDateTime;

public class Venda {
    private int idVenda;
    private int idCliente;
    private String cpfUsuario;
    private LocalDateTime dataVenda;
    private double valorTotal;

    public Venda() {}

    public Venda(int idVenda, int idCliente, String cpfUsuario, LocalDateTime dataVenda, double valorTotal) {
        this.idVenda = idVenda;
        this.idCliente = idCliente;
        this.cpfUsuario = cpfUsuario;
        this.dataVenda = dataVenda;
        this.valorTotal = valorTotal;
    }

    public int getIdVenda() { return idVenda; }
    public void setIdVenda(int idVenda) { this.idVenda = idVenda; }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public String getCpfUsuario() { return cpfUsuario; }
    public void setCpfUsuario(String cpfUsuario) { this.cpfUsuario = cpfUsuario; }

    public LocalDateTime getDataVenda() { return dataVenda; }
    public void setDataVenda(LocalDateTime dataVenda) { this.dataVenda = dataVenda; }

    public double getValorTotal() { return valorTotal; }
    public void setValorTotal(double valorTotal) { this.valorTotal = valorTotal; }
}
