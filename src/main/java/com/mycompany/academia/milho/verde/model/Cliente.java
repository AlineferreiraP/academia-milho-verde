/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model; // pacote do Model

import java.util.ArrayList; // lista que cresce sozinha
import java.util.List; // tipo da lista

/**
 * Cliente da academia. É uma {@link Pessoa} com CPF pseudonimizado, situação
 * (ativo ou desativado) e a lista dos seus extratos.
 *
 * <p>Cada cliente criado pelo construtor com parâmetros é contado nos dois
 * contadores estáticos do {@link Sistema}.</p>
 *
 * @author Aline Ferreira
 */
public class Cliente extends Pessoa { // Cliente É UMA Pessoa: herança

    /** CPF já pseudonimizado; o original não é guardado. */
    private String cpfPseudo; // CPF já pseudo-anonimizado (o original não é guardado)
    /** {@code true} se está ativo; {@code false} se foi desativado (exclusão lógica). */
    private boolean ativo; // true = ativo; false = desativado (exclusão lógica)
    /** Comprovantes do cliente; a lista é sempre a mesma. */
    private final List<Extrato> extratos; // comprovantes do cliente; final: a lista é sempre a mesma

    /**
     * Cria um cliente vazio, usado como "molde" (não entra nos contadores).
     */
    public Cliente() { // construtor sem parâmetros
        super(); // chama o construtor sem parâmetros da Pessoa
        this.cpfPseudo = ""; // ainda sem CPF
        this.ativo = true; // todo cliente começa ativo
        this.extratos = new ArrayList<>(); // começa sem extratos
    } // fim do construtor sem parâmetros

    /**
     * Cria um cliente com os dados informados e o conta nos contadores do {@link Sistema}.
     *
     * @param novoId identificador
     * @param novoNome nome completo
     * @param novoEndereco endereço
     * @param novoTelefone telefone
     * @param novoEmail e-mail
     * @param novoCpfPseudo CPF já pseudonimizado
     * @throws IllegalArgumentException se algum dado for inválido
     */
    public Cliente(int novoId, String novoNome, String novoEndereco,
            String novoTelefone, String novoEmail, String novoCpfPseudo) { // construtor com parâmetros
        super(novoId, novoNome, novoEndereco, novoTelefone, novoEmail); // primeira linha: a Pessoa guarda e valida os dados dela
        setCpfPseudo(novoCpfPseudo); // valida e guarda o CPF
        this.ativo = true; // todo cliente começa ativo
        this.extratos = new ArrayList<>(); // começa sem extratos
        contarInstancia(); // soma 1 nos contadores do Sistema
    } // fim do construtor com parâmetros

    /**
     * Método de classe que atualiza os dois contadores de clientes do {@link Sistema}.
     */
    private static void contarInstancia() { // método de classe: atualiza os dois contadores de clientes
        Sistema.setTotalClientes(Sistema.getTotalClientes() + 1); // estratégia 1: private static com get e set
        Sistema.totalClientesProtegido++; // estratégia 2: protected static, acesso direto (mesmo pacote)
    } // fim de contarInstancia

    /**
     * Devolve o CPF pseudonimizado.
     *
     * @return CPF pseudonimizado
     */
    public String getCpfPseudo() { // getter do CPF
        return cpfPseudo; // devolve o CPF pseudo-anonimizado
    } // fim de getCpfPseudo

    /**
     * Altera o CPF pseudonimizado (método final: as subclasses não podem trocá-lo).
     *
     * @param novoCpfPseudo novo CPF pseudonimizado
     * @throws IllegalArgumentException se estiver vazio
     */
    public final void setCpfPseudo(String novoCpfPseudo) { // setter final do CPF
        this.cpfPseudo = Validacao.exigirTexto(novoCpfPseudo, "CPF"); // valida e guarda
    } // fim de setCpfPseudo

    /**
     * Informa se o cliente está ativo.
     *
     * @return {@code true} se está ativo
     */
    public boolean isAtivo() { // getter de boolean usa "is"
        return ativo; // devolve a situação
    } // fim de isAtivo

    /**
     * Desativa o cliente (exclusão lógica): o histórico é mantido.
     */
    public void desativar() { // "excluir cliente": exclusão lógica
        this.ativo = false; // desativa sem apagar o histórico
    } // fim de desativar

    /**
     * Reativa um cliente desativado.
     */
    public void ativar() { // reativar um cliente desativado
        this.ativo = true; // volta a ficar ativo
    } // fim de ativar

    /**
     * Devolve os extratos do cliente.
     *
     * @return cópia da lista: quem recebe não altera a lista interna
     */
    public List<Extrato> getExtratos() { // devolve os extratos do cliente
        List<Extrato> copia = new ArrayList<>(); // lista nova que será devolvida
        IteradorLista<Extrato> iterador = new IteradorLista<>(extratos); // nosso iterador percorre os extratos
        while (iterador.hasNext()) { // enquanto houver extrato
            copia.add(iterador.next()); // copia o próximo
        } // fim do while
        return copia; // devolve a cópia: quem recebe não altera a lista interna
    } // fim de getExtratos

    /**
     * Guarda um extrato novo no cliente.
     *
     * @param extrato extrato a guardar
     * @throws IllegalArgumentException se for nulo ou for de outro cliente
     */
    public void adicionarExtrato(Extrato extrato) { // guarda um extrato novo
        if (extrato == null) { // não aceita "nenhum extrato"
            throw new IllegalArgumentException("Informe o extrato."); // erro de argumento
        } // fim do if
        if (extrato.getCliente().getId() != getId()) { // o extrato é de outro cliente?
            throw new IllegalArgumentException("Este extrato é de outro cliente."); // não deixa misturar
        } // fim do if
        extratos.add(extrato); // acrescenta no fim da lista
    } // fim de adicionarExtrato

    /**
     * Descreve o cliente.
     *
     * @return o texto da {@link Pessoa} mais a situação (ativo ou desativado)
     */
    @Override // sobrescreve o toString da Pessoa
    public String toString() { // texto que representa o cliente
        return "Cliente " + super.toString()
                + " | " + (ativo ? "ativo" : "desativado"); // reaproveita o texto da Pessoa e acrescenta a situação
    } // fim de toString
} // fim da classe Cliente