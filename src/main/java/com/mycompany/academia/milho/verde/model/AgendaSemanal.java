/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Quadro de horários de uma sala em uma semana.
 *
 * <p>A grade é uma matriz {@code grade[horario][dia]}: 18 linhas (das
 * 5h às 22h, aulas de 1 hora, a última termina às 23h) e 6 colunas
 * (segunda a sábado). Célula vazia ({@code null}) é horário livre.</p>
 *
 * @author Aline Ferreira
 */
public class AgendaSemanal {

    /** Número de dias de funcionamento (segunda a sábado). */
    public static final int DIAS = 6;

    /** Número de horários por dia (das 5h às 22h). */
    public static final int HORARIOS = 18;

    /** Hora da primeira aula do dia. */
    public static final int PRIMEIRA_HORA = 5;

    /** Segunda-feira que abre a semana. */
    private LocalDate inicioSemana;

    /** Sala a que esta agenda pertence. */
    private Sala sala;

    /** Matriz de aulas: linha é o horário, coluna é o dia. */
    private Aula[][] grade;

    /**
     * Construtor padrão: cria uma agenda vazia, sem sala e sem semana.
     */
    public AgendaSemanal() {
        this.inicioSemana = null;
        this.sala = null;
        this.grade = new Aula[HORARIOS][DIAS];
    }

    /**
     * Construtor com parâmetros.
     *
     * @param novoInicio segunda-feira da semana
     * @param novaSala sala da agenda
     * @throws IllegalArgumentException se algum dado for nulo ou se a
     *         data não for uma segunda-feira
     */
    public AgendaSemanal(LocalDate novoInicio, Sala novaSala) {
        if (novoInicio == null || novaSala == null) {
            throw new IllegalArgumentException(
                    "A semana e a sala são obrigatórias.");
        }
        if (novoInicio.getDayOfWeek() != DayOfWeek.MONDAY) {
            throw new IllegalArgumentException(
                    "A semana deve começar numa segunda-feira.");
        }
        this.inicioSemana = novoInicio;
        this.sala = novaSala;
        this.grade = new Aula[HORARIOS][DIAS];
    }

    /**
     * Retorna a segunda-feira da semana.
     *
     * @return a data de início
     */
    public LocalDate getInicioSemana() {
        return inicioSemana;
    }

    /**
     * Retorna a sala da agenda.
     *
     * @return a sala
     */
    public Sala getSala() {
        return sala;
    }

    /**
     * Retorna a aula de uma célula da grade.
     *
     * @param dia 0 (segunda) a 5 (sábado)
     * @param horario 0 (5h) a 17 (22h)
     * @return a aula, ou {@code null} se o horário está livre
     * @throws IllegalArgumentException se dia ou horário estiverem fora
     *         da grade
     */
    public Aula getAula(int dia, int horario) {
        if (dia < 0 || dia >= DIAS || horario < 0 || horario >= HORARIOS) {
            throw new IllegalArgumentException(
                    "Dia ou horário fora da grade.");
        }
        return grade[horario][dia];
    }

    /**
     * Informa se um horário está livre.
     *
     * @param dia 0 (segunda) a 5 (sábado)
     * @param horario 0 (5h) a 17 (22h)
     * @return {@code true} se não há aula nessa célula
     */
    public boolean estaLivre(int dia, int horario) {
        return getAula(dia, horario) == null;
    }

    /**
     * Coloca uma aula na célula do seu dia e horário.
     *
     * @param aula aula de uma hora cheia, desta sala e desta semana
     * @throws IllegalArgumentException se a aula for nula, de outra sala,
     *         de outra semana, de domingo, fora do horário de
     *         funcionamento ou não começar em hora cheia
     * @throws IllegalStateException se o horário já estiver ocupado
     */
    public void adicionarAula(Aula aula) {
        if (aula == null) {
            throw new IllegalArgumentException("A aula é obrigatória.");
        }
        if (aula.getSala() == null
                || aula.getSala().getCodigo() != sala.getCodigo()) {
            throw new IllegalArgumentException(
                    "A aula pertence a outra sala.");
        }
        LocalDateTime quando = aula.getDataHora();
        if (!contem(quando)) {
            throw new IllegalArgumentException(
                    "A aula não pertence a esta semana.");
        }
        if (quando.getMinute() != 0) {
            throw new IllegalArgumentException(
                    "As aulas começam em hora cheia.");
        }
        int dia = calcularDia(quando);
        int horario = calcularHorario(quando);
        if (grade[horario][dia] != null) {
            throw new IllegalStateException("Esse horário já está ocupado.");
        }
        grade[horario][dia] = aula;
    }

