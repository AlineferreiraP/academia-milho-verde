/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.view; // pacote da View

import com.mycompany.academia.milho.verde.model.Extrato; // extrato para imprimir
import com.mycompany.academia.milho.verde.model.IteradorLista; // nosso iterador
import java.time.LocalDate; // data sem hora
import java.time.LocalDateTime; // data com hora
import java.time.format.DateTimeFormatter; // formato das datas digitadas
import java.time.format.DateTimeParseException; // data digitada errada
import java.util.ArrayList; // lista que cresce sozinha
import java.util.List; // tipo da lista
import java.util.Scanner; // lê o teclado

/**
 * View do MVC: só lê o teclado e mostra resultados na tela.
 *
 * <p>Não tem nenhuma regra de negócio. Os métodos de leitura repetem a pergunta
 * até a pessoa digitar um valor válido, então o Controller só recebe valores já convertidos.</p>
 *
 * @author Aline Ferreira
 */
public class ConsoleView { // só lê o teclado e mostra na tela; não tem regra de negócio

    /** Formato das datas digitadas, por exemplo 07/12/2026. */
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy"); // ex.: 07/12/2026
    /** Formato de data e hora digitadas, por exemplo 07/12/2026 07:00. */
    private static final DateTimeFormatter FORMATO_DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"); // ex.: 07/12/2026 07:00

    /** De onde vem o que o usuário digita. */
    private final Scanner entrada; // de onde vem o que o usuário digita

    /**
     * Cria a View lendo do teclado.
     */
    public ConsoleView() { // construtor sem parâmetros
        this(new Scanner(System.in)); // lê do teclado
    } // fim do construtor sem parâmetros

    /**
     * Cria a View lendo do {@link Scanner} informado.
     *
     * @param novaEntrada de onde ler o que for digitado
     */
    public ConsoleView(Scanner novaEntrada) { // recebe o Scanner
        this.entrada = novaEntrada; // guarda o Scanner
    } // fim do construtor com parâmetros

    /**
     * Mostra um menu e devolve a opção escolhida.
     *
     * @param titulo título do menu
     * @param opcoes uma linha de texto por opção
     * @return o número digitado
     */
    public int mostrarMenu(String titulo, String[] opcoes) { // mostra as opções e devolve a escolhida
        System.out.println(); // linha em branco
        System.out.println("===== " + titulo + " ====="); // título
        for (int i = 0; i < opcoes.length; i++) { // vetor usa índice
            System.out.println(opcoes[i]); // uma opção por linha
        } // fim do for
        return lerInteiro("Opção"); // número da opção
    } // fim de mostrarMenu

    /**
     * Lê uma linha de texto.
     *
     * @param rotulo pergunta mostrada antes da leitura
     * @return o que foi digitado, sem espaços nas pontas
     */
    public String lerTexto(String rotulo) { // lê uma linha de texto
        System.out.print(rotulo + ": "); // pergunta
        return entrada.nextLine().trim(); // resposta sem espaços nas pontas
    } // fim de lerTexto

    /**
     * Lê um número inteiro, repetindo a pergunta até ser válido.
     *
     * @param rotulo pergunta mostrada antes da leitura
     * @return o número digitado
     */
    public int lerInteiro(String rotulo) { // lê um número inteiro; repete até ser válido
        while (true) { // até o usuário digitar certo
            String texto = lerTexto(rotulo); // lê o texto
            try { // a conversão pode falhar
                return Integer.parseInt(texto); // converte e devolve
            } catch (NumberFormatException erro) { // não era número
                mostrarErro("Digite um número inteiro."); // avisa e repete
            } // fim do try
        } // fim do while
    } // fim de lerInteiro

    /**
     * Lê um número decimal, aceitando vírgula ou ponto, repetindo até ser válido.
     *
     * @param rotulo pergunta mostrada antes da leitura
     * @return o número digitado
     */
    public double lerDecimal(String rotulo) { // lê um número com vírgula ou ponto
        while (true) { // até o usuário digitar certo
            String texto = lerTexto(rotulo).replace(',', '.'); // aceita 10,50 ou 10.50
            try { // a conversão pode falhar
                return Double.parseDouble(texto); // converte e devolve
            } catch (NumberFormatException erro) { // não era número
                mostrarErro("Digite um número, por exemplo 10,50."); // avisa e repete
            } // fim do try
        } // fim do while
    } // fim de lerDecimal

