/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model;

/**
 * Representa um colaborador da Academia Milho Verde.
 *
 * <p>Herda de {@link Funcionario} (superclasse direta) e de
 * {@link Pessoa} (superclasse indireta) todos os dados e métodos.
 * Não tem atributos próprios: a diferença para o administrador está
 * em {@link #podeAcessarFinanceiro()}, que aqui responde
 * {@code false}.</p>
 *
 * @author Aline Ferreira
 */

public class Colaborador extends Funcionario { // Colaborador É UM Funcionario: herança

    public Colaborador() { // construtor sem parâmetros
        super(); // chama o construtor sem parâmetros do Funcionario
    } // fim do construtor sem parâmetros

    public Colaborador(int novoId, String novoNome, String novoEndereco,
            String novoTelefone, String novoEmail, String novaSenha) { // construtor com parâmetros
        super(novoId, novoNome, novoEndereco, novoTelefone, novoEmail, novaSenha); // repassa tudo ao Funcionario (item 4)
    } // fim do construtor com parâmetros

    @Override // redefine o método abstrato do Funcionario
    public boolean podeAcessarFinanceiro() { // polimorfismo: cada subclasse responde do seu jeito
        return false; // colaborador não acessa financeiro nem funcionários
    } // fim de podeAcessarFinanceiro

    @Override // sobrescreve o toString herdado
    public String toString() { // item 3
        return "Colaborador " + super.toString(); // acrescenta o perfil ao texto da Pessoa
    } // fim de toString
} // fim da classe Colaborador