/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.controller; // pacote do Controller

import com.mycompany.academia.milho.verde.dao.DaoException; // erro de arquivo
import com.mycompany.academia.milho.verde.model.Agendamento; // reserva
import com.mycompany.academia.milho.verde.model.Cliente; // cliente
import com.mycompany.academia.milho.verde.model.Despesa; // despesa
import com.mycompany.academia.milho.verde.model.Extrato; // extrato
import com.mycompany.academia.milho.verde.model.GestaoAgendamentos; // agenda
import com.mycompany.academia.milho.verde.model.GestaoClientes; // clientes
import com.mycompany.academia.milho.verde.model.GestaoFinanceiro; // financeiro
import com.mycompany.academia.milho.verde.model.GestaoFuncionarios; // funcionários
import com.mycompany.academia.milho.verde.model.GestaoVendas; // lojinha
import com.mycompany.academia.milho.verde.model.IteradorLista; // nosso iterador
import com.mycompany.academia.milho.verde.model.Produto; // produto
import com.mycompany.academia.milho.verde.model.RegistroCatraca; // registro da catraca
import com.mycompany.academia.milho.verde.model.RepositorioJson; // arquivo academia.json
import com.mycompany.academia.milho.verde.model.Sistema; // fachada do Model
import com.mycompany.academia.milho.verde.view.ConsoleView; // tela
import java.time.LocalDate; // data sem hora
import java.time.LocalDateTime; // data com hora
import java.util.ArrayList; // lista que cresce sozinha
import java.util.List; // tipo da lista

/**
 * Controller do padrão MVC: recebe o que a pessoa escolhe no menu, chama as regras do
 * {@link Sistema} (Model) e manda a {@link ConsoleView} (View) mostrar o resultado.
 *
 * <p>Não tem regra de negócio: toda recusa vem do Model como exceção e aqui só vira mensagem de erro.
 * Na lojinha a lista de produtos aparece com o ID de cada um e a pessoa digita o ID escolhido.</p>
 *
 * @author Aline Ferreira
 */
public class AcademiaController { // liga a tela (View) às regras (Model)

    /**
     * O Model: a fachada com todas as regras.
     */
    private final Sistema sistema; // o Model
    /**
     * A View: entrada e saída em texto.
     */
    private final ConsoleView view; // a tela
    /**
     * O arquivo academia.json, onde ficam todos os dados fora os clientes.
     */
    private final RepositorioJson repositorio; // o academia.json

    /**
     * Liga as três partes do MVC.
     *
     * @param novoSistema o Model
     * @param novaView a tela em modo texto
     * @param novoRepositorio o arquivo academia.json
     */
    public AcademiaController(Sistema novoSistema, ConsoleView novaView, RepositorioJson novoRepositorio) { // recebe as três partes
        this.sistema = novoSistema; // guarda o Model
        this.view = novaView; // guarda a tela
        this.repositorio = novoRepositorio; // guarda o repositório
    } // fim do construtor

    /**
     * Começa o programa: lê o arquivo, cria o primeiro administrador se for o primeiro acesso,
     * mostra a tela inicial (entrar, catraca ou sair) e grava tudo ao sair.
     */
    public void iniciar() { // começa o programa
        try { // o arquivo pode estar com problema
            sistema.carregar(repositorio); // lê o academia.json
        } catch (DaoException erro) { // não conseguiu ler
            view.mostrarErro(erro.getMessage() + " Começando sem os dados do arquivo."); // avisa e segue
        } // fim do try
        if (!sistema.temFuncionarios()) { // primeiro acesso?
            prepararPrimeiroAcesso(); // cria o primeiro administrador
        } // fim do if
        int opcao = -1; // opção escolhida
        while (opcao != 0) { // até escolher sair
            opcao = view.mostrarMenu("ACADEMIA MILHO VERDE", new String[]{
                "1 - Entrar (funcionário)", "2 - Terminal da catraca", "0 - Sair"}); // tela inicial
            if (opcao == 1) { // entrar
                fazerLogin(); // pede e-mail e senha
            } else if (opcao == 2) { // catraca
                terminalCatraca(); // cliente digita o ID
            } // fim do if
        } // fim do while
        salvarDados(); // grava antes de fechar
        view.mostrarMensagem("Até logo!"); // despedida
    } // fim de iniciar

    /**
     * Cria o administrador de partida quando ainda não existe nenhum funcionário.
     */
    private void prepararPrimeiroAcesso() { // cria o administrador de partida
        sistema.criarPrimeiroAdministrador("Aline Ferreira", "Diamantina, MG", "3800000000",
                "aline.pena@ufvjm.edu.br", "Isabella18@"); // administrador já pronto no primeiro acesso
        salvarDados(); // grava
        view.mostrarMensagem("Primeiro acesso: administrador Aline Ferreira criado (login aline.pena@ufvjm.edu.br)."); // avisa
    } // fim de prepararPrimeiroAcesso

