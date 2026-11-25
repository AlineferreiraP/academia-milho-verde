/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model; // pacote do Model

import java.time.DayOfWeek; // dias da semana (para saber o que é dia útil)
import java.time.LocalDate; // datas sem hora
import java.time.LocalDateTime; // data e hora juntas
import java.time.temporal.ChronoUnit; // conta dias entre duas datas
import java.util.ArrayList; // lista que cresce
import java.util.List; // tipo genérico de lista

/**
 * A reserva de um cliente em uma ou mais aulas (sempre da mesma sala).
 * Nasce PRELIMINAR; o cliente tem até 5 dias úteis antes da primeira aula para confirmar,
 * senão o agendamento é cancelado e a vaga é liberada. Ao cancelar um agendamento confirmado
 * com 3 dias ou mais de antecedência, metade do valor volta ao cliente.
 * O número do cartão nunca é guardado por inteiro: só os 4 últimos dígitos.
 *
 * @author Aline Ferreira
 */
public class Agendamento implements Identificavel { // a reserva de um cliente em uma ou mais aulas

    /** Tipo de cobrança: paga por dia. */
    public static final String DIARIA = "DIARIA"; // tipo de cobrança: paga por dia
    /** Tipo de cobrança: paga por mês. */
    public static final String MENSALIDADE = "MENSALIDADE"; // tipo de cobrança: paga por mês
    /** Situação: reservado, ainda não pago. */
    public static final String PRELIMINAR = "PRELIMINAR"; // situação: reservado, ainda não pago
    /** Situação: confirmado e debitado no cartão. */
    public static final String CONFIRMADO = "CONFIRMADO"; // situação: confirmado e debitado no cartão
    /** Situação: cancelado, vaga liberada. */
    public static final String CANCELADO = "CANCELADO"; // situação: cancelado, vaga liberada
    /** Prazo para confirmar: até 5 dias úteis antes da primeira aula. */
    private static final int DIAS_UTEIS_CONFIRMACAO = 5; // confirmar até 5 dias úteis antes da primeira aula
    /** Desistir com 3 dias ou mais de antecedência devolve metade do valor. */
    private static final int DIAS_PARA_DEVOLUCAO = 3; // desistir com 3 dias ou mais devolve metade

    /** Identificador do agendamento; não muda. */
    private final int id; // identificador do agendamento; final: não muda
    /** Quem reservou (associação com {@link Cliente}). */
    private final Cliente cliente; // quem reservou (associação com Cliente)
    /** Aulas reservadas (associação com {@link Aula}). */
    private final List<Aula> aulas; // aulas reservadas (associação com Aula)
    /** DIARIA ou MENSALIDADE. */
    private final String tipoCobranca; // DIARIA ou MENSALIDADE
    /** Valor a debitar na confirmação. */
    private final double preco; // valor a debitar na confirmação
    /** Só os 4 últimos dígitos, no formato "**** 1234". */
    private final String cartaoMascarado; // só os 4 últimos dígitos, no formato "**** 1234"
    /** PRELIMINAR, CONFIRMADO ou CANCELADO; muda só pelos métodos da classe. */
    private String status; // PRELIMINAR, CONFIRMADO ou CANCELADO; muda só pelos métodos abaixo

    /** Cria um agendamento vazio, já PRELIMINAR. */
    public Agendamento() { // construtor sem parâmetros
        this.id = 0; // ainda sem ID
        this.cliente = null; // ainda sem cliente
        this.aulas = new ArrayList<>(); // lista vazia
        this.tipoCobranca = ""; // ainda sem tipo
        this.preco = 0; // ainda sem preço
        this.cartaoMascarado = ""; // ainda sem cartão
        this.status = PRELIMINAR; // todo agendamento nasce preliminar
    } // fim do construtor sem parâmetros

