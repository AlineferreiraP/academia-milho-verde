/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model; // pacote do Model

import java.time.LocalDateTime; // data e hora da aula

/**
 * Uma aula de uma hora, numa sala, num dia e horário, dada por um instrutor.
 *
 * @author Aline Ferreira
 */
public class Aula implements Identificavel { // uma aula de uma hora, numa sala, num dia e horário

    /** Identificador da aula; não muda. */
    private final int id; // identificador da aula; final: não muda
    /** Sala onde a aula acontece (associação com {@link Sala}). */
    private final Sala sala; // sala onde a aula acontece (associação com Sala)
    /** Dia e hora de início da aula. */
    private final LocalDateTime dataHora; // dia e hora de início da aula
    /** Quem dá a aula. */
    private final String nomeInstrutor; // quem dá a aula

    /** Cria uma aula vazia (sem ID, sala, data ou instrutor). */
    public Aula() { // construtor sem parâmetros
        this.id = 0; // ainda sem ID
        this.sala = null; // ainda sem sala
        this.dataHora = null; // ainda sem data
        this.nomeInstrutor = ""; // ainda sem instrutor
    } // fim do construtor sem parâmetros

    /**
     * Cria uma aula validando os dados.
     *
     * @param novoId ID da aula (maior que zero)
     * @param novaSala sala da aula (não pode ser nula)
     * @param novaDataHora dia e hora de início (não pode ser nula)
     * @param novoInstrutor nome do instrutor (não pode ser vazio)
     * @throws IllegalArgumentException se algum dado for inválido
     */
    public Aula(int novoId, Sala novaSala, LocalDateTime novaDataHora, String novoInstrutor) { // construtor com parâmetros
        if (novoId <= 0) { // o ID precisa ser positivo
            throw new IllegalArgumentException("O ID da aula deve ser maior que zero."); // erro de argumento
        } // fim do if
        if (novaSala == null) { // toda aula acontece numa sala
            throw new IllegalArgumentException("Informe a sala da aula."); // erro de argumento
        } // fim do if
        if (novaDataHora == null) { // toda aula tem dia e hora
            throw new IllegalArgumentException("Informe a data e a hora da aula."); // erro de argumento
        } // fim do if
        this.id = novoId; // guarda o ID
        this.sala = novaSala; // guarda a sala
        this.dataHora = novaDataHora; // guarda a data e a hora
        this.nomeInstrutor = Validacao.exigirTexto(novoInstrutor, "instrutor"); // instrutor obrigatório
    } // fim do construtor com parâmetros

    /**
     * Devolve: ID da aula.
     *
     * @return ID da aula
     */
    @Override // implementa o método da interface Identificavel
    public int getId() { // getter do ID
        return id; // devolve o ID
    } // fim de getId

    /**
     * Devolve: sala da aula.
     *
     * @return sala da aula
     */
    public Sala getSala() { // getter da sala
        return sala; // devolve a sala
    } // fim de getSala

    /**
     * Devolve a modalidade, que vem da sala (delegação).
     *
     * @return modalidade da aula
     */
    public String getModalidade() { // a modalidade vem da sala
        return sala.getModalidade(); // pergunta à sala (delegação)
    } // fim de getModalidade

    /**
     * Devolve: dia e hora de início.
     *
     * @return dia e hora de início
     */
    public LocalDateTime getDataHora() { // getter da data e hora
        return dataHora; // devolve a data e a hora
    } // fim de getDataHora

    /**
     * Devolve: nome do instrutor.
     *
     * @return nome do instrutor
     */
    public String getNomeInstrutor() { // getter do instrutor
        return nomeInstrutor; // devolve o nome do instrutor
    } // fim de getNomeInstrutor

    /**
     * Resume a aula em uma linha.
     *
     * @return ID, modalidade, sala, data/hora e instrutor
     */
    @Override // sobrescreve o toString herdado de Object
    public String toString() { // texto que representa a aula
        return "Aula " + id + " | " + sala.getModalidade() + " (sala " + sala.getCodigo() + ") | "
                + dataHora + " | " + nomeInstrutor; // ID, modalidade, sala, data/hora e instrutor
    } // fim de toString
} // fim da classe Aula