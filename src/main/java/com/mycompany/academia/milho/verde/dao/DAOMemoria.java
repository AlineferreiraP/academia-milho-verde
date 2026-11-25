/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mycompany.academia.milho.verde.dao; // pacote do DAO

import com.mycompany.academia.milho.verde.model.Identificavel; // só guarda objetos que têm ID
import com.mycompany.academia.milho.verde.model.IteradorLista; // nosso iterador, usado em todo percurso
import java.util.ArrayList; // implementação de lista que cresce sozinha
import java.util.List; // tipo da lista (a interface)

/**
 * DAO que guarda os objetos só na memória, numa lista. Serve para qualquer tipo que tenha ID.
 * Os dados somem quando o programa fecha; para guardar em arquivo, usa-se um DAO em JSON.
 * Todo percurso da lista usa o nosso {@link IteradorLista}.
 *
 * @param <T> tipo guardado; precisa ter ID
 * @author Aline Ferreira
 */
public class DAOMemoria<T extends Identificavel> implements DAO<T> { // guarda qualquer tipo com ID, só na memória

    /** Objetos guardados; a lista é sempre a mesma, o conteúdo é que muda. */
    private final List<T> objetos; // final: a lista é sempre a mesma; o conteúdo é que muda
    /** Próximo ID a ser entregue por gerarId. */
    private int proximoId; // próximo ID a ser entregue por gerarId

    /** Cria o DAO vazio; o primeiro objeto recebe o ID 1. */
    public DAOMemoria() { // construtor sem parâmetros
        this.objetos = new ArrayList<>(); // começa com a lista vazia
        this.proximoId = 1; // o primeiro objeto recebe o ID 1
    } // fim do construtor

    /**
     * Entrega um ID novo.
     *
     * @return ID reservado
     */
    @Override // implementa o método da interface
    public int gerarId() { // entrega um ID novo
        int id = proximoId; // guarda o ID que será entregue
        proximoId++; // avança para o próximo
        return id; // devolve o ID reservado
    } // fim de gerarId

    /**
     * Guarda um objeto novo e ajusta o próximo ID para nunca repetir.
     *
     * @param objeto objeto a guardar (não pode ser nulo)
     * @throws IllegalArgumentException se o objeto for nulo ou o ID já existir
     */
    @Override // implementa o método da interface
    public void inserir(T objeto) { // guarda um objeto novo
        if (objeto == null) { // não aceita "nenhum objeto"
            throw new IllegalArgumentException("Informe o objeto a guardar."); // erro de argumento
        } // fim do if
        if (buscarPorId(objeto.getId()) != null) { // já existe objeto com esse ID?
            throw new IllegalArgumentException("Já existe registro com o ID " + objeto.getId() + "."); // não deixa repetir
        } // fim do if
        objetos.add(objeto); // acrescenta no fim da lista
        if (objeto.getId() >= proximoId) { // o ID inserido é maior que o próximo previsto? (acontece ao carregar do arquivo)
            proximoId = objeto.getId() + 1; // ajusta para nunca entregar um ID repetido
        } // fim do if
    } // fim de inserir

    /**
     * Procura um objeto pelo ID.
     *
     * @param id ID procurado
     * @return o objeto, ou {@code null} se não existir
     */
    @Override // implementa o método da interface
    public T buscarPorId(int id) { // procura um objeto pelo ID
        IteradorLista<T> iterador = new IteradorLista<>(objetos); // nosso iterador percorre a lista
        while (iterador.hasNext()) { // enquanto houver elemento
            T objeto = iterador.next(); // pega o próximo
            if (objeto.getId() == id) { // achou o ID procurado?
                return objeto; // devolve o objeto e encerra a busca
            } // fim do if
        } // fim do while
        return null; // percorreu tudo e não achou
    } // fim de buscarPorId

    /**
     * Lista todos os objetos.
     *
     * @return cópia da lista; quem recebe não altera a lista interna
     */
    @Override // implementa o método da interface
    public List<T> listar() { // devolve todos os objetos
        List<T> copia = new ArrayList<>(); // lista nova que será devolvida
        IteradorLista<T> iterador = new IteradorLista<>(objetos); // nosso iterador percorre a lista
        while (iterador.hasNext()) { // enquanto houver elemento
            copia.add(iterador.next()); // copia o próximo para a lista nova
        } // fim do while
        return copia; // devolve a cópia: quem recebe não altera a lista interna
    } // fim de listar

    /**
     * Troca, na mesma posição, o objeto guardado pela versão alterada.
     *
     * @param objeto objeto alterado (não pode ser nulo)
     * @throws IllegalArgumentException se o objeto for nulo ou não existir
     */
    @Override // implementa o método da interface
    public void atualizar(T objeto) { // troca o objeto guardado pela versão alterada
        if (objeto == null) { // não aceita "nenhum objeto"
            throw new IllegalArgumentException("Informe o objeto a atualizar."); // erro de argumento
        } // fim do if
        IteradorLista<T> iterador = new IteradorLista<>(objetos); // nosso iterador percorre a lista
        while (iterador.hasNext()) { // enquanto houver elemento
            if (iterador.next().getId() == objeto.getId()) { // achou o guardado com esse ID?
                iterador.set(objeto); // troca, na mesma posição, pela versão alterada
                return; // atualizado: encerra
            } // fim do if
        } // fim do while
        throw new IllegalArgumentException("Registro " + objeto.getId() + " não encontrado."); // percorreu tudo: não existe
    } // fim de atualizar

    /**
     * Apaga o objeto com esse ID.
     *
     * @param id ID do objeto
     * @return {@code true} se apagou, {@code false} se não existia
     */
    @Override // implementa o método da interface
    public boolean remover(int id) { // apaga o objeto com esse ID
        IteradorLista<T> iterador = new IteradorLista<>(objetos); // nosso iterador percorre a lista
        while (iterador.hasNext()) { // enquanto houver elemento
            if (iterador.next().getId() == id) { // pega o próximo e confere o ID
                iterador.remove(); // remove com segurança durante o percurso
                return true; // apagado
            } // fim do if
        } // fim do while
        return false; // não existe: nada foi apagado
    } // fim de remover

    /**
     * Resume o DAO em uma linha.
     *
     * @return quantidade de registros
     */
    @Override // sobrescreve o toString herdado de Object
    public String toString() { // texto que representa o DAO
        return "DAOMemoria com " + objetos.size() + " registro(s)"; // mostra quantos registros há
    } // fim de toString
} // fim da classe DAOMemoria