    /**
     * Cria um agendamento no atendimento: recebe o cartão inteiro, guarda só o final e nasce PRELIMINAR.
     *
     * @param novoId ID do agendamento (maior que zero)
     * @param novoCliente quem reservou (não pode ser nulo)
     * @param novasAulas aulas reservadas (pelo menos uma)
     * @param novoTipo DIARIA ou MENSALIDADE
     * @param novoPreco valor a debitar (maior que zero)
     * @param novoCartao número do cartão (13 a 19 dígitos)
     * @throws IllegalArgumentException se algum dado for inválido
     */
    public Agendamento(int novoId, Cliente novoCliente, List<Aula> novasAulas, String novoTipo,
            double novoPreco, String novoCartao) { // construtor usado no atendimento: recebe o cartão inteiro
        this(novoId, novoCliente, novasAulas, novoTipo, novoPreco, mascararCartao(novoCartao), PRELIMINAR); // guarda só o mascarado e nasce preliminar
    } // fim do construtor do atendimento

    /**
     * Cria um agendamento com cartão já mascarado e situação informada (visível no pacote; usado ao carregar do JSON).
     *
     * @param novoId ID do agendamento (maior que zero)
     * @param novoCliente quem reservou (não pode ser nulo)
     * @param novasAulas aulas reservadas (pelo menos uma)
     * @param novoTipo DIARIA ou MENSALIDADE
     * @param novoPreco valor a debitar (maior que zero)
     * @param novoCartaoMascarado cartão no formato "**** 1234"
     * @param novoStatus PRELIMINAR, CONFIRMADO ou CANCELADO
     * @throws IllegalArgumentException se algum dado for inválido
     */
    Agendamento(int novoId, Cliente novoCliente, List<Aula> novasAulas, String novoTipo,
            double novoPreco, String novoCartaoMascarado, String novoStatus) { // acesso de pacote: usado também ao carregar do JSON
        if (novoId <= 0) { // o ID precisa ser positivo
            throw new IllegalArgumentException("O ID do agendamento deve ser maior que zero."); // erro de argumento
        } // fim do if
        if (novoCliente == null) { // todo agendamento é de um cliente
            throw new IllegalArgumentException("Informe o cliente do agendamento."); // erro de argumento
        } // fim do if
        if (novoPreco <= 0) { // o preço precisa ser positivo
            throw new IllegalArgumentException("O preço deve ser maior que zero."); // erro de argumento
        } // fim do if
        this.id = novoId; // guarda o ID
        this.cliente = novoCliente; // guarda o cliente
        this.aulas = exigirAulas(novasAulas); // guarda uma cópia da lista de aulas
        this.tipoCobranca = exigirTipoCobranca(novoTipo); // só DIARIA ou MENSALIDADE
        this.preco = novoPreco; // guarda o preço
        this.cartaoMascarado = exigirCartaoMascarado(novoCartaoMascarado); // confere o formato "**** 1234"
        this.status = exigirStatus(novoStatus); // PRELIMINAR no atendimento; a situação salva ao carregar
    } // fim do construtor de pacote

    /**
     * Devolve: ID do agendamento.
     *
     * @return ID do agendamento
     */
    @Override // implementa o método da interface Identificavel
    public int getId() { // getter do ID
        return id; // devolve o ID
    } // fim de getId

    /**
     * Devolve: quem reservou.
     *
     * @return quem reservou
     */
    public Cliente getCliente() { // getter do cliente
        return cliente; // devolve o cliente
    } // fim de getCliente

    /**
     * Devolve uma cópia da lista de aulas; quem recebe não altera a original.
     *
     * @return cópia da lista de aulas
     */
    public List<Aula> getAulas() { // getter das aulas
        return new ArrayList<>(aulas); // devolve uma cópia: quem recebe não altera a lista original
    } // fim de getAulas

    /**
     * Devolve: DIARIA ou MENSALIDADE.
     *
     * @return DIARIA ou MENSALIDADE
     */
    public String getTipoCobranca() { // getter do tipo de cobrança
        return tipoCobranca; // devolve DIARIA ou MENSALIDADE
    } // fim de getTipoCobranca

