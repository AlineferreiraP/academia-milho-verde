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
 * <p>A senha não tem getter e não aparece no {@code toString}: ela só
 * é conferida por {@link #verificarSenha(String)} e trocada por
 * {@link #alterarSenha(String, String)}.</p>
 *
 * @author Aline Ferreira Pena
 */
public abstract class Funcionario extends Pessoa {

    /** Senha de acesso ao sistema; nunca sai da classe. */
    private String senha;

    /**
     * Construtor padrão: cria um funcionário em branco.
     *
     * <p>{@code super()} cria a parte comum em branco; a senha começa
     * vazia.</p>
     */
    public Funcionario() {
        super();
        // String vazia em vez de null evita NullPointerException
        // em verificarSenha() antes de o cadastro ser preenchido.
        this.senha = "";
    }

    /**
     * Construtor com parâmetros: cria um funcionário ativo.
     *
     * <p>{@code super(...)} vem na primeira linha e valida o ID, o
     * nome e o e-mail; depois é validada a senha.</p>
     *
     * @param novoId identificador positivo do funcionário
     * @param novoNome nome completo
     * @param novoEmail e-mail usado no login
     * @param novaSenha senha de acesso
     * @throws IllegalArgumentException se o identificador não for
     *         positivo ou se algum dado estiver nulo, vazio ou
     *         contiver apenas espaços
     */
    public Funcionario(int novoId, String novoNome, String novoEmail,
                       String novaSenha) {
        // super(...) precisa ser a primeira linha: a Pessoa valida e
        // guarda o ID, o nome e o e-mail.
        super(novoId, novoNome, novoEmail);

        // A senha usa a mesma regra herdada de Pessoa.
        this.senha = exigirTexto(novaSenha, "senha");
    }

    /**
     * Altera o nome e o e-mail do funcionário.
     *
     * <p>Usa {@code alterarNomeEmail} da {@link Pessoa}, que valida os
     * dois antes de trocar.</p>
     *
     * @param novoNome novo nome completo
     * @param novoEmail novo e-mail
     * @throws IllegalArgumentException se algum dado estiver vazio
     */
    public void editarDados(String novoNome, String novoEmail) {
        alterarNomeEmail(novoNome, novoEmail);
    }

    /**
     * Confere se a senha informada é a senha do funcionário.
     *
     * <p>Usado no login. A senha guardada nunca sai da classe: quem
     * chama só recebe {@code true} ou {@code false}.</p>
     *
     * @param senhaInformada senha digitada no login
     * @return {@code true} se for igual à senha guardada
     */
    public boolean verificarSenha(String senhaInformada) {
        // Funcionário em branco (sem senha) nunca passa no login.
        if (senha.isEmpty()) {
            return false;
        }
        // equals compara o conteúdo do texto; devolve false se a
        // senha informada for null.
        return senha.equals(senhaInformada);
    }

    /**
     * Troca a senha, exigindo a senha atual.
     *
     * @param senhaAtual senha usada hoje
     * @param novaSenha senha nova
     * @return {@code true} se a senha foi trocada; {@code false} se a
     *         senha atual estiver errada
     * @throws IllegalArgumentException se a nova senha estiver vazia
     */
    public boolean alterarSenha(String senhaAtual, String novaSenha) {
        // Sem a senha atual certa, nada muda.
        if (senha.equals(senhaAtual)) {
            // A nova senha segue a regra de campo obrigatório.
            this.senha = exigirTexto(novaSenha, "senha");
            return true;
        }
        return false;
    }

    /**
     * Informa se o funcionário pode usar as despesas e o balanço mensal.
     *
     * <p>Método abstrato: cada subclasse responde do seu jeito
     * (polimorfismo). O colaborador responde {@code false}; o
     * administrador, {@code true}.</p>
     *
     * @return {@code true} se pode acessar o financeiro
     */
    public abstract boolean podeAcessarFinanceiro();

    /**
     * Retorna uma descrição do funcionário, sem a senha.
     *
     * @return texto com os dados comuns da pessoa
     */
    @Override
    public String toString() {
        // super.toString() traz ID, nome, e-mail e situação; a senha
        // fica de fora.
        return "Funcionario[" + super.toString() + "]";
    }
}