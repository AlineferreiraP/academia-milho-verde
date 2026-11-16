/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.dao; // pacote do DAO

import java.util.Iterator; // interface do padrão Iterator: hasNext, next e remove
import java.util.List; // lista que será percorrida
import java.util.NoSuchElementException; // erro quando pede o próximo e não existe

public class IteradorLista<T> implements Iterator<T> { // nosso iterador: percorre uma lista pela posição

    private final List<T> lista; // a lista percorrida (a mesma do DAO, não uma cópia)
    private int posicao; // posição do próximo elemento que o next vai devolver
    private boolean podeAlterar; // só pode trocar ou remover depois de um next

    public IteradorLista(List<T> novaLista) { // construtor: recebe a lista a percorrer
        if (novaLista == null) { // precisa de uma lista
            throw new IllegalArgumentException("Informe a lista a percorrer."); // erro de argumento
        } // fim do if
        this.lista = novaLista; // guarda a lista
        this.posicao = 0; // começa da primeira posição
        this.podeAlterar = false; // ainda não devolveu nenhum elemento
    } // fim do construtor

    @Override // implementa o método da interface Iterator
    public boolean hasNext() { // ainda tem elemento pela frente?
        return posicao < lista.size(); // tem, se a posição ainda está dentro da lista
    } // fim de hasNext

    @Override // implementa o método da interface Iterator
    public T next() { // devolve o elemento da posição atual e anda uma posição
        if (!hasNext()) { // já passou do fim?
            throw new NoSuchElementException("Não há mais elementos."); // erro do Iterator
        } // fim do if
        T elemento = lista.get(posicao); // pega o elemento da posição atual
        posicao++; // anda para a próxima posição
        podeAlterar = true; // agora pode trocar ou remover este elemento
        return elemento; // devolve o elemento
    } // fim de next

    public void set(T novoElemento) { // troca o elemento que o next acabou de devolver
        if (!podeAlterar) { // next não foi chamado, ou o elemento já foi removido
            throw new IllegalStateException("Chame next antes de set."); // erro de estado
        } // fim do if
        lista.set(posicao - 1, novoElemento); // o devolvido está uma posição atrás do marcador
    } // fim de set

    @Override // implementa o método da interface Iterator
    public void remove() { // remove o elemento que o next acabou de devolver
        if (!podeAlterar) { // next não foi chamado, ou já removeu
            throw new IllegalStateException("Chame next antes de remove."); // erro de estado
        } // fim do if
        posicao--; // volta para a posição do elemento devolvido
        lista.remove(posicao); // tira da lista; os seguintes andam uma casa para trás
        podeAlterar = false; // o elemento não existe mais: não pode trocar nem remover de novo
    } // fim de remove

    @Override // sobrescreve o toString herdado de Object
    public String toString() { // texto que representa o iterador
        return "IteradorLista na posição " + posicao + " de " + lista.size(); // onde está o percurso
    } // fim de toString
} // fim da classe IteradorLista