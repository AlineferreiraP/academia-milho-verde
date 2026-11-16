/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model; // pacote do Model

public class Sala { // uma das quatro salas da academia (fixas)

    public static final String SPINNING = "SPINNING"; // modalidade spinning
    public static final String MUSCULACAO = "MUSCULACAO"; // modalidade musculação
    public static final String FIT_DANCE = "FIT_DANCE"; // modalidade fit dance
    public static final String PILATES = "PILATES"; // modalidade pilates

    private final int codigo; // número da sala; final: não muda
    private final String modalidade; // atividade da sala; final: não muda
    private final int capacidade; // quantas pessoas cabem por aula; final: não muda
    private final double precoDiaria; // preço de uma aula avulsa nesta sala; final: não muda
    private final double precoMensalidade; // preço do mês nesta sala; final: não muda

    public Sala() { // construtor sem parâmetros
        this.codigo = 0; // ainda sem código
        this.modalidade = ""; // ainda sem modalidade
        this.capacidade = 0; // ainda sem capacidade
        this.precoDiaria = 0; // ainda sem preço da diária
        this.precoMensalidade = 0; // ainda sem preço da mensalidade
    } // fim do construtor sem parâmetros

    public Sala(int novoCodigo, String novaModalidade, int novaCapacidade,
            double novoPrecoDiaria, double novoPrecoMensalidade) { // construtor com parâmetros
        if (novoCodigo <= 0) { // o código precisa ser positivo
            throw new IllegalArgumentException("O código da sala deve ser maior que zero."); // erro de argumento
        } // fim do if
        if (novaCapacidade <= 0) { // a sala precisa caber alguém
            throw new IllegalArgumentException("A capacidade deve ser maior que zero."); // erro de argumento
        } // fim do if
        if (novoPrecoDiaria <= 0 || novoPrecoMensalidade <= 0) { // os preços precisam ser positivos
            throw new IllegalArgumentException("Os preços devem ser maiores que zero."); // erro de argumento
        } // fim do if
        this.codigo = novoCodigo; // guarda o código
        this.modalidade = exigirModalidade(novaModalidade); // só aceita uma das quatro modalidades
        this.capacidade = novaCapacidade; // guarda a capacidade
        this.precoDiaria = novoPrecoDiaria; // guarda o preço da diária
        this.precoMensalidade = novoPrecoMensalidade; // guarda o preço da mensalidade
    } // fim do construtor com parâmetros

    public int getCodigo() { // getter do código
        return codigo; // devolve o código
    } // fim de getCodigo

    public String getModalidade() { // getter da modalidade
        return modalidade; // devolve a modalidade
    } // fim de getModalidade

    public int getCapacidade() { // getter da capacidade
        return capacidade; // devolve a capacidade
    } // fim de getCapacidade

    public double getPrecoDiaria() { // getter do preço da diária
        return precoDiaria; // devolve o preço de uma aula avulsa
    } // fim de getPrecoDiaria

    public double getPrecoMensalidade() { // getter do preço da mensalidade
        return precoMensalidade; // devolve o preço do mês
    } // fim de getPrecoMensalidade

    private static String exigirModalidade(String valor) { // confere se a modalidade é uma das quatro
        if (SPINNING.equals(valor) || MUSCULACAO.equals(valor)
                || FIT_DANCE.equals(valor) || PILATES.equals(valor)) { // é uma modalidade conhecida?
            return valor; // modalidade válida
        } // fim do if
        throw new IllegalArgumentException("Modalidade inválida: " + valor + "."); // qualquer outro texto é recusado
    } // fim de exigirModalidade

    @Override // sobrescreve o toString herdado de Object
    public String toString() { // texto que representa a sala
        return "Sala " + codigo + " | " + modalidade + " | " + capacidade + " vagas | diária R$ "
                + String.format("%.2f", precoDiaria) + " | mensalidade R$ "
                + String.format("%.2f", precoMensalidade); // código, modalidade, vagas e preços
    } // fim de toString
} // fim da classe Sala