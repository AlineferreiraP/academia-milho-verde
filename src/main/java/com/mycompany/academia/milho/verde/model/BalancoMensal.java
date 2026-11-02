/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model;

import java.util.List;

/**
 * Balanço de um mês: receitas, despesas, saldo, número de vendas,
 * ticket médio e número de entradas na catraca.
 *
 * @author Aline Ferreira
 */
public class BalancoMensal {

    /** Ano do balanço. */
    private int ano;

    /** Mês do balanço, de 1 a 12. */
    private int mes;

    /** Soma das vendas do mês. */
    private double totalReceitas;

    /** Soma das despesas do mês. */
    private double totalDespesas;

    /** Número de vendas do mês. */
    private int quantidadeVendas;

    /** Número de entradas na catraca no mês. */
    private int quantidadeAcessos;

    /**
     * Construtor padrão: cria um balanço zerado, sem mês.
     */
    public BalancoMensal() {
        this.ano = 0;
        this.mes = 0;
        this.totalReceitas = 0;
        this.totalDespesas = 0;
        this.quantidadeVendas = 0;
        this.quantidadeAcessos = 0;
    }

    /**
     * Construtor com parâmetros: cria o balanço de um mês, ainda zerado.
     *
     * @param novoAno ano com quatro dígitos
     * @param novoMes mês, de 1 a 12
     * @throws IllegalArgumentException se o ano ou o mês forem inválidos
     */
    public BalancoMensal(int novoAno, int novoMes) {
        if (novoAno < 2000 || novoAno > 2100) {
            throw new IllegalArgumentException(
                    "Ano inválido: informe um ano com quatro dígitos.");
        }
        if (novoMes < 1 || novoMes > 12) {
            throw new IllegalArgumentException("O mês deve ser de 1 a 12.");
        }
        this.ano = novoAno;
        this.mes = novoMes;
        this.totalReceitas = 0;
        this.totalDespesas = 0;
        this.quantidadeVendas = 0;
        this.quantidadeAcessos = 0;
    }

    /**
     * Retorna o ano.
     *
     * @return o ano
     */
    public int getAno() {
        return ano;
    }

    /**
     * Retorna o mês.
     *
     * @return o mês
     */
    public int getMes() {
        return mes;
    }

    /**
     * Retorna o total de receitas.
     *
     * @return o total em reais
     */
    public double getTotalReceitas() {
        return totalReceitas;
    }

    /**
     * Retorna o total de despesas.
     *
     * @return o total em reais
     */
    public double getTotalDespesas() {
        return totalDespesas;
    }

    /**
     * Retorna o número de vendas.
     *
     * @return a quantidade de vendas
     */
    public int getQuantidadeVendas() {
        return quantidadeVendas;
    }

    /**
     * Retorna o número de entradas na catraca.
     *
     * @return a quantidade de acessos
     */
    public int getQuantidadeAcessos() {
        return quantidadeAcessos;
    }

    /**
     * Calcula o saldo do mês.
     *
     * @return receitas menos despesas
     */
    public double getSaldo() {
        return totalReceitas - totalDespesas;
    }

    /**
     * Calcula o ticket médio: receitas divididas pelo número de vendas.
     *
     * @return o ticket médio, ou 0 se não houve vendas
     */
    public double getTicketMedio() {
        if (quantidadeVendas == 0) {
            return 0;
        }
        return totalReceitas / quantidadeVendas;
    }

    /**
     * Calcula os totais do mês a partir das listas informadas. Só
     * entram os itens do ano e do mês deste balanço.
     *
     * @param vendas todas as vendas
     * @param despesas todas as despesas
     * @param registros todos os registros da catraca
     * @throws IllegalArgumentException se alguma lista for nula
     */
    public void calcular(List<Venda> vendas, List<Despesa> despesas,
                         List<RegistroCatraca> registros) {
        if (vendas == null || despesas == null || registros == null) {
            throw new IllegalArgumentException("As listas são obrigatórias.");
        }
        totalReceitas = 0;
        totalDespesas = 0;
        quantidadeVendas = 0;
        quantidadeAcessos = 0;
        for (Venda venda : vendas) {
            if (venda.getDataHora().getYear() == ano
                    && venda.getDataHora().getMonthValue() == mes) {
                totalReceitas += venda.getValorTotal();
                quantidadeVendas++;
            }
        }
        for (Despesa despesa : despesas) {
            if (despesa.getData().getYear() == ano
                    && despesa.getData().getMonthValue() == mes) {
                totalDespesas += despesa.getValor();
            }
        }
        for (RegistroCatraca registro : registros) {
            if (RegistroCatraca.ENTRADA.equals(registro.getTipo())
                    && registro.getDataHora().getYear() == ano
                    && registro.getDataHora().getMonthValue() == mes) {
                quantidadeAcessos++;
            }
        }
    }

    /**
     * Retorna o balanço em várias linhas.
     *
     * @return texto com os totais do mês
     */
    @Override
    public String toString() {
        return String.format("Balanço de %02d/%d%n", mes, ano)
                + "Receitas: R$ " + String.format("%.2f", totalReceitas) + "\n"
                + "Despesas: R$ " + String.format("%.2f", totalDespesas) + "\n"
                + "Saldo: R$ " + String.format("%.2f", getSaldo()) + "\n"
                + "Vendas: " + quantidadeVendas + "\n"
                + "Ticket médio: R$ " + String.format("%.2f", getTicketMedio()) + "\n"
                + "Entradas na catraca: " + quantidadeAcessos;
    }
}
