/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.dao;

import com.mycompany.academia.milho.verde.model.Cliente;
import java.util.List;

/**
 * Acesso aos dados dos clientes, sem expor onde nem como são guardados.
 */
public interface ClienteDAO {

    // Reserva e devolve um ID novo para o próximo cliente.
    int gerarId();

    // Guarda um cliente novo.
    void inserir(Cliente cliente);

    // Procura pelo ID; devolve null se não existir.
    Cliente buscarPorId(int id);

    // Devolve todos os clientes, ativos e desativados.
    List<Cliente> listar();

    // Grava as mudanças de um cliente já guardado (inclusive ativar e desativar).
    void atualizar(Cliente cliente);
}