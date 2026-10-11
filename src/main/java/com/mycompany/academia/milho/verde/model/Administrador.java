/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model;

/**
 * Representa um administrador da Academia Milho Verde.
 *
 * <p>Herda de {@link Funcionario} (superclasse direta) e de
 * {@link Pessoa} (superclasse indireta) todos os dados e métodos.
 * Não tem atributos próprios: a diferença para o colaborador está
 * em {@link #podeAcessarFinanceiro()}, que aqui responde
 * {@code true}.</p>
 *
 * @author Aline Ferreira Pena
 */
public class Administrador extends Funcionario {

    /**
     * Construtor padrão: cria um administrador em branco.
     *
     * <p>{@code super()} chama o construtor padrão do
     * {@link Funcionario}.</p>
     */
    public Administrador() {
        super();
    }

    /**
     * Construtor com parâmetros: cria um administrador ativo.
     *
     * <p>{@code super(...)} repassa os dados ao {@link Funcionario},
     * que valida o ID, o nome, o e-mail e a senha.</p>
     *
     * @param novoId identificador positivo do administrador
     * @param novoNome nome completo
     * @param novoEmail e-mail usado no login
     * @param novaSenha senha de acesso
     * @throws IllegalArgumentException se o identificador não for
     *         positivo ou se algum dado estiver nulo, vazio ou
     *         contiver apenas espaços
     */
    public Administrador(int novoId, String novoNome, String novoEmail,
                         String novaSenha) {
        super(novoId, novoNome, novoEmail, novaSenha);
    }

    /**
     * Informa que o administrador acessa o financeiro.
     *
     * <p>Sobrescreve o método abstrato de {@link Funcionario}
     * (polimorfismo): só o administrador gerencia as despesas e gera
     * o balanço mensal.</p>
     *
     * @return sempre {@code true}
     */
    @Override
    public boolean podeAcessarFinanceiro() {
        return true;    // administrador acessa despesas e balanço
    }

    /**
     * Retorna uma descrição do administrador, sem a senha.
     *
     * @return texto com o perfil e os dados do funcionário
     */
    @Override
    public String toString() {
        return "Administrador[" + super.toString() + "]";
    }
}