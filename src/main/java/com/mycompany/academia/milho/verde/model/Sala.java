/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model;

/**
 * Representa uma das quatro salas da academia.
 *
 * <p>As salas são fixas: são criadas uma única vez em
 * {@link Sistema#getSalas()}. Código, modalidade e capacidade são
 * definidos na criação e não mudam.</p>
 *
 * @author Aline Ferreira
 */
public class Sala {

    /** Modalidade: spinning. */
    public static final String SPINNING = "SPINNING";

    /** Modalidade: musculação. */
    public static final String MUSCULACAO = "MUSCULACAO";

    /** Modalidade: fit dance. */
    public static final String FIT_DANCE = "FIT_DANCE";

    /** Modalidade: pilates. */
    public static final String PILATES = "PILATES";

    /** Código da sala (0 na sala em branco). */
    private int codigo;

    /** Modalidade da sala: uma das constantes desta classe. */
    private String modalidade;

    /** Número máximo de alunos por aula. */
    private int capacidade;

    /**
     * Construtor padrão: cria uma sala em branco.
     */
    public Sala() {
        this.codigo = 0;
        this.modalidade = "";
        this.capacidade = 0;
    }

    /**
     * Construtor com parâmetros.
     *
     * @param novoCodigo código positivo da sala
     * @param novaModalidade {@link #SPINNING}, {@link #MUSCULACAO},
     *        {@link #FIT_DANCE} ou {@link #PILATES}
     * @param novaCapacidade capacidade, maior que zero
     * @throws IllegalArgumentException se algum dado for inválido
     */
    public Sala(int novoCodigo, String novaModalidade, int novaCapacidade) {
        if (novoCodigo <= 0) {
            throw new IllegalArgumentException("O código deve ser positivo.");
        }
        if (novaCapacidade <= 0) {
            throw new IllegalArgumentException(
                    "A capacidade deve ser maior que zero.");
        }
        this.codigo = novoCodigo;
        this.modalidade = exigirModalidade(novaModalidade);
        this.capacidade = novaCapacidade;
    }

    /**
     * Retorna o código da sala.
     *
     * @return o código
     */
    public int getCodigo() {
        return codigo;
    }

    /**
     * Retorna a modalidade da sala.
     *
     * @return a modalidade
     */
    public String getModalidade() {
        return modalidade;
    }

    /**
     * Retorna a capacidade da sala.
     *
     * @return o número máximo de alunos
     */
    public int getCapacidade() {
        return capacidade;
    }

    /**
     * Confere se a modalidade é uma das constantes aceitas.
     *
     * @param valor modalidade informada
     * @return o próprio {@code valor}, quando válido
     * @throws IllegalArgumentException se for outro texto
     */
    private static String exigirModalidade(String valor) {
        if (SPINNING.equals(valor) || MUSCULACAO.equals(valor)
                || FIT_DANCE.equals(valor) || PILATES.equals(valor)) {
            return valor;
        }
        throw new IllegalArgumentException(
                "Modalidade inválida: use SPINNING, MUSCULACAO, FIT_DANCE ou PILATES.");
    }

    /**
     * Retorna uma descrição legível da sala.
     *
     * @return texto com código, modalidade e capacidade
     */
    @Override
    public String toString() {
        return "Sala[codigo=" + codigo + ", modalidade=" + modalidade
                + ", capacidade=" + capacidade + "]";
    }
}