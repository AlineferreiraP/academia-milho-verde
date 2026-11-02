/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde;

import com.mycompany.academia.milho.verde.controller.AcademiaController;

/**
 * Programa interativo da Academia Milho Verde, com menus no console.
 *
 * <p>Cria o {@link AcademiaController}, que usa o Sistema (Model) e a
 * ConsoleView (View). Os dados ficam em {@code academia.json}. Para
 * rodar os testes automáticos do Model, execute
 * {@link AcademiaMilhoVerde}.</p>
 *
 * @author Aline Ferreira
 */
public class AcademiaConsole {

    /** Classe só com o main: não deve ser instanciada. */
    private AcademiaConsole() {
    }

    /**
     * Ponto de entrada do programa interativo.
     *
     * @param args argumentos da linha de comando não utilizados
     */
    public static void main(String[] args) {
        new AcademiaController().iniciar();
    }
}