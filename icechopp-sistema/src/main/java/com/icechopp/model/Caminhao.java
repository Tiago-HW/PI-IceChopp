package com.icechopp.model;

public class Caminhao {
    private String placa;
    private String marca;
    private int ano;
    private String modelo;

    public Caminhao() {}

    public Caminhao(String placa, String marca, int ano, String modelo) {
        this.placa = placa;
        this.marca = marca;
        this.ano = ano;
        this.modelo = modelo;
    }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public int getAno() { return ano; }
    public void setAno(int ano) { this.ano = ano; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    @Override
    public String toString() { return placa + " - " + marca + " " + modelo; }
}
