/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.dao;

// Erro de acesso aos dados (por exemplo, falha ao ler ou gravar o arquivo).
// É "unchecked": quem usa o DAO não precisa declarar throws.
public class DaoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DaoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}