/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model; // pacote do Model

import java.time.LocalDateTime; // data e hora

/**
 * Uma passagem de um cliente pela catraca (entrada ou saída).
 *
 * <p>Depois de criado o registro não muda: todos os atributos são {@code final},
 * pois o histórico de acessos não deve ser reescrito.</p>
 *
 * @author Aline Ferreira
 */
public class RegistroCatraca implements Identificavel { // uma passagem pela catraca (entrada ou saída)

    /** Tipo de registro: o cliente entrou. */
    public static final String ENTRADA = "ENTRADA"; // tipo: o cliente entrou
    /** Tipo de registro: o cliente saiu. */
    public static final String SAIDA = "SAIDA"; // tipo: o cliente saiu

    /** Identificador do registro; não muda. */
    private final int id; // identificador; final: não muda
    /** Cliente que passou pela catraca; não muda. */
    private final Cliente cliente; // quem passou; final: não muda
    /** Tipo do registro: ENTRADA ou SAIDA; não muda. */
    private final String tipo; // ENTRADA ou SAIDA; final: não muda
    /** Data e hora da passagem; não muda. */
    private final LocalDateTime dataHora; // quando passou; final: não muda

    /**
     * Cria um registro de passagem pela catraca.
     *
     * @param novoId identificador (maior que zero)
     * @param novoCliente cliente que passou
     * @param novoTipo {@link #ENTRADA} ou {@link #SAIDA}
     * @param novaDataHora data e hora da passagem
     * @throws IllegalArgumentException se o ID for inválido, faltar cliente ou data, ou o tipo não existir
     */
    public RegistroCatraca(int novoId, Cliente novoCliente, String novoTipo, LocalDateTime novaDataHora) { // construtor com parâmetros
        if (novoId <= 0) { // ID precisa ser maior que zero
            throw new IllegalArgumentException("O ID deve ser maior que zero."); // erro de argumento
        } // fim do if
        if (novoCliente == null || novaDataHora == null) { // precisa do cliente e da data
            throw new IllegalArgumentException("Informe o cliente e a data e hora."); // erro de argumento
        } // fim do if
        this.id = novoId; // guarda o ID
        this.cliente = novoCliente; // guarda o cliente
        this.tipo = exigirTipo(novoTipo); // só ENTRADA ou SAIDA
        this.dataHora = novaDataHora; // guarda a data e hora
    } // fim do construtor

    /**
     * Devolve o identificador do registro.
     *
     * @return ID do registro
     */
    @Override // vem da interface Identificavel
    public int getId() { // devolve o ID
        return id; // ID do registro
    } // fim de getId

    /**
     * Devolve o cliente que passou pela catraca.
     *
     * @return o cliente
     */
    public Cliente getCliente() { // devolve o cliente
        return cliente; // quem passou
    } // fim de getCliente

    /**
     * Devolve o tipo do registro.
     *
     * @return {@link #ENTRADA} ou {@link #SAIDA}
     */
    public String getTipo() { // devolve o tipo
        return tipo; // ENTRADA ou SAIDA
    } // fim de getTipo

    /**
     * Devolve quando o cliente passou.
     *
     * @return data e hora da passagem
     */
    public LocalDateTime getDataHora() { // devolve a data e hora
        return dataHora; // quando passou
    } // fim de getDataHora

    /**
     * Confere se o tipo é ENTRADA ou SAIDA.
     *
     * @param valor tipo a conferir
     * @return o próprio tipo, se for válido
     * @throws IllegalArgumentException se o tipo não for ENTRADA nem SAIDA
     */
    private static String exigirTipo(String valor) { // confere o tipo
        if (!ENTRADA.equals(valor) && !SAIDA.equals(valor)) { // só os dois tipos
            throw new IllegalArgumentException("Tipo de registro inválido: " + valor); // erro de argumento
        } // fim do if
        return valor; // devolve o tipo conferido
    } // fim de exigirTipo

    /**
     * Resume o registro em uma linha.
     *
     * @return tipo, nome do cliente e hora da passagem
     */
    @Override // sobrescreve o toString de Object
    public String toString() { // resumo em uma linha
        return "Registro " + id + " - " + tipo + " - " + cliente.getNome() + " - " + dataHora; // tipo, cliente e hora
    } // fim do toString
}