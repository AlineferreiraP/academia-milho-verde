/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model;

/**
 * Representa um cliente cadastrado na Academia Milho Verde.
 *
 * <p>Esta classe pertence à camada Model da organização MVC
 * e armazena os dados cadastrais do cliente. O cliente não
 * possui login no sistema: ele é cadastrado e atendido pelos
 * funcionários.</p>
 *
 * <p>A classe oferece dois construtores: o construtor com
 * parâmetros, que cria o cliente completo, e o construtor padrão,
 * que cria um cadastro em branco a ser preenchido depois.</p>
 *
 * <p>O identificador é definido uma única vez e não pode ser
 * alterado depois. O CPF é recebido já pseudonimizado, de modo
 * que o número original nunca é armazenado aqui.</p>
 *
 * @author Aline Ferreira Pena
 */
public class Cliente {

    /**
     * Valor do identificador enquanto o cliente ainda não recebeu
     * um ID do sistema.
     */
    private static final int SEM_ID = 0;

    /**
     * Identificador único do cliente, gerado pelo sistema.
     *
     * <p>Não é {@code final} porque o construtor padrão cria o
     * cliente sem ID. Mesmo assim, o valor só pode ser definido
     * uma vez: pelo construtor com parâmetros ou por
     * {@link #definirId(int)}.</p>
     */
    private int id;

    /** Nome completo do cliente. */
    private String nome;

    /** Endereço informado no cadastro. */
    private String endereco;

    /**
     * Telefone de contato.
     *
     * <p>Guardado como texto para preservar código de área,
     * zeros iniciais e símbolos como parênteses e hífen.</p>
     */
    private String telefone;

    /** Endereço de e-mail do cliente. */
    private String email;

    /**
     * CPF pseudonimizado do cliente.
     *
     * <p>Recebe o resultado do tratamento do CPF feito pelo
     * sistema, nunca o número original.</p>
     */
    private String cpfPseudo;

    /**
     * Indica se o cadastro está ativo.
     *
     * <p>Todo cliente começa ativo. Um cliente com histórico
     * (reservas, compras ou acessos) é desativado em vez de
     * apagado, preservando relatórios e extratos.</p>
     */
    private boolean ativo;

    /**
     * Construtor padrão: cria um cliente em branco.
     *
     * <p>O cliente nasce ativo, sem identificador e com os dados
     * textuais vazios. Antes de ser cadastrado no sistema, ele
     * precisa receber um ID por {@link #definirId(int)} e ter os
     * dados preenchidos por {@code atualizarCadastro}.</p>
     */
    public Cliente() {
        // Strings vazias em vez de null evitam NullPointerException
        // em toString() e em comparações feitas antes do preenchimento.
        this.id = SEM_ID;
        this.nome = "";
        this.endereco = "";
        this.telefone = "";
        this.email = "";
        this.cpfPseudo = "";
        this.ativo = true;
    }

    /**
     * Construtor com parâmetros: cria um cliente ativo com seus
     * dados cadastrais.
     *
     * <p>O identificador deve vir do gerador de IDs do sistema.
     * Este construtor só confere se ele é positivo; a garantia de
     * que é único fica com o gerador.</p>
     *
     * <p>Os dados textuais são obrigatórios. A validação confere
     * apenas o preenchimento, não o formato do telefone ou do e-mail.</p>
     *
     * <p>Os parâmetros têm nomes diferentes dos atributos (prefixo
     * {@code novo}) para evitar o sombreamento, prática não
     * recomendada na Aula 7.</p>
     *
     * @param novoId identificador positivo do cliente
     * @param novoNome nome completo do cliente
     * @param novoEndereco endereço do cliente
     * @param novoTelefone telefone de contato
     * @param novoEmail endereço de e-mail
     * @param novoCpfPseudo CPF já pseudonimizado
     * @throws IllegalArgumentException se o identificador não for
     *         positivo ou se algum dado textual estiver nulo, vazio
     *         ou contiver apenas espaços
     */
    public Cliente(int novoId, String novoNome, String novoEndereco,
                   String novoTelefone, String novoEmail,
                   String novoCpfPseudo) {

        // Um identificador válido precisa ser maior que zero.
        if (novoId <= 0) {
            throw new IllegalArgumentException(
                    "O identificador do cliente deve ser positivo.");
        }

        // Cada campo é conferido pelo mesmo método auxiliar, que
        // devolve o próprio valor quando ele é válido. Assim a regra
        // fica escrita em um só lugar e será reaproveitada em
        // atualizarCadastro(), sem duplicar código.
        this.id = novoId;
        this.nome = exigirTexto(novoNome, "nome");
        this.endereco = exigirTexto(novoEndereco, "endereço");
        this.telefone = exigirTexto(novoTelefone, "telefone");
        this.email = exigirTexto(novoEmail, "e-mail");
        this.cpfPseudo = exigirTexto(novoCpfPseudo, "CPF pseudonimizado");

        // Todo cliente recém-criado começa com o cadastro ativo.
        this.ativo = true;
    }

    /**
     * Retorna o identificador do cliente.
     *
     * @return o ID do cliente, ou 0 se ainda não foi definido
     */
    public int getId() {
        return id;
    }

