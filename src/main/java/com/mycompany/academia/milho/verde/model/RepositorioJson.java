/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model; // pacote do Model (usa os métodos de pacote das gestões)

import com.mycompany.academia.milho.verde.dao.DaoException; // erro de arquivo
import java.io.BufferedReader; // lê o arquivo linha por linha
import java.io.BufferedWriter; // escreve no arquivo
import java.io.IOException; // erro de leitura ou gravação
import java.nio.file.Files; // abre, move e confere arquivos
import java.nio.file.Path; // caminho de um arquivo
import java.nio.file.StandardCopyOption; // opção de substituir o arquivo na troca
import java.time.DateTimeException; // data mal escrita no arquivo
import java.time.LocalDate; // data sem hora
import java.time.LocalDateTime; // data com hora
import java.util.ArrayList; // lista que cresce sozinha
import java.util.List; // tipo da lista
import org.json.JSONArray; // lista em formato JSON
import org.json.JSONException; // JSON mal formado
import org.json.JSONObject; // objeto em formato JSON

/**
 * Salva e carrega no {@code academia.json} tudo o que não é cliente: funcionários,
 * aulas, agendamentos, pagamentos, estoque, vendas, relatórios, despesas e registros da catraca.
 *
 * <p>Quando um objeto aponta para outro, o arquivo guarda só o ID; na leitura o ID
 * é trocado pelo objeto de verdade. A gravação é feita primeiro num arquivo
 * temporário, e o arquivo é sempre fechado em um bloco {@code finally}.</p>
 *
 * @author Aline Ferreira
 */
public class RepositorioJson { // salva e carrega no academia.json tudo o que não é cliente

    /** Chave dos funcionários. */
    private static final String CHAVE_FUNCIONARIOS = "funcionarios"; // chave dos funcionários
    /** Chave das aulas. */
    private static final String CHAVE_AULAS = "aulas"; // chave das aulas
    /** Chave dos agendamentos. */
    private static final String CHAVE_AGENDAMENTOS = "agendamentos"; // chave dos agendamentos
    /** Chave dos pagamentos de diárias e mensalidades. */
    private static final String CHAVE_PAGAMENTOS = "pagamentos"; // chave dos pagamentos de diárias e mensalidades
    /** Chave dos produtos com as quantidades. */
    private static final String CHAVE_ESTOQUE = "estoque"; // chave dos produtos com as quantidades
    /** Chave das vendas da lojinha. */
    private static final String CHAVE_VENDAS = "vendas"; // chave das vendas da lojinha
    /** Chave dos relatórios de vendas. */
    private static final String CHAVE_RELATORIOS = "relatorios"; // chave dos relatórios de vendas
    /** Chave das despesas. */
    private static final String CHAVE_DESPESAS = "despesas"; // chave das despesas
    /** Chave dos registros da catraca. */
    private static final String CHAVE_REGISTROS = "registrosCatraca"; // chave dos registros da catraca
    /** Perfil gravado no arquivo para o administrador. */
    private static final String ADMINISTRADOR = "ADMINISTRADOR"; // perfil gravado no arquivo
    /** Perfil gravado no arquivo para o colaborador. */
    private static final String COLABORADOR = "COLABORADOR"; // perfil gravado no arquivo

    /** Caminho do academia.json. */
    private final Path arquivo; // caminho do academia.json

    /**
     * Cria o repositório para o arquivo padrão {@code academia.json}, na pasta do projeto.
     */
    public RepositorioJson() { // construtor sem parâmetros
        this(Path.of("academia.json")); // arquivo padrão, na pasta do projeto
    } // fim do construtor sem parâmetros

    /**
     * Cria o repositório para o arquivo informado.
     *
     * @param caminhoArquivo caminho do arquivo JSON
     * @throws IllegalArgumentException se o caminho for nulo
     */
    public RepositorioJson(Path caminhoArquivo) { // recebe o caminho do arquivo
        if (caminhoArquivo == null) { // precisa de um caminho
            throw new IllegalArgumentException("Informe o arquivo."); // erro de argumento
        } // fim do if
        this.arquivo = caminhoArquivo; // guarda o caminho
    } // fim do construtor com parâmetros

    /**
     * Devolve o caminho do arquivo.
     *
     * @return caminho do arquivo
     */
    public Path getArquivo() { // devolve o caminho
        return arquivo; // caminho do arquivo
    } // fim de getArquivo

