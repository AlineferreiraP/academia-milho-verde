/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model; // pacote do Model

import com.mycompany.academia.milho.verde.dao.DAO; // interface genérica do DAO
import com.mycompany.academia.milho.verde.dao.DAOMemoria; // DAO em memória, usado como padrão
import java.time.LocalDate; // só a data (para os relatórios)
import java.time.LocalDateTime; // data e hora da venda
import java.util.ArrayList; // lista que cresce sozinha
import java.util.List; // tipo da lista

/**
 * Porta de entrada do subsistema da lojinha: cadastra produtos, controla o
 * estoque e registra as vendas.
 *
 * <p>É dona do {@link Estoque} (composição). A venda é "tudo ou nada": primeiro
 * confere todos os produtos e só depois vende e dá baixa. O extrato da venda
 * é guardado no cliente pelo {@link Sistema}, não aqui.</p>
 *
 * @author Aline Ferreira
 */
public class GestaoVendas { // porta de entrada do subsistema Lojinha

    /** Os produtos da lojinha; nasce e morre com a gestão (composição). */
    private final Estoque estoque; // os produtos da lojinha; final: nasce e morre com a gestão (composição)
    /** As vendas da lojinha. */
    private final DAO<VendaProdutos> vendas; // as vendas da lojinha; final: não troca depois de criado

    /**
     * Cria a lojinha com o estoque e as vendas vazios.
     */
    public GestaoVendas() { // construtor sem parâmetros
        this.estoque = new Estoque(); // cria o estoque vazio (composição: a gestão cria a parte)
        this.vendas = new DAOMemoria<>(); // começa sem vendas
    } // fim do construtor

    /**
     * Cadastra um produto novo na lojinha.
     *
     * @param nome nome do produto
     * @param preco preço de uma unidade (maior que zero)
     * @param quantidade estoque inicial (não negativo)
     * @return o produto criado
     * @throws IllegalArgumentException se algum dado for inválido
     */
    public Produto cadastrarProduto(String nome, double preco, int quantidade) { // produto novo na lojinha
        Produto produto = new Produto(estoque.gerarId(), nome, preco, quantidade); // pede o ID e o construtor valida os dados
        estoque.adicionarProduto(produto); // guarda no estoque
        return produto; // devolve o produto criado
    } // fim de cadastrarProduto

    /**
     * Tira um produto da lojinha.
     *
     * @param id identificador do produto
     * @throws IllegalArgumentException se o produto não existir
     */
    public void removerProduto(int id) { // tira um produto da lojinha
        if (!estoque.removerProduto(id)) { // não existia?
            throw new IllegalArgumentException("Produto " + id + " não encontrado."); // erro de argumento
        } // fim do if
    } // fim de removerProduto

    /**
     * Consulta um produto (nome, preço e quantidade).
     *
     * @param id identificador do produto
     * @return o produto encontrado
     * @throws IllegalArgumentException se o produto não existir
     */
    public Produto verificarProduto(int id) { // consulta um produto (nome, preço e quantidade)
        Produto produto = estoque.buscarProduto(id); // procura no estoque
        if (produto == null) { // não achou?
            throw new IllegalArgumentException("Produto " + id + " não encontrado."); // erro de argumento
        } // fim do if
        return produto; // devolve o produto encontrado
    } // fim de verificarProduto

    /**
     * Repõe unidades de um produto que chegaram.
     *
     * @param idProduto identificador do produto
     * @param quantidade unidades que chegaram (maior que zero)
     * @throws IllegalArgumentException se o produto não existir ou a quantidade for inválida
     */
    public void reporEstoque(int idProduto, int quantidade) { // chegaram unidades novas
        estoque.reporEstoque(idProduto, quantidade); // o estoque confere e soma
    } // fim de reporEstoque

    /**
     * Registra uma venda: vende tudo ou nada.
     *
     * <p>A primeira volta só confere (existência e estoque de cada produto); a
     * segunda volta cria as linhas da venda e dá baixa no estoque.</p>
     *
     * @param cliente cliente que compra
     * @param idsProdutos identificadores dos produtos
     * @param quantidades quantidade de cada produto, na mesma ordem
     * @return a venda registrada (quem chamou gera o extrato e o guarda no cliente)
     * @throws IllegalArgumentException se faltar cliente, as listas forem inválidas ou um produto repetir
     * @throws IllegalStateException se algum produto não tiver estoque suficiente (nada é vendido)
     */
    public VendaProdutos registrarVenda(Cliente cliente, List<Integer> idsProdutos,
            List<Integer> quantidades) { // vende tudo ou nada
        if (cliente == null) { // a venda precisa de um cliente
            throw new IllegalArgumentException("Informe o cliente."); // erro de argumento
        } // fim do if
        if (idsProdutos == null || quantidades == null || idsProdutos.isEmpty()
                || idsProdutos.size() != quantidades.size()) { // listas vazias ou de tamanhos diferentes
            throw new IllegalArgumentException("Informe os produtos e as quantidades."); // erro de argumento
        } // fim do if
        exigirSemRepetidos(idsProdutos); // o mesmo produto não pode aparecer duas vezes

        IteradorLista<Integer> iteradorIds = new IteradorLista<>(idsProdutos); // percorre os IDs
        IteradorLista<Integer> iteradorQtds = new IteradorLista<>(quantidades); // percorre as quantidades, lado a lado
        while (iteradorIds.hasNext()) { // 1ª volta: só confere, não mexe em nada
            int id = iteradorIds.next(); // próximo produto
            int quantidade = iteradorQtds.next(); // quantidade desse produto
            Produto produto = verificarProduto(id); // o produto precisa existir
            if (!estoque.verificarDisponibilidade(id, quantidade)) { // não tem o suficiente?
                throw new IllegalStateException("Estoque insuficiente de " + produto.getNome()
                        + ": há " + produto.getQuantidade() + "."); // nada foi vendido ainda
            } // fim do if
        } // fim do while

        VendaProdutos venda = new VendaProdutos(vendas.gerarId(), cliente,
                LocalDateTime.now().withSecond(0).withNano(0)); // cria a venda com a hora de agora, sem segundos
        iteradorIds = new IteradorLista<>(idsProdutos); // iterador novo para a 2ª volta
        iteradorQtds = new IteradorLista<>(quantidades); // iterador novo para as quantidades
        while (iteradorIds.hasNext()) { // 2ª volta: tudo conferido, agora vende
            int id = iteradorIds.next(); // próximo produto
            int quantidade = iteradorQtds.next(); // quantidade desse produto
            venda.adicionarItem(new ItemVenda(estoque.buscarProduto(id), quantidade)); // linha com o preço de hoje
            estoque.darBaixa(id, quantidade); // tira do estoque
        } // fim do while
        vendas.inserir(venda); // guarda a venda
        return venda; // quem chamou gera o extrato e o guarda no cliente
    } // fim de registrarVenda