    /**
     * Pede e-mail e senha; se estiverem certos, abre o menu principal e, ao voltar, faz o logout.
     */
    private void fazerLogin() { // entra no sistema
        if (!sistema.login(view.lerTexto("E-mail"), view.lerTexto("Senha"))) { // e-mail ou senha errados?
            view.mostrarErro("E-mail ou senha incorretos."); // avisa
            return; // volta à tela inicial
        } // fim do if
        view.mostrarMensagem("Bem-vindo(a), " + sistema.getUsuarioLogado().getNome() + "!"); // saudação
        executarMenuPrincipal(); // menu do funcionário
        sistema.logout(); // ao voltar, sai
    } // fim de fazerLogin

    /**
     * Repete o menu principal até a pessoa escolher sair. Qualquer recusa das regras vira mensagem de erro.
     */
    private void executarMenuPrincipal() { // menu depois do login
        int opcao = -1; // opção escolhida
        while (opcao != 0) { // até escolher sair
            opcao = view.mostrarMenu("MENU PRINCIPAL", new String[]{"1 - Clientes", "2 - Agenda", "3 - Lojinha",
                "4 - Relatórios", "5 - Financeiro (administrador)", "6 - Funcionários (administrador)", "7 - Lanchonete",
                "0 - Sair"}); // opções
            try { // qualquer regra pode recusar a operação
                switch (opcao) { // escolhe o submenu
                    case 1 -> repetirSubmenu(this::menuClientes); // clientes
                    case 2 -> repetirSubmenu(this::menuAgenda); // agenda
                    case 3 -> repetirSubmenu(this::menuLoja); // lojinha
                    case 4 -> repetirSubmenu(this::menuRelatorios); // relatórios
                    case 5 -> { // financeiro: só o administrador entra
                        if (sistema.isAdministradorLogado()) { // confere o perfil
                            repetirSubmenu(this::menuFinanceiro); // financeiro
                        } else { // colaborador
                            view.mostrarMensagem("Acesso negado: apenas o administrador acessa o financeiro."); // avisa
                        } // fim do if
                    } // fim do case 5
                    case 6 -> { // funcionários: só o administrador entra
                        if (sistema.isAdministradorLogado()) { // confere o perfil
                            repetirSubmenu(this::menuFuncionarios); // funcionários
                        } else { // colaborador
                            view.mostrarMensagem("Acesso negado: apenas o administrador gerencia funcionários."); // avisa
                        } // fim do if
                    } // fim do case 6
                    case 7 -> repetirSubmenu(this::menuLanchonete); // lanchonete
                    default -> { } // 0 sai; outro número só repete o menu
                } // fim do switch
            } catch (IllegalArgumentException | IllegalStateException | DaoException erro) { // regra recusou
                view.mostrarErro(erro.getMessage()); // mostra o motivo
            } // fim do try
        } // fim do while
    } // fim de executarMenuPrincipal

    /**
     * Mantém um submenu aberto: depois de cada ação ele aparece de novo, até a pessoa escolher voltar.
     * Se uma regra recusar a ação, mostra o motivo e continua no mesmo submenu.
     *
     * @param submenu o submenu, que devolve {@code false} quando a pessoa escolhe voltar
     */
    private void repetirSubmenu(java.util.function.BooleanSupplier submenu) { // repete o submenu
        boolean continua = true; // começa dentro do submenu
        while (continua) { // até escolher voltar
            try { // uma regra pode recusar a ação
                continua = submenu.getAsBoolean(); // mostra o submenu e executa a opção
            } catch (IllegalArgumentException | IllegalStateException | DaoException erro) { // regra recusou
                view.mostrarErro(erro.getMessage()); // mostra o motivo e continua no submenu
            } // fim do try
        } // fim do while
    } // fim de repetirSubmenu

