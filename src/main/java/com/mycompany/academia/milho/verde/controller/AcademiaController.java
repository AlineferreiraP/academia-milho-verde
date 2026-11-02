/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.controller;

import com.mycompany.academia.milho.verde.model.Agendamento;
import com.mycompany.academia.milho.verde.model.AgendaSemanal;
import com.mycompany.academia.milho.verde.model.Aula;
import com.mycompany.academia.milho.verde.model.Cliente;
import com.mycompany.academia.milho.verde.model.ComparadorAgendamento;
import com.mycompany.academia.milho.verde.model.ComparadorCliente;
import com.mycompany.academia.milho.verde.model.Despesa;
import com.mycompany.academia.milho.verde.model.Extrato;
import com.mycompany.academia.milho.verde.model.Funcionario;
import com.mycompany.academia.milho.verde.model.GestaoAgendamentos;
import com.mycompany.academia.milho.verde.model.GestaoClientes;
import com.mycompany.academia.milho.verde.model.GestaoFinanceiro;
import com.mycompany.academia.milho.verde.model.GestaoFuncionarios;
import com.mycompany.academia.milho.verde.model.GestaoVendas;
import com.mycompany.academia.milho.verde.model.Produto;
import com.mycompany.academia.milho.verde.model.RegistroCatraca;
import com.mycompany.academia.milho.verde.model.RepositorioJson;
import com.mycompany.academia.milho.verde.model.Sistema;
import com.mycompany.academia.milho.verde.model.Venda;
import com.mycompany.academia.milho.verde.view.ConsoleView;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;

/**
 * Controlador da Academia Milho Verde (camada Controller do MVC).
 *
 * <p>Faz a ligação entre a {@link ConsoleView}, que lê e mostra, e o
 * {@link Sistema}, que guarda as regras. Recebe a opção escolhida na
 * View, chama o Sistema e devolve à View o resultado ou a mensagem de
 * erro. Não tem regra de negócio: toda validação está no Model, e os
 * erros chegam aqui como {@link IllegalArgumentException} ou
 * {@link IllegalStateException}.</p>
 *
 * <p>Os dados são carregados do JSON ao iniciar e gravados depois de
 * cada operação, para que uma falha não faça perder o que já foi
 * feito.</p>
 *
 * @author Aline Ferreira
 */
public class AcademiaController {

    /** Sistema que guarda os dados e as regras da academia. */
    private Sistema sistema;

    /** View que lê do teclado e mostra na tela. */
    private ConsoleView view;

    /** Repositório que grava e lê o arquivo JSON. */
    private RepositorioJson repositorio;

    /**
     * Construtor padrão: cria o sistema, a view e o repositório
     * padrão ({@code academia.json}).
     */
    public AcademiaController() {
        this(new Sistema(), new ConsoleView(), new RepositorioJson());
    }

    /**
     * Construtor com parâmetros.
     *
     * @param novoSistema sistema a controlar
     * @param novaView view usada para ler e mostrar
     * @param novoRepositorio repositório JSON
     * @throws IllegalArgumentException se algum parâmetro for nulo
     */
    public AcademiaController(Sistema novoSistema, ConsoleView novaView,
                              RepositorioJson novoRepositorio) {
        if (novoSistema == null || novaView == null
                || novoRepositorio == null) {
            throw new IllegalArgumentException(
                    "Sistema, view e repositório são obrigatórios.");
        }
        this.sistema = novoSistema;
        this.view = novaView;
        this.repositorio = novoRepositorio;
    }

    /**
     * Executa o programa: carrega os dados, faz o primeiro acesso se
     * for preciso, pede o login e mostra o menu principal até o
     * usuário sair.
     */
    public void iniciar() {
        view.mostrarMensagem("===== Academia Milho Verde =====");
        try {
            carregarDados();
            prepararPrimeiroAcesso();
            fazerLogin();
            while (sistema.getUsuarioLogado() != null) {
                executarMenuPrincipal();
            }
        } catch (IllegalStateException erro) {
            // A entrada acabou (ou o primeiro acesso falhou): encerra.
            view.mostrarErro(erro.getMessage());
        }
        view.mostrarMensagem("Até logo!");
    }

