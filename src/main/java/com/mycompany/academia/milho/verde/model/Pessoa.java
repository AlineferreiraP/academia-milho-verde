/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.academia.milho.verde.model;


public abstract class Pessoa implements Identificavel { // abstrata: não se cria uma "pessoa" solta; tem ID, então é Identificavel
    private static final int SEM_ID = 0; // constante da classe: valor de quem ainda não tem ID 

    private final int id; // final: recebe valor uma única vez, no construtor 
    private String nome; // nome completo
    private String endereco; // endereço
    private String telefone; // telefone de contato
    private String email; // e-mail de contato

    public Pessoa() { // construtor sem parâmetros 
        this.id = SEM_ID; // ainda sem ID
        this.nome = ""; // texto vazio, direto no atributo (o setter recusaria vazio)
        this.endereco = ""; // idem
        this.telefone = ""; // idem
        this.email = ""; // idem
    } // fim do construtor sem parâmetros

    public Pessoa(int novoId, String novoNome, String novoEndereco,
            String novoTelefone, String novoEmail) { // construtor com parâmetros; prefixo "novo" evita sombrear 
        if (novoId <= SEM_ID) { // ID precisa ser maior que zero
            throw new IllegalArgumentException("O ID deve ser maior que zero."); // ID inválido: lança o erro
        } // fim do if
        this.id = novoId; // guarda o ID (única vez)
        setNome(novoNome); // o setter valida e guarda (Aula 8)
        setEndereco(novoEndereco); // idem
        setTelefone(novoTelefone); // idem
        setEmail(novoEmail); // idem
    } // fim do construtor com parâmetros

    public String getNome() { // getter do nome
        return nome; // devolve o nome
    } // fim de getNome

    public final void setNome(String novoNome) { // final: subclasse não muda a validação (Aula 9)
        this.nome = Validacao.exigirTexto(novoNome, "nome"); // valida e guarda
    } // fim de setNome

    public String getEndereco() { // getter do endereço
        return endereco; // devolve o endereço
    } // fim de getEndereco

    public final void setEndereco(String novoEndereco) { // setter final do endereço
        this.endereco = Validacao.exigirTexto(novoEndereco, "endereço"); // valida e guarda
    } // fim de setEndereco

    public String getTelefone() { // getter do telefone
        return telefone; // devolve o telefone
    } // fim de getTelefone

    public final void setTelefone(String novoTelefone) { // setter final do telefone
        this.telefone = Validacao.exigirTexto(novoTelefone, "telefone"); // valida e guarda
    } // fim de setTelefone

    public String getEmail() { // getter do e-mail
        return email; // devolve o e-mail
    } // fim de getEmail
    public final void setEmail(String novoEmail) { // setter final do e-mail
        this.email = Validacao.exigirTexto(novoEmail, "e-mail"); // valida e guarda
    } // fim de setEmail

    @Override // sobrescreve o toString herdado de Object
    public String toString() { // texto que representa a pessoa 
        return "ID " + id + " | " + nome + " | " + endereco
                + " | " + telefone + " | " + email; // junta os cinco dados em uma linha
    } // fim de toString
    @Override // implementa o método da interface Identificavel
    public int getId() { // getter do ID (não existe setter)
        return id; // devolve o ID
    } // fim de getId
} // fim da classe Pessoa