/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Gerencia os funcionários: colaboradores e administradores.
 *
 * <p>Usa o polimorfismo: todos ficam na mesma lista de
 * {@link Funcionario}, e cada objeto é um {@link Colaborador} ou um
 * {@link Administrador}.</p>
 *
 * @author Aline Ferreira
 */
public class GestaoFuncionarios {

    /** Funcionários cadastrados, ativos e desativados. */
    private List<Funcionario> funcionarios;

    /** Identificador do próximo funcionário. */
    private int proximoId;

    /**
     * Construtor padrão: cria uma gestão sem funcionários.
     */
    public GestaoFuncionarios() {
        this.funcionarios = new ArrayList<>();
        this.proximoId = 1;
    }

    /**
     * Cadastra um colaborador.
     *
     * @param nome nome completo
     * @param email e-mail, único entre os funcionários
     * @param senha senha de acesso
     * @return o colaborador cadastrado
     * @throws IllegalArgumentException se algum dado for inválido ou o
     *         e-mail já estiver em uso
     */
    public Colaborador cadastrarColaborador(String nome, String email,
                                            String senha) {
        exigirEmailLivre(email, 0);
        Colaborador colaborador = new Colaborador(gerarId(), nome, email, senha);
        funcionarios.add(colaborador);
        return colaborador;
    }

    /**
     * Cadastra um administrador.
     *
     * @param nome nome completo
     * @param email e-mail, único entre os funcionários
     * @param senha senha de acesso
     * @return o administrador cadastrado
     * @throws IllegalArgumentException se algum dado for inválido ou o
     *         e-mail já estiver em uso
     */
    public Administrador cadastrarAdministrador(String nome, String email,
                                                String senha) {
        exigirEmailLivre(email, 0);
        Administrador administrador = new Administrador(gerarId(), nome,
                email, senha);
        funcionarios.add(administrador);
        return administrador;
    }

    /**
     * Altera nome e e-mail de um funcionário.
     *
     * @param id identificador do funcionário
     * @param nome novo nome
     * @param email novo e-mail
     * @throws IllegalArgumentException se o funcionário não existir, se
     *         algum dado for inválido ou se o e-mail estiver em uso
     */
    public void editarFuncionario(int id, String nome, String email) {
        Funcionario funcionario = exigirFuncionario(id);
        exigirEmailLivre(email, id);
        funcionario.editarDados(nome, email);
    }

    /**
     * Remove um funcionário por exclusão lógica (desativa o cadastro).
     *
     * @param id identificador do funcionário
     * @throws IllegalArgumentException se o funcionário não existir
     * @throws IllegalStateException se for o último administrador ativo
     */
    public void removerFuncionario(int id) {
        Funcionario funcionario = exigirFuncionario(id);
        if (funcionario instanceof Administrador && funcionario.isAtivo()
                && contarAdministradoresAtivos() <= 1) {
            throw new IllegalStateException(
                    "O último administrador ativo não pode ser removido.");
        }
        funcionario.desativar();
    }

    /**
     * Altera a senha de um administrador.
     *
     * @param id identificador do administrador
     * @param senhaAtual senha atual
     * @param novaSenha nova senha
     * @return {@code true} se a senha foi alterada; {@code false} se a
     *         senha atual estava incorreta
     * @throws IllegalArgumentException se o ID não for de um administrador
     */
    public boolean alterarSenhaAdministrador(int id, String senhaAtual,
                                             String novaSenha) {
        Funcionario funcionario = exigirFuncionario(id);
        if (!(funcionario instanceof Administrador)) {
            throw new IllegalArgumentException(
                    "O funcionário " + id + " não é administrador.");
        }
        return funcionario.alterarSenha(senhaAtual, novaSenha);
    }

    /**
     * Procura um funcionário ativo pelo e-mail. É usado pelo login.
     *
     * @param email e-mail procurado (sem diferenciar maiúsculas)
     * @return o funcionário ativo, ou {@code null} se não houver
     */
    public Funcionario buscarPorEmail(String email) {
        if (email == null) {
            return null;
        }
        for (Funcionario funcionario : funcionarios) {
            if (funcionario.isAtivo()
                    && funcionario.getEmail().equalsIgnoreCase(email.trim())) {
                return funcionario;
            }
        }
        return null;
    }

    /**
     * Lista os funcionários ativos.
     *
     * @return lista nova com os ativos
     */
    public List<Funcionario> listarFuncionarios() {
        List<Funcionario> ativos = new ArrayList<>();
        for (Funcionario funcionario : funcionarios) {
            if (funcionario.isAtivo()) {
                ativos.add(funcionario);
            }
        }
        return ativos;
    }

    /**
     * Retorna todos os funcionários, ativos e desativados.
     *
     * <p>Acesso de pacote: usado pelo {@link RepositorioJson}.</p>
     *
     * @return cópia da lista de funcionários
     */
    List<Funcionario> getFuncionarios() {
        return new ArrayList<>(funcionarios);
    }

    /**
     * Substitui os funcionários pelos lidos do arquivo e ajusta o
     * contador de IDs.
     *
     * @param lidos funcionários lidos do arquivo
     */
    void carregar(List<Funcionario> lidos) {
        funcionarios = new ArrayList<>(lidos);
        proximoId = 1;
        for (Funcionario funcionario : funcionarios) {
            if (funcionario.getId() >= proximoId) {
                proximoId = funcionario.getId() + 1;
            }
        }
    }

    /**
     * Procura um funcionário e falha se não existir.
     *
     * @param id identificador procurado
     * @return o funcionário
     * @throws IllegalArgumentException se o ID não existir
     */
    private Funcionario exigirFuncionario(int id) {
        for (Funcionario funcionario : funcionarios) {
            if (funcionario.getId() == id) {
                return funcionario;
            }
        }
        throw new IllegalArgumentException(
                "Funcionário " + id + " não encontrado.");
    }

    /**
     * Confere se nenhum outro funcionário usa o e-mail.
     *
     * @param email e-mail a conferir
     * @param idIgnorado funcionário que pode ficar com o próprio e-mail
     *        (0 para não ignorar ninguém)
     * @throws IllegalArgumentException se o e-mail já estiver em uso
     */
    private void exigirEmailLivre(String email, int idIgnorado) {
        if (email == null) {
            return;
        }
        for (Funcionario funcionario : funcionarios) {
            if (funcionario.getId() != idIgnorado
                    && funcionario.getEmail().equalsIgnoreCase(email.trim())) {
                throw new IllegalArgumentException(
                        "Já existe um funcionário com esse e-mail.");
            }
        }
    }

    /**
     * Conta os administradores ativos.
     *
     * @return o número de administradores ativos
     */
    private int contarAdministradoresAtivos() {
        int total = 0;
        for (Funcionario funcionario : funcionarios) {
            if (funcionario instanceof Administrador && funcionario.isAtivo()) {
                total++;
            }
        }
        return total;
    }

    /**
     * Devolve o próximo identificador e avança o contador.
     *
     * @return o identificador a usar
     */
    private int gerarId() {
        return proximoId++;
    }

    /**
     * Retorna um resumo da gestão.
     *
     * @return texto com o número de funcionários
     */
    @Override
    public String toString() {
        return "GestaoFuncionarios[" + funcionarios.size() + " funcionário(s)]";
    }
}