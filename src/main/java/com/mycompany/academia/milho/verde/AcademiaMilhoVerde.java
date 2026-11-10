/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.academia.milho.verde;

import com.mycompany.academia.milho.verde.controller.AcademiaController;
import com.mycompany.academia.milho.verde.model.Administrador;
import com.mycompany.academia.milho.verde.model.AgendaSemanal;
import com.mycompany.academia.milho.verde.model.Agendamento;
import com.mycompany.academia.milho.verde.model.BalancoMensal;
import com.mycompany.academia.milho.verde.model.Aula;
import com.mycompany.academia.milho.verde.model.Cliente;
import com.mycompany.academia.milho.verde.model.Colaborador;
import com.mycompany.academia.milho.verde.model.ComparadorAgendamento;
import com.mycompany.academia.milho.verde.model.ComparadorCliente;
import com.mycompany.academia.milho.verde.model.Estoque;
import com.mycompany.academia.milho.verde.model.Extrato;
import com.mycompany.academia.milho.verde.model.Funcionario;
import com.mycompany.academia.milho.verde.model.GestaoAgendamentos;
import com.mycompany.academia.milho.verde.model.GestaoClientes;
import com.mycompany.academia.milho.verde.model.GestaoFinanceiro;
import com.mycompany.academia.milho.verde.model.GestaoFuncionarios;
import com.mycompany.academia.milho.verde.model.GestaoVendas;
import com.mycompany.academia.milho.verde.model.ItemVenda;
import com.mycompany.academia.milho.verde.model.Produto;
import com.mycompany.academia.milho.verde.model.RegistroCatraca;
import com.mycompany.academia.milho.verde.model.RelatorioVendas;
import com.mycompany.academia.milho.verde.model.RepositorioJson;
import com.mycompany.academia.milho.verde.model.Sala;
import com.mycompany.academia.milho.verde.model.Sistema;
import com.mycompany.academia.milho.verde.model.Venda;
import java.io.IOException;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import com.mycompany.academia.milho.verde.dao.ClienteDAOJson;

/**
 * Classe principal da Academia Milho Verde.
 *
 * <p>
 * O main testa as classes do Model: clientes, funcionários, a lojinha e, em
 * seguida, o {@link Sistema} com todas as gestões (login, agenda, vendas,
 * catraca, financeiro, comparadores, contadores) e a gravação e leitura em
 * JSON. Ao final dos testes, abre o programa interativo com menus pelo
 * {@link AcademiaController}.</p>
 *
 * @author Aline Ferreira
 */
public class AcademiaMilhoVerde {

    /**
     * Classe só com o main: não deve ser instanciada.
     */
    private AcademiaMilhoVerde() {
    }

