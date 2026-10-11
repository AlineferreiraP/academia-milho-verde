/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model;

/**
 * Representa um cliente cadastrado na Academia Milho Verde.
 *
 * <p>Herda de {@link Pessoa} o identificador, o nome, o e-mail e a
 * situação do cadastro, e acrescenta endereço, telefone e CPF
 * pseudonimizado. O cliente não usa o sistema: é cadastrado e
 * atendido pelos funcionários.</p>
 *
 * @author Aline Ferreira Pena
 */
public class Cliente extends Pessoa {

    /** Endereço informado no cadastro. */
    private String endereco;

    /**
     * Telefone de contato.
     *
     * <p>Guardado como texto para preservar código de área,
     * zeros iniciais e símbolos como parênteses e hífen.</p>
     */
    private String telefone;

    /**
     * CPF pseudonimizado do cliente.
     *
     * <p>Recebe o resultado do tratamento do CPF feito pelo
     * sistema, nunca o número original.</p>
     */
    private String cpfPseudo;

    /**
     * Construtor padrão: cria um cliente em branco.
     *
     * <p>{@code super()} cria a parte comum em branco; endereço,
     * telefone e CPF começam vazios.</p>
     */
    public Cliente() {
        super();
        // Strings vazias em vez de null evitam NullPointerException
        // em toString() e em comparações feitas antes do preenchimento.
        this.endereco = "";
        this.telefone = "";
        this.cpfPseudo = "";
    }

    /**
     * Construtor com parâmetros: cria um cliente ativo.
     *
     * <p>{@code super(...)} vem na primeira linha e valida o ID, o
     * nome e o e-mail; depois são validados os dados próprios do
     * cliente.</p>
     *
     * @param novoId identificador positivo do cliente
     * @param novoNome nome completo do cliente
     * @param novoEndereco endereço do cliente
     * @param novoTelefone telefone de contato
     * @param novoEmail endereço de e-mail
     * @param novoCpfPseudo CPF já pseudonimizado
     * @throws IllegalArgumentException se o identificador não for
     *         positivo ou se algum dado estiver nulo, vazio ou
     *         contiver apenas espaços
     */
    public Cliente(int novoId, String novoNome, String novoEndereco,
                   String novoTelefone, String novoEmail,
                   String novoCpfPseudo) {
        // super(...) precisa ser a primeira linha: a Pessoa valida e
        // guarda o ID, o nome e o e-mail antes dos dados do cliente.
        super(novoId, novoNome, novoEmail);

        // Os dados próprios usam a mesma regra herdada de Pessoa.
        this.endereco = exigirTexto(novoEndereco, "endereço");
        this.telefone = exigirTexto(novoTelefone, "telefone");
        this.cpfPseudo = exigirTexto(novoCpfPseudo, "CPF pseudonimizado");
    }

    /**
     * Retorna o endereço do cliente.
     *
     * @return o endereço
     */
    public String getEndereco() {
        return endereco;
    }

    /**
     * Retorna o telefone de contato do cliente.
     *
     * @return o telefone
     */
    public String getTelefone() {
        return telefone;
    }

    /**
     * Retorna o CPF pseudonimizado do cliente.
     *
     * @return o CPF pseudonimizado, nunca o número original
     */
    public String getCpfPseudo() {
        return cpfPseudo;
    }

    /**
     * Atualiza os dados cadastrais do cliente.
     *
     * <p>Todos os dados são validados antes de qualquer alteração.
     * Se um deles for inválido, nada muda.</p>
     *
     * @param novoNome novo nome completo
     * @param novoEndereco novo endereço
     * @param novoTelefone novo telefone de contato
     * @param novoEmail novo e-mail
     * @param novoCpfPseudo novo CPF já pseudonimizado
     * @throws IllegalArgumentException se algum dado estiver nulo,
     *         vazio ou contiver apenas espaços
     */
    public void atualizarCadastro(String novoNome, String novoEndereco,
                                  String novoTelefone, String novoEmail,
                                  String novoCpfPseudo) {
        // Valida primeiro os dados do cliente; nome e e-mail são
        // validados por alterarNomeEmail antes de mudar qualquer coisa.
        String enderecoValido = exigirTexto(novoEndereco, "endereço");
        String telefoneValido = exigirTexto(novoTelefone, "telefone");
        String cpfValido = exigirTexto(novoCpfPseudo, "CPF pseudonimizado");

        alterarNomeEmail(novoNome, novoEmail);
        this.endereco = enderecoValido;
        this.telefone = telefoneValido;
        this.cpfPseudo = cpfValido;
    }

    /**
     * Compara este cliente com outro objeto pelo identificador.
     *
     * <p>Um cliente ainda sem ID só é igual a ele mesmo.</p>
     *
     * @param outro objeto a comparar
     * @return {@code true} se representarem o mesmo cliente
     */
    @Override
    public boolean equals(Object outro) {
        // Mesmo objeto na memória: certamente iguais.
        if (this == outro) {
            return true;
        }
        // instanceof também devolve false quando outro é null.
        if (!(outro instanceof Cliente)) {
            return false;
        }
        Cliente cliente = (Cliente) outro;

        // Clientes em branco (sem ID) não são considerados iguais
        // entre si, pois ainda não representam ninguém.
        if (getId() == SEM_ID) {
            return false;
        }
        return getId() == cliente.getId();
    }

    /**
     * Calcula o código hash a partir do ID, coerente com
     * {@link #equals(Object)}.
     *
     * @return o código hash do cliente
     */
    @Override
    public int hashCode() {
        return Integer.hashCode(getId());
    }

    /**
     * Retorna uma descrição legível do cliente, sem o CPF.
     *
     * @return texto com ID, nome, telefone, e-mail e situação
     */
    @Override
    public String toString() {
        // super.toString() traz os dados comuns da Pessoa; o CPF fica
        // de fora para não expor dado pessoal.
        return "Cliente[" + super.toString()
                + ", telefone=" + telefone + "]";
    }
}