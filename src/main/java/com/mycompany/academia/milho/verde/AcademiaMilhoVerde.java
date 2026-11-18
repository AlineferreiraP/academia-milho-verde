/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.academia.milho.verde; // pacote raiz do projeto

import com.mycompany.academia.milho.verde.controller.AcademiaController; // liga a tela às regras do sistema
import com.mycompany.academia.milho.verde.dao.ClienteDAOJson; // guarda os clientes em arquivo JSON
import com.mycompany.academia.milho.verde.dao.DAO; // contrato genérico de acesso aos dados
import com.mycompany.academia.milho.verde.dao.DAOMemoria; // DAO que guarda só na memória
import com.mycompany.academia.milho.verde.dao.DaoException; // erro de acesso aos dados
import com.mycompany.academia.milho.verde.model.Administrador; // perfil administrador
import com.mycompany.academia.milho.verde.model.AgendaSemanal; // grade semanal de uma sala
import com.mycompany.academia.milho.verde.model.Agendamento; // reserva de aula
import com.mycompany.academia.milho.verde.model.Aula; // aula numa sala
import com.mycompany.academia.milho.verde.model.BalancoMensal; // balanço do mês
import com.mycompany.academia.milho.verde.model.Catraca; // entrada e saída dos clientes
import com.mycompany.academia.milho.verde.model.Cliente; // cliente da academia
import com.mycompany.academia.milho.verde.model.Colaborador; // perfil colaborador
import com.mycompany.academia.milho.verde.model.ComparadorAgendamento; // compara agendamentos por dia e hora
import com.mycompany.academia.milho.verde.model.ComparadorCliente; // compara clientes por CPF ou nome
import com.mycompany.academia.milho.verde.model.Despesa; // despesa da academia
import com.mycompany.academia.milho.verde.model.Estoque; // estoque da lojinha
import com.mycompany.academia.milho.verde.model.Extrato; // nota de uma compra ou pagamento
import com.mycompany.academia.milho.verde.model.Funcionario; // superclasse dos funcionários
import com.mycompany.academia.milho.verde.model.GestaoAgendamentos; // regras da agenda
import com.mycompany.academia.milho.verde.model.GestaoClientes; // regras dos clientes
import com.mycompany.academia.milho.verde.model.GestaoFinanceiro; // relatórios, despesas e balanço
import com.mycompany.academia.milho.verde.model.GestaoFuncionarios; // regras dos funcionários
import com.mycompany.academia.milho.verde.model.GestaoVendas; // lojinha
import com.mycompany.academia.milho.verde.model.Identificavel; // contrato: todo objeto guardado tem ID
import com.mycompany.academia.milho.verde.model.ItemVenda; // linha de uma venda
import com.mycompany.academia.milho.verde.model.IteradorLista; // nosso iterador
import com.mycompany.academia.milho.verde.model.Pessoa; // superclasse de Cliente e Funcionario
import com.mycompany.academia.milho.verde.model.Produto; // produto da lojinha
import com.mycompany.academia.milho.verde.model.RegistroCatraca; // registro de entrada ou saída
import com.mycompany.academia.milho.verde.model.RelatorioVendas; // resumo de vendas de um período
import com.mycompany.academia.milho.verde.model.RepositorioJson; // lê e grava o academia.json
import com.mycompany.academia.milho.verde.model.Sala; // sala da academia
import com.mycompany.academia.milho.verde.model.Sistema; // fachada do Model
import com.mycompany.academia.milho.verde.model.Validacao; // validações reutilizadas
import com.mycompany.academia.milho.verde.model.Venda; // superclasse das vendas
import com.mycompany.academia.milho.verde.model.VendaAgendamento; // pagamento de diária ou mensalidade
import com.mycompany.academia.milho.verde.model.VendaProdutos; // venda da lojinha
import com.mycompany.academia.milho.verde.view.ConsoleView; // tela em modo texto
import java.io.IOException; // erro ao ler ou apagar os arquivos de teste
import org.json.JSONObject; // altera o JSON salvo para testar dados quebrados
import java.lang.reflect.Modifier; // descobre se a classe é abstrata
import java.nio.file.Files; // lê, grava e apaga os arquivos de teste
import java.nio.file.Path; // caminho de um arquivo
import java.time.DayOfWeek; // dias da semana
import java.time.LocalDate; // data sem hora
import java.time.LocalDateTime; // data com hora
import java.time.format.DateTimeFormatter; // formato das datas digitadas no menu
import java.time.temporal.TemporalAdjusters; // acha a próxima segunda-feira
import java.util.ArrayList; // lista que cresce sozinha
import java.util.Collections; // sort com comparator
import java.util.Iterator; // interface do padrão Iterator
import java.util.List; // tipo da lista
import java.util.Scanner; // lê o teclado ou um texto combinado

/**
 * Classe principal do projeto. Ao rodar, mostra a tela de entrada, onde a pessoa escolhe
 * entre abrir o sistema Academia (menu), fazer os testes do sistema (questões) ou fechar.
 *
 * <p>O menu é o sistema do dia a dia: monta as três partes do MVC ({@link Sistema},
 * {@link ConsoleView} e {@link RepositorioJson}) e entrega o controle ao
 * {@link AcademiaController}. Os testes executam todos os blocos
 * {@code // Questao N { ... }} em sequência e imprime o resultado; as validações que devem
 * ser recusadas são testadas com {@code esperarErro}, que mostra o motivo da recusa.
 * Os argumentos {@code menu} e {@code testes} pulam a tela de entrada.</p>
 *
 * @author Aline Ferreira
 */
public class AcademiaMilhoVerde { // classe principal: testes das questões e porta de entrada do menu

    /** Quantas validações foram recusadas como esperado. */
    private static int recusasEsperadas = 0; // contador das recusas esperadas
    /** Quantos valores conferidos bateram com o esperado. */
    private static int conferidosCertos = 0; // contador dos valores certos
    /** Quantas vezes uma validação deixou passar algo que devia recusar. */
    private static int falhas = 0; // contador das falhas

    /**
     * Construtor privado: a classe só tem métodos estáticos e não deve ser instanciada.
     */
    private AcademiaMilhoVerde() { // construtor privado
    } // fim do construtor

    /**
     * Ponto de entrada do programa.
     *
     * @param args sem argumento mostra a tela de entrada; {@code menu} abre o sistema direto;
     *             {@code testes} roda direto as questões
     */
    public static void main(String[] args) { // ponto de entrada do programa
        if (args.length > 0 && args[0].equals("testes")) { // pediu só as questões?
            executarQuestoes(); // roda as questões e termina
            return; // encerra
        } // fim do if
        ConsoleView view = new ConsoleView(); // a mesma tela serve à entrada e ao sistema
        if (args.length > 0 && args[0].equals("menu")) { // pediu só o sistema?
            abrirSistema(view); // abre o menu e termina
            return; // encerra
        } // fim do if
        telaInicial(view); // sem argumento: tela de entrada
    } // fim do main

    /**
     * Tela de entrada: mostra o nome da academia e deixa a pessoa escolher entre
     * abrir o sistema, fazer os testes do sistema ou fechar. Volta para cá depois de cada escolha.
     *
     * @param view a tela em modo texto
     */
    private static void telaInicial(ConsoleView view) { // nome da academia e escolha do que fazer
        view.mostrarMensagem("ACADEMIA MILHO VERDE"); // nome do sistema
        boolean continuar = true; // enquanto a pessoa não for embora
        while (continuar) { // volta para a entrada depois de cada escolha
            int opcao = view.mostrarMenu("O QUE VOCÊ QUER FAZER?", new String[]{
                "1 - Abrir o sistema Academia",
                "2 - Testes do sistema",
                "0 - Fechar"}); // opções
            switch (opcao) { // executa a escolha
                case 1 -> abrirSistema(view); // abre o menu do sistema
                case 2 -> executarQuestoes(); // roda as questões
                case 0 -> { // ir embora
                    view.mostrarMensagem("Até a próxima! Bons treinos!"); // despedida
                    continuar = false; // sai do laço
                } // fim do case 0
                default -> view.mostrarErro("Escolha 1, 2 ou 0."); // opção que não existe
            } // fim do switch
        } // fim do while
    } // fim de telaInicial

    /**
     * Abre o sistema: monta as três partes do MVC ({@link Sistema}, {@link ConsoleView} e
     * {@link RepositorioJson}) e entrega o controle ao {@link AcademiaController}.
     *
     * @param view a tela em modo texto
     */
    private static void abrirSistema(ConsoleView view) { // liga o MVC e abre o menu
        Sistema sistemaReal = new Sistema(new ClienteDAOJson()); // Model, com os clientes no clientes.json
        RepositorioJson repositorio = new RepositorioJson(); // lê e grava o academia.json
        new AcademiaController(sistemaReal, view, repositorio).iniciar(); // liga as três partes e abre o menu
    } // fim de abrirSistema

    /**
     * Roda as chamadas de teste de cada questão ({@code // Questao N { ... }}) e imprime o resumo.
     * As validações que devem ser recusadas são testadas com {@code esperarErro}.
     */
    private static void executarQuestoes() { // todas as questões em sequência
        recusasEsperadas = 0; // zera os contadores a cada circuito
        conferidosCertos = 0; // zera os contadores a cada circuito
        falhas = 0; // zera os contadores a cada circuito
        LocalDate segunda = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).plusWeeks(2); // uma segunda daqui a ~3 semanas
        Sistema sistema = new Sistema(new DAOMemoria<>()); // sistema com clientes só na memória
        sistema.criarPrimeiroAdministrador("Aline Ferreira", "Diamantina, MG", "3800000000",
                "aline.pena@ufvjm.edu.br", "Isabella18@"); // primeiro acesso: cria o administrador
        sistema.login("aline.pena@ufvjm.edu.br", "Isabella18@"); // entra como administrador
        GestaoClientes clientes = sistema.getGestaoClientes(); // atalho para os clientes
        GestaoAgendamentos agenda = sistema.getGestaoAgendamentos(); // atalho para a agenda
        GestaoVendas lojinha = sistema.getGestaoVendas(); // atalho para a lojinha
        GestaoFinanceiro financeiro = sistema.getGestaoFinanceiro(); // atalho para o financeiro
        GestaoFuncionarios funcionarios = sistema.getGestaoFuncionarios(); // atalho para os funcionários

        // Preparação: dados usados pelas questões seguintes (não imprime nada)
        { // cliente 1, aula 1, produto 1 e agendamento 1, que várias questões reaproveitam
            Cliente ana = clientes.cadastrarCliente("Ana Souza", "Rua das Flores, 10", "38999990000",
                    "ana@email.com", "111.111.111-11"); // Cliente (que é uma Pessoa)
            Aula aula = agenda.cadastrarAula(1, segunda.atTime(7, 0), "Rafael"); // Aula numa Sala
            lojinha.cadastrarProduto("Garrafa 500ml", 15.0, 20); // Produto no Estoque
            agenda.agendar(ana, List.of(aula.getId()), Agendamento.DIARIA, 30.0, "4111111111111111"); // Agendamento da Ana
        } // fim da preparação

        // Questao 1
        { // as classes do diagrama: a lista de todas as classes do projeto
            titulo("Questão 1 - Classes do diagrama"); // título da questão
            oQueTesta("lista todas as classes do projeto, com o pacote, o tipo e o papel de cada uma."); // explicação
            listarClassesDoProjeto(); // imprime as 40 classes, o pacote, o tipo e o papel de cada uma
        } // fim da Questao 1

        // Questao 2
        { // colaboradores e administrador: o que cada perfil pode fazer
            titulo("Questão 2 - Colaborador e Administrador"); // título da questão
            oQueTesta("mostra o que cada perfil pode fazer e confere que o colaborador é barrado onde só o administrador entra."); // explicação
            Colaborador paulo = funcionarios.cadastrarColaborador("Paulo Lima", "Rua B, 2", "3899990002",
                    "paulo@academia.com", "123"); // cadastra um colaborador
            mostrar("Colaborador cadastrado", paulo); // quem é o Paulo
            conferir("Colaborador acessa o financeiro?", false, paulo.podeAcessarFinanceiro()); // false
            conferir("Administrador acessa o financeiro?", true, sistema.getUsuarioLogado().podeAcessarFinanceiro()); // true
            esperarErro("colaborador lançar despesa", () -> financeiro.cadastrarDespesa(paulo, "Teste", Despesa.OUTROS, 10.0,
                    LocalDate.now())); // deve ser recusado
            Sistema outro = new Sistema(); // sistema novo, só para testar login e permissões
            esperarErro("usar os clientes sem fazer login", () -> outro.getGestaoClientes()); // exige login
            outro.criarPrimeiroAdministrador("Chefe", "Rua A", "3800000001", "chefe@academia.com", "senha1"); // primeiro acesso
            esperarErro("criar o primeiro administrador duas vezes", () -> outro.criarPrimeiroAdministrador(
                    "Outro", "Rua", "3800", "outro@academia.com", "s")); // só existe um primeiro acesso
            conferir("Login com senha errada devolve", false, outro.login("chefe@academia.com", "errada")); // false
            outro.login("chefe@academia.com", "senha1"); // entra como administrador
            outro.getGestaoFuncionarios().cadastrarColaborador("Maria Colab", "Rua B", "3800000002",
                    "colab@academia.com", "123"); // o administrador cadastra um colaborador
            outro.logout(); // sai
            outro.login("colab@academia.com", "123"); // entra como colaborador
            Funcionario colab = outro.getUsuarioLogado(); // o colaborador logado
            GestaoFinanceiro financeiroColab = outro.getGestaoFinanceiro(); // qualquer logado abre os relatórios
            esperarErro("colaborador abrir a gestão de funcionários", () -> outro.getGestaoFuncionarios()); // só administrador
            esperarErro("colaborador listar despesas", () -> financeiroColab.listarDespesas(colab)); // só administrador
            esperarErro("colaborador remover despesa", () -> financeiroColab.removerDespesa(colab, 1)); // só administrador
            esperarErro("colaborador ver o balanço", () -> financeiroColab.gerarBalancoMensal(colab, 2026, 11)); // só administrador
            esperarErro("financeiro sem gestões", () -> new GestaoFinanceiro(null, null, null)); // precisa das três partes
        } // fim da Questao 2

