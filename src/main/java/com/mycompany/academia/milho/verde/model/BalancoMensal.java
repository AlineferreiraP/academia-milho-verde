/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model; // pacote do Model

import java.time.YearMonth; // ano e mês juntos
import java.util.List; // tipo da lista

/**
 * Balanço de um mês: receitas, despesas, saldo e algumas estatísticas básicas
 * (número de vendas, ticket médio e número de acessos pela catraca).
 *
 * <p>Os números só aparecem depois de chamar {@link #calcular(List, List, List)}.</p>
 */
public class BalancoMensal { // receitas, despesas e estatísticas básicas de um mês

    /** Ano do balanço; não muda. */
    private final int ano; // ano do balanço; final: não muda
    /** Mês do balanço (1 a 12); não muda. */
    private final int mes; // mês do balanço; final: não muda
    /** Soma das vendas do mês. */
    private double totalReceitas; // soma das vendas do mês
    /** Soma das despesas do mês. */
    private double totalDespesas; // soma das despesas do mês
    /** Quantas vendas houve no mês. */
    private int quantidadeVendas; // quantas vendas no mês
    /** Quantas entradas houve na catraca no mês. */
    private int quantidadeAcessos; // quantas entradas na catraca no mês

    /**
     * Cria o balanço de um mês, ainda sem números.
     *
     * @param novoAno ano do balanço
     * @param novoMes mês do balanço (1 a 12)
     * @throws java.time.DateTimeException se o mês for inválido
     */
    public BalancoMensal(int novoAno, int novoMes) { // construtor com parâmetros
        YearMonth.of(novoAno, novoMes); // recusa mês inválido (lança DateTimeException)
        this.ano = novoAno; // guarda o ano
        this.mes = novoMes; // guarda o mês
    } // fim do construtor

    /**
     * Devolve o ano do balanço.
     *
     * @return o ano
     */
    public int getAno() { // devolve o ano
        return ano; // ano
    } // fim de getAno

    /**
     * Devolve o mês do balanço.
     *
     * @return o mês (1 a 12)
     */
    public int getMes() { // devolve o mês
        return mes; // mês
    } // fim de getMes

    /**
     * Devolve a soma das receitas do mês.
     *
     * @return total de receitas em reais
     */
    public double getTotalReceitas() { // devolve as receitas
        return totalReceitas; // soma das vendas
    } // fim de getTotalReceitas

    /**
     * Devolve a soma das despesas do mês.
     *
     * @return total de despesas em reais
     */
    public double getTotalDespesas() { // devolve as despesas
        return totalDespesas; // soma das despesas
    } // fim de getTotalDespesas

    /**
     * Devolve o número de vendas do mês.
     *
     * @return quantidade de vendas
     */
    public int getQuantidadeVendas() { // devolve o número de vendas
        return quantidadeVendas; // vendas do mês
    } // fim de getQuantidadeVendas

    /**
     * Devolve o número de entradas na catraca no mês.
     *
     * @return quantidade de acessos
     */
    public int getQuantidadeAcessos() { // devolve o número de entradas
        return quantidadeAcessos; // entradas do mês
    } // fim de getQuantidadeAcessos

    /**
     * Calcula o saldo do mês.
     *
     * @return receitas menos despesas
     */
    public double getSaldo() { // receitas menos despesas
        return totalReceitas - totalDespesas; // saldo do mês
    } // fim de getSaldo

    /**
     * Calcula o valor médio por venda.
     *
     * @return receitas divididas pelo número de vendas, ou zero se não houve vendas
     */
    public double getTicketMedio() { // valor médio por venda
        if (quantidadeVendas == 0) { // sem vendas não há média
            return 0; // evita divisão por zero
        } // fim do if
        return totalReceitas / quantidadeVendas; // média por venda
    } // fim de getTicketMedio

    /**
     * Faz as contas do mês a partir dos dados recebidos.
     * Cada venda calcula o seu total (polimorfismo) e só as entradas da catraca contam como acesso.
     *
     * @param vendas vendas do mês
     * @param despesas despesas do mês
     * @param registros registros da catraca no mês
     */
    public void calcular(List<Venda> vendas, List<Despesa> despesas, List<RegistroCatraca> registros) { // faz as contas do mês
        totalReceitas = 0; // zera antes de somar
        IteradorLista<Venda> iteradorVendas = new IteradorLista<>(vendas); // percorre as vendas
        while (iteradorVendas.hasNext()) { // enquanto houver venda
            totalReceitas = totalReceitas + iteradorVendas.next().calcularTotal(); // polimorfismo
        } // fim do while
        quantidadeVendas = vendas.size(); // número de vendas
        totalDespesas = 0; // zera antes de somar
        IteradorLista<Despesa> iteradorDespesas = new IteradorLista<>(despesas); // percorre as despesas
        while (iteradorDespesas.hasNext()) { // enquanto houver despesa
            totalDespesas = totalDespesas + iteradorDespesas.next().getValor(); // soma o valor
        } // fim do while
        quantidadeAcessos = 0; // zera antes de contar
        IteradorLista<RegistroCatraca> iteradorRegistros = new IteradorLista<>(registros); // percorre os registros
        while (iteradorRegistros.hasNext()) { // enquanto houver registro
            if (RegistroCatraca.ENTRADA.equals(iteradorRegistros.next().getTipo())) { // só entradas contam
                quantidadeAcessos++; // mais um acesso
            } // fim do if
        } // fim do while
    } // fim de calcular

    /**
     * Monta o balanço pronto para imprimir.
     *
     * @return uma informação por linha: receitas, despesas, saldo, vendas, ticket médio e acessos
     */
    @Override // sobrescreve o toString de Object
    public String toString() { // balanço pronto para imprimir
        return "Balanço " + mes + "/" + ano
                + "\n  Receitas: R$ " + String.format("%.2f", totalReceitas)
                + "\n  Despesas: R$ " + String.format("%.2f", totalDespesas)
                + "\n  Saldo: R$ " + String.format("%.2f", getSaldo())
                + "\n  Vendas: " + quantidadeVendas + " | Ticket médio: R$ " + String.format("%.2f", getTicketMedio())
                + "\n  Acessos: " + quantidadeAcessos; // uma informação por linha
    } // fim do toString
}