/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.dao;

/**
 *
 * @author aline
 */
public class DaoException extends RuntimeException { // não verificada: não exige throws

    private static final long serialVersionUID = 1L; // número de versão exigido de toda exceção (é Serializable)

    public DaoException(String mensagem, Throwable causa) { // recebe a mensagem e o erro original
        super(mensagem, causa); // exceção encadeada: guarda a causa para não perder o erro real
    } // fim do construtor
} // fim da classe DaoException