    /**
     * Devolve: valor do agendamento.
     *
     * @return valor do agendamento
     */
    public double getPreco() { // getter do preço
        return preco; // devolve o preço
    } // fim de getPreco

    /**
     * Devolve: cartão no formato "**** 1234" (para mostrar e para salvar).
     *
     * @return cartão no formato "**** 1234" (para mostrar e para salvar)
     */
    public String getCartaoMascarado() { // cartão para mostrar na tela e para salvar
        return cartaoMascarado; // só os 4 últimos dígitos aparecem
    } // fim de getCartaoMascarado

    /**
     * Devolve: PRELIMINAR, CONFIRMADO ou CANCELADO.
     *
     * @return PRELIMINAR, CONFIRMADO ou CANCELADO
     */
    public String getStatus() { // getter da situação
        return status; // devolve PRELIMINAR, CONFIRMADO ou CANCELADO
    } // fim de getStatus

    /**
     * Procura a aula mais cedo do agendamento.
     *
     * @return dia e hora da primeira aula
     */
    public LocalDateTime getPrimeiraDataHora() { // dia e hora da aula mais cedo do agendamento
        LocalDateTime primeira = aulas.get(0).getDataHora(); // começa pela primeira da lista
        IteradorLista<Aula> iterador = new IteradorLista<>(aulas); // nosso iterador percorre as aulas
        while (iterador.hasNext()) { // enquanto houver aula
            Aula aula = iterador.next(); // pega a próxima
            if (aula.getDataHora().isBefore(primeira)) { // se for antes da que temos
                primeira = aula.getDataHora(); // passa a ser a primeira
            } // fim do if
        } // fim do while
        return primeira; // devolve o dia e a hora mais cedo
    } // fim de getPrimeiraDataHora

    /**
     * Devolve só o dia da primeira aula (usado nos prazos).
     *
     * @return dia da primeira aula
     */
    public LocalDate getPrimeiraData() { // só o dia da primeira aula (usado nos prazos)
        return getPrimeiraDataHora().toLocalDate(); // tira a hora
    } // fim de getPrimeiraData

    /**
     * Devolve a sala do agendamento; todas as aulas são na mesma, então a primeira basta.
     *
     * @return sala do agendamento
     */
    public Sala getSala() { // sala do agendamento
        return aulas.get(0).getSala(); // todas as aulas são na mesma sala, então a primeira basta
    } // fim de getSala

    /**
     * Calcula o último dia para o cliente confirmar: 5 dias úteis antes da primeira aula.
     *
     * @return data limite da confirmação
     */
    public LocalDate calcularPrazoConfirmacao() { // último dia para o cliente confirmar
        return subtrairDiasUteis(getPrimeiraData(), DIAS_UTEIS_CONFIRMACAO); // 5 dias úteis antes da primeira aula
    } // fim de calcularPrazoConfirmacao

    /**
     * Confirma o agendamento (o cliente confirmou por telefone). Se o prazo já passou, cancela e libera a vaga.
     *
     * @param hoje data de hoje
     * @return {@code true} se confirmou; {@code false} se o prazo venceu e foi cancelado
     * @throws IllegalArgumentException se a data for nula
     * @throws IllegalStateException se o agendamento não estiver preliminar
     */
    public boolean confirmar(LocalDate hoje) { // cliente confirmou por telefone
        if (hoje == null) { // precisa saber o dia de hoje
            throw new IllegalArgumentException("Informe a data de hoje."); // erro de argumento
        } // fim do if
        if (!PRELIMINAR.equals(status)) { // só se confirma o que ainda é preliminar
            throw new IllegalStateException("Só agendamento preliminar pode ser confirmado."); // erro de estado
        } // fim do if
        if (hoje.isAfter(calcularPrazoConfirmacao())) { // passou do prazo
            status = CANCELADO; // cancela e libera a vaga
            return false; // avisa que não confirmou
        } // fim do if
        status = CONFIRMADO; // dentro do prazo: confirma
        return true; // avisa que confirmou (quem chamou registra o pagamento)
    } // fim de confirmar

