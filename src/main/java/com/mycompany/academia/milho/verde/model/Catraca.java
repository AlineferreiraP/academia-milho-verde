/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Catraca de acesso da academia.
 *
 * <p>É um sistema externo que avisa o programa a cada entrada e saída
 * de cliente. Guarda os registros e a lista de quem está dentro agora,
 * atualizada na hora: entrada inclui o cliente, saída retira.</p>
 *
 * @author Aline Ferreira
 */
public class Catraca {

    /** Todos os registros, em ordem de chegada. */
    private List<RegistroCatraca> registros;

    /** Clientes que estão na academia neste momento. */
    private List<Cliente> presentes;

    /** Identificador do próximo registro. */
    private int proximoId;

    /**
     * Construtor padrão: cria uma catraca sem registros.
     */
    public Catraca() {
        this.registros = new ArrayList<>();
        this.presentes = new ArrayList<>();
        this.proximoId = 1;
    }

    /**
     * Recebe o evento da catraca, guarda o registro e atualiza quem
     * está presente.
     *
     * @param cliente cliente que passou
     * @param tipo {@link RegistroCatraca#ENTRADA} ou
     *        {@link RegistroCatraca#SAIDA}
     * @param dataHora data e hora da passagem
     * @return o registro criado
     * @throws IllegalArgumentException se os dados forem inválidos
     * @throws IllegalStateException se o cliente já está dentro (entrada)
     *         ou não está dentro (saída)
     */
    public RegistroCatraca receberEvento(Cliente cliente, String tipo,
                                         LocalDateTime dataHora) {
        // O construtor valida os dados antes de qualquer lista mudar.
        RegistroCatraca registro = new RegistroCatraca(
                proximoId, cliente, tipo, dataHora);
        Cliente dentro = buscarPresente(cliente.getId());
        if (RegistroCatraca.ENTRADA.equals(tipo)) {
            if (dentro != null) {
                throw new IllegalStateException(
                        cliente.getNome() + " já está na academia.");
            }
            presentes.add(cliente);
        } else {
            if (dentro == null) {
                throw new IllegalStateException(
                        cliente.getNome() + " não está na academia.");
            }
            presentes.remove(dentro);
        }
        registros.add(registro);
        proximoId++;
        return registro;
    }

    /**
     * Retorna todos os registros.
     *
     * @return cópia da lista de registros
     */
    public List<RegistroCatraca> getRegistros() {
        return new ArrayList<>(registros);
    }

    /**
     * Retorna os clientes que estão na academia agora.
     *
     * @return cópia da lista de presentes
     */
    public List<Cliente> getPresentes() {
        return new ArrayList<>(presentes);
    }

    /**
     * Informa quantos clientes estão na academia agora.
     *
     * @return o número de presentes
     */
    public int getQuantidadePresentes() {
        return presentes.size();
    }
        /**
     * Informa se um cliente está na academia agora.
     *
     * @param idCliente identificador do cliente
     * @return {@code true} se a última passagem dele foi uma entrada
     */
    public boolean estaPresente(int idCliente) {
        return buscarPresente(idCliente) != null;
    }

    /**
     * Lista os registros de um dia.
     *
     * @param data o dia
     * @return os registros desse dia
     */
    public List<RegistroCatraca> registrosDoDia(LocalDate data) {
        List<RegistroCatraca> lista = new ArrayList<>();
        for (RegistroCatraca registro : registros) {
            if (registro.getDataHora().toLocalDate().equals(data)) {
                lista.add(registro);
            }
        }
        return lista;
    }

    /**
     * Lista os registros de um mês.
     *
     * @param ano o ano
     * @param mes o mês, de 1 a 12
     * @return os registros desse mês
     * @throws IllegalArgumentException se o mês for inválido
     */
    public List<RegistroCatraca> registrosDoMes(int ano, int mes) {
        if (mes < 1 || mes > 12) {
            throw new IllegalArgumentException("O mês deve ser de 1 a 12.");
        }
        List<RegistroCatraca> lista = new ArrayList<>();
        for (RegistroCatraca registro : registros) {
            LocalDateTime quando = registro.getDataHora();
            if (quando.getYear() == ano && quando.getMonthValue() == mes) {
                lista.add(registro);
            }
        }
        return lista;
    }

    /**
     * Lista os registros de um ano.
     *
     * @param ano o ano
     * @return os registros desse ano
     */
    public List<RegistroCatraca> registrosDoAno(int ano) {
        List<RegistroCatraca> lista = new ArrayList<>();
        for (RegistroCatraca registro : registros) {
            if (registro.getDataHora().getYear() == ano) {
                lista.add(registro);
            }
        }
        return lista;
    }

    /**
     * Lista os registros de um cliente.
     *
     * @param idCliente identificador do cliente
     * @return os registros desse cliente
     */
    public List<RegistroCatraca> registrosDoCliente(int idCliente) {
        List<RegistroCatraca> lista = new ArrayList<>();
        for (RegistroCatraca registro : registros) {
            if (registro.getCliente().getId() == idCliente) {
                lista.add(registro);
            }
        }
        return lista;
    }

    /**
     * Procura entre os presentes um cliente pelo identificador.
     *
     * @param idCliente identificador procurado
     * @return o cliente presente, ou {@code null}
     */
    private Cliente buscarPresente(int idCliente) {
        for (Cliente cliente : presentes) {
            if (cliente.getId() == idCliente) {
                return cliente;
            }
        }
        return null;
    }

    /**
     * Recria o estado da catraca a partir dos registros gravados.
     *
     * <p>Acesso de pacote: só o {@link RepositorioJson} usa. Reconstrói
     * a lista de presentes repetindo os eventos em ordem.</p>
     *
     * @param lidos registros lidos do arquivo, em ordem
     */
    void restaurar(List<RegistroCatraca> lidos) {
        registros = new ArrayList<>();
        presentes = new ArrayList<>();
        proximoId = 1;
        for (RegistroCatraca registro : lidos) {
            registros.add(registro);
            Cliente dentro = buscarPresente(registro.getCliente().getId());
            if (RegistroCatraca.ENTRADA.equals(registro.getTipo())) {
                if (dentro == null) {
                    presentes.add(registro.getCliente());
                }
            } else if (dentro != null) {
                presentes.remove(dentro);
            }
            if (registro.getId() >= proximoId) {
                proximoId = registro.getId() + 1;
            }
        }
    }

    /**
     * Retorna um resumo da catraca.
     *
     * @return texto com o número de registros e de presentes
     */
    @Override
    public String toString() {
        return "Catraca[registros=" + registros.size()
                + ", presentes=" + presentes.size() + "]";
    }
}