    /**
     * Grava tudo num arquivo só.
     *
     * @param clientes gestão de clientes (os clientes ficam no {@code clientes.json})
     * @param funcionarios gestão de funcionários
     * @param agendamentos gestão de agendamentos
     * @param vendas gestão da lojinha
     * @param financeiro gestão financeira
     * @param catraca catraca
     * @throws DaoException se não conseguir gravar o arquivo
     */
    public void salvar(GestaoClientes clientes, GestaoFuncionarios funcionarios, GestaoAgendamentos agendamentos,
            GestaoVendas vendas, GestaoFinanceiro financeiro, Catraca catraca) { // grava tudo num arquivo só
        JSONObject raiz = new JSONObject(); // objeto principal do arquivo

        JSONArray listaFuncionarios = new JSONArray(); // funcionários
        IteradorLista<Funcionario> itFuncionarios = new IteradorLista<>(funcionarios.listarFuncionarios()); // percorre
        while (itFuncionarios.hasNext()) { // enquanto houver funcionário
            listaFuncionarios.put(funcionarioParaJson(itFuncionarios.next())); // converte e acrescenta
        } // fim do while
        raiz.put(CHAVE_FUNCIONARIOS, listaFuncionarios); // guarda na raiz

        JSONArray listaAulas = new JSONArray(); // aulas
        IteradorLista<Aula> itAulas = new IteradorLista<>(agendamentos.listarAulas()); // percorre
        while (itAulas.hasNext()) { // enquanto houver aula
            listaAulas.put(aulaParaJson(itAulas.next())); // converte e acrescenta
        } // fim do while
        raiz.put(CHAVE_AULAS, listaAulas); // guarda na raiz

        JSONArray listaAgendamentos = new JSONArray(); // agendamentos
        IteradorLista<Agendamento> itAgendamentos = new IteradorLista<>(agendamentos.getAgendamentos()); // percorre
        while (itAgendamentos.hasNext()) { // enquanto houver agendamento
            listaAgendamentos.put(agendamentoParaJson(itAgendamentos.next())); // converte e acrescenta
        } // fim do while
        raiz.put(CHAVE_AGENDAMENTOS, listaAgendamentos); // guarda na raiz

        JSONArray listaPagamentos = new JSONArray(); // pagamentos de diárias e mensalidades
        IteradorLista<VendaAgendamento> itPagamentos = new IteradorLista<>(agendamentos.getPagamentos()); // percorre
        while (itPagamentos.hasNext()) { // enquanto houver pagamento
            listaPagamentos.put(pagamentoParaJson(itPagamentos.next())); // converte e acrescenta
        } // fim do while
        raiz.put(CHAVE_PAGAMENTOS, listaPagamentos); // guarda na raiz

        JSONArray listaEstoque = new JSONArray(); // produtos com as quantidades (o estoque)
        IteradorLista<Produto> itProdutos = new IteradorLista<>(vendas.getEstoque().getProdutos()); // percorre
        while (itProdutos.hasNext()) { // enquanto houver produto
            listaEstoque.put(produtoParaJson(itProdutos.next())); // converte e acrescenta
        } // fim do while
        raiz.put(CHAVE_ESTOQUE, listaEstoque); // guarda na raiz

        JSONArray listaVendas = new JSONArray(); // vendas da lojinha
        IteradorLista<VendaProdutos> itVendas = new IteradorLista<>(vendas.getVendas()); // percorre
        while (itVendas.hasNext()) { // enquanto houver venda
            listaVendas.put(vendaParaJson(itVendas.next())); // converte e acrescenta
        } // fim do while
        raiz.put(CHAVE_VENDAS, listaVendas); // guarda na raiz

        JSONArray listaRelatorios = new JSONArray(); // relatórios de vendas
        IteradorLista<RelatorioVendas> itRelatorios = new IteradorLista<>(financeiro.getRelatorios()); // percorre
        while (itRelatorios.hasNext()) { // enquanto houver relatório
            listaRelatorios.put(relatorioParaJson(itRelatorios.next())); // converte e acrescenta
        } // fim do while
        raiz.put(CHAVE_RELATORIOS, listaRelatorios); // guarda na raiz

        JSONArray listaDespesas = new JSONArray(); // despesas
        IteradorLista<Despesa> itDespesas = new IteradorLista<>(financeiro.getDespesas()); // percorre
        while (itDespesas.hasNext()) { // enquanto houver despesa
            listaDespesas.put(despesaParaJson(itDespesas.next())); // converte e acrescenta
        } // fim do while
        raiz.put(CHAVE_DESPESAS, listaDespesas); // guarda na raiz

        JSONArray listaRegistros = new JSONArray(); // registros da catraca
        IteradorLista<RegistroCatraca> itRegistros = new IteradorLista<>(catraca.getRegistros()); // percorre
        while (itRegistros.hasNext()) { // enquanto houver registro
            listaRegistros.put(registroParaJson(itRegistros.next())); // converte e acrescenta
        } // fim do while
        raiz.put(CHAVE_REGISTROS, listaRegistros); // guarda na raiz

        gravarTexto(raiz.toString(2)); // grava com recuo de 2 espaços
    } // fim de salvar

