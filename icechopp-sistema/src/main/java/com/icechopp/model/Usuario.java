package com.icechopp.model;

public class Usuario {
    private String cpf;
    private String nomeFuncionario;
    private long telefone;
    private String endereco;
    private double salario;
    private String login;
    private String senha;
    private String time; // "vendas", "compras" ou "admin"

    public Usuario() {}

    public Usuario(String cpf, String nomeFuncionario, long telefone, String endereco,
                   double salario, String login, String senha, String time) {
        this.cpf = cpf;
        this.nomeFuncionario = nomeFuncionario;
        this.telefone = telefone;
        this.endereco = endereco;
        this.salario = salario;
        this.login = login;
        this.senha = senha;
        this.time = time;
    }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getNomeFuncionario() { return nomeFuncionario; }
    public void setNomeFuncionario(String nomeFuncionario) { this.nomeFuncionario = nomeFuncionario; }

    public long getTelefone() { return telefone; }
    public void setTelefone(long telefone) { this.telefone = telefone; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public double getSalario() { return salario; }
    public void setSalario(double salario) { this.salario = salario; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    @Override
    public String toString() { return nomeFuncionario + " (" + login + ")"; }
}
