package com.icechopp.model;

public class Fornecedor {
    private int idFornecedor;
    private String nome;
    private String email;
    private String telefone;

    public Fornecedor() {}

    public Fornecedor(int idFornecedor, String nome, String email, String telefone) {
        this.idFornecedor = idFornecedor;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
    }

    public int getIdFornecedor() { return idFornecedor; }
    public void setIdFornecedor(int idFornecedor) { this.idFornecedor = idFornecedor; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    @Override
    public String toString() { return idFornecedor + " - " + nome; }
}