    /**
     * Mostra o menu principal uma vez e executa a opção escolhida.
     */
    private void executarMenuPrincipal() {
        Funcionario usuario = sistema.getUsuarioLogado();
        boolean financeiro = usuario.podeAcessarFinanceiro();
        String[] opcoes;
        if (financeiro) {
            opcoes = new String[] {"Clientes", "Funcionários",
                "Agenda e agendamentos", "Lojinha e lanchonete",
                "Catraca", "Relatórios de vendas", "Financeiro"};
        } else {
            opcoes = new String[] {"Clientes", "Funcionários",
                "Agenda e agendamentos", "Lojinha e lanchonete",
                "Catraca", "Relatórios de vendas"};
        }
        int opcao = view.mostrarMenu(
                "Menu principal - " + usuario.getNome(), opcoes);
        switch (opcao) {
            case 1:
                menuClientes();
                break;
            case 2:
                menuFuncionarios();
                break;
            case 3:
                menuAgenda();
                break;
            case 4:
                menuLoja();
                break;
            case 5:
                menuCatraca();
                break;
            case 6:
                menuRelatorios();
                break;
            case 7:
                menuFinanceiro();
                break;
            default:
                sistema.logout();
                salvarDados();
                view.mostrarMensagem("Sessão encerrada.");
                fazerLogin();
                break;
        }
        salvarDados();
    }

    // ----------------------------------------------------- dados e acesso

    /**
     * Carrega os dados do arquivo JSON, se ele existir.
     */
    private void carregarDados() {
        try {
            sistema.carregar(repositorio);
        } catch (IOException | JSONException | IllegalArgumentException
                 | IllegalStateException erro) {
            view.mostrarErro("Não foi possível carregar " + repositorio.getArquivo()
                    + ": " + erro.getMessage()
                    + ". O sistema começa vazio.");
        }
    }

    /**
     * Grava os dados no arquivo JSON e mostra o erro, se houver.
     */
    private void salvarDados() {
        try {
            sistema.salvar(repositorio);
        } catch (IOException | IllegalArgumentException
                 | IllegalStateException erro) {
            view.mostrarErro("Não foi possível salvar os dados: "
                    + erro.getMessage());
        }
    }

    /**
     * Se ainda não há funcionários, cadastra o primeiro administrador.
     */
    private void prepararPrimeiroAcesso() {
        boolean cadastrado = false;
        while (!cadastrado && !sistema.temFuncionarios()) {
            view.mostrarMensagem("Primeiro acesso: cadastre o administrador.");
            try {
                String nome = view.lerTexto("Nome");
                String email = view.lerTexto("E-mail");
                String senha = view.lerTexto("Senha");
                sistema.criarPrimeiroAdministrador(nome, email, senha);
                salvarDados();
                cadastrado = true;
            } catch (IllegalArgumentException erro) {
                view.mostrarErro(erro.getMessage());
            }
        }
    }

    /**
     * Pede e-mail e senha até o login dar certo ou o usuário desistir.
     */
    private void fazerLogin() {
        boolean tentar = true;
        while (tentar && sistema.getUsuarioLogado() == null) {
            view.mostrarMensagem("--- Login ---");
            String email = view.lerTexto("E-mail");
            String senha = view.lerTexto("Senha");
            if (sistema.login(email, senha)) {
                view.mostrarMensagem("Bem-vindo(a), "
                        + sistema.getUsuarioLogado().getNome() + "!");
            } else {
                view.mostrarErro("E-mail ou senha incorretos.");
                tentar = view.mostrarMenu("Tentar de novo?",
                        new String[] {"Sim"}) == 1;
            }
        }
    }

    // ------------------------------------------------------------ clientes

