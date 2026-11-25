/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mycompany.academia.milho.verde.dao; // pacote do DAO

import com.mycompany.academia.milho.verde.model.Identificavel; // só guarda objetos que têm ID
import java.util.List; // tipo de lista usado no retorno de listar

/**
 * Contrato do padrão DAO: define como guardar e buscar objetos, sem dizer onde eles ficam
 * (memória, arquivo JSON...). Quem usa o DAO não precisa saber como ele guarda.
 *
 * @param <T> tipo guardado; precisa ter ID ({@link Identificavel})
 * @author Aline Ferreira
 */
public interface DAO<T extends Identificavel> { // genérico: T pode ser Cliente, Produto, Aula... desde que tenha ID

    /**
     * Reserva e devolve um ID novo.
     *
     * @return ID ainda não usado
     */
    int gerarId(); // reserva e devolve um ID novo

    /**
     * Guarda um objeto novo.
     *
     * @param objeto objeto a guardar
     */
    void inserir(T objeto); // guarda um objeto novo

    /**
     * Procura um objeto pelo ID.
     *
     * @param id ID procurado
     * @return o objeto, ou {@code null} se não existir
     */
    T buscarPorId(int id); // devolve o objeto com esse ID, ou null se não existir

    /**
     * Lista todos os objetos guardados.
     *
     * @return lista com os objetos
     */
    List<T> listar(); // devolve todos os objetos guardados

    /**
     * Grava as alterações de um objeto que já existe.
     *
     * @param objeto objeto alterado
     */
    void atualizar(T objeto); // grava as alterações de um objeto que já existe

    /**
     * Apaga o objeto com esse ID.
     *
     * @param id ID do objeto
     * @return {@code true} se apagou, {@code false} se não existia
     */
    boolean remover(int id); // apaga o objeto com esse ID; true se apagou, false se não existia
} // fim da interface DAO