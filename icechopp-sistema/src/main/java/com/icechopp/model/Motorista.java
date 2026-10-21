package com.icechopp.model;

public class Motorista {
    private int idMotorista;
    private String cpf;
    private String endereco;
    private long telefone;
    private String placaCaminhao;
    private String nome;
    private double salario;

    public Motorista() {}

    public Motorista(int idMotorista, String cpf, String endereco, long telefone,
                      String placaCaminhao, String nome, double salario) {
        this.idMotorista = idMotorista;
        this.cpf = cpf;
        this.endereco = endereco;
        this.telefone = telefone;
        this.placaCaminhao = placaCaminhao;
        this.nome = nome;
        this.salario = salario;
    }

    public int getIdMotorista() { return idMotorista; }
    public void setIdMotorista(int idMotorista) { this.idMotorista = idMotorista; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public long getTelefone() { return telefone; }
    public void setTelefone(long telefone) { this.telefone = telefone; }

    public String getPlacaCaminhao() { return placaCaminhao; }
    public void setPlacaCaminhao(String placaCaminhao) { this.placaCaminhao = placaCaminhao; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public double getSalario() { return salario; }
    public void setSalario(double salario) { this.salario = salario; }

    @Override
    public String toString() { return idMotorista + " - " + nome; }
}
