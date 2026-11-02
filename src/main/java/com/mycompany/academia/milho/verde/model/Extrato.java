/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Representa o extrato de uma venda, entregue ao cliente.
 *
 * <p>Na emissão, a descrição e o valor são copiados da venda. O extrato
 * fica guardado na venda e no cliente, para formar a conta do cliente
 * (item 10).</p>
 *
 * @author Aline Ferreira
 */
public class Extrato {

    /** Identificador único, gerado pelo sistema (0 enquanto não definido). */
    private int id;

    /** Cliente da venda. */
    private Cliente cliente;

    /** Venda que originou o extrato. */
    private Venda venda;

    /** Data e hora da emissão. */
    private LocalDateTime dataEmissao;

    /** O que foi vendido, copiado da venda na emissão. */
    private String descricao;

    /** Valor da venda, copiado na emissão. */
    private double valor;

    /**
     * Construtor padrão: cria um extrato em branco, sem venda.
     */
    public Extrato() {
        this.id = 0;
        this.cliente = null;
        this.venda = null;
        this.dataEmissao = null;
        this.descricao = "";
        this.valor = 0;
    }

    /**
     * Construtor com parâmetros: emite o extrato de uma venda.
     *
     * <p>Normalmente é chamado por {@link Venda#gerarExtrato(int)}.</p>
     *
     * @param novoId identificador positivo
     * @param novoCliente cliente da venda
     * @param novaVenda venda que originou o extrato
     * @param novaDataEmissao data e hora da emissão
     * @throws IllegalArgumentException se algum dado for inválido ou se
     *         a venda for de outro cliente
     */
    public Extrato(int novoId, Cliente novoCliente, Venda novaVenda,
                   LocalDateTime novaDataEmissao) {
        if (novoId <= 0) {
            throw new IllegalArgumentException(
                    "O identificador deve ser positivo.");
        }
        if (novoCliente == null || novaVenda == null) {
            throw new IllegalArgumentException(
                    "O cliente e a venda são obrigatórios.");
        }
        if (novaVenda.getCliente() != novoCliente) {
            throw new IllegalArgumentException(
                    "A venda pertence a outro cliente.");
        }
        if (novaDataEmissao == null) {
            throw new IllegalArgumentException(
                    "A data de emissão é obrigatória.");
        }
        this.id = novoId;
        this.cliente = novoCliente;
        this.venda = novaVenda;
        this.dataEmissao = novaDataEmissao;
        this.valor = novaVenda.getValorTotal();

        // Monta a descrição conforme o tipo da venda.
        if (Venda.PRODUTO.equals(novaVenda.getTipo())) {
            String texto = "Produtos:";
            for (ItemVenda item : novaVenda.getItens()) {
                texto = texto + " " + item + ";";
            }
            this.descricao = texto;
        } else if (Venda.DIARIA.equals(novaVenda.getTipo())) {
            this.descricao = "Diária de aula";
        } else {
            this.descricao = "Mensalidade de aula";
        }
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
     * Retorna o cliente.
     *
     * @return o cliente da venda
     */
    public Cliente getCliente() {
        return cliente;
    }

    /**
     * Retorna a venda que originou o extrato.
     *
     * @return a venda
     */
    public Venda getVenda() {
        return venda;
    }

    /**
     * Retorna a data e a hora da emissão.
     *
     * @return a data e a hora
     */
    public LocalDateTime getDataEmissao() {
        return dataEmissao;
    }

    /**
     * Retorna a descrição do que foi vendido.
     *
     * @return a descrição
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * Retorna o valor do extrato.
     *
     * @return o valor em reais
     */
    public double getValor() {
        return valor;
    }

    /**
     * Monta o texto do extrato, pronto para a View imprimir.
     *
     * @return o extrato em várias linhas
     */
    public String gerarTexto() {
        if (venda == null) {
            return "Extrato em branco.";
        }
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return "===== Academia Milho Verde =====\n"
                + "Extrato nº " + id + "\n"
                + "Cliente: " + cliente.getNome() + " (ID " + cliente.getId() + ")\n"
                + "Data: " + dataEmissao.format(formato) + "\n"
                + "Venda nº " + venda.getId() + " (" + venda.getTipo() + ")\n"
                + descricao + "\n"
                + "Total: R$ " + String.format("%.2f", valor);
    }

    /**
     * Retorna uma descrição curta do extrato.
     *
     * <p>Mostra só o número da venda, e não a venda inteira, porque a
     * venda também aponta para o extrato.</p>
     *
     * @return texto com ID, cliente, venda e valor
     */
    @Override
    public String toString() {
        String nomeCliente = (cliente == null) ? "(sem cliente)" : cliente.getNome();
        String numeroVenda = (venda == null) ? "-" : String.valueOf(venda.getId());
        return "Extrato[id=" + id
                + ", cliente=" + nomeCliente
                + ", venda=" + numeroVenda
                + ", valor=R$ " + String.format("%.2f", valor) + "]";
    }
}
