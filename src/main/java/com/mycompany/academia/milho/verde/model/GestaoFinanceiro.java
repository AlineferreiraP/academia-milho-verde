/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model; // pacote do Model

import com.mycompany.academia.milho.verde.dao.DAO; // interface genérica do DAO
import com.mycompany.academia.milho.verde.dao.DAOMemoria; // DAO em memória
import java.time.LocalDate; // data sem hora
import java.util.ArrayList; // lista que cresce sozinha
import java.util.List; // tipo da lista

/**
 * Porta de entrada do financeiro: gera relatórios de vendas, controla as
 * despesas e calcula o balanço mensal.
 *
 * <p>Os relatórios de vendas podem ser gerados por qualquer funcionário logado.
 * As despesas e o balanço exigem um {@link Administrador}: a conferência usa
 * {@link Funcionario#podeAcessarFinanceiro()}, que cada perfil responde do seu jeito.</p>
 */
public class GestaoFinanceiro { // relatórios de vendas, despesas e balanço mensal

    /** Gastos da academia. */
    private final DAO<Despesa> despesas; // gastos da academia
    /** Relatórios já gerados (composição). */
    private final List<RelatorioVendas> relatorios; // relatórios gerados (composição)
    /** De onde vêm as vendas da lojinha. */
    private final GestaoVendas gestaoVendas; // de onde vêm as vendas da lojinha
    /** De onde vêm os pagamentos de diárias e mensalidades. */
    private final GestaoAgendamentos gestaoAgendamentos; // de onde vêm os pagamentos de aulas
    /** De onde vêm os acessos. */
    private final Catraca catraca; // de onde vêm os acessos

    /**
     * Cria o financeiro, recebendo as partes que ele consulta.
     *
     * @param novaGestaoVendas gestão da lojinha
     * @param novaGestaoAgendamentos gestão de agendamentos
     * @param novaCatraca catraca
     * @throws IllegalArgumentException se alguma das três for nula
     */
    public GestaoFinanceiro(GestaoVendas novaGestaoVendas, GestaoAgendamentos novaGestaoAgendamentos,
            Catraca novaCatraca) { // recebe as partes que ela consulta
        if (novaGestaoVendas == null || novaGestaoAgendamentos == null || novaCatraca == null) { // precisa das três
            throw new IllegalArgumentException("Informe as gestões e a catraca."); // erro de argumento
        } // fim do if
        this.despesas = new DAOMemoria<>(); // começa sem despesas
        this.relatorios = new ArrayList<>(); // começa sem relatórios
        this.gestaoVendas = novaGestaoVendas; // guarda a gestão de vendas
        this.gestaoAgendamentos = novaGestaoAgendamentos; // guarda a gestão de agendamentos
        this.catraca = novaCatraca; // guarda a catraca
    } // fim do construtor

    /**
     * Gera o relatório das vendas da lojinha de um dia.
     *
     * @param data o dia desejado
     * @return o relatório gerado
     */
    public RelatorioVendas gerarRelatorioVendasDia(LocalDate data) { // vendas da lojinha de um dia
        return guardarRelatorio(RelatorioVendas.LOJINHA, data, data, gestaoVendas.listarVendas(data, data)); // gera e guarda
    } // fim de gerarRelatorioVendasDia

    /**
     * Gera o relatório das vendas da lojinha de um mês.
     *
     * @param ano ano desejado
     * @param mes mês desejado (1 a 12)
     * @return o relatório gerado
     */
    public RelatorioVendas gerarRelatorioVendasMes(int ano, int mes) { // vendas da lojinha de um mês
        LocalDate inicio = LocalDate.of(ano, mes, 1); // primeiro dia do mês
        LocalDate fim = inicio.plusMonths(1).minusDays(1); // último dia do mês
        return guardarRelatorio(RelatorioVendas.LOJINHA, inicio, fim, gestaoVendas.listarVendas(inicio, fim)); // gera e guarda
    } // fim de gerarRelatorioVendasMes

