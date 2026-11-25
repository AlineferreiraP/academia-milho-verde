/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model; // pacote do Model

/**
 * Produto vendido na academia. Cada produto pertence a um setor: a lojinha de
 * material de academia ou a lanchonete. Os dois setores usam o mesmo estoque e
 * as mesmas vendas; o setor só separa o que aparece em cada menu.
 *
 * <p>Implementa {@link Identificavel} para poder ser guardado no DAO genérico.
 * O estoque só é alterado pelo próprio Model, e cada produto criado pelo
 * construtor com parâmetros é contado no {@link Sistema}.</p>
 *
 * @author Aline Ferreira
 */
public class Produto implements Identificavel { // produto vendido na lojinha ou na lanchonete; Identificavel permite usar o DAO genérico

    /** Setor da lojinha de material de academia. */
    public static final String LOJA = "LOJA"; // setor: lojinha
    /** Setor da lanchonete. */
    public static final String LANCHONETE = "LANCHONETE"; // setor: lanchonete

    /** Identificador do produto; não muda depois de criado. */
    private final int id; // identificador; final: não muda depois de criado
    /** Nome do produto. */
    private String nome; // nome do produto
    /** Preço de uma unidade. */
    private double preco; // preço de uma unidade
    /** Unidades disponíveis no estoque. */
    private int quantidade; // unidades disponíveis no estoque
    /** Setor do produto (LOJA ou LANCHONETE); não muda depois de criado. */
    private final String setor; // LOJA ou LANCHONETE

    /**
     * Cria um produto vazio da lojinha (não entra no contador do {@link Sistema}).
     */
    public Produto() { // construtor sem parâmetros
        this.id = 0; // ainda sem id
        this.nome = ""; // ainda sem nome
        this.preco = 0; // ainda sem preço
        this.quantidade = 0; // estoque vazio
        this.setor = LOJA; // setor padrão
    } // fim do construtor sem parâmetros

    /**
     * Cria um produto da lojinha com os dados informados e o conta no {@link Sistema}.
     *
     * @param novoId identificador
     * @param novoNome nome (não pode ser vazio)
     * @param novoPreco preço de uma unidade (maior que zero)
     * @param novaQuantidade estoque inicial (não pode ser negativo)
     * @throws IllegalArgumentException se algum dado for inválido
     */
    public Produto(int novoId, String novoNome, double novoPreco, int novaQuantidade) { // produto da lojinha
        this(novoId, novoNome, novoPreco, novaQuantidade, LOJA); // chama o construtor completo com o setor LOJA
    } // fim do construtor da lojinha

    /**
     * Cria um produto de um setor escolhido e o conta no {@link Sistema}.
     *
     * @param novoId identificador
     * @param novoNome nome (não pode ser vazio)
     * @param novoPreco preço de uma unidade (maior que zero)
     * @param novaQuantidade estoque inicial (não pode ser negativo)
     * @param novoSetor {@link #LOJA} ou {@link #LANCHONETE}
     * @throws IllegalArgumentException se algum dado ou o setor for inválido
     */
    public Produto(int novoId, String novoNome, double novoPreco, int novaQuantidade, String novoSetor) { // construtor completo
        if (novoNome == null || novoNome.isBlank()) { // o nome não pode ser vazio
            throw new IllegalArgumentException("O nome do produto é obrigatório."); // erro de argumento
        } // fim do if
        if (novoPreco <= 0) { // o preço precisa ser positivo
            throw new IllegalArgumentException("O preço deve ser maior que zero."); // erro de argumento
        } // fim do if
        if (novaQuantidade < 0) { // não existe estoque negativo
            throw new IllegalArgumentException("A quantidade não pode ser negativa."); // erro de argumento
        } // fim do if
        if (!LOJA.equals(novoSetor) && !LANCHONETE.equals(novoSetor)) { // só os dois setores
            throw new IllegalArgumentException("Setor inválido: " + novoSetor); // erro de argumento
        } // fim do if
        this.id = novoId; // guarda o id
        this.nome = novoNome.trim(); // guarda o nome sem espaços nas pontas
        this.preco = novoPreco; // guarda o preço
        this.quantidade = novaQuantidade; // guarda o estoque inicial
        this.setor = novoSetor; // guarda o setor
        Sistema.contarProduto(); // soma 1 no contador de produtos do Sistema
    } // fim do construtor completo

    /**
     * Devolve o identificador do produto.
     *
     * @return ID do produto
     */
    @Override // vem da interface Identificavel
    public int getId() { // devolve o id
        return id; // id do produto
    } // fim do getId

    /**
     * Devolve o nome do produto.
     *
     * @return nome do produto
     */
    public String getNome() { // devolve o nome
        return nome; // nome do produto
    } // fim do getNome

    /**
     * Devolve o preço de uma unidade.
     *
     * @return preço de uma unidade
     */
    public double getPreco() { // devolve o preço
        return preco; // preço de uma unidade
    } // fim do getPreco

    /**
     * Devolve quantas unidades há no estoque.
     *
     * @return unidades disponíveis
     */
    public int getQuantidade() { // devolve o estoque
        return quantidade; // unidades disponíveis
    } // fim do getQuantidade

    /**
     * Devolve o setor do produto. Não tem set: o setor é definido no cadastro.
     *
     * @return {@link #LOJA} ou {@link #LANCHONETE}
     */
    public String getSetor() { // devolve o setor
        return setor; // LOJA ou LANCHONETE
    } // fim do getSetor

    /**
     * Acesso de pacote: só o Model muda o estoque.
     *
     * @param diferenca valor positivo para repor, negativo para vender
     * @throws IllegalStateException se o estoque ficaria negativo
     */
    void alterarQuantidade(int diferenca) { // pacote: só o Model muda o estoque; positivo repõe, negativo vende
        if (quantidade + diferenca < 0) { // o estoque não pode ficar negativo
            throw new IllegalStateException("Estoque insuficiente de " + nome + "."); // erro de estado
        } // fim do if
        quantidade = quantidade + diferenca; // aplica a mudança
    } // fim do alterarQuantidade

    /**
     * Descreve o produto.
     *
     * @return identificador, nome, preço e estoque em uma linha (com "lanchonete" no fim, se for desse setor)
     */
    @Override // sobrescreve o toString de Object
    public String toString() { // texto que descreve o produto
        String texto = "Produto " + id + ": " + nome + " - R$ " + String.format("%.2f", preco)
                + " - estoque: " + quantidade; // resumo em uma linha
        if (LANCHONETE.equals(setor)) { // é da lanchonete?
            texto = texto + " - lanchonete"; // indica o setor
        } // fim do if
        return texto; // devolve o resumo
    } // fim do toString
}