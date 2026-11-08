/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.dao;

import com.mycompany.academia.milho.verde.model.Cliente;
import java.util.ArrayList;
import java.util.List;

/**
 * Guarda os clientes só na memória; os dados somem quando o programa fecha.
 *
 *
 * @author Aline Ferreira
 */
// Guarda os clientes só na memória; os dados somem quando o programa fecha.
public class ClienteDAOMemoria implements ClienteDAO {

    // Clientes guardados, ativos e desativados.
    private final List<Cliente> clientes = new ArrayList<>();

    // Identificador do próximo cliente.
    private int proximoId = 1;

    // Reserva e devolve um ID novo, avançando o contador.
    @Override
    public int gerarId() {
        return proximoId++;
    }

    // Guarda o cliente e mantém o contador acima do maior ID, para não
    // repetir IDs de clientes lidos de um arquivo.
    @Override
    public void inserir(Cliente cliente) {
        clientes.add(cliente);
        if (cliente.getId() >= proximoId) {
            proximoId = cliente.getId() + 1;
        }
    }

    // Procura pelo ID; devolve null se não existir.
    @Override
    public Cliente buscarPorId(int id) {
        for (Cliente cliente : clientes) {
            if (cliente.getId() == id) {
                return cliente;
            }
        }
        return null;
    }

    // Devolve uma cópia da lista, para quem chama não mexer na lista interna.
    @Override
    public List<Cliente> listar() {
        return new ArrayList<>(clientes);
    }

    // Troca o cliente guardado que tem o mesmo ID.
    @Override
    public void atualizar(Cliente cliente) {
        for (int i = 0; i < clientes.size(); i++) {
            if (clientes.get(i).getId() == cliente.getId()) {
                clientes.set(i, cliente);
                return;
            }
        }
        throw new IllegalArgumentException(
                "Cliente " + cliente.getId() + " não encontrado.");
    }
}