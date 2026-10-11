/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model;

/**
 * Representa uma pessoa cadastrada na Academia Milho Verde.
 *
 * <p>Classe abstrata: guarda o que cliente e funcionário têm em
 * comum (identificador, nome, e-mail e situação do cadastro). Não
 * existe uma "pessoa" solta no sistema; os objetos são sempre
 * {@link Cliente} ou {@code Funcionario}.</p>
 *
 * @author Aline Ferreira Pena
 */
public abstract class Pessoa {

    /**
     * Valor do identificador enquanto a pessoa ainda não recebeu
     * um ID do sistema.
     */
    protected static final int SEM_ID = 0;

    /**
     * Identificador único, gerado pelo sistema.
     *
     * <p>Não é {@code final} porque o construtor padrão cria a
     * pessoa sem ID. Mesmo assim, o valor só pode ser definido uma
     * vez: pelo construtor com parâmetros ou por
     * {@link #definirId(int)}.</p>
     */
    private int id;

    /** Nome completo. */
    private String nome;

    /** Endereço de e-mail. */
    private String email;

    /**
     * Indica se o cadastro está ativo.
     *
     * <p>Todo cadastro começa ativo. A remoção não apaga o objeto:
     * chama {@link #desativar()}, preservando o histórico.</p>
     */
    private boolean ativo;

    /**
     * Construtor padrão: cria um cadastro em branco.
     *
     * <p>A pessoa nasce ativa, sem identificador e com nome e
     * e-mail vazios.</p>
     */
    public Pessoa() {
        // Strings vazias em vez de null evitam NullPointerException
        // em toString() e em comparações feitas antes do preenchimento.
        this.id = SEM_ID;
        this.nome = "";
        this.email = "";
        this.ativo = true;
    }

    /**
     * Construtor com parâmetros: cria uma pessoa ativa.
     *
     * @param novoId identificador positivo
     * @param novoNome nome completo
     * @param novoEmail endereço de e-mail
     * @throws IllegalArgumentException se o identificador não for
     *         positivo ou se nome ou e-mail estiverem vazios
     */
    public Pessoa(int novoId, String novoNome, String novoEmail) {
        // Um identificador válido precisa ser maior que zero.
        if (novoId <= 0) {
            throw new IllegalArgumentException(
                    "O identificador deve ser positivo.");
        }
        this.id = novoId;
        this.nome = exigirTexto(novoNome, "nome");
        this.email = exigirTexto(novoEmail, "e-mail");
        this.ativo = true;
    }

    /**
     * Retorna o identificador.
     *
     * @return o ID, ou 0 se ainda não foi definido
     */
    public int getId() {
        return id;
    }

    /**
     * Retorna o nome completo.
     *
     * @return o nome
     */
    public String getNome() {
        return nome;
    }

    /**
     * Retorna o e-mail.
     *
     * @return o e-mail
     */
    public String getEmail() {
        return email;
    }

    /**
     * Informa se o cadastro está ativo.
     *
     * @return {@code true} se o cadastro estiver ativo
     */
    public boolean isAtivo() {
        return ativo;
    }

    /**
     * Desativa o cadastro (exclusão lógica).
     *
     * <p>O objeto continua existindo para o histórico, mas deixa de
     * aparecer como ativo.</p>
     */
    public void desativar() {
        this.ativo = false;
    }

    /**
     * Define o identificador de uma pessoa criada pelo construtor padrão.
     *
     * <p>Tem acesso de pacote: só as classes do pacote {@code model},
     * como as classes de gestão, que geram os IDs, podem chamá-lo.
     * Funciona uma única vez.</p>
     *
     * @param novoId identificador positivo gerado pelo sistema
     * @throws IllegalArgumentException se {@code novoId} não for positivo
     * @throws IllegalStateException se a pessoa já tiver identificador
     */
    void definirId(int novoId) {
        if (this.id != SEM_ID) {
            throw new IllegalStateException(
                    "O identificador já foi definido.");
        }
        if (novoId <= 0) {
            throw new IllegalArgumentException(
                    "O identificador deve ser positivo.");
        }
        this.id = novoId;
    }

    /**
     * Altera nome e e-mail, validando os dois antes.
     *
     * <p>É {@code protected}: só as subclasses usam, dentro dos seus
     * próprios métodos de alteração.</p>
     *
     * @param novoNome novo nome completo
     * @param novoEmail novo e-mail
     * @throws IllegalArgumentException se algum dado estiver vazio
     */
    protected void alterarNomeEmail(String novoNome, String novoEmail) {
        // Valida os dois antes de alterar: um e-mail vazio não deixa
        // o nome já trocado.
        String nomeValido = exigirTexto(novoNome, "nome");
        String emailValido = exigirTexto(novoEmail, "e-mail");
        this.nome = nomeValido;
        this.email = emailValido;
    }

    /**
     * Confere se um dado textual obrigatório foi preenchido.
     *
     * <p>É {@code static} porque não depende de nenhuma pessoa
     * específica e {@code protected} para as subclasses validarem
     * os próprios campos com a mesma regra.</p>
     *
     * @param valor texto a conferir
     * @param campo nome do campo, usado na mensagem de erro
     * @return o próprio {@code valor}, quando válido
     * @throws IllegalArgumentException se {@code valor} for nulo,
     *         vazio ou contiver apenas espaços
     */
    protected static String exigirTexto(String valor, String campo) {
        // isBlank() é verdadeiro para texto vazio ou só com espaços.
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(
                    "O campo " + campo + " deve ser preenchido.");
        }
        return valor;
    }

    /**
     * Retorna uma descrição com os dados comuns.
     *
     * @return texto com ID, nome, e-mail e situação
     */
    @Override
    public String toString() {
        return "id=" + id
                + ", nome=" + nome
                + ", email=" + email
                + ", situação=" + (ativo ? "ativo" : "inativo");
    }
}