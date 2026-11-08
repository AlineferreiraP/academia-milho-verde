/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model;

import com.mycompany.academia.milho.verde.dao.ClienteDAO;
import com.mycompany.academia.milho.verde.dao.ClienteDAOMemoria;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

/**
 * Coordena o sistema da Academia Milho Verde.
 *
 * <p>
 * Não guarda as listas de dados: cria as classes de gestão e a catraca e dá
 * acesso a elas (padrão Facade). Os getters das gestões só funcionam com um
 * funcionário logado, e a gestão financeira só é entregue a quem
 * {@link Funcionario#podeAcessarFinanceiro()} — o colaborador recebe
 * {@code false}, o administrador {@code true}.</p>
 *
 * <p>
 * Também guarda os membros da classe (static): as quatro salas, os contadores
 * de instâncias (itens 11 e 12) e o tratamento do CPF.</p>
 *
 * @author Aline Ferreira
 */
public class Sistema {

    /**
     * As quatro salas da academia, criadas uma única vez.
     */
    private static final Sala[] SALAS = {
        new Sala(1, Sala.SPINNING, 20),
        new Sala(2, Sala.MUSCULACAO, 40),
        new Sala(3, Sala.FIT_DANCE, 25),
        new Sala(4, Sala.PILATES, 15)
    };

    /**
     * Item 11a: clientes criados, private com get e set.
     */
    private static int totalClientes = 0;

    /**
     * Item 11b: clientes criados, protected (acesso direto no pacote).
     */
    protected static int totalClientesProtegido = 0;

    /**
     * Item 12: produtos criados.
     */
    private static int totalProdutos = 0;

    /**
     * Gestão dos clientes.
     */
    private GestaoClientes gestaoClientes;

    /**
     * Gestão dos funcionários.
     */
    private GestaoFuncionarios gestaoFuncionarios;

    /**
     * Gestão das aulas e dos agendamentos.
     */
    private GestaoAgendamentos gestaoAgendamentos;

    /**
     * Gestão da lojinha, da lanchonete e das vendas.
     */
    private GestaoVendas gestaoVendas;

    /**
     * Gestão das despesas e do balanço.
     */
    private GestaoFinanceiro gestaoFinanceiro;

    /**
     * Catraca de acesso.
     */
    private Catraca catraca;

    /**
     * Funcionário que fez login, ou {@code null}.
     */
    private Funcionario usuarioLogado;

    /**
     * Construtor padrão: cria as gestões e a catraca, sem dados e sem ninguém
     * logado. Os clientes ficam na memória.
     */
    public Sistema() {
        this(new ClienteDAOMemoria());
    }

    // Usa o DAO informado para guardar os clientes; o resto é igual ao padrão.
    public Sistema(ClienteDAO clienteDAO) {
        this.gestaoClientes = new GestaoClientes(clienteDAO);
        this.gestaoFuncionarios = new GestaoFuncionarios();
        this.gestaoVendas = new GestaoVendas();
        this.catraca = new Catraca();
        // As gestões abaixo usam a mesma GestaoVendas e a mesma Catraca.
        this.gestaoAgendamentos = new GestaoAgendamentos(gestaoVendas);
        this.gestaoFinanceiro = new GestaoFinanceiro(gestaoVendas, catraca);
        this.usuarioLogado = null;
    }

    /**
     * Retorna as quatro salas.
     *
     * <p>
     * Devolve uma cópia do vetor, para ninguém trocar as salas.</p>
     *
     * @return vetor com as salas
     */
    
    
    public static Sala[] getSalas() {
        return SALAS.clone();
    }

    /**
     * Retorna o total de clientes criados (item 11a).
     *
     * @return o total
     */
    public static int getTotalClientes() {
        return totalClientes;
    }

    /**
     * Altera o total de clientes criados (item 11a).
     *
     * @param valor novo total, zero ou maior
     * @throws IllegalArgumentException se for negativo
     */
    public static void setTotalClientes(int valor) {
        if (valor < 0) {
            throw new IllegalArgumentException(
                    "O total de clientes não pode ser negativo.");
        }
        totalClientes = valor;
    }

    /**
     * Retorna o total de produtos criados.
     *
     * @return o total
     */
    public static int getTotalProdutos() {
        return totalProdutos;
    }

    /**
     * Soma 1 ao total de produtos. Chamado pelos construtores de
     * {@link Produto}.
     */
    static void contarProduto() {
        totalProdutos++;
    }

