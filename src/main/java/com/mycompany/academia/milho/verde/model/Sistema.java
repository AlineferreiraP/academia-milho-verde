/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model; // pacote do Model

import com.mycompany.academia.milho.verde.dao.DAO; // interface genérica do DAO
import com.mycompany.academia.milho.verde.dao.DAOMemoria; // DAO em memória
import java.time.LocalDate; // data sem hora
import java.time.LocalDateTime; // data com hora
import java.util.List; // tipo da lista
import com.mycompany.academia.milho.verde.model.GestaoAgendamentos;

/**
 * Fachada do Model: é a única classe com que o Controller conversa.
 *
 * <p>Guarda os subsistemas da academia (clientes, funcionários, agenda, lojinha,
 * catraca e financeiro), controla quem está logado e só entrega cada gestão a
 * quem tem permissão. Também guarda as 4 salas fixas e os contadores estáticos
 * de clientes e produtos criados.</p>
 *
 * @author Aline Ferreira
 */
public class Sistema { // fachada do Model: o Controller conversa só com ela

    /** As 4 salas fixas da academia, criadas uma vez só (static). */
    private static final Sala[] SALAS = { // as 4 salas fixas da academia: criadas uma vez só (static)
        new Sala(1, Sala.SPINNING, 20, 30.0, 150.0), // sala 1: spinning
        new Sala(2, Sala.MUSCULACAO, 40, 25.0, 120.0), // sala 2: musculação
        new Sala(3, Sala.FIT_DANCE, 25, 25.0, 130.0), // sala 3: fit dance
        new Sala(4, Sala.PILATES, 15, 35.0, 180.0) // sala 4: pilates
    }; // fim do vetor de salas

    /** Estratégia 1: contador {@code private static}, acessado por get e set. */
    private static int totalClientes = 0; // estratégia 1: private static, acessado por get e set
    /** Estratégia 2: contador {@code protected static}, acessado direto pelo pacote. */
    protected static int totalClientesProtegido = 0; // estratégia 2: protected static, acesso direto
    /** Quantos produtos foram criados. */
    private static int totalProdutos = 0; // quantos produtos foram criados

    /** Regras dos clientes (composição). */
    private final GestaoClientes gestaoClientes; // clientes (composição)
    /** Regras dos funcionários (composição). */
    private final GestaoFuncionarios gestaoFuncionarios; // funcionários (composição)
    /** Regras das aulas e dos agendamentos (composição). */
    private final GestaoAgendamentos gestaoAgendamentos; // aulas e agendamentos (composição)
    /** Subsistema da lojinha (composição). */
    private final GestaoVendas gestaoVendas; // subsistema lojinha (composição)
    /** Subsistema da catraca (composição). */
    private final Catraca catraca; // subsistema catraca (composição)
    /** Relatórios, despesas e balanço (composição). */
    private final GestaoFinanceiro gestaoFinanceiro; // relatórios, despesas e balanço (composição)
    /** Funcionário que está usando o sistema; {@code null} se ninguém entrou. */
    private Funcionario usuarioLogado; // quem está usando o sistema; null = ninguém

    /**
     * Cria o sistema com os clientes guardados só na memória.
     */
    public Sistema() { // construtor sem parâmetros
        this(new DAOMemoria<>()); // clientes só na memória
    } // fim do construtor sem parâmetros

    /**
     * Cria o sistema com o DAO de clientes escolhido.
     *
     * @param clienteDAO onde os clientes ficam guardados (memória ou arquivo JSON)
     */
    public Sistema(DAO<Cliente> clienteDAO) { // recebe onde os clientes ficam guardados
        this.gestaoClientes = new GestaoClientes(clienteDAO); // a gestão de clientes usa o DAO recebido
        this.gestaoFuncionarios = new GestaoFuncionarios(); // cria a gestão de funcionários
        this.gestaoAgendamentos = new GestaoAgendamentos(); // cria a gestão de agendamentos
        this.gestaoVendas = new GestaoVendas(); // cria a lojinha
        this.catraca = new Catraca(); // cria a catraca
        this.gestaoFinanceiro = new GestaoFinanceiro(gestaoVendas, gestaoAgendamentos, catraca); // o financeiro consulta os três
        this.usuarioLogado = null; // ninguém logado
    } // fim do construtor com parâmetros

