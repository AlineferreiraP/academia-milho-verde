/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Salva e recupera os dados da academia em um arquivo JSON (padrão DAO).
 *
 * <p>
 * Pertence à camada Model da organização MVC. Usa a biblioteca pronta
 * {@code org.json}, conforme o item 14 da Questão 2, para converter os objetos
 * Java em texto JSON e vice-versa.</p>
 *
 * <p>
 * O arquivo tem um objeto principal com uma lista para cada tipo de cadastro:
 * {@code clientes}, {@code funcionarios}, {@code aulas}, {@code agendamentos},
 * {@code produtos} (o estoque), {@code vendas}, {@code relatorios},
 * {@code despesas} e {@code registrosCatraca}. Referências a outros objetos são
 * gravadas pelo ID e trocadas pelo objeto já carregado na leitura.</p>
 *
 * <pre>
 * {
 *   "clientes": [
 *     { "id": 1, "nome": "Ana", "endereco": "...", "telefone": "...",
 *       "email": "...", "cpfPseudo": "...", "ativo": true }
 *   ],
 *   "vendas": [
 *     { "id": 1, "idCliente": 1, "tipo": "PRODUTO", "idExtrato": 1,
 *       "itens": [ { "idProduto": 2, "quantidade": 3, ... } ] }
 *   ]
 * }
 * </pre>
 *
 * <p>
 * <b>Por que org.json e não Gson:</b> a org.json converte campo por campo, do
 * jeito que escrevemos nos conversores. Assim, só vai para o arquivo o que
 * decidimos gravar (o CPF original nunca é gravado) e, na leitura, cada objeto
 * é criado pelo construtor com parâmetros, que valida os dados. A Gson converte
 * automaticamente, mas cria os objetos sem chamar o construtor e precisaria de
 * configuração extra para datas ({@code LocalDate}) e para objetos que apontam
 * um para o outro, como {@code Cliente} e {@code Extrato}.</p>
 *
 * @author Aline Ferreira
 */
public class RepositorioJson {

    /**
     * Nome da lista de clientes dentro do arquivo JSON.
     */
    private static final String CHAVE_CLIENTES = "clientes";

    /**
     * Nome da lista de funcionários dentro do arquivo JSON.
     */
    private static final String CHAVE_FUNCIONARIOS = "funcionarios";

    /**
     * Nome da lista de aulas dentro do arquivo JSON.
     */
    private static final String CHAVE_AULAS = "aulas";

    /**
     * Nome da lista de agendamentos dentro do arquivo JSON.
     */
    private static final String CHAVE_AGENDAMENTOS = "agendamentos";

    /**
     * Nome da lista de produtos (estoque) dentro do arquivo JSON.
     */
    private static final String CHAVE_PRODUTOS = "produtos";

    /**
     * Nome da lista de vendas dentro do arquivo JSON.
     */
    private static final String CHAVE_VENDAS = "vendas";

    /**
     * Nome da lista de relatórios de vendas dentro do arquivo JSON.
     */
    private static final String CHAVE_RELATORIOS = "relatorios";

    /**
     * Nome da lista de despesas dentro do arquivo JSON.
     */
    private static final String CHAVE_DESPESAS = "despesas";

    /**
     * Nome da lista de registros da catraca dentro do arquivo JSON.
     */
    private static final String CHAVE_REGISTROS = "registrosCatraca";

    /**
     * Valor do campo "perfil" para administradores.
     */
    private static final String PERFIL_ADMINISTRADOR = "ADMINISTRADOR";

    /**
     * Valor do campo "perfil" para colaboradores.
     */
    private static final String PERFIL_COLABORADOR = "COLABORADOR";

    /**
     * Caminho do arquivo JSON onde os dados são gravados.
     */
    private final Path arquivo;

    /**
     * Construtor padrão: usa o arquivo {@code academia.json} na pasta em que o
     * programa é executado.
     */
    public RepositorioJson() {
        // this(...) chama o outro construtor desta mesma classe,
        // evitando repetir a inicialização.
        this(Path.of("academia.json"));
    }

