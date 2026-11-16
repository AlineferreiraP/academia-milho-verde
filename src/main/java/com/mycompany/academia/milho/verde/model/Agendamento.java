/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model; // pacote do Model

import java.time.DayOfWeek; // dias da semana (para saber o que é dia útil)
import java.time.LocalDate; // datas sem hora
import java.time.temporal.ChronoUnit; // conta dias entre duas datas
import java.util.ArrayList; // lista que cresce
import java.util.List; // tipo genérico de lista

public class Agendamento implements Identificavel { // a reserva de um cliente em uma ou mais aulas

    public static final String DIARIA = "DIARIA"; // tipo de cobrança: paga por dia
    public static final String MENSALIDADE = "MENSALIDADE"; // tipo de cobrança: paga por mês
    public static final String PRELIMINAR = "PRELIMINAR"; // situação: reservado, ainda não pago
    public static final String CONFIRMADO = "CONFIRMADO"; // situação: confirmado e debitado no cartão
    public static final String CANCELADO = "CANCELADO"; // situação: cancelado, vaga liberada
    private static final int DIAS_UTEIS_CONFIRMACAO = 5; // confirmar até 5 dias úteis antes da primeira aula
    private static final int DIAS_PARA_DEVOLUCAO = 3; // desistir com 3 dias ou mais devolve metade

    private final int id; // identificador do agendamento; final: não muda
    private final Cliente cliente; // quem reservou (associação com Cliente)
    private final List<Aula> aulas; // aulas reservadas (associação com Aula)
    private final String tipoCobranca; // DIARIA ou MENSALIDADE
    private final double preco; // valor a debitar na confirmação
    private final String cartaoMascarado; // só os 4 últimos dígitos, no formato "**** 1234"
    private String status; // PRELIMINAR, CONFIRMADO ou CANCELADO; muda só pelos métodos abaixo

    public Agendamento() { // construtor sem parâmetros
        this.id = 0; // ainda sem ID
        this.cliente = null; // ainda sem cliente
        this.aulas = new ArrayList<>(); // lista vazia
        this.tipoCobranca = ""; // ainda sem tipo
        this.preco = 0; // ainda sem preço
        this.cartaoMascarado = ""; // ainda sem cartão
        this.status = PRELIMINAR; // todo agendamento nasce preliminar
    } // fim do construtor sem parâmetros

    public Agendamento(int novoId, Cliente novoCliente, List<Aula> novasAulas, String novoTipo,
            double novoPreco, String novoCartao) { // construtor usado no atendimento: recebe o cartão inteiro
        this(novoId, novoCliente, novasAulas, novoTipo, novoPreco, mascararCartao(novoCartao), PRELIMINAR); // guarda só o mascarado e nasce preliminar
    } // fim do construtor do atendimento

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

    @Override // implementa o método da interface Identificavel
    public int getId() { // getter do ID
        return id; // devolve o ID
    } // fim de getId

    public Cliente getCliente() { // getter do cliente
        return cliente; // devolve o cliente
    } // fim de getCliente

    public List<Aula> getAulas() { // getter das aulas
        return new ArrayList<>(aulas); // devolve uma cópia: quem recebe não altera a lista original
    } // fim de getAulas

    public String getTipoCobranca() { // getter do tipo de cobrança
        return tipoCobranca; // devolve DIARIA ou MENSALIDADE
    } // fim de getTipoCobranca

    public double getPreco() { // getter do preço
        return preco; // devolve o preço
    } // fim de getPreco

    public String getCartaoMascarado() { // cartão para mostrar na tela e para salvar
        return cartaoMascarado; // só os 4 últimos dígitos aparecem
    } // fim de getCartaoMascarado

    public String getStatus() { // getter da situação
        return status; // devolve PRELIMINAR, CONFIRMADO ou CANCELADO
    } // fim de getStatus

    public LocalDate getPrimeiraData() { // dia da aula mais cedo do agendamento
        LocalDate primeira = aulas.get(0).getDataHora().toLocalDate(); // começa pela primeira da lista
        for (Aula aula : aulas) { // percorre todas as aulas
            LocalDate data = aula.getDataHora().toLocalDate(); // dia desta aula
            if (data.isBefore(primeira)) { // se for antes da que temos
                primeira = data; // passa a ser a primeira
            } // fim do if
        } // fim do for
        return primeira; // devolve o dia mais cedo
    } // fim de getPrimeiraData

