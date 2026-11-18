/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.dao; // pacote do DAO
import com.mycompany.academia.milho.verde.model.IteradorLista; // nosso iterador, usado em todo percurso

import com.mycompany.academia.milho.verde.model.Cliente; // classe guardada no arquivo
import java.io.BufferedReader; // lê o arquivo linha por linha
import java.io.BufferedWriter; // escreve no arquivo
import java.io.IOException; // erro de leitura ou gravação de arquivo
import java.nio.charset.StandardCharsets; // arquivo em UTF-8 (aceita acentos)
import java.nio.file.AtomicMoveNotSupportedException; // erro se o sistema não fizer a troca atômica
import java.nio.file.Files; // abre, move e confere arquivos
import java.nio.file.Path; // caminho de um arquivo
import java.nio.file.StandardCopyOption; // opções da troca de arquivo
import java.util.List; // tipo de lista
import org.json.JSONArray; // lista em formato JSON (biblioteca org.json)
import org.json.JSONException; // erro de JSON mal formado
import org.json.JSONObject; // objeto em formato JSON (biblioteca org.json)

public class ClienteDAOJson implements DAO<Cliente> { // guarda os clientes no arquivo clientes.json

    private final Path arquivo; // caminho do arquivo JSON
    private final DAOMemoria<Cliente> memoria; // cópia em memória: as buscas são feitas aqui, sem reler o arquivo

    public ClienteDAOJson() { // construtor sem parâmetros
        this(Path.of("clientes.json")); // usa o arquivo padrão, na pasta do projeto
    } // fim do construtor sem parâmetros

    public ClienteDAOJson(Path novoArquivo) { // recebe o caminho do arquivo
        if (novoArquivo == null) { // não aceita "nenhum arquivo"
            throw new IllegalArgumentException("Informe o arquivo de clientes."); // erro de argumento
        } // fim do if
        this.arquivo = novoArquivo; // guarda o caminho
        this.memoria = new DAOMemoria<>(); // cria a memória vazia
        carregar(); // lê os clientes que já estão no arquivo
    } // fim do construtor com parâmetros

    @Override // implementa o método da interface
    public int gerarId() { // entrega um ID novo
        return memoria.gerarId(); // a memória controla o próximo ID
    } // fim de gerarId

    @Override // implementa o método da interface
    public void inserir(Cliente cliente) { // guarda um cliente novo
        exigirCompleto(cliente); // não grava cliente sem ID
        memoria.inserir(cliente); // guarda na memória (ela recusa ID repetido)
        salvar(); // regrava o arquivo
    } // fim de inserir

    @Override // implementa o método da interface
    public Cliente buscarPorId(int id) { // procura um cliente pelo ID
        return memoria.buscarPorId(id); // busca na memória, sem ler o arquivo
    } // fim de buscarPorId

    @Override // implementa o método da interface
    public List<Cliente> listar() { // devolve todos os clientes
        return memoria.listar(); // cópia da lista em memória
    } // fim de listar

    @Override // implementa o método da interface
    public void atualizar(Cliente cliente) { // grava as alterações de um cliente
        exigirCompleto(cliente); // não grava cliente sem ID
        memoria.atualizar(cliente); // atualiza na memória (ela recusa ID inexistente)
        salvar(); // regrava o arquivo
    } // fim de atualizar

    @Override // implementa o método da interface
    public boolean remover(int id) { // apaga um cliente (o sistema usa desativar, mas o contrato do DAO exige)
        boolean removido = memoria.remover(id); // tenta remover da memória
        if (removido) { // removeu?
            salvar(); // regrava o arquivo
        } // fim do if
        return removido; // informa se removeu
    } // fim de remover

    private void exigirCompleto(Cliente cliente) { // confere se o cliente pode ser gravado
        if (cliente == null || cliente.getId() <= 0) { // sem cliente ou sem ID?
            throw new IllegalArgumentException("Cliente sem ID não pode ser gravado."); // erro de argumento
        } // fim do if
    } // fim de exigirCompleto

