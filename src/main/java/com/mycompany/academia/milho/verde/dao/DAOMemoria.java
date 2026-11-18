/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mycompany.academia.milho.verde.dao; // pacote do DAO
import com.mycompany.academia.milho.verde.model.IteradorLista; // nosso iterador, usado em todo percurso

import com.mycompany.academia.milho.verde.model.Identificavel; // só guarda objetos que têm ID
import java.util.ArrayList; // implementação de lista que cresce sozinha
import java.util.List; // tipo da lista (a interface)

/**
 *
 * @author aline
 */

public class DAOMemoria<T extends Identificavel> implements DAO<T> { // guarda qualquer tipo com ID, só na memória

    private final List<T> objetos; // final: a lista é sempre a mesma; o conteúdo é que muda
    private int proximoId; // próximo ID a ser entregue por gerarId

    public DAOMemoria() { // construtor sem parâmetros
        this.objetos = new ArrayList<>(); // começa com a lista vazia
        this.proximoId = 1; // o primeiro objeto recebe o ID 1
    } // fim do construtor

    @Override // implementa o método da interface
    public int gerarId() { // entrega um ID novo
        int id = proximoId; // guarda o ID que será entregue
        proximoId++; // avança para o próximo
        return id; // devolve o ID reservado
    } // fim de gerarId

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

    @Override // implementa o método da interface
    public T buscarPorId(int id) { // procura um objeto pelo ID
        for (T objeto : objetos) { // percorre a lista inteira
            if (objeto.getId() == id) { // achou o ID procurado?
                return objeto; // devolve o objeto e encerra a busca
            } // fim do if
        } // fim do for
        return null; // percorreu tudo e não achou
    } // fim de buscarPorId

    @Override // implementa o método da interface
    public List<T> listar() { // devolve todos os objetos
        return new ArrayList<>(objetos); // devolve uma cópia: quem recebe não altera a lista interna
    } // fim de listar

    @Override // implementa o método da interface
    public void atualizar(T objeto) { // troca o objeto guardado pela versão alterada
        if (objeto == null) { // não aceita "nenhum objeto"
            throw new IllegalArgumentException("Informe o objeto a atualizar."); // erro de argumento
        } // fim do if
        T atual = buscarPorId(objeto.getId()); // reaproveita a busca: acha o objeto guardado com esse ID
        if (atual == null) { // não existe objeto com esse ID?
            throw new IllegalArgumentException("Registro " + objeto.getId() + " não encontrado."); // não dá para atualizar o que não existe
        } // fim do if
        int posicao = objetos.indexOf(atual); // posição do objeto guardado na lista
        objetos.set(posicao, objeto); // substitui pela versão alterada
    } // fim de atualizar

    @Override // implementa o método da interface
    public boolean remover(int id) { // apaga o objeto com esse ID
        T atual = buscarPorId(id); // reaproveita a busca
        if (atual == null) { // não existe?
            return false; // nada foi apagado
        } // fim do if
        objetos.remove(atual); // tira da lista
        return true; // apagado
    } // fim de remover

    @Override // sobrescreve o toString herdado de Object
    public String toString() { // texto que representa o DAO
        return "DAOMemoria com " + objetos.size() + " registro(s)"; // mostra quantos registros há
    } // fim de toString
} // fim da classe DAOMemoria