    /**
     * Lê tudo do arquivo e devolve às gestões. Se o arquivo ainda não existe, não faz nada.
     *
     * @param clientes gestão de clientes (os clientes já vieram do {@code clientes.json})
     * @param funcionarios gestão de funcionários
     * @param agendamentos gestão de agendamentos
     * @param vendas gestão da lojinha
     * @param financeiro gestão financeira
     * @param catraca catraca
     * @throws DaoException se o arquivo não puder ser lido ou tiver dados inválidos
     */
    public void carregar(GestaoClientes clientes, GestaoFuncionarios funcionarios, GestaoAgendamentos agendamentos,
            GestaoVendas vendas, GestaoFinanceiro financeiro, Catraca catraca) { // lê tudo do arquivo
        if (Files.notExists(arquivo)) { // primeiro uso: ainda não há arquivo
            return; // começa vazio
        } // fim do if
        try { // os dados do arquivo podem estar errados
            JSONObject raiz = new JSONObject(lerTexto()); // transforma o texto em objeto JSON
            List<Cliente> listaClientes = clientes.getClientes(); // os clientes já vieram do clientes.json

            List<Funcionario> lidosFuncionarios = new ArrayList<>(); // funcionários lidos
            JSONArray jsonFuncionarios = raiz.getJSONArray(CHAVE_FUNCIONARIOS); // lista do arquivo
            for (int i = 0; i < jsonFuncionarios.length(); i++) { // JSONArray usa índice
                lidosFuncionarios.add(jsonParaFuncionario(jsonFuncionarios.getJSONObject(i))); // converte
            } // fim do for
            funcionarios.carregar(lidosFuncionarios); // devolve à gestão

            List<Aula> lidasAulas = new ArrayList<>(); // aulas lidas
            JSONArray jsonAulas = raiz.getJSONArray(CHAVE_AULAS); // lista do arquivo
            for (int i = 0; i < jsonAulas.length(); i++) { // percorre o JSONArray
                lidasAulas.add(jsonParaAula(jsonAulas.getJSONObject(i))); // converte
            } // fim do for

            List<Agendamento> lidosAgendamentos = new ArrayList<>(); // agendamentos lidos
            JSONArray jsonAgendamentos = raiz.getJSONArray(CHAVE_AGENDAMENTOS); // lista do arquivo
            for (int i = 0; i < jsonAgendamentos.length(); i++) { // percorre o JSONArray
                lidosAgendamentos.add(jsonParaAgendamento(jsonAgendamentos.getJSONObject(i), listaClientes, lidasAulas)); // converte
            } // fim do for

            List<VendaAgendamento> lidosPagamentos = new ArrayList<>(); // pagamentos lidos
            JSONArray jsonPagamentos = raiz.getJSONArray(CHAVE_PAGAMENTOS); // lista do arquivo
            for (int i = 0; i < jsonPagamentos.length(); i++) { // percorre o JSONArray
                lidosPagamentos.add(jsonParaPagamento(jsonPagamentos.getJSONObject(i), lidosAgendamentos)); // converte
            } // fim do for
            agendamentos.carregar(lidasAulas, lidosAgendamentos, lidosPagamentos); // devolve à gestão

            List<Produto> lidosProdutos = new ArrayList<>(); // produtos lidos
            JSONArray jsonEstoque = raiz.getJSONArray(CHAVE_ESTOQUE); // lista do arquivo
            for (int i = 0; i < jsonEstoque.length(); i++) { // percorre o JSONArray
                lidosProdutos.add(jsonParaProduto(jsonEstoque.getJSONObject(i))); // converte
            } // fim do for

            List<VendaProdutos> lidasVendas = new ArrayList<>(); // vendas lidas
            JSONArray jsonVendas = raiz.getJSONArray(CHAVE_VENDAS); // lista do arquivo
            for (int i = 0; i < jsonVendas.length(); i++) { // percorre o JSONArray
                lidasVendas.add(jsonParaVenda(jsonVendas.getJSONObject(i), listaClientes, lidosProdutos)); // converte
            } // fim do for
            vendas.carregar(lidosProdutos, lidasVendas); // devolve à lojinha

            List<RelatorioVendas> lidosRelatorios = new ArrayList<>(); // relatórios lidos
            JSONArray jsonRelatorios = raiz.getJSONArray(CHAVE_RELATORIOS); // lista do arquivo
            for (int i = 0; i < jsonRelatorios.length(); i++) { // percorre o JSONArray
                lidosRelatorios.add(jsonParaRelatorio(jsonRelatorios.getJSONObject(i), lidasVendas, lidosPagamentos)); // converte
            } // fim do for

            List<Despesa> lidasDespesas = new ArrayList<>(); // despesas lidas
            JSONArray jsonDespesas = raiz.getJSONArray(CHAVE_DESPESAS); // lista do arquivo
            for (int i = 0; i < jsonDespesas.length(); i++) { // percorre o JSONArray
                lidasDespesas.add(jsonParaDespesa(jsonDespesas.getJSONObject(i))); // converte
            } // fim do for
            financeiro.carregar(lidasDespesas, lidosRelatorios); // devolve ao financeiro

            List<RegistroCatraca> lidosRegistros = new ArrayList<>(); // registros lidos
            JSONArray jsonRegistros = raiz.getJSONArray(CHAVE_REGISTROS); // lista do arquivo
            for (int i = 0; i < jsonRegistros.length(); i++) { // percorre o JSONArray
                lidosRegistros.add(jsonParaRegistro(jsonRegistros.getJSONObject(i), listaClientes)); // converte
            } // fim do for
            catraca.carregar(lidosRegistros); // devolve à catraca
        } catch (JSONException | IllegalArgumentException | IllegalStateException | DateTimeException erro) { // dado inválido
            throw new DaoException("Erro ao ler " + arquivo + ".", erro); // exceção encadeada
        } // fim do try
    } // fim de carregar

