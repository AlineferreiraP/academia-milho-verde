/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model;


/**
 * Representa um cliente cadastrado na Academia Milho Verde.
 *
 * <p>Herda de {@link Pessoa} o identificador, o nome, o e-mail e a
 * situação do cadastro, e acrescenta endereço, telefone e CPF
 * pseudonimizado. O cliente não usa o sistema: é cadastrado e
 * atendido pelos funcionários.</p>
 *
 * @author Aline Ferreira
 */

public class Cliente extends Pessoa { // Cliente É UMA Pessoa: herança

    private String cpfPseudo; // CPF já pseudo-anonimizado (o original não é guardado)
    private boolean ativo; // true = ativo; false = desativado (exclusão lógica)

    public Cliente() { // construtor sem parâmetros
        super(); // chama o construtor sem parâmetros da Pessoa 
        this.cpfPseudo = ""; // ainda sem CPF
        this.ativo = true; // todo cliente começa ativo
    } // fim do construtor sem parâmetros

    public Cliente(int novoId, String novoNome, String novoEndereco,
            String novoTelefone, String novoEmail, String novoCpfPseudo) { // construtor com parâmetros
        super(novoId, novoNome, novoEndereco, novoTelefone, novoEmail); // primeira linha: a Pessoa guarda e valida os dados dela (item 4)
        setCpfPseudo(novoCpfPseudo); // valida e guarda o CPF
        this.ativo = true; // todo cliente começa ativo
    } // fim do construtor com parâmetros

    public String getCpfPseudo() { // getter do CPF
        return cpfPseudo; // devolve o CPF pseudo-anonimizado
    } // fim de getCpfPseudo

    public final void setCpfPseudo(String novoCpfPseudo) { // setter final do CPF
        this.cpfPseudo = Validacao.exigirTexto(novoCpfPseudo, "CPF"); // valida e guarda
    } // fim de setCpfPseudo

    public boolean isAtivo() { // getter de boolean usa "is"
        return ativo; // devolve a situação
    } // fim de isAtivo

    public void desativar() { // "excluir cliente" : exclusão lógica
        this.ativo = false; // desativa sem apagar o histórico
    } // fim de desativar

    public void ativar() { // reativar um cliente desativado
        this.ativo = true; // volta a ficar ativo
    } // fim de ativar

    @Override // sobrescreve o toString da Pessoa
    public String toString() {
        return "Cliente " + super.toString()
                + " | " + (ativo ? "ativo" : "desativado"); // reaproveita o texto da Pessoa e acrescenta a situação
    } // fim de toString
} // fim da classe Cliente