    private void carregar() { // lê o arquivo para a memória
        if (Files.notExists(arquivo)) { // o arquivo ainda não existe?
            return; // começa sem clientes
        } // fim do if
        try { // leitura de arquivo pode falhar
            StringBuilder texto = new StringBuilder(); // vai juntar o conteúdo do arquivo
            BufferedReader leitor = Files.newBufferedReader(arquivo, StandardCharsets.UTF_8); // abre o arquivo
            try { // usa o arquivo aberto
                String linha = leitor.readLine(); // lê a primeira linha
                while (linha != null) { // enquanto houver linhas
                    texto.append(linha).append('\n'); // junta a linha ao texto
                    linha = leitor.readLine(); // lê a próxima
                } // fim do while
            } finally { // executa com ou sem erro
                leitor.close(); // sempre fecha o arquivo
            } // fim do try interno
            JSONArray lista = new JSONArray(texto.toString()); // transforma o texto em lista JSON
            for (int i = 0; i < lista.length(); i++) { // percorre cada cliente do arquivo
                memoria.inserir(jsonParaCliente(lista.getJSONObject(i))); // converte e guarda na memória
            } // fim do for
        } catch (IOException | JSONException | IllegalArgumentException erro) { // arquivo ilegível ou dados inválidos
            throw new DaoException("Erro ao ler " + arquivo + ".", erro); // exceção encadeada
        } // fim do try
    } // fim de carregar

    private void salvar() { // grava todos os clientes da memória no arquivo
        JSONArray lista = new JSONArray(); // lista JSON vazia
        IteradorLista<Cliente> iterador = new IteradorLista<>(memoria.listar()); // nosso iterador percorre os clientes
        while (iterador.hasNext()) { // enquanto houver cliente
            lista.put(clienteParaJson(iterador.next())); // converte o próximo e acrescenta
        } // fim do while
        gravarTexto(lista.toString(2)); // grava com recuo de 2 espaços (fácil de ler)
    } // fim de salvar

    private JSONObject clienteParaJson(Cliente cliente) { // converte um Cliente em objeto JSON
        JSONObject json = new JSONObject(); // objeto JSON vazio
        json.put("id", cliente.getId()); // campo id
        json.put("nome", cliente.getNome()); // campo nome
        json.put("endereco", cliente.getEndereco()); // campo endereco
        json.put("telefone", cliente.getTelefone()); // campo telefone
        json.put("email", cliente.getEmail()); // campo email
        json.put("cpfPseudo", cliente.getCpfPseudo()); // campo cpfPseudo
        json.put("ativo", cliente.isAtivo()); // campo ativo
        return json; // devolve o objeto pronto
    } // fim de clienteParaJson

    private Cliente jsonParaCliente(JSONObject json) { // converte um objeto JSON em Cliente
        Cliente cliente = new Cliente(json.getInt("id"), json.getString("nome"),
                json.getString("endereco"), json.getString("telefone"),
                json.getString("email"), json.getString("cpfPseudo")); // o construtor valida os dados
        if (!json.getBoolean("ativo")) { // estava desativado no arquivo?
            cliente.desativar(); // mantém desativado
        } // fim do if
        return cliente; // devolve o cliente pronto
    } // fim de jsonParaCliente

    private void gravarTexto(String conteudo) { // grava com segurança: primeiro num temporário
        Path temporario = arquivo.resolveSibling(arquivo.getFileName() + ".tmp"); // arquivo temporário ao lado do original
        try { // gravação de arquivo pode falhar
            BufferedWriter escritor = Files.newBufferedWriter(temporario, StandardCharsets.UTF_8); // abre o temporário
            try { // usa o arquivo aberto
                escritor.write(conteudo); // escreve todo o conteúdo
            } finally { // executa com ou sem erro
                escritor.close(); // sempre fecha o arquivo
            } // fim do try interno
            try { // troca o original pelo temporário de uma vez só
                Files.move(temporario, arquivo, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE); // se falhar no meio, o original continua inteiro
            } catch (AtomicMoveNotSupportedException erro) { // o sistema não faz troca atômica?
                Files.move(temporario, arquivo, StandardCopyOption.REPLACE_EXISTING); // troca normal
            } // fim do try da troca
        } catch (IOException erro) { // não conseguiu gravar
            throw new DaoException("Erro ao gravar " + arquivo + ".", erro); // exceção encadeada
        } // fim do try
    } // fim de gravarTexto

    @Override // sobrescreve o toString herdado de Object
    public String toString() { // texto que representa o DAO
        return "ClienteDAOJson em " + arquivo + " com " + memoria.listar().size() + " cliente(s)"; // arquivo e quantidade
    } // fim de toString
} // fim da classe ClienteDAOJson