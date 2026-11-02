/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Guarda os produtos da lojinha e da lanchonete e controla as
 * quantidades em estoque.
 *
 * <p>As buscas percorrem a lista e comparam cada produto, um por um.
 * Toda alteração de quantidade passa por aqui, o que garante que o
 * estoque nunca fique negativo.</p>
 *
 * @author Aline Ferreira
 */
public class Estoque {

    /** Produtos cadastrados, da lojinha e da lanchonete. */
    private List<Produto> produtos;

    /** Construtor padrão: cria um estoque vazio. */
    public Estoque() {
        this.produtos = new ArrayList<>();
    }

    /**
     * Retorna os produtos cadastrados.
     *
     * <p>Devolve uma cópia da lista: quem recebe pode percorrê-la, mas
     * não consegue incluir ou remover produtos sem passar pelo
     * estoque.</p>
     *
     * @return cópia da lista de produtos
     */
    public List<Produto> getProdutos() {
        return new ArrayList<>(produtos);
    }

    /**
     * Cadastra um produto no estoque.
     *
     * @param produto produto com identificador definido
     * @throws IllegalArgumentException se o produto for nulo, não tiver
     *         identificador ou se o ID já estiver cadastrado
     */
    public void adicionarProduto(Produto produto) {
        if (produto == null) {
            throw new IllegalArgumentException("O produto é obrigatório.");
        }
        if (produto.getId() <= 0) {
            throw new IllegalArgumentException(
                    "O produto precisa ter identificador.");
        }
        if (buscarProduto(produto.getId()) != null) {
            throw new IllegalArgumentException(
                    "Já existe um produto com o ID " + produto.getId() + ".");
        }
        produtos.add(produto);
    }

    /**
     * Remove um produto do estoque.
     *
     * @param idProduto identificador do produto
     * @return {@code true} se o produto existia e foi removido
     */
    public boolean removerProduto(int idProduto) {
        Produto produto = buscarProduto(idProduto);
        if (produto == null) {
            return false;
        }
        return produtos.remove(produto);
    }

    /**
     * Procura um produto pelo identificador.
     *
     * @param idProduto identificador procurado
     * @return o produto, ou {@code null} se o ID não existir
     */
    public Produto buscarProduto(int idProduto) {
        // Percorre e compara: para no primeiro produto com o ID.
        for (Produto produto : produtos) {
            if (produto.getId() == idProduto) {
                return produto;
            }
        }
        return null;
    }

    /**
     * Procura os produtos cujo nome contém um trecho, sem diferenciar
     * maiúsculas e minúsculas.
     *
     * @param trecho parte do nome procurada
     * @return lista com os produtos encontrados (vazia se nenhum)
     * @throws IllegalArgumentException se o trecho estiver vazio
     */
    public List<Produto> buscarPorNome(String trecho) {
        if (trecho == null || trecho.isBlank()) {
            throw new IllegalArgumentException(
                    "Informe parte do nome para buscar.");
        }
        String procurado = trecho.toLowerCase();
        List<Produto> encontrados = new ArrayList<>();
        for (Produto produto : produtos) {
            if (produto.getNome().toLowerCase().contains(procurado)) {
                encontrados.add(produto);
            }
        }
        return encontrados;
    }

    /**
     * Informa se há quantidade suficiente de um produto.
     *
     * @param idProduto identificador do produto
     * @param quantidade quantidade desejada, maior que zero
     * @return {@code true} se o produto existe e tem a quantidade
     * @throws IllegalArgumentException se a quantidade não for positiva
     */
    public boolean verificarDisponibilidade(int idProduto, int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException(
                    "A quantidade deve ser maior que zero.");
        }
        Produto produto = buscarProduto(idProduto);
        return produto != null && produto.getQuantidade() >= quantidade;
    }

    /**
     * Acrescenta unidades de um produto ao estoque.
     *
     * @param idProduto identificador do produto
     * @param quantidade unidades a acrescentar, maior que zero
     * @throws IllegalArgumentException se a quantidade não for positiva
     *         ou se o produto não existir
     */
    public void reporEstoque(int idProduto, int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException(
                    "A quantidade deve ser maior que zero.");
        }
        Produto produto = buscarProduto(idProduto);
        if (produto == null) {
            throw new IllegalArgumentException(
                    "Produto " + idProduto + " não encontrado.");
        }
        produto.alterarQuantidade(quantidade);
    }

    /**
     * Retira unidades de um produto do estoque, depois de uma venda.
     *
     * @param idProduto identificador do produto
     * @param quantidade unidades a retirar, maior que zero
     * @throws IllegalArgumentException se a quantidade não for positiva
     *         ou se o produto não existir
     * @throws IllegalStateException se não houver quantidade suficiente;
     *         nesse caso nada muda
     */
    public void darBaixa(int idProduto, int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException(
                    "A quantidade deve ser maior que zero.");
        }
        Produto produto = buscarProduto(idProduto);
        if (produto == null) {
            throw new IllegalArgumentException(
                    "Produto " + idProduto + " não encontrado.");
        }
        // Valor negativo: alterarQuantidade recusa se faltar produto.
        produto.alterarQuantidade(-quantidade);
    }

    /**
     * Retorna um resumo do estoque.
     *
     * @return texto com o número de produtos cadastrados
     */
    @Override
    public String toString() {
        return "Estoque[" + produtos.size() + " produto(s)]";
    }
}
