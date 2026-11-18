/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model; // pacote do Model

import java.time.LocalDateTime; // data e hora juntas

public abstract class Venda implements Identificavel { // abstrata: só existem vendas de produtos ou de agendamento

    private final int id; // identificador; final: não muda
    private final Cliente cliente; // quem pagou; final: não muda
    private final LocalDateTime dataHora; // quando foi paga; final: não muda

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

    @Override // vem da interface Identificavel
    public int getId() { // devolve o ID
        return id; // ID da venda
    } // fim de getId

    public Cliente getCliente() { // devolve o cliente
        return cliente; // quem pagou
    } // fim de getCliente

    public LocalDateTime getDataHora() { // devolve a data e hora
        return dataHora; // quando foi paga
    } // fim de getDataHora

    public abstract double calcularTotal(); // cada filha calcula o total do seu jeito

    public abstract String descrever(); // cada filha descreve o que foi pago

    public Extrato gerarExtrato() { // cria o comprovante desta venda
        return new Extrato(cliente, dataHora, descrever(), calcularTotal()); // polimorfismo: chama a versão da filha
    } // fim de gerarExtrato

    @Override // sobrescreve o toString de Object
    public String toString() { // resumo em uma linha
        return "Venda " + id + " - " + dataHora + " - " + cliente.getNome()
                + " - " + descrever() + " - R$ " + String.format("%.2f", calcularTotal()); // usa as versões da filha
    } // fim do toString
}