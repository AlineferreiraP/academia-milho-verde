/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.academia.milho.verde;

import com.mycompany.academia.milho.verde.model.Administrador;
import com.mycompany.academia.milho.verde.model.Cliente;
import com.mycompany.academia.milho.verde.model.Colaborador;
import com.mycompany.academia.milho.verde.model.Funcionario;
import com.mycompany.academia.milho.verde.model.RepositorioJson;
import java.io.IOException;
import java.util.List;

/**
 * Classe principal da Academia Milho Verde.
 *
 * <p>Nesta etapa, o main testa clientes, funcionários e a gravação
 * e leitura em JSON.</p>
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

        // ------------------------------------------------- clientes
        System.out.println("=== Clientes ===");

        // Construtor com parâmetros.
        Cliente ana = new Cliente(1, "Ana Souza", "Rua das Flores, 10",
                "(38) 99999-0000", "ana@gmail.com", "a1b2c3");
        Cliente joao = new Cliente(2, "João Lima", "Av. Central, 5",
                "(38) 98888-1111", "joao@gmail.com", "d4e5f6");
        Cliente carla = new Cliente(3, "Carla Mendes", "Rua do Rosário, 42",
                "(38) 97777-2222", "carla@gmail.com", "g7h8i9");
        Cliente pedro = new Cliente(4, "Pedro Alves", "Travessa da Matriz, 3",
                "(31) 96666-3333", "pedro@gmail.com", "j1k2l3");
        Cliente lucia = new Cliente(5, "Lúcia Ferreira", "Rua Direita, 18",
                "(38) 95555-4444", "lucia@gmail.com", "m4n5o6");
        Cliente rafael = new Cliente(6, "Rafael Costa", "Rua do Comércio, 7",
                "(11) 94444-5555", "rafael@gmail.com", "p7q8r9");
        Cliente juliana = new Cliente(7, "Juliana Martins", "Largo do Rosário, 2",
                "(38) 93333-6666", "juliana@gmail.com", "s1t2u3");
        Cliente thiago = new Cliente(8, "Thiago Ribeiro", "Rua da Cachoeira, 25",
                "(31) 92222-7777", "thiago@gmail.com", "v4w5x6");
        joao.desativar();

        // Construtor padrão.
        Cliente vazio = new Cliente();
        System.out.println("Construtor padrão: id=" + vazio.getId()
                + ", ativo=" + vazio.isAtivo());

        // Validação: deve cair no catch.
        try {
            new Cliente(9, "Maria", "   ", "123", "m@email.com", "x");
        } catch (IllegalArgumentException erro) {
            System.out.println("Validação OK: " + erro.getMessage());
        }

        // atualizarCadastro e toString.
        ana.atualizarCadastro("Ana Souza Lima", "Rua das Flores, 20",
                "(38) 99999-0000", "ana@gmail.com", "a1b2c3");
        System.out.println("Atualizado: " + ana);

        // Dado inválido: nada muda, nem o nome.
        try {
            ana.atualizarCadastro("Nome Novo", "Rua X", "111", "   ", "a1");
        } catch (IllegalArgumentException erro) {
            System.out.println("Nada mudou: " + ana.getNome()
                    + " (" + erro.getMessage() + ")");
        }

        // equals e hashCode: o mesmo ID representa o mesmo cliente.
        Cliente mesmaAna = new Cliente(1, "Outro Nome", "Outra Rua",
                "000", "x@email.com", "zzz");
        System.out.println("Mesmo ID é igual: " + ana.equals(mesmaAna));
        System.out.println("Mesmo hash: "
                + (ana.hashCode() == mesmaAna.hashCode()));
        System.out.println("IDs diferentes: " + ana.equals(joao));

        // ------------------------------------------------- funcionários
        System.out.println();
        System.out.println("=== Funcionários ===");

        // Polimorfismo: a variável é Funcionario, o objeto é de cada subclasse.
        Funcionario bia = new Colaborador(1, "Bia Rocha", "bia@academia.com", "bia123");
        Funcionario marcos = new Colaborador(2, "Marcos Dias", "marcos@academia.com", "marcos123");
        Funcionario carlos = new Administrador(3, "Carlos Pena", "carlos@academia.com", "carlos123");
        System.out.println(bia + " financeiro: " + bia.podeAcessarFinanceiro());
        System.out.println(carlos + " financeiro: " + carlos.podeAcessarFinanceiro());

        // Construtores padrão das subclasses.
        System.out.println("Colaborador padrão: " + new Colaborador());
        System.out.println("Administrador padrão: " + new Administrador());

        // Senha: verificar e alterar.
        System.out.println("Senha certa: " + carlos.verificarSenha("carlos123"));
        System.out.println("Senha errada: " + carlos.verificarSenha("000"));
        System.out.println("Troca com senha errada: "
                + carlos.alterarSenha("000", "nova456"));
        System.out.println("Troca com senha certa: "
                + carlos.alterarSenha("carlos123", "nova456"));
        System.out.println("Nova senha vale: " + carlos.verificarSenha("nova456"));

        // editarDados e desativar.
        bia.editarDados("Beatriz Rocha", "beatriz@academia.com");
        System.out.println("Editado: " + bia);
        marcos.desativar();

        // ------------------------------------------------- JSON
        System.out.println();
        System.out.println("=== JSON ===");
        RepositorioJson repositorio = new RepositorioJson();
        try {
            repositorio.salvarDados(
                    List.of(ana, joao, carla, pedro, lucia, rafael, juliana, thiago),
                    List.of(bia, marcos, carlos));
            System.out.println("Gravado em: "
                    + repositorio.getArquivo().toAbsolutePath());

            for (Cliente c : repositorio.carregarClientes()) {
                System.out.println("Cliente " + c.getId() + " - " + c.getNome()
                        + " - ativo: " + c.isAtivo());
            }
            for (Funcionario f : repositorio.carregarFuncionarios()) {
                System.out.println(f + " financeiro: " + f.podeAcessarFinanceiro());
            }
        } catch (IOException erro) {
            System.out.println("Erro ao acessar o arquivo: "
                    + erro.getMessage());
        }

        // Cadastro incompleto não é gravado.
        try {
            repositorio.salvarDados(List.of(vazio), List.of());
        } catch (IllegalArgumentException | IOException erro) {
            System.out.println("Gravação recusada: " + erro.getMessage());
        }
    }
}