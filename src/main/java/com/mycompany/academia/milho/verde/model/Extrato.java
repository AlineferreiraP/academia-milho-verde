/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model; // pacote do Model

import java.time.LocalDateTime; // data e hora juntas

public class Extrato { // comprovante de um pagamento ou devolução, guardado no cliente

    private final Cliente cliente; // de quem é o extrato; final: não muda
    private final LocalDateTime dataEmissao; // quando foi emitido; final: não muda
    private final String descricao; // o que foi pago; final: não muda
    private final double valor; // quanto foi pago; negativo quando é devolução; final: não muda

    public Extrato(Cliente novoCliente, LocalDateTime novaDataEmissao,
            String novaDescricao, double novoValor) { // construtor com parâmetros
        if (novoCliente == null) { // o extrato precisa ter dono
            throw new IllegalArgumentException("Informe o cliente do extrato."); // erro de argumento
        } // fim do if
        if (novaDataEmissao == null) { // precisa ter data
            throw new IllegalArgumentException("Informe a data do extrato."); // erro de argumento
        } // fim do if
        this.cliente = novoCliente; // guarda o cliente
        this.dataEmissao = novaDataEmissao; // guarda a data e hora
        this.descricao = Validacao.exigirTexto(novaDescricao, "descrição do extrato"); // a descrição não pode ser vazia
        this.valor = novoValor; // guarda o valor (pode ser negativo numa devolução)
    } // fim do construtor

    public Cliente getCliente() { // devolve o cliente
        return cliente; // dono do extrato
    } // fim de getCliente

    public LocalDateTime getDataEmissao() { // devolve a data de emissão
        return dataEmissao; // data e hora
    } // fim de getDataEmissao

    public String getDescricao() { // devolve a descrição
        return descricao; // o que foi pago
    } // fim de getDescricao

    public double getValor() { // devolve o valor
        return valor; // quanto foi pago
    } // fim de getValor

    public String gerarTexto() { // monta o extrato para imprimir
        return "========== EXTRATO ==========\n"
                + "Cliente: " + cliente.getId() + " - " + cliente.getNome() + "\n" // quem pagou
                + "Data: " + dataEmissao + "\n" // quando
                + "Descrição: " + descricao + "\n" // o quê
                + "Valor: R$ " + String.format("%.2f", valor) + "\n" // quanto
                + "============================="; // fim do comprovante
    } // fim de gerarTexto

    @Override // sobrescreve o toString de Object
    public String toString() { // resumo em uma linha
        return dataEmissao + " - " + descricao + " - R$ " + String.format("%.2f", valor); // data, descrição e valor
    } // fim do toString
}