    /**
     * Construtor com parâmetros: usa o arquivo informado.
     *
     * @param caminhoArquivo caminho do arquivo JSON
     * @throws IllegalArgumentException se {@code caminhoArquivo} for nulo
     */
    public RepositorioJson(Path caminhoArquivo) {
        if (caminhoArquivo == null) {
            throw new IllegalArgumentException(
                    "O caminho do arquivo JSON deve ser informado.");
        }
        this.arquivo = caminhoArquivo;
    }

    /**
     * Retorna o caminho do arquivo JSON.
     *
     * @return o caminho do arquivo
     */
    public Path getArquivo() {
        return arquivo;
    }

    /**
     * Grava todos os dados no arquivo JSON.
     *
     * <p>
     * Todos os objetos são convertidos antes da gravação. Se um deles estiver
     * incompleto, nada é gravado e o arquivo anterior permanece como
     * estava.</p>
     *
     * @param clientes gestão de clientes
     * @param funcionarios gestão de funcionários
     * @param agendamentos gestão de agendamentos (aulas e reservas)
     * @param vendas gestão de vendas (estoque, vendas e relatórios)
     * @param financeiro gestão financeira (despesas)
     * @param catraca catraca (registros)
     * @throws IOException se não for possível gravar o arquivo
     * @throws IllegalArgumentException se algum cadastro estiver incompleto
     */
    public void salvar(GestaoClientes clientes, GestaoFuncionarios funcionarios,
            GestaoAgendamentos agendamentos, GestaoVendas vendas,
            GestaoFinanceiro financeiro, Catraca catraca)
            throws IOException {
        JSONObject raiz = new JSONObject();

        raiz.put(CHAVE_FUNCIONARIOS,
                funcionariosParaJson(funcionarios.getFuncionarios()));

        JSONArray aulas = new JSONArray();
        for (AgendaSemanal agenda : agendamentos.getAgendas()) {
            for (Aula aula : agenda.listarAulas()) {
                aulas.put(aulaParaJson(aula));
            }
        }
        raiz.put(CHAVE_AULAS, aulas);

        JSONArray reservas = new JSONArray();
        for (Agendamento agendamento : agendamentos.getAgendamentos()) {
            reservas.put(agendamentoParaJson(agendamento));
        }
        raiz.put(CHAVE_AGENDAMENTOS, reservas);

        JSONArray produtos = new JSONArray();
        for (Produto produto : vendas.getEstoque().getProdutos()) {
            produtos.put(produtoParaJson(produto));
        }
        raiz.put(CHAVE_PRODUTOS, produtos);

        JSONArray listaVendas = new JSONArray();
        for (Venda venda : vendas.getVendas()) {
            listaVendas.put(vendaParaJson(venda));
        }
        raiz.put(CHAVE_VENDAS, listaVendas);

        JSONArray relatorios = new JSONArray();
        for (RelatorioVendas relatorio : vendas.getRelatoriosVendas()) {
            relatorios.put(relatorioParaJson(relatorio));
        }
        raiz.put(CHAVE_RELATORIOS, relatorios);

        JSONArray despesas = new JSONArray();
        for (Despesa despesa : financeiro.listarDespesas()) {
            despesas.put(despesaParaJson(despesa));
        }
        raiz.put(CHAVE_DESPESAS, despesas);

        JSONArray registros = new JSONArray();
        for (RegistroCatraca registro : catraca.getRegistros()) {
            registros.put(registroParaJson(registro));
        }
        raiz.put(CHAVE_REGISTROS, registros);

        // toString(2) gera o texto com indentação de 2 espaços,
        // deixando o arquivo legível para conferência.
        gravarTexto(raiz.toString(2));
    }