    /**
     * Ponto de entrada do programa.
     *
     * @param args argumentos da linha de comando não utilizados
     */
    public static void main(String[] args) throws IOException {

        // ------------------------------------------------- clientes
        System.out.println("=== Clientes ===");

        // Construtor com parâmetros.
        Cliente ana = new Cliente(1, "Ana Souza", "Rua das Flores, 10",
                "(38) 99999-0000", "ana@gmail.com", "a1b2c3");
        Cliente joao = new Cliente(2, "João Lima", "Av. Central, 5",
                "(38) 98888-1111", "joao@gmail.com", "d4e5f6");
        Cliente carla = new Cliente(3, "Carla Mendes", "Rua do Rosário, 42",
                "(38) 97777-2222", "carla@gmail.com", "g7h8i9");
        Cliente pedro = new Cliente(4, "Pedro Alves", "Travessa da Matriz, 3",
                "(31) 96666-3333", "pedro@gmail.com", "j1k2l3");
        Cliente lucia = new Cliente(5, "Lúcia Ferreira", "Rua Direita, 18",
                "(38) 95555-4444", "lucia@gmail.com", "m4n5o6");
        Cliente rafael = new Cliente(6, "Rafael Costa", "Rua do Comércio, 7",
                "(11) 94444-5555", "rafael@gmail.com", "p7q8r9");
        Cliente juliana = new Cliente(7, "Juliana Martins", "Largo do Rosário, 2",
                "(38) 93333-6666", "juliana@gmail.com", "s1t2u3");
        Cliente thiago = new Cliente(8, "Thiago Ribeiro", "Rua da Cachoeira, 25",
                "(31) 92222-7777", "thiago@gmail.com", "v4w5x6");
        joao.desativar();

        // Construtor padrão.
        Cliente vazio = new Cliente();
        System.out.println("Construtor padrão: id=" + vazio.getId()
                + ", ativo=" + vazio.isAtivo());

        // Validação: deve cair no catch.
        try {
            new Cliente(9, "Maria", "   ", "123", "m@email.com", "x");
        } catch (IllegalArgumentException erro) {
            System.out.println("Validação OK: " + erro.getMessage());
        }

        // atualizarCadastro e toString.
        ana.atualizarCadastro("Ana Souza Lima", "Rua das Flores, 20",
                "(38) 99999-0000", "ana@gmail.com", "a1b2c3");
        System.out.println("Atualizado: " + ana);

        // Dado inválido: nada muda, nem o nome.
        try {
            ana.atualizarCadastro("Nome Novo", "Rua X", "111", "   ", "a1");
        } catch (IllegalArgumentException erro) {
            System.out.println("Nada mudou: " + ana.getNome()
                    + " (" + erro.getMessage() + ")");
        }

        // equals e hashCode: o mesmo ID representa o mesmo cliente.
        Cliente mesmaAna = new Cliente(1, "Outro Nome", "Outra Rua",
                "000", "x@email.com", "zzz");
        System.out.println("Mesmo ID é igual: " + ana.equals(mesmaAna));
        System.out.println("Mesmo hash: "
                + (ana.hashCode() == mesmaAna.hashCode()));
        System.out.println("IDs diferentes: " + ana.equals(joao));

        // ------------------------------------------------- funcionários
        System.out.println();
        System.out.println("=== Funcionários ===");

        // Polimorfismo: a variável é Funcionario, o objeto é de cada subclasse.
        Funcionario bia = new Colaborador(1, "Bia Rocha", "bia@academia.com", "bia123");
        Funcionario marcos = new Colaborador(2, "Marcos Dias", "marcos@academia.com", "marcos123");
        Funcionario carlos = new Administrador(3, "Carlos Pena", "carlos@academia.com", "carlos123");
        System.out.println(bia + " financeiro: " + bia.podeAcessarFinanceiro());
        System.out.println(carlos + " financeiro: " + carlos.podeAcessarFinanceiro());

        // Construtores padrão das subclasses.
        System.out.println("Colaborador padrão: " + new Colaborador());
        System.out.println("Administrador padrão: " + new Administrador());

        // Senha: verificar e alterar.
        System.out.println("Senha certa: " + carlos.verificarSenha("carlos123"));
        System.out.println("Senha errada: " + carlos.verificarSenha("000"));
        System.out.println("Troca com senha errada: "
                + carlos.alterarSenha("000", "nova456"));
        System.out.println("Troca com senha certa: "
                + carlos.alterarSenha("carlos123", "nova456"));
        System.out.println("Nova senha vale: " + carlos.verificarSenha("nova456"));

        // editarDados e desativar.
        bia.editarDados("Beatriz Rocha", "beatriz@academia.com");
        System.out.println("Editado: " + bia);
        marcos.desativar();

        // ------------------------------------------------- lojinha
        System.out.println();
        System.out.println("=== Lojinha e lanchonete ===");

        // Produtos das duas categorias, cadastrados no estoque.
        Estoque estoque = new Estoque();
        estoque.adicionarProduto(new Produto(1, "Garrafa de água 750 ml",
                Produto.LOJA, 25.0, 10));
        estoque.adicionarProduto(new Produto(2, "Luva de musculação",
                Produto.LOJA, 40.0, 3));
        estoque.adicionarProduto(new Produto(3, "Barra de proteína",
                Produto.LANCHONETE, 8.5, 20));
        System.out.println(estoque);
        for (Produto p : estoque.getProdutos()) {
            System.out.println(p);
        }

        // Construtor padrão do produto.
        System.out.println("Produto padrão: " + new Produto());

        // Categoria inválida: deve cair no catch.
        try {
            new Produto(4, "Toalha", "ROUPA", 30.0, 5);
        } catch (IllegalArgumentException erro) {
            System.out.println("Validação OK: " + erro.getMessage());
        }

        // Busca percorrendo e comparando.
        System.out.println("Busca 'barra': " + estoque.buscarPorNome("barra"));
        System.out.println("Produto 99 existe? "
                + (estoque.buscarProduto(99) != null));

        // Venda de produtos: confere tudo antes de dar baixa.
        Venda compra = new Venda(1, ana, Venda.PRODUTO, LocalDateTime.now());
        int[] ids = {1, 3};
        int[] quantidades = {2, 3};
        boolean disponivel = true;
        for (int i = 0; i < ids.length; i++) {
            if (!estoque.verificarDisponibilidade(ids[i], quantidades[i])) {
                disponivel = false;
            }
        }
        if (disponivel) {
            for (int i = 0; i < ids.length; i++) {
                compra.adicionarItem(new ItemVenda(
                        estoque.buscarProduto(ids[i]), quantidades[i]));
                estoque.darBaixa(ids[i], quantidades[i]);
            }
        }
        Extrato extratoCompra = compra.gerarExtrato(1);
        System.out.println(compra);
        System.out.println(extratoCompra.gerarTexto());
        System.out.println("Garrafas depois da venda: "
                + estoque.buscarProduto(1).getQuantidade());

        // Estoque insuficiente: nada muda.
        System.out.println("Há 5 luvas? "
                + estoque.verificarDisponibilidade(2, 5));
        try {
            estoque.darBaixa(2, 5);
        } catch (IllegalStateException erro) {
            System.out.println("Baixa recusada: " + erro.getMessage()
                    + " Luvas: " + estoque.buscarProduto(2).getQuantidade());
        }

        // Reposição.
        estoque.reporEstoque(2, 7);
        System.out.println("Luvas após reposição: "
                + estoque.buscarProduto(2).getQuantidade());

        // Venda de diária: sem itens, o valor vem do agendamento.
        Venda diaria = new Venda(2, ana, Venda.DIARIA, LocalDateTime.now());
        diaria.setValorTotal(35.0);
        diaria.gerarExtrato(2);
        System.out.println(diaria);

        // Diária não aceita itens.
        try {
            diaria.adicionarItem(new ItemVenda(estoque.buscarProduto(3), 1));
        } catch (IllegalStateException erro) {
            System.out.println("Item recusado: " + erro.getMessage());
        }

        // Um extrato por venda.
        try {
            compra.gerarExtrato(3);
        } catch (IllegalStateException erro) {
            System.out.println("Extrato repetido recusado: "
                    + erro.getMessage());
        }

        // Conta da cliente: os extratos ficam guardados nela (item 10).
        System.out.println("Extratos de " + ana.getNome() + ":");
        for (Extrato e : ana.getExtratos()) {
            System.out.println("  " + e);
        }

        // ------------------------------------------------- Sistema
        System.out.println();
        System.out.println("=== Sistema: login e permissões ===");
        Files.deleteIfExists(Path.of("clientes-teste.json"));
        Sistema sistema = new Sistema(new ClienteDAOJson(Path.of("clientes-teste.json")));
        try {
            sistema.getGestaoClientes();
        } catch (IllegalStateException erro) {
            System.out.println("Sem login: " + erro.getMessage());
        }
        System.out.println("Login sem cadastro: "
                + sistema.login("ninguem@academia.com", "123"));
        sistema.criarPrimeiroAdministrador("Carlos Pena",
                "carlos@academia.com", "carlos123");
        try {
            sistema.criarPrimeiroAdministrador("Outro", "o@academia.com", "123456");
        } catch (IllegalStateException erro) {
            System.out.println("Segundo administrador inicial recusado: "
                    + erro.getMessage());
        }
        System.out.println("Senha errada: "
                + sistema.login("carlos@academia.com", "errada"));
        System.out.println("Login do administrador: "
                + sistema.login("carlos@academia.com", "carlos123"));
        System.out.println(sistema);

        // ------------------------------------------------- funcionários (gestão)
        System.out.println();
        System.out.println("=== Gestão de funcionários ===");
        GestaoFuncionarios funcionarios = sistema.getGestaoFuncionarios();
        Colaborador bia2 = funcionarios.cadastrarColaborador("Bia Rocha",
                "bia@academia.com", "bia123");
        System.out.println("Cadastrado: " + bia2);
        try {
            funcionarios.cadastrarColaborador("Outra Bia", "BIA@academia.com", "abc123");
        } catch (IllegalArgumentException erro) {
            System.out.println("E-mail repetido recusado: " + erro.getMessage());
        }
        try {
            funcionarios.removerFuncionario(1);
        } catch (IllegalStateException erro) {
            System.out.println("Último administrador: " + erro.getMessage());
        }
        System.out.println("Senha trocada com senha errada: "
                + funcionarios.alterarSenhaAdministrador(1, "000", "nova456"));
        System.out.println("Senha trocada com senha certa: "
                + funcionarios.alterarSenhaAdministrador(1, "carlos123", "carlos123"));
        System.out.println("Ativos: " + funcionarios.listarFuncionarios());
        System.out.println(funcionarios);

        // ------------------------------------------------- clientes (gestão)
        System.out.println();
        System.out.println("=== Gestão de clientes ===");
        GestaoClientes clientes = sistema.getGestaoClientes();
        Cliente cAna = clientes.cadastrarCliente("Ana Souza", "Rua das Flores, 10",
                "(38) 99999-0000", "ana@gmail.com", "123.456.789-01");
        Cliente cJoao = clientes.cadastrarCliente("João Lima", "Av. Central, 5",
                "(38) 98888-1111", "joao@gmail.com", "98765432100");
        Cliente cCarla = clientes.cadastrarCliente("Carla Mendes",
                "Rua do Rosário, 42", "(38) 97777-2222", "carla@gmail.com",
                "11122233344");
        System.out.println("CPF pseudonimizado de Ana: " + cAna.getCpfPseudo());
        try {
            clientes.cadastrarCliente("Ana Clone", "Rua Z", "1", "z@z.com", "12345678901");
        } catch (IllegalArgumentException erro) {
            System.out.println("CPF repetido recusado: " + erro.getMessage());
        }
        try {
            clientes.cadastrarCliente("Sem CPF", "Rua Z", "1", "z@z.com", "123");
        } catch (IllegalArgumentException erro) {
            System.out.println("CPF inválido recusado: " + erro.getMessage());
        }
        clientes.editarCliente(cJoao.getId(), "João Lima Filho", "Av. Central, 50",
                "(38) 98888-1111", "joao@gmail.com", "98765432100");
        System.out.println("Busca 3: " + clientes.buscarCliente(3).getNome());
        System.out.println("Busca 99: " + clientes.buscarCliente(99));
        clientes.removerCliente(cCarla.getId());
        System.out.println("Ativos por nome: "
                + clientes.listarClientes(ComparadorCliente.NOME));
        System.out.println("Carla ainda existe, desativada: "
                + !clientes.buscarCliente(3).isAtivo());

        // ------------------------------------------------- aulas e agenda
        System.out.println();
        System.out.println("=== Aulas, agenda e agendamentos ===");
        GestaoAgendamentos agenda = sistema.getGestaoAgendamentos();
        LocalDate hoje = LocalDate.now();
        LocalDate dia = diaUtil(hoje.plusDays(30));
        for (Sala sala : Sistema.getSalas()) {
            System.out.println(sala);
        }
        Aula spinning = agenda.cadastrarAula(1, dia.atTime(9, 0), "Rafa");
        Aula spinning2 = agenda.cadastrarAula(1, dia.atTime(10, 0), "Rafa");
        Aula pilates = agenda.cadastrarAula(4, dia.atTime(9, 0), "Marina");
        System.out.println("Cadastradas: " + spinning.getId() + ", "
                + spinning2.getId() + ", " + pilates.getId());
        try {
            agenda.cadastrarAula(1, dia.atTime(9, 0), "Outro");
        } catch (IllegalStateException erro) {
            System.out.println("Horário ocupado: " + erro.getMessage());
        }
        try {
            agenda.cadastrarAula(1, domingoApos(dia).atTime(9, 0), "Outro");
        } catch (IllegalArgumentException erro) {
            System.out.println("Domingo recusado: " + erro.getMessage());
        }
        try {
            agenda.cadastrarAula(1, dia.atTime(23, 0), "Outro");
        } catch (IllegalArgumentException erro) {
            System.out.println("Fora do horário recusado: " + erro.getMessage());
        }
        try {
            agenda.cadastrarAula(9, dia.atTime(9, 0), "Outro");
        } catch (IllegalArgumentException erro) {
            System.out.println("Sala inexistente: " + erro.getMessage());
        }
        System.out.println("Aulas: " + agenda.listarAulas().size()
                + ", busca 2: " + agenda.buscarAula(2));
        AgendaSemanal semana = agenda.consultarAgendaSemanal(1, dia);
        System.out.println("Livre às 9h de " + dia + "? "
                + semana.estaLivre(dia.getDayOfWeek().getValue() - 1, 4));
        System.out.println(semana);
        System.out.println("Vagas da aula 1: " + agenda.verificarVagas(1));

        // Agendamento: preliminar, confirmação, cancelamentos.
        Agendamento ag1 = agenda.agendar(cAna, List.of(1, 2),
                Agendamento.MENSALIDADE, 120.0, "4111 1111 1111 1234");
        System.out.println(ag1);
        System.out.println("Prazo para confirmar: " + ag1.calcularPrazoConfirmacao());
        try {
            agenda.agendar(cAna, List.of(1), Agendamento.DIARIA, 30.0,
                    "4111111111111234");
        } catch (IllegalStateException erro) {
            System.out.println("Reserva repetida recusada: " + erro.getMessage());
        }
        try {
            agenda.agendar(cCarla, List.of(1), Agendamento.DIARIA, 30.0, "123");
        } catch (IllegalArgumentException erro) {
            System.out.println("Cartão ou cliente inválido: " + erro.getMessage());
        }
        System.out.println("Vagas da aula 1 agora: " + agenda.verificarVagas(1));
        Venda vendaAg1 = agenda.confirmarAgendamento(ag1.getId(), hoje);
        System.out.println(vendaAg1);
        System.out.println(vendaAg1.getExtrato().gerarTexto());
        double devolvido = agenda.cancelarAgendamento(ag1.getId(),
                ag1.getPrimeiraData().minusDays(10));
        System.out.println("Cancelamento com 10 dias de antecedência devolve R$ "
                + devolvido);

        Agendamento ag2 = agenda.agendar(cJoao, List.of(3), Agendamento.DIARIA,
                40.0, "5500000000000004");
        agenda.confirmarAgendamento(ag2.getId(), hoje);
        System.out.println("Cancelamento na véspera devolve R$ "
                + agenda.cancelarAgendamento(ag2.getId(),
                        ag2.getPrimeiraData().minusDays(1)));
        try {
            agenda.cancelarAgendamento(ag2.getId(), hoje);
        } catch (IllegalStateException erro) {
            System.out.println("Cancelar duas vezes: " + erro.getMessage());
        }

        Agendamento ag3 = agenda.agendar(cJoao, List.of(2), Agendamento.DIARIA,
                45.0, "5500000000000004");
        try {
            agenda.confirmarAgendamento(ag3.getId(),
                    ag3.calcularPrazoConfirmacao().plusDays(1));
        } catch (IllegalStateException erro) {
            System.out.println("Fora do prazo: " + erro.getMessage()
                    + " (situação: " + ag3.getStatus() + ")");
        }
        Agendamento ag4 = agenda.agendar(cAna, List.of(2), Agendamento.DIARIA,
                50.0, "4111111111111234");
        System.out.println("Vencidos cancelados: " + agenda.cancelarVencidos(
                ag4.calcularPrazoConfirmacao().plusDays(1)));
        Agendamento ag5 = agenda.agendar(cAna, List.of(1), Agendamento.DIARIA,
                30.0, "4111111111111234");
        System.out.println("Por data:");
        for (Agendamento a : agenda.listarAgendamentos(ComparadorAgendamento.DATA)) {
            System.out.println("  " + a);
        }
        System.out.println("Por valor:");
        for (Agendamento a : agenda.listarAgendamentos(ComparadorAgendamento.VALOR)) {
            System.out.println("  " + a);
        }
        agenda.removerAula(3);
        try {
            agenda.removerAula(1);
        } catch (IllegalStateException erro) {
            System.out.println("Aula com reserva: " + erro.getMessage());
        }
        System.out.println(agenda);

        // ------------------------------------------------- vendas (gestão)
        System.out.println();
        System.out.println("=== Gestão de vendas ===");
        GestaoVendas vendas = sistema.getGestaoVendas();
        Produto agua = vendas.cadastrarProduto("Água 500 ml", Produto.LANCHONETE, 4.0, 30);
        Produto luva = vendas.cadastrarProduto("Luva de musculação", Produto.LOJA, 40.0, 3);
        Produto barra = vendas.cadastrarProduto("Barra de proteína", Produto.LANCHONETE, 8.5, 20);
        System.out.println(vendas.verificarProduto(luva.getId()));
        Venda v1 = vendas.registrarVenda(cAna, List.of(agua.getId(), barra.getId()),
                List.of(2, 3));
        System.out.println(v1);
        try {
            vendas.registrarVenda(cAna, List.of(luva.getId(), barra.getId()),
                    List.of(5, 1));
        } catch (IllegalStateException erro) {
            System.out.println("Venda recusada: " + erro.getMessage()
                    + " Barras: " + barra.getQuantidade());
        }
        try {
            vendas.registrarVenda(cCarla, List.of(agua.getId()), List.of(1));
        } catch (IllegalArgumentException erro) {
            System.out.println("Cliente desativado: " + erro.getMessage());
        }
        try {
            vendas.registrarVenda(cAna, List.of(agua.getId(), agua.getId()),
                    List.of(1, 1));
        } catch (IllegalArgumentException erro) {
            System.out.println("Produto repetido: " + erro.getMessage());
        }
        vendas.reporEstoque(luva.getId(), 7);
        System.out.println("Luvas após reposição: " + luva.getQuantidade());
        vendas.removerProduto(agua.getId());
        try {
            vendas.verificarProduto(agua.getId());
        } catch (IllegalArgumentException erro) {
            System.out.println("Produto removido: " + erro.getMessage());
        }
        RelatorioVendas relDia = vendas.gerarRelatorioVendasDia(hoje);
        System.out.println(relDia);
        RelatorioVendas relMes = vendas.gerarRelatorioVendasMes(hoje.getYear(),
                hoje.getMonthValue());
        System.out.println(relMes);
        System.out.println("Conta da Ana: R$ " + String.format("%.2f",
                clientes.calcularTotalConta(cAna.getId()))
                + " em " + clientes.emitirConta(cAna.getId()).size() + " extratos");
        System.out.println(vendas);

        // ------------------------------------------------- catraca
        System.out.println();
        System.out.println("=== Catraca ===");
        LocalDateTime agora = LocalDateTime.now();
        RegistroCatraca entrada = sistema.receberEventoCatraca(cAna.getId(),
                RegistroCatraca.ENTRADA, agora);
        sistema.receberEventoCatraca(cJoao.getId(), RegistroCatraca.ENTRADA, agora);
        System.out.println(entrada);
        try {
            sistema.receberEventoCatraca(cAna.getId(), RegistroCatraca.ENTRADA, agora);
        } catch (IllegalStateException erro) {
            System.out.println("Entrada repetida: " + erro.getMessage());
        }
        try {
            sistema.receberEventoCatraca(cCarla.getId(), RegistroCatraca.ENTRADA, agora);
        } catch (IllegalArgumentException erro) {
            System.out.println("Cliente desativado: " + erro.getMessage());
        }
        System.out.println("Presentes: " + sistema.getCatraca().getQuantidadePresentes());
        sistema.receberEventoCatraca(cAna.getId(), RegistroCatraca.SAIDA, agora.plusHours(1));
        System.out.println("Presentes após a saída da Ana: "
                + sistema.getCatraca().getPresentes());
        System.out.println("Registros do dia: "
                + sistema.getCatraca().registrosDoDia(hoje).size()
                + ", do mês: " + sistema.getCatraca().registrosDoMes(
                        hoje.getYear(), hoje.getMonthValue()).size()
                + ", do ano: " + sistema.getCatraca().registrosDoAno(hoje.getYear()).size()
                + ", da Ana: " + sistema.getCatraca().registrosDoCliente(cAna.getId()).size());
        System.out.println(sistema.getCatraca());

        // ------------------------------------------------- financeiro
        System.out.println();
        System.out.println("=== Financeiro (só administrador) ===");
        GestaoFinanceiro financeiro = sistema.getGestaoFinanceiro();
        financeiro.cadastrarDespesa("Produtos de limpeza", "LIMPEZA", 180.0, hoje);
        financeiro.cadastrarDespesa("Salário da recepção", "FUNCIONARIOS", 1800.0, hoje);
        System.out.println("Despesas: " + financeiro.listarDespesas());
        financeiro.removerDespesa(1);
        try {
            financeiro.cadastrarDespesa("Despesa", "VIAGEM", 10.0, hoje);
        } catch (IllegalArgumentException erro) {
            System.out.println("Categoria inválida: " + erro.getMessage());
        }
        BalancoMensal balanco = financeiro.gerarBalancoMensal(hoje.getYear(),
                hoje.getMonthValue());
        System.out.println(balanco);
        sistema.logout();
        System.out.println("Login da Bia (colaboradora): "
                + sistema.login("bia@academia.com", "bia123"));
        try {
            sistema.getGestaoFinanceiro();
        } catch (IllegalStateException erro) {
            System.out.println("Colaboradora no financeiro: " + erro.getMessage());
        }
        System.out.println("Pode abrir clientes: "
                + (sistema.getGestaoClientes() != null));

        // ------------------------------------------------- contadores
        System.out.println();
        System.out.println("=== Contadores (itens 11 e 12) ===");
        System.out.println(Sistema.obterTotalInstancias());
        System.out.println("Clientes (private com get): " + Sistema.getTotalClientes());
        try {
            Sistema.setTotalClientes(-1);
        } catch (IllegalArgumentException erro) {
            System.out.println("Set negativo recusado: " + erro.getMessage());
        }

        // ------------------------------------------------- comparadores
        System.out.println();
        System.out.println("=== Comparadores (item 13) ===");
        List<Cliente> ordenar = new ArrayList<>(List.of(thiago, ana, juliana, carla));
        ordenar.sort(new ComparadorCliente(ComparadorCliente.NOME));
        System.out.print("Por nome:");
        for (Cliente c : ordenar) {
            System.out.print(" " + c.getNome() + ";");
        }
        ordenar.sort(new ComparadorCliente());
        System.out.print("\nPor ID:");
        for (Cliente c : ordenar) {
            System.out.print(" " + c.getId() + ";");
        }
        System.out.println();
        try {
            new ComparadorCliente("IDADE");
        } catch (IllegalArgumentException erro) {
            System.out.println("Critério inválido: " + erro.getMessage());
        }
        System.out.println(new ComparadorAgendamento(ComparadorAgendamento.VALOR));

        // ------------------------------------------------- JSON
        System.out.println();
        System.out.println("=== JSON (item 14) ===");
        RepositorioJson repositorio = new RepositorioJson(Path.of("academia-teste.json"));
        try {
            sistema.salvar(repositorio);
            System.out.println("Gravado em: "
                    + repositorio.getArquivo().toAbsolutePath());

            Sistema outro = new Sistema(new ClienteDAOJson(Path.of("clientes-teste.json")));
            outro.carregar(repositorio);
            System.out.println("Login após carregar: "
                    + outro.login("carlos@academia.com", "carlos123"));
            System.out.println("Clientes ativos: "
                    + outro.getGestaoClientes().listarClientes(ComparadorCliente.ID).size());
            System.out.println("Conta da Ana: R$ " + String.format("%.2f",
                    outro.getGestaoClientes().calcularTotalConta(cAna.getId())));
            System.out.println("Produtos: "
                    + outro.getGestaoVendas().getEstoque().getProdutos().size());
            System.out.println("Agendamentos: "
                    + outro.getGestaoAgendamentos()
                            .listarAgendamentos(ComparadorAgendamento.DATA).size());
            System.out.println("Aulas: "
                    + outro.getGestaoAgendamentos().listarAulas().size());
            System.out.println("Despesas: "
                    + outro.getGestaoFinanceiro().listarDespesas().size());
            System.out.println("Presentes na catraca: "
                    + outro.getCatraca().getQuantidadePresentes());
            System.out.println("Balanço igual: " + outro.getGestaoFinanceiro()
                    .gerarBalancoMensal(hoje.getYear(), hoje.getMonthValue())
                    .toString().equals(balanco.toString()));
        } catch (IOException erro) {
            System.out.println("Erro ao acessar o arquivo: "
                    + erro.getMessage());
        }

        // ------------------------------------------------- programa com menus
        // Depois dos testes, abre o programa interativo (MVC). Os dados
        // dele ficam em academia.json, separados do arquivo de teste.
        System.out.println();
        System.out.println("=== Programa com menus ===");
        new AcademiaController().iniciar();
    }

    /**
     * Devolve a própria data, ou a segunda-feira seguinte se for domingo.
     *
     * @param data data de partida
     * @return um dia em que a academia abre
     */
    private static LocalDate diaUtil(LocalDate data) {
        if (data.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return data.plusDays(1);
        }
        return data;
    }

    /**
     * Devolve o primeiro domingo a partir da data.
     *
     * @param data data de partida
     * @return o domingo da mesma semana ou da seguinte
     */
    private static LocalDate domingoApos(LocalDate data) {
        LocalDate resultado = data;
        while (resultado.getDayOfWeek() != DayOfWeek.SUNDAY) {
            resultado = resultado.plusDays(1);
        }
        return resultado;
    }
}
