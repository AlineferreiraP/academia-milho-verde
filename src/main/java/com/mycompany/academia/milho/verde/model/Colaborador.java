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
 * @author Aline Ferreira Pena
 */
public class Colaborador extends Funcionario {

    /**
     * Construtor padrão: cria um colaborador em branco.
     *
     * <p>{@code super()} chama o construtor padrão do
     * {@link Funcionario}.</p>
     */
    public Colaborador() {
        super();
    }

    /**
     * Construtor com parâmetros: cria um colaborador ativo.
     *
     * <p>{@code super(...)} repassa os dados ao {@link Funcionario},
     * que valida o ID, o nome, o e-mail e a senha.</p>
     *
     * @param novoId identificador positivo do colaborador
     * @param novoNome nome completo
     * @param novoEmail e-mail usado no login
     * @param novaSenha senha de acesso
     * @throws IllegalArgumentException se o identificador não for
     *         positivo ou se algum dado estiver nulo, vazio ou
     *         contiver apenas espaços
     */
    public Colaborador(int novoId, String novoNome, String novoEmail,
                       String novaSenha) {
        super(novoId, novoNome, novoEmail, novaSenha);
    }

    /**
     * Informa que o colaborador não acessa o financeiro.
     *
     * <p>Sobrescreve o método abstrato de {@link Funcionario}
     * (polimorfismo): despesas e balanço mensal são exclusivos do
     * administrador.</p>
     *
     * @return sempre {@code false}
     */
    @Override
    public boolean podeAcessarFinanceiro() {
        return false;   // colaborador não acessa despesas nem balanço
    }

    /**
     * Retorna uma descrição do colaborador, sem a senha.
     *
     * @return texto com o perfil e os dados do funcionário
     */
    @Override
    public String toString() {
        return "Colaborador[" + super.toString() + "]";
    }
}