    /**
     * Lê o arquivo JSON e entrega os dados às gestões.
     *
     * <p>
     * Primeiro converte tudo em objetos; só se não houver erro as gestões são
     * atualizadas. Se o arquivo não existir (primeiro uso), nada muda.</p>
     *
     * @param clientes gestão de clientes a preencher
     * @param funcionarios gestão de funcionários a preencher
     * @param agendamentos gestão de agendamentos a preencher
     * @param vendas gestão de vendas a preencher
     * @param financeiro gestão financeira a preencher
     * @param catraca catraca a preencher
     * @throws IOException se não for possível ler o arquivo
     * @throws JSONException se o conteúdo não for um JSON válido ou faltar
     * algum campo obrigatório
     */
    public void carregar(GestaoClientes clientes, GestaoFuncionarios funcionarios,
            GestaoAgendamentos agendamentos, GestaoVendas vendas,
            GestaoFinanceiro financeiro, Catraca catraca)
            throws IOException {
        if (Files.notExists(arquivo)) {
            return;
        }
        JSONObject raiz = new JSONObject(lerTexto());

        // Os clientes vêm do DAO, já carregados.
        List<Cliente> listaClientes = clientes.getClientes();

        List<Funcionario> listaFuncionarios = new ArrayList<>();
        JSONArray json = lista(raiz, CHAVE_FUNCIONARIOS);
        for (int i = 0; i < json.length(); i++) {
            listaFuncionarios.add(jsonParaFuncionario(json.getJSONObject(i)));
        }

        List<Aula> aulas = new ArrayList<>();
        json = lista(raiz, CHAVE_AULAS);
        for (int i = 0; i < json.length(); i++) {
            aulas.add(jsonParaAula(json.getJSONObject(i)));
        }

        List<Agendamento> reservas = new ArrayList<>();
        json = lista(raiz, CHAVE_AGENDAMENTOS);
        for (int i = 0; i < json.length(); i++) {
            reservas.add(jsonParaAgendamento(json.getJSONObject(i),
                    listaClientes, aulas));
        }

        List<Produto> produtos = new ArrayList<>();
        json = lista(raiz, CHAVE_PRODUTOS);
        for (int i = 0; i < json.length(); i++) {
            produtos.add(jsonParaProduto(json.getJSONObject(i)));
        }

        List<Venda> listaVendas = new ArrayList<>();
        json = lista(raiz, CHAVE_VENDAS);
        for (int i = 0; i < json.length(); i++) {
            listaVendas.add(jsonParaVenda(json.getJSONObject(i),
                    listaClientes, produtos));
        }

        List<RelatorioVendas> relatorios = new ArrayList<>();
        json = lista(raiz, CHAVE_RELATORIOS);
        for (int i = 0; i < json.length(); i++) {
            relatorios.add(jsonParaRelatorio(json.getJSONObject(i), listaVendas));
        }

        List<Despesa> despesas = new ArrayList<>();
        json = lista(raiz, CHAVE_DESPESAS);
        for (int i = 0; i < json.length(); i++) {
            despesas.add(jsonParaDespesa(json.getJSONObject(i)));
        }

        List<RegistroCatraca> registros = new ArrayList<>();
        json = lista(raiz, CHAVE_REGISTROS);
        for (int i = 0; i < json.length(); i++) {
            registros.add(jsonParaRegistro(json.getJSONObject(i), listaClientes));
        }

        // Tudo convertido sem erro: agora as gestões são atualizadas.
 
        funcionarios.carregar(listaFuncionarios);
        agendamentos.carregar(aulas, reservas);
        vendas.carregar(produtos, listaVendas, relatorios);
        financeiro.carregar(despesas);
        catraca.restaurar(registros);
    }

    /**
     * Converte uma lista de clientes em um array JSON.
     *
     * @param clientes clientes a converter
     * @return array JSON com um objeto por cliente
     */
    private JSONArray clientesParaJson(List<Cliente> clientes) {
        JSONArray lista = new JSONArray();
        for (Cliente cliente : clientes) {
            lista.put(clienteParaJson(cliente));
        }
        return lista;
    }

