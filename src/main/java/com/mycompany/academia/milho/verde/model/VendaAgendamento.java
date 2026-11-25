/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model; // pacote do Model

import java.time.LocalDateTime; // data e hora juntas

/**
 * Pagamento de uma diária ou de uma mensalidade: é uma {@link Venda} ligada a um {@link Agendamento}.
 * Guarda o valor pago e, se o agendamento for cancelado, o valor devolvido ao cliente.
 *
 * @author Aline Ferreira
 */
public class VendaAgendamento extends Venda { // pagamento de diária ou mensalidade: é uma Venda

    /** O que foi pago; não muda. */
    private final Agendamento agendamento; // o que foi pago; final: não muda
    /** Quanto foi pago; não muda. */
    private final double valor; // quanto foi pago; final: não muda
    /** Quanto voltou ao cliente num cancelamento (começa em 0). */
    private double valorDevolvido; // quanto voltou ao cliente num cancelamento (começa em 0)

    /**
     * Cria o pagamento de um agendamento; o valor é o preço combinado nele.
     *
     * @param novoId ID da venda (maior que zero)
     * @param novoAgendamento agendamento pago (não pode ser nulo)
     * @param novaDataHora quando foi pago (não pode ser nula)
     * @throws IllegalArgumentException se algum dado for inválido
     */
    public VendaAgendamento(int novoId, Agendamento novoAgendamento, LocalDateTime novaDataHora) { // construtor com parâmetros
        super(novoId, clienteDe(novoAgendamento), novaDataHora); // o cliente vem do próprio agendamento
        this.agendamento = novoAgendamento; // guarda o agendamento
        this.valor = novoAgendamento.getPreco(); // o valor pago é o preço combinado no agendamento
        this.valorDevolvido = 0; // nada devolvido ainda
    } // fim do construtor

    /**
     * Lê o cliente do agendamento sem quebrar se ele for nulo.
     *
     * @param agendamento agendamento consultado
     * @return dono do agendamento
     * @throws IllegalArgumentException se o agendamento for nulo
     */
    private static Cliente clienteDe(Agendamento agendamento) { // lê o cliente sem quebrar se vier null
        if (agendamento == null) { // sem agendamento?
            throw new IllegalArgumentException("Informe o agendamento do pagamento."); // erro de argumento
        } // fim do if
        return agendamento.getCliente(); // dono do agendamento
    } // fim de clienteDe

    /**
     * Devolve: agendamento pago.
     *
     * @return agendamento pago
     */
    public Agendamento getAgendamento() { // devolve o agendamento
        return agendamento; // o que foi pago
    } // fim de getAgendamento

    /**
     * Devolve: DIARIA ou MENSALIDADE.
     *
     * @return DIARIA ou MENSALIDADE
     */
    public String getTipo() { // DIARIA ou MENSALIDADE
        return agendamento.getTipoCobranca(); // pergunta ao agendamento
    } // fim de getTipo

    /**
     * Devolve: valor pago.
     *
     * @return valor pago
     */
    public double getValor() { // devolve o valor pago
        return valor; // quanto foi pago
    } // fim de getValor

    /**
     * Devolve: valor que voltou ao cliente.
     *
     * @return valor que voltou ao cliente
     */
    public double getValorDevolvido() { // devolve o valor devolvido
        return valorDevolvido; // quanto voltou ao cliente
    } // fim de getValorDevolvido

    /**
     * Guarda quanto voltou ao cliente. Só vale para agendamento cancelado e só uma vez.
     *
     * @param novoValorDevolvido valor devolvido (entre zero e o valor pago)
     * @throws IllegalStateException se o agendamento não estiver cancelado ou a devolução já tiver sido registrada
     * @throws IllegalArgumentException se o valor for inválido
     */
    public void registrarDevolucao(double novoValorDevolvido) { // guarda quanto voltou ao cliente
        if (!Agendamento.CANCELADO.equals(agendamento.getStatus())) { // só agendamento cancelado tem devolução
            throw new IllegalStateException("O agendamento não está cancelado."); // erro de estado
        } // fim do if
        if (valorDevolvido > 0) { // a devolução só acontece uma vez
            throw new IllegalStateException("A devolução já foi registrada."); // erro de estado
        } // fim do if
        if (novoValorDevolvido < 0 || novoValorDevolvido > valor) { // entre zero e o valor pago
            throw new IllegalArgumentException("Valor de devolução inválido."); // erro de argumento
        } // fim do if
        valorDevolvido = novoValorDevolvido; // guarda a devolução
    } // fim de registrarDevolucao

    /**
     * Calcula o que a academia ficou de fato: pago menos devolvido.
     *
     * @return total em reais
     */
    @Override // redefine o método abstrato da Venda
    public double calcularTotal() { // o que a academia ficou de fato
        return valor - valorDevolvido; // pago menos devolvido
    } // fim de calcularTotal

    /**
     * Descreve o que foi pago, por exemplo "Diária de PILATES (agendamento 3)".
     *
     * @return texto da descrição
     */
    @Override // redefine o método abstrato da Venda
    public String descrever() { // texto do que foi pago
        String tipo = Agendamento.DIARIA.equals(getTipo()) ? "Diária" : "Mensalidade"; // nome do tipo para mostrar
        return tipo + " de " + agendamento.getSala().getModalidade()
                + " (agendamento " + agendamento.getId() + ")"; // ex.: Diária de PILATES (agendamento 3)
    } // fim de descrever

    /**
     * Resume a venda: o texto da mãe mais o valor devolvido.
     *
     * @return resumo em uma linha
     */
    @Override // redefine o toString da Venda
    public String toString() { // resumo em uma linha
        return super.toString() + " (devolvido: R$ " + String.format("%.2f", valorDevolvido) + ")"; // o da mãe mais a devolução
    } // fim do toString
}