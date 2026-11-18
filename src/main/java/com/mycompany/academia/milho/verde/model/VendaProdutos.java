/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model; // pacote do Model

import java.time.LocalDateTime; // data e hora juntas
import java.util.ArrayList; // lista que cresce sozinha
import java.util.List; // tipo da lista

public class VendaProdutos extends Venda { // venda da lojinha: é uma Venda feita de itens

    private final List<ItemVenda> itens; // linhas da venda; final: a lista é sempre a mesma

    public VendaProdutos(int novoId, Cliente novoCliente, LocalDateTime novaDataHora) { // construtor com parâmetros
        super(novoId, novoCliente, novaDataHora); // a mãe confere e guarda ID, cliente e data
        this.itens = new ArrayList<>(); // começa sem itens
    } // fim do construtor

    public List<ItemVenda> getItens() { // devolve os itens
        List<ItemVenda> copia = new ArrayList<>(); // lista nova que será devolvida
        IteradorLista<ItemVenda> iterador = new IteradorLista<>(itens); // nosso iterador percorre os itens
        while (iterador.hasNext()) { // enquanto houver item
            copia.add(iterador.next()); // copia o próximo
        } // fim do while
        return copia; // devolve a cópia: quem recebe não altera a lista interna
    } // fim de getItens

    public void adicionarItem(ItemVenda item) { // coloca uma linha na venda
        if (item == null) { // não aceita "nenhum item"
            throw new IllegalArgumentException("Informe o item."); // erro de argumento
        } // fim do if
        itens.add(item); // acrescenta no fim da lista
    } // fim de adicionarItem

    @Override // redefine o método abstrato da Venda
    public double calcularTotal() { // soma dos subtotais
        double total = 0; // começa em zero
        IteradorLista<ItemVenda> iterador = new IteradorLista<>(itens); // nosso iterador percorre os itens
        while (iterador.hasNext()) { // enquanto houver item
            total = total + iterador.next().calcularSubtotal(); // soma o subtotal do próximo
        } // fim do while
        return total; // devolve o total da venda
    } // fim de calcularTotal

    @Override // redefine o método abstrato da Venda
    public String descrever() { // texto do que foi comprado
        String texto = "Lojinha:"; // começo da descrição
        IteradorLista<ItemVenda> iterador = new IteradorLista<>(itens); // nosso iterador percorre os itens
        while (iterador.hasNext()) { // enquanto houver item
            ItemVenda item = iterador.next(); // pega o próximo
            texto = texto + " " + item.getQuantidade() + "x " + item.getProduto().getNome(); // ex.: 2x Garrafa
            if (iterador.hasNext()) { // ainda vem outro item?
                texto = texto + ","; // separa com vírgula
            } // fim do if
        } // fim do while
        return texto; // ex.: Lojinha: 2x Garrafa, 1x Luva
    } // fim de descrever

    @Override // redefine o toString da Venda
    public String toString() { // resumo em uma linha
        return super.toString() + " (" + itens.size() + " item(ns))"; // o da mãe mais a quantidade de itens
    } // fim do toString
}