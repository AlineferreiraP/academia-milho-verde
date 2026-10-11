/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.academia.milho.verde;

import com.mycompany.academia.milho.verde.model.Cliente;
import com.mycompany.academia.milho.verde.model.RepositorioJson;
import java.io.IOException;
import java.util.List;

/**
 * Classe principal da Academia Milho Verde.
 *
 * <p>Nesta etapa, o main apenas testa o cadastro de clientes
 * e a gravação e leitura em JSON.</p>
 *
 * @author Aline Ferreira Pena
 */
public class AcademiaMilhoVerde {

    /** Classe só com o main: não deve ser instanciada. */
    private AcademiaMilhoVerde() {
    }

    /**
     * Ponto de entrada do programa.
     *
     * @param args argumentos da linha de comando não utilizados
     */
    public static void main(String[] args) {

        RepositorioJson repositorio = new RepositorioJson();

        // Construtor com parâmetros.
        Cliente ana = new Cliente(1, "Ana Souza", "Rua das Flores, 10",
                "(38) 99999-0000", "ana@gmail.com", "a1b2c3");
        Cliente joao = new Cliente(2, "João Lima", "Av. Central, 5",
                "(38) 98888-1111", "joao@gmail.com", "d4e5f6");
        joao.desativar();

        // Construtor padrão.
        Cliente vazio = new Cliente();
        System.out.println("Construtor padrão: id=" + vazio.getId()
                + ", ativo=" + vazio.isAtivo());

        try {
            repositorio.salvarClientes(List.of(ana, joao));
            System.out.println("Gravado em: "
                    + repositorio.getArquivo().toAbsolutePath());

            List<Cliente> lidos = repositorio.carregarClientes();
            for (Cliente c : lidos) {
                System.out.println(c.getId() + " - " + c.getNome()
                        + " - ativo: " + c.isAtivo());
            }
        } catch (IOException erro) {
            System.out.println("Erro ao acessar o arquivo: "
                    + erro.getMessage());
        }

        // Validação: deve cair no catch.
        try {
            new Cliente(3, "Maria", "   ", "123", "m@email.com", "x");
        } catch (IllegalArgumentException erro) {
            System.out.println("Validação OK: " + erro.getMessage());
        }

        // atualizarCadastro e toString.
        ana.atualizarCadastro("Ana Souza Lima", "Rua das Flores, 20",
                "(38) 99999-0000", "ana@email.com", "a1b2c3");
        System.out.println("Atualizado: " + ana);

        // equals e hashCode: o mesmo ID representa o mesmo cliente.
        Cliente mesmaAna = new Cliente(1, "Outro Nome", "Outra Rua",
                "000", "x@email.com", "zzz");
        System.out.println("Mesmo ID é igual: " + ana.equals(mesmaAna));
        System.out.println("Mesmo hash: "
                + (ana.hashCode() == mesmaAna.hashCode()));
        System.out.println("IDs diferentes: " + ana.equals(joao));

        // Dado inválido: nada muda, nem o nome.
        try {
            ana.atualizarCadastro("Nome Novo", "Rua X", "111", "   ", "a1");
        } catch (IllegalArgumentException erro) {
            System.out.println("Nada mudou: " + ana.getNome()
                    + " (" + erro.getMessage() + ")");
        }
    }
}