    /**
     * Cancela o agendamento (o cliente desistiu) e informa quanto deve voltar a ele.
     * Confirmado com 3 dias ou mais de antecedência devolve metade; nos outros casos, nada.
     *
     * @param hoje data de hoje
     * @return valor a devolver ao cliente
     * @throws IllegalArgumentException se a data for nula
     * @throws IllegalStateException se já estiver cancelado
     */
    public double cancelar(LocalDate hoje) { // cliente desistiu; devolve quanto deve voltar a ele
        if (hoje == null) { // precisa saber o dia de hoje
            throw new IllegalArgumentException("Informe a data de hoje."); // erro de argumento
        } // fim do if
        if (CANCELADO.equals(status)) { // não se cancela duas vezes
            throw new IllegalStateException("Este agendamento já está cancelado."); // erro de estado
        } // fim do if
        double aDevolver = 0; // começa sem devolução
        long diasQueFaltam = ChronoUnit.DAYS.between(hoje, getPrimeiraData()); // dias até a primeira aula
        if (CONFIRMADO.equals(status) && diasQueFaltam >= DIAS_PARA_DEVOLUCAO) { // já pagou e avisou a tempo
            aDevolver = preco / 2; // devolve metade; a academia fica com a outra metade
        } // fim do if
        status = CANCELADO; // agora está cancelado
        return aDevolver; // preliminar ou em cima da hora devolve 0; quem chamou registra no pagamento
    } // fim de cancelar

    /**
     * Cancela o agendamento preliminar que perdeu o prazo de confirmação.
     *
     * @param hoje data de hoje
     * @return {@code true} se cancelou; {@code false} se nada mudou
     */
    public boolean cancelarSeVencido(LocalDate hoje) { // cancela o preliminar que perdeu o prazo
        if (PRELIMINAR.equals(status) && hoje.isAfter(calcularPrazoConfirmacao())) { // preliminar e prazo vencido
            status = CANCELADO; // cancela e libera a vaga
            return true; // avisa que cancelou
        } // fim do if
        return false; // nada mudou
    } // fim de cancelarSeVencido

    /**
     * Confere a lista de aulas e guarda uma cópia.
     *
     * @param novasAulas lista recebida
     * @return cópia da lista
     * @throws IllegalArgumentException se a lista for vazia ou tiver posição nula
     */
    private static List<Aula> exigirAulas(List<Aula> novasAulas) { // confere a lista de aulas
        if (novasAulas == null || novasAulas.isEmpty()) { // precisa de pelo menos uma aula
            throw new IllegalArgumentException("Escolha pelo menos uma aula."); // erro de argumento
        } // fim do if
        IteradorLista<Aula> iterador = new IteradorLista<>(novasAulas); // nosso iterador percorre a lista recebida
        while (iterador.hasNext()) { // enquanto houver posição
            if (iterador.next() == null) { // nenhuma posição pode estar vazia
                throw new IllegalArgumentException("Há uma aula inválida na lista."); // erro de argumento
            } // fim do if
        } // fim do while
        return new ArrayList<>(novasAulas); // guarda uma cópia: mudanças lá fora não afetam o agendamento
    } // fim de exigirAulas

    /**
     * Volta alguns dias úteis (segunda a sexta) no calendário.
     *
     * @param data data de partida
     * @param dias quantos dias úteis voltar
     * @return data encontrada
     */
    private static LocalDate subtrairDiasUteis(LocalDate data, int dias) { // volta N dias úteis no calendário
        LocalDate resultado = data; // começa na data informada
        int contados = 0; // dias úteis já contados
        while (contados < dias) { // até contar todos
            resultado = resultado.minusDays(1); // volta um dia
            DayOfWeek dia = resultado.getDayOfWeek(); // que dia da semana é
            if (dia != DayOfWeek.SATURDAY && dia != DayOfWeek.SUNDAY) { // segunda a sexta é dia útil
                contados++; // conta mais um dia útil
            } // fim do if
        } // fim do while
        return resultado; // devolve a data do prazo
    } // fim de subtrairDiasUteis

