/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model;

/**
 * Tudo que tem um ID único no sistema. Os DAOs só guardam objetos desse tipo.
 *
 * @author Aline Ferreira
 */
public interface Identificavel { // tudo que tem um ID único no sistema

    /**
     * Devolve o ID do objeto.
     *
     * @return ID único
     */
    int getId(); // toda classe que implementa precisa devolver o seu ID
} // fim da interface Identificavel