    /**
     * Converte um cliente em objeto JSON.
     *
     * <p>
     * Grava apenas o CPF pseudonimizado; o número original nunca chega ao
     * arquivo. Recusa clientes incompletos: se fossem gravados, o construtor
     * com parâmetros os rejeitaria na leitura e o arquivo inteiro deixaria de
     * abrir.</p>
     *
     * @param cliente cliente a converter
     * @return objeto JSON com os dados do cliente
     * @throws IllegalArgumentException se o cliente estiver incompleto
     */
    private JSONObject clienteParaJson(Cliente cliente) {
        exigirPessoaCompleta(cliente);
        if (cliente.getEndereco().isBlank()
                || cliente.getTelefone().isBlank()
                || cliente.getCpfPseudo().isBlank()) {
            throw new IllegalArgumentException(
                    "Cliente incompleto não pode ser gravado: " + cliente);
        }

        JSONObject json = new JSONObject();
        json.put("id", cliente.getId());
        json.put("nome", cliente.getNome());
        json.put("endereco", cliente.getEndereco());
        json.put("telefone", cliente.getTelefone());
        json.put("email", cliente.getEmail());
        json.put("cpfPseudo", cliente.getCpfPseudo());
        json.put("ativo", cliente.isAtivo());
        return json;
    }

    /**
     * Converte uma lista de funcionários em um array JSON.
     *
     * @param funcionarios funcionários a converter
     * @return array JSON com um objeto por funcionário
     */
    private JSONArray funcionariosParaJson(List<Funcionario> funcionarios) {
        JSONArray lista = new JSONArray();
        for (Funcionario funcionario : funcionarios) {
            lista.put(funcionarioParaJson(funcionario));
        }
        return lista;
    }

    /**
     * Converte um funcionário em objeto JSON.
     *
     * <p>
     * O campo {@code perfil} guarda se é colaborador ou administrador, para
     * recriar o objeto da subclasse certa na leitura. A senha é gravada como
     * texto, porque criptografia está fora do escopo da disciplina.</p>
     *
     * @param funcionario funcionário a converter
     * @return objeto JSON com os dados do funcionário
     * @throws IllegalArgumentException se o funcionário estiver incompleto
     */
    private JSONObject funcionarioParaJson(Funcionario funcionario) {
        exigirPessoaCompleta(funcionario);
        if (funcionario.getSenha().isBlank()) {
            throw new IllegalArgumentException(
                    "Funcionário sem senha não pode ser gravado: " + funcionario);
        }

        JSONObject json = new JSONObject();
        json.put("id", funcionario.getId());
        // instanceof diz qual é a subclasse real do objeto.
        json.put("perfil", funcionario instanceof Administrador
                ? PERFIL_ADMINISTRADOR : PERFIL_COLABORADOR);
        json.put("nome", funcionario.getNome());
        json.put("email", funcionario.getEmail());
        json.put("senha", funcionario.getSenha());
        json.put("ativo", funcionario.isAtivo());
        return json;
    }

    /**
     * Confere se a pessoa tem ID, nome e e-mail preenchidos.
     *
     * <p>
     * Recebe {@link Pessoa}, então serve tanto para clientes quanto para
     * funcionários.</p>
     *
     * @param pessoa cliente ou funcionário a conferir
     * @throws IllegalArgumentException se faltar algum desses dados
     */
    private void exigirPessoaCompleta(Pessoa pessoa) {
        boolean incompleta = pessoa.getId() <= 0
                || pessoa.getNome().isBlank()
                || pessoa.getEmail().isBlank();
        if (incompleta) {
            throw new IllegalArgumentException(
                    "Cadastro incompleto não pode ser gravado: " + pessoa);
        }
    }

    /**
     * Reconstrói um cliente a partir de um objeto JSON.
     *
     * <p>
     * Usa o construtor com parâmetros, que valida os dados. Se o cliente estava
     * desativado, a situação é restaurada com {@link Pessoa#desativar()}.</p>
     *
     * @param json objeto JSON de um cliente
     * @return o cliente reconstruído
     * @throws JSONException se faltar algum campo obrigatório
     */
    private Cliente jsonParaCliente(JSONObject json) {
        // getInt e getString lançam JSONException se o campo não existir.
        Cliente cliente = new Cliente(
                json.getInt("id"),
                json.getString("nome"),
                json.getString("endereco"),
                json.getString("telefone"),
                json.getString("email"),
                json.getString("cpfPseudo"));

        if (!json.getBoolean("ativo")) {
            cliente.desativar();
        }
        return cliente;
    }