    /**
     * Informa quantos clientes e produtos já foram criados (item 12).
     *
     * @return texto com os dois totais
     */
    public static String obterTotalInstancias() {
        return "Clientes criados: " + totalClientes
                + " | Produtos criados: " + totalProdutos;
    }

    /**
     * Pseudonimiza o CPF: guarda só um código gerado a partir dele.
     *
     * <p>
     * Usa SHA-256 com um texto fixo à frente e fica com os 16 primeiros
     * caracteres. O mesmo CPF dá sempre o mesmo código, o que permite achar
     * duplicados, mas o número original não pode ser recuperado do código.</p>
     *
     * @param cpf CPF com 11 dígitos (pontos e traço são ignorados)
     * @return o código pseudonimizado
     * @throws IllegalArgumentException se não tiver 11 dígitos
     */
    public static String pseudonimizarCpf(String cpf) {
        if (cpf == null) {
            throw new IllegalArgumentException("O CPF é obrigatório.");
        }
        String digitos = cpf.replace(".", "").replace("-", "").trim();
        if (!digitos.matches("\\d{11}")) {
            throw new IllegalArgumentException("O CPF deve ter 11 dígitos.");
        }
        try {
            MessageDigest resumo = MessageDigest.getInstance("SHA-256");
            byte[] bytes = resumo.digest(
                    ("milho-verde:" + digitos).getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : bytes) {
                hex.append(String.format("%02x", b));
            }
            return hex.substring(0, 16);
        } catch (NoSuchAlgorithmException erro) {
            // SHA-256 existe em todo Java; este erro não acontece.
            throw new IllegalStateException("SHA-256 indisponível.", erro);
        }
    }

    /**
     * Informa se já existe algum funcionário cadastrado.
     *
     * @return {@code true} se houver pelo menos um funcionário
     */
    public boolean temFuncionarios() {
        return !gestaoFuncionarios.getFuncionarios().isEmpty();
    }

    /**
     * Cadastra o primeiro administrador, para o primeiro acesso.
     *
     * @param nome nome completo
     * @param email e-mail
     * @param senha senha de acesso
     * @return o administrador cadastrado
     * @throws IllegalStateException se já houver funcionários
     * @throws IllegalArgumentException se algum dado for inválido
     */
    public Administrador criarPrimeiroAdministrador(String nome, String email,
            String senha) {
        if (temFuncionarios()) {
            throw new IllegalStateException(
                    "O primeiro administrador só pode ser criado quando não há funcionários.");
        }
        return gestaoFuncionarios.cadastrarAdministrador(nome, email, senha);
    }

    /**
     * Faz o login de um funcionário.
     *
     * @param email e-mail do funcionário
     * @param senha senha informada
     * @return {@code true} se o e-mail e a senha conferem
     */
    public boolean login(String email, String senha) {
        Funcionario funcionario = gestaoFuncionarios.buscarPorEmail(email);
        if (funcionario != null && funcionario.verificarSenha(senha)) {
            this.usuarioLogado = funcionario;
            return true;
        }
        return false;
    }

    /**
     * Encerra a sessão do funcionário logado.
     */
    public void logout() {
        this.usuarioLogado = null;
    }

    /**
     * Retorna o funcionário logado.
     *
     * @return o funcionário, ou {@code null} se ninguém fez login
     */
    public Funcionario getUsuarioLogado() {
        return usuarioLogado;
    }

    /**
     * Retorna a gestão de clientes.
     *
     * @return a gestão
     * @throws IllegalStateException se ninguém estiver logado
     */
    public GestaoClientes getGestaoClientes() {
        exigirLogin();
        return gestaoClientes;
    }

    /**
     * Retorna a gestão de funcionários.
     *
     * @return a gestão
     * @throws IllegalStateException se ninguém estiver logado
     */
    public GestaoFuncionarios getGestaoFuncionarios() {
        exigirLogin();
        return gestaoFuncionarios;
    }

    /**
     * Retorna a gestão de agendamentos.
     *
     * @return a gestão
     * @throws IllegalStateException se ninguém estiver logado
     */
    public GestaoAgendamentos getGestaoAgendamentos() {
        exigirLogin();
        return gestaoAgendamentos;
    }

    /**
     * Retorna a gestão de vendas.
     *
     * @return a gestão
     * @throws IllegalStateException se ninguém estiver logado
     */
    public GestaoVendas getGestaoVendas() {
        exigirLogin();
        return gestaoVendas;
    }

