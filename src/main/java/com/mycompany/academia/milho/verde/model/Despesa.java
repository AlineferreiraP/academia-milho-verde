/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model;

import java.time.LocalDate;

/**
 * Representa uma despesa da academia (limpeza, funcionários, material
 * ou outras).
 *
 * <p>Só o administrador cadastra, lista e remove despesas. Os dados
 * são definidos no cadastro: o enunciado não pede edição.</p>
 *
 * @author Aline Ferreira
 */
public class Despesa {

    /** Categoria: limpeza. */
    public static final String LIMPEZA = "LIMPEZA";

    /** Categoria: pagamento de funcionários. */
    public static final String FUNCIONARIOS = "FUNCIONARIOS";

    /** Categoria: material. */
    public static final String MATERIAL = "MATERIAL";

    /** Categoria: outras despesas. */
    public static final String OUTROS = "OUTROS";

    /** Identificador único (0 enquanto não definido). */
    private int id;

    /** O que foi pago. */
    private String descricao;

    /** Categoria: uma das constantes desta classe. */
    private String categoria;

    /** Valor pago, em reais. */
    private double valor;

    /** Data da despesa. */
    private LocalDate data;

    /**
     * Construtor padrão: cria uma despesa em branco.
     */
    public Despesa() {
        this.id = 0;
        this.descricao = "";
        this.categoria = "";
        this.valor = 0;
        this.data = null;
    }

    /**
     * Construtor com parâmetros.
     *
     * @param novoId identificador positivo
     * @param novaDescricao descrição, não vazia
     * @param novaCategoria {@link #LIMPEZA}, {@link #FUNCIONARIOS},
     *        {@link #MATERIAL} ou {@link #OUTROS}
     * @param novoValor valor maior que zero
     * @param novaData data da despesa
     * @throws IllegalArgumentException se algum dado for inválido
     */
    public Despesa(int novoId, String novaDescricao, String novaCategoria,
                   double novoValor, LocalDate novaData) {
        if (novoId <= 0) {
            throw new IllegalArgumentException(
                    "O identificador deve ser positivo.");
        }
        if (novaDescricao == null || novaDescricao.isBlank()) {
            throw new IllegalArgumentException(
                    "A descrição deve ser preenchida.");
        }
        if (novoValor <= 0) {
            throw new IllegalArgumentException(
                    "O valor deve ser maior que zero.");
        }
        if (novaData == null) {
            throw new IllegalArgumentException("A data é obrigatória.");
        }
        this.id = novoId;
        this.descricao = novaDescricao;
        this.categoria = exigirCategoria(novaCategoria);
        this.valor = novoValor;
        this.data = novaData;
    }

    /**
     * Retorna o identificador.
     *
     * @return o ID
     */
    public int getId() {
        return id;
    }

    /**
     * Retorna a descrição.
     *
     * @return a descrição
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * Retorna a categoria.
     *
     * @return a categoria
     */
    public String getCategoria() {
        return categoria;
    }

    /**
     * Retorna o valor.
     *
     * @return o valor em reais
     */
    public double getValor() {
        return valor;
    }

    /**
     * Retorna a data.
     *
     * @return a data da despesa
     */
    public LocalDate getData() {
        return data;
    }

    /**
     * Define o identificador de uma despesa criada pelo construtor
     * padrão. Acesso de pacote; funciona uma única vez.
     *
     * @param novoId identificador positivo
     * @throws IllegalArgumentException se não for positivo
     * @throws IllegalStateException se já tiver identificador
     */
    void definirId(int novoId) {
        if (this.id != 0) {
            throw new IllegalStateException(
                    "O identificador já foi definido.");
        }
        if (novoId <= 0) {
            throw new IllegalArgumentException(
                    "O identificador deve ser positivo.");
        }
        this.id = novoId;
    }

    /**
     * Confere se a categoria é aceita.
     *
     * @param valor categoria informada
     * @return o próprio {@code valor}, quando válido
     * @throws IllegalArgumentException se for outro texto
     */
    private static String exigirCategoria(String valor) {
        if (LIMPEZA.equals(valor) || FUNCIONARIOS.equals(valor)
                || MATERIAL.equals(valor) || OUTROS.equals(valor)) {
            return valor;
        }
        throw new IllegalArgumentException(
                "Categoria inválida: use LIMPEZA, FUNCIONARIOS, MATERIAL ou OUTROS.");
    }

    /**
     * Retorna uma descrição legível da despesa.
     *
     * @return texto com ID, descrição, categoria, valor e data
     */
    @Override
    public String toString() {
        return "Despesa[id=" + id + ", " + descricao + ", " + categoria
                + ", R$ " + String.format("%.2f", valor) + ", " + data + "]";
    }
}
