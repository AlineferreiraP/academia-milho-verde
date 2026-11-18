/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mycompany.academia.milho.verde.model; // pacote do Model

import java.util.Comparator; // interface do Java para comparar dois objetos

/**
 * Compara dois agendamentos pelo dia e hora da primeira aula.
 *
 * <p>O resultado de {@code compare(a, b)} é negativo se {@code a} começa antes,
 * zero se os dois começam no mesmo dia e hora, e positivo se {@code a} começa depois.</p>
 */
public class ComparadorAgendamento implements Comparator<Agendamento> { // compara dois agendamentos pelo dia e hora da primeira aula

    /**
     * Cria o comparador. Não há atributos: o critério é sempre o dia e a hora da primeira aula.
     */
    public ComparadorAgendamento() { // construtor sem parâmetros
        // não há atributos: o critério é sempre o dia e a hora da primeira aula
    } // fim do construtor

    /**
     * Compara dois agendamentos pelo dia e hora da primeira aula.
     *
     * @param a primeiro agendamento
     * @param b segundo agendamento
     * @return negativo, zero ou positivo, conforme {@code a} comece antes, junto ou depois de {@code b}
     */
    @Override // implementa o método da interface Comparator
    public int compare(Agendamento a, Agendamento b) { // negativo: a começa antes; zero: mesmo dia e hora; positivo: a começa depois
        return a.getPrimeiraDataHora().compareTo(b.getPrimeiraDataHora()); // o LocalDateTime compara o dia e, no mesmo dia, a hora
    } // fim de compare

    /**
     * Descreve o comparador.
     *
     * @return texto com o critério usado
     */
    @Override // sobrescreve o toString herdado de Object
    public String toString() { // texto que representa o comparador
        return "ComparadorAgendamento (compara pelo dia e hora da primeira aula)"; // descreve o critério
    } // fim de toString
} // fim da classe ComparadorAgendamento