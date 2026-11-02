/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model;

/**
 * Representa um produto vendido na lojinha ou na lanchonete da academia.
 *
 * <p>Nome, categoria e preço são definidos no cadastro: o enunciado não
 * pede edição de produto. A quantidade em estoque só muda pelo
 * {@link Estoque}, por {@link #alterarQuantidade(int)}.</p>
 *
 * @author Aline Ferreira
 */
public class Produto {

    /** Categoria dos produtos da lojinha de material de academia. */
    public static final String LOJA = "LOJA";

    /** Categoria dos produtos da lanchonete. */
    public static final String LANCHONETE = "LANCHONETE";

    /** Identificador único, gerado pelo sistema (0 enquanto não definido). */
    private int id;

    /** Nome do produto. */
    private String nome;

    /** Categoria: {@link #LOJA} ou {@link #LANCHONETE}. */
    private String categoria;

    /** Preço unitário de venda, em reais. */
    private double preco;

    /** Quantidade disponível em estoque. Nunca fica negativa. */
    private int quantidade;

    /**
     * Construtor padrão: cria um produto em branco.
     *
     * <p>Sem identificador, com textos vazios, preço e quantidade zero.</p>
     */
    public Produto() {
        // Strings vazias em vez de null evitam NullPointerException
        // em toString() e em buscas feitas antes do preenchimento.
        this.id = 0;
        this.nome = "";
        this.categoria = "";
        this.preco = 0;
        this.quantidade = 0;
        Sistema.contarProduto();
    }

    /**
     * Construtor com parâmetros: cria um produto completo.
     *
     * @param novoId identificador positivo
     * @param novoNome nome do produto
     * @param novaCategoria {@link #LOJA} ou {@link #LANCHONETE}
     * @param novoPreco preço unitário, zero ou maior
     * @param novaQuantidade quantidade inicial em estoque, zero ou maior
     * @throws IllegalArgumentException se algum dado for inválido
     */
    public Produto(int novoId, String novoNome, String novaCategoria,
                   double novoPreco, int novaQuantidade) {
        if (novoId <= 0) {
            throw new IllegalArgumentException(
                    "O identificador deve ser positivo.");
        }
        // isBlank() é verdadeiro para texto vazio ou só com espaços.
        if (novoNome == null || novoNome.isBlank()) {
            throw new IllegalArgumentException(
                    "O nome do produto deve ser preenchido.");
        }
        if (novoPreco < 0) {
            throw new IllegalArgumentException(
                    "O preço não pode ser negativo.");
        }
        if (novaQuantidade < 0) {
            throw new IllegalArgumentException(
                    "A quantidade não pode ser negativa.");
        }
        this.id = novoId;
        this.nome = novoNome;
        this.categoria = exigirCategoria(novaCategoria);
        this.preco = novoPreco;
        this.quantidade = novaQuantidade;
        Sistema.contarProduto();
    }

    /**
     * Retorna o identificador.
     *
     * @return o ID, ou 0 se ainda não foi definido
     */
    public int getId() {
        return id;
    }

    /**
     * Retorna o nome do produto.
     *
     * @return o nome
     */
    public String getNome() {
        return nome;
    }

    /**
     * Retorna a categoria do produto.
     *
     * @return {@link #LOJA} ou {@link #LANCHONETE}
     */
    public String getCategoria() {
        return categoria;
    }

    /**
     * Retorna o preço unitário.
     *
     * @return o preço em reais
     */
    public double getPreco() {
        return preco;
    }

    /**
     * Retorna a quantidade em estoque.
     *
     * @return a quantidade disponível
     */
    public int getQuantidade() {
        return quantidade;
    }

    /**
     * Soma uma diferença à quantidade em estoque.
     *
     * <p>Tem acesso de pacote: só o {@link Estoque} chama, com valor
     * positivo para repor e negativo para dar baixa.</p>
     *
     * @param diferenca quantidade a somar (negativa para retirar)
     * @throws IllegalStateException se a quantidade ficaria negativa;
     *         nesse caso nada muda
     */
    void alterarQuantidade(int diferenca) {
        // Confere antes de alterar: o estoque nunca fica negativo.
        if (quantidade + diferenca < 0) {
            throw new IllegalStateException(
                    "Estoque insuficiente de " + nome + ".");
        }
        this.quantidade = quantidade + diferenca;
    }

    /**
     * Define o identificador de um produto criado pelo construtor padrão.
     *
     * <p>Tem acesso de pacote: a gestão que gera os IDs chama. Funciona
     * uma única vez.</p>
     *
     * @param novoId identificador positivo
     * @throws IllegalArgumentException se {@code novoId} não for positivo
     * @throws IllegalStateException se o produto já tiver identificador
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
     * Confere se a categoria é uma das constantes aceitas.
     *
     * <p>É {@code static} porque não depende de nenhum produto
     * específico, só do texto recebido.</p>
     *
     * @param valor categoria informada
     * @return o próprio {@code valor}, quando válido
     * @throws IllegalArgumentException se não for {@link #LOJA}
     *         nem {@link #LANCHONETE}
     */
    private static String exigirCategoria(String valor) {
        // LOJA.equals(valor) também funciona quando valor é null.
        if (LOJA.equals(valor) || LANCHONETE.equals(valor)) {
            return valor;
        }
        throw new IllegalArgumentException(
                "A categoria deve ser LOJA ou LANCHONETE.");
    }

    /**
     * Retorna uma descrição legível do produto.
     *
     * @return texto com ID, nome, categoria, preço e quantidade
     */
    @Override
    public String toString() {
        return "Produto[id=" + id
                + ", nome=" + nome
                + ", categoria=" + categoria
                + ", preço=R$ " + String.format("%.2f", preco)
                + ", quantidade=" + quantidade + "]";
    }
}