    /**
     * Devolve as 4 salas fixas da academia.
     *
     * @return cópia do vetor de salas
     */
    public static Sala[] getSalas() { // devolve as salas
        return SALAS.clone(); // cópia do vetor: ninguém troca as salas de fora
    } // fim de getSalas

    /**
     * Getter do contador privado de clientes.
     *
     * @return quantos clientes foram criados
     */
    public static int getTotalClientes() { // getter do contador privado
        return totalClientes; // quantos clientes foram criados
    } // fim de getTotalClientes

    /**
     * Setter do contador privado de clientes, com validação.
     *
     * @param valor novo total (não pode ser negativo)
     * @throws IllegalArgumentException se o valor for negativo
     */
    public static void setTotalClientes(int valor) { // setter do contador privado: pode validar
        if (valor < 0) { // contador nunca é negativo
            throw new IllegalArgumentException("O total não pode ser negativo."); // erro de argumento
        } // fim do if
        totalClientes = valor; // guarda o novo total
    } // fim de setTotalClientes

    /**
     * Getter do contador de produtos.
     *
     * @return quantos produtos foram criados
     */
    public static int getTotalProdutos() { // getter do contador de produtos
        return totalProdutos; // quantos produtos foram criados
    } // fim de getTotalProdutos

    /**
     * Acesso de pacote: o construtor do {@link Produto} avisa que nasceu um produto.
     */
    static void contarProduto() { // pacote: o construtor do Produto avisa que nasceu
        totalProdutos++; // soma 1
    } // fim de contarProduto

    /**
     * Método de classe que resume os contadores de instâncias.
     *
     * @return texto com o total de clientes (dos dois contadores) e de produtos
     */
    public static String obterTotalInstancias() { // método de classe: conta clientes e produtos criados
        return "Clientes: " + totalClientes + " (protegido: " + totalClientesProtegido + ") | Produtos: " + totalProdutos; // os contadores
    } // fim de obterTotalInstancias

    /**
     * Diz se já existe algum funcionário cadastrado.
     *
     * @return {@code true} se houver funcionário; {@code false} no primeiro acesso
     */
    public boolean temFuncionarios() { // primeiro acesso?
        return gestaoFuncionarios.temFuncionarios(); // pergunta à gestão
    } // fim de temFuncionarios

    /**
     * Cria o primeiro administrador, só no primeiro acesso.
     *
     * @param nome nome do administrador
     * @param endereco endereço
     * @param telefone telefone
     * @param email e-mail (também é o login)
     * @param senha senha de acesso
     * @return o administrador criado
     * @throws IllegalStateException se já existir funcionário
     */
    public Administrador criarPrimeiroAdministrador(String nome, String endereco, String telefone,
            String email, String senha) { // só no primeiro acesso
        if (temFuncionarios()) { // já existe funcionário?
            throw new IllegalStateException("O primeiro administrador já foi criado."); // erro de estado
        } // fim do if
        return gestaoFuncionarios.cadastrarAdministrador(nome, endereco, telefone, email, senha); // cria o administrador
    } // fim de criarPrimeiroAdministrador

    /**
     * Entra no sistema com e-mail e senha.
     *
     * @param email e-mail do funcionário
     * @param senha senha digitada
     * @return {@code true} se entrou; {@code false} se o e-mail ou a senha estiverem errados
     */
    public boolean login(String email, String senha) { // entra no sistema
        Funcionario funcionario = gestaoFuncionarios.buscarPorEmail(email); // procura pelo e-mail
        if (funcionario == null || !funcionario.verificarSenha(senha)) { // não existe ou senha errada?
            return false; // não entra
        } // fim do if
        usuarioLogado = funcionario; // guarda quem entrou
        return true; // entrou
    } // fim de login

    /**
     * Sai do sistema.
     */
    public void logout() { // sai do sistema
        usuarioLogado = null; // ninguém logado
    } // fim de logout

    /**
     * Devolve quem está logado.
     *
     * @return o funcionário logado, ou {@code null}
     */
    public Funcionario getUsuarioLogado() { // quem está logado
        return usuarioLogado; // pode ser null
    } // fim de getUsuarioLogado

    /**
     * Diz se o usuário logado é administrador (polimorfismo de {@link Funcionario}).
     *
     * @return {@code true} se há alguém logado e ele é administrador
     */
    public boolean isAdministradorLogado() { // o logado é administrador?
        return usuarioLogado != null && usuarioLogado.podeAcessarFinanceiro(); // polimorfismo
    } // fim de isAdministradorLogado

