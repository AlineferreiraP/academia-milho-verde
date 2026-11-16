/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.dao;
import com.mycompany.academia.milho.verde.model.Cliente; // a interface trabalha com objetos Cliente


/**
 *
 * @author aline
 */

import java.util.List; // tipo de lista usado no retorno de listar

public interface ClienteDAO { // interface: só diz O QUE fazer; cada implementação decide COMO

    int gerarId(); // reserva e devolve um ID novo para o próximo cliente

    void inserir(Cliente cliente); // guarda um cliente novo

    Cliente buscarPorId(int id); // devolve o cliente com esse ID, ou null se não existir

    List<Cliente> listar(); // devolve todos os clientes, ativos e desativados

    void atualizar(Cliente cliente); // grava as alterações de um cliente que já existe
} // fim da interface ClienteDAO