        // Questao 3
        { // toString sobrescrito em todas as classes
            titulo("Questão 3 - toString"); // título da questão
            oQueTesta("imprime o toString de cada classe."); // explicação
            mostrar("Sistema", sistema); // toString do Sistema
            mostrar("Estoque", lojinha.getEstoque()); // toString do Estoque
            mostrar("Catraca", sistema.getCatraca()); // toString da Catraca
            mostrar("ComparadorCliente", new ComparadorCliente(ComparadorCliente.NOME)); // toString do ComparadorCliente
            mostrar("ComparadorAgendamento", new ComparadorAgendamento()); // toString do ComparadorAgendamento
            mostrar("GestaoAgendamentos", agenda); // toString da GestaoAgendamentos
            mostrar("GestaoClientes", clientes); // toString da GestaoClientes
            mostrar("GestaoFuncionarios", funcionarios); // toString da GestaoFuncionarios
            mostrar("GestaoVendas", lojinha); // toString da GestaoVendas
            mostrar("GestaoFinanceiro", financeiro); // toString da GestaoFinanceiro
            mostrar("DAOMemoria", new DAOMemoria<Cliente>()); // toString do DAOMemoria
            mostrar("ClienteDAOJson", new ClienteDAOJson(Path.of("teste-tostring.json"))); // toString do ClienteDAOJson (não cria o arquivo)
            RepositorioJson repositorioTeste = new RepositorioJson(Path.of("teste-academia.json")); // repositório de teste
            mostrar("RepositorioJson", repositorioTeste + " | arquivo: " + repositorioTeste.getArquivo()); // toString e getArquivo
            mostrar("IteradorLista", new IteradorLista<>(clientes.listarClientes())); // toString do IteradorLista
            mostrar("ConsoleView", new ConsoleView()); // toString da ConsoleView
            mostrar("AcademiaController", new AcademiaController(sistema, new ConsoleView(), repositorioTeste)); // toString do Controller
        } // fim da Questao 3

        // Questao 4
        { // super: as subclasses chamam o construtor e o toString da classe mãe
            titulo("Questão 4 - super"); // título da questão
            oQueTesta("mostra as subclasses reaproveitando o construtor e o toString da classe mãe (super)."); // explicação
            Administrador adm = new Administrador(99, "Teste Super", "Rua C", "3899990003",
                    "super@email.com", "x"); // super(...) sobe até Pessoa
            mostrar("Administrador (\"Administrador \" + super.toString())", adm); // toString com super
            Colaborador col = new Colaborador(98, "Teste Colab", "Rua C", "3899990004", "colab@email.com", "y"); // idem
            mostrar("Colaborador (\"Colaborador \" + super.toString())", col); // toString com super
            Pessoa pessoa = new Cliente(97, "Teste Cliente", "Rua C", "3899990005", "cli@email.com", "12345678901"); // Pessoa aponta para Cliente
            mostrar("Pessoa que aponta para um Cliente (toString do Cliente)", pessoa); // escolhido em tempo de execução
            esperarErro("pessoa com ID zero", () -> new Cliente(0, "Sem ID", "Rua", "38", "s@s.com", "12345678901")); // ID inválido
            esperarErro("funcionário com ID zero", () -> new Colaborador(0, "Sem ID", "Rua", "38", "s@s.com", "x")); // ID inválido
        } // fim da Questao 4

        // Questao 5
        { // as 4 salas são static: criadas uma vez só, no Sistema
            titulo("Questão 5 - Salas static"); // título da questão
            oQueTesta("mostra as 4 salas fixas (static) e confere as validações da Sala."); // explicação
            Sala[] salas = Sistema.getSalas(); // vetor static
            for (int i = 0; i < salas.length; i++) { // vetor usa índice
                mostrar("Sala " + (i + 1), salas[i] + " | capacidade " + salas[i].getCapacidade() + " | diária R$ "
                        + salas[i].getPrecoDiaria() + " | mensalidade R$ " + salas[i].getPrecoMensalidade()); // dados da sala
            } // fim do for
            esperarErro("sala com código zero", () -> new Sala(0, Sala.PILATES, 10, 10.0, 100.0)); // código inválido
            esperarErro("sala sem capacidade", () -> new Sala(5, Sala.PILATES, 0, 10.0, 100.0)); // capacidade inválida
            esperarErro("sala com preço zero", () -> new Sala(5, Sala.PILATES, 10, 0, 100.0)); // preço inválido
            esperarErro("sala com modalidade que não existe", () -> new Sala(5, "YOGA", 10, 10.0, 100.0)); // modalidade inválida
        } // fim da Questao 5

        // Questao 6
        { // administrador cadastra, altera e remove funcionários
            titulo("Questão 6 - Cadastrar, alterar e remover funcionários"); // título da questão
            oQueTesta("cadastra, altera e remove funcionários e confere as regras (e-mail único, senha, não remover a si mesmo, último administrador)."); // explicação
            Colaborador lia = funcionarios.cadastrarColaborador("Lia Rocha", "Rua D, 4", "3899990004",
                    "lia@academia.com", "456"); // cadastra
            funcionarios.editarFuncionario(lia.getId(), "Lia Rocha Santos", "Rua D, 40", "3899990044",
                    "lia@academia.com"); // altera
            mostrar("Funcionários depois de cadastrar e alterar a Lia", funcionarios.listarFuncionarios()); // lista os funcionários
            esperarErro("cadastrar funcionário com e-mail repetido", () -> funcionarios.cadastrarColaborador("Outra", "Rua", "38",
                    "lia@academia.com", "1")); // deve ser recusado
            Funcionario paulo = funcionarios.buscarPorEmail("paulo@academia.com"); // acha o Paulo de novo
            conferir("Buscar um e-mail que não existe devolve", null, funcionarios.buscarPorEmail("ninguem@x.com")); // null
            conferir("A senha certa confere?", true, paulo.verificarSenha("123")); // true
            conferir("Trocar a senha informando a atual errada devolve", false, funcionarios.alterarSenha(paulo.getId(), "errada", "nova123")); // false
            conferir("Trocar a senha informando a atual certa devolve", true, funcionarios.alterarSenha(paulo.getId(), "123", "nova123")); // true
            conferir("A senha nova passa a valer?", true, paulo.verificarSenha("nova123")); // true
            conferir("Trocar a senha direto no funcionário devolve", true, paulo.alterarSenha("nova123", "outra456")); // true
            esperarErro("editar funcionário que não existe", () -> funcionarios.editarFuncionario(999, "X", "Rua", "38", "x@x.com")); // não existe
            esperarErro("editar com o e-mail de outro funcionário", () -> funcionarios.editarFuncionario(
                    lia.getId(), "Lia", "Rua", "38", "paulo@academia.com")); // e-mail repetido
            int idAdmin = sistema.getUsuarioLogado().getId(); // quem está logado
            esperarErro("remover o próprio usuário", () -> funcionarios.removerFuncionario(idAdmin, idAdmin)); // não pode
            esperarErro("remover o último administrador", () -> funcionarios.removerFuncionario(idAdmin, paulo.getId())); // protegido
            esperarErro("remover funcionário que não existe", () -> funcionarios.removerFuncionario(999, idAdmin)); // não existe
            funcionarios.removerFuncionario(lia.getId(), idAdmin); // remove a Lia
            Administrador bia = funcionarios.cadastrarAdministrador("Bia Admin", "Rua F, 6", "3899990006",
                    "bia@academia.com", "789"); // segundo administrador
            funcionarios.removerFuncionario(bia.getId(), idAdmin); // com dois administradores, um pode sair
            conferir("Tem funcionários?", true, funcionarios.temFuncionarios()); // true
            mostrar("Funcionários no fim", funcionarios.listarFuncionarios()); // lista final
        } // fim da Questao 6

        // Questao 7
        { // CRUD de clientes
            titulo("Questão 7 - CRUD de clientes"); // título da questão
            oQueTesta("cria, lê, altera, desativa e reativa clientes e confere as regras de CPF e de cliente que não existe."); // explicação
            Cliente bruno = clientes.cadastrarCliente("Bruno Alves", "Av. Central, 5", "38988881111",
                    "bruno@email.com", "222.222.222-22"); // create
            mostrar("Cliente cadastrado, lido pelo ID", clientes.buscarCliente(bruno.getId())); // read
            clientes.editarCliente(bruno.getId(), "Bruno Alves Costa", "Av. Central, 50", "38988881111",
                    "bruno@email.com", "222.222.222-22"); // update
            mostrar("Busca pelo nome \"bru\"", clientes.buscarPorNome("bru")); // busca pelo nome
            clientes.removerCliente(bruno.getId()); // delete (exclusão lógica)
            mostrar("Cliente depois de removido (fica desativado)", clientes.buscarCliente(bruno.getId())); // aparece desativado
            clientes.reativarCliente(bruno.getId()); // reativa o cliente
            conferir("Buscar o cliente 999 devolve", null, clientes.buscarCliente(999)); // null
            esperarErro("CPF com poucos dígitos", () -> clientes.cadastrarCliente("Cpf Curto", "Rua", "38", "c@c.com", "123")); // 11 dígitos
            esperarErro("CPF repetido", () -> clientes.cadastrarCliente("Outro", "Rua", "38", "o@o.com", "111.111.111-11")); // já existe
            esperarErro("editar cliente que não existe", () -> clientes.editarCliente(999, "X", "Rua", "38", "x@x.com", "333.333.333-33")); // não existe
            esperarErro("editar com o CPF de outro cliente", () -> clientes.editarCliente(bruno.getId(), "Bruno", "Rua",
                    "38", "bruno@email.com", "111.111.111-11")); // CPF repetido
            esperarErro("remover cliente que não existe", () -> clientes.removerCliente(999)); // não existe
            esperarErro("reativar cliente que não existe", () -> clientes.reativarCliente(999)); // não existe
            esperarErro("emitir a conta de cliente que não existe", () -> clientes.emitirConta(999)); // não existe
            esperarErro("gestão de clientes sem DAO", () -> new GestaoClientes(null)); // precisa do DAO
            esperarErro("guardar extrato nulo", () -> clientes.guardarExtrato(null)); // precisa do extrato
        } // fim da Questao 7

        // Questao 8
        { // imprimir vendas diárias, pagamentos de diárias e mensalidades e dados dos clientes
            titulo("Questão 8 - Relatórios e dados dos clientes"); // título da questão
            oQueTesta("gera os relatórios de vendas e de pagamentos (por dia e por mês) e mostra os dados dos clientes."); // explicação
            LocalDate hoje = LocalDate.now(); // dia de hoje
            VendaAgendamento pagamento = sistema.confirmarAgendamento(1, LocalDateTime.now()); // paga a diária da Ana (Questão 1)
            sistema.registrarVendaLojinha(1, List.of(1), List.of(2)); // Ana compra 2 garrafas
            RelatorioVendas relLoja = financeiro.gerarRelatorioVendasDia(hoje); // lojinha do dia
            mostrar("Relatório de vendas da lojinha do dia", relLoja); // mostra o relatório
            mostrar("Relatório de diárias e mensalidades do dia", financeiro.gerarRelatorioPagamentosDia(hoje)); // do dia
            mostrar("Relatório de vendas da lojinha do mês", financeiro.gerarRelatorioVendasMes(hoje.getYear(), hoje.getMonthValue())); // do mês
            mostrar("Relatório de diárias e mensalidades do mês", financeiro.gerarRelatorioPagamentosMes(hoje.getYear(), hoje.getMonthValue())); // do mês
            conferir("Relatórios guardados no histórico", 4, financeiro.listarRelatorios().size()); // 4 relatórios gerados acima
            mostrar("Dados do relatório da lojinha do dia", "tipo " + relLoja.getTipo() + " | de " + relLoja.getInicio() + " até " + relLoja.getFim()
                    + " | quantidade " + relLoja.getQuantidade() + " | total R$ " + relLoja.getTotal()); // dados do relatório
            mostrar("Vendas do relatório", relLoja.getVendas()); // lista de Venda
            mostrar("Dados do pagamento", "tipo " + pagamento.getTipo() + " | valor R$ " + pagamento.getValor()
                    + " | devolvido R$ " + pagamento.getValorDevolvido() + " | agendamento " + pagamento.getAgendamento().getId()); // dados do pagamento
            mostrar("Dados dos clientes", clientes.listarClientes()); // dados dos clientes
            esperarErro("relatório de tipo inválido", () -> new RelatorioVendas("OUTRO", hoje, hoje, new ArrayList<>())); // tipo inválido
            esperarErro("relatório sem período", () -> new RelatorioVendas(RelatorioVendas.LOJINHA, null, hoje, new ArrayList<>())); // período inválido
            esperarErro("relatório com fim antes do início", () -> new RelatorioVendas(RelatorioVendas.LOJINHA, hoje, hoje.minusDays(1),
                    new ArrayList<>())); // período inválido
            esperarErro("listar vendas com período invertido", () -> lojinha.listarVendas(hoje, hoje.minusDays(1))); // período inválido
            esperarErro("listar pagamentos com período invertido", () -> agenda.listarPagamentos(hoje, hoje.minusDays(1))); // período inválido
        } // fim da Questao 8