    /**
     * Transforma um funcionário em JSON.
     *
     * @param funcionario funcionário a converter
     * @return o objeto JSON
     */
    private JSONObject funcionarioParaJson(Funcionario funcionario) { // Funcionario vira JSON
        JSONObject json = new JSONObject(); // objeto vazio
        json.put("id", funcionario.getId()); // ID
        json.put("perfil", funcionario.podeAcessarFinanceiro() ? ADMINISTRADOR : COLABORADOR); // qual subclasse
        json.put("nome", funcionario.getNome()); // nome
        json.put("endereco", funcionario.getEndereco()); // endereço
        json.put("telefone", funcionario.getTelefone()); // telefone
        json.put("email", funcionario.getEmail()); // e-mail
        json.put("senha", funcionario.getSenha()); // senha (getter de pacote)
        return json; // objeto pronto
    } // fim de funcionarioParaJson

    /**
     * Recria um funcionário a partir do JSON, como administrador ou colaborador.
     *
     * @param json objeto lido do arquivo
     * @return o funcionário recriado
     */
    private Funcionario jsonParaFuncionario(JSONObject json) { // JSON vira Funcionario
        int id = json.getInt("id"); // lê o ID
        String nome = json.getString("nome"); // lê o nome
        String endereco = json.getString("endereco"); // lê o endereço
        String telefone = json.getString("telefone"); // lê o telefone
        String email = json.getString("email"); // lê o e-mail
        String senha = json.getString("senha"); // lê a senha
        if (ADMINISTRADOR.equals(json.getString("perfil"))) { // era administrador?
            return new Administrador(id, nome, endereco, telefone, email, senha); // recria o administrador
        } // fim do if
        return new Colaborador(id, nome, endereco, telefone, email, senha); // recria o colaborador
    } // fim de jsonParaFuncionario

    /**
     * Transforma uma aula em JSON, guardando só o código da sala.
     *
     * @param aula aula a converter
     * @return o objeto JSON
     */
    private JSONObject aulaParaJson(Aula aula) { // Aula vira JSON
        JSONObject json = new JSONObject(); // objeto vazio
        json.put("id", aula.getId()); // ID
        json.put("sala", aula.getSala().getCodigo()); // só o código da sala
        json.put("dataHora", aula.getDataHora().toString()); // data e hora como texto
        json.put("instrutor", aula.getNomeInstrutor()); // instrutor
        return json; // objeto pronto
    } // fim de aulaParaJson