    /**
     * Gera o relatório dos pagamentos de diárias e mensalidades de um dia.
     *
     * @param data o dia desejado
     * @return o relatório gerado
     */
    public RelatorioVendas gerarRelatorioPagamentosDia(LocalDate data) { // diárias e mensalidades de um dia
        return guardarRelatorio(RelatorioVendas.DIARIAS_MENSALIDADES, data, data,
                gestaoAgendamentos.listarPagamentos(data, data)); // gera e guarda
    } // fim de gerarRelatorioPagamentosDia

    /**
     * Gera o relatório dos pagamentos de diárias e mensalidades de um mês.
     *
     * @param ano ano desejado
     * @param mes mês desejado (1 a 12)
     * @return o relatório gerado
     */
    public RelatorioVendas gerarRelatorioPagamentosMes(int ano, int mes) { // diárias e mensalidades de um mês
        LocalDate inicio = LocalDate.of(ano, mes, 1); // primeiro dia do mês
        LocalDate fim = inicio.plusMonths(1).minusDays(1); // último dia do mês
        return guardarRelatorio(RelatorioVendas.DIARIAS_MENSALIDADES, inicio, fim,
                gestaoAgendamentos.listarPagamentos(inicio, fim)); // gera e guarda
    } // fim de gerarRelatorioPagamentosMes

    /**
     * Lista os relatórios já gerados.
     *
     * @return cópia da lista de relatórios
     */
    public List<RelatorioVendas> listarRelatorios() { // relatórios já gerados
        return new ArrayList<>(relatorios); // cópia da lista
    } // fim de listarRelatorios

    /**
     * Lança uma despesa. Só o administrador pode.
     *
     * @param solicitante funcionário que está pedindo
     * @param descricao o que foi pago
     * @param categoria uma das categorias de {@link Despesa}
     * @param valor quanto custou
     * @param data quando foi paga
     * @return a despesa criada
     * @throws IllegalStateException se o solicitante não for administrador
     * @throws IllegalArgumentException se algum dado da despesa for inválido
     */
    public Despesa cadastrarDespesa(Funcionario solicitante, String descricao, String categoria,
            double valor, LocalDate data) { // só o administrador lança despesas
        exigirAdministrador(solicitante); // confere o perfil
        Despesa despesa = new Despesa(despesas.gerarId(), descricao, categoria, valor, data); // o construtor valida
        despesas.inserir(despesa); // guarda a despesa
        return despesa; // devolve a despesa criada
    } // fim de cadastrarDespesa

    /**
     * Remove uma despesa. Só o administrador pode.
     *
     * @param solicitante funcionário que está pedindo
     * @param id identificador da despesa
     * @throws IllegalStateException se o solicitante não for administrador
     * @throws IllegalArgumentException se a despesa não existir
     */
    public void removerDespesa(Funcionario solicitante, int id) { // só o administrador remove
        exigirAdministrador(solicitante); // confere o perfil
        if (!despesas.remover(id)) { // não existia?
            throw new IllegalArgumentException("Despesa " + id + " não encontrada."); // erro de argumento
        } // fim do if
    } // fim de removerDespesa

    /**
     * Lista as despesas. Só o administrador pode.
     *
     * @param solicitante funcionário que está pedindo
     * @return cópia da lista de despesas
     * @throws IllegalStateException se o solicitante não for administrador
     */
    public List<Despesa> listarDespesas(Funcionario solicitante) { // só o administrador vê
        exigirAdministrador(solicitante); // confere o perfil
        return despesas.listar(); // cópia da lista
    } // fim de listarDespesas

