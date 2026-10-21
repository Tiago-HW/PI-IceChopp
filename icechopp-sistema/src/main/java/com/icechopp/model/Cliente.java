package com.icechopp.model;

public class Cliente {
    private int idCliente;
    private String nome;
    private String endereco;
    private long telefone;
    private String email;

    public Cliente() {}

    public Cliente(int idCliente, String nome, String endereco, long telefone, String email) {
        this.idCliente = idCliente;
        this.nome = nome;
        this.endereco = endereco;
        this.telefone = telefone;
        this.email = email;
    }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public long getTelefone() { return telefone; }
    public void setTelefone(long telefone) { this.telefone = telefone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() { return idCliente + " - " + nome; }
}
