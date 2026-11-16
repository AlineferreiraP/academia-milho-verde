/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model; // pacote do Model

import java.time.DayOfWeek; // dias da semana (MONDAY, SUNDAY...)
import java.time.LocalDate; // data sem hora
import java.time.LocalDateTime; // data com hora
import java.time.temporal.TemporalAdjusters; // ajusta uma data, por exemplo para a segunda-feira da semana
import java.util.ArrayList; // lista que cresce sozinha
import java.util.List; // tipo da lista

public class AgendaSemanal { // quadro de horários de UMA sala em UMA semana

    public static final int DIAS = 6; // colunas: segunda a sábado
    public static final int HORARIOS = 18; // linhas: aulas das 5h às 22h (a última termina às 23h)
    public static final int PRIMEIRA_HORA = 5; // horário da primeira aula do dia

    private final LocalDate inicioSemana; // a segunda-feira desta semana
    private final Sala sala; // a sala desta agenda
    private final Aula[][] grade; // matriz [horário][dia]; null quer dizer horário livre

    public AgendaSemanal() { // construtor sem parâmetros
        this.inicioSemana = null; // ainda sem semana
        this.sala = null; // ainda sem sala
        this.grade = new Aula[HORARIOS][DIAS]; // matriz vazia
    } // fim do construtor sem parâmetros

    public AgendaSemanal(LocalDate novoInicio, Sala novaSala) { // construtor com parâmetros
        if (novoInicio == null || novaSala == null) { // precisa da semana e da sala
            throw new IllegalArgumentException("Informe a semana e a sala da agenda."); // erro de argumento
        } // fim do if
        this.inicioSemana = novoInicio.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)); // volta para a segunda-feira
        this.sala = novaSala; // guarda a sala
        this.grade = new Aula[HORARIOS][DIAS]; // matriz vazia: 18 horários x 6 dias
    } // fim do construtor com parâmetros

    public LocalDate getInicioSemana() { // getter da segunda-feira da semana
        return inicioSemana; // devolve a data
    } // fim de getInicioSemana

    public Sala getSala() { // getter da sala
        return sala; // devolve a sala
    } // fim de getSala

    public Aula getAula(int dia, int horario) { // aula de uma célula da matriz
        if (dia < 0 || dia >= DIAS || horario < 0 || horario >= HORARIOS) { // fora da matriz?
            throw new IllegalArgumentException("Dia ou horário fora da agenda."); // erro de argumento
        } // fim do if
        return grade[horario][dia]; // devolve a aula ou null
    } // fim de getAula

    public boolean estaLivre(int dia, int horario) { // o horário está vago?
        return getAula(dia, horario) == null; // livre quando a célula está vazia
    } // fim de estaLivre

    public void adicionarAula(Aula aula) { // coloca a aula na célula do seu dia e horário
        if (aula == null) { // não aceita "nenhuma aula"
            throw new IllegalArgumentException("Informe a aula."); // erro de argumento
        } // fim do if
        if (aula.getSala().getCodigo() != sala.getCodigo()) { // a aula é de outra sala?
            throw new IllegalArgumentException("A aula não é desta sala."); // erro de argumento
        } // fim do if
        if (!contem(aula.getDataHora())) { // a aula é de outra semana?
            throw new IllegalArgumentException("A aula não é desta semana."); // erro de argumento
        } // fim do if
        int dia = calcularDia(aula.getDataHora()); // coluna da matriz (já recusa domingo)
        int horario = calcularHorario(aula.getDataHora()); // linha da matriz (já recusa horário fora)
        if (!estaLivre(dia, horario)) { // a célula já tem aula?
            throw new IllegalArgumentException("Já existe aula nesta sala neste horário."); // horário ocupado
        } // fim do if
        grade[horario][dia] = aula; // guarda a aula na célula
    } // fim de adicionarAula

    public boolean removerAula(Aula aula) { // tira a aula da matriz
        for (int h = 0; h < HORARIOS; h++) { // percorre as linhas (horários)
            for (int d = 0; d < DIAS; d++) { // percorre as colunas (dias)
                if (grade[h][d] != null && grade[h][d].getId() == aula.getId()) { // achou a aula?
                    grade[h][d] = null; // libera o horário
                    return true; // removida
                } // fim do if
            } // fim do for dos dias
        } // fim do for dos horários
        return false; // a aula não estava nesta agenda
    } // fim de removerAula

    public List<Aula> listarAulas() { // todas as aulas da semana
        List<Aula> aulas = new ArrayList<>(); // resultado começa vazio
        for (int d = 0; d < DIAS; d++) { // percorre os dias (segunda a sábado)
            for (int h = 0; h < HORARIOS; h++) { // percorre os horários do dia
                if (grade[h][d] != null) { // há aula nesta célula?
                    aulas.add(grade[h][d]); // entra na lista, em ordem de dia e horário
                } // fim do if
            } // fim do for dos horários
        } // fim do for dos dias
        return aulas; // devolve as aulas
    } // fim de listarAulas

    public boolean contem(LocalDateTime dataHora) { // a data cai nesta semana?
        LocalDate data = dataHora.toLocalDate(); // só o dia, sem a hora
        return !data.isBefore(inicioSemana) && data.isBefore(inicioSemana.plusDays(7)); // de segunda até domingo
    } // fim de contem

    private int calcularDia(LocalDateTime dataHora) { // coluna: segunda = 0 ... sábado = 5
        DayOfWeek diaSemana = dataHora.getDayOfWeek(); // dia da semana da data
        if (diaSemana == DayOfWeek.SUNDAY) { // domingo?
            throw new IllegalArgumentException("A academia não abre aos domingos."); // regra de funcionamento
        } // fim do if
        return diaSemana.getValue() - 1; // getValue: segunda = 1 ... sábado = 6
    } // fim de calcularDia

    private int calcularHorario(LocalDateTime dataHora) { // linha: 5h = 0 ... 22h = 17
        if (dataHora.getMinute() != 0) { // aulas começam em hora cheia
            throw new IllegalArgumentException("As aulas começam em hora cheia (ex.: 06:00)."); // erro de argumento
        } // fim do if
        int horario = dataHora.getHour() - PRIMEIRA_HORA; // 6h vira linha 1, por exemplo
        if (horario < 0 || horario >= HORARIOS) { // antes das 5h ou depois das 22h?
            throw new IllegalArgumentException("Fora do horário de funcionamento (aulas das 5h às 22h)."); // regra
        } // fim do if
        return horario; // linha da matriz
    } // fim de calcularHorario

    @Override // sobrescreve o toString herdado de Object
    public String toString() { // texto que representa a agenda
        return "Agenda da sala " + sala.getCodigo() + " (" + sala.getModalidade() + ") | semana de "
                + inicioSemana + " | " + listarAulas().size() + " aula(s)"; // sala, semana e quantidade
    } // fim de toString
} // fim da classe AgendaSemanal