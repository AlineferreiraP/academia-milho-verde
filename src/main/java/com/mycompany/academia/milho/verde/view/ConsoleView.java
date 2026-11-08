/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.view;

import com.mycompany.academia.milho.verde.model.Extrato;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Camada View do MVC: lê do teclado e mostra no console.
 *
 * <p>Só lê e mostra: não guarda dados do negócio nem aplica regras. Os
 * métodos de leitura repetem a pergunta enquanto a resposta não puder
 * ser entendida (por exemplo, texto onde se espera um número). Se a
 * entrada terminar, {@link #mostrarMenu(String, String[])} devolve 0
 * para o programa encerrar, e os demais métodos de leitura lançam
 * {@link IllegalStateException}.</p>
 *
 * @author Aline Ferreira
 */
public class ConsoleView {

    /** Formato aceito e mostrado para datas. */
    private static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /** Formato aceito para data e hora. */
    private static final DateTimeFormatter FORMATO_DATA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /** Leitor do teclado. */
    private Scanner entrada;

    /**
     * Construtor padrão: lê da entrada padrão (teclado).
     */
    public ConsoleView() {
        this(new Scanner(System.in, "UTF-8"));
    }

    /**
     * Construtor com parâmetros.
     *
     * @param novaEntrada leitor de onde vêm as respostas
     * @throws IllegalArgumentException se for nulo
     */
    public ConsoleView(Scanner novaEntrada) {
        if (novaEntrada == null) {
            throw new IllegalArgumentException("A entrada é obrigatória.");
        }
        this.entrada = novaEntrada;
    }

    /**
     * Mostra um menu numerado e lê a opção escolhida.
     *
     * <p>A opção 0 é sempre "Voltar / Sair". Repete a pergunta até a
     * resposta ser um número da lista.</p>
     *
     * @param titulo título do menu
     * @param opcoes textos das opções 1, 2, 3...
     * @return o número escolhido, de 0 a {@code opcoes.length}; 0 se a
     *         entrada terminar
     */
    public int mostrarMenu(String titulo, String[] opcoes) {
        System.out.println();
        System.out.println("=== " + titulo + " ===");
        for (int i = 0; i < opcoes.length; i++) {
            System.out.println((i + 1) + " - " + opcoes[i]);
        }
        System.out.println("0 - Voltar / Sair");
        while (true) {
            System.out.print("Opção: ");
            String linha = lerLinha();
            if (linha == null) {
                return 0;
            }
            try {
                int opcao = Integer.parseInt(linha.trim());
                if (opcao >= 0 && opcao <= opcoes.length) {
                    return opcao;
                }
            } catch (NumberFormatException erro) {
                // Cai na mensagem abaixo e pergunta de novo.
            }
            mostrarErro("Escolha um número de 0 a " + opcoes.length + ".");
        }
    }

    /**
     * Pergunta e lê um texto.
     *
     * @param rotulo o que está sendo pedido
     * @return o texto digitado, sem espaços nas pontas
     * @throws IllegalStateException se a entrada terminar
     */
    public String lerTexto(String rotulo) {
        System.out.print(rotulo + ": ");
        String linha = lerLinha();
        if (linha == null) {
            throw new IllegalStateException("A entrada de dados terminou.");
        }
        return linha.trim();
    }

    /**
     * Pergunta e lê um número inteiro, repetindo até ser válido.
     *
     * @param rotulo o que está sendo pedido
     * @return o número digitado
     * @throws IllegalStateException se a entrada terminar
     */
    public int lerInteiro(String rotulo) {
        while (true) {
            String texto = lerTexto(rotulo);
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException erro) {
                mostrarErro("Digite um número inteiro.");
            }
        }
    }

    /**
     * Pergunta e lê um número decimal, repetindo até ser válido.
     * Aceita vírgula ou ponto.
     *
     * @param rotulo o que está sendo pedido
     * @return o número digitado
     * @throws IllegalStateException se a entrada terminar
     */
    public double lerDecimal(String rotulo) {
        while (true) {
            String texto = lerTexto(rotulo).replace(',', '.');
            try {
                return Double.parseDouble(texto);
            } catch (NumberFormatException erro) {
                mostrarErro("Digite um número, por exemplo 12,50.");
            }
        }
    }

    /**
     * Pergunta e lê uma lista de números inteiros separados por
     * vírgula, repetindo até ser válida.
     *
     * @param rotulo o que está sendo pedido
     * @return os números digitados
     * @throws IllegalStateException se a entrada terminar
     */
    public List<Integer> lerInteiros(String rotulo) {
        while (true) {
            String texto = lerTexto(rotulo);
            List<Integer> numeros = new ArrayList<>();
            boolean valido = !texto.isEmpty();
            for (String parte : texto.split(",")) {
                try {
                    numeros.add(Integer.parseInt(parte.trim()));
                } catch (NumberFormatException erro) {
                    valido = false;
                }
            }
            if (valido) {
                return numeros;
            }
            mostrarErro("Digite números inteiros separados por vírgula, por exemplo 1, 2.");
        }
    }

    /**
     * Pergunta e lê uma data no formato dd/mm/aaaa, repetindo até ser
     * válida.
     *
     * @param rotulo o que está sendo pedido
     * @return a data digitada
     * @throws IllegalStateException se a entrada terminar
     */
    public LocalDate lerData(String rotulo) {
        while (true) {
            String texto = lerTexto(rotulo + " (dd/mm/aaaa)");
            try {
                return LocalDate.parse(texto, FORMATO_DATA);
            } catch (DateTimeParseException erro) {
                mostrarErro("Data inválida. Exemplo: 25/12/2026.");
            }
        }
    }

    /**
     * Pergunta e lê data e hora no formato dd/mm/aaaa hh:mm,
     * repetindo até ser válida.
     *
     * @param rotulo o que está sendo pedido
     * @return a data e a hora digitadas
     * @throws IllegalStateException se a entrada terminar
     */
    public LocalDateTime lerDataHora(String rotulo) {
        while (true) {
            String texto = lerTexto(rotulo + " (dd/mm/aaaa hh:mm)");
            try {
                return LocalDateTime.parse(texto, FORMATO_DATA_HORA);
            } catch (DateTimeParseException erro) {
                mostrarErro("Data ou hora inválida. Exemplo: 25/12/2026 09:00.");
            }
        }
    }

    /**
     * Mostra uma mensagem.
     *
     * @param mensagem texto a mostrar
     */
    public void mostrarMensagem(String mensagem) {
        System.out.println(mensagem);
    }

    /**
     * Mostra uma mensagem de erro.
     *
     * @param mensagem texto do erro
     */
    public void mostrarErro(String mensagem) {
        System.out.println("[ERRO] " + mensagem);
    }

    /**
     * Mostra uma lista, um item por linha.
     *
     * @param titulo título da lista
     * @param itens itens a mostrar (cada um por seu {@code toString})
     */
    public void mostrarLista(String titulo, List<?> itens) {
        System.out.println("--- " + titulo + " (" + itens.size() + ") ---");
        if (itens.isEmpty()) {
            System.out.println("(nenhum)");
            return;
        }
        for (Object item : itens) {
            System.out.println(item);
        }
    }

    /**
     * Imprime o extrato de uma venda.
     *
     * @param extrato extrato a imprimir
     */
    public void imprimirExtrato(Extrato extrato) {
        System.out.println(extrato.gerarTexto());
        System.out.println("================================");
    }

    /**
     * Lê uma linha da entrada.
     *
     * @return a linha, ou {@code null} se a entrada terminou
     */
    private String lerLinha() {
        try {
            return entrada.nextLine();
        } catch (NoSuchElementException fim) {
            return null;
        }
    }

    /**
     * Retorna uma descrição da view.
     *
     * @return texto fixo identificando a view
     */
    @Override
    public String toString() {
        return "ConsoleView[console]";
    }
}