    /**
     * Reconstrói um funcionário a partir de um objeto JSON.
     *
     * <p>
     * O campo {@code perfil} decide qual subclasse é criada:
     * {@link Administrador} ou {@link Colaborador}. Os dois passam pelo
     * construtor com parâmetros, que valida os dados.</p>
     *
     * @param json objeto JSON de um funcionário
     * @return o funcionário reconstruído
     * @throws JSONException se faltar algum campo obrigatório
     */
    private Funcionario jsonParaFuncionario(JSONObject json) {
        int id = json.getInt("id");
        String nome = json.getString("nome");
        String email = json.getString("email");
        String senha = json.getString("senha");

        Funcionario funcionario;
        if (PERFIL_ADMINISTRADOR.equals(json.getString("perfil"))) {
            funcionario = new Administrador(id, nome, email, senha);
        } else {
            funcionario = new Colaborador(id, nome, email, senha);
        }

        if (!json.getBoolean("ativo")) {
            funcionario.desativar();
        }
        return funcionario;
    }

    /**
     * Devolve uma das listas do objeto principal.
     *
     * @param raiz objeto principal lido do arquivo
     * @param chave nome da lista ({@code "clientes"}, {@code "vendas"}...)
     * @return a lista; vazia se ela não existir no arquivo
     */
    private JSONArray lista(JSONObject raiz, String chave) {
        // optJSONArray devolve null em vez de lançar erro quando a
        // lista não existe no arquivo.
        JSONArray lista = raiz.optJSONArray(chave);
        return lista == null ? new JSONArray() : lista;
    }

    // ----------------------------------------------- aulas e agendamentos
    /**
     * Converte uma aula em objeto JSON.
     *
     * @param aula aula a converter
     * @return objeto JSON com os dados da aula
     */
    private JSONObject aulaParaJson(Aula aula) {
        JSONObject json = new JSONObject();
        json.put("id", aula.getId());
        json.put("codigoSala", aula.getSala().getCodigo());
        json.put("dataHora", aula.getDataHora().toString());
        json.put("instrutor", aula.getNomeInstrutor());
        return json;
    }

    /**
     * Reconstrói uma aula a partir de um objeto JSON.
     *
     * @param json objeto JSON de uma aula
     * @return a aula reconstruída
     * @throws JSONException se faltar algum campo ou a sala não existir
     */
    private Aula jsonParaAula(JSONObject json) {
        int codigo = json.getInt("codigoSala");
        for (Sala sala : Sistema.getSalas()) {
            if (sala.getCodigo() == codigo) {
                return new Aula(json.getInt("id"), sala,
                        LocalDateTime.parse(json.getString("dataHora")),
                        json.getString("instrutor"));
            }
        }
        throw new JSONException("Sala " + codigo + " não existe.");
    }

    /**
     * Converte um agendamento em objeto JSON.
     *
     * <p>
     * As aulas vão por inteiro dentro do agendamento, porque uma aula já
     * removida da agenda ainda faz parte de reservas antigas.</p>
     *
     * @param agendamento agendamento a converter
     * @return objeto JSON com os dados do agendamento
     */
    private JSONObject agendamentoParaJson(Agendamento agendamento) {
        JSONObject json = new JSONObject();
        json.put("id", agendamento.getId());
        json.put("idCliente", agendamento.getCliente().getId());
        JSONArray aulas = new JSONArray();
        for (Aula aula : agendamento.getAulas()) {
            aulas.put(aulaParaJson(aula));
        }
        json.put("aulas", aulas);
        json.put("tipoCobranca", agendamento.getTipoCobranca());
        json.put("preco", agendamento.getPreco());
        json.put("cartao", agendamento.getNumeroCartao());
        json.put("status", agendamento.getStatus());
        json.put("valorDevolvido", agendamento.getValorDevolvido());
        return json;
    }