    /**
     * Menu dos clientes: cadastrar, editar, remover, reativar, buscar, listar e emitir a conta.
     *
     * @return {@code true} para continuar no submenu; {@code false} quando a pessoa escolhe voltar
     */
    private boolean menuClientes() { // CRUD de clientes
        GestaoClientes gestao = sistema.getGestaoClientes(); // exige login
        int opcao = view.mostrarMenu("CLIENTES", new String[]{"1 - Cadastrar", "2 - Editar", "3 - Remover",
            "4 - Reativar", "5 - Buscar por ID", "6 - Buscar por nome", "7 - Listar", "8 - Emitir conta", "0 - Voltar"}); // opções
        if (opcao == 0) { // 0 volta ao menu principal
            return false; // sai do submenu
        } // fim do if
        switch (opcao) { // executa a opção
            case 1 -> view.mostrarMensagem("Cadastrado: " + gestao.cadastrarCliente(view.lerTexto("Nome"),
                    view.lerTexto("Endereço"), view.lerTexto("Telefone"), view.lerTexto("E-mail"), view.lerTexto("CPF"))); // cadastra
            case 2 -> { // edita
                gestao.editarCliente(view.lerInteiro("ID"), view.lerTexto("Nome"), view.lerTexto("Endereço"),
                        view.lerTexto("Telefone"), view.lerTexto("E-mail"), view.lerTexto("CPF")); // altera tudo
                view.mostrarMensagem("Cliente alterado."); // confirma
            } // fim do case 2
            case 3 -> { // remove (desativa)
                gestao.removerCliente(view.lerInteiro("ID")); // exclusão lógica
                view.mostrarMensagem("Cliente desativado."); // confirma
            } // fim do case 3
            case 4 -> { // reativa
                gestao.reativarCliente(view.lerInteiro("ID")); // volta a ficar ativo
                view.mostrarMensagem("Cliente reativado."); // confirma
            } // fim do case 4
            case 5 -> view.mostrarMensagem(String.valueOf(gestao.buscarCliente(view.lerInteiro("ID")))); // mostra ou null
            case 6 -> view.mostrarLista("Encontrados", gestao.buscarPorNome(view.lerTexto("Nome"))); // busca
            case 7 -> view.mostrarLista("Clientes", gestao.listarClientes()); // lista
            case 8 -> emitirConta(gestao); // conta do cliente
            default -> { } // voltar
        } // fim do switch
        return true; // continua no mesmo submenu
    } // fim de menuClientes

    /**
     * Imprime todos os extratos de um cliente e o total da conta.
     *
     * @param gestao as regras dos clientes
     */
    private void emitirConta(GestaoClientes gestao) { // todos os extratos do cliente
        int id = view.lerInteiro("ID do cliente"); // qual cliente
        IteradorLista<Extrato> iterador = new IteradorLista<>(gestao.emitirConta(id)); // percorre os extratos
        while (iterador.hasNext()) { // enquanto houver extrato
            view.imprimirExtrato(iterador.next()); // imprime
        } // fim do while
        view.mostrarMensagem("Total: R$ " + String.format("%.2f", gestao.calcularTotalConta(id))); // total da conta
    } // fim de emitirConta

    /**
     * Menu da agenda: aulas, vagas, agendar, confirmar, cancelar e listar.
     *
     * @return {@code true} para continuar no submenu; {@code false} quando a pessoa escolhe voltar
     */
    private boolean menuAgenda() { // aulas e agendamentos
        GestaoAgendamentos gestao = sistema.getGestaoAgendamentos(); // exige login
        int opcao = view.mostrarMenu("AGENDA", new String[]{"1 - Cadastrar aula", "2 - Listar aulas",
            "3 - Agenda da semana", "4 - Verificar vagas", "5 - Agendar", "6 - Confirmar agendamento",
            "7 - Cancelar agendamento", "8 - Cancelar vencidos", "9 - Próximo agendamento do cliente",
            "10 - Listar agendamentos", "0 - Voltar"}); // opções
        if (opcao == 0) { // 0 volta ao menu principal
            return false; // sai do submenu
        } // fim do if
        switch (opcao) { // executa a opção
            case 1 -> view.mostrarMensagem("Cadastrada: " + gestao.cadastrarAula(
                    view.lerInteiro("Sala (1 Spinning, 2 Musculação, 3 Fit Dance, 4 Pilates)"),
                    view.lerDataHora("Início"), view.lerTexto("Instrutor"))); // cadastra a aula
            case 2 -> view.mostrarLista("Aulas", gestao.listarAulas()); // lista
            case 3 -> view.mostrarMensagem(String.valueOf(gestao.consultarAgendaSemanal(view.lerInteiro("Sala"),
                    view.lerData("Um dia da semana")))); // mostra a grade
            case 4 -> view.mostrarMensagem("Vagas: " + gestao.verificarVagas(view.lerInteiro("ID da aula"))); // vagas
            case 5 -> agendarCliente(gestao); // agenda
            case 6 -> { // confirma
                sistema.confirmarAgendamento(view.lerInteiro("ID do agendamento"), LocalDateTime.now()); // pagamento e extrato
                view.mostrarMensagem("Agendamento confirmado e extrato salvo no cliente."); // confirma
            } // fim do case 6
            case 7 -> view.mostrarMensagem("Devolvido ao cliente: R$ " + String.format("%.2f",
                    sistema.cancelarAgendamento(view.lerInteiro("ID do agendamento"), LocalDate.now()))); // cancela
            case 8 -> view.mostrarMensagem("Cancelados: " + gestao.cancelarVencidos(LocalDate.now())); // vencidos
            case 9 -> view.mostrarMensagem(String.valueOf(gestao.buscarProximoAgendamento(
                    view.lerInteiro("ID do cliente"), LocalDate.now()))); // próximo ou null
            case 10 -> view.mostrarLista("Agendamentos", gestao.listarAgendamentos()); // lista
            default -> { } // voltar
        } // fim do switch
        salvarDados(); // grava aulas, agendamentos e pagamentos
        return true; // continua no mesmo submenu
    } // fim de menuAgenda

