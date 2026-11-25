/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model; // pacote do Model

import java.util.Iterator; // interface do padrão Iterator: hasNext, next e remove
import java.util.List; // lista que será percorrida
import java.util.NoSuchElementException; // erro quando pede o próximo e não existe

/**
 * Nosso iterador: percorre uma lista pela posição (padrão Iterator).
 * Todo percurso de lista do sistema usa esta classe; o for-each do Java faz o mesmo por baixo dos panos.
 * Além de hasNext, next e remove, oferece set para trocar o elemento devolvido.
 *
 * @param <T> tipo dos elementos da lista
 * @author Aline Ferreira
 */
public class IteradorLista<T> implements Iterator<T> { // nosso iterador: percorre uma lista pela posição

    /** Lista percorrida (a mesma do DAO, não uma cópia). */
    private final List<T> lista; // a lista percorrida (a mesma do DAO, não uma cópia)
    /** Posição do próximo elemento que o next vai devolver. */
    private int posicao; // posição do próximo elemento que o next vai devolver
    /** Só pode trocar ou remover depois de um next. */
    private boolean podeAlterar; // só pode trocar ou remover depois de um next

    /**
     * Cria o iterador no começo da lista.
     *
     * @param novaLista lista a percorrer (não pode ser nula)
     * @throws IllegalArgumentException se a lista for nula
     */
    public IteradorLista(List<T> novaLista) { // construtor: recebe a lista a percorrer
        if (novaLista == null) { // precisa de uma lista
            throw new IllegalArgumentException("Informe a lista a percorrer."); // erro de argumento
        } // fim do if
        this.lista = novaLista; // guarda a lista
        this.posicao = 0; // começa da primeira posição
        this.podeAlterar = false; // ainda não devolveu nenhum elemento
    } // fim do construtor

    /**
     * Diz se ainda há elemento pela frente.
     *
     * @return {@code true} se a posição ainda está dentro da lista
     */
    @Override // implementa o método da interface Iterator
    public boolean hasNext() { // ainda tem elemento pela frente?
        return posicao < lista.size(); // tem, se a posição ainda está dentro da lista
    } // fim de hasNext

    /**
     * Devolve o elemento da posição atual e anda uma posição.
     *
     * @return o elemento
     * @throws NoSuchElementException se não houver mais elementos
     */
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

    /**
     * Troca o elemento que o último next devolveu.
     *
     * @param novoElemento elemento que ocupa o lugar
     * @throws IllegalStateException se next não foi chamado antes
     */
    public void set(T novoElemento) { // troca o elemento que o next acabou de devolver
        if (!podeAlterar) { // next não foi chamado, ou o elemento já foi removido
            throw new IllegalStateException("Chame next antes de set."); // erro de estado
        } // fim do if
        lista.set(posicao - 1, novoElemento); // o devolvido está uma posição atrás do marcador
    } // fim de set

    /**
     * Remove da lista o elemento que o último next devolveu.
     *
     * @throws IllegalStateException se next não foi chamado antes
     */
    @Override // implementa o método da interface Iterator
    public void remove() { // remove o elemento que o next acabou de devolver
        if (!podeAlterar) { // next não foi chamado, ou já removeu
            throw new IllegalStateException("Chame next antes de remove."); // erro de estado
        } // fim do if
        posicao--; // volta para a posição do elemento devolvido
        lista.remove(posicao); // tira da lista; os seguintes andam uma casa para trás
        podeAlterar = false; // o elemento não existe mais: não pode trocar nem remover de novo
    } // fim de remove

    /**
     * Resume o iterador em uma linha.
     *
     * @return posição atual e tamanho da lista
     */
    @Override // sobrescreve o toString herdado de Object
    public String toString() { // texto que representa o iterador
        return "IteradorLista na posição " + posicao + " de " + lista.size(); // onde está o percurso
    } // fim de toString
} // fim da classe IteradorLista