    /**
     * Devolve o estoque da lojinha, para consultas.
     *
     * @return o estoque
     */
    public Estoque getEstoque() { // devolve o estoque
        return estoque; // para consultas
    } // fim de getEstoque

    /**
     * Acesso de pacote: usado pelo {@code RepositorioJson} para gravar.
     *
     * @return cópia da lista de vendas
     */
    List<VendaProdutos> getVendas() { // pacote: todas as vendas, para o RepositorioJson gravar
        return vendas.listar(); // cópia da lista
    } // fim de getVendas

    /**
     * Acesso de pacote: devolve à lojinha o que foi lido do JSON.
     *
     * @param produtosLidos produtos lidos do arquivo, com a quantidade salva
     * @param vendasLidas vendas lidas do arquivo
     */
    void carregar(List<Produto> produtosLidos, List<VendaProdutos> vendasLidas) { // pacote: devolve o que veio do JSON
        IteradorLista<Produto> iteradorProdutos = new IteradorLista<>(produtosLidos); // percorre os produtos lidos
        while (iteradorProdutos.hasNext()) { // enquanto houver produto
            estoque.adicionarProduto(iteradorProdutos.next()); // volta para o estoque com a quantidade salva
        } // fim do while
        IteradorLista<VendaProdutos> iteradorVendas = new IteradorLista<>(vendasLidas); // percorre as vendas lidas
        while (iteradorVendas.hasNext()) { // enquanto houver venda
            vendas.inserir(iteradorVendas.next()); // volta para o DAO de vendas
        } // fim do while
    } // fim de carregar

    /**
     * Lista as vendas de um período (usado nos relatórios).
     *
     * @param inicio primeiro dia do período
     * @param fim último dia do período (inclusive)
     * @return as vendas do período, como {@link Venda}; a lista é nova e pode receber mais itens
     * @throws IllegalArgumentException se o período for inválido
     */
    public List<Venda> listarVendas(LocalDate inicio, LocalDate fim) { // vendas de um período (para os relatórios)
        if (inicio == null || fim == null || fim.isBefore(inicio)) { // período inválido
            throw new IllegalArgumentException("Período inválido."); // erro de argumento
        } // fim do if
        List<Venda> doPeriodo = new ArrayList<>(); // resultado começa vazio
        IteradorLista<VendaProdutos> iterador = new IteradorLista<>(vendas.listar()); // percorre as vendas
        while (iterador.hasNext()) { // enquanto houver venda
            VendaProdutos venda = iterador.next(); // pega a próxima
            LocalDate dia = venda.getDataHora().toLocalDate(); // só o dia da venda
            if (!dia.isBefore(inicio) && !dia.isAfter(fim)) { // está dentro do período (inclui as pontas)?
                doPeriodo.add(venda); // entra no resultado
            } // fim do if
        } // fim do while
        return doPeriodo; // lista de Venda: o relatório soma sem saber o tipo (polimorfismo)
    } // fim de listarVendas

    /**
     * Recusa a lista de IDs se o mesmo produto aparecer duas vezes.
     *
     * @param ids identificadores pedidos
     * @throws IllegalArgumentException se algum ID se repetir
     */
    private static void exigirSemRepetidos(List<Integer> ids) { // recusa o mesmo produto duas vezes
        List<Integer> vistos = new ArrayList<>(); // IDs que já apareceram
        IteradorLista<Integer> iterador = new IteradorLista<>(ids); // percorre os IDs pedidos
        while (iterador.hasNext()) { // enquanto houver ID
            int id = iterador.next(); // pega o próximo
            IteradorLista<Integer> iteradorVistos = new IteradorLista<>(vistos); // percorre os que já vimos
            while (iteradorVistos.hasNext()) { // enquanto houver ID visto
                if (iteradorVistos.next() == id) { // já apareceu antes?
                    throw new IllegalArgumentException("O produto " + id + " aparece duas vezes."); // erro de argumento
                } // fim do if
            } // fim do while interno
            vistos.add(id); // marca como visto
        } // fim do while
    } // fim de exigirSemRepetidos

    /**
     * Resume a lojinha em uma linha.
     *
     * @return o estoque e a quantidade de vendas
     */
    @Override // sobrescreve o toString de Object
    public String toString() { // resumo em uma linha
        return "GestaoVendas: " + estoque + ", " + vendas.listar().size() + " venda(s)"; // estoque e quantidade de vendas
    } // fim do toString
}