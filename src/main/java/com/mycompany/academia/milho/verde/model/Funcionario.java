/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model;

/**
 * Representa um funcionário da Academia Milho Verde.
 *
 * <p>Classe abstrata: herda de {@link Pessoa} o identificador, o nome,
 * o e-mail e a situação do cadastro, e acrescenta a senha de acesso.
 * Não existe um funcionário "genérico"; os objetos são sempre
 * {@code Colaborador} ou {@code Administrador}.</p>
 *
 * <p>A senha não tem getter público e não aparece no {@code toString}: ela só
 * é conferida por {@link #verificarSenha(String)} e trocada por
 * {@link #alterarSenha(String, String)}.</p>
 *
 * @author Aline Ferreira
 */

public abstract class Funcionario extends Pessoa { // abstrata: ou é Colaborador ou é Administrador

    private String senha; // senha de acesso (sem getter público nem setter)

    public Funcionario() { // construtor sem parâmetros
        super(); // chama o construtor sem parâmetros da Pessoa
        this.senha = ""; // ainda sem senha
    } // fim do construtor sem parâmetros

    public Funcionario(int novoId, String novoNome, String novoEndereco,
            String novoTelefone, String novoEmail, String novaSenha) { // construtor com parâmetros
        super(novoId, novoNome, novoEndereco, novoTelefone, novoEmail); // primeira linha: dados da Pessoa (item 4)
        this.senha = Validacao.exigirTexto(novaSenha, "senha"); // senha obrigatória
    } // fim do construtor com parâmetros

    public boolean verificarSenha(String senhaInformada) { // usado no login
        return senha.equals(senhaInformada); // true se a senha digitada for igual à guardada
    } // fim de verificarSenha

    public boolean alterarSenha(String senhaAtual, String novaSenha) { // troca a senha com segurança
        if (!verificarSenha(senhaAtual)) { // a senha atual está errada?
            return false; // não troca nada
        } // fim do if
        this.senha = Validacao.exigirTexto(novaSenha, "nova senha"); // valida e guarda a nova senha
        return true; // troca feita
    } // fim de alterarSenha

    String getSenha() { // sem modificador = acesso de pacote: só o RepositorioJson usa, para gravar
        return senha; // devolve a senha
    } // fim de getSenha

    public abstract boolean podeAcessarFinanceiro(); // método abstrato, sem corpo: cada subclasse redefine
} // fim da classe Funcionario