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
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Salva e recupera os dados da academia em um arquivo JSON.
 *
 * <p>Pertence à camada Model da organização MVC. Usa a biblioteca
 * pronta {@code org.json}, conforme o item 14 da Questão 2, para
 * converter os objetos Java em texto JSON e vice-versa.</p>
 *
 * <p>O arquivo tem um objeto principal com uma lista para cada
 * tipo de cadastro. Nesta etapa, só a lista de clientes existe:</p>
 *
 * <pre>
 * {
 *   "clientes": [
 *     { "id": 1, "nome": "Ana", "endereco": "...", "telefone": "...",
 *       "email": "...", "cpfPseudo": "...", "ativo": true }
 *   ]
 * }
 * </pre>
 *
 * <p>As demais listas (funcionários, produtos, agendamentos...)
 * serão acrescentadas ao mesmo arquivo nas próximas etapas.</p>
 *
 * @author Aline Ferreira Pena
 */
public class RepositorioJson {

    /** Nome da lista de clientes dentro do arquivo JSON. */
    private static final String CHAVE_CLIENTES = "clientes";

    /** Caminho do arquivo JSON onde os dados são gravados. */
    private final Path arquivo;

    /**
     * Construtor padrão: usa o arquivo {@code academia.json} na pasta
     * em que o programa é executado.
     */
    public RepositorioJson() {
        // this(...) chama o outro construtor desta mesma classe,
        // evitando repetir a inicialização.
        this(Path.of("academia.json"));
    }

    /**
     * Construtor com parâmetros: usa o arquivo informado.
     *
     * @param arquivo caminho do arquivo JSON
     * @throws IllegalArgumentException se {@code arquivo} for nulo
     */
    public RepositorioJson(Path arquivo) {
        if (arquivo == null) {
            throw new IllegalArgumentException(
                    "O caminho do arquivo JSON deve ser informado.");
        }
        this.arquivo = arquivo;
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
     * Grava a lista de clientes no arquivo JSON.
     *
     * <p><b>Etapa provisória:</b> quando a classe Sistema existir,
     * este método será substituído por {@code salvar(sistema)}, que
     * grava todos os cadastros juntos usando estes mesmos conversores.</p>
     *
     * <p>Todos os clientes são convertidos antes da gravação. Se um
     * deles estiver incompleto, nada é gravado e o arquivo anterior
     * permanece como estava.</p>
     *
     * @param clientes clientes a gravar
     * @throws IOException se não for possível gravar o arquivo
     * @throws IllegalArgumentException se algum cliente estiver incompleto
     */
    public void salvarClientes(List<Cliente> clientes) throws IOException {
        JSONObject raiz = new JSONObject();
        raiz.put(CHAVE_CLIENTES, clientesParaJson(clientes));

        // toString(2) gera o texto com indentação de 2 espaços,
        // deixando o arquivo legível para conferência.
        gravarTexto(raiz.toString(2));
    }

    /**
     * Lê a lista de clientes do arquivo JSON.
     *
     * <p><b>Etapa provisória:</b> será substituído por
     * {@code carregarEm(sistema)} quando a classe Sistema existir.</p>
     *
     * @return os clientes gravados; lista vazia se o arquivo não existir
     * @throws IOException se não for possível ler o arquivo
     * @throws JSONException se o conteúdo não for um JSON válido ou
     *         faltar algum campo obrigatório
     */
    public List<Cliente> carregarClientes() throws IOException {
        // No primeiro uso o arquivo ainda não existe: não há o que carregar.
        if (Files.notExists(arquivo)) {
            return new ArrayList<>();
        }
        JSONObject raiz = new JSONObject(lerTexto());

        // optJSONArray devolve null em vez de lançar erro quando a
        // lista não existe no arquivo.
        JSONArray lista = raiz.optJSONArray(CHAVE_CLIENTES);
        if (lista == null) {
            return new ArrayList<>();
        }
        return jsonParaClientes(lista);
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
     * <p>Grava apenas o CPF pseudonimizado; o número original
     * nunca chega ao arquivo.</p>
     *
     * <p>Recusa clientes incompletos (sem ID ou com dado vazio,
     * como os criados pelo construtor padrão e ainda não
     * preenchidos). Se fossem gravados, o construtor com parâmetros
     * os rejeitaria na leitura e o arquivo inteiro deixaria de abrir.</p>
     *
     * @param cliente cliente a converter
     * @return objeto JSON com os dados do cliente
     * @throws IllegalArgumentException se o cliente estiver incompleto
     */
    private JSONObject clienteParaJson(Cliente cliente) {
        exigirClienteCompleto(cliente);

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
     * Confere se o cliente tem ID e todos os dados preenchidos.
     *
     * @param cliente cliente a conferir
     * @throws IllegalArgumentException se o cliente estiver incompleto
     */
    private void exigirClienteCompleto(Cliente cliente) {
        boolean incompleto = cliente.getId() <= 0
                || cliente.getNome().isBlank()
                || cliente.getEndereco().isBlank()
                || cliente.getTelefone().isBlank()
                || cliente.getEmail().isBlank()
                || cliente.getCpfPseudo().isBlank();
        if (incompleto) {
            throw new IllegalArgumentException(
                    "Cliente incompleto não pode ser gravado: " + cliente);
        }
    }

    /**
     * Converte um array JSON em lista de clientes.
     *
     * @param lista array JSON lido do arquivo
     * @return os clientes reconstruídos
     * @throws JSONException se algum objeto não tiver os campos esperados
     */
    private List<Cliente> jsonParaClientes(JSONArray lista) {
        List<Cliente> clientes = new ArrayList<>();
        for (int i = 0; i < lista.length(); i++) {
            clientes.add(jsonParaCliente(lista.getJSONObject(i)));
        }
        return clientes;
    }

    /**
     * Reconstrói um cliente a partir de um objeto JSON.
     *
     * <p>Usa o construtor com parâmetros, que valida os dados.
     * Se o cliente estava desativado, a situação é restaurada
     * com {@link Cliente#desativar()}.</p>
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
     * Grava o texto no arquivo de forma segura.
     *
     * <p>Primeiro escreve em um arquivo temporário e só depois o move
     * para o lugar do arquivo definitivo. Se algo falhar no meio da
     * gravação, o arquivo anterior continua intacto.</p>
     *
     * <p>O {@code try-with-resources} fecha o arquivo automaticamente,
     * mesmo quando ocorre erro, liberando o recurso com segurança.</p>
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
     * <p>Tenta a troca atômica (tudo ou nada). Se o sistema de
     * arquivos não suportar, faz a troca comum.</p>
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