    /**
     * Entrega a gestão de clientes (qualquer funcionário logado).
     *
     * @return a gestão de clientes
     * @throws IllegalStateException se ninguém estiver logado
     */
    public GestaoClientes getGestaoClientes() { // clientes: qualquer funcionário logado
        exigirLogin(); // precisa estar logado
        return gestaoClientes; // devolve a gestão
    } // fim de getGestaoClientes

    /**
     * Entrega a gestão de funcionários (só o administrador).
     *
     * @return a gestão de funcionários
     * @throws IllegalStateException se o logado não for administrador
     */
    public GestaoFuncionarios getGestaoFuncionarios() { // funcionários: só o administrador
        exigirAdministrador(); // precisa ser administrador
        return gestaoFuncionarios; // devolve a gestão
    } // fim de getGestaoFuncionarios

    /**
     * Entrega a gestão de agendamentos (qualquer funcionário logado).
     *
     * @return a gestão de agendamentos
     * @throws IllegalStateException se ninguém estiver logado
     */
    public GestaoAgendamentos getGestaoAgendamentos() { // agenda: qualquer funcionário logado
        exigirLogin(); // precisa estar logado
        return gestaoAgendamentos; // devolve a gestão
    } // fim de getGestaoAgendamentos

    /**
     * Entrega a lojinha (qualquer funcionário logado).
     *
     * @return a gestão de vendas
     * @throws IllegalStateException se ninguém estiver logado
     */
    public GestaoVendas getGestaoVendas() { // lojinha: qualquer funcionário logado
        exigirLogin(); // precisa estar logado
        return gestaoVendas; // devolve a gestão
    } // fim de getGestaoVendas

    /**
     * Entrega o financeiro. Qualquer logado vê relatórios; despesas e balanço
     * conferem o administrador por dentro.
     *
     * @return a gestão financeira
     * @throws IllegalStateException se ninguém estiver logado
     */
    public GestaoFinanceiro getGestaoFinanceiro() { // relatórios: qualquer logado; despesas e balanço conferem o administrador
        exigirLogin(); // precisa estar logado
        return gestaoFinanceiro; // devolve a gestão
    } // fim de getGestaoFinanceiro

    /**
     * Entrega a catraca, que funciona sozinha, sem login.
     *
     * @return a catraca
     */
    public Catraca getCatraca() { // a catraca funciona sozinha, sem login
        return catraca; // devolve a catraca
    } // fim de getCatraca

    /**
     * Terminal da catraca: o cliente digita o ID e a catraca decide entrada ou saída.
     *
     * @param idCliente identificador do cliente
     * @param dataHora momento em que passou
     * @return o registro de entrada ou de saída
     * @throws IllegalArgumentException se o cliente não existir
     */
    public RegistroCatraca passarNaCatraca(int idCliente, LocalDateTime dataHora) { // terminal da catraca: sem login
        Cliente cliente = gestaoClientes.buscarCliente(idCliente); // procura o cliente pelo ID digitado
        return catraca.passar(cliente, dataHora); // a catraca decide se é entrada ou saída
    } // fim de passarNaCatraca

    /**
     * Registra uma venda da lojinha e guarda o extrato no cliente.
     *
     * @param idCliente identificador do cliente que compra
     * @param idsProdutos identificadores dos produtos
     * @param quantidades quantidade de cada produto, na mesma ordem
     * @return a venda registrada
     * @throws IllegalStateException se ninguém estiver logado ou faltar estoque
     * @throws IllegalArgumentException se o cliente ou os dados forem inválidos
     */
    public VendaProdutos registrarVendaLojinha(int idCliente, List<Integer> idsProdutos,
            List<Integer> quantidades) { // venda na lojinha com extrato
        exigirLogin(); // precisa estar logado
        Cliente cliente = gestaoClientes.buscarCliente(idCliente); // o cliente que compra
        VendaProdutos venda = gestaoVendas.registrarVenda(cliente, idsProdutos, quantidades); // a lojinha vende
        gestaoClientes.guardarExtrato(venda.gerarExtrato()); // o extrato fica salvo com o cliente
        return venda; // devolve a venda
    } // fim de registrarVendaLojinha

