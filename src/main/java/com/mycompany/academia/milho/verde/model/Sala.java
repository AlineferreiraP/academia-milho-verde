/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model; // pacote do Model

/**
 * Uma das quatro salas fixas da academia (spinning, musculação, fit dance e pilates).
 * Cada sala tem código, modalidade, capacidade de alunos por aula e os preços da diária e da mensalidade.
 *
 * @author Aline Ferreira
 */
public class Sala { // uma das quatro salas da academia (fixas)

    /** Modalidade spinning. */
    public static final String SPINNING = "SPINNING"; // modalidade spinning
    /** Modalidade musculação. */
    public static final String MUSCULACAO = "MUSCULACAO"; // modalidade musculação
    /** Modalidade fit dance. */
    public static final String FIT_DANCE = "FIT_DANCE"; // modalidade fit dance
    /** Modalidade pilates. */
    public static final String PILATES = "PILATES"; // modalidade pilates

    /** Número da sala; não muda. */
    private final int codigo; // número da sala; final: não muda
    /** Atividade praticada na sala; não muda. */
    private final String modalidade; // atividade da sala; final: não muda
    /** Quantas pessoas cabem por aula; não muda. */
    private final int capacidade; // quantas pessoas cabem por aula; final: não muda
    /** Preço de uma aula avulsa nesta sala; não muda. */
    private final double precoDiaria; // preço de uma aula avulsa nesta sala; final: não muda
    /** Preço do mês nesta sala; não muda. */
    private final double precoMensalidade; // preço do mês nesta sala; final: não muda

    /** Cria uma sala vazia (sem código, modalidade, capacidade ou preços). */
    public Sala() { // construtor sem parâmetros
        this.codigo = 0; // ainda sem código
        this.modalidade = ""; // ainda sem modalidade
        this.capacidade = 0; // ainda sem capacidade
        this.precoDiaria = 0; // ainda sem preço da diária
        this.precoMensalidade = 0; // ainda sem preço da mensalidade
    } // fim do construtor sem parâmetros

    /**
     * Cria uma sala validando todos os dados.
     *
     * @param novoCodigo código da sala (maior que zero)
     * @param novaModalidade uma das quatro modalidades da classe
     * @param novaCapacidade vagas por aula (maior que zero)
     * @param novoPrecoDiaria preço da aula avulsa (maior que zero)
     * @param novoPrecoMensalidade preço do mês (maior que zero)
     * @throws IllegalArgumentException se algum dado for inválido
     */
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

    /**
     * Devolve: código da sala.
     *
     * @return código da sala
     */
    public int getCodigo() { // getter do código
        return codigo; // devolve o código
    } // fim de getCodigo

    /**
     * Devolve: modalidade da sala.
     *
     * @return modalidade da sala
     */
    public String getModalidade() { // getter da modalidade
        return modalidade; // devolve a modalidade
    } // fim de getModalidade

    /**
     * Devolve: vagas por aula.
     *
     * @return vagas por aula
     */
    public int getCapacidade() { // getter da capacidade
        return capacidade; // devolve a capacidade
    } // fim de getCapacidade

    /**
     * Devolve: preço de uma aula avulsa.
     *
     * @return preço de uma aula avulsa
     */
    public double getPrecoDiaria() { // getter do preço da diária
        return precoDiaria; // devolve o preço de uma aula avulsa
    } // fim de getPrecoDiaria

    /**
     * Devolve: preço do mês.
     *
     * @return preço do mês
     */
    public double getPrecoMensalidade() { // getter do preço da mensalidade
        return precoMensalidade; // devolve o preço do mês
    } // fim de getPrecoMensalidade

    /**
     * Confere se o texto é uma das quatro modalidades.
     *
     * @param valor modalidade informada
     * @return a própria modalidade, se for válida
     * @throws IllegalArgumentException se não for uma das quatro
     */
    private static String exigirModalidade(String valor) { // confere se a modalidade é uma das quatro
        if (SPINNING.equals(valor) || MUSCULACAO.equals(valor)
                || FIT_DANCE.equals(valor) || PILATES.equals(valor)) { // é uma modalidade conhecida?
            return valor; // modalidade válida
        } // fim do if
        throw new IllegalArgumentException("Modalidade inválida: " + valor + "."); // qualquer outro texto é recusado
    } // fim de exigirModalidade

    /**
     * Resume a sala em uma linha.
     *
     * @return código, modalidade, vagas e preços
     */
    @Override // sobrescreve o toString herdado de Object
    public String toString() { // texto que representa a sala
        return "Sala " + codigo + " | " + modalidade + " | " + capacidade + " vagas | diária R$ "
                + String.format("%.2f", precoDiaria) + " | mensalidade R$ "
                + String.format("%.2f", precoMensalidade); // código, modalidade, vagas e preços
    } // fim de toString
} // fim da classe Sala