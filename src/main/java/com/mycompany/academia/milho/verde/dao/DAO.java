/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mycompany.academia.milho.verde.dao; // pacote do DAO

import com.mycompany.academia.milho.verde.model.Identificavel; // só guarda objetos que têm ID
import java.util.List; // tipo de lista usado no retorno de listar

public interface DAO<T extends Identificavel> { // genérico: T pode ser Cliente, Produto, Aula... desde que tenha ID

    int gerarId(); // reserva e devolve um ID novo

    void inserir(T objeto); // guarda um objeto novo

    T buscarPorId(int id); // devolve o objeto com esse ID, ou null se não existir

    List<T> listar(); // devolve todos os objetos guardados

    void atualizar(T objeto); // grava as alterações de um objeto que já existe

    boolean remover(int id); // apaga o objeto com esse ID; true se apagou, false se não existia
} // fim da interface DAO
/**
 *
 * @author aline
 */