    /**
     * Retorna o nome completo do cliente.
     *
     * @return o nome do cliente
     */
    public String getNome() {
        return nome;
    }

    /**
     * Retorna o endereço do cliente.
     *
     * @return o endereço do cliente
     */
    public String getEndereco() {
        return endereco;
    }

    /**
     * Retorna o telefone de contato do cliente.
     *
     * @return o telefone do cliente
     */
    public String getTelefone() {
        return telefone;
    }

    /**
     * Retorna o e-mail do cliente.
     *
     * @return o e-mail do cliente
     */
    public String getEmail() {
        return email;
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
     * Informa se o cadastro do cliente está ativo.
     *
     * <p>Para atributos {@code boolean}, a convenção Java usa o
     * prefixo {@code is} no lugar de {@code get}.</p>
     *
     * @return {@code true} se o cadastro estiver ativo
     */
    public boolean isAtivo() {
        return ativo;
    }

    /**
     * Atualiza os dados cadastrais do cliente.
     *
     * <p>Todos os dados são validados antes de qualquer alteração.
     * Se um deles for inválido, nenhum atributo muda: o cadastro
     * nunca fica pela metade.</p>
     *
     * <p>O identificador e a situação (ativo ou não) não são
     * alterados por este método.</p>
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
        // Primeiro valida tudo em variáveis locais; só depois altera
        // os atributos. Assim, um erro no e-mail não deixa o nome
        // já trocado.
        String nomeValido = exigirTexto(novoNome, "nome");
        String enderecoValido = exigirTexto(novoEndereco, "endereço");
        String telefoneValido = exigirTexto(novoTelefone, "telefone");
        String emailValido = exigirTexto(novoEmail, "e-mail");
        String cpfValido = exigirTexto(novoCpfPseudo, "CPF pseudonimizado");

        this.nome = nomeValido;
        this.endereco = enderecoValido;
        this.telefone = telefoneValido;
        this.email = emailValido;
        this.cpfPseudo = cpfValido;
    }

    /**
     * Desativa o cadastro do cliente.
     *
     * <p>É a remoção lógica usada quando o cliente já possui
     * histórico: o objeto continua existindo para relatórios e
     * extratos, mas deixa de aparecer como cliente ativo.</p>
     */
    public void desativar() {
        this.ativo = false;
    }

    /**
     * Define o identificador de um cliente criado pelo construtor padrão.
     *
     * <p>Tem acesso de pacote (sem modificador): só as classes do
     * pacote {@code model}, como o Sistema, podem chamá-lo. Funciona
     * uma única vez, preservando a regra de que o ID não muda.</p>
     *
     * @param novoId identificador positivo gerado pelo sistema
     * @throws IllegalArgumentException se {@code novoId} não for positivo
     * @throws IllegalStateException se o cliente já tiver identificador
     */
    void definirId(int novoId) {
        if (this.id != SEM_ID) {
            throw new IllegalStateException(
                    "O identificador do cliente já foi definido.");
        }
        if (novoId <= 0) {
            throw new IllegalArgumentException(
                    "O identificador do cliente deve ser positivo.");
        }
        this.id = novoId;
    }

    /**
     * Confere se um dado textual obrigatório foi preenchido.
     *
     * <p>É {@code static} porque não depende de nenhum cliente
     * específico: só analisa o texto recebido.</p>
     *
     * @param valor texto a conferir
     * @param campo nome do campo, usado na mensagem de erro
     * @return o próprio {@code valor}, quando válido
     * @throws IllegalArgumentException se {@code valor} for nulo,
     *         vazio ou contiver apenas espaços
     */
    private static String exigirTexto(String valor, String campo) {
        // isBlank() é verdadeiro para texto vazio ou só com espaços.
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(
                    "O campo " + campo + " do cliente deve ser preenchido.");
        }
        return valor;
    }

    /**
     * Compara este cliente com outro objeto pelo identificador.
     *
     * <p>Dois objetos {@code Cliente} representam o mesmo cliente
     * quando têm o mesmo ID, mesmo que outros dados tenham mudado.
     * Um cliente ainda sem ID só é igual a ele mesmo.</p>
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
        if (this.id == SEM_ID) {
            return false;
        }
        return this.id == cliente.id;
    }

    /**
     * Calcula o código de dispersão (hash) a partir do ID.
     *
     * <p>Deve ser coerente com {@link #equals(Object)}: clientes
     * iguais têm o mesmo hash. Coleções como {@code HashSet} usam
     * esse valor para localizar o objeto.</p>
     *
     * @return o código hash do cliente
     */
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    /**
     * Retorna uma descrição legível do cliente.
     *
     * <p>Não inclui o CPF, mesmo pseudonimizado, para que dados
     * pessoais não apareçam em impressões e mensagens de erro.</p>
     *
     * @return texto com ID, nome, telefone, e-mail e situação
     */
    @Override
    public String toString() {
        return "Cliente[id=" + id
                + ", nome=" + nome
                + ", telefone=" + telefone
                + ", email=" + email
                + ", situação=" + (ativo ? "ativo" : "inativo")
                + "]";
    }
}