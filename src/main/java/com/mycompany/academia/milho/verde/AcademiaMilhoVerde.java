/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.academia.milho.verde; // pacote raiz do projeto

import com.mycompany.academia.milho.verde.controller.AcademiaController; // liga a tela às regras do sistema
import com.mycompany.academia.milho.verde.dao.ClienteDAOJson; // guarda os clientes em arquivo JSON
import com.mycompany.academia.milho.verde.model.RepositorioJson; // lê e grava o academia.json
import com.mycompany.academia.milho.verde.model.Sistema; // fachada do Model
import com.mycompany.academia.milho.verde.view.ConsoleView; // tela em modo texto

/**
 * Classe principal do projeto. Ao rodar, mostra a tela de entrada, onde a pessoa escolhe
 * entre abrir o sistema Academia (menu), fazer os testes do sistema (questões) ou fechar.
 *
 * <p>O menu é o sistema do dia a dia: monta as três partes do MVC ({@link Sistema},
 * {@link ConsoleView} e {@link RepositorioJson}) e entrega o controle ao
 * {@link AcademiaController}. Os testes ficam na classe {@link TestesSistema}, que esta classe chama
 * ({@code TestesSistema.executar()}): executam todos os blocos {@code // Questao N { ... }} em sequência
 * e imprimem o resultado.
 * Os argumentos {@code menu} e {@code testes} pulam a tela de entrada.</p>
 *
 * @author Aline Ferreira
 */
public class AcademiaMilhoVerde { // classe principal: porta de entrada do menu e dos testes

    /**
     * Construtor privado: a classe só tem métodos estáticos e não deve ser instanciada.
     */
    private AcademiaMilhoVerde() { // construtor privado
    } // fim do construtor

    /**
     * Ponto de entrada do programa.
     *
     * @param args sem argumento mostra a tela de entrada; {@code menu} abre o sistema direto;
     *             {@code testes} roda direto as questões
     */
    public static void main(String[] args) { // ponto de entrada do programa
        if (args.length > 0 && args[0].equals("testes")) { // pediu só as questões?
            TestesSistema.executar(); // roda as questões e termina
            return; // encerra
        } // fim do if
        ConsoleView view = new ConsoleView(); // a mesma tela serve à entrada e ao sistema
        if (args.length > 0 && args[0].equals("menu")) { // pediu só o sistema?
            abrirSistema(view); // abre o menu e termina
            return; // encerra
        } // fim do if
        telaInicial(view); // sem argumento: tela de entrada
    } // fim do main

    /**
     * Tela de entrada: mostra o nome da academia e deixa a pessoa escolher entre
     * abrir o sistema, fazer os testes do sistema ou fechar. Volta para cá depois de cada escolha.
     *
     * @param view a tela em modo texto
     */
    private static void telaInicial(ConsoleView view) { // nome da academia e escolha do que fazer
        view.mostrarMensagem("ACADEMIA MILHO VERDE"); // nome do sistema
        boolean continuar = true; // enquanto a pessoa não for embora
        while (continuar) { // volta para a entrada depois de cada escolha
            int opcao = view.mostrarMenu("O QUE VOCÊ QUER FAZER?", new String[]{
                "1 - Abrir o sistema Academia",
                "2 - Testes do sistema",
                "0 - Fechar"}); // opções
            switch (opcao) { // executa a escolha
                case 1 -> abrirSistema(view); // abre o menu do sistema
                case 2 -> TestesSistema.executar(); // roda as questões
                case 0 -> { // ir embora
                    view.mostrarMensagem("Até a próxima! Bons treinos!"); // despedida
                    continuar = false; // sai do laço
                } // fim do case 0
                default -> view.mostrarErro("Escolha 1, 2 ou 0."); // opção que não existe
            } // fim do switch
        } // fim do while
    } // fim de telaInicial

    /**
     * Abre o sistema: monta as três partes do MVC ({@link Sistema}, {@link ConsoleView} e
     * {@link RepositorioJson}) e entrega o controle ao {@link AcademiaController}.
     *
     * @param view a tela em modo texto
     */
    private static void abrirSistema(ConsoleView view) { // liga o MVC e abre o menu
        Sistema sistemaReal = new Sistema(new ClienteDAOJson()); // Model, com os clientes no clientes.json
        RepositorioJson repositorio = new RepositorioJson(); // lê e grava o academia.json
        new AcademiaController(sistemaReal, view, repositorio).iniciar(); // liga as três partes e abre o menu
    } // fim de abrirSistema

} // fim da classe AcademiaMilhoVerde