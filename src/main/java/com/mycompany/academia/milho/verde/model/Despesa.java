/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model; // pacote do Model

import java.time.LocalDate; // data sem hora

/**
 * Um gasto da academia (limpeza, funcionários, material ou outros).
 *
 * <p>Depois de criada a despesa não muda: todos os atributos são {@code final}.
 * Só o administrador lança despesas, e isso é conferido na {@code GestaoFinanceiro}.</p>
 */
public class Despesa implements Identificavel { // um gasto da academia

    /** Categoria: limpeza. */
    public static final String LIMPEZA = "LIMPEZA"; // categoria: limpeza
    /** Categoria: salários dos funcionários. */
    public static final String FUNCIONARIOS = "FUNCIONARIOS"; // categoria: salários
    /** Categoria: material e equipamentos. */
    public static final String MATERIAL = "MATERIAL"; // categoria: material e equipamentos
    /** Categoria: outros gastos. */
    public static final String OUTROS = "OUTROS"; // categoria: outros gastos

    /** Identificador da despesa; não muda. */
    private final int id; // identificador; final: não muda
    /** O que foi pago; não muda. */
    private final String descricao; // o que foi pago; final: não muda
    /** Uma das quatro categorias; não muda. */
    private final String categoria; // uma das quatro categorias; final: não muda
    /** Quanto custou; não muda. */
    private final double valor; // quanto custou; final: não muda
    /** Quando foi paga; não muda. */
    private final LocalDate data; // quando foi paga; final: não muda

    /**
     * Cria uma despesa.
     *
     * @param novoId identificador (maior que zero)
     * @param novaDescricao o que foi pago (não pode ser vazio)
     * @param novaCategoria {@link #LIMPEZA}, {@link #FUNCIONARIOS}, {@link #MATERIAL} ou {@link #OUTROS}
     * @param novoValor quanto custou (maior que zero)
     * @param novaData quando foi paga
     * @throws IllegalArgumentException se algum dado for inválido
     */
    public Despesa(int novoId, String novaDescricao, String novaCategoria, double novoValor, LocalDate novaData) { // construtor com parâmetros
        if (novoId <= 0) { // ID precisa ser maior que zero
            throw new IllegalArgumentException("O ID deve ser maior que zero."); // erro de argumento
        } // fim do if
        if (novoValor <= 0) { // o valor precisa ser positivo
            throw new IllegalArgumentException("O valor deve ser maior que zero."); // erro de argumento
        } // fim do if
        if (novaData == null) { // precisa da data
            throw new IllegalArgumentException("Informe a data da despesa."); // erro de argumento
        } // fim do if
        this.id = novoId; // guarda o ID
        this.descricao = Validacao.exigirTexto(novaDescricao, "descrição"); // descrição obrigatória
        this.categoria = exigirCategoria(novaCategoria); // só uma das quatro
        this.valor = novoValor; // guarda o valor
        this.data = novaData; // guarda a data
    } // fim do construtor

    /**
     * Devolve o identificador da despesa.
     *
     * @return ID da despesa
     */
    @Override // vem da interface Identificavel
    public int getId() { // devolve o ID
        return id; // ID da despesa
    } // fim de getId

    /**
     * Devolve a descrição da despesa.
     *
     * @return o que foi pago
     */
    public String getDescricao() { // devolve a descrição
        return descricao; // o que foi pago
    } // fim de getDescricao

    /**
     * Devolve a categoria da despesa.
     *
     * @return LIMPEZA, FUNCIONARIOS, MATERIAL ou OUTROS
     */
    public String getCategoria() { // devolve a categoria
        return categoria; // LIMPEZA, FUNCIONARIOS, MATERIAL ou OUTROS
    } // fim de getCategoria

    /**
     * Devolve o valor da despesa.
     *
     * @return quanto custou
     */
    public double getValor() { // devolve o valor
        return valor; // quanto custou
    } // fim de getValor

    /**
     * Devolve a data da despesa.
     *
     * @return quando foi paga
     */
    public LocalDate getData() { // devolve a data
        return data; // quando foi paga
    } // fim de getData

    /**
     * Confere se a categoria é uma das quatro permitidas.
     *
     * @param valor categoria a conferir
     * @return a própria categoria, se for válida
     * @throws IllegalArgumentException se a categoria não existir
     */
    private static String exigirCategoria(String valor) { // confere a categoria
        if (!LIMPEZA.equals(valor) && !FUNCIONARIOS.equals(valor)
                && !MATERIAL.equals(valor) && !OUTROS.equals(valor)) { // só as quatro
            throw new IllegalArgumentException("Categoria inválida: " + valor); // erro de argumento
        } // fim do if
        return valor; // devolve a categoria conferida
    } // fim de exigirCategoria

    /**
     * Resume a despesa em uma linha.
     *
     * @return data, categoria, descrição e valor
     */
    @Override // sobrescreve o toString de Object
    public String toString() { // resumo em uma linha
        return "Despesa " + id + " - " + data + " - " + categoria + " - " + descricao
                + " - R$ " + String.format("%.2f", valor); // data, categoria, descrição e valor
    } // fim do toString
}