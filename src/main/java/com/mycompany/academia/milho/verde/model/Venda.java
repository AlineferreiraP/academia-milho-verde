/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model; // pacote do Model

import java.time.LocalDateTime; // data e hora juntas

/**
 * Venda feita a um cliente. É abstrata: só existem vendas de produtos ({@link VendaProdutos})
 * ou de agendamento ({@link VendaAgendamento}). Guarda o que é comum (ID, cliente, data e hora)
 * e deixa cada filha calcular o total e descrever o que foi pago.
 *
 * @author Aline Ferreira
 */
public abstract class Venda implements Identificavel { // abstrata: só existem vendas de produtos ou de agendamento

    /** Identificador da venda; não muda. */
    private final int id; // identificador; final: não muda
    /** Quem pagou; não muda. */
    private final Cliente cliente; // quem pagou; final: não muda
    /** Quando foi paga; não muda. */
    private final LocalDateTime dataHora; // quando foi paga; final: não muda

    /**
     * Cria a venda validando os dados (chamado pelas filhas com super).
     *
     * @param novoId ID da venda (maior que zero)
     * @param novoCliente quem pagou (não pode ser nulo)
     * @param novaDataHora quando foi paga (não pode ser nula)
     * @throws IllegalArgumentException se algum dado for inválido
     */
    public Venda(int novoId, Cliente novoCliente, LocalDateTime novaDataHora) { // construtor chamado pelas filhas com super(...)
        if (novoId <= 0) { // ID precisa ser maior que zero
            throw new IllegalArgumentException("O ID deve ser maior que zero."); // erro de argumento
        } // fim do if
        if (novoCliente == null) { // toda venda tem um cliente
            throw new IllegalArgumentException("Informe o cliente da venda."); // erro de argumento
        } // fim do if
        if (novaDataHora == null) { // toda venda tem data e hora
            throw new IllegalArgumentException("Informe a data e hora da venda."); // erro de argumento
        } // fim do if
        this.id = novoId; // guarda o ID
        this.cliente = novoCliente; // guarda o cliente
        this.dataHora = novaDataHora; // guarda a data e hora
    } // fim do construtor

    /**
     * Devolve: ID da venda.
     *
     * @return ID da venda
     */
    @Override // vem da interface Identificavel
    public int getId() { // devolve o ID
        return id; // ID da venda
    } // fim de getId

    /**
     * Devolve: quem pagou.
     *
     * @return quem pagou
     */
    public Cliente getCliente() { // devolve o cliente
        return cliente; // quem pagou
    } // fim de getCliente

    /**
     * Devolve: quando foi paga.
     *
     * @return quando foi paga
     */
    public LocalDateTime getDataHora() { // devolve a data e hora
        return dataHora; // quando foi paga
    } // fim de getDataHora

    /**
     * Calcula o total da venda; cada filha faz do seu jeito.
     *
     * @return total em reais
     */
    public abstract double calcularTotal(); // cada filha calcula o total do seu jeito

    /**
     * Descreve o que foi pago; cada filha faz do seu jeito.
     *
     * @return texto da descrição
     */
    public abstract String descrever(); // cada filha descreve o que foi pago

    /**
     * Cria o comprovante desta venda (polimorfismo: usa a versão da filha).
     *
     * @return extrato com descrição e total
     */
    public Extrato gerarExtrato() { // cria o comprovante desta venda
        return new Extrato(cliente, dataHora, descrever(), calcularTotal()); // polimorfismo: chama a versão da filha
    } // fim de gerarExtrato

    /**
     * Resume a venda em uma linha.
     *
     * @return ID, data, cliente, descrição e total
     */
    @Override // sobrescreve o toString de Object
    public String toString() { // resumo em uma linha
        return "Venda " + id + " - " + dataHora + " - " + cliente.getNome()
                + " - " + descrever() + " - R$ " + String.format("%.2f", calcularTotal()); // usa as versões da filha
    } // fim do toString
}