/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model; // pacote do Model

import java.time.LocalDate; // data sem hora
import java.util.ArrayList; // lista que cresce sozinha
import java.util.List; // tipo da lista

/**
 * Relatório das vendas de um período: das compras da lojinha ou dos
 * pagamentos de diárias e mensalidades.
 *
 * <p>A lista guarda objetos {@link Venda}. Na hora de somar, cada venda
 * calcula o seu próprio total (polimorfismo), então o relatório não precisa
 * saber de qual tipo é cada uma.</p>
 *
 * @author Aline Ferreira
 */
public class RelatorioVendas { // relatório das vendas de um período

    /** Tipo de relatório: vendas da lojinha. */
    public static final String LOJINHA = "LOJINHA"; // tipo: vendas da lojinha
    /** Tipo de relatório: pagamentos de diárias e mensalidades. */
    public static final String DIARIAS_MENSALIDADES = "DIARIAS_MENSALIDADES"; // tipo: pagamentos de aulas

    /** LOJINHA ou DIARIAS_MENSALIDADES; não muda. */
    private final String tipo; // LOJINHA ou DIARIAS_MENSALIDADES; final: não muda
    /** Primeiro dia do período; não muda. */
    private final LocalDate inicio; // primeiro dia do período; final: não muda
    /** Último dia do período; não muda. */
    private final LocalDate fim; // último dia do período; final: não muda
    /** Vendas do período. */
    private final List<Venda> vendas; // vendas do período; final: a lista é sempre a mesma

    /**
     * Cria um relatório de vendas.
     *
     * @param novoTipo {@link #LOJINHA} ou {@link #DIARIAS_MENSALIDADES}
     * @param novoInicio primeiro dia do período
     * @param novoFim último dia do período (não pode ser antes do início)
     * @param novasVendas vendas do período (a lista é copiada)
     * @throws IllegalArgumentException se o período, as vendas ou o tipo forem inválidos
     */
    public RelatorioVendas(String novoTipo, LocalDate novoInicio, LocalDate novoFim, List<Venda> novasVendas) { // construtor com parâmetros
        if (novoInicio == null || novoFim == null || novoFim.isBefore(novoInicio) || novasVendas == null) { // dados inválidos
            throw new IllegalArgumentException("Período ou vendas inválidos."); // erro de argumento
        } // fim do if
        this.tipo = exigirTipo(novoTipo); // só os dois tipos
        this.inicio = novoInicio; // guarda o início
        this.fim = novoFim; // guarda o fim
        this.vendas = new ArrayList<>(novasVendas); // guarda uma cópia das vendas
    } // fim do construtor

    /**
     * Devolve o tipo do relatório.
     *
     * @return LOJINHA ou DIARIAS_MENSALIDADES
     */
    public String getTipo() { // devolve o tipo
        return tipo; // LOJINHA ou DIARIAS_MENSALIDADES
    } // fim de getTipo

    /**
     * Devolve o primeiro dia do período.
     *
     * @return início do período
     */
    public LocalDate getInicio() { // devolve o início
        return inicio; // primeiro dia
    } // fim de getInicio

    /**
     * Devolve o último dia do período.
     *
     * @return fim do período
     */
    public LocalDate getFim() { // devolve o fim
        return fim; // último dia
    } // fim de getFim

    /**
     * Devolve as vendas do período.
     *
     * @return cópia da lista de vendas
     */
    public List<Venda> getVendas() { // devolve as vendas
        return new ArrayList<>(vendas); // cópia da lista
    } // fim de getVendas

    /**
     * Conta quantas vendas há no período.
     *
     * @return quantidade de vendas
     */
    public int getQuantidade() { // quantas vendas
        return vendas.size(); // tamanho da lista
    } // fim de getQuantidade

    /**
     * Soma o total das vendas; cada venda calcula o seu (polimorfismo).
     *
     * @return total do período em reais
     */
    public double getTotal() { // soma das vendas
        double total = 0; // começa em zero
        IteradorLista<Venda> iterador = new IteradorLista<>(vendas); // percorre as vendas
        while (iterador.hasNext()) { // enquanto houver venda
            total = total + iterador.next().calcularTotal(); // polimorfismo: cada venda calcula o seu total
        } // fim do while
        return total; // devolve o total
    } // fim de getTotal

    /**
     * Confere se o tipo é um dos dois permitidos.
     *
     * @param valor tipo a conferir
     * @return o próprio tipo, se for válido
     * @throws IllegalArgumentException se o tipo não existir
     */
    private static String exigirTipo(String valor) { // confere o tipo
        if (!LOJINHA.equals(valor) && !DIARIAS_MENSALIDADES.equals(valor)) { // só os dois tipos
            throw new IllegalArgumentException("Tipo de relatório inválido: " + valor); // erro de argumento
        } // fim do if
        return valor; // devolve o tipo conferido
    } // fim de exigirTipo

    /**
     * Monta o relatório pronto para imprimir.
     *
     * @return cabeçalho, uma linha por venda e o rodapé com quantidade e total
     */
    @Override // sobrescreve o toString de Object
    public String toString() { // relatório pronto para imprimir
        String texto = "Relatório " + tipo + " de " + inicio + " a " + fim + "\n"; // cabeçalho
        IteradorLista<Venda> iterador = new IteradorLista<>(vendas); // percorre as vendas
        while (iterador.hasNext()) { // enquanto houver venda
            texto = texto + "  " + iterador.next() + "\n"; // uma linha por venda
        } // fim do while
        return texto + "Quantidade: " + getQuantidade() + " | Total: R$ " + String.format("%.2f", getTotal()); // rodapé
    } // fim do toString
}