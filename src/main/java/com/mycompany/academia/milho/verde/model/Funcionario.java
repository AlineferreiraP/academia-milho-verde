/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model;

/**
 * Representa um funcionário da Academia Milho Verde.
 *
 * <p>Classe abstrata: herda de {@link Pessoa} o identificador, o nome,
 * o endereço, o telefone e o e-mail, e acrescenta a senha de acesso.
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

    /** Senha de acesso (sem getter público nem setter). */
    private String senha; // senha de acesso (sem getter público nem setter)

    /** Cria um funcionário vazio, ainda sem senha. */
    public Funcionario() { // construtor sem parâmetros
        super(); // chama o construtor sem parâmetros da Pessoa
        this.senha = ""; // ainda sem senha
    } // fim do construtor sem parâmetros

    /**
     * Cria um funcionário validando todos os dados.
     *
     * @param novoId ID do funcionário (maior que zero)
     * @param novoNome nome completo (não pode ser vazio)
     * @param novoEndereco endereço (não pode ser vazio)
     * @param novoTelefone telefone (não pode ser vazio)
     * @param novoEmail e-mail (não pode ser vazio)
     * @param novaSenha senha de acesso (não pode ser vazia)
     * @throws IllegalArgumentException se algum dado for inválido
     */
    public Funcionario(int novoId, String novoNome, String novoEndereco,
            String novoTelefone, String novoEmail, String novaSenha) { // construtor com parâmetros
        super(novoId, novoNome, novoEndereco, novoTelefone, novoEmail); // primeira linha: dados da Pessoa (item 4)
        this.senha = Validacao.exigirTexto(novaSenha, "senha"); // senha obrigatória
    } // fim do construtor com parâmetros

    /**
     * Confere a senha digitada (usado no login).
     *
     * @param senhaInformada senha digitada
     * @return {@code true} se for igual à senha guardada
     */
    public boolean verificarSenha(String senhaInformada) { // usado no login
        return senha.equals(senhaInformada); // true se a senha digitada for igual à guardada
    } // fim de verificarSenha

    /**
     * Troca a senha, desde que a senha atual esteja correta.
     *
     * @param senhaAtual senha atual, para conferir
     * @param novaSenha nova senha (não pode ser vazia)
     * @return {@code true} se trocou; {@code false} se a senha atual estava errada
     * @throws IllegalArgumentException se a nova senha for vazia
     */
    public boolean alterarSenha(String senhaAtual, String novaSenha) { // troca a senha com segurança
        if (!verificarSenha(senhaAtual)) { // a senha atual está errada?
            return false; // não troca nada
        } // fim do if
        this.senha = Validacao.exigirTexto(novaSenha, "nova senha"); // valida e guarda a nova senha
        return true; // troca feita
    } // fim de alterarSenha

    /**
     * Devolve a senha. Tem acesso de pacote: só o RepositorioJson usa, para gravar.
     *
     * @return senha guardada
     */
    String getSenha() { // sem modificador = acesso de pacote: só o RepositorioJson usa, para gravar
        return senha; // devolve a senha
    } // fim de getSenha

    /**
     * Diz se o funcionário pode acessar o financeiro; cada subclasse responde do seu jeito.
     *
     * @return {@code true} se pode
     */
    public abstract boolean podeAcessarFinanceiro(); // método abstrato, sem corpo: cada subclasse redefine
} // fim da classe Funcionario