    /**
     * Recria uma aula a partir do JSON, procurando a sala pelo código.
     *
     * @param json objeto lido do arquivo
     * @return a aula recriada
     * @throws IllegalArgumentException se a sala não existir
     */
    private Aula jsonParaAula(JSONObject json) { // JSON vira Aula
        int codigoSala = json.getInt("sala"); // código da sala
        Sala[] salas = Sistema.getSalas(); // as 4 salas fixas
        for (int i = 0; i < salas.length; i++) { // procura a sala pelo código (vetor usa índice)
            if (salas[i].getCodigo() == codigoSala) { // achou?
                return new Aula(json.getInt("id"), salas[i], LocalDateTime.parse(json.getString("dataHora")),
                        json.getString("instrutor")); // recria a aula
            } // fim do if
        } // fim do for
        throw new IllegalArgumentException("Sala " + codigoSala + " não existe."); // sala inválida no arquivo
    } // fim de jsonParaAula

    /**
     * Transforma um agendamento em JSON, guardando só o ID do cliente, os IDs das aulas e o cartão mascarado.
     *
     * @param agendamento agendamento a converter
     * @return o objeto JSON
     */
    private JSONObject agendamentoParaJson(Agendamento agendamento) { // Agendamento vira JSON
        JSONObject json = new JSONObject(); // objeto vazio
        json.put("id", agendamento.getId()); // ID
        json.put("cliente", agendamento.getCliente().getId()); // só o ID do cliente
        JSONArray idsAulas = new JSONArray(); // IDs das aulas
        IteradorLista<Aula> iterador = new IteradorLista<>(agendamento.getAulas()); // percorre as aulas
        while (iterador.hasNext()) { // enquanto houver aula
            idsAulas.put(iterador.next().getId()); // guarda só o ID
        } // fim do while
        json.put("aulas", idsAulas); // lista de IDs
        json.put("tipo", agendamento.getTipoCobranca()); // DIARIA ou MENSALIDADE
        json.put("preco", agendamento.getPreco()); // preço combinado
        json.put("cartao", agendamento.getCartaoMascarado()); // só o cartão mascarado
        json.put("status", agendamento.getStatus()); // situação
        return json; // objeto pronto
    } // fim de agendamentoParaJson

    /**
     * Recria um agendamento a partir do JSON, trocando os IDs por clientes e aulas.
     *
     * @param json objeto lido do arquivo
     * @param clientes clientes já carregados
     * @param aulas aulas já carregadas
     * @return o agendamento recriado
     */
    private Agendamento jsonParaAgendamento(JSONObject json, List<Cliente> clientes, List<Aula> aulas) { // JSON vira Agendamento
        List<Aula> doAgendamento = new ArrayList<>(); // aulas deste agendamento
        JSONArray idsAulas = json.getJSONArray("aulas"); // IDs gravados
        for (int i = 0; i < idsAulas.length(); i++) { // percorre os IDs
            doAgendamento.add(acharPorId(aulas, idsAulas.getInt(i))); // troca o ID pela aula
        } // fim do for
        return new Agendamento(json.getInt("id"), acharPorId(clientes, json.getInt("cliente")), doAgendamento,
                json.getString("tipo"), json.getDouble("preco"), json.getString("cartao"),
                json.getString("status")); // construtor de pacote: cartão já mascarado e situação salva
    } // fim de jsonParaAgendamento

    /**
     * Transforma um pagamento de diária ou mensalidade em JSON.
     *
     * @param pagamento pagamento a converter
     * @return o objeto JSON
     */
    private JSONObject pagamentoParaJson(VendaAgendamento pagamento) { // VendaAgendamento vira JSON
        JSONObject json = new JSONObject(); // objeto vazio
        json.put("id", pagamento.getId()); // ID
        json.put("agendamento", pagamento.getAgendamento().getId()); // só o ID do agendamento
        json.put("dataHora", pagamento.getDataHora().toString()); // data e hora
        json.put("valorDevolvido", pagamento.getValorDevolvido()); // devolução, se houve
        return json; // objeto pronto
    } // fim de pagamentoParaJson

