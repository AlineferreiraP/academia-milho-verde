/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model;

/**
 * Serviços de validação usados pelas classes do Model.
 * É final e tem construtor privado: não se cria objeto, só se chamam os métodos estáticos.
 *
 * @author Aline Ferreira
 */
public final class Validacao { // final: ninguém pode herdar desta classe 

    /** Construtor privado: impede criar objeto com new. */
    private Validacao() { // construtor privado: impede criar objeto com new Validacao()
    } // vazio, porque a classe não guarda dados, só oferece um serviço

    /**
     * Exige um texto preenchido.
     *
     * @param valor texto recebido
     * @param campo nome do campo, usado na mensagem de erro
     * @return o texto sem os espaços das pontas
     * @throws IllegalArgumentException se for nulo, vazio ou só espaços
     */
    public static String exigirTexto(String valor, String campo) { // static: chamado pelo nome da classe (Aula 7)
        if (valor == null || valor.isBlank()) { // sem texto, vazio ou só com espaços?
            throw new IllegalArgumentException("Informe o " + campo + "."); // lança o erro e para aqui (Aula 10)
        } // fim do if
        return valor.trim(); // texto válido: devolve sem os espaços das pontas
    } // fim de exigirTexto
} // fim da classe Validacao