    /**
     * Cria um agendamento preliminar, mostrando o preço da tabela para o funcionário confirmar.
     *
     * @param gestao as regras da agenda
     */
    private void agendarCliente(GestaoAgendamentos gestao) { // agendamento preliminar
        Cliente cliente = sistema.getGestaoClientes().buscarCliente(view.lerInteiro("ID do cliente")); // quem agenda
        List<Integer> idsAulas = view.lerInteiros("IDs das aulas"); // quais aulas
        String tipo = view.lerInteiro("1 - Diária | 2 - Mensalidade") == 2 ? Agendamento.MENSALIDADE : Agendamento.DIARIA; // cobrança
        double sugerido = gestao.calcularPrecoSugerido(idsAulas, tipo); // preço da tabela
        double preco = view.lerDecimal("Preço (tabela: R$ " + String.format("%.2f", sugerido) + "; 0 para aceitar)"); // funcionário confirma
        if (preco == 0) { // aceitou a tabela?
            preco = sugerido; // usa o preço sugerido
        } // fim do if
        Agendamento agendamento = gestao.agendar(cliente, idsAulas, tipo, preco, view.lerTexto("Número do cartão")); // agenda
        view.mostrarMensagem("Agendado: " + agendamento + " | confirmar até " + agendamento.calcularPrazoConfirmacao()); // prazo
    } // fim de agendarCliente

    /**
     * Menu da lojinha: cadastrar, remover, verificar, repor, vender e listar. Os produtos são escolhidos pelo ID mostrado na lista.
     *
     * @return {@code true} para continuar no submenu; {@code false} quando a pessoa escolhe voltar
     */
    private boolean menuLoja() { // lojinha
        GestaoVendas gestao = sistema.getGestaoVendas(); // exige login
        int opcao = view.mostrarMenu("LOJINHA", new String[]{"1 - Cadastrar produto", "2 - Remover produto",
            "3 - Verificar produto", "4 - Repor estoque", "5 - Registrar venda", "6 - Listar produtos", "0 - Voltar"}); // opções
        if (opcao == 0) { // 0 volta ao menu principal
            return false; // sai do submenu
        } // fim do if
        switch (opcao) { // executa a opção
            case 1 -> view.mostrarMensagem("Cadastrado: " + gestao.cadastrarProduto(view.lerTexto("Nome"),
                    view.lerDecimal("Preço"), view.lerInteiro("Quantidade"))); // cadastra
            case 2 -> { // remove
                Produto produto = escolherProdutoDaLista(gestao, Produto.LOJA); // mostra a lista e acha o produto pelo ID
                if (produto != null) { // o usuário escolheu um produto?
                    gestao.removerProduto(produto.getId()); // tira do estoque
                    view.mostrarMensagem("Produto removido: " + produto.getNome()); // confirma
                } // fim do if
            } // fim do case 2
            case 3 -> { // consulta
                Produto produto = escolherProdutoDaLista(gestao, Produto.LOJA); // mostra a lista e acha o produto pelo ID
                if (produto != null) { // o usuário escolheu um produto?
                    view.mostrarMensagem(String.valueOf(gestao.verificarProduto(produto.getId()))); // mostra o produto
                } // fim do if
            } // fim do case 3
            case 4 -> { // repõe
                Produto produto = escolherProdutoDaLista(gestao, Produto.LOJA); // mostra a lista e acha o produto pelo ID
                if (produto != null) { // o usuário escolheu um produto?
                    gestao.reporEstoque(produto.getId(), view.lerInteiro("Quantidade que chegou")); // soma
                    view.mostrarMensagem("Estoque atualizado: " + gestao.verificarProduto(produto.getId())); // confirma
                } // fim do if
            } // fim do case 4
            case 5 -> registrarVendaProdutos(gestao, Produto.LOJA); // vende
            case 6 -> view.mostrarLista("Produtos", gestao.getEstoque().listarPorSetor(Produto.LOJA)); // lista só a lojinha
            default -> { } // voltar
        } // fim do switch
        salvarDados(); // grava o estoque e as vendas
        return true; // continua no mesmo submenu
    } // fim de menuLoja