        // Questao 9
        { // listas dinâmicas: clientes, reservas e catraca crescem e diminuem durante o uso
            titulo("Questão 9 - Listas dinâmicas e catraca"); // título da questão
            oQueTesta("mostra a lista de presentes da catraca crescendo e diminuindo e os registros por dia, mês e ano."); // explicação
            mostrar("Agendamentos cadastrados", agenda.listarAgendamentos().size()); // reservas
            RegistroCatraca entrada = sistema.passarNaCatraca(1, segunda.atTime(6, 50)); // Ana entra
            mostrar("Presentes depois da entrada", sistema.getCatraca().getPresentes()); // a lista cresce
            conferir("Quantos presentes depois da entrada", 1, sistema.getCatraca().getQuantidadePresentes()); // 1
            conferir("A Ana está dentro?", true, sistema.getCatraca().estaPresente(1)); // true
            mostrar("Registro da entrada", entrada.getId() + " | " + entrada.getCliente().getNome() + " | "
                    + entrada.getTipo() + " | " + entrada.getDataHora()); // dados do registro
            RegistroCatraca saida = sistema.passarNaCatraca(1, segunda.atTime(8, 10)); // Ana sai
            mostrar("Presentes depois da saída", sistema.getCatraca().getPresentes()); // a lista diminui
            conferir("Tipo do segundo registro", RegistroCatraca.SAIDA, saida.getTipo()); // SAIDA
            conferir("A Ana está dentro depois da saída?", false, sistema.getCatraca().estaPresente(1)); // false
            mostrar("Histórico da catraca", sistema.getCatraca().getRegistros()); // histórico da catraca
            conferir("Registros do dia", 2, sistema.getCatraca().registrosDoDia(segunda).size()); // 2
            conferir("Registros do mês", 2, sistema.getCatraca().registrosDoMes(segunda.getYear(), segunda.getMonthValue()).size()); // 2
            conferir("Registros do ano", 2, sistema.getCatraca().registrosDoAno(segunda.getYear()).size()); // 2
            conferir("Registros de outro dia", 0, sistema.getCatraca().registrosDoDia(segunda.plusDays(1)).size()); // 0
            esperarErro("catraca com cliente que não existe", () -> sistema.passarNaCatraca(999, segunda.atTime(9, 0))); // não existe
            esperarErro("catraca sem cliente", () -> sistema.getCatraca().passar(null, segunda.atTime(9, 0))); // nulo
            clientes.removerCliente(1); // desativa a Ana por um instante
            esperarErro("catraca com cliente desativado", () -> sistema.passarNaCatraca(1, segunda.atTime(9, 0))); // desativado
            clientes.reativarCliente(1); // reativa a Ana
            Cliente ana = clientes.buscarCliente(1); // a Ana
            esperarErro("registro com ID zero", () -> new RegistroCatraca(0, ana, RegistroCatraca.ENTRADA, segunda.atTime(9, 0))); // ID inválido
            esperarErro("registro sem cliente", () -> new RegistroCatraca(1, null, RegistroCatraca.ENTRADA, segunda.atTime(9, 0))); // sem cliente
            esperarErro("registro de tipo inválido", () -> new RegistroCatraca(1, ana, "PASSEIO", segunda.atTime(9, 0))); // tipo inválido
        } // fim da Questao 9

        // Questao 10
        { // extrato impresso e salvo com o cliente
            titulo("Questão 10 - Extrato"); // título da questão
            oQueTesta("imprime os extratos e as vendas (cada tipo de venda responde do seu jeito) e confere as regras do extrato."); // explicação
            Cliente ana = clientes.buscarCliente(1); // a Ana
            Cliente bruno = clientes.buscarCliente(2); // o Bruno
            List<Extrato> conta = clientes.emitirConta(1); // extratos da Ana
            IteradorLista<Extrato> iterador = new IteradorLista<>(conta); // percorre os extratos
            while (iterador.hasNext()) { // enquanto houver extrato
                Extrato extrato = iterador.next(); // pega o próximo
                System.out.println(extrato.gerarTexto()); // extrato impresso
                System.out.println("  cliente: " + extrato.getCliente().getNome() + " | emitido em: "
                        + extrato.getDataEmissao() + " | descrição: " + extrato.getDescricao() + " | valor: " + extrato.getValor()); // getters
                System.out.println("  " + extrato); // toString
            } // fim do while
            conferir("Total da conta da Ana (diária 30 + 2 garrafas 30)", 60.0, clientes.calcularTotalConta(1)); // soma dos extratos
            List<Venda> todas = new ArrayList<>(lojinha.listarVendas(LocalDate.now(), LocalDate.now())); // vendas da lojinha de hoje
            todas.addAll(agenda.listarPagamentos(LocalDate.now(), LocalDate.now())); // mais os pagamentos de hoje
            IteradorLista<Venda> iteradorVendas = new IteradorLista<>(todas); // percorre como Venda (polimorfismo)
            while (iteradorVendas.hasNext()) { // enquanto houver venda
                Venda venda = iteradorVendas.next(); // pega a próxima
                System.out.println("Venda " + venda.getId() + " de " + venda.getCliente().getNome() + " em " + venda.getDataHora()
                        + " | " + venda.descrever() + " | total R$ " + venda.calcularTotal()); // cada subclasse responde do seu jeito
                System.out.println("  " + venda.gerarExtrato().gerarTexto()); // extrato da venda
                if (venda instanceof VendaProdutos vendaProdutos) { // é venda da lojinha?
                    IteradorLista<ItemVenda> iteradorItens = new IteradorLista<>(vendaProdutos.getItens()); // percorre os itens
                    while (iteradorItens.hasNext()) { // enquanto houver item
                        ItemVenda item = iteradorItens.next(); // pega o próximo
                        System.out.println("  item: " + item + " | " + item.getProduto().getNome() + " x" + item.getQuantidade()
                                + " a R$ " + item.getPrecoUnitario() + " = R$ " + item.calcularSubtotal()); // dados do item
                    } // fim do while dos itens
                } // fim do if
            } // fim do while
            esperarErro("extrato sem cliente", () -> new Extrato(null, LocalDateTime.now(), "Teste", 10.0)); // sem cliente
            esperarErro("extrato sem data", () -> new Extrato(ana, null, "Teste", 10.0)); // sem data
            esperarErro("cliente guardar extrato nulo", () -> ana.adicionarExtrato(null)); // nulo
            esperarErro("cliente guardar extrato de outro cliente", () -> ana.adicionarExtrato(
                    new Extrato(bruno, LocalDateTime.now(), "De outro", 5.0))); // outro cliente
        } // fim da Questao 10

        // Questao 11
        { // dois contadores static: (a) private com get e set; (b) protected com acesso direto
            titulo("Questão 11 - Contadores static"); // título da questão
            oQueTesta("mostra os dois contadores static: o private (com get e set) e o protected."); // explicação
            mostrar("(a) private static, lido por get", Sistema.getTotalClientes()); // contador privado
            Sistema.setTotalClientes(Sistema.getTotalClientes()); // o set aceita valor válido
            // (b) o protected static só é acessado direto dentro do pacote model (o construtor do Cliente faz Sistema.totalClientesProtegido++)
            mostrar("(b) protected static, mostrado pelo método de classe", Sistema.obterTotalInstancias()); // contador protegido
            esperarErro("(c) o setter do contador private com valor negativo", () -> Sistema.setTotalClientes(-1)); // o setter valida
        } // fim da Questao 11

        // Questao 12
        { // método de classe que conta clientes e produtos criados
            titulo("Questão 12 - Método de classe"); // título da questão
            oQueTesta("chama o método de classe que conta os clientes e os produtos criados."); // explicação
            new Produto(500, "Produto avulso", 1.0, 1); // um produto a mais (não entra no estoque)
            mostrar("Total de instâncias (método de classe, chamado pelo nome da classe)", Sistema.obterTotalInstancias()); // total
            mostrar("Produtos criados", Sistema.getTotalProdutos()); // contador de produtos
        } // fim da Questao 12

        // Questao 13
        { // Comparator para Cliente e Agendamento, por atributos diferentes
            titulo("Questão 13 - Comparators"); // título da questão
            oQueTesta("compara clientes (por nome e por CPF) e agendamentos (por dia e hora) com Comparator."); // explicação
            Cliente a = clientes.buscarCliente(1); // Ana
            Cliente b = clientes.buscarCliente(2); // Bruno
            ComparadorCliente porNome = new ComparadorCliente(ComparadorCliente.NOME); // critério nome
            conferir("Critério do comparador", ComparadorCliente.NOME, porNome.getCriterio()); // NOME
            conferir("Por nome, Ana x Bruno (negativo: Ana vem antes)", -1, Integer.signum(porNome.compare(a, b))); // negativo
            conferir("Por CPF, Ana x Ana (zero: iguais)", 0, new ComparadorCliente(ComparadorCliente.CPF).compare(a, a)); // zero
            conferir("Critério padrão do comparador", ComparadorCliente.CPF, new ComparadorCliente().getCriterio()); // CPF
            esperarErro("comparador de cliente com critério inválido", () -> new ComparadorCliente("IDADE")); // critério inválido
            Aula cedo = agenda.cadastrarAula(4, segunda.atTime(8, 0), "Carla"); // pilates segunda 8h
            Aula tarde = agenda.cadastrarAula(4, segunda.plusDays(2).atTime(8, 0), "Carla"); // pilates quarta 8h
            Agendamento ag1 = agenda.agendar(b, List.of(tarde.getId()), Agendamento.DIARIA, 35.0,
                    "5555444433332222"); // quarta
            Agendamento ag2 = agenda.agendar(b, List.of(cedo.getId()), Agendamento.DIARIA, 35.0,
                    "5555444433332222"); // segunda
            Agendamento ag3 = agenda.agendar(a, List.of(cedo.getId()), Agendamento.DIARIA, 35.0,
                    "4111111111111111"); // segunda, mesmo horário do ag2
            ComparadorAgendamento porHorario = new ComparadorAgendamento(); // critério dia e hora
            conferir("Agendamento de quarta x de segunda (positivo: quarta vem depois)", 1, Integer.signum(porHorario.compare(ag1, ag2))); // positivo
            conferir("Agendamento de segunda x de quarta (negativo)", -1, Integer.signum(porHorario.compare(ag2, ag1))); // negativo
            conferir("Dois agendamentos no mesmo horário (zero)", 0, porHorario.compare(ag2, ag3)); // zero
            mostrar("Próximo agendamento do Bruno (o de segunda)", agenda.buscarProximoAgendamento(b.getId(), LocalDate.now())); // o de segunda
            conferir("Próximo agendamento de quem não tem nenhum", null, agenda.buscarProximoAgendamento(999, LocalDate.now())); // null
        } // fim da Questao 13

        // Questao 14
        { // salvar e carregar em JSON, com alocação e desalocação segura (try/finally)
            titulo("Questão 14 - JSON"); // título da questão
            oQueTesta("salva tudo em JSON, lê de volta e testa arquivos com problema."); // explicação
            testarJson(segunda); // salva e lê tudo, e testa os arquivos com problema
        } // fim da Questao 14

        // Questao 15
        { // JavaDoc: a documentação está em cada classe, atributo e método (gerada pelo NetBeans em Run > Generate Javadoc)
            titulo("Questão 15 - JavaDoc"); // título da questão
            oQueTesta("indica onde está a documentação JavaDoc."); // explicação
            System.out.println("Ver a pasta dist/javadoc do projeto."); // onde está a documentação
        } // fim da Questao 15

        // Questao 16
        { // get e set: só existe set onde o dado pode mudar; os outros são final
            titulo("Questão 16 - get e set"); // título da questão
            oQueTesta("usa os get e set dos clientes e confere que os set recusam valores vazios."); // explicação
            Cliente ana = clientes.buscarCliente(1); // Ana
            ana.setTelefone("38911112222"); // set valida e guarda
            conferir("Telefone depois do set", "38911112222", ana.getTelefone()); // get
            ana.setNome("Ana Souza Lima"); // set do nome
            ana.setEndereco("Rua Nova, 20"); // set do endereço
            ana.setEmail("ana.lima@email.com"); // set do e-mail
            ana.setCpfPseudo("a1b2c3d4e5f60718"); // set do CPF pseudonimizado
            mostrar("Dados depois dos set", ana.getNome() + " | " + ana.getEndereco() + " | " + ana.getEmail() + " | " + ana.getCpfPseudo()); // gets
            esperarErro("e-mail vazio", () -> ana.setEmail("  ")); // o set valida
            esperarErro("nome vazio", () -> ana.setNome("")); // o set valida
            esperarErro("endereço vazio", () -> ana.setEndereco(" ")); // o set valida
            esperarErro("telefone vazio", () -> ana.setTelefone("")); // o set valida
            esperarErro("CPF vazio", () -> ana.setCpfPseudo(" ")); // o set valida
            mostrar("ID (só tem get, é final)", ana.getId()); // sem set
            ana.desativar(); // exclusão lógica
            conferir("Cliente ativo depois de desativar?", false, ana.isAtivo()); // false
            ana.ativar(); // reativa
            conferir("Cliente ativo depois de reativar?", true, ana.isAtivo()); // true
            mostrar("Extratos guardados no cliente", ana.getExtratos().size()); // quantidade
        } // fim da Questao 16

        // Questao 17
        { // testes das funções: lojinha, estoque, agenda, financeiro e classes de apoio, com os casos que devem ser recusados
            titulo("Questão 17 - Testes das funções"); // título da questão
            oQueTesta("testa as funções da lojinha, do estoque, da agenda, do financeiro e das classes de apoio."); // explicação
            subtitulo("Funções básicas da lojinha e da agenda"); // grupo
            mostrar("Consulta do produto 1", lojinha.verificarProduto(1)); // consulta da lojinha
            lojinha.reporEstoque(1, 5); // reposição de estoque
            conferir("Vagas na aula 1 (20 vagas, 1 reserva)", 19, agenda.verificarVagas(1)); // vagas
            conferir("Agendamentos cancelados por prazo vencido hoje", 0, agenda.cancelarVencidos(LocalDate.now())); // nenhum
            Despesa luz = financeiro.cadastrarDespesa(sistema.getUsuarioLogado(), "Conta de luz",
                    Despesa.OUTROS, 300.0, LocalDate.now()); // despesa lançada pelo administrador
            mostrar("Despesa lançada pelo administrador", luz); // mostra a despesa
            mostrar("Balanço do mês", financeiro.gerarBalancoMensal(sistema.getUsuarioLogado(),
                    LocalDate.now().getYear(), LocalDate.now().getMonthValue())); // balanço do mês
            subtitulo("Lojinha e estoque"); // grupo
            testarLojinhaEEstoque(); // produtos, estoque e vendas
            subtitulo("Agenda, agendamentos e devoluções"); // grupo
            testarAgenda(segunda); // aulas, grade, agendamentos, devoluções e vagas
            subtitulo("Financeiro"); // grupo
            testarFinanceiro(); // despesas, relatórios e balanço
            subtitulo("Classes de apoio (validação, iterador, DAO e exceção)"); // grupo
            testarClassesDeApoio(); // validação, iterador, DAO e exceção
        } // fim da Questao 17

