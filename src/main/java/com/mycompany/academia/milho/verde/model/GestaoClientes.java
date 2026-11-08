/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model;

import com.mycompany.academia.milho.verde.dao.ClienteDAO;
import com.mycompany.academia.milho.verde.dao.ClienteDAOMemoria;
import java.util.ArrayList;
import java.util.List;

/**
 * Gerencia o cadastro de clientes: cadastro, edição, remoção lógica, busca,
 * listagem e conta.
 *
 * <p>
 * Quem age é o funcionário usando o sistema; o {@link Cliente} só guarda dados.
 * O CPF é pseudonimizado aqui, antes de criar o objeto: o número original nunca
 * é guardado.</p>
 *
 * @author Aline Ferreira
 */
public class GestaoClientes {

    // Onde os clientes são guardados; a gestão não sabe como.
    private final ClienteDAO dao;

    // Por padrão, guarda os clientes na memória.
    public GestaoClientes() {
        this(new ClienteDAOMemoria());
    }

    // Usa o DAO informado.
    public GestaoClientes(ClienteDAO dao) {
        if (dao == null) {
            throw new IllegalArgumentException(
                    "O DAO de clientes deve ser informado.");
        }
        this.dao = dao;
    }

    /**
     * Cadastra um cliente novo, com ID gerado e CPF pseudonimizado.
     *
     * @param nome nome completo
     * @param endereco endereço
     * @param telefone telefone de contato
     * @param email e-mail
     * @param cpf CPF com 11 dígitos
     * @return o cliente cadastrado
     * @throws IllegalArgumentException se algum dado for inválido ou se o CPF
     * já estiver cadastrado
     */
    public Cliente cadastrarCliente(String nome, String endereco,
            String telefone, String email, String cpf) {
        String cpfPseudo = Sistema.pseudonimizarCpf(cpf);
        for (Cliente existente : dao.listar()) {
            if (existente.getCpfPseudo().equals(cpfPseudo)) {
                throw new IllegalArgumentException(
                        "Já existe um cliente com esse CPF.");
            }
        }
        Cliente cliente = new Cliente(dao.gerarId(), nome, endereco, telefone,
                email, cpfPseudo);
        dao.inserir(cliente);
        return cliente;
    }

    /**
     * Altera os dados de um cliente.
     *
     * @param id identificador do cliente
     * @param nome novo nome
     * @param endereco novo endereço
     * @param telefone novo telefone
     * @param email novo e-mail
     * @param cpf CPF com 11 dígitos
     * @throws IllegalArgumentException se o cliente não existir ou se algum
     * dado for inválido
     */
    public void editarCliente(int id, String nome, String endereco,
            String telefone, String email, String cpf) {
        Cliente cliente = exigirCliente(id);
        cliente.atualizarCadastro(nome, endereco, telefone, email,
                Sistema.pseudonimizarCpf(cpf));
    }

    /**
     * Remove um cliente por exclusão lógica: o cadastro é desativado e
     * agendamentos, compras e registros da catraca são preservados.
     *
     * @param id identificador do cliente
     * @throws IllegalArgumentException se o cliente não existir
     */
    public void removerCliente(int id) {
        exigirCliente(id).desativar();
    }

    /**
     * Reativa um cliente desativado, com todo o histórico dele.
     *
     * @param id identificador do cliente
     * @throws IllegalArgumentException se o cliente não existir
     * @throws IllegalStateException se o cliente já estiver ativo
     */
    public void reativarCliente(int id) {
        Cliente cliente = exigirCliente(id);
        if (cliente.isAtivo()) {
            throw new IllegalStateException("O cliente já está ativo.");
        }
        cliente.ativar();
    }

    /**
     * Procura um cliente pelo identificador.
     *
     * @param id identificador procurado
     * @return o cliente, ou {@code null} se o ID não existir
     */
    public Cliente buscarCliente(int id) {
        for (Cliente cliente : dao.listar()) {
            if (cliente.getId() == id) {
                return cliente;
            }
        }
        return null;
    }

