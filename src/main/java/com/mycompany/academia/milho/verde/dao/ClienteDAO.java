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
    int gerarId();                    // reserva um ID novo
    void inserir(Cliente cliente);    // guarda um cliente novo
    Cliente buscarPorId(int id);      // null se não existir
    List<Cliente> listar();           // ativos e desativados
    void atualizar(Cliente cliente);  // grava mudanças (inclusive ativar e desativar)
    
}