/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.dao;

import com.mycompany.academia.milho.verde.model.Cliente;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// Guarda os clientes em um arquivo JSON.
// Mantém uma cópia na memória (ClienteDAOMemoria) e regrava o arquivo
// a cada inserção ou atualização.
public class ClienteDAOJson implements ClienteDAO {

    // Arquivo onde os clientes são gravados.
    private final Path arquivo;

    // Clientes em memória; o arquivo é lido uma vez, na criação.
    private final ClienteDAOMemoria memoria = new ClienteDAOMemoria();

    // Usa o arquivo informado e carrega os clientes que já estiverem nele.
    public ClienteDAOJson(Path arquivo) {
        if (arquivo == null) {
            throw new IllegalArgumentException(
                    "O caminho do arquivo JSON deve ser informado.");
        }
        this.arquivo = arquivo;
        carregar();
    }

    @Override
    public int gerarId() {
        return memoria.gerarId();
    }

    @Override
    public void inserir(Cliente cliente) {
        exigirCompleto(cliente);
        memoria.inserir(cliente);
        salvar();
    }

    @Override
    public Cliente buscarPorId(int id) {
        return memoria.buscarPorId(id);
    }

    @Override
    public List<Cliente> listar() {
        return memoria.listar();
    }

    @Override
    public void atualizar(Cliente cliente) {
        exigirCompleto(cliente);
        memoria.atualizar(cliente);
        salvar();
    }

    // Recusa cliente incompleto: se fosse gravado, o construtor o
    // rejeitaria na leitura e o arquivo inteiro deixaria de abrir.
    private void exigirCompleto(Cliente cliente) {
        boolean incompleto = cliente.getId() <= 0
                || cliente.getNome().isBlank()
                || cliente.getEmail().isBlank()
                || cliente.getEndereco().isBlank()
                || cliente.getTelefone().isBlank()
                || cliente.getCpfPseudo().isBlank();
        if (incompleto) {
            throw new IllegalArgumentException(
                    "Cliente incompleto não pode ser gravado: " + cliente);
        }
    }

    // Lê o arquivo (se existir) e coloca os clientes na memória.
    private void carregar() {
        if (Files.notExists(arquivo)) {
            return;
        }
        try {
            String texto = Files.readString(arquivo, StandardCharsets.UTF_8);
            JSONArray lista = new JSONArray(texto);
            for (int i = 0; i < lista.length(); i++) {
                memoria.inserir(jsonParaCliente(lista.getJSONObject(i)));
            }
        } catch (IOException | JSONException erro) {
            throw new DaoException("Não foi possível ler " + arquivo, erro);
        }
    }

    // Grava todos os clientes no arquivo.
    private void salvar() {
        JSONArray lista = new JSONArray();
        for (Cliente cliente : memoria.listar()) {
            lista.put(clienteParaJson(cliente));
        }
        try {
            gravarTexto(lista.toString(2));
        } catch (IOException erro) {
            throw new DaoException("Não foi possível gravar " + arquivo, erro);
        }
    }

    // Converte um cliente em objeto JSON. O CPF original nunca é gravado:
    // só o CPF pseudonimizado.
    private JSONObject clienteParaJson(Cliente cliente) {
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

    // Reconstrói um cliente pelo construtor com parâmetros, que valida
    // os dados. Se estava desativado, a situação é restaurada.
    private Cliente jsonParaCliente(JSONObject json) {
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

    // Grava em um arquivo temporário e só depois troca pelo definitivo:
    // se algo falhar no meio, o arquivo anterior continua intacto.
    private void gravarTexto(String conteudo) throws IOException {
        Path pasta = arquivo.toAbsolutePath().getParent();
        Files.createDirectories(pasta);
        Path temporario = Files.createTempFile(pasta, "clientes-", ".tmp");
        try {
            Files.writeString(temporario, conteudo, StandardCharsets.UTF_8);
            try {
                Files.move(temporario, arquivo,
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException naoSuportado) {
                Files.move(temporario, arquivo,
                        StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException erro) {
            Files.deleteIfExists(temporario);
            throw erro;
        }
    }
}