    /**
     * Menu da lanchonete: cadastrar item, ver o cardápio, repor e vender. Usa o mesmo estoque e as mesmas
     * vendas da lojinha; só mostra os produtos do setor LANCHONETE. Os itens são escolhidos pelo ID mostrado na lista.
     *
     * @return {@code true} para continuar no submenu; {@code false} quando a pessoa escolhe voltar
     */
    private boolean menuLanchonete() { // lanchonete
        GestaoVendas gestao = sistema.getGestaoVendas(); // exige login; o mesmo estoque da lojinha
        int opcao = view.mostrarMenu("LANCHONETE", new String[]{"1 - Cadastrar item", "2 - Ver cardápio",
            "3 - Repor estoque", "4 - Registrar venda", "0 - Voltar"}); // opções
        if (opcao == 0) { // 0 volta ao menu principal
            return false; // sai do submenu
        } // fim do if
        switch (opcao) { // executa a opção
            case 1 -> view.mostrarMensagem("Cadastrado: " + gestao.cadastrarProduto(view.lerTexto("Nome"),
                    view.lerDecimal("Preço"), view.lerInteiro("Quantidade"), Produto.LANCHONETE)); // cadastra na lanchonete
            case 2 -> { // cardápio
                List<Produto> cardapio = gestao.getEstoque().listarPorSetor(Produto.LANCHONETE); // itens da lanchonete
                if (cardapio.isEmpty()) { // ainda não tem itens?
                    view.mostrarErro("Nenhum item cadastrado na lanchonete."); // avisa
                } else { // tem itens
                    mostrarProdutosComId(cardapio); // ID, nome, preço e estoque
                } // fim do if
            } // fim do case 2
            case 3 -> { // repõe
                Produto produto = escolherProdutoDaLista(gestao, Produto.LANCHONETE); // mostra o cardápio e acha o item pelo ID
                if (produto != null) { // o usuário escolheu um item?
                    gestao.reporEstoque(produto.getId(), view.lerInteiro("Quantidade que chegou")); // soma
                    view.mostrarMensagem("Estoque atualizado: " + gestao.verificarProduto(produto.getId())); // confirma
                } // fim do if
            } // fim do case 3
            case 4 -> registrarVendaProdutos(gestao, Produto.LANCHONETE); // vende
            default -> { } // voltar
        } // fim do switch
        salvarDados(); // grava o estoque e as vendas
        return true; // continua no mesmo submenu
    } // fim de menuLanchonete

    /**
     * Registra uma venda de produtos (lojinha ou lanchonete): mostra primeiro a lista do setor com o ID de cada
     * produto, pede o cliente e depois o ID de cada produto e a quantidade. Se faltar estoque, pergunta se vende
     * o que há ou retira o item.
     *
     * @param gestao as regras da lojinha
     * @param setor {@link Produto#LOJA} ou {@link Produto#LANCHONETE}
     */
    private void registrarVendaProdutos(GestaoVendas gestao, String setor) { // venda escolhendo os produtos pelo ID mostrado na lista
        List<Produto> produtos = gestao.getEstoque().listarPorSetor(setor); // produtos à venda neste setor
        if (produtos.isEmpty()) { // setor vazio?
            view.mostrarErro("Nenhum produto cadastrado."); // avisa
            return; // nada a vender
        } // fim do if
        mostrarProdutosComId(produtos); // lista antes de registrar a venda
        int idCliente = view.lerInteiro("ID do cliente"); // quem compra
        List<Integer> idsFinais = new ArrayList<>(); // itens que vão para a venda
        List<Integer> quantidadesFinais = new ArrayList<>(); // quantidades que vão para a venda
        int numero = view.lerInteiro("ID do produto (0 para terminar)"); // primeiro produto
        while (numero != 0) { // 0 encerra a escolha
            Produto produto = produtoPorId(produtos, numero); // ID da lista vira produto
            if (produto != null) { // número válido?
                if (jaEstaNaVenda(idsFinais, produto.getId())) { // não repete o produto na mesma venda
                    view.mostrarErro("Esse produto já está na venda."); // avisa
                } else { // produto novo na venda
                    int quantidade = view.lerInteiro("Quantidade de " + produto.getNome()); // quanto leva
                    if (quantidade > produto.getQuantidade()) { // falta estoque?
                        quantidade = ajustarItemSemEstoque(produto, quantidade); // pergunta o que fazer
                    } // fim do if
                    if (quantidade > 0) { // o item continua na venda?
                        idsFinais.add(produto.getId()); // entra
                        quantidadesFinais.add(quantidade); // com a quantidade final
                    } // fim do if
                } // fim do if interno
            } // fim do if
            numero = view.lerInteiro("ID do produto (0 para terminar)"); // próximo produto
        } // fim do while
        if (idsFinais.isEmpty()) { // nenhum item sobrou?
            view.mostrarMensagem("Nenhum item na venda. Venda não registrada."); // nada a vender
            return; // não cria a venda
        } // fim do if
        Cliente cliente = sistema.getGestaoClientes().buscarCliente(idCliente); // para o extrato
        sistema.registrarVendaLojinha(idCliente, idsFinais, quantidadesFinais); // vende e salva o extrato
        IteradorLista<Extrato> iterador = new IteradorLista<>(cliente.getExtratos()); // extratos do cliente
        Extrato ultimo = null; // o último é o desta venda
        while (iterador.hasNext()) { // percorre até o fim
            ultimo = iterador.next(); // guarda o último
        } // fim do while
        view.imprimirExtrato(ultimo); // imprime o extrato da venda
    } // fim de registrarVendaProdutos

