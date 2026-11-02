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
 * Gerencia produtos, estoque, vendas e relatórios de vendas.
 *
 * <p>Toda venda gera o extrato, que também fica guardado no cliente. O
 * extrato recebe o mesmo número da venda, porque há um por venda.</p>
 *
 * @author Aline Ferreira
 */
public class GestaoVendas {

    /** Estoque da lojinha e da lanchonete. */
    private Estoque estoque;

    /** Todas as vendas registradas. */
    private List<Venda> vendas;

    /** Relatórios de vendas já gerados. */
    private List<RelatorioVendas> relatoriosVendas;

    /** Identificador da próxima venda. */
    private int proximoId;

    /** Identificador do próximo produto. */
    private int proximoIdProduto;

    /**
     * Construtor padrão: cria uma gestão com estoque vazio.
     */
    public GestaoVendas() {
        this.estoque = new Estoque();
        this.vendas = new ArrayList<>();
        this.relatoriosVendas = new ArrayList<>();
        this.proximoId = 1;
        this.proximoIdProduto = 1;
    }

    /**
     * Cadastra um produto novo no estoque.
     *
     * @param nome nome do produto
     * @param categoria {@link Produto#LOJA} ou {@link Produto#LANCHONETE}
     * @param preco preço unitário
     * @param quantidade quantidade inicial
     * @return o produto cadastrado
     * @throws IllegalArgumentException se algum dado for inválido
     */
    public Produto cadastrarProduto(String nome, String categoria,
                                    double preco, int quantidade) {
        Produto produto = new Produto(proximoIdProduto, nome, categoria,
                preco, quantidade);
        estoque.adicionarProduto(produto);
        proximoIdProduto++;
        return produto;
    }

    /**
     * Remove um produto do estoque. As vendas antigas continuam
     * guardando os dados do produto.
     *
     * @param id identificador do produto
     * @throws IllegalArgumentException se o produto não existir
     */
    public void removerProduto(int id) {
        if (!estoque.removerProduto(id)) {
            throw new IllegalArgumentException(
                    "Produto " + id + " não encontrado.");
        }
    }

    /**
     * Procura um produto e falha se não existir.
     *
     * @param id identificador do produto
     * @return o produto
     * @throws IllegalArgumentException se o produto não existir
     */
    public Produto verificarProduto(int id) {
        Produto produto = estoque.buscarProduto(id);
        if (produto == null) {
            throw new IllegalArgumentException(
                    "Produto " + id + " não encontrado.");
        }
        return produto;
    }

    /**
     * Acrescenta unidades de um produto ao estoque.
     *
     * @param idProduto identificador do produto
     * @param quantidade unidades a acrescentar
     * @throws IllegalArgumentException se o produto não existir ou a
     *         quantidade não for positiva
     */
    public void reporEstoque(int idProduto, int quantidade) {
        estoque.reporEstoque(idProduto, quantidade);
    }

    /**
     * Registra a venda de produtos da lojinha ou da lanchonete.
     *
     * <p>Primeiro confere todos os itens; só depois dá baixa no
     * estoque. Assim, uma venda recusada não deixa o estoque pela
     * metade.</p>
     *
     * @param cliente cliente ativo que compra
     * @param idsProdutos identificadores dos produtos, sem repetição
     * @param quantidades quantidade de cada produto, na mesma ordem
     * @return a venda registrada, já com o extrato
     * @throws IllegalArgumentException se os dados forem inválidos ou
     *         algum produto não existir
     * @throws IllegalStateException se faltar estoque de algum produto
     */
    public Venda registrarVenda(Cliente cliente, List<Integer> idsProdutos,
                                List<Integer> quantidades) {
        if (cliente == null || !cliente.isAtivo()) {
            throw new IllegalArgumentException(
                    "A venda precisa de um cliente ativo.");
        }
        if (idsProdutos == null || quantidades == null
                || idsProdutos.isEmpty()
                || idsProdutos.size() != quantidades.size()) {
            throw new IllegalArgumentException(
                    "Informe ao menos um produto, com a quantidade de cada um.");
        }
        // 1ª passada: confere tudo sem alterar nada.
        for (int i = 0; i < idsProdutos.size(); i++) {
            int id = idsProdutos.get(i);
            int quantidade = quantidades.get(i);
            for (int j = 0; j < i; j++) {
                if (idsProdutos.get(j) == id) {
                    throw new IllegalArgumentException(
                            "O produto " + id + " foi informado duas vezes.");
                }
            }
            Produto produto = verificarProduto(id);
            if (quantidade <= 0) {
                throw new IllegalArgumentException(
                        "A quantidade deve ser maior que zero.");
            }
            if (!estoque.verificarDisponibilidade(id, quantidade)) {
                throw new IllegalStateException(
                        "Estoque insuficiente de " + produto.getNome() + ".");
            }
        }
        // 2ª passada: tudo conferido, agora registra e dá baixa.
        Venda venda = new Venda(gerarId(), cliente, Venda.PRODUTO,
                LocalDateTime.now());
        for (int i = 0; i < idsProdutos.size(); i++) {
            Produto produto = estoque.buscarProduto(idsProdutos.get(i));
            venda.adicionarItem(new ItemVenda(produto, quantidades.get(i)));
            estoque.darBaixa(produto.getId(), quantidades.get(i));
        }
        venda.gerarExtrato(venda.getId());
        vendas.add(venda);
        return venda;
    }