    /**
     * Confere o tipo de cobrança.
     *
     * @param valor texto recebido
     * @return DIARIA ou MENSALIDADE, em maiúsculas
     * @throws IllegalArgumentException se não for um dos dois
     */
    private static String exigirTipoCobranca(String valor) { // confere o tipo de cobrança
        String tipo = Validacao.exigirTexto(valor, "tipo de cobrança").toUpperCase(); // obrigatório, em maiúsculas
        if (!DIARIA.equals(tipo) && !MENSALIDADE.equals(tipo)) { // só os dois tipos do enunciado
            throw new IllegalArgumentException("O tipo de cobrança deve ser DIARIA ou MENSALIDADE."); // erro de argumento
        } // fim do if
        return tipo; // devolve o tipo conferido
    } // fim de exigirTipoCobranca

    /**
     * Confere o cartão inteiro e guarda só o final (o número completo não fica guardado).
     *
     * @param valor número do cartão
     * @return texto no formato "**** 1234"
     * @throws IllegalArgumentException se não tiver de 13 a 19 dígitos
     */
    private static String mascararCartao(String valor) { // confere o cartão inteiro e guarda só o final
        String digitos = Validacao.exigirTexto(valor, "número do cartão").replaceAll("\\D", ""); // tira espaços e traços
        if (digitos.length() < 13 || digitos.length() > 19) { // cartões têm de 13 a 19 dígitos
            throw new IllegalArgumentException("Número de cartão inválido."); // erro de argumento
        } // fim do if
        return "**** " + digitos.substring(digitos.length() - 4); // o número inteiro não fica guardado
    } // fim de mascararCartao

    /**
     * Confere o formato do cartão mascarado.
     *
     * @param valor texto recebido
     * @return o próprio texto, se estiver no formato "**** 1234"
     * @throws IllegalArgumentException se o formato for inválido
     */
    private static String exigirCartaoMascarado(String valor) { // confere o formato do cartão mascarado
        if (valor == null || !valor.matches("\\*{4} \\d{4}")) { // tem que ser "**** " e 4 dígitos
            throw new IllegalArgumentException("Cartão mascarado inválido."); // erro de argumento
        } // fim do if
        return valor; // devolve o cartão mascarado conferido
    } // fim de exigirCartaoMascarado

    /**
     * Confere a situação do agendamento.
     *
     * @param valor texto recebido
     * @return o próprio texto, se for uma das três situações
     * @throws IllegalArgumentException se for inválido
     */
    private static String exigirStatus(String valor) { // confere a situação
        if (!PRELIMINAR.equals(valor) && !CONFIRMADO.equals(valor) && !CANCELADO.equals(valor)) { // só as três situações
            throw new IllegalArgumentException("Situação de agendamento inválida."); // erro de argumento
        } // fim do if
        return valor; // devolve a situação conferida
    } // fim de exigirStatus

    /**
     * Resume o agendamento em uma linha.
     *
     * @return ID, cliente, aulas, cobrança, preço, cartão e situação
     */
    @Override // sobrescreve o toString herdado de Object
    public String toString() { // texto que representa o agendamento
        return "Agendamento " + id + " | " + cliente.getNome() + " | " + aulas.size() + " aula(s) | "
                + tipoCobranca + " | R$ " + String.format("%.2f", preco) + " | cartão " + cartaoMascarado
                + " | " + status; // ID, cliente, aulas, cobrança, preço, cartão e situação
    } // fim de toString
} // fim da classe Agendamento