    /**
     * Retorna a gestão financeira, só para quem tem acesso a ela.
     *
     * @return a gestão
     * @throws IllegalStateException se ninguém estiver logado ou se o usuário
     * não for administrador
     */
    public GestaoFinanceiro getGestaoFinanceiro() {
        exigirLogin();
        exigirAdministrador();
        return gestaoFinanceiro;
    }

    /**
     * Retorna a catraca.
     *
     * @return a catraca
     * @throws IllegalStateException se ninguém estiver logado
     */
    public Catraca getCatraca() {
        exigirLogin();
        return catraca;
    }

    /**
     * Recebe um evento da catraca externa, que não faz login.
     *
     * @param idCliente identificador do cliente
     * @param tipo {@link RegistroCatraca#ENTRADA} ou
     * {@link RegistroCatraca#SAIDA}
     * @param dataHora data e hora da passagem
     * @return o registro criado
     * @throws IllegalArgumentException se o cliente não existir ou estiver
     * desativado
     * @throws IllegalStateException se o evento não combina com a situação do
     * cliente (por exemplo, entrada de quem já entrou)
     */
    public RegistroCatraca receberEventoCatraca(int idCliente, String tipo,
            LocalDateTime dataHora) {
        Cliente cliente = gestaoClientes.buscarCliente(idCliente);
        if (cliente == null || !cliente.isAtivo()) {
            throw new IllegalArgumentException(
                    "Cliente " + idCliente + " não encontrado ou desativado.");
        }
        return catraca.receberEvento(cliente, tipo, dataHora);
    }

    /**
     * Registra a passagem de um cliente pela catraca externa, decidindo sozinho
     * se é entrada ou saída.
     *
     * <p>
     * É o que o equipamento da catraca chama quando o cliente passa o cartão:
     * não exige login de funcionário. Se o cliente está fora, registra
     * {@link RegistroCatraca#ENTRADA}; se está dentro,
     * {@link RegistroCatraca#SAIDA}.</p>
     *
     * @param idCliente identificador do cliente
     * @param dataHora data e hora da passagem
     * @return o registro criado
     * @throws IllegalArgumentException se o cliente não existir ou estiver
     * desativado (acesso negado)
     */
    public RegistroCatraca passarNaCatraca(int idCliente, LocalDateTime dataHora) {
        String tipo = catraca.estaPresente(idCliente)
                ? RegistroCatraca.SAIDA : RegistroCatraca.ENTRADA;
        return receberEventoCatraca(idCliente, tipo, dataHora);
    }

    /**
     * Grava todos os dados no arquivo JSON.
     *
     * @param repositorio repositório que grava o arquivo
     * @throws IOException se não for possível gravar
     */
    public void salvar(RepositorioJson repositorio) throws IOException {
        repositorio.salvar(gestaoClientes, gestaoFuncionarios,
                gestaoAgendamentos, gestaoVendas, gestaoFinanceiro, catraca);
    }

    /**
     * Carrega todos os dados do arquivo JSON, se ele existir.
     *
     * @param repositorio repositório que lê o arquivo
     * @throws IOException se não for possível ler
     */
    public void carregar(RepositorioJson repositorio) throws IOException {
        repositorio.carregar(gestaoClientes, gestaoFuncionarios,
                gestaoAgendamentos, gestaoVendas, gestaoFinanceiro, catraca);
    }

    /**
     * Confere se há um funcionário logado.
     *
     * @throws IllegalStateException se ninguém fez login
     */
    private void exigirLogin() {
        if (usuarioLogado == null) {
            throw new IllegalStateException("Faça login para continuar.");
        }
    }

    /**
     * Confere se o usuário logado pode acessar o financeiro.
     *
     * <p>
     * Chama {@code podeAcessarFinanceiro()} do funcionário; o resultado depende
     * da subclasse real (polimorfismo).</p>
     *
     * @throws IllegalStateException se não puder
     */
    private void exigirAdministrador() {
        if (!usuarioLogado.podeAcessarFinanceiro()) {
            throw new IllegalStateException(
                    "Somente o administrador acessa o financeiro.");
        }
    }

    /**
     * Retorna um resumo do sistema.
     *
     * @return texto com o usuário logado e os totais de instâncias
     */
    @Override
    public String toString() {
        String usuario = (usuarioLogado == null) ? "ninguém"
                : usuarioLogado.getNome();
        return "Sistema[logado=" + usuario + ", " + obterTotalInstancias() + "]";
    }
}
