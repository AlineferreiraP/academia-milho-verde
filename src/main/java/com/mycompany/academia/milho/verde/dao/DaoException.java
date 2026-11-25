/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.dao;

/**
 * Erro de leitura ou gravação nos DAOs (por exemplo, arquivo ilegível).
 * É não verificada: não obriga a usar throws. Guarda o erro original como causa.
 *
 * @author Aline Ferreira
 */
public class DaoException extends RuntimeException { // não verificada: não exige throws

    /** Número de versão exigido de toda exceção (ela é Serializable). */
    private static final long serialVersionUID = 1L; // número de versão exigido de toda exceção (é Serializable)

    /**
     * Cria o erro guardando a causa original.
     *
     * @param mensagem texto que explica o problema
     * @param causa erro original que aconteceu
     */
    public DaoException(String mensagem, Throwable causa) { // recebe a mensagem e o erro original
        super(mensagem, causa); // exceção encadeada: guarda a causa para não perder o erro real
    } // fim do construtor
} // fim da classe DaoException DaoException