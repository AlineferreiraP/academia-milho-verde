/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Representa uma passagem de um cliente pela catraca (entrada ou saída).
 *
 * <p>Um registro é um fato que já aconteceu: não tem setters.</p>
 *
 * @author Aline Ferreira
 */
public class RegistroCatraca {

    /** Tipo de registro: entrada na academia. */
    public static final String ENTRADA = "ENTRADA";

    /** Tipo de registro: saída da academia. */
    public static final String SAIDA = "SAIDA";

    /** Identificador único (0 no registro em branco). */
    private int id;

    /** Cliente que passou pela catraca. */
    private Cliente cliente;

    /** {@link #ENTRADA} ou {@link #SAIDA}. */
    private String tipo;

    /** Data e hora da passagem. */
    private LocalDateTime dataHora;

    /**
     * Construtor padrão: cria um registro em branco.
     */
    public RegistroCatraca() {
        this.id = 0;
        this.cliente = null;
        this.tipo = "";
        this.dataHora = null;
    }

    /**
     * Construtor com parâmetros.
     *
     * @param novoId identificador positivo
     * @param novoCliente cliente que passou
     * @param novoTipo {@link #ENTRADA} ou {@link #SAIDA}
     * @param novaDataHora data e hora da passagem
     * @throws IllegalArgumentException se algum dado for inválido
     */
    public RegistroCatraca(int novoId, Cliente novoCliente, String novoTipo,
                           LocalDateTime novaDataHora) {
        if (novoId <= 0) {
            throw new IllegalArgumentException(
                    "O identificador deve ser positivo.");
        }
        if (novoCliente == null) {
            throw new IllegalArgumentException("O cliente é obrigatório.");
        }
        if (novaDataHora == null) {
            throw new IllegalArgumentException(
                    "A data e a hora são obrigatórias.");
        }
        this.id = novoId;
        this.cliente = novoCliente;
        this.tipo = exigirTipo(novoTipo);
        this.dataHora = novaDataHora;
    }

    /**
     * Retorna o identificador.
     *
     * @return o ID
     */
    public int getId() {
        return id;
    }

    /**
     * Retorna o cliente.
     *
     * @return o cliente
     */
    public Cliente getCliente() {
        return cliente;
    }

    /**
     * Retorna o tipo do registro.
     *
     * @return {@link #ENTRADA} ou {@link #SAIDA}
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Retorna a data e a hora.
     *
     * @return a data e a hora da passagem
     */
    public LocalDateTime getDataHora() {
        return dataHora;
    }

    /**
     * Confere se o tipo é aceito.
     *
     * @param valor tipo informado
     * @return o próprio {@code valor}, quando válido
     * @throws IllegalArgumentException se não for ENTRADA nem SAIDA
     */
    private static String exigirTipo(String valor) {
        if (ENTRADA.equals(valor) || SAIDA.equals(valor)) {
            return valor;
        }
        throw new IllegalArgumentException("O tipo deve ser ENTRADA ou SAIDA.");
    }

    /**
     * Retorna uma descrição legível do registro.
     *
     * @return texto com ID, cliente, tipo e horário
     */
    @Override
    public String toString() {
        String nome = (cliente == null) ? "(sem cliente)" : cliente.getNome();
        String quando = (dataHora == null) ? "-"
                : dataHora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        return "Registro[id=" + id + ", " + tipo + ", cliente=" + nome
                + ", " + quando + "]";
    }
}
