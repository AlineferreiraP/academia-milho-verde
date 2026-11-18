/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model; // pacote do Model

import com.mycompany.academia.milho.verde.dao.DAO; // interface genérica do DAO: o estoque não sabe onde os produtos ficam
import com.mycompany.academia.milho.verde.dao.DAOMemoria; // DAO em memória, usado como padrão
import java.util.ArrayList; // lista que cresce sozinha
import java.util.List; // tipo da lista

public class Estoque { // guarda os produtos da lojinha e controla as quantidades

    private final DAO<Produto> produtos; // onde os produtos ficam guardados; final: não troca depois de criado

    public Estoque() { // construtor sem parâmetros
        this.produtos = new DAOMemoria<>(); // começa com o DAO em memória vazio (o <> deduz o tipo Produto)
    } // fim do construtor

    int gerarId() { // pacote: a GestaoVendas pede um ID novo antes de criar o produto
        return produtos.gerarId(); // o DAO entrega o próximo ID livre
    } // fim de gerarId

    public List<Produto> getProdutos() { // devolve todos os produtos
        return produtos.listar(); // cópia da lista: quem recebe não mexe na lista interna
    } // fim de getProdutos

    public void adicionarProduto(Produto produto) { // guarda um produto novo no estoque
        if (produto == null) { // não aceita "nenhum produto"
            throw new IllegalArgumentException("Informe o produto."); // erro de argumento
        } // fim do if
        produtos.inserir(produto); // o DAO guarda e recusa ID repetido
    } // fim de adicionarProduto

    public boolean removerProduto(int idProduto) { // tira um produto do estoque
        return produtos.remover(idProduto); // true se removeu, false se não existia
    } // fim de removerProduto

    public Produto buscarProduto(int idProduto) { // procura um produto pelo ID
        return produtos.buscarPorId(idProduto); // devolve o produto ou null se não existir
    } // fim de buscarProduto

    public List<Produto> buscarPorNome(String trecho) { // busca pelo começo de qualquer palavra do nome
        String procurado = Validacao.exigirTexto(trecho, "nome para busca").toLowerCase(); // texto em minúsculas
        List<Produto> encontrados = new ArrayList<>(); // resultado começa vazio
        IteradorLista<Produto> iterador = new IteradorLista<>(produtos.listar()); // nosso iterador percorre os produtos
        while (iterador.hasNext()) { // enquanto houver produto
            Produto produto = iterador.next(); // pega o próximo
            for (String palavra : produto.getNome().toLowerCase().split(" ")) { // percorre as palavras (vetor, não lista)
                if (palavra.startsWith(procurado)) { // a palavra começa com o texto digitado?
                    encontrados.add(produto); // achou: entra no resultado
                    break; // não precisa olhar as outras palavras deste produto
                } // fim do if
            } // fim do for das palavras
        } // fim do while dos produtos
        return encontrados; // devolve o que foi encontrado (pode ser vazia)
    } // fim de buscarPorNome

    public boolean verificarDisponibilidade(int idProduto, int quantidade) { // há unidades suficientes?
        exigirQuantidadePositiva(quantidade); // a quantidade pedida precisa ser maior que zero
        return exigirProduto(idProduto).getQuantidade() >= quantidade; // compara o que tem com o que foi pedido
    } // fim de verificarDisponibilidade

    public void reporEstoque(int idProduto, int quantidade) { // chegaram unidades novas
        exigirQuantidadePositiva(quantidade); // só repõe quantidade positiva
        Produto produto = exigirProduto(idProduto); // o produto precisa existir
        produto.alterarQuantidade(quantidade); // soma no estoque
        produtos.atualizar(produto); // avisa o DAO que o produto mudou
    } // fim de reporEstoque

    public void darBaixa(int idProduto, int quantidade) { // tira unidades vendidas do estoque
        if (!verificarDisponibilidade(idProduto, quantidade)) { // não tem o suficiente?
            throw new IllegalStateException("Estoque insuficiente para o produto " + idProduto + "."); // erro de estado
        } // fim do if
        Produto produto = exigirProduto(idProduto); // pega o produto
        produto.alterarQuantidade(-quantidade); // diminui do estoque (negativo = saída)
        produtos.atualizar(produto); // avisa o DAO que o produto mudou
    } // fim de darBaixa

    private Produto exigirProduto(int idProduto) { // busca e exige que exista
        Produto produto = produtos.buscarPorId(idProduto); // procura pelo ID
        if (produto == null) { // não encontrou?
            throw new IllegalArgumentException("Produto " + idProduto + " não encontrado."); // erro de argumento
        } // fim do if
        return produto; // devolve o produto encontrado
    } // fim de exigirProduto

    private static void exigirQuantidadePositiva(int quantidade) { // confere a quantidade recebida
        if (quantidade <= 0) { // zero ou negativa não vale
            throw new IllegalArgumentException("A quantidade deve ser maior que zero."); // erro de argumento
        } // fim do if
    } // fim de exigirQuantidadePositiva

    @Override // sobrescreve o toString de Object
    public String toString() { // texto que descreve o estoque
        return "Estoque com " + produtos.listar().size() + " produto(s)"; // resumo em uma linha
    } // fim do toString
}