    /**
     * Recria um pagamento a partir do JSON, trocando o ID pelo agendamento.
     *
     * @param json objeto lido do arquivo
     * @param agendamentos agendamentos já carregados
     * @return o pagamento recriado
     */
    private VendaAgendamento jsonParaPagamento(JSONObject json, List<Agendamento> agendamentos) { // JSON vira VendaAgendamento
        VendaAgendamento pagamento = new VendaAgendamento(json.getInt("id"),
                acharPorId(agendamentos, json.getInt("agendamento")),
                LocalDateTime.parse(json.getString("dataHora"))); // recria o pagamento
        double devolvido = json.getDouble("valorDevolvido"); // devolução gravada
        if (devolvido > 0) { // houve devolução?
            pagamento.registrarDevolucao(devolvido); // devolve ao pagamento
        } // fim do if
        return pagamento; // objeto pronto
    } // fim de jsonParaPagamento

    /**
     * Transforma um produto em JSON.
     *
     * @param produto produto a converter
     * @return o objeto JSON
     */
    private JSONObject produtoParaJson(Produto produto) { // Produto vira JSON
        JSONObject json = new JSONObject(); // objeto vazio
        json.put("id", produto.getId()); // ID
        json.put("nome", produto.getNome()); // nome
        json.put("preco", produto.getPreco()); // preço
        json.put("quantidade", produto.getQuantidade()); // quantidade em estoque
        return json; // objeto pronto
    } // fim de produtoParaJson

    /**
     * Recria um produto a partir do JSON.
     *
     * @param json objeto lido do arquivo
     * @return o produto recriado
     */
    private Produto jsonParaProduto(JSONObject json) { // JSON vira Produto
        return new Produto(json.getInt("id"), json.getString("nome"),
                json.getDouble("preco"), json.getInt("quantidade")); // o construtor valida
    } // fim de jsonParaProduto

    /**
     * Transforma uma venda da lojinha em JSON, com os itens e o preço do dia da venda.
     *
     * @param venda venda a converter
     * @return o objeto JSON
     */
    private JSONObject vendaParaJson(VendaProdutos venda) { // VendaProdutos vira JSON
        JSONObject json = new JSONObject(); // objeto vazio
        json.put("id", venda.getId()); // ID
        json.put("cliente", venda.getCliente().getId()); // só o ID do cliente
        json.put("dataHora", venda.getDataHora().toString()); // data e hora
        JSONArray itens = new JSONArray(); // itens da venda
        IteradorLista<ItemVenda> iterador = new IteradorLista<>(venda.getItens()); // percorre os itens
        while (iterador.hasNext()) { // enquanto houver item
            ItemVenda item = iterador.next(); // pega o próximo
            JSONObject jsonItem = new JSONObject(); // objeto do item
            jsonItem.put("produto", item.getProduto().getId()); // ID do produto
            jsonItem.put("nome", item.getProduto().getNome()); // nome (caso o produto saia do estoque depois)
            jsonItem.put("quantidade", item.getQuantidade()); // quantidade
            jsonItem.put("precoUnitario", item.getPrecoUnitario()); // preço do dia da venda
            itens.put(jsonItem); // acrescenta
        } // fim do while
        json.put("itens", itens); // lista de itens
        return json; // objeto pronto
    } // fim de vendaParaJson

    /**
     * Recria uma venda da lojinha a partir do JSON. Se o produto saiu do estoque
     * depois da venda, recria um produto só para a venda antiga.
     *
     * @param json objeto lido do arquivo
     * @param clientes clientes já carregados
     * @param produtos produtos já carregados
     * @return a venda recriada
     */
    private VendaProdutos jsonParaVenda(JSONObject json, List<Cliente> clientes, List<Produto> produtos) { // JSON vira VendaProdutos
        VendaProdutos venda = new VendaProdutos(json.getInt("id"), acharPorId(clientes, json.getInt("cliente")),
                LocalDateTime.parse(json.getString("dataHora"))); // recria a venda
        JSONArray itens = json.getJSONArray("itens"); // itens gravados
        for (int i = 0; i < itens.length(); i++) { // percorre os itens
            JSONObject jsonItem = itens.getJSONObject(i); // um item
            int idProduto = jsonItem.getInt("produto"); // ID do produto
            double preco = jsonItem.getDouble("precoUnitario"); // preço do dia
            Produto produto = buscarPorId(produtos, idProduto); // o produto ainda está no estoque?
            if (produto == null) { // foi removido do estoque depois da venda
                produto = new Produto(idProduto, jsonItem.getString("nome"), preco, 0); // recria só para a venda antiga
            } // fim do if
            venda.adicionarItem(new ItemVenda(produto, jsonItem.getInt("quantidade"), preco)); // recria o item
        } // fim do for
        return venda; // objeto pronto
    } // fim de jsonParaVenda

