/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model; // pacote do Model

public class ItemVenda { // uma linha da venda: qual produto, quantas unidades e a que preço

    private final Produto produto; // produto vendido; final: não muda
    private final int quantidade; // quantas unidades; final: não muda
    private final double precoUnitario; // preço de uma unidade no momento da venda; final: não muda

    public ItemVenda() { // construtor sem parâmetros
        this.produto = null; // ainda sem produto
        this.quantidade = 0; // nenhuma unidade
        this.precoUnitario = 0; // sem preço
    } // fim do construtor sem parâmetros

    public ItemVenda(Produto novoProduto, int novaQuantidade) { // venda nova: usa o preço atual do produto
        this(novoProduto, novaQuantidade, precoAtual(novoProduto)); // chama o outro construtor com o preço de hoje
    } // fim do construtor de venda nova

    public ItemVenda(Produto novoProduto, int novaQuantidade, double precoDaVenda) { // usado ao carregar do JSON
        if (novoProduto == null) { // o item precisa ter produto
            throw new IllegalArgumentException("Informe o produto do item."); // erro de argumento
        } // fim do if
        if (novaQuantidade <= 0) { // precisa vender pelo menos uma unidade
            throw new IllegalArgumentException("A quantidade deve ser maior que zero."); // erro de argumento
        } // fim do if
        if (precoDaVenda <= 0) { // o preço precisa ser positivo
            throw new IllegalArgumentException("O preço deve ser maior que zero."); // erro de argumento
        } // fim do if
        this.produto = novoProduto; // guarda o produto
        this.quantidade = novaQuantidade; // guarda a quantidade
        this.precoUnitario = precoDaVenda; // guarda o preço daquele dia
    } // fim do construtor completo

    private static double precoAtual(Produto produto) { // lê o preço sem quebrar se o produto for null
        if (produto == null) { // sem produto?
            throw new IllegalArgumentException("Informe o produto do item."); // erro de argumento
        } // fim do if
        return produto.getPreco(); // preço atual do produto
    } // fim de precoAtual

    public Produto getProduto() { // devolve o produto
        return produto; // produto vendido
    } // fim de getProduto

    public int getQuantidade() { // devolve a quantidade
        return quantidade; // unidades vendidas
    } // fim de getQuantidade

    public double getPrecoUnitario() { // devolve o preço de uma unidade
        return precoUnitario; // preço no momento da venda
    } // fim de getPrecoUnitario

    public double calcularSubtotal() { // valor desta linha da venda
        return quantidade * precoUnitario; // quantidade vezes o preço da unidade
    } // fim de calcularSubtotal

    @Override // sobrescreve o toString de Object
    public String toString() { // resumo em uma linha
        return quantidade + "x " + produto.getNome() + " (R$ " + String.format("%.2f", precoUnitario)
                + ") = R$ " + String.format("%.2f", calcularSubtotal()); // ex.: 2x Garrafa (R$ 15,00) = R$ 30,00
    } // fim do toString
}