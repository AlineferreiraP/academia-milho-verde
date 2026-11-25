/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model; // pacote do Model

import com.mycompany.academia.milho.verde.dao.DAO; // interface genérica do DAO
import com.mycompany.academia.milho.verde.dao.DAOMemoria; // DAO em memória
import java.time.LocalDate; // data sem hora
import java.time.LocalDateTime; // data com hora
import java.util.ArrayList; // lista que cresce sozinha
import java.util.List; // tipo da lista

/**
 * Porta de entrada do subsistema da catraca: registra as entradas e saídas
 * dos clientes e sabe quem está dentro da academia.
 *
 * <p>O cliente apenas "passa" pela catraca; ela decide se a passagem é uma
 * ENTRADA (cliente estava fora) ou uma SAIDA (cliente estava dentro).</p>
 *
 * @author Aline Ferreira
 */
public class Catraca { // porta de entrada do subsistema Catraca: registra entradas e saídas

    /** Histórico de todas as passagens (composição). */
    private final DAO<RegistroCatraca> registros; // histórico de passagens (composição)
    /** Clientes que estão dentro da academia agora. */
    private final List<Cliente> presentes; // quem está dentro da academia agora

    /**
     * Cria a catraca sem registros e sem ninguém dentro.
     */
    public Catraca() { // construtor sem parâmetros
        this.registros = new DAOMemoria<>(); // começa sem registros
        this.presentes = new ArrayList<>(); // começa vazia
    } // fim do construtor

    /**
     * Registra a passagem de um cliente, decidindo se é entrada ou saída.
     *
     * @param cliente cliente que passou
     * @param dataHora data e hora da passagem
     * @return o registro criado
     * @throws IllegalArgumentException se o cliente não existir ou estiver desativado
     */
    public RegistroCatraca passar(Cliente cliente, LocalDateTime dataHora) { // o cliente passou: decide se é entrada ou saída
        if (cliente == null || !cliente.isAtivo()) { // só cliente ativo passa
            throw new IllegalArgumentException("Cliente inexistente ou desativado."); // erro de argumento
        } // fim do if
        String tipo; // ENTRADA ou SAIDA
        if (estaPresente(cliente.getId())) { // já está dentro?
            tipo = RegistroCatraca.SAIDA; // então está saindo
            removerPresente(cliente.getId()); // tira da lista de presentes
        } else { // está fora
            tipo = RegistroCatraca.ENTRADA; // então está entrando
            presentes.add(cliente); // entra na lista de presentes
        } // fim do if
        RegistroCatraca registro = new RegistroCatraca(registros.gerarId(), cliente, tipo, dataHora); // cria o registro
        registros.inserir(registro); // guarda no histórico
        return registro; // devolve o registro
    } // fim de passar

    /**
     * Devolve todo o histórico de passagens.
     *
     * @return cópia da lista de registros
     */
    public List<RegistroCatraca> getRegistros() { // todo o histórico
        return registros.listar(); // cópia da lista
    } // fim de getRegistros

    /**
     * Devolve quem está dentro da academia agora.
     *
     * @return cópia da lista de presentes
     */
    public List<Cliente> getPresentes() { // quem está dentro
        return new ArrayList<>(presentes); // cópia da lista
    } // fim de getPresentes

    /**
     * Conta quantas pessoas estão dentro da academia agora.
     *
     * @return quantidade de presentes
     */
    public int getQuantidadePresentes() { // quantas pessoas estão dentro
        return presentes.size(); // tamanho da lista
    } // fim de getQuantidadePresentes

    /**
     * Confere se um cliente está dentro da academia.
     *
     * @param idCliente identificador do cliente
     * @return {@code true} se está dentro, {@code false} se está fora
     */
    public boolean estaPresente(int idCliente) { // o cliente está dentro?
        IteradorLista<Cliente> iterador = new IteradorLista<>(presentes); // percorre os presentes
        while (iterador.hasNext()) { // enquanto houver cliente
            if (iterador.next().getId() == idCliente) { // é ele?
                return true; // está dentro
            } // fim do if
        } // fim do while
        return false; // está fora
    } // fim de estaPresente