    public LocalDate calcularPrazoConfirmacao() { // último dia para o cliente confirmar
        return subtrairDiasUteis(getPrimeiraData(), DIAS_UTEIS_CONFIRMACAO); // 5 dias úteis antes da primeira aula
    } // fim de calcularPrazoConfirmacao

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

    public boolean cancelarSeVencido(LocalDate hoje) { // cancela o preliminar que perdeu o prazo
        if (PRELIMINAR.equals(status) && hoje.isAfter(calcularPrazoConfirmacao())) { // preliminar e prazo vencido
            status = CANCELADO; // cancela e libera a vaga
            return true; // avisa que cancelou
        } // fim do if
        return false; // nada mudou
    } // fim de cancelarSeVencido

    private static List<Aula> exigirAulas(List<Aula> novasAulas) { // confere a lista de aulas
        if (novasAulas == null || novasAulas.isEmpty()) { // precisa de pelo menos uma aula
            throw new IllegalArgumentException("Escolha pelo menos uma aula."); // erro de argumento
        } // fim do if
        for (Aula aula : novasAulas) { // percorre a lista
            if (aula == null) { // nenhuma posição pode estar vazia
                throw new IllegalArgumentException("Há uma aula inválida na lista."); // erro de argumento
            } // fim do if
        } // fim do for
        return new ArrayList<>(novasAulas); // guarda uma cópia: mudanças lá fora não afetam o agendamento
    } // fim de exigirAulas

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

    private static String exigirTipoCobranca(String valor) { // confere o tipo de cobrança
        String tipo = Validacao.exigirTexto(valor, "tipo de cobrança").toUpperCase(); // obrigatório, em maiúsculas
        if (!DIARIA.equals(tipo) && !MENSALIDADE.equals(tipo)) { // só os dois tipos do enunciado
            throw new IllegalArgumentException("O tipo de cobrança deve ser DIARIA ou MENSALIDADE."); // erro de argumento
        } // fim do if
        return tipo; // devolve o tipo conferido
    } // fim de exigirTipoCobranca

    private static String mascararCartao(String valor) { // confere o cartão inteiro e guarda só o final
        String digitos = Validacao.exigirTexto(valor, "número do cartão").replaceAll("\\D", ""); // tira espaços e traços
        if (digitos.length() < 13 || digitos.length() > 19) { // cartões têm de 13 a 19 dígitos
            throw new IllegalArgumentException("Número de cartão inválido."); // erro de argumento
        } // fim do if
        return "**** " + digitos.substring(digitos.length() - 4); // o número inteiro não fica guardado
    } // fim de mascararCartao

    private static String exigirCartaoMascarado(String valor) { // confere o formato do cartão mascarado
        if (valor == null || !valor.matches("\\*{4} \\d{4}")) { // tem que ser "**** " e 4 dígitos
            throw new IllegalArgumentException("Cartão mascarado inválido."); // erro de argumento
        } // fim do if
        return valor; // devolve o cartão mascarado conferido
    } // fim de exigirCartaoMascarado

    private static String exigirStatus(String valor) { // confere a situação
        if (!PRELIMINAR.equals(valor) && !CONFIRMADO.equals(valor) && !CANCELADO.equals(valor)) { // só as três situações
            throw new IllegalArgumentException("Situação de agendamento inválida."); // erro de argumento
        } // fim do if
        return valor; // devolve a situação conferida
    } // fim de exigirStatus

    @Override // sobrescreve o toString herdado de Object
    public String toString() { // texto que representa o agendamento
        return "Agendamento " + id + " | " + cliente.getNome() + " | " + aulas.size() + " aula(s) | "
                + tipoCobranca + " | R$ " + String.format("%.2f", preco) + " | cartão " + cartaoMascarado
                + " | " + status; // ID, cliente, aulas, cobrança, preço, cartão e situação
    } // fim de toString
} // fim da classe Agendamento