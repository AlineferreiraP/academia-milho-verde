/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Gerencia as aulas, as agendas semanais e os agendamentos dos clientes.
 *
 * <p>Cada sala tem uma {@link AgendaSemanal} por semana, criada quando
 * a primeira aula daquela semana é cadastrada. Ao confirmar um
 * agendamento, pede à {@link GestaoVendas} a venda da diária ou da
 * mensalidade, com o extrato.</p>
 *
 * @author Aline Ferreira
 */
public class GestaoAgendamentos {

    /** Agendas semanais de todas as salas. */
    private List<AgendaSemanal> agendas;

    /** Todos os agendamentos, de qualquer situação. */
    private List<Agendamento> agendamentos;

    /** Gestão de vendas, que cobra os agendamentos confirmados. */
    private GestaoVendas gestaoVendas;

    /** Identificador do próximo agendamento. */
    private int proximoId;

    /** Identificador da próxima aula. */
    private int proximoIdAula;

    /**
     * Construtor padrão: cria uma gestão com a sua própria
     * {@link GestaoVendas}.
     */
    public GestaoAgendamentos() {
        this(new GestaoVendas());
    }

    /**
     * Construtor com parâmetros.
     *
     * @param novaGestaoVendas gestão de vendas compartilhada com a
     *        lojinha
     * @throws IllegalArgumentException se for nula
     */
    public GestaoAgendamentos(GestaoVendas novaGestaoVendas) {
        if (novaGestaoVendas == null) {
            throw new IllegalArgumentException(
                    "A gestão de vendas é obrigatória.");
        }
        this.agendas = new ArrayList<>();
        this.agendamentos = new ArrayList<>();
        this.gestaoVendas = novaGestaoVendas;
        this.proximoId = 1;
        this.proximoIdAula = 1;
    }

    /**
     * Cadastra uma aula na agenda da sala, na semana da data.
     *
     * @param codigoSala código da sala (1 a 4)
     * @param dataHora data e hora de início, em hora cheia
     * @param nomeInstrutor nome do instrutor
     * @return a aula cadastrada
     * @throws IllegalArgumentException se a sala não existir ou a aula
     *         for inválida (domingo, fora do horário...)
     * @throws IllegalStateException se o horário da sala já estiver ocupado
     */
    public Aula cadastrarAula(int codigoSala, LocalDateTime dataHora,
                              String nomeInstrutor) {
        Sala sala = buscarSala(codigoSala);
        if (sala == null) {
            throw new IllegalArgumentException(
                    "Sala " + codigoSala + " não encontrada.");
        }
        Aula aula = new Aula(proximoIdAula, sala, dataHora, nomeInstrutor);
        buscarOuCriarAgenda(sala, dataHora).adicionarAula(aula);
        proximoIdAula++;
        return aula;
    }

    /**
     * Remove uma aula da agenda.
     *
     * @param id identificador da aula
     * @throws IllegalArgumentException se a aula não existir
     * @throws IllegalStateException se houver agendamento preliminar ou
     *         confirmado para a aula
     */
    public void removerAula(int id) {
        Aula aula = buscarAula(id);
        if (aula == null) {
            throw new IllegalArgumentException("Aula " + id + " não encontrada.");
        }
        if (contarReservas(aula) > 0) {
            throw new IllegalStateException(
                    "Há agendamentos ativos para esta aula; cancele-os antes.");
        }
        for (AgendaSemanal agenda : agendas) {
            if (agenda.removerAula(aula)) {
                return;
            }
        }
    }

    /**
     * Procura uma aula pelo identificador.
     *
     * @param id identificador procurado
     * @return a aula, ou {@code null} se não existir
     */
    public Aula buscarAula(int id) {
        for (AgendaSemanal agenda : agendas) {
            for (Aula aula : agenda.listarAulas()) {
                if (aula.getId() == id) {
                    return aula;
                }
            }
        }
        return null;
    }