    /**
     * Reconstrói um agendamento a partir de um objeto JSON.
     *
     * @param json objeto JSON de um agendamento
     * @param clientes clientes já carregados
     * @param aulasDasAgendas aulas já carregadas, para reaproveitar os objetos
     * @return o agendamento reconstruído
     * @throws JSONException se faltar campo ou o cliente não existir
     */
    private Agendamento jsonParaAgendamento(JSONObject json,
            List<Cliente> clientes,
            List<Aula> aulasDasAgendas) {
        Cliente cliente = acharCliente(clientes, json.getInt("idCliente"));
        List<Aula> aulas = new ArrayList<>();
        JSONArray jsonAulas = json.getJSONArray("aulas");
        for (int i = 0; i < jsonAulas.length(); i++) {
            Aula lida = jsonParaAula(jsonAulas.getJSONObject(i));
            Aula existente = null;
            for (Aula aula : aulasDasAgendas) {
                if (aula.getId() == lida.getId()) {
                    existente = aula;
                }
            }
            aulas.add(existente == null ? lida : existente);
        }
        Agendamento agendamento = new Agendamento(json.getInt("id"), cliente,
                aulas, json.getString("tipoCobranca"), json.getDouble("preco"),
                json.getString("cartao"));
        agendamento.restaurarSituacao(json.getString("status"),
                json.getDouble("valorDevolvido"));
        return agendamento;
    }

    // ------------------------------------------------ produtos e vendas
    /**
     * Converte um produto em objeto JSON.
     *
     * @param produto produto a converter
     * @return objeto JSON com os dados do produto
     */
    private JSONObject produtoParaJson(Produto produto) {
        JSONObject json = new JSONObject();
        json.put("id", produto.getId());
        json.put("nome", produto.getNome());
        json.put("categoria", produto.getCategoria());
        json.put("preco", produto.getPreco());
        json.put("quantidade", produto.getQuantidade());
        return json;
    }

    /**
     * Reconstrói um produto a partir de um objeto JSON.
     *
     * @param json objeto JSON de um produto
     * @return o produto reconstruído
     * @throws JSONException se faltar algum campo obrigatório
     */
    private Produto jsonParaProduto(JSONObject json) {
        return new Produto(json.getInt("id"), json.getString("nome"),
                json.getString("categoria"), json.getDouble("preco"),
                json.getInt("quantidade"));
    }

    /**
     * Converte uma venda em objeto JSON.
     *
     * <p>
     * Cada item guarda os dados do produto da época, porque o produto pode ter
     * sido removido do estoque depois.</p>
     *
     * @param venda venda a converter
     * @return objeto JSON com os dados da venda
     */
    private JSONObject vendaParaJson(Venda venda) {
        JSONObject json = new JSONObject();
        json.put("id", venda.getId());
        json.put("idCliente", venda.getCliente().getId());
        json.put("tipo", venda.getTipo());
        json.put("dataHora", venda.getDataHora().toString());
        json.put("valorTotal", venda.getValorTotal());
        json.put("idExtrato", venda.getExtrato() == null
                ? 0 : venda.getExtrato().getId());
        JSONArray itens = new JSONArray();
        for (ItemVenda item : venda.getItens()) {
            JSONObject jsonItem = new JSONObject();
            jsonItem.put("idProduto", item.getProduto().getId());
            jsonItem.put("nomeProduto", item.getProduto().getNome());
            jsonItem.put("categoria", item.getProduto().getCategoria());
            jsonItem.put("precoUnitario", item.getPrecoUnitario());
            jsonItem.put("quantidade", item.getQuantidade());
            itens.put(jsonItem);
        }
        json.put("itens", itens);
        return json;
    }

