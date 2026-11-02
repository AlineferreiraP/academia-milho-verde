/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model;

import java.util.Comparator;

/**
 * Compara agendamentos pela data da primeira aula ou pelo valor
 * (item 13).
 *
 * <p>Implementa {@link Comparator}: serve para ordenar listas com
 * {@code lista.sort(comparador)}. O critério é escolhido no construtor.</p>
 *
 * @author Aline Ferreira
 */
public class ComparadorAgendamento implements Comparator<Agendamento> {

    /** Critério: data da primeira aula, da mais próxima para a mais distante. */
    public static final String DATA = "DATA";

    /** Critério: preço, do menor para o maior. */
    public static final String VALOR = "VALOR";

    /** Critério escolhido: {@link #DATA} ou {@link #VALOR}. */
    private String criterio;

    /**
     * Construtor padrão: ordena por {@link #DATA}.
     */
    public ComparadorAgendamento() {
        this.criterio = DATA;
    }

    /**
     * Construtor com parâmetros.
     *
     * @param novoCriterio {@link #DATA} ou {@link #VALOR}
     * @throws IllegalArgumentException se for outro texto
     */
    public ComparadorAgendamento(String novoCriterio) {
        this.criterio = exigirCriterio(novoCriterio);
    }

    /**
     * Retorna o critério.
     *
     * @return {@link #DATA} ou {@link #VALOR}
     */
    public String getCriterio() {
        return criterio;
    }

    /**
     * Compara dois agendamentos pelo critério escolhido.
     *
     * @param a primeiro agendamento
     * @param b segundo agendamento
     * @return negativo se {@code a} vem antes, positivo se vem depois,
     *         zero se empatam
     */
    @Override
    public int compare(Agendamento a, Agendamento b) {
        if (VALOR.equals(criterio)) {
            return Double.compare(a.getPreco(), b.getPreco());
        }
        return a.getPrimeiraData().compareTo(b.getPrimeiraData());
    }

    /**
     * Confere se o critério é aceito.
     *
     * @param valor critério informado
     * @return o próprio {@code valor}, quando válido
     * @throws IllegalArgumentException se não for DATA nem VALOR
     */
    private static String exigirCriterio(String valor) {
        if (DATA.equals(valor) || VALOR.equals(valor)) {
            return valor;
        }
        throw new IllegalArgumentException("O critério deve ser DATA ou VALOR.");
    }

    /**
     * Retorna uma descrição do comparador.
     *
     * @return texto com o critério
     */
    @Override
    public String toString() {
        return "ComparadorAgendamento[criterio=" + criterio + "]";
    }
}