    /**
     * Mostra a lista de produtos de um setor com o ID de cada um e pede o ID de um deles.
     *
     * @param gestao as regras da lojinha
     * @param setor {@link Produto#LOJA} ou {@link Produto#LANCHONETE}
     * @return o produto escolhido, ou {@code null} se não há produtos ou o ID não está na lista
     */
    private Produto escolherProdutoDaLista(GestaoVendas gestao, String setor) { // lista com ID e escolha pelo ID
        List<Produto> produtos = gestao.getEstoque().listarPorSetor(setor); // produtos do setor
        if (produtos.isEmpty()) { // lojinha vazia?
            view.mostrarErro("Nenhum produto cadastrado."); // avisa
            return null; // sem produto
        } // fim do if
        mostrarProdutosComId(produtos); // mostra a lista
        return produtoPorId(produtos, view.lerInteiro("ID do produto")); // escolhe pelo ID
    } // fim de escolherProdutoDaLista

    /**
     * Mostra a lista de produtos com o ID de cada um, o nome, o preço e o estoque.
     *
     * @param produtos a lista de produtos
     */
    private void mostrarProdutosComId(List<Produto> produtos) { // ID - produto
        view.mostrarMensagem("--- Produtos (ID - produto) ---"); // título
        IteradorLista<Produto> iterador = new IteradorLista<>(produtos); // percorre os produtos
        while (iterador.hasNext()) { // enquanto houver produto
            Produto produto = iterador.next(); // pega o próximo
            view.mostrarMensagem(produto.getId() + " - " + produto.getNome() + " - R$ " + String.format("%.2f", produto.getPreco())
                    + " - estoque: " + produto.getQuantidade()); // uma linha por produto
        } // fim do while
    } // fim de mostrarProdutosComId

    /**
     * Acha na lista o produto que tem o ID digitado.
     *
     * @param produtos a lista mostrada na tela
     * @param id o ID digitado
     * @return o produto, ou {@code null} se o ID não está na lista
     */
    private Produto produtoPorId(List<Produto> produtos, int id) { // ID da lista vira produto
        IteradorLista<Produto> iterador = new IteradorLista<>(produtos); // percorre os produtos
        while (iterador.hasNext()) { // enquanto houver produto
            Produto produto = iterador.next(); // pega o próximo
            if (produto.getId() == id) { // é o ID digitado?
                return produto; // achou
            } // fim do if
        } // fim do while
        view.mostrarErro("Esse ID não está na lista."); // não achou
        return null; // sem produto
    } // fim de produtoPorId

    /**
     * Confere se o produto já foi colocado na venda em andamento.
     *
     * @param ids os produtos já colocados
     * @param id o produto a conferir
     * @return {@code true} se já está na venda
     */
    private boolean jaEstaNaVenda(List<Integer> ids, int id) { // o produto já foi colocado nesta venda?
        IteradorLista<Integer> iterador = new IteradorLista<>(ids); // percorre os produtos da venda
        while (iterador.hasNext()) { // enquanto houver produto
            if (iterador.next() == id) { // é o mesmo?
                return true; // já está
            } // fim do if
        } // fim do while
        return false; // ainda não está
    } // fim de jaEstaNaVenda

    /**
     * Trata o pedido maior que o estoque: vende o que há ou retira o item.
     *
     * @param produto o produto pedido
     * @param pedida a quantidade pedida
     * @return a quantidade final, ou 0 para retirar o item
     */
    private int ajustarItemSemEstoque(Produto produto, int pedida) { // falta estoque: vende o que há ou retira o item
        view.mostrarMensagem("Pedido: " + pedida + " | em estoque: " + produto.getQuantidade() + " (" + produto.getNome() + ")"); // avisa
        if (produto.getQuantidade() > 0 && view.lerInteiro("1 - Vender o que há | 2 - Retirar o item") == 1) { // vende o que há?
            return produto.getQuantidade(); // a quantidade disponível
        } // fim do if
        return 0; // retira o item
    } // fim de ajustarItemSemEstoque