    /**
     * Menu de clientes (cadastro, busca, listagem e conta).
     */
    private void menuClientes() {
        GestaoClientes gestao = sistema.getGestaoClientes();
        int opcao = -1;
        while (opcao != 0) {
            opcao = view.mostrarMenu("Clientes", new String[] {
                "Cadastrar", "Editar", "Remover (desativar)",
                "Buscar por ID", "Listar", "Emitir conta"});
            try {
                switch (opcao) {
                    case 1:
                        Cliente novo = gestao.cadastrarCliente(
                                view.lerTexto("Nome"),
                                view.lerTexto("Endereço"),
                                view.lerTexto("Telefone"),
                                view.lerTexto("E-mail"),
                                view.lerTexto("CPF"));
                        view.mostrarMensagem("Cadastrado: " + novo);
                        break;
                    case 2:
                        gestao.editarCliente(view.lerInteiro("ID do cliente"),
                                view.lerTexto("Novo nome"),
                                view.lerTexto("Novo endereço"),
                                view.lerTexto("Novo telefone"),
                                view.lerTexto("Novo e-mail"),
                                view.lerTexto("CPF"));
                        view.mostrarMensagem("Cliente atualizado.");
                        break;
                    case 3:
                        gestao.removerCliente(view.lerInteiro("ID do cliente"));
                        view.mostrarMensagem("Cliente desativado.");
                        break;
                    case 4:
                        Cliente achado = gestao.buscarCliente(
                                view.lerInteiro("ID do cliente"));
                        view.mostrarMensagem(achado == null
                                ? "Cliente não encontrado." : achado.toString());
                        break;
                    case 5:
                        int ordem = view.mostrarMenu("Ordenar por",
                                new String[] {"ID", "Nome"});
                        String criterio = (ordem == 2) ? ComparadorCliente.NOME
                                : ComparadorCliente.ID;
                        view.mostrarLista("Clientes ativos",
                                gestao.listarClientes(criterio));
                        break;
                    case 6:
                        emitirConta(gestao);
                        break;
                    default:
                        break;
                }
            } catch (IllegalArgumentException | IllegalStateException erro) {
                view.mostrarErro(erro.getMessage());
            }
        }
    }

    /**
     * Mostra a conta (extratos e total) de um cliente.
     *
     * @param gestao gestão de clientes
     */
    private void emitirConta(GestaoClientes gestao) {
        int id = view.lerInteiro("ID do cliente");
        List<Extrato> extratos = gestao.emitirConta(id);
        if (extratos.isEmpty()) {
            view.mostrarMensagem("O cliente ainda não tem compras.");
            return;
        }
        for (Extrato extrato : extratos) {
            view.imprimirExtrato(extrato);
        }
        view.mostrarMensagem("Total da conta: R$ "
                + String.format("%.2f", gestao.calcularTotalConta(id)));
    }

    // ------------------------------------------------------- funcionários

    /**
     * Menu de funcionários (colaboradores e administradores).
     */
    private void menuFuncionarios() {
        GestaoFuncionarios gestao = sistema.getGestaoFuncionarios();
        int opcao = -1;
        while (opcao != 0) {
            opcao = view.mostrarMenu("Funcionários", new String[] {
                "Cadastrar colaborador", "Cadastrar administrador",
                "Editar", "Remover (desativar)",
                "Alterar senha de administrador", "Listar"});
            try {
                switch (opcao) {
                    case 1:
                        view.mostrarMensagem("Cadastrado: "
                                + gestao.cadastrarColaborador(
                                        view.lerTexto("Nome"),
                                        view.lerTexto("E-mail"),
                                        view.lerTexto("Senha")));
                        break;
                    case 2:
                        view.mostrarMensagem("Cadastrado: "
                                + gestao.cadastrarAdministrador(
                                        view.lerTexto("Nome"),
                                        view.lerTexto("E-mail"),
                                        view.lerTexto("Senha")));
                        break;
                    case 3:
                        gestao.editarFuncionario(view.lerInteiro("ID"),
                                view.lerTexto("Novo nome"),
                                view.lerTexto("Novo e-mail"));
                        view.mostrarMensagem("Funcionário atualizado.");
                        break;
                    case 4:
                        gestao.removerFuncionario(view.lerInteiro("ID"));
                        view.mostrarMensagem("Funcionário desativado.");
                        break;
                    case 5:
                        boolean trocou = gestao.alterarSenhaAdministrador(
                                view.lerInteiro("ID do administrador"),
                                view.lerTexto("Senha atual"),
                                view.lerTexto("Nova senha"));
                        view.mostrarMensagem(trocou ? "Senha alterada."
                                : "Senha atual incorreta.");
                        break;
                    case 6:
                        view.mostrarLista("Funcionários ativos",
                                gestao.listarFuncionarios());
                        break;
                    default:
                        break;
                }
            } catch (IllegalArgumentException | IllegalStateException erro) {
                view.mostrarErro(erro.getMessage());
            }
        }
    }

    // ------------------------------------------------------------- agenda

