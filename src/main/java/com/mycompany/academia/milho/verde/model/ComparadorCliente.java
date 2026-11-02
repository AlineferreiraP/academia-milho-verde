/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model;

import java.util.Comparator;

/**
 * Compara clientes por ID ou por nome (item 13).
 *
 * <p>Implementa {@link Comparator}: serve para ordenar listas com
 * {@code lista.sort(comparador)}. O critério é escolhido no construtor.</p>
 *
 * @author Aline Ferreira
 */
public class ComparadorCliente implements Comparator<Cliente> {

    /** Critério: ordem crescente de identificador. */
    public static final String ID = "ID";

    /** Critério: ordem alfabética do nome. */
    public static final String NOME = "NOME";

    /** Critério escolhido: {@link #ID} ou {@link #NOME}. */
    private String criterio;

    /**
     * Construtor padrão: ordena por {@link #ID}.
     */
    public ComparadorCliente() {
        this.criterio = ID;
    }

    /**
     * Construtor com parâmetros.
     *
     * @param novoCriterio {@link #ID} ou {@link #NOME}
     * @throws IllegalArgumentException se for outro texto
     */
    public ComparadorCliente(String novoCriterio) {
        this.criterio = exigirCriterio(novoCriterio);
    }

    /**
     * Retorna o critério.
     *
     * @return {@link #ID} ou {@link #NOME}
     */
    public String getCriterio() {
        return criterio;
    }

    /**
     * Compara dois clientes pelo critério escolhido.
     *
     * @param a primeiro cliente
     * @param b segundo cliente
     * @return negativo se {@code a} vem antes, positivo se vem depois,
     *         zero se empatam
     */
    @Override
    public int compare(Cliente a, Cliente b) {
        if (NOME.equals(criterio)) {
            return a.getNome().compareToIgnoreCase(b.getNome());
        }
        return Integer.compare(a.getId(), b.getId());
    }

    /**
     * Confere se o critério é aceito.
     *
     * @param valor critério informado
     * @return o próprio {@code valor}, quando válido
     * @throws IllegalArgumentException se não for ID nem NOME
     */
    private static String exigirCriterio(String valor) {
        if (ID.equals(valor) || NOME.equals(valor)) {
            return valor;
        }
        throw new IllegalArgumentException("O critério deve ser ID ou NOME.");
    }

    /**
     * Retorna uma descrição do comparador.
     *
     * @return texto com o critério
     */
    @Override
    public String toString() {
        return "ComparadorCliente[criterio=" + criterio + "]";
    }
}