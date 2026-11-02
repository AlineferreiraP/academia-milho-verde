/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Representa uma aula oferecida em uma sala, em um dia e horário.
 *
 * <p>O tipo da aula é a modalidade da sala. O instrutor é registrado
 * pelo nome, como pede o enunciado. Cada aula ocupa uma célula da
 * {@link AgendaSemanal} da sua sala.</p>
 *
 * @author Aline Ferreira
 */
public class Aula {

    /** Identificador único (0 enquanto não definido). */
    private int id;

    /** Sala onde a aula acontece. */
    private Sala sala;

    /** Data e hora de início da aula. */
    private LocalDateTime dataHora;

    /** Nome do instrutor. */
    private String nomeInstrutor;

    /**
     * Construtor padrão: cria uma aula em branco.
     */
    public Aula() {
        this.id = 0;
        this.sala = null;
        this.dataHora = null;
        this.nomeInstrutor = "";
    }

    /**
     * Construtor com parâmetros.
     *
     * @param novoId identificador positivo
     * @param novaSala sala da aula
     * @param novaDataHora data e hora de início
     * @param novoInstrutor nome do instrutor
     * @throws IllegalArgumentException se algum dado for inválido
     */
    public Aula(int novoId, Sala novaSala, LocalDateTime novaDataHora,
                String novoInstrutor) {
        if (novoId <= 0) {
            throw new IllegalArgumentException(
                    "O identificador deve ser positivo.");
        }
        if (novaSala == null) {
            throw new IllegalArgumentException("A sala é obrigatória.");
        }
        if (novaDataHora == null) {
            throw new IllegalArgumentException(
                    "A data e a hora da aula são obrigatórias.");
        }
        if (novoInstrutor == null || novoInstrutor.isBlank()) {
            throw new IllegalArgumentException(
                    "O nome do instrutor deve ser preenchido.");
        }
        this.id = novoId;
        this.sala = novaSala;
        this.dataHora = novaDataHora;
        this.nomeInstrutor = novoInstrutor;
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
     * Retorna a sala da aula.
     *
     * @return a sala
     */
    public Sala getSala() {
        return sala;
    }

    /**
     * Retorna a modalidade, que é a da sala.
     *
     * @return a modalidade, ou texto vazio se a aula não tem sala
     */
    public String getModalidade() {
        return (sala == null) ? "" : sala.getModalidade();
    }

    /**
     * Retorna a data e a hora de início.
     *
     * @return a data e a hora
     */
    public LocalDateTime getDataHora() {
        return dataHora;
    }

    /**
     * Retorna o nome do instrutor.
     *
     * @return o nome
     */
    public String getNomeInstrutor() {
        return nomeInstrutor;
    }

    /**
     * Define o identificador de uma aula criada pelo construtor padrão.
     *
     * <p>Tem acesso de pacote e funciona uma única vez.</p>
     *
     * @param novoId identificador positivo
     * @throws IllegalArgumentException se não for positivo
     * @throws IllegalStateException se a aula já tiver identificador
     */
    void definirId(int novoId) {
        if (this.id != 0) {
            throw new IllegalStateException(
                    "O identificador já foi definido.");
        }
        if (novoId <= 0) {
            throw new IllegalArgumentException(
                    "O identificador deve ser positivo.");
        }
        this.id = novoId;
    }

    /**
     * Retorna uma descrição legível da aula.
     *
     * @return texto com ID, modalidade, sala, data e instrutor
     */
    @Override
    public String toString() {
        String quando = (dataHora == null) ? "-"
                : dataHora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        String codigoSala = (sala == null) ? "-" : String.valueOf(sala.getCodigo());
        return "Aula[id=" + id + ", " + getModalidade()
                + ", sala=" + codigoSala + ", " + quando
                + ", instrutor=" + nomeInstrutor + "]";
    }
}