    /**
     * Menu da agenda: aulas, vagas e agendamentos.
     */
    private void menuAgenda() {
        GestaoAgendamentos gestao = sistema.getGestaoAgendamentos();
        int opcao = -1;
        while (opcao != 0) {
            opcao = view.mostrarMenu("Agenda e agendamentos", new String[] {
                "Cadastrar aula", "Remover aula", "Listar aulas",
                "Consultar agenda semanal", "Verificar vagas de uma aula",
                "Agendar cliente", "Confirmar agendamento",
                "Cancelar agendamento", "Listar agendamentos"});
            try {
                switch (opcao) {
                    case 1:
                        view.mostrarLista("Salas", java.util.Arrays.asList(
                                Sistema.getSalas()));
                        Aula aula = gestao.cadastrarAula(
                                view.lerInteiro("Código da sala"),
                                view.lerDataHora("Data e hora da aula"),
                                view.lerTexto("Nome do instrutor"));
                        view.mostrarMensagem("Cadastrada: " + aula);
                        break;
                    case 2:
                        gestao.removerAula(view.lerInteiro("ID da aula"));
                        view.mostrarMensagem("Aula removida.");
                        break;
                    case 3:
                        view.mostrarLista("Aulas", gestao.listarAulas());
                        break;
                    case 4:
                        AgendaSemanal agenda = gestao.consultarAgendaSemanal(
                                view.lerInteiro("Código da sala"),
                                view.lerData("Qualquer dia da semana"));
                        view.mostrarMensagem(agenda.toString());
                        break;
                    case 5:
                        view.mostrarMensagem("Vagas livres: "
                                + gestao.verificarVagas(
                                        view.lerInteiro("ID da aula")));
                        break;
                    case 6:
                        agendarCliente(gestao);
                        break;
                    case 7:
                        confirmarAgendamento(gestao);
                        break;
                    case 8:
                        double devolvido = gestao.cancelarAgendamento(
                                view.lerInteiro("ID do agendamento"),
                                LocalDate.now());
                        view.mostrarMensagem("Agendamento cancelado. "
                                + "Valor devolvido: R$ "
                                + String.format("%.2f", devolvido));
                        break;
                    case 9:
                        int ordem = view.mostrarMenu("Ordenar por",
                                new String[] {"Data", "Valor"});
                        String criterio = (ordem == 2)
                                ? ComparadorAgendamento.VALOR
                                : ComparadorAgendamento.DATA;
                        view.mostrarLista("Agendamentos",
                                gestao.listarAgendamentos(criterio));
                        break;
                    default:
                        break;
                }
            } catch (IllegalArgumentException | IllegalStateException erro) {
                view.mostrarErro(erro.getMessage());
            }
        }
    }