    /**
     * Reconstrói uma venda a partir de um objeto JSON, inclusive o extrato, que
     * volta a ser guardado no cliente.
     *
     * @param json objeto JSON de uma venda
     * @param clientes clientes já carregados
     * @param produtos produtos já carregados
     * @return a venda reconstruída
     * @throws JSONException se faltar campo ou o cliente não existir
     */
    private Venda jsonParaVenda(JSONObject json, List<Cliente> clientes,
            List<Produto> produtos) {
        Cliente cliente = acharCliente(clientes, json.getInt("idCliente"));
        Venda venda = new Venda(json.getInt("id"), cliente,
                json.getString("tipo"),
                LocalDateTime.parse(json.getString("dataHora")));
        if (Venda.PRODUTO.equals(venda.getTipo())) {
            JSONArray itens = json.getJSONArray("itens");
            for (int i = 0; i < itens.length(); i++) {
                JSONObject jsonItem = itens.getJSONObject(i);
                Produto produto = null;
                for (Produto candidato : produtos) {
                    if (candidato.getId() == jsonItem.getInt("idProduto")) {
                        produto = candidato;
                    }
                }
                if (produto == null) {
                    // Produto removido do estoque: recria só para o histórico.
                    produto = new Produto(jsonItem.getInt("idProduto"),
                            jsonItem.getString("nomeProduto"),
                            jsonItem.getString("categoria"),
                            jsonItem.getDouble("precoUnitario"), 0);
                }
                venda.adicionarItem(new ItemVenda(produto,
                        jsonItem.getInt("quantidade"),
                        jsonItem.getDouble("precoUnitario")));
            }
        } else {
            venda.setValorTotal(json.getDouble("valorTotal"));
        }
        int idExtrato = json.getInt("idExtrato");
        if (idExtrato > 0) {
            venda.gerarExtrato(idExtrato);
        }
        return venda;
    }

    /**
     * Converte um relatório de vendas em objeto JSON.
     *
     * @param relatorio relatório a converter
     * @return objeto JSON com o período e os IDs das vendas
     */
    private JSONObject relatorioParaJson(RelatorioVendas relatorio) {
        JSONObject json = new JSONObject();
        json.put("inicio", relatorio.getInicio().toString());
        json.put("fim", relatorio.getFim().toString());
        JSONArray ids = new JSONArray();
        for (Venda venda : relatorio.getVendas()) {
            ids.put(venda.getId());
        }
        json.put("idsVendas", ids);
        return json;
    }

    /**
     * Reconstrói um relatório de vendas a partir de um objeto JSON.
     *
     * @param json objeto JSON de um relatório
     * @param vendas vendas já carregadas
     * @return o relatório reconstruído
     * @throws JSONException se faltar campo ou uma venda não existir
     */
    private RelatorioVendas jsonParaRelatorio(JSONObject json,
            List<Venda> vendas) {
        List<Venda> doRelatorio = new ArrayList<>();
        JSONArray ids = json.getJSONArray("idsVendas");
        for (int i = 0; i < ids.length(); i++) {
            Venda achada = null;
            for (Venda venda : vendas) {
                if (venda.getId() == ids.getInt(i)) {
                    achada = venda;
                }
            }
            if (achada == null) {
                throw new JSONException("Venda " + ids.getInt(i)
                        + " do relatório não existe.");
            }
            doRelatorio.add(achada);
        }
        return new RelatorioVendas(LocalDate.parse(json.getString("inicio")),
                LocalDate.parse(json.getString("fim")), doRelatorio);
    }

    // ----------------------------------------------- despesas e catraca
    /**
     * Converte uma despesa em objeto JSON.
     *
     * @param despesa despesa a converter
     * @return objeto JSON com os dados da despesa
     */
    private JSONObject despesaParaJson(Despesa despesa) {
        JSONObject json = new JSONObject();
        json.put("id", despesa.getId());
        json.put("descricao", despesa.getDescricao());
        json.put("categoria", despesa.getCategoria());
        json.put("valor", despesa.getValor());
        json.put("data", despesa.getData().toString());
        return json;
    }

    /**
     * Reconstrói uma despesa a partir de um objeto JSON.
     *
     * @param json objeto JSON de uma despesa
     * @return a despesa reconstruída
     * @throws JSONException se faltar algum campo obrigatório
     */
    private Despesa jsonParaDespesa(JSONObject json) {
        return new Despesa(json.getInt("id"), json.getString("descricao"),
                json.getString("categoria"), json.getDouble("valor"),
                LocalDate.parse(json.getString("data")));
    }