        // Questao 18
        { // Iterator na ArrayList de clientes e a relação com o foreach
            titulo("Questão 18 - Iterator e foreach"); // título da questão
            oQueTesta("percorre a lista de clientes com o iterador (while) e com foreach."); // explicação
            List<Cliente> lista = clientes.listarClientes(); // a ArrayList de clientes
            Iterator<Cliente> iterator = new IteradorLista<>(lista); // nosso iterador (implements Iterator)
            while (iterator.hasNext()) { // hasNext: ainda tem cliente na próxima posição?
                System.out.println(iterator.next()); // next: devolve o cliente da posição e anda uma posição
            } // fim do while
            System.out.println("--- foreach ---"); // separa os dois modos
            for (Cliente cliente : lista) { // foreach: o Java chama lista.iterator() e faz o mesmo while com hasNext e next
                System.out.println(cliente); // mostra o cliente
            } // fim do for
        } // fim da Questao 18

        // Questao 19
        { // Collections.sort com o comparator, rodando com dois critérios
            titulo("Questão 19 - Collections.sort com comparator"); // título da questão
            oQueTesta("ordena os clientes duas vezes, por nome e por CPF."); // explicação
            List<Cliente> lista = clientes.listarClientes(); // cópia da lista de clientes
            Collections.sort(lista, new ComparadorCliente(ComparadorCliente.NOME)); // 1ª vez: por nome
            System.out.println("Por nome:"); // título da lista
            imprimir(lista); // mostra nome e CPF
            Collections.sort(lista, new ComparadorCliente(ComparadorCliente.CPF)); // 2ª vez: por CPF
            System.out.println("Por CPF:"); // título da lista
            imprimir(lista); // mostra nome e CPF
        } // fim da Questao 19

        // Questao 21
        { // cenário: atendimento de 10 clientes, cada um com um caminho diferente
            titulo("Questão 21 - Cenário com 10 clientes"); // título da questão
            oQueTesta("atende 10 clientes, cada um por um caminho diferente, e termina com os extratos."); // explicação
            cenarioDezClientes(segunda); // roda os 10 atendimentos
        } // fim da Questao 21

        // Teste do menu (MVC)
        { // simula uma pessoa usando o menu: a View lê de um texto combinado em vez do teclado
            titulo("Teste do menu (View e Controller)"); // título do teste
            testarMenu(segunda); // percorre todos os menus
        } // fim do teste do menu