    /**
     * Retira uma aula da grade.
     *
     * @param aula aula a retirar
     * @return {@code true} se a aula estava na grade
     */
    public boolean removerAula(Aula aula) {
        for (int dia = 0; dia < DIAS; dia++) {
            for (int horario = 0; horario < HORARIOS; horario++) {
                if (grade[horario][dia] != null && grade[horario][dia] == aula) {
                    grade[horario][dia] = null;
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Lista as aulas da semana em ordem cronológica.
     *
     * @return lista nova com as aulas, de segunda a sábado
     */
    public List<Aula> listarAulas() {
        List<Aula> aulas = new ArrayList<>();
        for (int dia = 0; dia < DIAS; dia++) {
            for (int horario = 0; horario < HORARIOS; horario++) {
                if (grade[horario][dia] != null) {
                    aulas.add(grade[horario][dia]);
                }
            }
        }
        return aulas;
    }

    /**
     * Informa se uma data e hora cai nesta semana.
     *
     * @param dataHora data e hora a conferir
     * @return {@code true} se está entre a segunda e o domingo da semana
     */
    public boolean contem(LocalDateTime dataHora) {
        if (dataHora == null || inicioSemana == null) {
            return false;
        }
        LocalDate data = dataHora.toLocalDate();
        return !data.isBefore(inicioSemana)
                && !data.isAfter(inicioSemana.plusDays(6));
    }

    /**
     * Calcula a coluna da grade a partir da data.
     *
     * @param dataHora data e hora da aula
     * @return 0 para segunda até 5 para sábado
     * @throws IllegalArgumentException se for domingo
     */
    private int calcularDia(LocalDateTime dataHora) {
        int dia = dataHora.getDayOfWeek().getValue() - 1;
        if (dia >= DIAS) {
            throw new IllegalArgumentException(
                    "A academia não abre aos domingos.");
        }
        return dia;
    }

    /**
     * Calcula a linha da grade a partir da hora.
     *
     * @param dataHora data e hora da aula
     * @return 0 para 5h até 17 para 22h
     * @throws IllegalArgumentException se a hora estiver fora do
     *         funcionamento
     */
    private int calcularHorario(LocalDateTime dataHora) {
        int horario = dataHora.getHour() - PRIMEIRA_HORA;
        if (horario < 0 || horario >= HORARIOS) {
            throw new IllegalArgumentException(
                    "A academia funciona das 5h às 23h; a aula deve começar entre 5h e 22h.");
        }
        return horario;
    }

    /**
     * Imprime a semana como tabela de horários.
     *
     * @return texto com uma linha por horário e uma coluna por dia
     */
    @Override
    public String toString() {
        if (sala == null || inicioSemana == null) {
            return "Agenda em branco.";
        }
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String[] nomes = {"Seg", "Ter", "Qua", "Qui", "Sex", "Sáb"};
        StringBuilder texto = new StringBuilder();
        texto.append("Sala ").append(sala.getCodigo()).append(" (")
                .append(sala.getModalidade()).append(") - semana de ")
                .append(inicioSemana.format(formato)).append('\n');
        texto.append(String.format("%-6s", "Hora"));
        for (int dia = 0; dia < DIAS; dia++) {
            texto.append(String.format("| %-14s", nomes[dia] + " "
                    + inicioSemana.plusDays(dia).getDayOfMonth()));
        }
        texto.append('\n');
        for (int horario = 0; horario < HORARIOS; horario++) {
            texto.append(String.format("%02dh   ", horario + PRIMEIRA_HORA));
            for (int dia = 0; dia < DIAS; dia++) {
                Aula aula = grade[horario][dia];
                String celula = (aula == null) ? "-"
                        : "#" + aula.getId() + " " + aula.getNomeInstrutor();
                if (celula.length() > 14) {
                    celula = celula.substring(0, 14);
                }
                texto.append(String.format("| %-14s", celula));
            }
            texto.append('\n');
        }
        return texto.toString();
    }
}