    /**
     * Gera o balanço de um mês juntando vendas da lojinha, pagamentos de aulas,
     * despesas e acessos da catraca. Só o administrador pode.
     *
     * @param solicitante funcionário que está pedindo
     * @param ano ano desejado
     * @param mes mês desejado (1 a 12)
     * @return o balanço calculado
     * @throws IllegalStateException se o solicitante não for administrador
     */
    public BalancoMensal gerarBalancoMensal(Funcionario solicitante, int ano, int mes) { // só o administrador
        exigirAdministrador(solicitante); // confere o perfil
        LocalDate inicio = LocalDate.of(ano, mes, 1); // primeiro dia do mês
        LocalDate fim = inicio.plusMonths(1).minusDays(1); // último dia do mês
        List<Venda> vendas = gestaoVendas.listarVendas(inicio, fim); // vendas da lojinha do mês
        vendas.addAll(gestaoAgendamentos.listarPagamentos(inicio, fim)); // mais os pagamentos de aulas (as duas são Venda)
        List<Despesa> doMes = new ArrayList<>(); // despesas do mês
        IteradorLista<Despesa> iterador = new IteradorLista<>(despesas.listar()); // percorre as despesas
        while (iterador.hasNext()) { // enquanto houver despesa
            Despesa despesa = iterador.next(); // pega a próxima
            if (!despesa.getData().isBefore(inicio) && !despesa.getData().isAfter(fim)) { // é do mês?
                doMes.add(despesa); // entra na lista
            } // fim do if
        } // fim do while
        BalancoMensal balanco = new BalancoMensal(ano, mes); // balanço vazio
        balanco.calcular(vendas, doMes, catraca.registrosDoMes(ano, mes)); // faz as contas
        return balanco; // devolve o balanço
    } // fim de gerarBalancoMensal

    /**
     * Acesso de pacote: usado pelo {@code RepositorioJson} para gravar.
     *
     * @return cópia da lista de despesas
     */
    List<Despesa> getDespesas() { // pacote: para o RepositorioJson gravar
        return despesas.listar(); // cópia da lista
    } // fim de getDespesas

    /**
     * Acesso de pacote: usado pelo {@code RepositorioJson} para gravar.
     *
     * @return cópia da lista de relatórios
     */
    List<RelatorioVendas> getRelatorios() { // pacote: para o RepositorioJson gravar
        return new ArrayList<>(relatorios); // cópia da lista
    } // fim de getRelatorios

    /**
     * Acesso de pacote: devolve ao financeiro o que foi lido do JSON.
     *
     * @param despesasLidas despesas lidas do arquivo
     * @param relatoriosLidos relatórios lidos do arquivo
     */
    void carregar(List<Despesa> despesasLidas, List<RelatorioVendas> relatoriosLidos) { // pacote: devolve o que veio do JSON
        IteradorLista<Despesa> iteradorDespesas = new IteradorLista<>(despesasLidas); // percorre as despesas lidas
        while (iteradorDespesas.hasNext()) { // enquanto houver despesa
            despesas.inserir(iteradorDespesas.next()); // volta para o DAO
        } // fim do while
        relatorios.addAll(relatoriosLidos); // os relatórios voltam para a lista
    } // fim de carregar

    /**
     * Cria um relatório e o guarda na lista de relatórios gerados.
     *
     * @param tipo tipo do relatório
     * @param inicio primeiro dia do período
     * @param fim último dia do período
     * @param vendas vendas do período
     * @return o relatório criado
     */
    private RelatorioVendas guardarRelatorio(String tipo, LocalDate inicio, LocalDate fim, List<Venda> vendas) { // cria e guarda
        RelatorioVendas relatorio = new RelatorioVendas(tipo, inicio, fim, vendas); // cria o relatório
        relatorios.add(relatorio); // guarda (composição)
        return relatorio; // devolve o relatório
    } // fim de guardarRelatorio

    /**
     * Confere se quem pede é administrador, perguntando ao próprio funcionário
     * (polimorfismo: só o {@link Administrador} responde {@code true}).
     *
     * @param solicitante funcionário que está pedindo
     * @throws IllegalStateException se for nulo ou não puder acessar o financeiro
     */
    private void exigirAdministrador(Funcionario solicitante) { // confere o perfil de quem pede
        if (solicitante == null || !solicitante.podeAcessarFinanceiro()) { // polimorfismo: só o administrador responde true
            throw new IllegalStateException("Apenas o administrador pode acessar o financeiro."); // erro de estado
        } // fim do if
    } // fim de exigirAdministrador

    /**
     * Resume o financeiro em uma linha.
     *
     * @return quantidade de despesas e de relatórios
     */
    @Override // sobrescreve o toString de Object
    public String toString() { // resumo em uma linha
        return "GestaoFinanceiro: " + despesas.listar().size() + " despesa(s), "
                + relatorios.size() + " relatório(s)"; // quantidades
    } // fim do toString
}
