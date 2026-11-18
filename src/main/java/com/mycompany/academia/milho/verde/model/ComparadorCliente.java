/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model; // pacote do Model

import java.util.Comparator; // interface do Java para comparar dois objetos

/**
 * Compara dois clientes pelo critério escolhido na criação: CPF pseudonimizado ou nome.
 *
 * <p>O resultado de {@code compare(a, b)} é negativo se {@code a} vem antes, zero se os
 * dois são iguais no critério e positivo se {@code a} vem depois. Serve para ordenar a
 * lista de clientes com {@code Collections.sort}.</p>
 *
 * @author Aline Ferreira
 */
public class ComparadorCliente implements Comparator<Cliente> { // compara dois clientes pelo critério escolhido

    /** Critério: compara pelo CPF pseudonimizado. */
    public static final String CPF = "CPF"; // critério: compara pelo CPF pseudonimizado
    /** Critério: compara pelo nome, sem diferenciar maiúsculas de minúsculas. */
    public static final String NOME = "NOME"; // critério: compara pelo nome

    /** Qual atributo este comparador usa; não muda. */
    private final String criterio; // qual atributo este comparador usa; final: não muda

    /**
     * Cria um comparador que usa o CPF como critério (o padrão).
     */
    public ComparadorCliente() { // construtor sem parâmetros
        this(CPF); // o padrão é comparar pelo CPF
    } // fim do construtor sem parâmetros

    /**
     * Cria um comparador com o critério informado.
     *
     * @param novoCriterio {@link #CPF} ou {@link #NOME}
     * @throws IllegalArgumentException se o critério não existir
     */
    public ComparadorCliente(String novoCriterio) { // construtor com o critério
        if (!CPF.equals(novoCriterio) && !NOME.equals(novoCriterio)) { // só os dois critérios existentes
            throw new IllegalArgumentException("Critério inválido: " + novoCriterio); // erro de argumento
        } // fim do if
        this.criterio = novoCriterio; // guarda o critério
    } // fim do construtor com parâmetros

    /**
     * Devolve o critério usado por este comparador.
     *
     * @return {@link #CPF} ou {@link #NOME}
     */
    public String getCriterio() { // devolve o critério
        return criterio; // CPF ou NOME
    } // fim de getCriterio

    /**
     * Compara dois clientes pelo critério deste comparador.
     *
     * @param a primeiro cliente
     * @param b segundo cliente
     * @return negativo se {@code a} vem antes, zero se são iguais no critério, positivo se {@code a} vem depois
     */
    @Override // implementa o único método da interface Comparator
    public int compare(Cliente a, Cliente b) { // negativo: a vem antes; zero: iguais; positivo: a vem depois
        if (NOME.equals(criterio)) { // critério nome?
            return a.getNome().compareToIgnoreCase(b.getNome()); // ordem alfabética, sem diferenciar maiúsculas
        } // fim do if
        return a.getCpfPseudo().compareTo(b.getCpfPseudo()); // critério CPF
    } // fim de compare

    /**
     * Descreve o comparador.
     *
     * @return texto com o critério usado
     */
    @Override // sobrescreve o toString herdado de Object
    public String toString() { // texto que representa o comparador
        return "ComparadorCliente por " + criterio; // mostra o critério
    } // fim de toString
} // fim da classe ComparadorCliente