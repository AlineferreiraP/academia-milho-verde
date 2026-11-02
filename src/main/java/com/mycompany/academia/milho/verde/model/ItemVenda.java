/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model;

/**
 * Representa um item de uma venda da lojinha ou da lanchonete: um
 * produto e a quantidade vendida.
 *
 * <p>Guarda o preço do momento da venda. Se o preço do produto mudar
 * depois, as vendas antigas continuam com o valor original.</p>
 *
 * @author Aline Ferreira
 */
public class ItemVenda {

    /** Produto vendido. */
    private Produto produto;

    /** Quantidade vendida. */
    private int quantidade;

    /** Preço unitário copiado do produto no momento da venda. */
    private double precoUnitario;

    /**
     * Construtor padrão: cria um item em branco, sem produto.
     */
    public ItemVenda() {
        this.produto = null;
        this.quantidade = 0;
        this.precoUnitario = 0;
    }

    /**
     * Construtor com parâmetros: cria um item com o preço atual do
     * produto.
     *
     * @param novoProduto produto vendido
     * @param novaQuantidade quantidade vendida, maior que zero
     * @throws IllegalArgumentException se o produto for nulo ou a
     *         quantidade não for positiva
     */
    public ItemVenda(Produto novoProduto, int novaQuantidade) {
        if (novoProduto == null) {
            throw new IllegalArgumentException("O produto é obrigatório.");
        }
        if (novaQuantidade <= 0) {
            throw new IllegalArgumentException(
                    "A quantidade deve ser maior que zero.");
        }
        this.produto = novoProduto;
        this.quantidade = novaQuantidade;
        // Copia o preço agora: mudanças futuras não afetam este item.
        this.precoUnitario = novoProduto.getPreco();
    }

    /**
     * Construtor de pacote: recria um item gravado, com o preço da
     * época da venda.
     *
     * <p>Usado só pelo {@link RepositorioJson}: o construtor público
     * copiaria o preço de hoje, e a venda antiga mudaria de valor.</p>
     *
     * @param novoProduto produto vendido
     * @param novaQuantidade quantidade vendida, maior que zero
     * @param precoDaVenda preço unitário da época, zero ou maior
     * @throws IllegalArgumentException se algum dado for inválido
     */
    ItemVenda(Produto novoProduto, int novaQuantidade, double precoDaVenda) {
        this(novoProduto, novaQuantidade);
        if (precoDaVenda < 0) {
            throw new IllegalArgumentException(
                    "O preço não pode ser negativo.");
        }
        this.precoUnitario = precoDaVenda;
    }

    /**
     * Retorna o produto vendido.
     *
     * @return o produto
     */
    public Produto getProduto() {
        return produto;
    }

    /**
     * Retorna a quantidade vendida.
     *
     * @return a quantidade
     */
    public int getQuantidade() {
        return quantidade;
    }

    /**
     * Retorna o preço unitário da venda.
     *
     * @return o preço do momento da venda
     */
    public double getPrecoUnitario() {
        return precoUnitario;
    }

    /**
     * Calcula o valor do item.
     *
     * @return quantidade vezes preço unitário
     */
    public double calcularSubtotal() {
        return quantidade * precoUnitario;
    }

    /**
     * Retorna uma descrição legível do item.
     *
     * @return texto com quantidade, nome do produto e subtotal
     */
    @Override
    public String toString() {
        String nome = (produto == null) ? "(sem produto)" : produto.getNome();
        return quantidade + " x " + nome
                + " (R$ " + String.format("%.2f", precoUnitario) + ")"
                + " = R$ " + String.format("%.2f", calcularSubtotal());
    }
}