    /**
     * Confirma um agendamento, registra o pagamento e guarda o extrato no cliente.
     *
     * @param idAgendamento identificador do agendamento
     * @param agora data e hora da confirmação
     * @return o pagamento registrado
     * @throws IllegalStateException se ninguém estiver logado ou o prazo já passou
     */
    public VendaAgendamento confirmarAgendamento(int idAgendamento, LocalDateTime agora) { // confirmação com extrato
        exigirLogin(); // precisa estar logado
        VendaAgendamento pagamento = gestaoAgendamentos.confirmarAgendamento(idAgendamento, agora); // registra o pagamento
        gestaoClientes.guardarExtrato(pagamento.gerarExtrato()); // o extrato fica salvo com o cliente
        return pagamento; // devolve o pagamento
    } // fim de confirmarAgendamento

    /**
     * Cancela um agendamento e, se houver devolução, guarda o extrato dela no cliente.
     *
     * @param idAgendamento identificador do agendamento
     * @param hoje data do cancelamento
     * @return valor devolvido ao cliente (50% ou zero)
     * @throws IllegalStateException se ninguém estiver logado
     */
    public double cancelarAgendamento(int idAgendamento, LocalDate hoje) { // cancelamento com extrato da devolução
        exigirLogin(); // precisa estar logado
        double devolvido = gestaoAgendamentos.cancelarAgendamento(idAgendamento, hoje); // cancela e calcula a devolução
        if (devolvido > 0) { // houve devolução?
            Agendamento agendamento = buscarAgendamento(idAgendamento); // para saber o cliente
            gestaoClientes.guardarExtrato(new Extrato(agendamento.getCliente(), hoje.atStartOfDay(),
                    "Devolução de 50% (agendamento " + idAgendamento + ")", -devolvido)); // extrato com valor negativo
        } // fim do if
        return devolvido; // devolve o valor que voltou ao cliente
    } // fim de cancelarAgendamento

    /**
     * Grava tudo no arquivo {@code academia.json}.
     *
     * @param repositorio quem lê e grava o arquivo
     */
    public void salvar(RepositorioJson repositorio) { // grava tudo no academia.json
        repositorio.salvar(gestaoClientes, gestaoFuncionarios, gestaoAgendamentos, gestaoVendas, gestaoFinanceiro, catraca); // o repositório grava
    } // fim de salvar

    /**
     * Lê tudo do arquivo {@code academia.json}.
     *
     * @param repositorio quem lê e grava o arquivo
     */
    public void carregar(RepositorioJson repositorio) { // lê tudo do academia.json
        repositorio.carregar(gestaoClientes, gestaoFuncionarios, gestaoAgendamentos, gestaoVendas, gestaoFinanceiro, catraca); // o repositório lê
    } // fim de carregar

    /**
     * Acha um agendamento pelo ID.
     *
     * @param id identificador do agendamento
     * @return o agendamento
     * @throws IllegalArgumentException se não existir
     */
    private Agendamento buscarAgendamento(int id) { // acha um agendamento pelo ID
        IteradorLista<Agendamento> iterador = new IteradorLista<>(gestaoAgendamentos.listarAgendamentos()); // percorre
        while (iterador.hasNext()) { // enquanto houver agendamento
            Agendamento agendamento = iterador.next(); // pega o próximo
            if (agendamento.getId() == id) { // é ele?
                return agendamento; // achou
            } // fim do if
        } // fim do while
        throw new IllegalArgumentException("Agendamento " + id + " não encontrado."); // erro de argumento
    } // fim de buscarAgendamento

    /**
     * Confere se há alguém logado.
     *
     * @throws IllegalStateException se ninguém estiver logado
     */
    private void exigirLogin() { // confere se há alguém logado
        if (usuarioLogado == null) { // ninguém logado?
            throw new IllegalStateException("Faça login primeiro."); // erro de estado
        } // fim do if
    } // fim de exigirLogin

    /**
     * Confere se o logado é administrador.
     *
     * @throws IllegalStateException se não for administrador
     */
    private void exigirAdministrador() { // confere se o logado é administrador
        if (!isAdministradorLogado()) { // não é administrador?
            throw new IllegalStateException("Apenas o administrador pode fazer isso."); // erro de estado
        } // fim do if
    } // fim de exigirAdministrador

    /**
     * Resume o sistema em uma linha.
     *
     * @return texto com o nome de quem está logado
     */
    @Override // sobrescreve o toString de Object
    public String toString() { // resumo em uma linha
        return "Sistema da Academia Milho Verde | logado: "
                + (usuarioLogado == null ? "ninguém" : usuarioLogado.getNome()); // quem está logado
    } // fim do toString
}