    /**
     * Registra a venda de uma diária ou mensalidade, a partir de um
     * agendamento confirmado.
     *
     * @param agendamento agendamento confirmado
     * @param dataHora data e hora da cobrança
     * @return a venda registrada, já com o extrato
     * @throws IllegalArgumentException se algum parâmetro for nulo
     * @throws IllegalStateException se o agendamento não estiver confirmado
     */
    public Venda registrarVendaAgendamento(Agendamento agendamento,
                                           LocalDateTime dataHora) {
        if (agendamento == null || dataHora == null) {
            throw new IllegalArgumentException(
                    "Agendamento e data são obrigatórios.");
        }
        if (!Agendamento.CONFIRMADO.equals(agendamento.getStatus())) {
            throw new IllegalStateException(
                    "Só um agendamento confirmado pode ser cobrado.");
        }
        String tipo = Agendamento.DIARIA.equals(agendamento.getTipoCobranca())
                ? Venda.DIARIA : Venda.MENSALIDADE;
        Venda venda = new Venda(gerarId(), agendamento.getCliente(), tipo,
                dataHora);
        venda.setValorTotal(agendamento.getPreco());
        venda.gerarExtrato(venda.getId());
        vendas.add(venda);
        return venda;
    }

    /**
     * Gera o relatório das vendas de um dia.
     *
     * @param data o dia
     * @return o relatório, também guardado na lista de relatórios
     * @throws IllegalArgumentException se a data for nula
     */
    public RelatorioVendas gerarRelatorioVendasDia(LocalDate data) {
        if (data == null) {
            throw new IllegalArgumentException("A data é obrigatória.");
        }
        RelatorioVendas relatorio = new RelatorioVendas(data, data,
                vendasEntre(data, data));
        relatoriosVendas.add(relatorio);
        return relatorio;
    }

    /**
     * Gera o relatório das vendas de um mês.
     *
     * @param ano o ano
     * @param mes o mês, de 1 a 12
     * @return o relatório, também guardado na lista de relatórios
     * @throws IllegalArgumentException se o mês for inválido
     */
    public RelatorioVendas gerarRelatorioVendasMes(int ano, int mes) {
        if (mes < 1 || mes > 12) {
            throw new IllegalArgumentException("O mês deve ser de 1 a 12.");
        }
        LocalDate inicio = LocalDate.of(ano, mes, 1);
        LocalDate fim = inicio.withDayOfMonth(inicio.lengthOfMonth());
        RelatorioVendas relatorio = new RelatorioVendas(inicio, fim,
                vendasEntre(inicio, fim));
        relatoriosVendas.add(relatorio);
        return relatorio;
    }

    /**
     * Retorna o estoque.
     *
     * @return o estoque da lojinha e da lanchonete
     */
    public Estoque getEstoque() {
        return estoque;
    }

    /**
     * Retorna todas as vendas.
     *
     * <p>Acesso de pacote: usado pelo {@link RepositorioJson} e pelas
     * outras gestões.</p>
     *
     * @return cópia da lista de vendas
     */
    List<Venda> getVendas() {
        return new ArrayList<>(vendas);
    }

    /**
     * Retorna os relatórios já gerados.
     *
     * <p>Acesso de pacote: usado pelo {@link RepositorioJson}.</p>
     *
     * @return cópia da lista de relatórios
     */
    List<RelatorioVendas> getRelatoriosVendas() {
        return new ArrayList<>(relatoriosVendas);
    }

    /**
     * Substitui produtos, vendas e relatórios pelos lidos do arquivo e
     * ajusta os contadores de IDs.
     *
     * @param produtosLidos produtos do estoque
     * @param vendasLidas todas as vendas
     * @param relatoriosLidos relatórios gravados
     */
    void carregar(List<Produto> produtosLidos, List<Venda> vendasLidas,
                  List<RelatorioVendas> relatoriosLidos) {
        estoque = new Estoque();
        proximoIdProduto = 1;
        for (Produto produto : produtosLidos) {
            estoque.adicionarProduto(produto);
            if (produto.getId() >= proximoIdProduto) {
                proximoIdProduto = produto.getId() + 1;
            }
        }
        vendas = new ArrayList<>(vendasLidas);
        proximoId = 1;
        for (Venda venda : vendas) {
            if (venda.getId() >= proximoId) {
                proximoId = venda.getId() + 1;
            }
        }
        relatoriosVendas = new ArrayList<>(relatoriosLidos);
    }

    /**
     * Seleciona as vendas feitas entre duas datas (inclusive).
     *
     * @param inicio primeiro dia
     * @param fim último dia
     * @return lista nova com as vendas do período
     */
    private List<Venda> vendasEntre(LocalDate inicio, LocalDate fim) {
        List<Venda> selecionadas = new ArrayList<>();
        for (Venda venda : vendas) {
            LocalDate dia = venda.getDataHora().toLocalDate();
            if (!dia.isBefore(inicio) && !dia.isAfter(fim)) {
                selecionadas.add(venda);
            }
        }
        return selecionadas;
    }

    /**
     * Devolve o próximo identificador de venda e avança o contador.
     *
     * @return o identificador a usar
     */
    private int gerarId() {
        return proximoId++;
    }

    /**
     * Retorna um resumo da gestão.
     *
     * @return texto com o número de vendas e o estoque
     */
    @Override
    public String toString() {
        return "GestaoVendas[" + vendas.size() + " venda(s), " + estoque + "]";
    }
}