    /**
     * Pede os dados e cria um agendamento preliminar.
     *
     * @param gestao gestão de agendamentos
     */
    private void agendarCliente(GestaoAgendamentos gestao) {
        Cliente cliente = sistema.getGestaoClientes().buscarCliente(
                view.lerInteiro("ID do cliente"));
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente não encontrado.");
        }
        List<Integer> ids = view.lerInteiros("IDs das aulas, separados por vírgula");
        int tipoEscolhido = view.mostrarMenu("Tipo de cobrança",
                new String[] {"Diária", "Mensalidade"});
        String tipo = (tipoEscolhido == 2) ? Agendamento.MENSALIDADE
                : Agendamento.DIARIA;
        double preco = view.lerDecimal("Preço em reais");
        String cartao = view.lerTexto("Número do cartão");
        Agendamento agendamento = gestao.agendar(cliente, ids, tipo, preco, cartao);
        view.mostrarMensagem("Agendamento criado: " + agendamento);
        view.mostrarMensagem("Confirme até "
                + agendamento.calcularPrazoConfirmacao() + ".");
    }

    /**
     * Confirma um agendamento e imprime o extrato da cobrança.
     *
     * @param gestao gestão de agendamentos
     */
    private void confirmarAgendamento(GestaoAgendamentos gestao) {
        Venda venda = gestao.confirmarAgendamento(
                view.lerInteiro("ID do agendamento"), LocalDate.now());
        view.mostrarMensagem("Agendamento confirmado.");
        view.imprimirExtrato(venda.getExtrato());
    }

    // --------------------------------------------------------------- loja

    /**
     * Menu da lojinha e da lanchonete: produtos, estoque e vendas.
     */
    private void menuLoja() {
        GestaoVendas gestao = sistema.getGestaoVendas();
        int opcao = -1;
        while (opcao != 0) {
            opcao = view.mostrarMenu("Lojinha e lanchonete", new String[] {
                "Cadastrar produto", "Remover produto", "Listar produtos",
                "Buscar produto por nome", "Repor estoque",
                "Registrar venda de produtos"});
            try {
                switch (opcao) {
                    case 1:
                        int tipo = view.mostrarMenu("Categoria",
                                new String[] {"Lojinha", "Lanchonete"});
                        String categoria = (tipo == 2) ? Produto.LANCHONETE
                                : Produto.LOJA;
                        Produto produto = gestao.cadastrarProduto(
                                view.lerTexto("Nome"), categoria,
                                view.lerDecimal("Preço"),
                                view.lerInteiro("Quantidade inicial"));
                        view.mostrarMensagem("Cadastrado: " + produto);
                        break;
                    case 2:
                        gestao.removerProduto(view.lerInteiro("ID do produto"));
                        view.mostrarMensagem("Produto removido.");
                        break;
                    case 3:
                        view.mostrarLista("Produtos",
                                gestao.getEstoque().getProdutos());
                        break;
                    case 4:
                        view.mostrarLista("Encontrados",
                                gestao.getEstoque().buscarPorNome(
                                        view.lerTexto("Parte do nome")));
                        break;
                    case 5:
                        gestao.reporEstoque(view.lerInteiro("ID do produto"),
                                view.lerInteiro("Quantidade a repor"));
                        view.mostrarMensagem("Estoque atualizado.");
                        break;
                    case 6:
                        registrarVendaProdutos(gestao);
                        break;
                    default:
                        break;
                }
            } catch (IllegalArgumentException | IllegalStateException erro) {
                view.mostrarErro(erro.getMessage());
            }
        }
    }

    /**
     * Monta uma venda de produtos item por item e imprime o extrato.
     *
     * @param gestao gestão de vendas
     */
    private void registrarVendaProdutos(GestaoVendas gestao) {
        Cliente cliente = sistema.getGestaoClientes().buscarCliente(
                view.lerInteiro("ID do cliente"));
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente não encontrado.");
        }
        List<Integer> ids = new ArrayList<>();
        List<Integer> quantidades = new ArrayList<>();
        int idProduto = view.lerInteiro("ID do produto (0 para terminar)");
        while (idProduto != 0) {
            ids.add(idProduto);
            quantidades.add(view.lerInteiro("Quantidade"));
            idProduto = view.lerInteiro("ID do produto (0 para terminar)");
        }
        Venda venda = gestao.registrarVenda(cliente, ids, quantidades);
        view.imprimirExtrato(venda.getExtrato());
    }

    // ------------------------------------------------------------ catraca

    /**
     * Menu da catraca: simula os eventos da catraca externa e consulta
     * os registros.
     */
    private void menuCatraca() {
        int opcao = -1;
        while (opcao != 0) {
            opcao = view.mostrarMenu("Catraca", new String[] {
                "Registrar entrada", "Registrar saída", "Quem está na academia",
                "Registros de um dia", "Registros de um mês",
                "Registros de um ano", "Registros de um cliente"});
            try {
                switch (opcao) {
                    case 1:
                        registrarEvento(RegistroCatraca.ENTRADA);
                        break;
                    case 2:
                        registrarEvento(RegistroCatraca.SAIDA);
                        break;
                    case 3:
                        view.mostrarMensagem("Pessoas na academia: "
                                + sistema.getCatraca().getQuantidadePresentes());
                        view.mostrarLista("Presentes",
                                sistema.getCatraca().getPresentes());
                        break;
                    case 4:
                        view.mostrarLista("Registros do dia",
                                sistema.getCatraca().registrosDoDia(
                                        view.lerData("Dia")));
                        break;
                    case 5:
                        view.mostrarLista("Registros do mês",
                                sistema.getCatraca().registrosDoMes(
                                        view.lerInteiro("Ano"),
                                        view.lerInteiro("Mês")));
                        break;
                    case 6:
                        view.mostrarLista("Registros do ano",
                                sistema.getCatraca().registrosDoAno(
                                        view.lerInteiro("Ano")));
                        break;
                    case 7:
                        view.mostrarLista("Registros do cliente",
                                sistema.getCatraca().registrosDoCliente(
                                        view.lerInteiro("ID do cliente")));
                        break;
                    default:
                        break;
                }
            } catch (IllegalArgumentException | IllegalStateException erro) {
                view.mostrarErro(erro.getMessage());
            }
        }
    }

    /**
     * Repassa ao sistema um evento da catraca, com a hora atual.
     *
     * @param tipo {@link RegistroCatraca#ENTRADA} ou
     *        {@link RegistroCatraca#SAIDA}
     */
    private void registrarEvento(String tipo) {
        RegistroCatraca registro = sistema.receberEventoCatraca(
                view.lerInteiro("ID do cliente"), tipo, LocalDateTime.now());
        view.mostrarMensagem("Registrado: " + registro);
    }

    // ---------------------------------------------------------- relatórios

    /**
     * Menu de relatórios de vendas (dia e mês).
     */
    private void menuRelatorios() {
        GestaoVendas gestao = sistema.getGestaoVendas();
        int opcao = -1;
        while (opcao != 0) {
            opcao = view.mostrarMenu("Relatórios de vendas", new String[] {
                "Vendas de um dia", "Vendas de um mês"});
            try {
                switch (opcao) {
                    case 1:
                        view.mostrarMensagem(gestao.gerarRelatorioVendasDia(
                                view.lerData("Dia")).toString());
                        break;
                    case 2:
                        view.mostrarMensagem(gestao.gerarRelatorioVendasMes(
                                view.lerInteiro("Ano"),
                                view.lerInteiro("Mês")).toString());
                        break;
                    default:
                        break;
                }
            } catch (IllegalArgumentException | IllegalStateException erro) {
                view.mostrarErro(erro.getMessage());
            }
        }
    }

    // ---------------------------------------------------------- financeiro

    /**
     * Menu financeiro: despesas e balanço mensal. Só o administrador
     * chega aqui; o Sistema recusa os demais.
     */
    private void menuFinanceiro() {
        GestaoFinanceiro gestao = sistema.getGestaoFinanceiro();
        int opcao = -1;
        while (opcao != 0) {
            opcao = view.mostrarMenu("Financeiro", new String[] {
                "Cadastrar despesa", "Remover despesa", "Listar despesas",
                "Balanço mensal"});
            try {
                switch (opcao) {
                    case 1:
                        int tipo = view.mostrarMenu("Categoria", new String[] {
                            "Limpeza", "Funcionários", "Material", "Outros"});
                        Despesa despesa = gestao.cadastrarDespesa(
                                view.lerTexto("Descrição"),
                                categoriaDespesa(tipo),
                                view.lerDecimal("Valor"),
                                view.lerData("Data"));
                        view.mostrarMensagem("Cadastrada: " + despesa);
                        break;
                    case 2:
                        gestao.removerDespesa(view.lerInteiro("ID da despesa"));
                        view.mostrarMensagem("Despesa removida.");
                        break;
                    case 3:
                        view.mostrarLista("Despesas", gestao.listarDespesas());
                        break;
                    case 4:
                        view.mostrarMensagem(gestao.gerarBalancoMensal(
                                view.lerInteiro("Ano"),
                                view.lerInteiro("Mês")).toString());
                        break;
                    default:
                        break;
                }
            } catch (IllegalArgumentException | IllegalStateException erro) {
                view.mostrarErro(erro.getMessage());
            }
        }
    }

    /**
     * Converte a opção do menu na categoria de despesa.
     *
     * @param opcao número escolhido (1 a 4)
     * @return a constante de categoria de {@link Despesa}
     */
    private String categoriaDespesa(int opcao) {
        switch (opcao) {
            case 1:
                return Despesa.LIMPEZA;
            case 2:
                return Despesa.FUNCIONARIOS;
            case 3:
                return Despesa.MATERIAL;
            default:
                return Despesa.OUTROS;
        }
    }

    /**
     * Retorna uma descrição do controlador.
     *
     * @return texto com o arquivo JSON usado
     */
    @Override
    public String toString() {
        return "AcademiaController[arquivo=" + repositorio.getArquivo() + "]";
    }
}