    /**
     * Transforma um relatório em JSON, guardando só os IDs das vendas.
     *
     * @param relatorio relatório a converter
     * @return o objeto JSON
     */
    private JSONObject relatorioParaJson(RelatorioVendas relatorio) { // RelatorioVendas vira JSON
        JSONObject json = new JSONObject(); // objeto vazio
        json.put("tipo", relatorio.getTipo()); // LOJINHA ou DIARIAS_MENSALIDADES
        json.put("inicio", relatorio.getInicio().toString()); // primeiro dia
        json.put("fim", relatorio.getFim().toString()); // último dia
        JSONArray idsVendas = new JSONArray(); // IDs das vendas do relatório
        IteradorLista<Venda> iterador = new IteradorLista<>(relatorio.getVendas()); // percorre as vendas
        while (iterador.hasNext()) { // enquanto houver venda
            idsVendas.put(iterador.next().getId()); // guarda só o ID
        } // fim do while
        json.put("vendas", idsVendas); // lista de IDs
        return json; // objeto pronto
    } // fim de relatorioParaJson

    /**
     * Recria um relatório a partir do JSON, procurando as vendas na lista certa conforme o tipo.
     *
     * @param json objeto lido do arquivo
     * @param vendasLojinha vendas da lojinha já carregadas
     * @param pagamentos pagamentos de aulas já carregados
     * @return o relatório recriado
     */
    private RelatorioVendas jsonParaRelatorio(JSONObject json, List<VendaProdutos> vendasLojinha,
            List<VendaAgendamento> pagamentos) { // JSON vira RelatorioVendas
        String tipo = json.getString("tipo"); // tipo do relatório
        List<Venda> vendas = new ArrayList<>(); // vendas do relatório
        JSONArray idsVendas = json.getJSONArray("vendas"); // IDs gravados
        for (int i = 0; i < idsVendas.length(); i++) { // percorre os IDs
            if (RelatorioVendas.LOJINHA.equals(tipo)) { // relatório da lojinha?
                vendas.add(acharPorId(vendasLojinha, idsVendas.getInt(i))); // procura nas vendas da lojinha
            } else { // relatório de diárias e mensalidades
                vendas.add(acharPorId(pagamentos, idsVendas.getInt(i))); // procura nos pagamentos
            } // fim do if
        } // fim do for
        return new RelatorioVendas(tipo, LocalDate.parse(json.getString("inicio")),
                LocalDate.parse(json.getString("fim")), vendas); // recria o relatório
    } // fim de jsonParaRelatorio

    /**
     * Transforma uma despesa em JSON.
     *
     * @param despesa despesa a converter
     * @return o objeto JSON
     */
    private JSONObject despesaParaJson(Despesa despesa) { // Despesa vira JSON
        JSONObject json = new JSONObject(); // objeto vazio
        json.put("id", despesa.getId()); // ID
        json.put("descricao", despesa.getDescricao()); // descrição
        json.put("categoria", despesa.getCategoria()); // categoria
        json.put("valor", despesa.getValor()); // valor
        json.put("data", despesa.getData().toString()); // data
        return json; // objeto pronto
    } // fim de despesaParaJson

    /**
     * Recria uma despesa a partir do JSON.
     *
     * @param json objeto lido do arquivo
     * @return a despesa recriada
     */
    private Despesa jsonParaDespesa(JSONObject json) { // JSON vira Despesa
        return new Despesa(json.getInt("id"), json.getString("descricao"), json.getString("categoria"),
                json.getDouble("valor"), LocalDate.parse(json.getString("data"))); // o construtor valida
    } // fim de jsonParaDespesa

    /**
     * Transforma um registro da catraca em JSON, guardando só o ID do cliente.
     *
     * @param registro registro a converter
     * @return o objeto JSON
     */
    private JSONObject registroParaJson(RegistroCatraca registro) { // RegistroCatraca vira JSON
        JSONObject json = new JSONObject(); // objeto vazio
        json.put("id", registro.getId()); // ID
        json.put("cliente", registro.getCliente().getId()); // só o ID do cliente
        json.put("tipo", registro.getTipo()); // ENTRADA ou SAIDA
        json.put("dataHora", registro.getDataHora().toString()); // data e hora
        return json; // objeto pronto
    } // fim de registroParaJson

