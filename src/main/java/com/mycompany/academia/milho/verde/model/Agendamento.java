/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa a reserva de uma ou mais aulas feita por um cliente.
 *
 * <p>Todo agendamento começa {@link #PRELIMINAR}. O funcionário
 * confirma até 5 dias úteis antes da primeira aula; se o prazo passar,
 * o agendamento é cancelado. O cancelamento de uma reserva confirmada
 * devolve 50% do preço quando faltam 3 dias ou mais.</p>
 *
 * @author Aline Ferreira
 */
public class Agendamento {

    /** Tipo de cobrança: diária. */
    public static final String DIARIA = "DIARIA";

    /** Tipo de cobrança: mensalidade. */
    public static final String MENSALIDADE = "MENSALIDADE";

    /** Situação: reservado, ainda sem cobrança. */
    public static final String PRELIMINAR = "PRELIMINAR";

    /** Situação: confirmado e cobrado. */
    public static final String CONFIRMADO = "CONFIRMADO";

    /** Situação: cancelado. */
    public static final String CANCELADO = "CANCELADO";

    /** Dias úteis de antecedência para confirmar. */
    private static final int DIAS_UTEIS_CONFIRMACAO = 5;

    /** Dias corridos mínimos para ter devolução ao cancelar. */
    private static final int DIAS_PARA_DEVOLUCAO = 3;

    /** Identificador único (0 enquanto não definido). */
    private int id;

    /** Cliente que reservou. */
    private Cliente cliente;

    /** Aulas reservadas. */
    private List<Aula> aulas;

    /** Tipo de cobrança: {@link #DIARIA} ou {@link #MENSALIDADE}. */
    private String tipoCobranca;

    /** Preço total da reserva, em reais. */
    private double preco;

    /** Número do cartão do cliente (nunca aparece por inteiro no texto). */
    private String numeroCartao;

    /** Situação: preliminar, confirmado ou cancelado. */
    private String status;

    /** Valor devolvido ao cancelar (0 até haver cancelamento). */
    private double valorDevolvido;

    /**
     * Construtor padrão: cria um agendamento em branco, preliminar.
     */
    public Agendamento() {
        this.id = 0;
        this.cliente = null;
        this.aulas = new ArrayList<>();
        this.tipoCobranca = "";
        this.preco = 0;
        this.numeroCartao = "";
        this.status = PRELIMINAR;
        this.valorDevolvido = 0;
    }

    /**
     * Construtor com parâmetros: cria um agendamento preliminar.
     *
     * @param novoId identificador positivo
     * @param novoCliente cliente que reserva
     * @param novasAulas aulas reservadas (pelo menos uma)
     * @param novoTipo {@link #DIARIA} ou {@link #MENSALIDADE}
     * @param novoPreco preço total, maior que zero
     * @param novoCartao número do cartão (13 a 19 dígitos)
     * @throws IllegalArgumentException se algum dado for inválido
     */
    public Agendamento(int novoId, Cliente novoCliente, List<Aula> novasAulas,
                       String novoTipo, double novoPreco, String novoCartao) {
        if (novoId <= 0) {
            throw new IllegalArgumentException(
                    "O identificador deve ser positivo.");
        }
        if (novoCliente == null) {
            throw new IllegalArgumentException("O cliente é obrigatório.");
        }
        if (novasAulas == null || novasAulas.isEmpty()) {
            throw new IllegalArgumentException(
                    "Escolha pelo menos uma aula.");
        }
        for (Aula aula : novasAulas) {
            if (aula == null) {
                throw new IllegalArgumentException("Aula inválida.");
            }
        }
        if (novoPreco <= 0) {
            throw new IllegalArgumentException(
                    "O preço deve ser maior que zero.");
        }
        this.id = novoId;
        this.cliente = novoCliente;
        this.aulas = new ArrayList<>(novasAulas);
        this.tipoCobranca = exigirTipoCobranca(novoTipo);
        this.preco = novoPreco;
        this.numeroCartao = exigirCartao(novoCartao);
        this.status = PRELIMINAR;
        this.valorDevolvido = 0;
    }

    /**
     * Retorna o identificador.
     *
     * @return o ID, ou 0 se ainda não foi definido
     */
    public int getId() {
        return id;
    }

    /**
     * Retorna o cliente.
     *
     * @return o cliente
     */
    public Cliente getCliente() {
        return cliente;
    }

    /**
     * Retorna as aulas reservadas.
     *
     * @return cópia da lista de aulas
     */
    public List<Aula> getAulas() {
        return new ArrayList<>(aulas);
    }

    /**
     * Retorna o tipo de cobrança.
     *
     * @return {@link #DIARIA} ou {@link #MENSALIDADE}
     */
    public String getTipoCobranca() {
        return tipoCobranca;
    }

    /**
     * Retorna o preço total.
     *
     * @return o preço em reais
     */
    public double getPreco() {
        return preco;
    }

    /**
     * Retorna o cartão com só os 4 últimos dígitos visíveis.
     *
     * @return por exemplo {@code **** **** **** 1234}
     */
    public String getCartaoMascarado() {
        if (numeroCartao.length() < 4) {
            return "****";
        }
        return "**** **** **** "
                + numeroCartao.substring(numeroCartao.length() - 4);
    }

    /**
     * Retorna o número completo do cartão, para gravação no arquivo.
     *
     * <p>Acesso de pacote: só o {@link RepositorioJson} usa.</p>
     *
     * @return o número do cartão
     */
    String getNumeroCartao() {
        return numeroCartao;
    }

    /**
     * Retorna a situação.
     *
     * @return {@link #PRELIMINAR}, {@link #CONFIRMADO} ou {@link #CANCELADO}
     */
    public String getStatus() {
        return status;
    }

    /**
     * Retorna o valor devolvido no cancelamento.
     *
     * @return o valor em reais (0 se não houve devolução)
     */
    public double getValorDevolvido() {
        return valorDevolvido;
    }

    /**
     * Retorna a data da primeira aula reservada.
     *
     * @return a data mais cedo entre as aulas, ou {@code null} se não
     *         há aulas
     */
    public LocalDate getPrimeiraData() {
        LocalDate primeira = null;
        for (Aula aula : aulas) {
            LocalDate data = aula.getDataHora().toLocalDate();
            if (primeira == null || data.isBefore(primeira)) {
                primeira = data;
            }
        }
        return primeira;
    }

    /**
     * Calcula o último dia para confirmar: 5 dias úteis (segunda a
     * sexta) antes da primeira aula.
     *
     * @return a data limite, ou {@code null} se não há aulas
     */
    public LocalDate calcularPrazoConfirmacao() {
        LocalDate primeira = getPrimeiraData();
        if (primeira == null) {
            return null;
        }
        return subtrairDiasUteis(primeira, DIAS_UTEIS_CONFIRMACAO);
    }

    /**
     * Confirma o agendamento, se ainda estiver no prazo.
     *
     * <p>Confirmar exatamente no último dia do prazo é permitido.</p>
     *
     * @param hoje data da confirmação
     * @return {@code true} se confirmou; {@code false} se o prazo passou
     * @throws IllegalStateException se o agendamento não estiver preliminar
     */
    public boolean confirmar(LocalDate hoje) {
        if (!PRELIMINAR.equals(status)) {
            throw new IllegalStateException(
                    "Só um agendamento preliminar pode ser confirmado.");
        }
        exigirAulas();
        if (hoje.isAfter(calcularPrazoConfirmacao())) {
            return false;
        }
        this.status = CONFIRMADO;
        return true;
    }

    /**
     * Cancela o agendamento.
     *
     * <p>Se estava confirmado e faltam 3 dias corridos ou mais para a
     * primeira aula, devolve 50% do preço; senão, nada. Um
     * agendamento preliminar não foi cobrado e não devolve nada.</p>
     *
     * @param hoje data do cancelamento
     * @return o valor devolvido ao cliente
     * @throws IllegalStateException se já estiver cancelado
     */
    public double cancelar(LocalDate hoje) {
        if (CANCELADO.equals(status)) {
            throw new IllegalStateException("O agendamento já está cancelado.");
        }
        exigirAulas();
        double devolver = 0;
        if (CONFIRMADO.equals(status)) {
            long dias = ChronoUnit.DAYS.between(hoje, getPrimeiraData());
            if (dias >= DIAS_PARA_DEVOLUCAO) {
                devolver = preco * 0.5;
            }
        }
        this.status = CANCELADO;
        this.valorDevolvido = devolver;
        return devolver;
    }

    /**
     * Cancela o agendamento preliminar cujo prazo de confirmação passou.
     *
     * @param hoje data de hoje
     * @return {@code true} se cancelou
     */
    public boolean cancelarSeVencido(LocalDate hoje) {
        if (aulas.isEmpty()) {
            return false;
        }
        if (PRELIMINAR.equals(status) && hoje.isAfter(calcularPrazoConfirmacao())) {
            this.status = CANCELADO;
            return true;
        }
        return false;
    }

    /**
     * Restaura a situação lida do arquivo JSON.
     *
     * <p>Acesso de pacote: só o {@link RepositorioJson} usa, para
     * recriar um agendamento já confirmado ou cancelado.</p>
     *
     * @param novoStatus situação gravada
     * @param novoValorDevolvido valor devolvido gravado
     * @throws IllegalArgumentException se a situação ou o valor forem inválidos
     */
    void restaurarSituacao(String novoStatus, double novoValorDevolvido) {
        if (!PRELIMINAR.equals(novoStatus) && !CONFIRMADO.equals(novoStatus)
                && !CANCELADO.equals(novoStatus)) {
            throw new IllegalArgumentException("Situação inválida: " + novoStatus);
        }
        if (novoValorDevolvido < 0) {
            throw new IllegalArgumentException(
                    "O valor devolvido não pode ser negativo.");
        }
        this.status = novoStatus;
        this.valorDevolvido = novoValorDevolvido;
    }

    /**
     * Define o identificador de um agendamento criado pelo construtor
     * padrão. Acesso de pacote; funciona uma única vez.
     *
     * @param novoId identificador positivo
     * @throws IllegalArgumentException se não for positivo
     * @throws IllegalStateException se já tiver identificador
     */
    void definirId(int novoId) {
        if (this.id != 0) {
            throw new IllegalStateException(
                    "O identificador já foi definido.");
        }
        if (novoId <= 0) {
            throw new IllegalArgumentException(
                    "O identificador deve ser positivo.");
        }
        this.id = novoId;
    }

    /**
     * Confere se o agendamento tem aulas, necessárias para calcular
     * prazos.
     *
     * @throws IllegalStateException se o agendamento está em branco
     */
    private void exigirAulas() {
        if (aulas.isEmpty()) {
            throw new IllegalStateException("O agendamento não tem aulas.");
        }
    }

    /**
     * Volta alguns dias úteis (segunda a sexta) a partir de uma data.
     *
     * @param data data de partida
     * @param dias quantidade de dias úteis a voltar
     * @return a data alcançada
     */
    private static LocalDate subtrairDiasUteis(LocalDate data, int dias) {
        LocalDate atual = data;
        int restantes = dias;
        while (restantes > 0) {
            atual = atual.minusDays(1);
            DayOfWeek dia = atual.getDayOfWeek();
            if (dia != DayOfWeek.SATURDAY && dia != DayOfWeek.SUNDAY) {
                restantes--;
            }
        }
        return atual;
    }

    /**
     * Confere se o tipo de cobrança é aceito.
     *
     * @param valor tipo informado
     * @return o próprio {@code valor}, quando válido
     * @throws IllegalArgumentException se não for DIARIA nem MENSALIDADE
     */
    private static String exigirTipoCobranca(String valor) {
        if (DIARIA.equals(valor) || MENSALIDADE.equals(valor)) {
            return valor;
        }
        throw new IllegalArgumentException(
                "O tipo de cobrança deve ser DIARIA ou MENSALIDADE.");
    }

    /**
     * Confere o número do cartão e remove espaços e traços.
     *
     * @param valor número informado
     * @return só os dígitos
     * @throws IllegalArgumentException se não tiver de 13 a 19 dígitos
     */
    private static String exigirCartao(String valor) {
        if (valor == null) {
            throw new IllegalArgumentException("O cartão é obrigatório.");
        }
        String digitos = valor.replace(" ", "").replace("-", "");
        if (!digitos.matches("\\d{13,19}")) {
            throw new IllegalArgumentException(
                    "O cartão deve ter de 13 a 19 dígitos.");
        }
        return digitos;
    }

    /**
     * Retorna uma descrição legível, sem o número completo do cartão.
     *
     * @return texto com ID, cliente, tipo, aulas, preço e situação
     */
    @Override
    public String toString() {
        String nome = (cliente == null) ? "(sem cliente)" : cliente.getNome();
        return "Agendamento[id=" + id + ", cliente=" + nome
                + ", tipo=" + tipoCobranca + ", aulas=" + aulas.size()
                + ", primeira data=" + getPrimeiraData()
                + ", preço=R$ " + String.format("%.2f", preco)
                + ", cartão=" + getCartaoMascarado()
                + ", situação=" + status + "]";
    }
}