    /**
     * Lista todas as aulas, da mais próxima para a mais distante.
     *
     * @return lista nova de aulas
     */
    public List<Aula> listarAulas() {
        List<Aula> aulas = new ArrayList<>();
        for (AgendaSemanal agenda : agendas) {
            aulas.addAll(agenda.listarAulas());
        }
        // Ordena pela data: classe anônima que implementa Comparator.
        aulas.sort(new Comparator<Aula>() {
            @Override
            public int compare(Aula a, Aula b) {
                return a.getDataHora().compareTo(b.getDataHora());
            }
        });
        return aulas;
    }

    /**
     * Devolve a agenda de uma sala em uma semana.
     *
     * <p>Antes, cancela os agendamentos preliminares com prazo vencido.
     * Se a semana ainda não tem aulas, devolve uma agenda vazia.</p>
     *
     * @param codigoSala código da sala
     * @param inicioSemana qualquer dia da semana desejada
     * @return a agenda da semana
     * @throws IllegalArgumentException se a sala não existir
     */
    public AgendaSemanal consultarAgendaSemanal(int codigoSala,
                                                LocalDate inicioSemana) {
        Sala sala = buscarSala(codigoSala);
        if (sala == null) {
            throw new IllegalArgumentException(
                    "Sala " + codigoSala + " não encontrada.");
        }
        cancelarVencidos(LocalDate.now());
        LocalDate segunda = inicioSemana.with(
                TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        for (AgendaSemanal agenda : agendas) {
            if (agenda.getSala().getCodigo() == codigoSala
                    && agenda.getInicioSemana().equals(segunda)) {
                return agenda;
            }
        }
        return new AgendaSemanal(segunda, sala);
    }

    /**
     * Informa quantas vagas livres uma aula ainda tem.
     *
     * @param idAula identificador da aula
     * @return capacidade da sala menos as reservas ativas
     * @throws IllegalArgumentException se a aula não existir
     */
    public int verificarVagas(int idAula) {
        Aula aula = buscarAula(idAula);
        if (aula == null) {
            throw new IllegalArgumentException(
                    "Aula " + idAula + " não encontrada.");
        }
        int vagas = aula.getSala().getCapacidade() - contarReservas(aula);
        return Math.max(vagas, 0);
    }

    /**
     * Cria um agendamento preliminar para o cliente.
     *
     * @param cliente cliente ativo
     * @param idsAulas identificadores das aulas, sem repetição
     * @param tipo {@link Agendamento#DIARIA} ou {@link Agendamento#MENSALIDADE}
     * @param preco preço total
     * @param numeroCartao número do cartão
     * @return o agendamento criado
     * @throws IllegalArgumentException se os dados forem inválidos ou
     *         alguma aula não existir
     * @throws IllegalStateException se alguma aula estiver sem vagas ou
     *         o cliente já a tiver reservado
     */
    public Agendamento agendar(Cliente cliente, List<Integer> idsAulas,
                               String tipo, double preco, String numeroCartao) {
        if (cliente == null || !cliente.isAtivo()) {
            throw new IllegalArgumentException(
                    "O agendamento precisa de um cliente ativo.");
        }
        if (idsAulas == null || idsAulas.isEmpty()) {
            throw new IllegalArgumentException("Escolha pelo menos uma aula.");
        }
        List<Aula> aulas = new ArrayList<>();
        for (int i = 0; i < idsAulas.size(); i++) {
            int id = idsAulas.get(i);
            for (int j = 0; j < i; j++) {
                if (idsAulas.get(j) == id) {
                    throw new IllegalArgumentException(
                            "A aula " + id + " foi informada duas vezes.");
                }
            }
            Aula aula = buscarAula(id);
            if (aula == null) {
                throw new IllegalArgumentException(
                        "Aula " + id + " não encontrada.");
            }
            if (verificarVagas(id) <= 0) {
                throw new IllegalStateException("A aula " + id + " está lotada.");
            }
            if (clienteJaReservou(cliente, aula)) {
                throw new IllegalStateException(
                        "O cliente já reservou a aula " + id + ".");
            }
            aulas.add(aula);
        }
        Agendamento agendamento = new Agendamento(proximoId, cliente, aulas,
                tipo, preco, numeroCartao);
        proximoId++;
        agendamentos.add(agendamento);
        return agendamento;
    }

    /**
     * Confirma um agendamento e cobra o valor (simulado).
     *
     * <p>Se o prazo já passou, o agendamento é cancelado e o erro é
     * informado.</p>
     *
     * @param id identificador do agendamento
     * @param hoje data da confirmação
     * @return a venda da diária ou mensalidade, com o extrato
     * @throws IllegalArgumentException se o agendamento não existir
     * @throws IllegalStateException se não estiver preliminar ou se o
     *         prazo tiver passado
     */
    public Venda confirmarAgendamento(int id, LocalDate hoje) {
        Agendamento agendamento = exigirAgendamento(id);
        if (!agendamento.confirmar(hoje)) {
            agendamento.cancelarSeVencido(hoje);
            throw new IllegalStateException(
                    "O prazo para confirmar terminou em "
                    + agendamento.calcularPrazoConfirmacao()
                    + "; o agendamento foi cancelado.");
        }
        return gestaoVendas.registrarVendaAgendamento(agendamento,
                LocalDateTime.now());
    }

    /**
     * Cancela um agendamento.
     *
     * @param id identificador do agendamento
     * @param hoje data do cancelamento
     * @return o valor devolvido ao cliente
     * @throws IllegalArgumentException se o agendamento não existir
     * @throws IllegalStateException se já estiver cancelado
     */
    public double cancelarAgendamento(int id, LocalDate hoje) {
        return exigirAgendamento(id).cancelar(hoje);
    }

    /**
     * Cancela os agendamentos preliminares com prazo vencido.
     *
     * @param hoje data de hoje
     * @return quantos foram cancelados
     */
    public int cancelarVencidos(LocalDate hoje) {
        int cancelados = 0;
        for (Agendamento agendamento : agendamentos) {
            if (agendamento.cancelarSeVencido(hoje)) {
                cancelados++;
            }
        }
        return cancelados;
    }

    /**
     * Lista os agendamentos ordenados pelo critério.
     *
     * @param criterio {@link ComparadorAgendamento#DATA} ou
     *        {@link ComparadorAgendamento#VALOR}
     * @return lista nova, ordenada
     * @throws IllegalArgumentException se o critério for inválido
     */
    public List<Agendamento> listarAgendamentos(String criterio) {
        ComparadorAgendamento comparador = new ComparadorAgendamento(criterio);
        List<Agendamento> lista = new ArrayList<>(agendamentos);
        lista.sort(comparador);
        return lista;
    }

    /**
     * Retorna as agendas semanais.
     *
     * <p>Acesso de pacote: usado pelo {@link RepositorioJson}.</p>
     *
     * @return cópia da lista de agendas
     */
    List<AgendaSemanal> getAgendas() {
        return new ArrayList<>(agendas);
    }

    /**
     * Retorna todos os agendamentos.
     *
     * <p>Acesso de pacote: usado pelo {@link RepositorioJson}.</p>
     *
     * @return cópia da lista de agendamentos
     */
    List<Agendamento> getAgendamentos() {
        return new ArrayList<>(agendamentos);
    }

    /**
     * Substitui aulas e agendamentos pelos lidos do arquivo, refaz as
     * agendas semanais e ajusta os contadores de IDs.
     *
     * @param aulasLidas aulas que estão nas agendas
     * @param agendamentosLidos todos os agendamentos
     */
    void carregar(List<Aula> aulasLidas, List<Agendamento> agendamentosLidos) {
        agendas = new ArrayList<>();
        proximoIdAula = 1;
        for (Aula aula : aulasLidas) {
            buscarOuCriarAgenda(aula.getSala(), aula.getDataHora())
                    .adicionarAula(aula);
            if (aula.getId() >= proximoIdAula) {
                proximoIdAula = aula.getId() + 1;
            }
        }
        agendamentos = new ArrayList<>(agendamentosLidos);
        proximoId = 1;
        for (Agendamento agendamento : agendamentos) {
            if (agendamento.getId() >= proximoId) {
                proximoId = agendamento.getId() + 1;
            }
            // Aulas removidas da agenda ainda ocupam números de ID.
            for (Aula aula : agendamento.getAulas()) {
                if (aula.getId() >= proximoIdAula) {
                    proximoIdAula = aula.getId() + 1;
                }
            }
        }
    }

    /**
     * Procura uma sala pelo código entre as quatro salas fixas.
     *
     * @param codigo código procurado
     * @return a sala, ou {@code null} se não existir
     */
    private Sala buscarSala(int codigo) {
        for (Sala sala : Sistema.getSalas()) {
            if (sala.getCodigo() == codigo) {
                return sala;
            }
        }
        return null;
    }

    /**
     * Acha a agenda da sala na semana da data; cria se ainda não existir.
     *
     * @param sala sala da agenda
     * @param dataHora data e hora de uma aula da semana
     * @return a agenda semanal
     */
    private AgendaSemanal buscarOuCriarAgenda(Sala sala, LocalDateTime dataHora) {
        LocalDate segunda = dataHora.toLocalDate().with(
                TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        for (AgendaSemanal agenda : agendas) {
            if (agenda.getSala().getCodigo() == sala.getCodigo()
                    && agenda.getInicioSemana().equals(segunda)) {
                return agenda;
            }
        }
        AgendaSemanal nova = new AgendaSemanal(segunda, sala);
        agendas.add(nova);
        return nova;
    }

    /**
     * Procura um agendamento e falha se não existir.
     *
     * @param id identificador procurado
     * @return o agendamento
     * @throws IllegalArgumentException se o ID não existir
     */
    private Agendamento exigirAgendamento(int id) {
        for (Agendamento agendamento : agendamentos) {
            if (agendamento.getId() == id) {
                return agendamento;
            }
        }
        throw new IllegalArgumentException(
                "Agendamento " + id + " não encontrado.");
    }

    /**
     * Conta os agendamentos preliminares e confirmados de uma aula.
     *
     * @param aula aula a conferir
     * @return o número de reservas ativas
     */
    private int contarReservas(Aula aula) {
        int total = 0;
        for (Agendamento agendamento : agendamentos) {
            if (!Agendamento.CANCELADO.equals(agendamento.getStatus())
                    && reserva(agendamento, aula)) {
                total++;
            }
        }
        return total;
    }

    /**
     * Informa se o cliente já tem reserva ativa na aula.
     *
     * @param cliente cliente a conferir
     * @param aula aula a conferir
     * @return {@code true} se já reservou
     */
    private boolean clienteJaReservou(Cliente cliente, Aula aula) {
        for (Agendamento agendamento : agendamentos) {
            if (!Agendamento.CANCELADO.equals(agendamento.getStatus())
                    && agendamento.getCliente().getId() == cliente.getId()
                    && reserva(agendamento, aula)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Informa se o agendamento inclui a aula.
     *
     * @param agendamento agendamento a conferir
     * @param aula aula procurada
     * @return {@code true} se a aula está entre as reservadas
     */
    private boolean reserva(Agendamento agendamento, Aula aula) {
        for (Aula reservada : agendamento.getAulas()) {
            if (reservada.getId() == aula.getId()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retorna um resumo da gestão.
     *
     * @return texto com o número de agendas e de agendamentos
     */
    @Override
    public String toString() {
        return "GestaoAgendamentos[" + agendas.size() + " agenda(s), "
                + agendamentos.size() + " agendamento(s)]";
    }
}