    /**
     * Recria um registro da catraca a partir do JSON, trocando o ID pelo cliente.
     *
     * @param json objeto lido do arquivo
     * @param clientes clientes já carregados
     * @return o registro recriado
     */
    private RegistroCatraca jsonParaRegistro(JSONObject json, List<Cliente> clientes) { // JSON vira RegistroCatraca
        return new RegistroCatraca(json.getInt("id"), acharPorId(clientes, json.getInt("cliente")),
                json.getString("tipo"), LocalDateTime.parse(json.getString("dataHora"))); // o construtor valida
    } // fim de jsonParaRegistro

    /**
     * Acha qualquer objeto identificável pelo ID.
     *
     * @param <T> tipo dos objetos da lista
     * @param lista lista onde procurar
     * @param id identificador procurado
     * @return o objeto, ou {@code null} se não achar
     */
    private static <T extends Identificavel> T buscarPorId(List<T> lista, int id) { // acha qualquer objeto com ID (ou null)
        IteradorLista<T> iterador = new IteradorLista<>(lista); // percorre a lista
        while (iterador.hasNext()) { // enquanto houver elemento
            T objeto = iterador.next(); // pega o próximo
            if (objeto.getId() == id) { // é o ID procurado?
                return objeto; // achou
            } // fim do if
        } // fim do while
        return null; // não achou
    } // fim de buscarPorId

    /**
     * Como {@code buscarPorId}, mas exige que o objeto exista.
     *
     * @param <T> tipo dos objetos da lista
     * @param lista lista onde procurar
     * @param id identificador procurado
     * @return o objeto encontrado
     * @throws IllegalArgumentException se o arquivo apontar para algo que não existe
     */
    private static <T extends Identificavel> T acharPorId(List<T> lista, int id) { // como buscarPorId, mas exige que exista
        T objeto = buscarPorId(lista, id); // procura
        if (objeto == null) { // o arquivo aponta para algo que não existe?
            throw new IllegalArgumentException("Registro " + id + " não encontrado no arquivo."); // dado inválido
        } // fim do if
        return objeto; // devolve o objeto
    } // fim de acharPorId

    /**
     * Grava o texto com segurança: primeiro num arquivo temporário e depois troca pelo original.
     * O arquivo é sempre fechado, com ou sem erro.
     *
     * @param conteudo texto a gravar
     * @throws DaoException se não conseguir gravar
     */
    private void gravarTexto(String conteudo) { // grava com segurança: primeiro num temporário
        Path temporario = arquivo.resolveSibling(arquivo.getFileName() + ".tmp"); // arquivo temporário ao lado
        try { // gravação pode falhar
            BufferedWriter escritor = Files.newBufferedWriter(temporario); // aloca o arquivo
            try { // usa o arquivo aberto
                escritor.write(conteudo); // escreve tudo
            } finally { // executa com ou sem erro
                escritor.close(); // desaloca: sempre fecha o arquivo
            } // fim do try interno
            Files.move(temporario, arquivo, StandardCopyOption.REPLACE_EXISTING); // troca o original pelo temporário
        } catch (IOException erro) { // não conseguiu gravar
            throw new DaoException("Erro ao gravar " + arquivo + ".", erro); // exceção encadeada
        } // fim do try
    } // fim de gravarTexto

    /**
     * Lê o arquivo inteiro. O arquivo é sempre fechado, com ou sem erro.
     *
     * @return o conteúdo do arquivo
     * @throws DaoException se não conseguir ler
     */
    private String lerTexto() { // lê o arquivo inteiro
        try { // leitura pode falhar
            StringBuilder texto = new StringBuilder(); // junta o conteúdo
            BufferedReader leitor = Files.newBufferedReader(arquivo); // aloca o arquivo
            try { // usa o arquivo aberto
                String linha = leitor.readLine(); // primeira linha
                while (linha != null) { // enquanto houver linhas
                    texto.append(linha).append('\n'); // junta a linha
                    linha = leitor.readLine(); // próxima
                } // fim do while
            } finally { // executa com ou sem erro
                leitor.close(); // desaloca: sempre fecha o arquivo
            } // fim do try interno
            return texto.toString(); // devolve o conteúdo
        } catch (IOException erro) { // não conseguiu ler
            throw new DaoException("Erro ao ler " + arquivo + ".", erro); // exceção encadeada
        } // fim do try
    } // fim de lerTexto

    /**
     * Descreve o repositório.
     *
     * @return o caminho do arquivo
     */
    @Override // sobrescreve o toString de Object
    public String toString() { // resumo em uma linha
        return "RepositorioJson em " + arquivo; // caminho do arquivo
    } // fim do toString
}