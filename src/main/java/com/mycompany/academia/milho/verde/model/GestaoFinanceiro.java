/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Gerencia as despesas e gera o balanço mensal.
 *
 * <p>Só é acessada pelo administrador (veja
 * {@link Sistema#getGestaoFinanceiro()}). Para o balanço, consulta as
 * vendas da {@link GestaoVendas} e os registros da {@link Catraca}.</p>
 *
 * @author Aline Ferreira
 */
public class GestaoFinanceiro {

    /** Despesas cadastradas. */
    private List<Despesa> despesas;

    /** Gestão de vendas, fonte das receitas. */
    private GestaoVendas gestaoVendas;

    /** Catraca, fonte do número de acessos. */
    private Catraca catraca;

    /** Identificador da próxima despesa. */
    private int proximoId;

    /**
     * Construtor padrão: cria uma gestão com vendas e catraca próprias.
     */
    public GestaoFinanceiro() {
        this(new GestaoVendas(), new Catraca());
    }

    /**
     * Construtor com parâmetros.
     *
     * @param novaGestaoVendas gestão de vendas compartilhada
     * @param novaCatraca catraca compartilhada
     * @throws IllegalArgumentException se algum parâmetro for nulo
     */
    public GestaoFinanceiro(GestaoVendas novaGestaoVendas, Catraca novaCatraca) {
        if (novaGestaoVendas == null || novaCatraca == null) {
            throw new IllegalArgumentException(
                    "A gestão de vendas e a catraca são obrigatórias.");
        }
        this.despesas = new ArrayList<>();
        this.gestaoVendas = novaGestaoVendas;
        this.catraca = novaCatraca;
        this.proximoId = 1;
    }

    /**
     * Cadastra uma despesa.
     *
     * @param descricao o que foi pago
     * @param categoria uma das categorias de {@link Despesa}
     * @param valor valor maior que zero
     * @param data data da despesa
     * @return a despesa cadastrada
     * @throws IllegalArgumentException se algum dado for inválido
     */
    public Despesa cadastrarDespesa(String descricao, String categoria,
                                    double valor, LocalDate data) {
        Despesa despesa = new Despesa(gerarId(), descricao, categoria,
                valor, data);
        despesas.add(despesa);
        return despesa;
    }

    /**
     * Remove uma despesa.
     *
     * @param id identificador da despesa
     * @throws IllegalArgumentException se não existir
     */
    public void removerDespesa(int id) {
        for (Despesa despesa : despesas) {
            if (despesa.getId() == id) {
                despesas.remove(despesa);
                return;
            }
        }
        throw new IllegalArgumentException("Despesa " + id + " não encontrada.");
    }

    /**
     * Lista as despesas.
     *
     * @return cópia da lista de despesas
     */
    public List<Despesa> listarDespesas() {
        return new ArrayList<>(despesas);
    }

    /**
     * Gera o balanço de um mês.
     *
     * @param ano o ano
     * @param mes o mês, de 1 a 12
     * @return o balanço calculado
     * @throws IllegalArgumentException se o ano ou o mês forem inválidos
     */
    public BalancoMensal gerarBalancoMensal(int ano, int mes) {
        BalancoMensal balanco = new BalancoMensal(ano, mes);
        balanco.calcular(gestaoVendas.getVendas(), despesas,
                catraca.getRegistros());
        return balanco;
    }

    /**
     * Substitui as despesas pelas lidas do arquivo e ajusta o contador
     * de IDs.
     *
     * <p>Acesso de pacote: usado pelo {@link RepositorioJson}.</p>
     *
     * @param lidas despesas lidas do arquivo
     */
    void carregar(List<Despesa> lidas) {
        despesas = new ArrayList<>(lidas);
        proximoId = 1;
        for (Despesa despesa : despesas) {
            if (despesa.getId() >= proximoId) {
                proximoId = despesa.getId() + 1;
            }
        }
    }

    /**
     * Devolve o próximo identificador e avança o contador.
     *
     * @return o identificador a usar
     */
    private int gerarId() {
        return proximoId++;
    }

    /**
     * Retorna um resumo da gestão.
     *
     * @return texto com o número de despesas
     */
    @Override
    public String toString() {
        return "GestaoFinanceiro[" + despesas.size() + " despesa(s)]";
    }
}