    /**
     * Lista as passagens de um dia (relatório diário).
     *
     * @param data o dia desejado
     * @return registros do dia
     */
    public List<RegistroCatraca> registrosDoDia(LocalDate data) { // relatório diário
        return filtrar(data, data); // do dia até o mesmo dia
    } // fim de registrosDoDia

    /**
     * Lista as passagens de um mês (relatório mensal).
     *
     * @param ano ano desejado
     * @param mes mês desejado (1 a 12)
     * @return registros do mês
     */
    public List<RegistroCatraca> registrosDoMes(int ano, int mes) { // relatório mensal
        LocalDate inicio = LocalDate.of(ano, mes, 1); // primeiro dia do mês
        return filtrar(inicio, inicio.plusMonths(1).minusDays(1)); // até o último dia do mês
    } // fim de registrosDoMes

    /**
     * Lista as passagens de um ano (relatório anual).
     *
     * @param ano ano desejado
     * @return registros do ano
     */
    public List<RegistroCatraca> registrosDoAno(int ano) { // relatório anual
        return filtrar(LocalDate.of(ano, 1, 1), LocalDate.of(ano, 12, 31)); // de janeiro a dezembro
    } // fim de registrosDoAno

    /**
     * Acesso de pacote: devolve à catraca o que foi lido do JSON, refazendo
     * também a lista de quem está dentro.
     *
     * @param lidos registros lidos do arquivo, em ordem
     */
    void carregar(List<RegistroCatraca> lidos) { // pacote: devolve o que veio do JSON
        IteradorLista<RegistroCatraca> iterador = new IteradorLista<>(lidos); // percorre os registros lidos
        while (iterador.hasNext()) { // enquanto houver registro
            RegistroCatraca registro = iterador.next(); // pega o próximo
            registros.inserir(registro); // volta para o histórico
            if (RegistroCatraca.ENTRADA.equals(registro.getTipo())) { // entrou?
                presentes.add(registro.getCliente()); // está dentro
            } else { // saiu
                removerPresente(registro.getCliente().getId()); // está fora
            } // fim do if
        } // fim do while
    } // fim de carregar

    /**
     * Seleciona os registros entre duas datas (inclusive).
     *
     * @param inicio primeiro dia
     * @param fim último dia
     * @return registros do período
     */
    private List<RegistroCatraca> filtrar(LocalDate inicio, LocalDate fim) { // registros entre duas datas
        List<RegistroCatraca> resultado = new ArrayList<>(); // resultado começa vazio
        IteradorLista<RegistroCatraca> iterador = new IteradorLista<>(registros.listar()); // percorre o histórico
        while (iterador.hasNext()) { // enquanto houver registro
            RegistroCatraca registro = iterador.next(); // pega o próximo
            LocalDate dia = registro.getDataHora().toLocalDate(); // dia do registro
            if (!dia.isBefore(inicio) && !dia.isAfter(fim)) { // dentro do período?
                resultado.add(registro); // entra no resultado
            } // fim do if
        } // fim do while
        return resultado; // devolve os registros do período
    } // fim de filtrar

    /**
     * Tira um cliente da lista de presentes, usando o {@code remove} do iterador.
     *
     * @param idCliente identificador do cliente que saiu
     */
    private void removerPresente(int idCliente) { // tira um cliente da lista de presentes
        IteradorLista<Cliente> iterador = new IteradorLista<>(presentes); // percorre a lista de verdade (não uma cópia)
        while (iterador.hasNext()) { // enquanto houver cliente
            if (iterador.next().getId() == idCliente) { // é ele?
                iterador.remove(); // o iterador remove o último devolvido
                return; // removido
            } // fim do if
        } // fim do while
    } // fim de removerPresente

    /**
     * Resume a catraca em uma linha.
     *
     * @return quantos estão dentro e quantos registros existem
     */
    @Override // sobrescreve o toString de Object
    public String toString() { // resumo em uma linha
        return "Catraca: " + presentes.size() + " pessoa(s) dentro, " + registros.listar().size() + " registro(s)"; // situação atual
    } // fim do toString
}