/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa uma venda feita a um cliente.
 *
 * <p>Há três tipos de venda. A venda de produtos ({@link #PRODUTO}),
 * da lojinha ou da lanchonete, tem itens, e o total é a soma deles.
 * A venda de diária ({@link #DIARIA}) ou de mensalidade
 * ({@link #MENSALIDADE}) nasce da confirmação de um agendamento: não
 * tem itens e o valor vem do agendamento, por
 * {@link #setValorTotal(double)}.</p>
 *
 * <p>Os itens fazem parte da venda (composição): só existem dentro
 * dela. Toda venda gera um extrato, que também fica guardado no
 * cliente.</p>
 *
 * @author Aline Ferreira
 */
public class Venda {

    /** Tipo de venda: diária de uma aula. */
    public static final String DIARIA = "DIARIA";

    /** Tipo de venda: mensalidade de uma aula. */
    public static final String MENSALIDADE = "MENSALIDADE";

    /** Tipo de venda: produtos da lojinha ou da lanchonete. */
    public static final String PRODUTO = "PRODUTO";

    /** Identificador único, gerado pelo sistema (0 enquanto não definido). */
    private int id;

    /** Cliente que comprou. */
    private Cliente cliente;

    /** Tipo: {@link #DIARIA}, {@link #MENSALIDADE} ou {@link #PRODUTO}. */
    private String tipo;

    /** Data e hora da venda. */
    private LocalDateTime dataHora;

    /** Itens vendidos; só a venda de produtos tem itens. */
    private List<ItemVenda> itens;

    /** Valor total da venda, em reais. */
    private double valorTotal;

    /** Extrato da venda, ou {@code null} enquanto não for gerado. */
    private Extrato extrato;

    /**
     * Construtor padrão: cria uma venda em branco, sem cliente e sem
     * itens.
     */
    public Venda() {
        this.id = 0;
        this.cliente = null;
        this.tipo = "";
        this.dataHora = null;
        this.itens = new ArrayList<>();
        this.valorTotal = 0;
        this.extrato = null;
    }

    /**
     * Construtor com parâmetros: abre uma venda sem itens e com valor
     * zero.
     *
     * @param novoId identificador positivo
     * @param novoCliente cliente que compra
     * @param novoTipo {@link #DIARIA}, {@link #MENSALIDADE} ou
     *        {@link #PRODUTO}
     * @param novaDataHora data e hora da venda
     * @throws IllegalArgumentException se algum dado for inválido
     */
    public Venda(int novoId, Cliente novoCliente, String novoTipo,
                 LocalDateTime novaDataHora) {
        if (novoId <= 0) {
            throw new IllegalArgumentException(
                    "O identificador deve ser positivo.");
        }
        if (novoCliente == null) {
            throw new IllegalArgumentException("O cliente é obrigatório.");
        }
        if (novaDataHora == null) {
            throw new IllegalArgumentException(
                    "A data e a hora da venda são obrigatórias.");
        }
        this.id = novoId;
        this.cliente = novoCliente;
        this.tipo = exigirTipo(novoTipo);
        this.dataHora = novaDataHora;
        this.itens = new ArrayList<>();
        this.valorTotal = 0;
        this.extrato = null;
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
     * Retorna o cliente da venda.
     *
     * @return o cliente
     */
    public Cliente getCliente() {
        return cliente;
    }

    /**
     * Retorna o tipo da venda.
     *
     * @return {@link #DIARIA}, {@link #MENSALIDADE} ou {@link #PRODUTO}
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Retorna a data e a hora da venda.
     *
     * @return a data e a hora
     */
    public LocalDateTime getDataHora() {
        return dataHora;
    }

    /**
     * Retorna os itens vendidos.
     *
     * <p>Devolve uma cópia: itens só entram por
     * {@link #adicionarItem(ItemVenda)}, que também atualiza o total.</p>
     *
     * @return cópia da lista de itens
     */
    public List<ItemVenda> getItens() {
        return new ArrayList<>(itens);
    }

    /**
     * Retorna o valor total.
     *
     * @return o total em reais
     */
    public double getValorTotal() {
        return valorTotal;
    }

    /**
     * Retorna o extrato da venda.
     *
     * @return o extrato, ou {@code null} se ainda não foi gerado
     */
    public Extrato getExtrato() {
        return extrato;
    }

    /**
     * Acrescenta um item a uma venda de produtos e soma o subtotal ao
     * total.
     *
     * <p>Não confere o estoque: quem registra a venda verifica a
     * disponibilidade de todos os itens antes de dar baixa.</p>
     *
     * @param item item a acrescentar
     * @throws IllegalArgumentException se o item for nulo
     * @throws IllegalStateException se a venda não for de produtos ou
     *         se o extrato já tiver sido gerado
     */
    public void adicionarItem(ItemVenda item) {
        if (item == null) {
            throw new IllegalArgumentException("O item é obrigatório.");
        }
        if (!PRODUTO.equals(tipo)) {
            throw new IllegalStateException(
                    "Só a venda de produtos tem itens.");
        }
        if (extrato != null) {
            throw new IllegalStateException(
                    "A venda já foi fechada com extrato.");
        }
        itens.add(item);
        this.valorTotal = valorTotal + item.calcularSubtotal();
    }

    /**
     * Define o valor de uma venda de diária ou mensalidade.
     *
     * <p>O valor vem do agendamento confirmado. Na venda de produtos o
     * total é sempre a soma dos itens, por isso este método não vale
     * para ela.</p>
     *
     * @param novoValor valor em reais, zero ou maior
     * @throws IllegalArgumentException se o valor for negativo
     * @throws IllegalStateException se a venda for de produtos ou se o
     *         extrato já tiver sido gerado
     */
    public void setValorTotal(double novoValor) {
        if (novoValor < 0) {
            throw new IllegalArgumentException(
                    "O valor não pode ser negativo.");
        }
        if (PRODUTO.equals(tipo)) {
            throw new IllegalStateException(
                    "Na venda de produtos o total é a soma dos itens.");
        }
        if (extrato != null) {
            throw new IllegalStateException(
                    "A venda já foi fechada com extrato.");
        }
        this.valorTotal = novoValor;
    }

    /**
     * Gera o extrato da venda e o guarda também no cliente (item 10).
     *
     * <p>Cada venda tem um único extrato. Depois dele, a venda não
     * aceita mais itens nem mudança de valor.</p>
     *
     * @param idExtrato identificador positivo do extrato
     * @return o extrato gerado
     * @throws IllegalStateException se a venda não tiver cliente, não
     *         tiver valor ou já tiver extrato
     * @throws IllegalArgumentException se o identificador for inválido
     */
    public Extrato gerarExtrato(int idExtrato) {
        if (cliente == null) {
            throw new IllegalStateException("A venda não tem cliente.");
        }
        if (extrato != null) {
            throw new IllegalStateException(
                    "O extrato desta venda já foi gerado.");
        }
        if (valorTotal <= 0) {
            throw new IllegalStateException(
                    "A venda não tem valor para gerar extrato.");
        }
        // A data do extrato é a da venda.
        Extrato novoExtrato = new Extrato(idExtrato, cliente, this, dataHora);
        cliente.adicionarExtrato(novoExtrato);
        this.extrato = novoExtrato;
        return novoExtrato;
    }

    /**
     * Define o identificador de uma venda criada pelo construtor padrão.
     *
     * <p>Tem acesso de pacote: a gestão que gera os IDs chama. Funciona
     * uma única vez.</p>
     *
     * @param novoId identificador positivo
     * @throws IllegalArgumentException se {@code novoId} não for positivo
     * @throws IllegalStateException se a venda já tiver identificador
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
     * Confere se o tipo é uma das constantes aceitas.
     *
     * @param valor tipo informado
     * @return o próprio {@code valor}, quando válido
     * @throws IllegalArgumentException se não for {@link #DIARIA},
     *         {@link #MENSALIDADE} nem {@link #PRODUTO}
     */
    private static String exigirTipo(String valor) {
        if (DIARIA.equals(valor) || MENSALIDADE.equals(valor)
                || PRODUTO.equals(valor)) {
            return valor;
        }
        throw new IllegalArgumentException(
                "O tipo deve ser DIARIA, MENSALIDADE ou PRODUTO.");
    }

    /**
     * Retorna uma descrição legível da venda.
     *
     * <p>Mostra só o número do extrato, e não o extrato inteiro, porque
     * o extrato também aponta para a venda.</p>
     *
     * @return texto com ID, cliente, tipo, número de itens, total e extrato
     */
    @Override
    public String toString() {
        String nomeCliente = (cliente == null) ? "(sem cliente)" : cliente.getNome();
        String numeroExtrato = (extrato == null) ? "não gerado"
                : String.valueOf(extrato.getId());
        return "Venda[id=" + id
                + ", cliente=" + nomeCliente
                + ", tipo=" + tipo
                + ", itens=" + itens.size()
                + ", total=R$ " + String.format("%.2f", valorTotal)
                + ", extrato=" + numeroExtrato + "]";
    }
}
