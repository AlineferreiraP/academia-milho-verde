/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model; // pacote do Model

import java.time.LocalDateTime; // data e hora da aula

public class Aula implements Identificavel { // uma aula de uma hora, numa sala, num dia e horário

    private final int id; // identificador da aula; final: não muda
    private final Sala sala; // sala onde a aula acontece (associação com Sala)
    private final LocalDateTime dataHora; // dia e hora de início da aula
    private final String nomeInstrutor; // quem dá a aula

    public Aula() { // construtor sem parâmetros
        this.id = 0; // ainda sem ID
        this.sala = null; // ainda sem sala
        this.dataHora = null; // ainda sem data
        this.nomeInstrutor = ""; // ainda sem instrutor
    } // fim do construtor sem parâmetros

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

    @Override // implementa o método da interface Identificavel
    public int getId() { // getter do ID
        return id; // devolve o ID
    } // fim de getId

    public Sala getSala() { // getter da sala
        return sala; // devolve a sala
    } // fim de getSala

    public String getModalidade() { // a modalidade vem da sala
        return sala.getModalidade(); // pergunta à sala (delegação)
    } // fim de getModalidade

    public LocalDateTime getDataHora() { // getter da data e hora
        return dataHora; // devolve a data e a hora
    } // fim de getDataHora

    public String getNomeInstrutor() { // getter do instrutor
        return nomeInstrutor; // devolve o nome do instrutor
    } // fim de getNomeInstrutor

    @Override // sobrescreve o toString herdado de Object
    public String toString() { // texto que representa a aula
        return "Aula " + id + " | " + sala.getModalidade() + " (sala " + sala.getCodigo() + ") | "
                + dataHora + " | " + nomeInstrutor; // ID, modalidade, sala, data/hora e instrutor
    } // fim de toString
} // fim da classe Aula