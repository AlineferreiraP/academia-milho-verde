/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model; // pacote do Model

import java.util.Comparator; // interface do Java para comparar dois objetos

public class ComparadorCliente implements Comparator<Cliente> { // compara dois clientes pelo CPF

    public ComparadorCliente() { // construtor sem parâmetros
    } // nada a guardar: o critério é sempre o CPF

    @Override // implementa o único método da interface Comparator
    public int compare(Cliente a, Cliente b) { // zero: mesmo CPF; diferente de zero: CPFs diferentes
        return a.getCpfPseudo().compareTo(b.getCpfPseudo()); // compara os CPFs pseudonimizados
    } // fim de compare

    @Override // sobrescreve o toString herdado de Object
    public String toString() { // texto que representa o comparador
        return "ComparadorCliente por CPF"; // mostra o critério
    } // fim de toString
} // fim da classe ComparadorCliente