    /**
     * Procura os clientes que têm alguma palavra do nome começando pelo trecho,
     * sem diferenciar maiúsculas e minúsculas.
     *
     * <p>
     * Percorre a lista e compara cada palavra do nome, uma por uma: "ana" acha
     * "Ana Souza", mas não "Juliana"; "mart" acha "Juliana Martins". Devolve
     * também os desativados, para o funcionário ver o histórico.</p>
     *
     * @param trecho parte do nome procurada (por exemplo, "ana")
     * @return lista com os clientes encontrados (vazia se nenhum)
     * @throws IllegalArgumentException se o trecho estiver vazio
     */
    public List<Cliente> buscarPorNome(String trecho) {
        if (trecho == null || trecho.isBlank()) {
            throw new IllegalArgumentException(
                    "Informe parte do nome para buscar.");
        }
        String procurado = trecho.trim().toLowerCase();
        List<Cliente> encontrados = new ArrayList<>();
        for (Cliente cliente : dao.listar()) {
            // Compara o começo de cada palavra do nome.
            for (String palavra : cliente.getNome().toLowerCase().split(" ")) {
                if (palavra.startsWith(procurado)) {
                    encontrados.add(cliente);
                    break;
                }
            }
        }
        return encontrados;
    }

    /**
     * Lista os clientes ativos, ordenados pelo critério.
     *
     * @param criterio {@link ComparadorCliente#ID} ou
     * {@link ComparadorCliente#NOME}
     * @return lista nova de clientes ativos, ordenada
     * @throws IllegalArgumentException se o critério for inválido
     */
    public List<Cliente> listarClientes(String criterio) {
        ComparadorCliente comparador = new ComparadorCliente(criterio);
        List<Cliente> ativos = new ArrayList<>();
        for (Cliente cliente : dao.listar()) {
            if (cliente.isAtivo()) {
                ativos.add(cliente);
            }
        }
        ativos.sort(comparador);
        return ativos;
    }

    /**
     * Emite a conta de um cliente: os extratos de todas as compras.
     *
     * @param idCliente identificador do cliente
     * @return os extratos do cliente
     * @throws IllegalArgumentException se o cliente não existir
     */
    public List<Extrato> emitirConta(int idCliente) {
        return exigirCliente(idCliente).getExtratos();
    }

    /**
     * Soma o valor de todos os extratos de um cliente.
     *
     * @param idCliente identificador do cliente
     * @return o total da conta em reais
     * @throws IllegalArgumentException se o cliente não existir
     */
    public double calcularTotalConta(int idCliente) {
        double total = 0;
        for (Extrato extrato : emitirConta(idCliente)) {
            total += extrato.getValor();
        }
        return total;
    }

    /**
     * Retorna todos os clientes, ativos e desativados.
     *
     * <p>
     * Acesso de pacote: usado pelo {@link RepositorioJson}.</p>
     *
     * @return cópia da lista de clientes
     */
    List<Cliente> getClientes() {
        return new ArrayList<>(dao.listar());
    }

    // Guarda no DAO os clientes lidos do arquivo (usado pelo RepositorioJson).
    // Supõe o DAO vazio: chamar duas vezes duplicaria os clientes.
    void carregar(List<Cliente> lidos) {
        for (Cliente cliente : lidos) {
            dao.inserir(cliente);
        }
    }

    /**
     * Procura um cliente e falha se não existir.
     *
     * @param id identificador procurado
     * @return o cliente
     * @throws IllegalArgumentException se o ID não existir
     */
    private Cliente exigirCliente(int id) {
        Cliente cliente = buscarCliente(id);
        if (cliente == null) {
            throw new IllegalArgumentException(
                    "Cliente " + id + " não encontrado.");
        }
        return cliente;
    }

    /**
     * Retorna um resumo da gestão.
     *
     * @return texto com o número de clientes
     */
    @Override
    public String toString() {
        return "GestaoClientes[" + dao.listar().size() + " cliente(s)]";
    }
}