    /**
     * Terminal da catraca: o cliente digita o ID e entra ou sai. Não precisa de login.
     */
    private void terminalCatraca() { // cliente digita o ID; não precisa de login
        try { // o ID pode não existir
            RegistroCatraca registro = sistema.passarNaCatraca(view.lerInteiro("Digite seu ID"), LocalDateTime.now()); // passa
            view.mostrarMensagem(registro + " | pessoas na academia: " + sistema.getCatraca().getQuantidadePresentes()); // resultado
            salvarDados(); // grava o registro
        } catch (IllegalArgumentException erro) { // cliente inválido
            view.mostrarErro(erro.getMessage()); // avisa
        } // fim do try
    } // fim de terminalCatraca

    /**
     * Menu dos relatórios: vendas, pagamentos, catraca, dados dos clientes e relatórios já gerados.
     *
     * @return {@code true} para continuar no submenu; {@code false} quando a pessoa escolhe voltar
     */
    private boolean menuRelatorios() { // relatórios para qualquer funcionário
        GestaoFinanceiro financeiro = sistema.getGestaoFinanceiro(); // exige login
        int opcao = view.mostrarMenu("RELATÓRIOS", new String[]{"1 - Vendas da lojinha e lanchonete do dia",
            "2 - Vendas da lojinha e lanchonete do mês",
            "3 - Diárias e mensalidades do dia", "4 - Diárias e mensalidades do mês", "5 - Catraca do dia",
            "6 - Catraca do mês", "7 - Catraca do ano", "8 - Dados dos clientes", "9 - Relatórios já gerados", "0 - Voltar"}); // opções
        if (opcao == 0) { // 0 volta ao menu principal
            return false; // sai do submenu
        } // fim do if
        switch (opcao) { // executa a opção
            case 1 -> view.mostrarMensagem(financeiro.gerarRelatorioVendasDia(view.lerData("Dia")).toString()); // lojinha dia
            case 2 -> view.mostrarMensagem(financeiro.gerarRelatorioVendasMes(view.lerInteiro("Ano"),
                    view.lerInteiro("Mês")).toString()); // lojinha mês
            case 3 -> view.mostrarMensagem(financeiro.gerarRelatorioPagamentosDia(view.lerData("Dia")).toString()); // aulas dia
            case 4 -> view.mostrarMensagem(financeiro.gerarRelatorioPagamentosMes(view.lerInteiro("Ano"),
                    view.lerInteiro("Mês")).toString()); // aulas mês
            case 5 -> view.mostrarLista("Catraca", sistema.getCatraca().registrosDoDia(view.lerData("Dia"))); // catraca dia
            case 6 -> view.mostrarLista("Catraca", sistema.getCatraca().registrosDoMes(view.lerInteiro("Ano"),
                    view.lerInteiro("Mês"))); // catraca mês
            case 7 -> view.mostrarLista("Catraca", sistema.getCatraca().registrosDoAno(view.lerInteiro("Ano"))); // catraca ano
            case 8 -> view.mostrarLista("Clientes", sistema.getGestaoClientes().listarClientes()); // dados dos clientes
            case 9 -> view.mostrarLista("Relatórios", financeiro.listarRelatorios()); // já gerados
            default -> { } // voltar
        } // fim do switch
        salvarDados(); // grava os relatórios gerados
        return true; // continua no mesmo submenu
    } // fim de menuRelatorios

    /**
     * Menu do financeiro: lançar, remover e listar despesas e ver o balanço (só o administrador).
     *
     * @return {@code true} para continuar no submenu; {@code false} quando a pessoa escolhe voltar
     */
    private boolean menuFinanceiro() { // despesas e balanço: só administrador (a GestaoFinanceiro confere)
        GestaoFinanceiro financeiro = sistema.getGestaoFinanceiro(); // exige login
        int opcao = view.mostrarMenu("FINANCEIRO", new String[]{"1 - Lançar despesa", "2 - Remover despesa",
            "3 - Listar despesas", "4 - Balanço mensal", "0 - Voltar"}); // opções
        if (opcao == 0) { // 0 volta ao menu principal
            return false; // sai do submenu
        } // fim do if
        switch (opcao) { // executa a opção
            case 1 -> view.mostrarMensagem("Lançada: " + financeiro.cadastrarDespesa(sistema.getUsuarioLogado(),
                    view.lerTexto("Descrição"), categoriaDespesa(view.lerInteiro("1 Limpeza, 2 Funcionários, 3 Material, 4 Outros")),
                    view.lerDecimal("Valor"), view.lerData("Data"))); // lança
            case 2 -> { // remove
                financeiro.removerDespesa(sistema.getUsuarioLogado(), view.lerInteiro("ID")); // apaga
                view.mostrarMensagem("Despesa removida."); // confirma
            } // fim do case 2
            case 3 -> view.mostrarLista("Despesas", financeiro.listarDespesas(sistema.getUsuarioLogado())); // lista
            case 4 -> view.mostrarMensagem(financeiro.gerarBalancoMensal(sistema.getUsuarioLogado(),
                    view.lerInteiro("Ano"), view.lerInteiro("Mês")).toString()); // balanço
            default -> { } // voltar
        } // fim do switch
        salvarDados(); // grava as despesas
        return true; // continua no mesmo submenu
    } // fim de menuFinanceiro