    /**
     * Converte um registro da catraca em objeto JSON.
     *
     * @param registro registro a converter
     * @return objeto JSON com os dados do registro
     */
    private JSONObject registroParaJson(RegistroCatraca registro) {
        JSONObject json = new JSONObject();
        json.put("id", registro.getId());
        json.put("idCliente", registro.getCliente().getId());
        json.put("tipo", registro.getTipo());
        json.put("dataHora", registro.getDataHora().toString());
        return json;
    }

    /**
     * Reconstrói um registro da catraca a partir de um objeto JSON.
     *
     * @param json objeto JSON de um registro
     * @param clientes clientes já carregados
     * @return o registro reconstruído
     * @throws JSONException se faltar campo ou o cliente não existir
     */
    private RegistroCatraca jsonParaRegistro(JSONObject json,
            List<Cliente> clientes) {
        return new RegistroCatraca(json.getInt("id"),
                acharCliente(clientes, json.getInt("idCliente")),
                json.getString("tipo"),
                LocalDateTime.parse(json.getString("dataHora")));
    }

    /**
     * Procura um cliente já carregado pelo identificador.
     *
     * @param clientes clientes já carregados
     * @param id identificador procurado
     * @return o cliente
     * @throws JSONException se o cliente não existir
     */
    private Cliente acharCliente(List<Cliente> clientes, int id) {
        for (Cliente cliente : clientes) {
            if (cliente.getId() == id) {
                return cliente;
            }
        }
        throw new JSONException("Cliente " + id + " não existe no arquivo.");
    }

    /**
     * Grava o texto no arquivo de forma segura.
     *
     * <p>
     * Primeiro escreve em um arquivo temporário e só depois o move para o lugar
     * do arquivo definitivo. Se algo falhar no meio da gravação, o arquivo
     * anterior continua intacto.</p>
     *
     * <p>
     * O {@code try-with-resources} fecha o arquivo automaticamente, mesmo
     * quando ocorre erro, liberando o recurso com segurança.</p>
     *
     * @param conteudo texto JSON a gravar
     * @throws IOException se não for possível gravar ou mover o arquivo
     */
    private void gravarTexto(String conteudo) throws IOException {
        Path pasta = arquivo.toAbsolutePath().getParent();
        Files.createDirectories(pasta);
        Path temporario = Files.createTempFile(pasta, "academia-", ".tmp");

        try {
            // O writer declarado entre parênteses é fechado ao final do
            // bloco, com ou sem exceção.
            try (BufferedWriter escritor = Files.newBufferedWriter(
                    temporario, StandardCharsets.UTF_8)) {
                escritor.write(conteudo);
            }
            moverSubstituindo(temporario);
        } catch (IOException erro) {
            // Remove o temporário para não deixar lixo na pasta e
            // repassa o erro: a falha não pode ser tratada como sucesso.
            Files.deleteIfExists(temporario);
            throw erro;
        }
    }

    /**
     * Move o arquivo temporário para o lugar do arquivo definitivo.
     *
     * <p>
     * Tenta a troca atômica (tudo ou nada). Se o sistema de arquivos não
     * suportar, faz a troca comum.</p>
     *
     * @param temporario arquivo já gravado
     * @throws IOException se não for possível mover o arquivo
     */
    private void moverSubstituindo(Path temporario) throws IOException {
        try {
            Files.move(temporario, arquivo,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException naoSuportado) {
            Files.move(temporario, arquivo,
                    StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Lê todo o conteúdo do arquivo JSON.
     *
     * @return o texto do arquivo
     * @throws IOException se não for possível ler o arquivo
     */
    private String lerTexto() throws IOException {
        StringBuilder texto = new StringBuilder();
        try (BufferedReader leitor = Files.newBufferedReader(
                arquivo, StandardCharsets.UTF_8)) {
            String linha;
            while ((linha = leitor.readLine()) != null) {
                texto.append(linha).append('\n');
            }
        }
        return texto.toString();
    }

    /**
     * Retorna uma descrição do repositório.
     *
     * @return texto com o caminho do arquivo
     */
    @Override
    public String toString() {
        return "RepositorioJson[arquivo=" + arquivo + "]";
    }
}
