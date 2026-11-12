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
public class ClienteDAOMemoria implements ClienteDAO {      // cumpre o contrato ClienteDAO; guarda só na memória

    private final List<Cliente> clientes = new ArrayList<>(); // fonte de dados: lista com ativos e desativados
    private int proximoId = 1;                              // contador do próximo ID

    @Override
    public int gerarId() {                                  // reserva um ID novo
        return proximoId++;                                 // devolve o valor atual e depois soma 1
    }

    @Override
    public void inserir(Cliente cliente) {                  // guarda um cliente novo
        clientes.add(cliente);                              // coloca na lista
        if (cliente.getId() >= proximoId) {                 // ID igual ou maior que o contador?
            proximoId = cliente.getId() + 1;                // mantém o contador acima do maior ID
        }
    }

    @Override
    public Cliente buscarPorId(int id) {                    // procura pelo ID
        for (Cliente cliente : clientes) {                  // percorre a lista
            if (cliente.getId() == id) {                    // achou o ID pedido?
                return cliente;                             // devolve o cliente
            }
        }
        return null;                                        // não existe: devolve null
    }

    @Override
    public List<Cliente> listar() {                         // todos os clientes
        return new ArrayList<>(clientes);                   // cópia, para ninguém mexer na lista interna
    }

    @Override
    public void atualizar(Cliente cliente) {                // grava as mudanças de um cliente já guardado
        for (int i = 0; i < clientes.size(); i++) {         // percorre as posições da lista
            if (clientes.get(i).getId() == cliente.getId()) { // achou o cliente de mesmo ID?
                clientes.set(i, cliente);                   // troca o objeto guardado pelo recebido
                return;                                     // terminou
            }
        }
        throw new IllegalArgumentException(                 // não achou: avisa com erro
                "Cliente " + cliente.getId() + " não encontrado.");
    }
}