    /**
     * Lê vários números inteiros separados por vírgula, por exemplo 1,2,3.
     *
     * @param rotulo pergunta mostrada antes da leitura
     * @return a lista de números digitados
     */
    public List<Integer> lerInteiros(String rotulo) { // lê vários números separados por vírgula (ex.: 1,2,3)
        while (true) { // até o usuário digitar certo
            String[] partes = lerTexto(rotulo + " (separe por vírgula)").split(","); // separa os números
            List<Integer> numeros = new ArrayList<>(); // resultado
            try { // a conversão pode falhar
                for (int i = 0; i < partes.length; i++) { // vetor usa índice
                    numeros.add(Integer.parseInt(partes[i].trim())); // converte cada parte
                } // fim do for
                return numeros; // devolve a lista
            } catch (NumberFormatException erro) { // alguma parte não era número
                mostrarErro("Digite números separados por vírgula, por exemplo 1,2,3."); // avisa e repete
            } // fim do try
        } // fim do while
    } // fim de lerInteiros

    /**
     * Lê uma data no formato dd/MM/aaaa, repetindo até ser válida.
     *
     * @param rotulo pergunta mostrada antes da leitura
     * @return a data digitada
     */
    public LocalDate lerData(String rotulo) { // lê uma data dd/MM/aaaa
        while (true) { // até o usuário digitar certo
            try { // a conversão pode falhar
                return LocalDate.parse(lerTexto(rotulo + " (dd/MM/aaaa)"), FORMATO_DATA); // converte e devolve
            } catch (DateTimeParseException erro) { // formato errado
                mostrarErro("Data inválida."); // avisa e repete
            } // fim do try
        } // fim do while
    } // fim de lerData

    /**
     * Lê data e hora no formato dd/MM/aaaa HH:mm, repetindo até ser válido.
     *
     * @param rotulo pergunta mostrada antes da leitura
     * @return a data e hora digitadas
     */
    public LocalDateTime lerDataHora(String rotulo) { // lê data e hora dd/MM/aaaa HH:mm
        while (true) { // até o usuário digitar certo
            try { // a conversão pode falhar
                return LocalDateTime.parse(lerTexto(rotulo + " (dd/MM/aaaa HH:mm)"), FORMATO_DATA_HORA); // converte
            } catch (DateTimeParseException erro) { // formato errado
                mostrarErro("Data e hora inválidas."); // avisa e repete
            } // fim do try
        } // fim do while
    } // fim de lerDataHora

    /**
     * Mostra uma mensagem comum.
     *
     * @param mensagem texto a mostrar
     */
    public void mostrarMensagem(String mensagem) { // mensagem comum
        System.out.println(mensagem); // mostra na tela
    } // fim de mostrarMensagem

    /**
     * Mostra uma mensagem de erro, com o prefixo "Erro: ".
     *
     * @param mensagem texto do erro
     */
    public void mostrarErro(String mensagem) { // mensagem de erro
        System.out.println("Erro: " + mensagem); // mostra com o prefixo
    } // fim de mostrarErro

    /**
     * Mostra qualquer lista, um item por linha, usando o {@code toString} de cada item.
     * O {@code ?} faz o mesmo método servir para lista de qualquer tipo.
     *
     * @param titulo título da lista
     * @param itens itens a mostrar
     */
    public void mostrarLista(String titulo, List<?> itens) { // mostra qualquer lista (o ? aceita lista de qualquer tipo)
        System.out.println("--- " + titulo + " (" + itens.size() + ") ---"); // título e quantidade
        IteradorLista<?> iterador = new IteradorLista<>(itens); // nosso iterador percorre a lista
        while (iterador.hasNext()) { // enquanto houver item
            System.out.println(iterador.next()); // chama o toString de cada item
        } // fim do while
    } // fim de mostrarLista

    /**
     * Imprime um extrato.
     *
     * @param extrato extrato a imprimir
     */
    public void imprimirExtrato(Extrato extrato) { // extrato impresso
        System.out.println(extrato.gerarTexto()); // o próprio extrato monta o texto
    } // fim de imprimirExtrato

    /**
     * Descreve a View.
     *
     * @return texto fixo informando que a entrada é pelo teclado
     */
    @Override // sobrescreve o toString de Object
    public String toString() { // resumo em uma linha
        return "ConsoleView (entrada pelo teclado)"; // descrição
    } // fim do toString
}