    /**
     * Converte o número digitado na categoria da despesa.
     *
     * @param opcao 1 Limpeza, 2 Funcionários, 3 Material, outro número Outros
     * @return a categoria
     */
    private String categoriaDespesa(int opcao) { // número digitado vira categoria
        return switch (opcao) { // escolhe a constante
            case 1 -> Despesa.LIMPEZA; // limpeza
            case 2 -> Despesa.FUNCIONARIOS; // funcionários
            case 3 -> Despesa.MATERIAL; // material
            default -> Despesa.OUTROS; // outros
        }; // fim do switch
    } // fim de categoriaDespesa

    /**
     * Menu dos funcionários: cadastrar, editar, remover, alterar senha e listar (só o administrador).
     *
     * @return {@code true} para continuar no submenu; {@code false} quando a pessoa escolhe voltar
     */
    private boolean menuFuncionarios() { // só administrador
        GestaoFuncionarios gestao = sistema.getGestaoFuncionarios(); // exige administrador
        int opcao = view.mostrarMenu("FUNCIONÁRIOS", new String[]{"1 - Cadastrar colaborador", "2 - Cadastrar administrador",
            "3 - Editar", "4 - Remover", "5 - Alterar senha", "6 - Listar", "0 - Voltar"}); // opções
        if (opcao == 0) { // 0 volta ao menu principal
            return false; // sai do submenu
        } // fim do if
        switch (opcao) { // executa a opção
            case 1 -> view.mostrarMensagem("Cadastrado: " + gestao.cadastrarColaborador(view.lerTexto("Nome"),
                    view.lerTexto("Endereço"), view.lerTexto("Telefone"), view.lerTexto("E-mail"), view.lerTexto("Senha"))); // colaborador
            case 2 -> view.mostrarMensagem("Cadastrado: " + gestao.cadastrarAdministrador(view.lerTexto("Nome"),
                    view.lerTexto("Endereço"), view.lerTexto("Telefone"), view.lerTexto("E-mail"), view.lerTexto("Senha"))); // administrador
            case 3 -> { // edita
                gestao.editarFuncionario(view.lerInteiro("ID"), view.lerTexto("Nome"), view.lerTexto("Endereço"),
                        view.lerTexto("Telefone"), view.lerTexto("E-mail")); // altera
                view.mostrarMensagem("Funcionário alterado."); // confirma
            } // fim do case 3
            case 4 -> { // remove
                gestao.removerFuncionario(view.lerInteiro("ID"), sistema.getUsuarioLogado().getId()); // apaga
                view.mostrarMensagem("Funcionário removido."); // confirma
            } // fim do case 4
            case 5 -> view.mostrarMensagem(gestao.alterarSenha(view.lerInteiro("ID"), view.lerTexto("Senha atual"),
                    view.lerTexto("Nova senha")) ? "Senha alterada." : "Senha atual incorreta."); // troca a senha
            case 6 -> view.mostrarLista("Funcionários", gestao.listarFuncionarios()); // lista
            default -> { } // voltar
        } // fim do switch
        salvarDados(); // grava os funcionários
        return true; // continua no mesmo submenu
    } // fim de menuFuncionarios

    /**
     * Grava o academia.json. Os clientes já são gravados pelo ClienteDAOJson. Se falhar, só avisa.
     */
    private void salvarDados() { // grava o academia.json (os clientes já são gravados pelo ClienteDAOJson)
        try { // a gravação pode falhar
            sistema.salvar(repositorio); // grava
        } catch (DaoException erro) { // não conseguiu gravar
            view.mostrarErro(erro.getMessage()); // avisa
        } // fim do try
    } // fim de salvarDados

    @Override // sobrescreve o toString de Object
    /**
     * Resumo do controller em uma linha.
     *
     * @return o texto com o Model e a View ligados
     */
    public String toString() { // resumo em uma linha
        return "AcademiaController com " + sistema + " e " + view; // partes ligadas
    } // fim do toString
}