        System.out.println("\n========== Resumo dos testes =========="); // fecha a saída
        System.out.println("Valores conferidos certos: " + conferidosCertos + " | Recusas esperadas certas: " + recusasEsperadas
                + " | FALHAS: " + falhas); // resumo
        System.out.println(falhas == 0 ? "Tudo certo: nenhum teste falhou." : "Atenção: há testes com [FALHA] acima."); // conclusão
    } // fim de executarQuestoes

    /**
     * Executa uma ação que deve ser recusada com uma exceção e mostra o motivo.
     * Se a ação não for recusada, conta como falha.
     *
     * @param descricao o que está sendo testado
     * @param acao a chamada que deve ser recusada
     */
    private static void esperarErro(String descricao, Runnable acao) { // confere que a regra recusou
        try { // a ação deve falhar
            acao.run(); // executa a chamada
            falhas++; // não falhou: é um problema
            System.out.println("  [FALHA] deveria recusar: " + descricao); // avisa
        } catch (RuntimeException e) { // recusou, como esperado
            recusasEsperadas++; // conta mais uma
            System.out.println("  [OK] recusou \"" + descricao + "\" -> motivo: " + e.getMessage()); // mostra o motivo
        } // fim do try
    } // fim de esperarErro

    /**
     * Compara o valor esperado com o obtido e mostra os dois.
     * Se forem diferentes, conta como falha.
     *
     * @param descricao o que está sendo conferido
     * @param esperado o valor que deveria sair
     * @param obtido o valor que realmente saiu
     */
    private static void conferir(String descricao, Object esperado, Object obtido) { // confere um valor
        if (java.util.Objects.equals(esperado, obtido)) { // bateu?
            conferidosCertos++; // conta mais um certo
            System.out.println("  [OK] " + descricao + " -> esperado: " + esperado + " | obtido: " + obtido); // mostra os dois
        } else { // não bateu
            falhas++; // é um problema
            System.out.println("  [FALHA] " + descricao + " -> esperado: " + esperado + " | obtido: " + obtido); // mostra os dois
        } // fim do if
    } // fim de conferir

    /**
     * Mostra um resultado só para o leitor ver (sem conferir).
     *
     * @param legenda o nome do que está sendo mostrado
     * @param valor o valor a mostrar
     */
    private static void mostrar(String legenda, Object valor) { // mostra um resultado com legenda
        System.out.println("  " + legenda + ": " + valor); // legenda e valor
    } // fim de mostrar

    /**
     * Mostra um título menor, que separa grupos de testes dentro de uma questão.
     *
     * @param texto o nome do grupo
     */
    private static void subtitulo(String texto) { // separa grupos de testes
        System.out.println("\n-- " + texto + " --"); // linha de subtítulo
    } // fim de subtitulo

    /**
     * Explica em uma linha o que a questão testa.
     *
     * @param texto o que o teste faz
     */
    private static void oQueTesta(String texto) { // explica o teste
        System.out.println("O que este teste faz: " + texto); // linha de explicação
    } // fim de oQueTesta

    /**
     * Cria um sistema só na memória, já com o administrador logado, para os testes.
     *
     * @return o sistema pronto para usar
     */
    private static Sistema novoSistemaDeTeste() { // sistema de teste com administrador logado
        Sistema s = new Sistema(new DAOMemoria<>()); // clientes só na memória
        s.criarPrimeiroAdministrador("Aline Ferreira", "Diamantina, MG", "3800000000",
                "aline.pena@ufvjm.edu.br", "Isabella18@"); // primeiro acesso
        s.login("aline.pena@ufvjm.edu.br", "Isabella18@"); // entra
        return s; // devolve o sistema
    } // fim de novoSistemaDeTeste

    /**
     * Questão 14: grava o sistema inteiro em JSON, lê de volta e testa os
     * arquivos com problema (texto quebrado, pasta que não existe, dados que não batem).
     *
     * @param segunda uma segunda-feira futura, usada nas datas das aulas
     */
    private static void testarJson(LocalDate segunda) { // salvar e carregar em JSON
        Path arqClientes = Path.of("teste-clientes.json"); // arquivo de teste dos clientes
        Path arqAcademia = Path.of("teste-academia.json"); // arquivo de teste do resto
        apagar(arqClientes); // começa limpo
        apagar(arqAcademia); // começa limpo
        Sistema emArquivo = new Sistema(new ClienteDAOJson(arqClientes)); // clientes gravados em arquivo
        emArquivo.criarPrimeiroAdministrador("Aline Ferreira", "Diamantina, MG", "3800000000",
                "aline.pena@ufvjm.edu.br", "Isabella18@"); // administrador do teste
        emArquivo.login("aline.pena@ufvjm.edu.br", "Isabella18@"); // entra
        GestaoClientes clientes = emArquivo.getGestaoClientes(); // atalho
        GestaoAgendamentos agenda = emArquivo.getGestaoAgendamentos(); // atalho
        GestaoVendas lojinha = emArquivo.getGestaoVendas(); // atalho
        GestaoFinanceiro financeiro = emArquivo.getGestaoFinanceiro(); // atalho
        Cliente clara = clientes.cadastrarCliente("Clara Dias", "Rua E, 5", "3897770000", "clara@email.com", "333.333.333-33"); // cliente
        clientes.cadastrarCliente("Davi Melo", "Rua F, 6", "3897771111", "davi@email.com", "444.444.444-44"); // segundo cliente
        clientes.removerCliente(2); // o Davi fica desativado no arquivo
        lojinha.cadastrarProduto("Toalha", 20.0, 5); // produto
        emArquivo.registrarVendaLojinha(clara.getId(), List.of(1), List.of(1)); // venda com extrato
        Aula aula1 = agenda.cadastrarAula(1, segunda.atTime(7, 0), "Rafael"); // aula 1
        Aula aula2 = agenda.cadastrarAula(1, segunda.plusDays(1).atTime(7, 0), "Rafael"); // aula 2
        Agendamento pago = agenda.agendar(clara, List.of(aula1.getId(), aula2.getId()), Agendamento.MENSALIDADE,
                150.0, "4111111111111111"); // mensalidade com 2 aulas
        emArquivo.confirmarAgendamento(pago.getId(), LocalDateTime.now()); // pagamento e extrato
        emArquivo.cancelarAgendamento(pago.getId(), LocalDate.now()); // cancela e devolve 50%
        agenda.agendar(clara, List.of(aula1.getId()), Agendamento.DIARIA, 30.0, "4111111111111111"); // ficará preliminar
        financeiro.cadastrarDespesa(emArquivo.getUsuarioLogado(), "Material de limpeza", Despesa.LIMPEZA, 80.0, LocalDate.now()); // despesa
        financeiro.gerarRelatorioVendasDia(LocalDate.now()); // relatório da lojinha
        financeiro.gerarRelatorioPagamentosDia(LocalDate.now()); // relatório das diárias
        emArquivo.passarNaCatraca(clara.getId(), segunda.atTime(6, 55)); // registro da catraca
        emArquivo.getGestaoFuncionarios().cadastrarColaborador("Paulo Lima", "Rua B, 2", "3899990002", "paulo@academia.com", "123"); // colaborador
        RepositorioJson repositorio = new RepositorioJson(arqAcademia); // repositório do teste
        emArquivo.salvar(repositorio); // grava o academia.json
        Sistema lido = new Sistema(new ClienteDAOJson(arqClientes)); // outro sistema: lê o clientes.json
        lido.carregar(repositorio); // lê o academia.json
        subtitulo("Salvou nos arquivos; agora um sistema novo lê de volta"); // grupo
        conferir("Login do administrador lido do arquivo", true, lido.login("aline.pena@ufvjm.edu.br", "Isabella18@")); // voltou
        mostrar("Clientes lidos", lido.getGestaoClientes().listarClientes()); // clientes do arquivo
        mostrar("Extratos da Clara lidos", lido.getGestaoClientes().emitirConta(clara.getId())); // extratos do arquivo
        mostrar("Estoque lido", lido.getGestaoVendas().getEstoque().getProdutos()); // estoque do arquivo
        conferir("Aulas lidas do arquivo", 2, lido.getGestaoAgendamentos().listarAulas().size()); // aulas
        mostrar("Agendamentos lidos", lido.getGestaoAgendamentos().listarAgendamentos()); // agendamentos
        mostrar("Despesas lidas", lido.getGestaoFinanceiro().listarDespesas(lido.getUsuarioLogado())); // despesas
        conferir("Relatórios lidos do arquivo", 2, lido.getGestaoFinanceiro().listarRelatorios().size()); // relatórios
        mostrar("Registros da catraca lidos", lido.getCatraca().getRegistros()); // catraca
        conferir("Funcionários lidos do arquivo", 2, lido.getGestaoFuncionarios().listarFuncionarios().size()); // funcionários

        subtitulo("Arquivos com problema"); // casos de erro
        esperarErro("repositório sem arquivo", () -> new RepositorioJson(null)); // arquivo nulo
        esperarErro("DAO de clientes sem arquivo", () -> new ClienteDAOJson(null)); // arquivo nulo
        Path ruim = Path.of("teste-ruim.json"); // arquivo com texto que não é JSON
        gravar(ruim, "isto não é json"); // escreve o texto quebrado
        esperarErro("ler clientes de um arquivo quebrado", () -> new ClienteDAOJson(ruim)); // JSON inválido
        esperarErro("ler academia de um arquivo quebrado", () -> new RepositorioJson(ruim).carregar(
                emArquivo.getGestaoClientes(), emArquivo.getGestaoFuncionarios(), emArquivo.getGestaoAgendamentos(),
                emArquivo.getGestaoVendas(), emArquivo.getGestaoFinanceiro(), emArquivo.getCatraca())); // JSON inválido
        esperarErro("ler academia de um caminho que é pasta", () -> new RepositorioJson(Path.of(".")).carregar(
                emArquivo.getGestaoClientes(), emArquivo.getGestaoFuncionarios(), emArquivo.getGestaoAgendamentos(),
                emArquivo.getGestaoVendas(), emArquivo.getGestaoFinanceiro(), emArquivo.getCatraca())); // não é arquivo
        esperarErro("gravar academia numa pasta que não existe", () -> new RepositorioJson(Path.of("pasta-que-nao-existe/a.json")).salvar(
                emArquivo.getGestaoClientes(), emArquivo.getGestaoFuncionarios(), emArquivo.getGestaoAgendamentos(),
                emArquivo.getGestaoVendas(), emArquivo.getGestaoFinanceiro(), emArquivo.getCatraca())); // não grava
        esperarErro("gravar cliente numa pasta que não existe", () -> new ClienteDAOJson(Path.of("pasta-que-nao-existe/c.json"))
                .inserir(clara)); // não grava
        ClienteDAOJson daoJson = new ClienteDAOJson(arqClientes); // DAO com os clientes do teste
        esperarErro("gravar cliente sem ID", () -> daoJson.inserir(new Cliente())); // sem ID
        esperarErro("atualizar cliente sem ID", () -> daoJson.atualizar(new Cliente())); // sem ID
        conferir("Remover pelo DAO um cliente que existe devolve", true, daoJson.remover(2)); // true
        conferir("Remover pelo DAO um cliente que não existe devolve", false, daoJson.remover(999)); // false
        mostrar("Próximo ID livre", daoJson.gerarId()); // ID novo
        mostrar("Buscar o cliente 1 pelo DAO", daoJson.buscarPorId(1)); // busca

        String texto = lerTexto(arqAcademia); // o academia.json salvo
        Path semSala = Path.of("teste-sem-sala.json"); // arquivo com uma sala que não existe
        gravar(semSala, texto.replace("\"sala\": 1", "\"sala\": 9")); // troca a sala 1 pela sala 9
        esperarErro("ler aula de uma sala que não existe", () -> new Sistema(new ClienteDAOJson(arqClientes))
                .carregar(new RepositorioJson(semSala))); // sala inválida
        Path ruim2 = Path.of("teste-dados-ruins.json"); // arquivo com dados que não batem
        gravar(ruim2, texto.replace("\"cartao\": \"**** 1111\"", "\"cartao\": \"1111\"")); // cartão sem máscara
        esperarErro("ler agendamento com cartão sem máscara", () -> new Sistema(new ClienteDAOJson(arqClientes))
                .carregar(new RepositorioJson(ruim2))); // cartão inválido
        gravar(ruim2, texto.replace("\"status\": \"PRELIMINAR\"", "\"status\": \"ATRASADO\"")); // situação que não existe
        esperarErro("ler agendamento com situação que não existe", () -> new Sistema(new ClienteDAOJson(arqClientes))
                .carregar(new RepositorioJson(ruim2))); // situação inválida
        JSONObject raiz = new JSONObject(texto); // o arquivo salvo, como objeto
        raiz.getJSONArray("registrosCatraca").getJSONObject(0).put("cliente", 99); // aponta para um cliente que não existe
        gravar(ruim2, raiz.toString(2)); // grava o arquivo alterado
        esperarErro("ler registro da catraca de um cliente que não existe", () -> new Sistema(new ClienteDAOJson(arqClientes))
                .carregar(new RepositorioJson(ruim2))); // cliente inexistente
        apagar(ruim); // limpa os arquivos de teste
        apagar(ruim2); // limpa os arquivos de teste
        apagar(semSala); // limpa os arquivos de teste
    } // fim de testarJson

    /**
     * Questão 17: testa produtos, estoque e vendas da lojinha, incluindo as recusas.
     */
    private static void testarLojinhaEEstoque() { // testes da lojinha
        Sistema s = novoSistemaDeTeste(); // sistema só para este teste
        GestaoVendas loja = s.getGestaoVendas(); // a lojinha
        Cliente cliente = s.getGestaoClientes().cadastrarCliente("Teste Loja", "Rua 1", "3800", "t@t.com", "000.000.000-01"); // cliente
        Produto garrafa = loja.cadastrarProduto("Garrafa Térmica", 30.0, 10); // produto 1
        Produto toalha = loja.cadastrarProduto("Toalha Azul", 20.0, 4); // produto 2
        Estoque estoque = loja.getEstoque(); // o estoque
        mostrar("Busca por \"gar\" (começo da primeira palavra)", estoque.buscarPorNome("gar")); // começo da primeira palavra
        mostrar("Busca por \"azul\" (começo da segunda palavra)", estoque.buscarPorNome("azul")); // começo da segunda palavra
        mostrar("Busca por \"xyz\" (nada)", estoque.buscarPorNome("xyz")); // nada
        conferir("Tem 10 garrafas em estoque?", true, estoque.verificarDisponibilidade(garrafa.getId(), 10)); // true
        conferir("Tem 11 garrafas em estoque?", false, estoque.verificarDisponibilidade(garrafa.getId(), 11)); // false
        estoque.reporEstoque(garrafa.getId(), 5); // chegaram 5
        estoque.darBaixa(garrafa.getId(), 3); // saíram 3
        conferir("Garrafas em estoque depois de repor 5 e dar baixa em 3", 12, estoque.buscarProduto(garrafa.getId()).getQuantidade()); // 12
        conferir("Buscar um produto que não existe devolve", null, estoque.buscarProduto(999)); // null
        esperarErro("dar baixa acima do estoque", () -> estoque.darBaixa(toalha.getId(), 99)); // insuficiente
        esperarErro("repor quantidade zero", () -> estoque.reporEstoque(garrafa.getId(), 0)); // quantidade inválida
        esperarErro("repor produto que não existe", () -> estoque.reporEstoque(999, 1)); // não existe
        esperarErro("verificar quantidade negativa", () -> estoque.verificarDisponibilidade(garrafa.getId(), -1)); // inválida
        esperarErro("guardar produto nulo no estoque", () -> estoque.adicionarProduto(null)); // nulo
        esperarErro("buscar por nome vazio", () -> estoque.buscarPorNome("  ")); // vazio
        esperarErro("consultar produto que não existe", () -> loja.verificarProduto(999)); // não existe
        esperarErro("remover produto que não existe", () -> loja.removerProduto(999)); // não existe
        esperarErro("repor em produto que não existe pela lojinha", () -> loja.reporEstoque(999, 1)); // não existe
        esperarErro("venda sem cliente", () -> loja.registrarVenda(null, List.of(1), List.of(1))); // sem cliente
        esperarErro("venda sem produtos", () -> loja.registrarVenda(cliente, new ArrayList<>(), new ArrayList<>())); // vazia
        esperarErro("venda com listas de tamanhos diferentes", () -> loja.registrarVenda(cliente, List.of(1, 2), List.of(1))); // diferentes
        esperarErro("venda com o mesmo produto duas vezes", () -> loja.registrarVenda(cliente, List.of(1, 1), List.of(1, 1))); // repetido
        esperarErro("venda de produto que não existe", () -> loja.registrarVenda(cliente, List.of(999), List.of(1))); // não existe
        esperarErro("venda acima do estoque", () -> loja.registrarVenda(cliente, List.of(toalha.getId()), List.of(50))); // insuficiente
        VendaProdutos venda = loja.registrarVenda(cliente, List.of(garrafa.getId(), toalha.getId()), List.of(2, 1)); // venda certa
        mostrar("Venda registrada", venda); // a venda
        conferir("Toalhas que sobraram depois de vender 1 (eram 4)", 3, toalha.getQuantidade()); // 3 sobraram
        conferir("Remover a toalha do estoque devolve", true, estoque.removerProduto(toalha.getId())); // true
        loja.removerProduto(garrafa.getId()); // remove pela lojinha
        mostrar("Resumo da lojinha", loja); // resumo
        esperarErro("produto sem nome", () -> new Produto(1, "", 10.0, 1)); // nome vazio
        esperarErro("produto com preço zero", () -> new Produto(1, "Teste", 0, 1)); // preço inválido
        esperarErro("produto com quantidade negativa", () -> new Produto(1, "Teste", 10.0, -1)); // quantidade inválida
        esperarErro("item sem produto", () -> new ItemVenda(null, 1)); // sem produto
        esperarErro("item sem produto, com preço informado", () -> new ItemVenda(null, 1, 10.0)); // sem produto
        esperarErro("item com quantidade zero", () -> new ItemVenda(garrafa, 0)); // quantidade inválida
        esperarErro("item com preço zero", () -> new ItemVenda(garrafa, 1, 0)); // preço inválido
        esperarErro("venda de produtos com ID zero", () -> new VendaProdutos(0, cliente, LocalDateTime.now())); // ID inválido
        esperarErro("venda de produtos sem cliente", () -> new VendaProdutos(1, null, LocalDateTime.now())); // sem cliente
        esperarErro("venda de produtos sem data", () -> new VendaProdutos(1, cliente, null)); // sem data
        esperarErro("adicionar item nulo à venda", () -> venda.adicionarItem(null)); // item nulo
    } // fim de testarLojinhaEEstoque

    /**
     * Questão 17: testa aulas, grade semanal, agendamentos, confirmações, devoluções e vagas.
     *
     * @param segunda uma segunda-feira futura, usada nas datas das aulas
     */
    private static void testarAgenda(LocalDate segunda) { // testes da agenda
        Sistema s = novoSistemaDeTeste(); // sistema só para este teste
        GestaoAgendamentos ag = s.getGestaoAgendamentos(); // a agenda
        GestaoClientes c = s.getGestaoClientes(); // os clientes
        Cliente ana = c.cadastrarCliente("Ana Agenda", "Rua 1", "3800", "ana@a.com", "000.000.000-11"); // cliente
        Aula aula1 = ag.cadastrarAula(1, segunda.atTime(7, 0), "Rafael"); // segunda 7h
        Aula aula2 = ag.cadastrarAula(1, segunda.plusDays(1).atTime(7, 0), "Rafael"); // terça 7h
        Aula pilates = ag.cadastrarAula(4, segunda.atTime(8, 0), "Carla"); // outra sala
        mostrar("Aula buscada", ag.buscarAula(aula1.getId()) + " | " + aula1.getModalidade() + " | instrutor " + aula1.getNomeInstrutor()); // dados
        conferir("Aulas cadastradas", 3, ag.listarAulas().size()); // 3

        AgendaSemanal grade = ag.consultarAgendaSemanal(1, segunda); // grade da semana
        mostrar("Grade da semana", grade); // mostra a grade
        mostrar("Dados da grade", "semana começa em " + grade.getInicioSemana() + " | sala " + grade.getSala().getCodigo()
                + " | contém a segunda? " + grade.contem(segunda.atTime(7, 0)) + " | contém daqui a 2 semanas? "
                + grade.contem(segunda.plusWeeks(2).atTime(7, 0))); // dados da grade
        mostrar("Posições da grade", "aula na segunda 7h: " + grade.getAula(0, 2) + " | livre? " + grade.estaLivre(0, 2)
                + " | terça 7h livre? " + grade.estaLivre(1, 2) + " | aulas na grade: " + grade.listarAulas().size()); // posições
        esperarErro("posição fora da grade", () -> grade.estaLivre(9, 99)); // fora da grade
        Sala sala1 = Sistema.getSalas()[0]; // spinning
        esperarErro("agenda sem sala", () -> new AgendaSemanal(segunda, null)); // sem sala
        esperarErro("agenda sem data", () -> new AgendaSemanal(null, sala1)); // sem data
        esperarErro("adicionar aula nula na grade", () -> grade.adicionarAula(null)); // nula
        esperarErro("adicionar na grade aula de outra sala", () -> grade.adicionarAula(
                new Aula(70, Sistema.getSalas()[1], segunda.atTime(9, 0), "Y"))); // outra sala
        esperarErro("adicionar na grade aula de outra semana", () -> grade.adicionarAula(
                new Aula(71, sala1, segunda.plusWeeks(1).atTime(9, 0), "Y"))); // outra semana
        esperarErro("adicionar na grade aula em horário ocupado", () -> grade.adicionarAula(
                new Aula(72, sala1, segunda.atTime(7, 0), "Z"))); // ocupado
        esperarErro("cadastrar aula em horário ocupado", () -> ag.cadastrarAula(1, segunda.atTime(7, 0), "Outro")); // ocupado
        esperarErro("cadastrar aula no domingo", () -> ag.cadastrarAula(1, segunda.plusDays(6).atTime(7, 0), "X")); // domingo
        esperarErro("cadastrar aula às 23h", () -> ag.cadastrarAula(1, segunda.atTime(23, 0), "X")); // fora do horário
        esperarErro("cadastrar aula às 7h30", () -> ag.cadastrarAula(1, segunda.atTime(7, 30), "X")); // hora não cheia
        esperarErro("cadastrar aula em sala que não existe", () -> ag.cadastrarAula(9, segunda.atTime(9, 0), "X")); // sala inválida
        esperarErro("buscar aula que não existe", () -> ag.buscarAula(999)); // não existe
        esperarErro("aula com ID zero", () -> new Aula(0, sala1, segunda.atTime(9, 0), "X")); // ID inválido
        esperarErro("aula sem sala", () -> new Aula(1, null, segunda.atTime(9, 0), "X")); // sem sala
        esperarErro("aula sem data", () -> new Aula(1, sala1, null, "X")); // sem data
        Aula solta = new Aula(73, sala1, segunda.atTime(10, 0), "Solta"); // aula fora do sistema
        grade.adicionarAula(solta); // entra na grade
        conferir("Tirar a aula da grade devolve", true, grade.removerAula(solta)); // true
        conferir("Tirar a mesma aula de novo devolve", false, grade.removerAula(solta)); // false

        conferir("Preço sugerido da diária de 2 aulas de spinning (2 x 30)", 60.0, ag.calcularPrecoSugerido(List.of(aula1.getId(), aula2.getId()), Agendamento.DIARIA)); // 60
        conferir("Preço sugerido da mensalidade de spinning", 150.0, ag.calcularPrecoSugerido(List.of(aula1.getId()), Agendamento.MENSALIDADE)); // 150
        esperarErro("preço sugerido com tipo inválido", () -> ag.calcularPrecoSugerido(List.of(aula1.getId()), "SEMANAL")); // tipo inválido
        esperarErro("agendar sem aulas", () -> ag.agendar(ana, new ArrayList<>(), Agendamento.DIARIA, 30.0, "4111111111111111")); // sem aulas
        esperarErro("agendar sem cliente", () -> ag.agendar(null, List.of(aula1.getId()), Agendamento.DIARIA, 30.0, "4111111111111111")); // sem cliente
        esperarErro("agendar aulas de salas diferentes", () -> ag.agendar(ana, List.of(aula1.getId(), pilates.getId()),
                Agendamento.DIARIA, 30.0, "4111111111111111")); // salas diferentes
        esperarErro("agendar com tipo de cobrança inválido", () -> ag.agendar(ana, List.of(aula1.getId()), "SEMANAL", 30.0, "4111111111111111")); // tipo
        esperarErro("agendar com cartão inválido", () -> ag.agendar(ana, List.of(aula1.getId()), Agendamento.DIARIA, 30.0, "12")); // cartão
        esperarErro("agendar com preço zero", () -> ag.agendar(ana, List.of(aula1.getId()), Agendamento.DIARIA, 0, "4111111111111111")); // preço
        esperarErro("agendar aula que não existe", () -> ag.agendar(ana, List.of(999), Agendamento.DIARIA, 30.0, "4111111111111111")); // não existe

        Agendamento diaria = ag.agendar(ana, List.of(aula1.getId()), Agendamento.DIARIA, 30.0, "4111 1111 1111 1111"); // reserva preliminar
        mostrar("Agendamento criado", diaria + " | cartão " + diaria.getCartaoMascarado() + " | " + diaria.getTipoCobranca() + " | R$ " + diaria.getPreco()
                + " | sala " + diaria.getSala().getCodigo() + " | primeira aula " + diaria.getPrimeiraDataHora() + " | prazo " + diaria.calcularPrazoConfirmacao()); // dados
        conferir("Vagas na aula 1 (20 vagas, 1 reserva)", 19, ag.verificarVagas(aula1.getId())); // 19
        mostrar("Próximo agendamento da Ana", ag.buscarProximoAgendamento(ana.getId(), LocalDate.now())); // a diária
        conferir("Cancelar vencidos hoje (prazo ainda não venceu)", 0, ag.cancelarVencidos(LocalDate.now())); // 0
        conferir("Cancelar vencidos depois do prazo", 1, ag.cancelarVencidos(diaria.calcularPrazoConfirmacao().plusDays(1))); // 1
        conferir("Situação do agendamento vencido", Agendamento.CANCELADO, diaria.getStatus()); // CANCELADO
        conferir("Cancelar de novo um vencido devolve", false, diaria.cancelarSeVencido(LocalDate.now())); // false
        esperarErro("confirmar agendamento já cancelado", () -> s.confirmarAgendamento(diaria.getId(), LocalDateTime.now())); // só preliminar
        esperarErro("cancelar agendamento duas vezes", () -> s.cancelarAgendamento(diaria.getId(), LocalDate.now())); // já cancelado
        esperarErro("confirmar agendamento que não existe", () -> s.confirmarAgendamento(999, LocalDateTime.now())); // não existe
        esperarErro("cancelar agendamento que não existe", () -> s.cancelarAgendamento(999, LocalDate.now())); // não existe

        Agendamento fora = ag.agendar(ana, List.of(aula2.getId()), Agendamento.DIARIA, 30.0, "4111111111111111"); // outra reserva
        esperarErro("confirmar depois do prazo", () -> s.confirmarAgendamento(fora.getId(),
                fora.calcularPrazoConfirmacao().plusDays(1).atTime(10, 0))); // fora do prazo: o agendamento se cancela
        conferir("Situação do agendamento confirmado fora do prazo", Agendamento.CANCELADO, fora.getStatus()); // CANCELADO

        Agendamento mensal = ag.agendar(ana, List.of(aula1.getId()), Agendamento.MENSALIDADE, 150.0, "4111111111111111"); // mensalidade
        VendaAgendamento pagamento = s.confirmarAgendamento(mensal.getId(), LocalDateTime.now()); // confirma e paga
        esperarErro("registrar devolução de agendamento que não está cancelado", () -> pagamento.registrarDevolucao(10.0)); // não cancelado
        double devolvido = s.cancelarAgendamento(mensal.getId(), mensal.getPrimeiraData().minusDays(10)); // cancela com antecedência
        conferir("Devolvido ao cancelar com antecedência (50% de 150)", 75.0, devolvido); // 75
        conferir("Valor devolvido registrado no pagamento", 75.0, pagamento.getValorDevolvido()); // 75
        esperarErro("registrar a devolução duas vezes", () -> pagamento.registrarDevolucao(10.0)); // já registrada

        Agendamento tarde = ag.agendar(ana, List.of(aula1.getId()), Agendamento.DIARIA, 30.0, "4111111111111111"); // outra reserva
        VendaAgendamento pagamento2 = s.confirmarAgendamento(tarde.getId(), LocalDateTime.now()); // confirma e paga
        conferir("Devolvido ao cancelar em cima da hora", 0.0, s.cancelarAgendamento(tarde.getId(), tarde.getPrimeiraData())); // 0
        esperarErro("devolução com valor negativo", () -> pagamento2.registrarDevolucao(-1)); // inválida
        esperarErro("devolução maior que o pagamento", () -> pagamento2.registrarDevolucao(pagamento2.getValor() + 1)); // inválida
        conferir("Pagamentos registrados hoje", 2, ag.listarPagamentos(LocalDate.now(), LocalDate.now()).size()); // 2

        Cliente clienteSemAgenda = c.cadastrarCliente("Sem Agenda", "Rua 2", "3801", "s@a.com", "000.000.000-12"); // cliente sem reservas
        conferir("Próximo agendamento de quem não tem reserva", null, ag.buscarProximoAgendamento(clienteSemAgenda.getId(), LocalDate.now())); // null
        c.removerCliente(clienteSemAgenda.getId()); // desativa
        esperarErro("agendar com cliente desativado", () -> ag.agendar(clienteSemAgenda, List.of(aula1.getId()),
                Agendamento.DIARIA, 30.0, "4111111111111111")); // desativado

        LocalDate amanha = LocalDate.now().plusDays(1); // amanhã
        if (amanha.getDayOfWeek() == DayOfWeek.SUNDAY) { // a academia não abre no domingo
            amanha = amanha.plusDays(1); // vai para segunda
        } // fim do if
        Aula proxima = ag.cadastrarAula(2, amanha.atTime(10, 0), "Léo"); // aula daqui a pouco
        esperarErro("agendar com o prazo de confirmação já vencido", () -> ag.agendar(ana, List.of(proxima.getId()),
                Agendamento.DIARIA, 25.0, "4111111111111111")); // prazo passou

        Aula lotada = ag.cadastrarAula(4, segunda.plusDays(2).atTime(9, 0), "Carla"); // pilates, 15 vagas
        int capacidade = lotada.getSala().getCapacidade(); // 15
        Agendamento ultimo = null; // o último agendamento feito
        for (int i = 0; i < capacidade; i++) { // vetor usa índice: enche a aula
            Cliente aluno = c.cadastrarCliente("Aluno " + i, "Rua " + i, "38" + i, "aluno" + i + "@a.com", String.format("500.000.000-%02d", i)); // cliente
            ultimo = ag.agendar(aluno, List.of(lotada.getId()), Agendamento.DIARIA, 35.0, "4111111111111111"); // ocupa uma vaga
        } // fim do for
        conferir("Vagas depois de encher a aula (15 de 15)", 0, ag.verificarVagas(lotada.getId())); // 0
        esperarErro("agendar em horário lotado", () -> ag.agendar(ana, List.of(lotada.getId()), Agendamento.DIARIA, 35.0, "4111111111111111")); // lotado
        s.cancelarAgendamento(ultimo.getId(), LocalDate.now()); // libera uma vaga
        conferir("Vagas depois de cancelar uma reserva", 1, ag.verificarVagas(lotada.getId())); // 1

        esperarErro("remover aula que tem agendamentos", () -> ag.removerAula(lotada.getId())); // tem reservas
        esperarErro("remover aula que não existe", () -> ag.removerAula(999)); // não existe
        Aula livre = ag.cadastrarAula(3, segunda.plusDays(3).atTime(19, 0), "Bia"); // aula sem reservas
        ag.removerAula(livre.getId()); // remove
        mostrar("Resumo depois de remover uma aula", ag.listarAulas().size() + " aulas | " + ag.listarAgendamentos().size() + " agendamentos | " + ag); // resumo

        List<Aula> umaAula = List.of(aula1); // lista com uma aula
        esperarErro("agendamento com ID zero", () -> new Agendamento(0, ana, umaAula, Agendamento.DIARIA, 10.0, "4111111111111111")); // ID
        esperarErro("agendamento sem cliente", () -> new Agendamento(1, null, umaAula, Agendamento.DIARIA, 10.0, "4111111111111111")); // cliente
        esperarErro("agendamento sem aulas", () -> new Agendamento(1, ana, new ArrayList<>(), Agendamento.DIARIA, 10.0, "4111111111111111")); // aulas
        esperarErro("agendamento com aula nula na lista", () -> new Agendamento(1, ana, Collections.singletonList((Aula) null),
                Agendamento.DIARIA, 10.0, "4111111111111111")); // aula nula
        esperarErro("agendamento com preço zero", () -> new Agendamento(1, ana, umaAula, Agendamento.DIARIA, 0, "4111111111111111")); // preço
        esperarErro("agendamento com tipo inválido", () -> new Agendamento(1, ana, umaAula, "SEMANAL", 10.0, "4111111111111111")); // tipo
        esperarErro("agendamento com cartão inválido", () -> new Agendamento(1, ana, umaAula, Agendamento.DIARIA, 10.0, "12")); // cartão
        Agendamento teste = new Agendamento(1, ana, umaAula, Agendamento.DIARIA, 10.0, "4111111111111111"); // agendamento válido
        esperarErro("confirmar sem informar o dia", () -> teste.confirmar(null)); // sem data
        esperarErro("cancelar sem informar o dia", () -> teste.cancelar(null)); // sem data
        esperarErro("pagamento sem agendamento", () -> new VendaAgendamento(1, null, LocalDateTime.now())); // sem agendamento
        mostrar("Pagamento", pagamento2.descrever() + " | total R$ " + pagamento2.calcularTotal() + " | " + pagamento2); // dados do pagamento
    } // fim de testarAgenda

    /**
     * Questão 17: testa despesas, relatórios e balanço mensal, com as recusas.
     */
    private static void testarFinanceiro() { // testes do financeiro
        Sistema s = novoSistemaDeTeste(); // sistema só para este teste
        GestaoFinanceiro fin = s.getGestaoFinanceiro(); // o financeiro
        Funcionario adm = s.getUsuarioLogado(); // administrador logado
        LocalDate hoje = LocalDate.now(); // hoje
        Despesa limpeza = fin.cadastrarDespesa(adm, "Detergente", Despesa.LIMPEZA, 50.0, hoje); // despesa 1
        fin.cadastrarDespesa(adm, "Salário do instrutor", Despesa.FUNCIONARIOS, 1500.0, hoje); // despesa 2
        fin.cadastrarDespesa(adm, "Halteres novos", Despesa.MATERIAL, 400.0, hoje); // despesa 3
        fin.cadastrarDespesa(adm, "Conta de água", Despesa.OUTROS, 120.0, hoje); // despesa 4
        mostrar("Despesa lançada", limpeza + " | " + limpeza.getId() + " | " + limpeza.getDescricao() + " | " + limpeza.getCategoria()
                + " | R$ " + limpeza.getValor() + " | " + limpeza.getData()); // dados da despesa
        conferir("Despesas lançadas", 4, fin.listarDespesas(adm).size()); // 4
        fin.removerDespesa(adm, limpeza.getId()); // remove a primeira
        conferir("Despesas depois de remover uma", 3, fin.listarDespesas(adm).size()); // 3
        esperarErro("remover despesa que não existe", () -> fin.removerDespesa(adm, 999)); // não existe
        esperarErro("despesa com categoria inválida", () -> fin.cadastrarDespesa(adm, "X", "LAZER", 10.0, hoje)); // categoria
        esperarErro("despesa com valor zero", () -> fin.cadastrarDespesa(adm, "X", Despesa.OUTROS, 0, hoje)); // valor
        esperarErro("despesa sem data", () -> fin.cadastrarDespesa(adm, "X", Despesa.OUTROS, 10.0, null)); // data
        esperarErro("despesa com ID zero", () -> new Despesa(0, "X", Despesa.OUTROS, 10.0, hoje)); // ID

        Cliente cliente = s.getGestaoClientes().cadastrarCliente("Cliente Fin", "Rua 1", "3800", "f@f.com", "000.000.000-21"); // cliente
        s.getGestaoVendas().cadastrarProduto("Água", 5.0, 50); // produto
        s.registrarVendaLojinha(cliente.getId(), List.of(1), List.of(4)); // venda: R$ 20
        Aula aula = s.getGestaoAgendamentos().cadastrarAula(1, hoje.plusWeeks(3).with(TemporalAdjusters.next(DayOfWeek.MONDAY)).atTime(7, 0), "Rafael"); // aula
        Agendamento reserva = s.getGestaoAgendamentos().agendar(cliente, List.of(aula.getId()), Agendamento.DIARIA, 30.0, "4111111111111111"); // reserva
        s.confirmarAgendamento(reserva.getId(), LocalDateTime.now()); // pagamento: R$ 30
        s.passarNaCatraca(cliente.getId(), LocalDateTime.now()); // um acesso
        BalancoMensal balanco = fin.gerarBalancoMensal(adm, hoje.getYear(), hoje.getMonthValue()); // balanço do mês
        mostrar("Balanço do mês", balanco); // mostra o balanço
        mostrar("Dados do balanço", "ano " + balanco.getAno() + " | mês " + balanco.getMes() + " | receitas R$ " + balanco.getTotalReceitas()
                + " | despesas R$ " + balanco.getTotalDespesas() + " | vendas " + balanco.getQuantidadeVendas()
                + " | acessos " + balanco.getQuantidadeAcessos() + " | saldo R$ " + balanco.getSaldo()
                + " | ticket médio R$ " + balanco.getTicketMedio()); // todos os dados
        BalancoMensal vazio = new BalancoMensal(hoje.getYear(), hoje.getMonthValue()); // balanço sem dados
        vazio.calcular(new ArrayList<Venda>(), new ArrayList<Despesa>(), new ArrayList<RegistroCatraca>()); // calcula com listas vazias
        conferir("Saldo de um balanço sem dados", 0.0, vazio.getSaldo()); // zero
        conferir("Ticket médio de um balanço sem dados", 0.0, vazio.getTicketMedio()); // zero
        mostrar("Relatório de vendas da lojinha do mês", fin.gerarRelatorioVendasMes(hoje.getYear(), hoje.getMonthValue())); // lojinha do mês
        mostrar("Relatório de pagamentos do mês", fin.gerarRelatorioPagamentosMes(hoje.getYear(), hoje.getMonthValue())); // pagamentos do mês
        conferir("Relatórios guardados no histórico", 2, fin.listarRelatorios().size()); // 2
        mostrar("Resumo do financeiro", fin); // resumo
    } // fim de testarFinanceiro

    /**
     * Questão 17: testa as classes de apoio: validação de texto, iterador, DAO em memória e exceção.
     */
    private static void testarClassesDeApoio() { // testes das classes de apoio
        conferir("Validar um texto devolve sem os espaços das pontas", "Aline", Validacao.exigirTexto("  Aline  ", "nome")); // sem as pontas
        esperarErro("validar texto nulo", () -> Validacao.exigirTexto(null, "nome")); // nulo
        esperarErro("validar texto em branco", () -> Validacao.exigirTexto("   ", "nome")); // em branco

        List<String> letras = new ArrayList<>(List.of("a", "b", "c")); // lista para o iterador
        IteradorLista<String> iterador = new IteradorLista<>(letras); // nosso iterador
        esperarErro("iterador sem lista", () -> new IteradorLista<String>(null)); // sem lista
        esperarErro("alterar antes do next", () -> iterador.set("x")); // precisa do next
        esperarErro("remover antes do next", () -> iterador.remove()); // precisa do next
        conferir("Primeiro next do iterador", "a", iterador.next()); // a
        iterador.set("A"); // troca o elemento da posição
        conferir("Segundo next do iterador", "b", iterador.next()); // b
        iterador.remove(); // tira o b da lista
        conferir("Terceiro next do iterador", "c", iterador.next()); // c
        conferir("Lista depois do set e do remove", List.of("A", "c"), letras); // A e c
        conferir("O iterador ainda tem elementos?", false, iterador.hasNext()); // false
        esperarErro("next depois do fim", () -> iterador.next()); // acabou

        DAO<Produto> memoria = new DAOMemoria<>(); // DAO em memória
        Produto p = new Produto(memoria.gerarId(), "Corda", 10.0, 3); // produto com ID novo
        memoria.inserir(p); // guarda
        mostrar("DAO em memória", "guardado: " + memoria.buscarPorId(p.getId()) + " | lista: " + memoria.listar() + " | " + memoria); // dados
        esperarErro("guardar o mesmo ID duas vezes", () -> memoria.inserir(p)); // repetido
        esperarErro("guardar objeto nulo", () -> memoria.inserir(null)); // nulo
        esperarErro("atualizar objeto nulo", () -> memoria.atualizar(null)); // nulo
        esperarErro("atualizar objeto que não existe", () -> memoria.atualizar(new Produto(999, "Fantasma", 1.0, 1))); // não existe
        memoria.atualizar(p); // atualiza o que existe
        conferir("Remover do DAO um produto que existe devolve", true, memoria.remover(p.getId())); // true
        conferir("Remover do DAO o mesmo produto de novo devolve", false, memoria.remover(p.getId())); // false
        Identificavel semNome = p; // o produto também é Identificavel
        mostrar("ID pelo contrato Identificavel", semNome.getId()); // getId

        DaoException erro = new DaoException("Erro de teste", new RuntimeException("causa original")); // exceção encadeada
        conferir("Mensagem da DaoException", "Erro de teste", erro.getMessage()); // mensagem
        conferir("Causa da DaoException", "causa original", erro.getCause().getMessage()); // causa
    } // fim de testarClassesDeApoio

    /**
     * Teste do menu: monta um texto com tudo o que uma pessoa digitaria e entrega à
     * {@link ConsoleView}, para percorrer todos os menus do {@link AcademiaController}.
     *
     * @param segunda uma segunda-feira futura, usada nas datas das aulas
     */
    private static void testarMenu(LocalDate segunda) { // simula o uso do menu
        Path arqClientes = Path.of("teste-menu-clientes.json"); // clientes do teste do menu
        Path arqAcademia = Path.of("teste-menu-academia.json"); // academia do teste do menu
        apagar(arqClientes); // começa limpo
        apagar(arqAcademia); // começa limpo
        DateTimeFormatter dia = DateTimeFormatter.ofPattern("dd/MM/yyyy"); // formato do dia
        String hoje = LocalDate.now().format(dia); // hoje digitado
        String seg = segunda.format(dia); // a segunda digitada
        String ano = String.valueOf(LocalDate.now().getYear()); // ano de hoje
        String mes = String.valueOf(LocalDate.now().getMonthValue()); // mês de hoje
        String[] digitado = { // tudo o que a pessoa digita, uma linha por resposta
            "abc", "1", "aline.pena@ufvjm.edu.br", "errada", // número inválido; entra com a senha errada
            "1", "aline.pena@ufvjm.edu.br", "Isabella18@", // entra certo
            "1", "1", "Maria Teste", "Rua 1", "3811112222", "maria@email.com", "123.456.789-09", // clientes: cadastrar
            "1", "1", "Pedro Teste", "Rua 2", "3822223333", "pedro@email.com", "987.654.321-00", // cadastrar o segundo
            "1", "2", "1", "Maria Teste Silva", "Rua 10", "3811112222", "maria@email.com", "123.456.789-09", // editar
            "1", "3", "2", "1", "4", "2", // remover e reativar o Pedro
            "1", "5", "1", "1", "5", "999", // buscar por ID (existe e não existe)
            "1", "6", "mar", "1", "7", "1", "8", "1", // buscar por nome, listar e conta sem extratos
            "2", "1", "1", "31/02/2026 07:00", seg + " 07:00", "Rafael", // agenda: cadastrar aula (a 1ª data é inválida)
            "2", "1", "4", seg + " 08:00", "Carla", // cadastrar a segunda aula
            "2", "2", "2", "3", "1", seg, "2", "4", "1", // listar aulas, ver a semana e as vagas
            "2", "5", "1", "1", "1", "0", "4111111111111111", // agendar a diária (preço 0 aceita a tabela)
            "2", "5", "1", "2", "2", "180,00", "5555444433332222", // agendar a mensalidade
            "2", "6", "1", "2", "7", "2", "2", "8", "2", "9", "1", "2", "10", // confirmar, cancelar, vencidos, próximo, listar
            "2", "5", "999", "1", "1", "0", "4111", // erro: cliente que não existe
            "3", "1", "Garrafa", "15,50", "5", "3", "1", "Luva", "40", "2", "3", "1", "Garrafa Térmica", "60", "4", // lojinha: cadastrar três produtos
            "3", "6", "3", "3", "2", "3", "3", "9", // listar e verificar pelo número da lista (a luva e um número fora da lista)
            "3", "4", "2", "3", // repor 3 luvas (produto 2 da lista)
            "3", "5", "1", "2", "8", "1", "2", "3", "2", "0", // venda: vende o que há de luva, produto repetido é recusado, mais 2 garrafas térmicas
            "3", "5", "1", "2", "1", "0", // luva sem estoque: nenhum item
            "3", "5", "1", "9", "3", "99", "2", "0", // número fora da lista e pedido acima do estoque com o item retirado
            "3", "5", "1", "1", "1", "0", // venda comum de uma garrafa
            "3", "2", "2", // remove a luva (produto 2 da lista)
            "1", "8", "1", // clientes: conta da Maria, agora com os extratos
            "4", "1", hoje, "4", "2", ano, mes, "4", "3", hoje, "4", "4", ano, mes, // relatórios: lojinha e pagamentos, por dia e por mês
            "4", "5", hoje, "4", "6", ano, mes, "4", "7", ano, "4", "8", "4", "9", // catraca por dia, mês e ano, dados dos clientes e relatórios gerados
            "5", "1", "Limpeza geral", "1", "100", hoje, "5", "1", "Salário", "2", "900,50", hoje, // financeiro: despesas
            "5", "1", "Halteres", "3", "250", hoje, "5", "1", "Água", "4", "80", hoje, // mais despesas
            "5", "3", "5", "4", ano, mes, "5", "2", "1", // listar, balanço e remover
            "6", "1", "Paulo Teste", "Rua 3", "3833334444", "paulo@academia.com", "123", // funcionários: colaborador
            "6", "2", "Bia Teste", "Rua 4", "3844445555", "bia@academia.com", "abc", // administrador
            "6", "3", "2", "Paulo T. Silva", "Rua 30", "3833334444", "paulo@academia.com", // editar
            "6", "5", "2", "123", "456", "6", "5", "2", "errada", "x", // trocar a senha (certa e errada)
            "6", "4", "3", "6", "4", "1", "6", "6", // remover a Bia, tentar remover a si mesmo, listar
            "99", "0", // opção que não existe e sair
            "2", "1", "2", "1", "2", "999", // terminal da catraca: entra, sai e ID que não existe
            "1", "paulo@academia.com", "456", "6", "5", "1", "Teste", "1", "10", hoje, "0", // colaborador: funcionários e despesa recusados
            "0" // sai do sistema
        }; // fim do texto digitado
        Scanner teclado = new Scanner(String.join("\n", digitado) + "\n"); // a View lê daqui, não do teclado
        Sistema sistema = new Sistema(new ClienteDAOJson(arqClientes)); // Model com clientes em arquivo de teste
        new AcademiaController(sistema, new ConsoleView(teclado), new RepositorioJson(arqAcademia)).iniciar(); // roda o menu
        System.out.println("\n-- menu com arquivo quebrado e com pasta que não existe --"); // casos de erro do controller
        Path quebrado = Path.of("teste-menu-quebrado.json"); // arquivo que não é JSON
        gravar(quebrado, "isto não é json"); // escreve o texto quebrado
        new AcademiaController(new Sistema(), new ConsoleView(new Scanner("0\n")), new RepositorioJson(quebrado)).iniciar(); // não lê e segue
        new AcademiaController(new Sistema(), new ConsoleView(new Scanner("0\n")),
                new RepositorioJson(Path.of("pasta-que-nao-existe/a.json"))).iniciar(); // não grava e avisa
        apagar(arqClientes); // limpa os arquivos de teste
        apagar(arqAcademia); // limpa os arquivos de teste
        apagar(quebrado); // limpa os arquivos de teste
    } // fim de testarMenu

    /**
     * Lista todas as classes, interfaces e classes abstratas do projeto, mostrando
     * o pacote, o tipo (classe, abstrata ou interface) e o papel de cada uma.
     * É a resposta da Questão 1: o diagrama completo, em texto.
     */
    private static void listarClassesDoProjeto() { // lista as classes do projeto
        Class<?>[] todas = { // vetor com todas as classes: se alguma deixar de existir, o código não compila
            AcademiaMilhoVerde.class, AcademiaController.class, ConsoleView.class, // raiz, controller e view
            DAO.class, DAOMemoria.class, ClienteDAOJson.class, DaoException.class, // pacote dao
            Identificavel.class, Pessoa.class, Cliente.class, Funcionario.class, Administrador.class, Colaborador.class, // pessoas
            Sala.class, Aula.class, AgendaSemanal.class, Agendamento.class, // agenda
            Venda.class, VendaProdutos.class, VendaAgendamento.class, ItemVenda.class, Extrato.class, // vendas e extratos
            Produto.class, Estoque.class, // lojinha
            Catraca.class, RegistroCatraca.class, // catraca
            Despesa.class, RelatorioVendas.class, BalancoMensal.class, // financeiro
            ComparadorCliente.class, ComparadorAgendamento.class, IteradorLista.class, Validacao.class, // apoio
            GestaoClientes.class, GestaoFuncionarios.class, GestaoAgendamentos.class, GestaoVendas.class, // gestões
            GestaoFinanceiro.class, Sistema.class, RepositorioJson.class // gestão financeira, fachada e arquivo
        }; // fim do vetor de classes
        String[] papeis = { // o papel de cada classe, na mesma ordem do vetor acima
            "Classe principal: testes das questões e porta de entrada do menu", "Controller do MVC: liga a tela às regras", "View do MVC: entrada e saída em texto",
            "Contrato genérico de acesso aos dados", "DAO que guarda os objetos na memória", "Guarda os clientes no clientes.json", "Erro de acesso aos dados",
            "Contrato: todo objeto guardado tem ID", "Superclasse de Cliente e Funcionario", "Aluno da academia (é uma Pessoa)", "Superclasse dos funcionários, com login por e-mail", "Funcionário com acesso ao financeiro", "Funcionário sem acesso ao financeiro",
            "Uma das 4 salas fixas", "Aula de uma sala, com dia, hora e instrutor", "Grade semanal de aulas de uma sala", "Reserva de aulas: diária ou mensalidade",
            "Superclasse das vendas (total polimórfico)", "Venda de produtos da lojinha", "Pagamento de diária ou mensalidade", "Linha de uma venda: produto e quantidade", "Nota de uma compra ou pagamento",
            "Item à venda na lojinha", "Produtos da lojinha e suas quantidades",
            "Controla a entrada e a saída dos clientes", "Registro de uma entrada ou saída",
            "Gasto lançado pelo administrador", "Resumo das vendas de um período", "Resultado do mês: receitas menos despesas",
            "Ordena clientes por CPF ou nome", "Ordena agendamentos por dia e hora", "Nosso iterador para percorrer listas", "Validações reutilizadas pelas classes",
            "Regras dos clientes e das contas", "Regras dos funcionários", "Regras das aulas, agendamentos e pagamentos", "Lojinha: produtos, estoque e vendas",
            "Relatórios, despesas e balanço", "Fachada do Model e controle de login", "Grava e lê o academia.json"
        }; // fim do vetor de papéis
        System.out.println("Total de classes do projeto: " + todas.length); // quantas são
        for (int i = 0; i < todas.length; i++) { // vetor usa índice
            Class<?> classe = todas[i]; // a classe da vez
            String tipo = "classe"; // tipo padrão
            if (classe.isInterface()) { // é interface?
                tipo = "interface"; // contrato sem código
            } else if (Modifier.isAbstract(classe.getModifiers())) { // é abstrata?
                tipo = "abstrata"; // não pode ser instanciada
            } // fim do if
            String pacote = classe.getPackageName(); // pacote completo
            pacote = pacote.endsWith(".verde") ? "raiz" : pacote.substring(pacote.lastIndexOf('.') + 1); // só o último trecho
            System.out.println(String.format("%2d. %-22s %-10s %-11s %s", i + 1, classe.getSimpleName(), pacote, tipo, papeis[i])); // uma linha por classe
        } // fim do for
    } // fim de listarClassesDoProjeto

    /**
     * Cenário da Questão 21: atende 10 clientes, cada um por um caminho diferente
     * (diária, mensalidade, devolução de 50%, prazo vencido, estoque insuficiente,
     * CPF repetido e cancelamento da matrícula) e termina com as notas (extratos).
     *
     * @param segunda uma segunda-feira futura, usada como base das datas das aulas
     */
    private static void cenarioDezClientes(LocalDate segunda) { // 10 atendimentos com variações
        Sistema s = new Sistema(new DAOMemoria<>()); // sistema novo, só na memória
        s.criarPrimeiroAdministrador("Aline Ferreira", "Diamantina, MG", "3800000000",
                "aline.pena@ufvjm.edu.br", "Isabella18@"); // administrador do cenário
        s.login("aline.pena@ufvjm.edu.br", "Isabella18@"); // entra
        GestaoClientes c = s.getGestaoClientes(); // atalho para os clientes
        GestaoAgendamentos ag = s.getGestaoAgendamentos(); // atalho para a agenda
        GestaoVendas loja = s.getGestaoVendas(); // atalho para a lojinha
        Produto garrafa = loja.cadastrarProduto("Garrafa 500ml", 15.0, 10); // produto 1
        Produto luva = loja.cadastrarProduto("Luva de treino", 40.0, 3); // produto 2
        Produto barra = loja.cadastrarProduto("Barra de proteína", 8.0, 30); // produto 3
        Aula spinning = ag.cadastrarAula(1, segunda.atTime(7, 0), "Rafael"); // segunda 7h
        Aula pilates = ag.cadastrarAula(4, segunda.atTime(8, 0), "Carla"); // segunda 8h
        Aula danca = ag.cadastrarAula(3, segunda.plusDays(2).atTime(19, 0), "Bia"); // quarta 19h
        Aula muscA = ag.cadastrarAula(2, segunda.plusDays(1).atTime(18, 0), "Léo"); // terça 18h
        Aula muscB = ag.cadastrarAula(2, segunda.plusDays(3).atTime(18, 0), "Léo"); // quinta 18h

        System.out.println("\n1) Ana: diária de spinning, confirma, entra e sai, compra 2 garrafas"); // cliente 1
        Cliente ana = c.cadastrarCliente("Ana Souza", "Rua 1", "38900000001", "ana@email.com", "111.111.111-11"); // cadastra
        Agendamento a1 = ag.agendar(ana, List.of(spinning.getId()), Agendamento.DIARIA,
                ag.calcularPrecoSugerido(List.of(spinning.getId()), Agendamento.DIARIA), "4111111111111111"); // agenda pela tabela
        s.confirmarAgendamento(a1.getId(), LocalDateTime.now()); // confirma e paga
        System.out.println(s.passarNaCatraca(ana.getId(), segunda.atTime(6, 55))); // entrada
        System.out.println(s.passarNaCatraca(ana.getId(), segunda.atTime(8, 5))); // saída
        s.registrarVendaLojinha(ana.getId(), List.of(garrafa.getId()), List.of(2)); // compra 2 garrafas

        System.out.println("\n2) Bruno: mensalidade de musculação (2 aulas), compra luva e barra"); // cliente 2
        Cliente bruno = c.cadastrarCliente("Bruno Alves", "Rua 2", "38900000002", "bruno@email.com", "222.222.222-22"); // cadastra
        Agendamento a2 = ag.agendar(bruno, List.of(muscA.getId(), muscB.getId()), Agendamento.MENSALIDADE,
                ag.calcularPrecoSugerido(List.of(muscA.getId(), muscB.getId()), Agendamento.MENSALIDADE),
                "5555444433332222"); // mensalidade com 2 aulas
        s.confirmarAgendamento(a2.getId(), LocalDateTime.now()); // confirma e paga
        s.registrarVendaLojinha(bruno.getId(), List.of(luva.getId(), barra.getId()), List.of(1, 3)); // compra 2 produtos

        System.out.println("\n3) Carla: confirma pilates e depois desiste com antecedência (devolve 50%)"); // cliente 3
        Cliente carla = c.cadastrarCliente("Carla Mendes", "Rua 3", "38900000003", "carla@email.com", "333.333.333-33"); // cadastra
        Agendamento a3 = ag.agendar(carla, List.of(pilates.getId()), Agendamento.DIARIA, 35.0, "4000123412341234"); // agenda
        s.confirmarAgendamento(a3.getId(), LocalDateTime.now()); // confirma e paga
        System.out.println("Devolvido: R$ " + s.cancelarAgendamento(a3.getId(), LocalDate.now())); // devolve 50%

        System.out.println("\n4) Daniel: agendamento preliminar cancelado antes de confirmar (nada a devolver)"); // cliente 4
        Cliente daniel = c.cadastrarCliente("Daniel Reis", "Rua 4", "38900000004", "daniel@email.com", "444.444.444-44"); // cadastra
        Agendamento a4 = ag.agendar(daniel, List.of(danca.getId()), Agendamento.DIARIA, 25.0, "4000222233334444"); // agenda
        System.out.println("Devolvido: R$ " + s.cancelarAgendamento(a4.getId(), LocalDate.now())); // nada a devolver

        System.out.println("\n5) Elisa: tenta aulas de salas diferentes (recusado) e agenda certo"); // cliente 5
        Cliente elisa = c.cadastrarCliente("Elisa Prado", "Rua 5", "38900000005", "elisa@email.com", "555.555.555-55"); // cadastra
        try { // duas salas no mesmo agendamento
            ag.agendar(elisa, List.of(spinning.getId(), pilates.getId()), Agendamento.DIARIA, 65.0, "4000555566667777"); // recusado
        } catch (IllegalArgumentException e) { // a regra recusou
            System.out.println("Esperado: " + e.getMessage()); // mostra o motivo
        } // fim do try
        Agendamento a5 = ag.agendar(elisa, List.of(pilates.getId()), Agendamento.DIARIA, 35.0, "4000555566667777"); // agenda certo
        s.confirmarAgendamento(a5.getId(), LocalDateTime.now()); // confirma e paga

        System.out.println("\n6) Fábio: pede 5 luvas (só há 2), a venda é recusada; compra as 2"); // cliente 6
        Cliente fabio = c.cadastrarCliente("Fábio Nunes", "Rua 6", "38900000006", "fabio@email.com", "666.666.666-66"); // cadastra
        try { // pede mais do que há
            s.registrarVendaLojinha(fabio.getId(), List.of(luva.getId()), List.of(5)); // recusada
        } catch (IllegalStateException e) { // faltou estoque
            System.out.println("Esperado: " + e.getMessage()); // mostra o motivo
        } // fim do try
        s.registrarVendaLojinha(fabio.getId(), List.of(luva.getId()), List.of(luva.getQuantidade())); // compra o que restou
        System.out.println(loja.verificarProduto(luva.getId())); // estoque zerado

        System.out.println("\n7) Gabriela: confirma depois do prazo e o agendamento é cancelado"); // cliente 7
        Cliente gabi = c.cadastrarCliente("Gabriela Lopes", "Rua 7", "38900000007", "gabi@email.com", "777.777.777-77"); // cadastra
        Agendamento a7 = ag.agendar(gabi, List.of(danca.getId()), Agendamento.DIARIA, 25.0, "4000777788889999"); // agenda
        try { // confirma um dia depois do prazo
            s.confirmarAgendamento(a7.getId(), a7.calcularPrazoConfirmacao().plusDays(1).atTime(10, 0)); // fora do prazo
        } catch (IllegalStateException e) { // o agendamento se cancelou
            System.out.println("Esperado: " + e.getMessage() + " -> " + a7.getStatus()); // motivo e status
        } // fim do try

        System.out.println("\n8) Hugo: CPF repetido é recusado; cadastra certo e agenda spinning"); // cliente 8
        try { // CPF da Ana
            c.cadastrarCliente("Hugo Ramos", "Rua 8", "38900000008", "hugo@email.com", "111.111.111-11"); // recusado
        } catch (IllegalArgumentException e) { // CPF repetido
            System.out.println("Esperado: " + e.getMessage()); // mostra o motivo
        } // fim do try
        Cliente hugo = c.cadastrarCliente("Hugo Ramos", "Rua 8", "38900000008", "hugo@email.com", "888.888.888-88"); // cadastra certo
        Agendamento a8 = ag.agendar(hugo, List.of(spinning.getId()), Agendamento.DIARIA, 30.0, "4000888899990000"); // agenda
        s.confirmarAgendamento(a8.getId(), LocalDateTime.now()); // confirma e paga
        System.out.println(s.passarNaCatraca(hugo.getId(), segunda.atTime(6, 58))); // entra na catraca

        System.out.println("\n9) Íris: mensalidade de fit dance; o próximo agendamento dela é mostrado"); // cliente 9
        Cliente iris = c.cadastrarCliente("Íris Costa", "Rua 9", "38900000009", "iris@email.com", "999.999.999-99"); // cadastra
        Agendamento a9 = ag.agendar(iris, List.of(danca.getId()), Agendamento.MENSALIDADE, 130.0, "4000999900001111"); // agenda
        s.confirmarAgendamento(a9.getId(), LocalDateTime.now()); // confirma e paga
        System.out.println("Próximo: " + ag.buscarProximoAgendamento(iris.getId(), LocalDate.now())); // o mais cedo

        System.out.println("\n10) João: deixa o preliminar vencer, é cancelado automaticamente e cancela a matrícula"); // cliente 10
        Cliente joao = c.cadastrarCliente("João Silva", "Rua 10", "38900000010", "joao@email.com", "101.010.101-01"); // cadastra
        Agendamento a10 = ag.agendar(joao, List.of(muscA.getId()), Agendamento.DIARIA, 25.0, "4000101010101010"); // agenda
        System.out.println("Cancelados por prazo: " + ag.cancelarVencidos(a10.calcularPrazoConfirmacao().plusDays(1))
                + " -> " + a10.getStatus()); // o prazo venceu
        c.removerCliente(joao.getId()); // cancelamento da matrícula
        try { // cliente desativado na catraca
            s.passarNaCatraca(joao.getId(), segunda.atTime(18, 0)); // desativado não passa
        } catch (IllegalArgumentException e) { // a catraca recusou
            System.out.println("Esperado: " + e.getMessage()); // mostra o motivo
        } // fim do try

        System.out.println("\n--- Fim: notas (extratos) de cada cliente ---"); // fechamento do cenário
        IteradorLista<Cliente> iterador = new IteradorLista<>(c.listarClientes()); // percorre os 10 clientes
        while (iterador.hasNext()) { // enquanto houver cliente
            Cliente cliente = iterador.next(); // pega o próximo
            System.out.println(cliente.getNome() + ": " + c.emitirConta(cliente.getId())
                    + " | total R$ " + String.format("%.2f", c.calcularTotalConta(cliente.getId()))); // nota do cliente
        } // fim do while
        System.out.println(s.getGestaoFinanceiro().gerarRelatorioVendasDia(LocalDate.now())); // lojinha do dia
        System.out.println(s.getGestaoFinanceiro().gerarRelatorioPagamentosDia(LocalDate.now())); // pagamentos do dia
        System.out.println("Estoque: " + loja.getEstoque().getProdutos()); // estoque no fim
        System.out.println("Na academia agora: " + s.getCatraca().getPresentes()); // quem ficou na academia
    } // fim de cenarioDezClientes

    /**
     * Imprime uma linha separando as questões na saída.
     *
     * @param texto título da questão
     */
    private static void titulo(String texto) { // separa as questões na saída
        System.out.println("\n========== " + texto + " =========="); // linha de título
    } // fim de titulo

    /**
     * Mostra o nome e o CPF de cada cliente da lista.
     *
     * @param lista clientes a mostrar
     */
    private static void imprimir(List<Cliente> lista) { // mostra nome e CPF de cada cliente
        IteradorLista<Cliente> iterador = new IteradorLista<>(lista); // percorre a lista
        while (iterador.hasNext()) { // enquanto houver cliente
            Cliente cliente = iterador.next(); // pega o próximo
            System.out.println("  " + cliente.getNome() + " | " + cliente.getCpfPseudo()); // nome e CPF
        } // fim do while
    } // fim de imprimir

    /**
     * Apaga um arquivo de teste, se ele existir.
     *
     * @param arquivo caminho do arquivo a apagar
     */
    private static void apagar(Path arquivo) { // apaga um arquivo de teste, se existir
        try { // apagar arquivo pode falhar
            Files.deleteIfExists(arquivo); // apaga se existir
        } catch (IOException e) { // não conseguiu apagar
            System.out.println("Não consegui apagar " + arquivo); // avisa
        } // fim do try
    } // fim de apagar

    /**
     * Grava um texto num arquivo de teste.
     *
     * @param arquivo caminho do arquivo
     * @param texto conteúdo a gravar
     */
    private static void gravar(Path arquivo, String texto) { // grava um arquivo de teste
        try { // gravar arquivo pode falhar
            Files.writeString(arquivo, texto); // grava o texto
        } catch (IOException e) { // não conseguiu gravar
            System.out.println("Não consegui gravar " + arquivo); // avisa
        } // fim do try
    } // fim de gravar

    /**
     * Lê o texto de um arquivo de teste.
     *
     * @param arquivo caminho do arquivo
     * @return o conteúdo, ou texto vazio se não conseguir ler
     */
    private static String lerTexto(Path arquivo) { // lê um arquivo de teste
        try { // ler arquivo pode falhar
            return Files.readString(arquivo); // devolve o conteúdo
        } catch (IOException e) { // não conseguiu ler
            System.out.println("Não consegui ler " + arquivo); // avisa
            return ""; // texto vazio
        } // fim do try
    } // fim de lerTexto
}