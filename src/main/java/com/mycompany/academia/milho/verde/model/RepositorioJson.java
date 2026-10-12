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
 * tipo de cadastro. Nesta etapa, existem as listas de clientes e de
 * funcionários:</p>
 *
 * <pre>
 * {
 *   "clientes": [
 *     { "id": 1, "nome": "Ana", "endereco": "...", "telefone": "...",
 *       "email": "...", "cpfPseudo": "...", "ativo": true }
 *   ],
 *   "funcionarios": [
 *     { "id": 1, "perfil": "COLABORADOR", "nome": "Bia",
 *       "email": "...", "senha": "...", "ativo": true }
 *   ]
 * }
 * </pre>
 *
 * <p>As demais listas (produtos, agendamentos...) serão acrescentadas
 * ao mesmo arquivo nas próximas etapas.</p>
 *
 * <p><b>Por que org.json e não Gson:</b> a org.json converte campo
 * por campo, do jeito que escrevemos nos conversores. Assim, só vai
 * para o arquivo o que decidimos gravar (o CPF original nunca é
 * gravado) e, na leitura, cada objeto é criado pelo construtor com
 * parâmetros, que valida os dados. A Gson converte automaticamente,
 * mas cria os objetos sem chamar o construtor e precisaria de
 * configuração extra para datas ({@code LocalDate}) e para objetos
 * que apontam um para o outro, como {@code Cliente} e {@code Extrato}.</p>
 *
 * @author Aline Ferreira Pena
 */
public class RepositorioJson {

    /** Nome da lista de clientes dentro do arquivo JSON. */
    private static final String CHAVE_CLIENTES = "clientes";

    /** Nome da lista de funcionários dentro do arquivo JSON. */
    private static final String CHAVE_FUNCIONARIOS = "funcionarios";

    /** Valor do campo "perfil" para administradores. */
    private static final String PERFIL_ADMINISTRADOR = "ADMINISTRADOR";

    /** Valor do campo "perfil" para colaboradores. */
    private static final String PERFIL_COLABORADOR = "COLABORADOR";

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
     * Grava os clientes e os funcionários no arquivo JSON.
     *
     * <p><b>Etapa provisória:</b> quando a classe Sistema existir,
     * este método será substituído por {@code salvar(sistema)}, que
     * grava todos os cadastros juntos usando estes mesmos conversores.</p>
     *
     * <p>Todos os objetos são convertidos antes da gravação. Se um
     * deles estiver incompleto, nada é gravado e o arquivo anterior
     * permanece como estava.</p>
     *
     * @param clientes clientes a gravar
     * @param funcionarios funcionários a gravar
     * @throws IOException se não for possível gravar o arquivo
     * @throws IllegalArgumentException se algum cadastro estiver incompleto
     */
    public void salvarDados(List<Cliente> clientes,
                            List<Funcionario> funcionarios) throws IOException {
        JSONObject raiz = new JSONObject();
        raiz.put(CHAVE_CLIENTES, clientesParaJson(clientes));
        raiz.put(CHAVE_FUNCIONARIOS, funcionariosParaJson(funcionarios));

        // toString(2) gera o texto com indentação de 2 espaços,
        // deixando o arquivo legível para conferência.
        gravarTexto(raiz.toString(2));
    }

    /**
     * Lê a lista de clientes do arquivo JSON.
     *
     * <p><b>Etapa provisória:</b> será substituído por
     * {@code carregar(sistema)} quando a classe Sistema existir.</p>
     *
     * @return os clientes gravados; lista vazia se o arquivo ou a
     *         lista não existirem
     * @throws IOException se não for possível ler o arquivo
     * @throws JSONException se o conteúdo não for um JSON válido ou
     *         faltar algum campo obrigatório
     */
    public List<Cliente> carregarClientes() throws IOException {
        JSONArray lista = lerLista(CHAVE_CLIENTES);
        List<Cliente> clientes = new ArrayList<>();
        for (int i = 0; i < lista.length(); i++) {
            clientes.add(jsonParaCliente(lista.getJSONObject(i)));
        }
        return clientes;
    }

    /**
     * Lê a lista de funcionários do arquivo JSON.
     *
     * <p><b>Etapa provisória:</b> será substituído por
     * {@code carregar(sistema)} quando a classe Sistema existir.</p>
     *
     * @return os funcionários gravados; lista vazia se o arquivo ou a
     *         lista não existirem
     * @throws IOException se não for possível ler o arquivo
     * @throws JSONException se o conteúdo não for um JSON válido ou
     *         faltar algum campo obrigatório
     */
    public List<Funcionario> carregarFuncionarios() throws IOException {
        JSONArray lista = lerLista(CHAVE_FUNCIONARIOS);
        List<Funcionario> funcionarios = new ArrayList<>();
        for (int i = 0; i < lista.length(); i++) {
            funcionarios.add(jsonParaFuncionario(lista.getJSONObject(i)));
        }
        return funcionarios;
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
     * nunca chega ao arquivo. Recusa clientes incompletos: se fossem
     * gravados, o construtor com parâmetros os rejeitaria na leitura
     * e o arquivo inteiro deixaria de abrir.</p>
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
     * <p>O campo {@code perfil} guarda se é colaborador ou
     * administrador, para recriar o objeto da subclasse certa na
     * leitura. A senha é gravada como texto, porque criptografia está
     * fora do escopo da disciplina.</p>
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
     * <p>Recebe {@link Pessoa}, então serve tanto para clientes quanto
     * para funcionários.</p>
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
     * <p>Usa o construtor com parâmetros, que valida os dados.
     * Se o cliente estava desativado, a situação é restaurada
     * com {@link Pessoa#desativar()}.</p>
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
     * <p>O campo {@code perfil} decide qual subclasse é criada:
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
     * Lê o arquivo e devolve uma das listas do objeto principal.
     *
     * @param chave nome da lista ({@code "clientes"}, {@code "funcionarios"}...)
     * @return a lista; vazia se o arquivo ou a lista não existirem
     * @throws IOException se não for possível ler o arquivo
     * @throws JSONException se o conteúdo não for um JSON válido
     */
    private JSONArray lerLista(String chave) throws IOException {
        // No primeiro uso o arquivo ainda não existe: não há o que carregar.
        if (Files.notExists(arquivo)) {
            return new JSONArray();
        }
        JSONObject raiz = new JSONObject(lerTexto());

        // optJSONArray devolve null em vez de lançar erro quando a
        // lista não existe no arquivo.
        JSONArray lista = raiz.optJSONArray(chave);
        return lista == null ? new JSONArray() : lista;
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