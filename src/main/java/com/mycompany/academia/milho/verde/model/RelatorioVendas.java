/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Relatório das vendas de um período (um dia ou um mês).
 *
 * <p>Um dia tem início e fim iguais; um mês vai do primeiro ao último
 * dia. Os totais por tipo de venda são calculados no construtor.</p>
 *
 * @author Aline Ferreira
 */
public class RelatorioVendas {

    /** Primeiro dia do período. */
    private LocalDate inicio;

    /** Último dia do período. */
    private LocalDate fim;

    /** Vendas do período. */
    private List<Venda> vendas;

    /** Soma das vendas de diárias. */
    private double totalDiarias;

    /** Soma das vendas de mensalidades. */
    private double totalMensalidades;

    /** Soma das vendas de produtos. */
    private double totalProdutos;

    /**
     * Construtor padrão: cria um relatório vazio, sem período.
     */
    public RelatorioVendas() {
        this.inicio = null;
        this.fim = null;
        this.vendas = new ArrayList<>();
        this.totalDiarias = 0;
        this.totalMensalidades = 0;
        this.totalProdutos = 0;
    }

    /**
     * Construtor com parâmetros: calcula os totais.
     *
     * @param novoInicio primeiro dia do período
     * @param novoFim último dia do período
     * @param novasVendas vendas do período
     * @throws IllegalArgumentException se algum dado for nulo ou se o
     *         fim for anterior ao início
     */
    public RelatorioVendas(LocalDate novoInicio, LocalDate novoFim,
                           List<Venda> novasVendas) {
        if (novoInicio == null || novoFim == null || novasVendas == null) {
            throw new IllegalArgumentException(
                    "Período e vendas são obrigatórios.");
        }
        if (novoFim.isBefore(novoInicio)) {
            throw new IllegalArgumentException(
                    "O fim não pode ser anterior ao início.");
        }
        this.inicio = novoInicio;
        this.fim = novoFim;
        this.vendas = new ArrayList<>(novasVendas);
        calcularTotais();
    }

    /**
     * Retorna o primeiro dia.
     *
     * @return a data de início
     */
    public LocalDate getInicio() {
        return inicio;
    }

    /**
     * Retorna o último dia.
     *
     * @return a data de fim
     */
    public LocalDate getFim() {
        return fim;
    }

    /**
     * Retorna as vendas do período.
     *
     * @return cópia da lista de vendas
     */
    public List<Venda> getVendas() {
        return new ArrayList<>(vendas);
    }

    /**
     * Retorna o total de diárias.
     *
     * @return o total em reais
     */
    public double getTotalDiarias() {
        return totalDiarias;
    }

    /**
     * Retorna o total de mensalidades.
     *
     * @return o total em reais
     */
    public double getTotalMensalidades() {
        return totalMensalidades;
    }

    /**
     * Retorna o total de produtos.
     *
     * @return o total em reais
     */
    public double getTotalProdutos() {
        return totalProdutos;
    }

    /**
     * Retorna o total geral do período.
     *
     * @return diárias mais mensalidades mais produtos
     */
    public double getTotalGeral() {
        return totalDiarias + totalMensalidades + totalProdutos;
    }

    /**
     * Soma as vendas por tipo.
     */
    private void calcularTotais() {
        totalDiarias = 0;
        totalMensalidades = 0;
        totalProdutos = 0;
        for (Venda venda : vendas) {
            if (Venda.DIARIA.equals(venda.getTipo())) {
                totalDiarias += venda.getValorTotal();
            } else if (Venda.MENSALIDADE.equals(venda.getTipo())) {
                totalMensalidades += venda.getValorTotal();
            } else {
                totalProdutos += venda.getValorTotal();
            }
        }
    }

    /**
     * Retorna o relatório em várias linhas.
     *
     * @return texto com período, número de vendas e totais
     */
    @Override
    public String toString() {
        if (inicio == null) {
            return "Relatório em branco.";
        }
        String periodo = inicio.equals(fim) ? inicio.toString()
                : inicio + " a " + fim;
        return "Relatório de vendas (" + periodo + ")\n"
                + "Vendas: " + vendas.size() + "\n"
                + "Diárias: R$ " + String.format("%.2f", totalDiarias) + "\n"
                + "Mensalidades: R$ " + String.format("%.2f", totalMensalidades) + "\n"
                + "Produtos: R$ " + String.format("%.2f", totalProdutos) + "\n"
                + "Total: R$ " + String.format("%.2f", getTotalGeral());
    }
}
